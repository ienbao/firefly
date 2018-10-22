package com.dmsoft.firefly.plugin.yield.service.impl;

import com.dmsoft.firefly.plugin.yield.service.YieldService;
import com.dmsoft.firefly.plugin.yield.dto.*;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class YieldServiceImpl implements YieldService {

    private static Logger logger = LoggerFactory.getLogger(YieldServiceImpl.class);


    private List<YieldOverviewDto> getOverviewResult(SearchDataFrame searchDataFrame, List<SearchConditionDto> searchConditions,
                                                     YieldAnalysisConfigDto configDto){
        return null;
    }

    @Override
    public List<YieldResultDto> getResult(SearchDataFrame searchDataFrame, List<SearchConditionDto> searchConditions, YieldAnalysisConfigDto configDto) {

        logger.debug("Getting Yield totalProcesses result...");
        if (searchDataFrame == null || searchConditions == null || configDto == null) {
            pushProgress(100);
            throw new ApplicationException();
        }
        //TODO td SpcFxmlAndLanguageUtils.getString(SpcExceptionCode.ERR_11002)
        List<YieldResultDto> result = Lists.newArrayList();
        List<YieldOverviewDto> overResult = Lists.newArrayList();
        List<YieldNTFChartDto> ntfChartResult = Lists.newArrayList();
        List<YieldTotalProcessesDto> yieldTotalProcessesDtos = Lists.newArrayList();
        YieldTotalProcessesDto yieldTotalProcessesDto = new YieldTotalProcessesDto();


        //分类产品
        List<String> searchRowKeys = searchDataFrame.getSearchRowKey(searchConditions.get(0).getCondition());
        List<String> datas = searchDataFrame.getDataValue(searchConditions.get(0).getItemName(), searchRowKeys);
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
            for (int i = 1;i<searchConditions.size();i++) {
                for (int k = 0; k < unRepetitionDatas.size(); k++) {
                    List<String> rowKeys = dataAndRowKeyMap.get(unRepetitionDatas.get(k));
                    for (int j = 0; j < rowKeys.size(); j++) {
                        RowDataDto rowDataDto = searchDataFrame.getDataRow(rowKeys.get(j));
                        String key = null;
                        int ngFlag = 0;
                        for (Map.Entry<String, String> entry : rowDataDto.getData().entrySet()) {
                            if (entry.getKey().equals(searchConditions.get(i).getItemName())) {
                                key = entry.getKey();
                            }
                        }
                        if (!searchConditions.get(i).getLslOrFail().equals("") &&  !searchConditions.get(i).getUslOrPass().equals("")) {
                            if (searchConditions.get(i).getTestItemType().getCode().equals("Variable")) {
                                String lslOrFail = searchConditions.get(i).getLslOrFail();
                                String uslOrPass = searchConditions.get(i).getUslOrPass();
                                double lsl = Double.NaN;
                                double usl = Double.NaN;
                                if (DAPStringUtils.isNumeric(lslOrFail)) {
                                    lsl = Double.parseDouble(searchConditions.get(i).getLslOrFail());
                                }
                                if (DAPStringUtils.isNumeric(uslOrPass)) {
                                    usl = Double.parseDouble(searchConditions.get(i).getUslOrPass());
                                }

                                if (Double.parseDouble(rowDataDto.getData().get(key)) >= lsl && Double.parseDouble(rowDataDto.getData().get(key)) <= usl && usl != Double.NaN && lsl != Double.NaN) {
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
                                if (rowDataDto.getData().get(key).equals(searchConditions.get(i).getUslOrPass())) {
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
                }
                overTotalSamples = overPassSamples+overNtfSamples+overNgSamples;

                YieldOverviewDto yieldOverviewDto = new YieldOverviewDto();
                yieldOverviewDto.setItemName(searchConditions.get(i).getItemName());
                yieldOverviewDto.setKey(searchConditions.get(i).getKey());
                if (!searchConditions.get(i).getLslOrFail().equals("") &&  !searchConditions.get(i).getUslOrPass().equals("")) {
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
                    boolean flag = false;
                    int count = 0;
                    RowDataDto rowDataDto = searchDataFrame.getDataRow(rowKeys.get(j));
                    for (Map.Entry<String, String> entry : rowDataDto.getData().entrySet()) {
                        for (int k = 1; k < searchConditions.size(); k++) {
                            if (entry.getKey().equals(searchConditions.get(k).getItemName())) {
                                if (!searchConditions.get(k).getLslOrFail().equals("") &&  !searchConditions.get(k).getUslOrPass().equals("")) {
                                    if (searchConditions.get(k).getTestItemType().getCode().equals("Variable") || searchConditions.get(k).getTestItemType() == null) {
                                        double lsl = Double.parseDouble(searchConditions.get(k).getLslOrFail());
                                        double usl = Double.parseDouble(searchConditions.get(k).getUslOrPass());
                                        if (Double.parseDouble(entry.getValue()) >= lsl && Double.parseDouble(entry.getValue()) <= usl) {
                                            count = count + 1;
                                        }
                                    } else if (searchConditions.get(k).getTestItemType().getCode().equals("Attribute")) {
                                        if (entry.getValue().equals(searchConditions.get(k).getUslOrPass())) {
                                            count = count + 1;
                                        }
                                    }
                                }
                            }
                        }
                        if (count == searchConditions.size()-1 && j == 0) {
                            totalProFpySamples = totalProFpySamples + 1;
                            totalProPassSamples = totalProPassSamples + 1;
                            flag = true;
                            break;
                        } else if (count == searchConditions.size()-1 && j > 0 && j <= rowKeys.size() - 1) {
                            totalProPassSamples = totalProPassSamples + 1;
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
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
            yieldTotalProcessesDto.setFpyPercent((double)totalProFpySamples / (double)totalProTotalSamples);
            yieldTotalProcessesDto.setNgPercent((double) totalProNgSamples / (double)totalProTotalSamples);
            yieldTotalProcessesDto.setNtfPercent((double) totalProNtfSamples / (double)totalProTotalSamples);
        }
        configDto.setTopN(5);//
        for (int i = 0; i < configDto.getTopN() && i < searchConditions.size() -1 ; i++) {
            YieldNTFChartDto yieldNTFChartDto = new YieldNTFChartDto();
            yieldNTFChartDto.setItemName(overResult.get(i).getItemName());
            yieldNTFChartDto.setNtfPercent(overResult.get(i).getNtfPercent());
            ntfChartResult.add(yieldNTFChartDto);
        }
        yieldTotalProcessesDtos.add(yieldTotalProcessesDto);
        YieldResultDto yieldResultDto = new YieldResultDto();
        yieldResultDto.setTotalProcessesDtos(yieldTotalProcessesDtos);
        yieldResultDto.setYieldNTFChartDtos(ntfChartResult);
        yieldResultDto.setYieldOverviewDtos(overResult);
        result.add(yieldResultDto);
        return result;
    }


    @Override
    public List<YieldViewDataResultDto> getViewData(SearchDataFrame searchDataFrame,  List<SearchConditionDto> searchConditions, YieldAnalysisConfigDto configDto) {

        logger.debug("Getting ViewData...");
        if (searchDataFrame == null || searchConditions == null || configDto == null) {
            pushProgress(100);
            throw new ApplicationException();
        }
        //TODO td SpcFxmlAndLanguageUtils.getString(SpcExceptionCode.ERR_11002)
        List<YieldResultDto> result = Lists.newArrayList();
        List<YieldOverviewDto> overResult = Lists.newArrayList();
        List<YieldNTFChartDto> ntfChartResult = Lists.newArrayList();
        List<YieldTotalProcessesDto> yieldTotalProcessesDtos = Lists.newArrayList();
        YieldTotalProcessesDto yieldTotalProcessesDto = new YieldTotalProcessesDto();


        List<YieldViewDataResultDto> viewDataResultDto = Lists.newArrayList();


        //分类产品
        List<String> searchRowKeys = searchDataFrame.getSearchRowKey(searchConditions.get(0).getCondition());
        List<String> datas = searchDataFrame.getDataValue(searchConditions.get(0).getItemName(), searchRowKeys);
        Map<String, List<String>> dataAndRowKeyMap = new HashMap<>();//过滤后的数据（产品号，行号）
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

            YieldViewDataDto yieldViewDataDto = new YieldViewDataDto();
            List<YieldViewDataDto> Fpylist = Lists.newArrayList();
            List<YieldViewDataDto> Passlist = Lists.newArrayList();
            List<YieldViewDataDto> Ntflist = Lists.newArrayList();
            List<YieldViewDataDto> Nglist = Lists.newArrayList();
            List<YieldViewDataDto> Totallist = Lists.newArrayList();

            Map<String, List<YieldViewDataDto>> overFpySamplesMap = new HashMap<>();
            Map<String, List<YieldViewDataDto>> overPassSamplesMap = new HashMap<>();
            Map<String, List<YieldViewDataDto>> overNtfSamplesMap = new HashMap<>();
            Map<String, List<YieldViewDataDto>> overNgSamplesMap = new HashMap<>();
            Map<String, List<YieldViewDataDto>> overTotalSamplesMap = new HashMap<>();

            //overView
            for (int i = 1;i<searchConditions.size();i++) {
                for (int k = 0; k < unRepetitionDatas.size(); k++) {
                    List<String> rowKeys = dataAndRowKeyMap.get(unRepetitionDatas.get(k));//一个产品的每一行
                    for (int j = 0; j < rowKeys.size(); j++) {
                        RowDataDto rowDataDto = searchDataFrame.getDataRow(rowKeys.get(j));//一个产品对应的所有测试项的数据
                        String key = null;
                        int ngFlag = 0;
                        for (Map.Entry<String, String> entry : rowDataDto.getData().entrySet()) {//键值对的集合，每一个比较
                            if (entry.getKey().equals(searchConditions.get(i).getItemName())) {
                                key = entry.getKey();
                            }
                        }
                        if (searchConditions.get(i).getTestItemType().getCode().equals("Variable")) {
                            String lslOrFail = searchConditions.get(i).getLslOrFail();
                            String uslOrPass = searchConditions.get(i).getUslOrPass();
                            double lsl = Double.NaN;
                            double usl = Double.NaN;
                            if (DAPStringUtils.isNumeric(lslOrFail)) {
                                lsl = Double.parseDouble(searchConditions.get(i).getLslOrFail());
                            }
                            if (DAPStringUtils.isNumeric(uslOrPass)){
                                usl = Double.parseDouble(searchConditions.get(i).getUslOrPass());
                            }

                            if (Double.parseDouble(rowDataDto.getData().get(key)) >= lsl && Double.parseDouble(rowDataDto.getData().get(key)) <= usl && usl != Double.NaN && lsl != Double.NaN) {
                                ngFlag = ngFlag + 1;
                                if (j == 0) {
                                    yieldViewDataDto.setProductName(unRepetitionDatas.get(k));
                                    yieldViewDataDto.setResult(Double.parseDouble(rowDataDto.getData().get(key)));
                                    Fpylist.add(yieldViewDataDto);
                                    Passlist.add(yieldViewDataDto);

                                    break;
                                } else if (j > 0 && j <= rowKeys.size() - 1) {
                                    yieldViewDataDto.setProductName(unRepetitionDatas.get(k));
                                    yieldViewDataDto.setResult(Double.parseDouble(rowDataDto.getData().get(key)));
                                    Ntflist.add(yieldViewDataDto);
                                    Passlist.add(yieldViewDataDto);

                                    break;
                                }
                            }

                        } else if (searchConditions.get(i).getTestItemType().getCode().equals("Attribute")) {
                            if (rowDataDto.getData().get(key).equals(searchConditions.get(i).getUslOrPass())) {
                                ngFlag = ngFlag +1;
                                if (j == 0) {
                                    yieldViewDataDto.setProductName(unRepetitionDatas.get(k));
                                    yieldViewDataDto.setResult(Double.parseDouble(rowDataDto.getData().get(key)));
                                    Fpylist.add(yieldViewDataDto);
                                    Passlist.add(yieldViewDataDto);
                                    break;
                                } else if (j > 0 && j <= rowKeys.size() - 1) {
                                    yieldViewDataDto.setProductName(unRepetitionDatas.get(k));
                                    yieldViewDataDto.setResult(Double.parseDouble(rowDataDto.getData().get(key)));
                                    Ntflist.add(yieldViewDataDto);
                                    Passlist.add(yieldViewDataDto);

                                    break;
                                }
                            }
                        }
                        if(j==rowKeys.size()-1){
                            if (ngFlag == 0){
                                overNgSamples = overNgSamples + 1 + j;
                            }
                        }
                    }
                }
                overTotalSamples = overPassSamples+overNtfSamples+overNgSamples;

//                Totallist.addAll(Fpylist);
//                Totallist.removeAll(Passlist);
//                Totallist.addAll(Passlist);
//                Totallist.removeAll(Ntflist);
//                Totallist.addAll(Ntflist);

                overFpySamplesMap.put(searchConditions.get(i).getItemName(),Fpylist);
                overPassSamplesMap.put(searchConditions.get(i).getItemName(),Passlist);
                overNtfSamplesMap.put(searchConditions.get(i).getItemName(),Ntflist);

//                overTotalSamplesMap.put(searchConditions.get(i).getItemName(),Totallist);

                YieldViewDataResultDto yieldViewDataResultDto = new YieldViewDataResultDto();
                yieldViewDataResultDto.setItemName(searchConditions.get(i).getItemName());
                yieldViewDataResultDto.setPrimary(configDto.getPrimaryKey());
                yieldViewDataResultDto.setFPYlist(Fpylist);
                yieldViewDataResultDto.setPASSlist(Passlist);
                yieldViewDataResultDto.setNtflist(Ntflist);
                yieldViewDataResultDto.setNglist(Nglist);
                yieldViewDataResultDto.setTotallist(Totallist);

                viewDataResultDto.add(yieldViewDataResultDto);

            }

        }

        return viewDataResultDto;
    }


    private void refreshChartResult(String columnKey, String rowKey){

    }

//    private List<YieldOverviewDto> getOverviewResult(SearchDataFrame searchDataFrame, List<SearchConditionDto> searchConditions,
//                                                     YieldAnalysisConfigDto configDto) {
//
//    }
//
//    private List<YieldTotalProcessesDto> getTotalProcessesResult(SearchDataFrame searchDataFrame, List<SearchConditionDto> searchConditions,
//                                                                 YieldAnalysisConfigDto configDto){
//        return null;
//    }
//
//    private List<YieldNTFChartDto> getNTFChartResult(SearchDataFrame searchDataFrame, List<SearchConditionDto> searchConditions,
//                                             YieldAnalysisConfigDto configDto){
//        return null;
//    }

    private void pushProgress(int progress) {
        JobContext context = RuntimeContext.getBean(JobManager.class).findJobContext(Thread.currentThread());
        if (context != null) {
            context.pushEvent(new JobEvent("YieldService", progress + 0.0, null));
        }
    }
}
