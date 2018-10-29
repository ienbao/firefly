package com.dmsoft.firefly.plugin.yield.service.impl;

import com.dmsoft.firefly.plugin.yield.service.YieldService;
import com.dmsoft.firefly.plugin.yield.dto.*;
import com.dmsoft.firefly.plugin.yield.utils.YieldType;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.job.core.JobContext;
import com.dmsoft.firefly.sdk.job.core.JobEvent;
import com.dmsoft.firefly.sdk.job.core.JobManager;

import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class YieldServiceImpl implements YieldService {
    private static Logger logger = LoggerFactory.getLogger(YieldServiceImpl.class);


    @Override
    public YieldResultDto getYieldResult(SearchDataFrame searchDataFrame, List<SearchConditionDto> oldSearchConditions,
                                         YieldAnalysisConfigDto configDto) {
        logger.debug("Getting Yield totalProcesses result...");
        if (searchDataFrame == null || oldSearchConditions == null || configDto == null) {
            pushProgress(100);
            throw new ApplicationException();
        }
        //TODO td SpcFxmlAndLanguageUtils.getString(SpcExceptionCode.ERR_11002)
        YieldResultDto result = new YieldResultDto();
        List<YieldOverviewDto> overResult = Lists.newArrayList();
        List<YieldNTFChartDto> ntfChartResult = Lists.newArrayList();
        YieldTotalProcessesDto yieldTotalProcessesDto = new YieldTotalProcessesDto();


        //分类产品

        List<String> oldSearchRowKeys = searchDataFrame.getSearchRowKey(oldSearchConditions.get(1).getCondition());
        List<String> oldDatas = searchDataFrame.getDataValue(configDto.getPrimaryKey(), oldSearchRowKeys);
        List<String> noNullProductSearchRowKeys = Lists.newArrayList();
        List<String> noNullProductDatas = Lists.newArrayList();

        //过滤产品为空
        for (int i = 0; i < oldDatas.size(); i++) {
            if (!DAPStringUtils.isBlank(oldDatas.get(i))) {
                noNullProductDatas.add(oldDatas.get(i));
                noNullProductSearchRowKeys.add(oldSearchRowKeys.get(i));
            }
        }

        //过滤上下限为空
        List<SearchConditionDto> searchConditions = Lists.newArrayList();
        List<Integer> noRangeSearchConditionIndex = Lists.newArrayList();
        List<Integer> rangeSearchConditionIndex = Lists.newArrayList();
        searchConditions.add(oldSearchConditions.get(0));
        for (int i = 1; i < oldSearchConditions.size(); i++) {
            if (!(DAPStringUtils.isBlank(oldSearchConditions.get(i).getLslOrFail()) && DAPStringUtils.isBlank(oldSearchConditions.get(i).getUslOrPass()))) {
                searchConditions.add(oldSearchConditions.get(i));
                rangeSearchConditionIndex.add(i);
            } else {
                noRangeSearchConditionIndex.add(i);
            }
        }

        //过滤测试项为空
        List<String> searchRowKeys = Lists.newArrayList();
        List<String> datas = Lists.newArrayList();
        List<IgnoreTestItemValue> ignoreTestItemValueList = Lists.newArrayList();
        for (int i = 0; i < noNullProductSearchRowKeys.size(); i++) {
            RowDataDto rowDataDto = searchDataFrame.getDataRow(noNullProductSearchRowKeys.get(i));
            Map<String, String> map = rowDataDto.getData();
            int count = 0;
            for (int j = 1; j < searchConditions.size(); j++) {
                String testItemValue = map.get(searchConditions.get(j).getItemName());
                if (!DAPStringUtils.isBlank(testItemValue)) {
                    if (searchConditions.get(j).getTestItemType().getCode().equals("Attribute")) {
                        if (!DAPStringUtils.isBlank(searchConditions.get(j).getUslOrPass()) || !DAPStringUtils.isBlank(searchConditions.get(j).getLslOrFail())) {
                            if (!DAPStringUtils.isBlank(searchConditions.get(j).getUslOrPass()) && !DAPStringUtils.isBlank(searchConditions.get(j).getLslOrFail())) {
                                if (testItemValue.equals(searchConditions.get(j).getUslOrPass()) || testItemValue.equals(searchConditions.get(j).getLslOrFail())) {
                                    count++;
                                } else {
                                    IgnoreTestItemValue ignoreTestItemValue = new IgnoreTestItemValue();
                                    ignoreTestItemValue.setRowKey(noNullProductSearchRowKeys.get(i));
                                    ignoreTestItemValue.setSearchConditionDto(searchConditions.get(j));
                                    ignoreTestItemValueList.add(ignoreTestItemValue);
                                }
                            } else {
                                count++;
                            }
                        } else {
                            IgnoreTestItemValue ignoreTestItemValue = new IgnoreTestItemValue();
                            ignoreTestItemValue.setRowKey(noNullProductSearchRowKeys.get(i));
                            ignoreTestItemValue.setSearchConditionDto(searchConditions.get(j));
                            ignoreTestItemValueList.add(ignoreTestItemValue);
                        }
                    } else if (searchConditions.get(j).getTestItemType().getCode().equals("Variable")) {
                        if (DAPStringUtils.isNumeric(testItemValue)) {
                            count++;
                        } else {
                            IgnoreTestItemValue ignoreTestItemValue = new IgnoreTestItemValue();
                            ignoreTestItemValue.setRowKey(noNullProductSearchRowKeys.get(i));
                            ignoreTestItemValue.setSearchConditionDto(searchConditions.get(j));
                            ignoreTestItemValueList.add(ignoreTestItemValue);
                        }
                    }
                } else {
                    IgnoreTestItemValue ignoreTestItemValue = new IgnoreTestItemValue();
                    ignoreTestItemValue.setRowKey(noNullProductSearchRowKeys.get(i));
                    ignoreTestItemValue.setSearchConditionDto(searchConditions.get(j));
                    ignoreTestItemValueList.add(ignoreTestItemValue);
                }
            }
            if (count <= searchConditions.size() - 1 && count > 0) {
                datas.add(noNullProductDatas.get(i));
                searchRowKeys.add(noNullProductSearchRowKeys.get(i));
            }
        }


        Map<String, List<String>> dataAndRowKeyMap = new HashMap<>();
        List<String> unRepetitionDatas = Lists.newArrayList();
        if (searchConditions.get(0).getItemName().equals(configDto.getPrimaryKey())) {
            for (int i = 0; i < datas.size(); i++) {
                boolean flag = false;
                for (String newData : unRepetitionDatas) {
                    if (datas.get(i).equals(newData)) {
                        flag = true;
                    }
                }
                if (!flag) {
                    unRepetitionDatas.add(datas.get(i));
                }
            }
            for (int i = 0; i < unRepetitionDatas.size(); i++) {
                List<String> unRepetitionDatasRowKeys = Lists.newArrayList();
                for (int j = 0; j < datas.size(); j++) {
                    if (unRepetitionDatas.get(i).equals(datas.get(j))) {
                        unRepetitionDatasRowKeys.add(searchRowKeys.get(j));
                    }
                }
                dataAndRowKeyMap.put(unRepetitionDatas.get(i), unRepetitionDatasRowKeys);
            }
        }


        if (!dataAndRowKeyMap.isEmpty()) {

            int overTotalSamples = 0;
            int overFpySamples = 0;
            int overPassSamples = 0;
            int overNtfSamples = 0;
            int overNgSamples = 0;

            //overView
            for (int i = 1; i < searchConditions.size(); i++) {
                for (int k = 0; k < unRepetitionDatas.size(); k++) {
                    List<String> oldRowKeys = dataAndRowKeyMap.get(unRepetitionDatas.get(k));
                    List<String> rowKeys = Lists.newArrayList();
                    for (int n = 0; n < oldRowKeys.size(); n++) {
                        int ignoreCount = 0;
                        for (int m = 0; m < ignoreTestItemValueList.size(); m++) {
                            if (!(ignoreTestItemValueList.get(m).getRowKey().equals(oldRowKeys.get(n)) && ignoreTestItemValueList.get(m).getSearchConditionDto().equals(searchConditions.get(i)))) {
                                ignoreCount++;
                            }
                        }
                        if (ignoreCount == ignoreTestItemValueList.size()) {
                            rowKeys.add(oldRowKeys.get(n));
                        }
                    }
                    if (rowKeys.size() == oldRowKeys.size()) {
                        rowKeys = oldRowKeys;
                    }
                    for (int j = 0; j < rowKeys.size(); j++) {
                        RowDataDto rowDataDto = searchDataFrame.getDataRow(rowKeys.get(j));
                        int ngFlag = 0;

                        String key = null;
                        for (Map.Entry<String, String> entry : rowDataDto.getData().entrySet()) {
                            if (entry.getKey().equals(searchConditions.get(i).getItemName())) {
                                key = entry.getKey();
                            }
                        }
                        String lslOrFail = searchConditions.get(i).getLslOrFail();
                        String uslOrPass = searchConditions.get(i).getUslOrPass();
                        if (searchConditions.get(i).getTestItemType().getCode().equals("Variable")) {
                            if (validateValue(rowDataDto.getData().get(key), uslOrPass, lslOrFail, searchConditions.get(i).getTestItemType().getCode())) {
                                ngFlag = ngFlag + 1;
                                if (j == 0) {
                                    overFpySamples = overFpySamples + 1;
                                    overPassSamples = overPassSamples + 1;
                                    break;
                                } else if (j > 0 && j <= rowKeys.size() - 1) {
                                    overNtfSamples = overNtfSamples + j;
                                    overPassSamples = overPassSamples + 1;
                                    break;
                                }
                            }
                        } else if (searchConditions.get(i).getTestItemType().getCode().equals("Attribute")) {
                            if (validateValue(rowDataDto.getData().get(key), uslOrPass, lslOrFail, searchConditions.get(i).getTestItemType().getCode())) {
                                ngFlag = ngFlag + 1;
                                if (j == 0) {
                                    overFpySamples = overFpySamples + 1;
                                    overPassSamples = overPassSamples + 1;
                                    break;
                                } else if (j > 0 && j <= rowKeys.size() - 1) {
                                    overNtfSamples = overNtfSamples + j;
                                    overPassSamples = overPassSamples + 1;
                                    break;
                                }
                            }
                        }
                        if (j == rowKeys.size() - 1) {
                            if (ngFlag == 0) {
                                overNgSamples = overNgSamples + 1 + j;
                            }
                        }
                    }
                }
                overTotalSamples = overPassSamples + overNtfSamples + overNgSamples;

                YieldOverviewDto yieldOverviewDto = new YieldOverviewDto();
                yieldOverviewDto.setItemName(searchConditions.get(i).getItemName());
                yieldOverviewDto.setKey(searchConditions.get(i).getKey());
                yieldOverviewDto.setTestItemType(searchConditions.get(i).getTestItemType());
                if (overTotalSamples != 0) {
                    yieldOverviewDto.setFpySamples(overFpySamples);
                    yieldOverviewDto.setNtfSamples(overNtfSamples);
                    yieldOverviewDto.setPassSamples(overPassSamples);
                    yieldOverviewDto.setNgSamples(overNgSamples);
                    yieldOverviewDto.setFpyPercent((double) overFpySamples / (double) overTotalSamples);
                    yieldOverviewDto.setNtfPercent((double) overNtfSamples / (double) overTotalSamples);
                    yieldOverviewDto.setNgPersent((double) overNgSamples / (double) overTotalSamples);
                    yieldOverviewDto.setLslOrPass(searchConditions.get(i).getLslOrFail());
                    yieldOverviewDto.setUslOrPass(searchConditions.get(i).getUslOrPass());
                    yieldOverviewDto.setTotalSamples(overTotalSamples);
                }
                overResult.add(yieldOverviewDto);
                overTotalSamples = 0;
                overFpySamples = 0;
                overNtfSamples = 0;
                overPassSamples = 0;
                overNgSamples = 0;
            }

            //total
            int totalProTotalSamples = 0;
            int totalProFpySamples = 0;
            int totalProPassSamples = 0;
            int totalProNtfSamples = 0;
            int totalProNgSamples = 0;


            for (int i = 0; i < unRepetitionDatas.size(); i++) {
                List<String> rowKeys = dataAndRowKeyMap.get(unRepetitionDatas.get(i));
                for (int j = 0; j < rowKeys.size(); j++) {
                    int count = 0;
                    int ignoreCount = 0;
                    RowDataDto rowDataDto = searchDataFrame.getDataRow(rowKeys.get(j));
                    Map<String, String> rowData = rowDataDto.getData();
                    for (int k = 1; k < searchConditions.size(); k++) {
                        IgnoreTestItemValue ignoreTestItemValue = null;
                        for (int m = 0; m < ignoreTestItemValueList.size(); m++) {
                            if (ignoreTestItemValueList.get(m).getRowKey().equals(rowKeys.get(j)) && ignoreTestItemValueList.get(m).getSearchConditionDto().equals(searchConditions.get(k))) {
                                ignoreTestItemValue = ignoreTestItemValueList.get(m);
                            }
                        }
                        if (ignoreTestItemValue == null) {
                            String lslOrFail = searchConditions.get(k).getLslOrFail();
                            String uslOrPass = searchConditions.get(k).getUslOrPass();
                            if (searchConditions.get(k).getTestItemType().getCode().equals("Variable")) {
                                if (validateValue(rowData.get(searchConditions.get(k).getItemName()), uslOrPass, lslOrFail, searchConditions.get(k).getTestItemType().getCode())) {
                                    count = count + 1;
                                }
                            } else if (searchConditions.get(k).getTestItemType().getCode().equals("Attribute")) {
                                if (validateValue(rowData.get(searchConditions.get(k).getItemName()), uslOrPass, lslOrFail, searchConditions.get(k).getTestItemType().getCode())) {
                                    count = count + 1;
                                }
                            }
                        } else {
                            ignoreCount++;
                        }
                    }
                    if (count == searchConditions.size() - 1 - ignoreCount && j == 0) {
                        totalProFpySamples = totalProFpySamples + 1;
                        totalProPassSamples = totalProPassSamples + 1;
                        break;
                    } else if (count == searchConditions.size() - 1 - ignoreCount && j > 0 && j <= rowKeys.size() - 1) {
                        totalProPassSamples = totalProPassSamples + 1;
                        break;
                    }
                }
            }


            totalProTotalSamples = unRepetitionDatas.size();
            totalProNgSamples = totalProTotalSamples - totalProPassSamples;
            totalProNtfSamples = totalProPassSamples - totalProFpySamples;
            yieldTotalProcessesDto.setFpySamples(totalProFpySamples);
            yieldTotalProcessesDto.setNgSamples(totalProNgSamples);
            yieldTotalProcessesDto.setNtfSamples(totalProNtfSamples);
            yieldTotalProcessesDto.setPassSamples(totalProPassSamples);
            yieldTotalProcessesDto.setTotalSamples(totalProTotalSamples);
            yieldTotalProcessesDto.setFpyPercent((double) totalProFpySamples / (double) totalProTotalSamples);
            yieldTotalProcessesDto.setNgPercent((double) totalProNgSamples / (double) totalProTotalSamples);
            yieldTotalProcessesDto.setNtfPercent((double) totalProNtfSamples / (double) totalProTotalSamples);
        } else {
            YieldOverviewDto yieldOverviewDto = new YieldOverviewDto();
            for (int i = 1; i < oldSearchConditions.size(); i++) {
                yieldOverviewDto.setKey(oldSearchConditions.get(i).getKey());
                yieldOverviewDto.setItemName(oldSearchConditions.get(i).getItemName());
                overResult.add(yieldOverviewDto);
            }
        }

        //ntfChart
        configDto.setTopN(5);//
        List<YieldNTFChartDto> ntfChartDtoList = Lists.newArrayList();
        for (int i = 0; i < searchConditions.size() - 1; i++) {
            YieldNTFChartDto yieldNTFChartDto = new YieldNTFChartDto();
            yieldNTFChartDto.setItemName(overResult.get(i).getItemName());
            if (overResult.get(i).getNtfPercent() == null) {
                yieldNTFChartDto.setNtfPercent(0.0);
            } else {
                yieldNTFChartDto.setNtfPercent(overResult.get(i).getNtfPercent());
            }
            ntfChartDtoList.add(yieldNTFChartDto);
        }
        Collections.sort(ntfChartDtoList, new Comparator<YieldNTFChartDto>() {
            @Override
            public int compare(YieldNTFChartDto o1, YieldNTFChartDto o2) {
                double ntf1 = o1.getNtfPercent();
                double ntf2 = o2.getNtfPercent();
                if (ntf1 > ntf2) {
                    return -1;
                } else if (ntf1 == ntf2) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
        for (int i = 0; i < configDto.getTopN() && i < searchConditions.size() - 1; i++) {
            ntfChartResult.add(ntfChartDtoList.get(i));
        }

        List<YieldOverviewDto> overViewResult = Lists.newArrayList();
        //组装overViewResult
        int overIndex = 0;
        for (int i = 1; i < oldSearchConditions.size(); i++) {
            for (Integer noRangeIndex : noRangeSearchConditionIndex) {
                if (noRangeIndex == i) {
                    YieldOverviewDto yieldOverviewDto = new YieldOverviewDto();
                    yieldOverviewDto.setKey(oldSearchConditions.get(i).getKey());
                    yieldOverviewDto.setItemName(oldSearchConditions.get(i).getItemName());
                    overViewResult.add(yieldOverviewDto);
                }
            }
            for (Integer rangeIndex : rangeSearchConditionIndex) {
                if (rangeIndex == i) {
                    overViewResult.add(overResult.get(overIndex));
                    overIndex++;
                }
            }
        }


        result.setTotalProcessesDtos(yieldTotalProcessesDto);
        result.setYieldNTFChartDtos(ntfChartResult);
        result.setYieldOverviewDtos(overViewResult);
        return result;
    }

    @Override
    public YieldViewDataResultDto getViewData(SearchDataFrame searchDataFrame, List<SearchConditionDto> oldSearchConditions, YieldAnalysisConfigDto configDto) {
        logger.debug("Getting Yield ViewData result...");
        if (searchDataFrame == null || oldSearchConditions == null || configDto == null) {
            pushProgress(100);
            throw new ApplicationException();
        }

        YieldViewDataResultDto yieldViewDataResultDto = new YieldViewDataResultDto();

        //分类产品

        List<String> oldSearchRowKeys = searchDataFrame.getSearchRowKey(oldSearchConditions.get(1).getCondition());
        List<String> oldDatas = searchDataFrame.getDataValue(oldSearchConditions.get(0).getItemName(), oldSearchRowKeys);
        List<String> noNullProductSearchRowKeys = Lists.newArrayList();
        List<String> noNullProductDatas = Lists.newArrayList();

        //过滤产品为空
        for (int i = 0; i < oldDatas.size(); i++) {
            if (!DAPStringUtils.isBlank(oldDatas.get(i))) {
                noNullProductDatas.add(oldDatas.get(i));
                noNullProductSearchRowKeys.add(oldSearchRowKeys.get(i));
            }
        }

        //过滤上下限为空
        List<SearchConditionDto> searchConditions = Lists.newArrayList();
        List<Integer> noRangeSearchConditionIndex = Lists.newArrayList();
        List<Integer> rangeSearchConditionIndex = Lists.newArrayList();
        searchConditions.add(oldSearchConditions.get(0));
        for (int i = 1; i < oldSearchConditions.size(); i++) {
            if (!(DAPStringUtils.isBlank(oldSearchConditions.get(i).getLslOrFail()) && DAPStringUtils.isBlank(oldSearchConditions.get(i).getUslOrPass()))) {
                searchConditions.add(oldSearchConditions.get(i));
                rangeSearchConditionIndex.add(i);
            } else {
                noRangeSearchConditionIndex.add(i);
            }
        }

        //过滤测试项为空
        List<String> searchRowKeys = Lists.newArrayList();
        List<String> datas = Lists.newArrayList();
        List<IgnoreTestItemValue> ignoreTestItemValueList = Lists.newArrayList();
        for (int i = 0; i < noNullProductSearchRowKeys.size(); i++) {
            RowDataDto rowDataDto = searchDataFrame.getDataRow(noNullProductSearchRowKeys.get(i));
            Map<String, String> map = rowDataDto.getData();
            int count = 0;
            for (int j = 1; j < searchConditions.size(); j++) {
                String testItemValue = map.get(searchConditions.get(j).getItemName());
                if (!DAPStringUtils.isBlank(testItemValue)) {
                    if (searchConditions.get(j).getTestItemType().getCode().equals("Attribute")) {
                        if (!DAPStringUtils.isBlank(searchConditions.get(j).getUslOrPass()) || !DAPStringUtils.isBlank(searchConditions.get(j).getLslOrFail())) {
                            if (!DAPStringUtils.isBlank(searchConditions.get(j).getUslOrPass()) && !DAPStringUtils.isBlank(searchConditions.get(j).getLslOrFail())) {
                                if (testItemValue.equals(searchConditions.get(j).getUslOrPass()) || testItemValue.equals(searchConditions.get(j).getLslOrFail())) {
                                    count++;
                                } else {
                                    IgnoreTestItemValue ignoreTestItemValue = new IgnoreTestItemValue();
                                    ignoreTestItemValue.setRowKey(noNullProductSearchRowKeys.get(i));
                                    ignoreTestItemValue.setSearchConditionDto(searchConditions.get(j));
                                    ignoreTestItemValueList.add(ignoreTestItemValue);
                                }
                            } else {
                                count++;
                            }
                        } else {
                            IgnoreTestItemValue ignoreTestItemValue = new IgnoreTestItemValue();
                            ignoreTestItemValue.setRowKey(noNullProductSearchRowKeys.get(i));
                            ignoreTestItemValue.setSearchConditionDto(searchConditions.get(j));
                            ignoreTestItemValueList.add(ignoreTestItemValue);
                        }
                    } else if (searchConditions.get(j).getTestItemType().getCode().equals("Variable")) {
                        if (DAPStringUtils.isNumeric(testItemValue)) {
                            count++;
                        } else {
                            IgnoreTestItemValue ignoreTestItemValue = new IgnoreTestItemValue();
                            ignoreTestItemValue.setRowKey(noNullProductSearchRowKeys.get(i));
                            ignoreTestItemValue.setSearchConditionDto(searchConditions.get(j));
                            ignoreTestItemValueList.add(ignoreTestItemValue);
                        }
                    }
                }else {
                    IgnoreTestItemValue ignoreTestItemValue = new IgnoreTestItemValue();
                    ignoreTestItemValue.setRowKey(noNullProductSearchRowKeys.get(i));
                    ignoreTestItemValue.setSearchConditionDto(searchConditions.get(j));
                    ignoreTestItemValueList.add(ignoreTestItemValue);
                }
            }
            if (count <= searchConditions.size() - 1 && count > 0) {
                datas.add(noNullProductDatas.get(i));
                searchRowKeys.add(noNullProductSearchRowKeys.get(i));
            }
        }

        Map<String, List<String>> dataAndRowKeyMap = new HashMap<>();
        List<String> unRepetitionDatas = Lists.newArrayList();
        if (searchConditions.get(0).getItemName().equals(configDto.getPrimaryKey())) {
            for (int i = 0; i < datas.size(); i++) {
                boolean flag = false;
                for (String newData : unRepetitionDatas) {
                    if (datas.get(i).equals(newData)) {
                        flag = true;
                    }
                }
                if (!flag) {
                    unRepetitionDatas.add(datas.get(i));
                }
            }
            for (int i = 0; i < unRepetitionDatas.size(); i++) {
                List<String> unRepetitionDatasRowKeys = Lists.newArrayList();
                for (int j = 0; j < datas.size(); j++) {
                    if (unRepetitionDatas.get(i).equals(datas.get(j))) {
                        unRepetitionDatasRowKeys.add(searchRowKeys.get(j));
                    }
                }
                dataAndRowKeyMap.put(unRepetitionDatas.get(i), unRepetitionDatasRowKeys);
            }
        }

        if (!dataAndRowKeyMap.isEmpty()) {

            List<String> Resultlist = Lists.newArrayList();


            //ViewData
            for (int i = 1; i < searchConditions.size(); i++) {
                for (int k = 0; k < unRepetitionDatas.size(); k++) {
                    List<String> oldRowKeys = dataAndRowKeyMap.get(unRepetitionDatas.get(k));
                    List<String> rowKeys = Lists.newArrayList();
                    for (int n = 0; n < oldRowKeys.size(); n++) {
                        int ignoreCount = 0;
                        for (int m = 0; m < ignoreTestItemValueList.size(); m++) {
                            if (!(ignoreTestItemValueList.get(m).getRowKey().equals(oldRowKeys.get(n)) && ignoreTestItemValueList.get(m).getSearchConditionDto().equals(searchConditions.get(i)))) {
                                ignoreCount++;
                            }
                        }
                        if (ignoreCount == ignoreTestItemValueList.size()) {
                            rowKeys.add(oldRowKeys.get(n));
                        }
                    }
                    boolean ng = false;
                    if (rowKeys.size() == oldRowKeys.size()) {
                        rowKeys = oldRowKeys;
                    }
                    int j;
                    for (j = 0; j < rowKeys.size(); j++) {
                        RowDataDto rowDataDto = searchDataFrame.getDataRow(rowKeys.get(j));
                        int ngFlag = 0;

                        String key = null;
                        for (Map.Entry<String, String> entry : rowDataDto.getData().entrySet()) {
                            if (entry.getKey().equals(searchConditions.get(i).getItemName())) {
                                key = entry.getKey();
                            }
                        }
                        String lslOrFail = searchConditions.get(i).getLslOrFail();
                        String uslOrPass = searchConditions.get(i).getUslOrPass();

                        if (searchConditions.get(i).getTestItemType().getCode().equals("Variable")) {
                            if (validateValue(rowDataDto.getData().get(key), uslOrPass, lslOrFail, searchConditions.get(i).getTestItemType().getCode())) {
                                ngFlag = ngFlag + 1;
                                if (j == 0) {
                                    if(oldSearchConditions.get(0).getYieldType() == YieldType.FPY || oldSearchConditions.get(0).getYieldType() == YieldType.PASS){
                                        Resultlist.add(rowKeys.get(j));
                                    }
                                    break;

                                } else if (j > 0 && j <= rowKeys.size() - 1) {
                                    if(oldSearchConditions.get(0).getYieldType() == YieldType.NTF){
                                        for (int n = 0; n < j; n++) {
                                            Resultlist.add(rowKeys.get(n));
                                        }
                                    }else if(oldSearchConditions.get(0).getYieldType() == YieldType.PASS){
                                        Resultlist.add(rowKeys.get(j));
                                    }
                                    break;
                                }
                            }
                        } else if (searchConditions.get(i).getTestItemType().getCode().equals("Attribute")) {
                            if (validateValue(rowDataDto.getData().get(key), uslOrPass, lslOrFail, searchConditions.get(i).getTestItemType().getCode())) {
                                ngFlag = ngFlag + 1;
                                if (j == 0) {
                                    if(oldSearchConditions.get(0).getYieldType() == YieldType.FPY || oldSearchConditions.get(0).getYieldType() == YieldType.PASS){
                                        Resultlist.add(rowKeys.get(j));
                                    }
                                    break;
                                } else if (j > 0 && j <= rowKeys.size() - 1) {
                                    if(oldSearchConditions.get(0).getYieldType() == YieldType.NTF){
                                        for (int n = 0; n < j; n++) {
                                            Resultlist.add(rowKeys.get(n));
                                        }
                                    }else if(oldSearchConditions.get(0).getYieldType() == YieldType.PASS){
                                        Resultlist.add(rowKeys.get(j));
                                    }
                                    break;
                                }
                            }
                        }
                    }

                    if(j == rowKeys.size()) {
                        if (oldSearchConditions.get(0).getYieldType() == YieldType.NG) {
                            for (int n = 0; n < rowKeys.size(); n++) {
                                Resultlist.add(rowKeys.get(n));
                            }
                        }
                    }

                }

                if(oldSearchConditions.get(0).getYieldType() == YieldType.TOTAL){
                    for (int n = 0; n < searchRowKeys.size(); n++) {
                        Resultlist.add(searchRowKeys.get(n));
                    }
                }
                yieldViewDataResultDto.setItemName(searchConditions.get(i).getItemName());
                yieldViewDataResultDto.setPrimary(configDto.getPrimaryKey());
                yieldViewDataResultDto.setResultlist(Resultlist);
            }
        }

        return yieldViewDataResultDto;
    }


    @Override
    public YieldViewDataResultDto getTotalData(SearchDataFrame searchDataFrame, List<SearchConditionDto> oldSearchConditions, YieldAnalysisConfigDto configDto) {

        logger.debug("Getting TotalData...");
        if (searchDataFrame == null || oldSearchConditions == null || configDto == null) {
            pushProgress(100);
            throw new ApplicationException();
        }

        YieldViewDataResultDto yieldViewDataResultDto = new YieldViewDataResultDto();


        //分类产品
        List<String> oldSearchRowKeys = searchDataFrame.getSearchRowKey(oldSearchConditions.get(1).getCondition());
        List<String> oldDatas = searchDataFrame.getDataValue(oldSearchConditions.get(0).getItemName(), oldSearchRowKeys);
        List<String> noNullProductSearchRowKeys = Lists.newArrayList();
        List<String> noNullProductDatas = Lists.newArrayList();

        //过滤产品为空
        for (int i = 0; i < oldDatas.size(); i++) {
            if (!DAPStringUtils.isBlank(oldDatas.get(i))) {
                noNullProductDatas.add(oldDatas.get(i));
                noNullProductSearchRowKeys.add(oldSearchRowKeys.get(i));
            }
        }

        //过滤上下限为空
        List<SearchConditionDto> searchConditions = Lists.newArrayList();
        List<Integer> noRangeSearchConditionIndex = Lists.newArrayList();
        List<Integer> rangeSearchConditionIndex = Lists.newArrayList();
        searchConditions.add(oldSearchConditions.get(0));
        for (int i = 1; i < oldSearchConditions.size(); i++) {
            if (!(DAPStringUtils.isBlank(oldSearchConditions.get(i).getLslOrFail()) && DAPStringUtils.isBlank(oldSearchConditions.get(i).getUslOrPass()))) {
                searchConditions.add(oldSearchConditions.get(i));
                rangeSearchConditionIndex.add(i);
            } else {
                noRangeSearchConditionIndex.add(i);
            }
        }

        //过滤测试项为空
        List<String> searchRowKeys = Lists.newArrayList();
        List<String> datas = Lists.newArrayList();
        List<IgnoreTestItemValue> ignoreTestItemValueList = Lists.newArrayList();
        for (int i = 0; i < noNullProductSearchRowKeys.size(); i++) {
            RowDataDto rowDataDto = searchDataFrame.getDataRow(noNullProductSearchRowKeys.get(i));
            Map<String, String> map = rowDataDto.getData();
            int count = 0;
            for (int j = 1; j < searchConditions.size(); j++) {
                String testItemValue = map.get(searchConditions.get(j).getItemName());
                if (!DAPStringUtils.isBlank(testItemValue)) {
                    if (searchConditions.get(j).getTestItemType().getCode().equals("Attribute")) {
                        if (testItemValue.equals(searchConditions.get(j).getUslOrPass()) || testItemValue.equals(searchConditions.get(j).getLslOrFail())) {
                            count++;
                        } else {
                            IgnoreTestItemValue ignoreTestItemValue = new IgnoreTestItemValue();
                            ignoreTestItemValue.setRowKey(noNullProductSearchRowKeys.get(i));
                            ignoreTestItemValue.setSearchConditionDto(searchConditions.get(j));
                            ignoreTestItemValueList.add(ignoreTestItemValue);
                        }
                    } else if (searchConditions.get(j).getTestItemType().getCode().equals("Variable")) {
                        if (DAPStringUtils.isNumeric(testItemValue)) {
                            count++;
                        } else {
                            IgnoreTestItemValue ignoreTestItemValue = new IgnoreTestItemValue();
                            ignoreTestItemValue.setRowKey(noNullProductSearchRowKeys.get(i));
                            ignoreTestItemValue.setSearchConditionDto(searchConditions.get(j));
                            ignoreTestItemValueList.add(ignoreTestItemValue);
                        }
                    }
                }else {
                    IgnoreTestItemValue ignoreTestItemValue = new IgnoreTestItemValue();
                    ignoreTestItemValue.setRowKey(noNullProductSearchRowKeys.get(i));
                    ignoreTestItemValue.setSearchConditionDto(searchConditions.get(j));
                    ignoreTestItemValueList.add(ignoreTestItemValue);
                }
            }
            if (count <= searchConditions.size() - 1 && count > 0) {
                datas.add(noNullProductDatas.get(i));
                searchRowKeys.add(noNullProductSearchRowKeys.get(i));
            }
        }

        Map<String, List<String>> dataAndRowKeyMap = new HashMap<>();
        List<String> unRepetitionDatas = Lists.newArrayList();
        if (searchConditions.get(0).getItemName().equals(configDto.getPrimaryKey())) {
            for (int i = 0; i < datas.size(); i++) {
                boolean flag = false;
                for (String newData : unRepetitionDatas) {
                    if (datas.get(i).equals(newData)) {
                        flag = true;
                    }
                }
                if (!flag) {
                    unRepetitionDatas.add(datas.get(i));
                }
            }
            for (int i = 0; i < unRepetitionDatas.size(); i++) {
                List<String> unRepetitionDatasRowKeys = Lists.newArrayList();
                for (int j = 0; j < datas.size(); j++) {
                    if (unRepetitionDatas.get(i).equals(datas.get(j))) {
                        unRepetitionDatasRowKeys.add(searchRowKeys.get(j));
                    }
                }
                dataAndRowKeyMap.put(unRepetitionDatas.get(i), unRepetitionDatasRowKeys);
            }
        }



        if (!dataAndRowKeyMap.isEmpty()) {

            List<String> totalFpylist = Lists.newArrayList();
            List<String> totalPasslist = Lists.newArrayList();
            List<String> totalNtflist = Lists.newArrayList();
            List<String> totalNglist = Lists.newArrayList();
            List<String> totalTotallist = Lists.newArrayList();

            //TotalData
            for (int i = 0; i < unRepetitionDatas.size(); i++) {
                List<String> rowKeys = dataAndRowKeyMap.get(unRepetitionDatas.get(i));
                int j;
                for (j = 0; j < rowKeys.size(); j++) {

                    int count = 0;
                    int ignoreCount = 0;
                    RowDataDto rowDataDto = searchDataFrame.getDataRow(rowKeys.get(j));
                    Map<String, String> rowData = rowDataDto.getData();
                    for (int k = 1; k < searchConditions.size(); k++) {
                        IgnoreTestItemValue ignoreTestItemValue = null;
                        for (int m = 0; m < ignoreTestItemValueList.size(); m++) {
                            if (ignoreTestItemValueList.get(m).getRowKey().equals(rowKeys.get(j)) && ignoreTestItemValueList.get(m).getSearchConditionDto().equals(searchConditions.get(k))) {
                                ignoreTestItemValue = ignoreTestItemValueList.get(m);
                            }
                        }
                        if (ignoreTestItemValue == null) {
                            String lslOrFail = searchConditions.get(k).getLslOrFail();
                            String uslOrPass = searchConditions.get(k).getUslOrPass();
                            if (searchConditions.get(k).getTestItemType().getCode().equals("Variable")) {
                                if (validateValue(rowData.get(searchConditions.get(k).getItemName()), uslOrPass, lslOrFail, searchConditions.get(k).getTestItemType().getCode())) {
                                    count = count + 1;
                                }
                            } else if (searchConditions.get(k).getTestItemType().getCode().equals("Attribute")) {
                                if (validateValue(rowData.get(searchConditions.get(k).getItemName()), uslOrPass, lslOrFail, searchConditions.get(k).getTestItemType().getCode())) {
                                    count = count + 1;
                                }
                            }
                        } else {
                            ignoreCount++;
                        }
                    }
                    if (count == searchConditions.size() - 1 - ignoreCount && j == 0) {
                        totalFpylist.add(rowKeys.get(j));
                        totalPasslist.add(rowKeys.get(j));
                        break;
                    } else if (count == searchConditions.size() - 1 - ignoreCount && j > 0 && j <= rowKeys.size() - 1) {
                        for (int n = 0; n < j; n++) {
                            totalNtflist.add(rowKeys.get(n));
                        }
                        totalPasslist.add(rowKeys.get(j));
                        break;
                    }
                }
                if (j == rowKeys.size()) {
                    for (int n = 0; n < rowKeys.size(); n++) {
                        totalNglist.add(rowKeys.get(n));
                    }
                }

            }


            for (int i = 0; i < searchRowKeys.size(); i++) {
                totalTotallist.add(searchRowKeys.get(i));
            }


            yieldViewDataResultDto.setPrimary(configDto.getPrimaryKey());

            yieldViewDataResultDto.setFPYlist(totalFpylist);
            yieldViewDataResultDto.setPASSlist(totalPasslist);
            yieldViewDataResultDto.setNtflist(totalNtflist);
            yieldViewDataResultDto.setNglist(totalNglist);
            yieldViewDataResultDto.setTotallist(totalTotallist);




        }

        return yieldViewDataResultDto;

    }


    private Boolean validateValue(String value, String uslOrPass, String lslOrFail, String type) {

        if (type.equals("Attribute")) {
            if (value.equals(uslOrPass)) {
                return true;
            }
            if (value.equals(lslOrFail)) {
                return false;
            }
            if (DAPStringUtils.isBlank(lslOrFail) && !value.equals(uslOrPass)) {
                return false;
            }
            if (DAPStringUtils.isBlank(uslOrPass) && !value.equals(lslOrFail)) {
                return true;
            } else {
                return false;
            }
        } else {
            Double upperLimited = null;
            Double lowerLimited = null;
            Double testItemValue = null;
            if (DAPStringUtils.isNumeric(uslOrPass)) {
                upperLimited = Double.parseDouble(uslOrPass);
            }
            if (DAPStringUtils.isNumeric(lslOrFail)) {
                lowerLimited = Double.parseDouble(lslOrFail);
            }
            if (DAPStringUtils.isNumeric(value)) {
                testItemValue = Double.parseDouble(value);
            }
            if (upperLimited != null && lowerLimited != null) {
                if (testItemValue <= upperLimited && testItemValue >= lowerLimited) {
                    return true;
                } else {
                    return false;
                }
            } else if (upperLimited != null) {
                if (testItemValue <= upperLimited) {
                    return true;
                } else {
                    return false;
                }
            } else if (lowerLimited != null) {
                if (testItemValue >= lowerLimited) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    private void pushProgress(int progress) {
        JobContext context = RuntimeContext.getBean(JobManager.class).findJobContext(Thread.currentThread());
        if (context != null) {
            context.pushEvent(new JobEvent("YieldService", progress + 0.0, null));
        }
    }
}
