package com.dmsoft.firefly.plugin.yield.service.impl;

import com.dmsoft.firefly.plugin.yield.dto.*;
import com.dmsoft.firefly.plugin.yield.service.YieldService;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.job.core.JobContext;
import com.dmsoft.firefly.sdk.job.core.JobEvent;
import com.dmsoft.firefly.sdk.job.core.JobManager;

import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        List<String> searchRowKeys = searchDataFrame.getSearchRowKey(searchConditions.get(1).getCondition());
        List<String> datas = searchDataFrame.getDataValue(searchConditions.get(1).getItemName(), searchRowKeys);
        Map<String, List<String>> dataAndRowKeyMap = new HashMap<>();
        List<String> unRepetitionDatas = Lists.newArrayList();
        if (searchConditions.get(1).getItemName().equals(configDto.getPrimaryKey())) {
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
                        unRepetitionDatasRowKeys.add(searchRowKeys.get(i));
                    }
                }
                dataAndRowKeyMap.put(unRepetitionDatas.get(i), unRepetitionDatasRowKeys);
            }
        }


        if (!dataAndRowKeyMap.isEmpty()) {
            int overTotalSamples = unRepetitionDatas.size();
            int overFpySamples = 0;
            int overPassSamples = 0;
            int overNtfSamples = 0;
            int overNgSamples = 0;

            //overView
            for (SearchConditionDto searchConditionDto : searchConditions) {
                for (int k = 0; k < unRepetitionDatas.size(); k++) {
                    List<String> rowKeys = dataAndRowKeyMap.get(unRepetitionDatas.get(k));
                    for (int j = 0; j < rowKeys.size(); j++) {
                        RowDataDto rowDataDto = searchDataFrame.getDataRow(rowKeys.get(j));
                        String key = null;
                        for (Map.Entry<String, String> entry : rowDataDto.getData().entrySet()) {
                            if (entry.getKey().equals(searchConditionDto.getItemName())) {
                                key = entry.getKey();
                            }
                        }
                        if (searchConditionDto.getTestItemType().getCode().equals("Variable") || searchConditionDto.getTestItemType() == null) {
                            double lsl = Double.parseDouble(searchConditionDto.getLslOrFail());
                            double usl = Double.parseDouble(searchConditionDto.getUslOrPass());
                            if (Double.parseDouble(rowDataDto.getData().get(key)) >= lsl && Double.parseDouble(rowDataDto.getData().get(key)) <= usl) {
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
                        } else if (searchConditionDto.getTestItemType().getCode().equals("Attribute")) {
                            if (rowDataDto.getData().get(key).equals("Pass")) {
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
                    }
                }
                overNgSamples = overTotalSamples - overPassSamples;

                YieldOverviewDto yieldOverviewDto = new YieldOverviewDto();
                yieldOverviewDto.setFpySamples(overFpySamples);
                yieldOverviewDto.setNtfSamples(overNtfSamples);
                yieldOverviewDto.setPassSamples(overPassSamples);
                yieldOverviewDto.setNgSamples(overNgSamples);
                yieldOverviewDto.setFpyPercent((double) (overFpySamples / overTotalSamples));
                yieldOverviewDto.setNtfPercent((double) (overNtfSamples / overTotalSamples));
                yieldOverviewDto.setNgPersent((double) (overNgSamples / overTotalSamples));
                yieldOverviewDto.setItemName(searchConditionDto.getItemName());
                yieldOverviewDto.setLslOrPass(searchConditionDto.getLslOrFail());
                yieldOverviewDto.setUslOrPass(searchConditionDto.getUslOrPass());
                yieldOverviewDto.setTotalSamples(overTotalSamples);
                overResult.add(yieldOverviewDto);
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
                    RowDataDto rowDataDto = searchDataFrame.getDataRow(rowKeys.get(j));
                    for (Map.Entry<String, String> entry : rowDataDto.getData().entrySet()) {
                        int count = 0;
                        for (int k = 0; k < searchConditions.size(); k++) {
                            if (entry.getKey().equals(searchConditions.get(k))) {
                                if (searchConditions.get(k).getTestItemType().getCode().equals("Variable") || searchConditions.get(k).getTestItemType() == null) {
                                    double lsl = Double.parseDouble(searchConditions.get(k).getLslOrFail());
                                    double usl = Double.parseDouble(searchConditions.get(k).getUslOrPass());
                                    if (Double.parseDouble(entry.getValue()) >= lsl && Double.parseDouble(entry.getValue()) <= usl) {
                                        count = count + 1;
                                    }
                                } else if (searchConditions.get(k).getTestItemType().getCode().equals("Attribute")) {
                                    if (entry.getValue().equals("Pass")) {
                                        count = count + 1;
                                    }
                                }
                            }
                        }
                        if (count == searchConditions.size() && j == 0) {
                            totalProFpySamples = totalProNgSamples + 1;
                            totalProPassSamples = totalProPassSamples + 1;
                            flag = true;
                            break;
                        } else if (count == searchConditions.size() && j > 0 && j <= rowKeys.size() - 1) {
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


            totalProNgSamples = totalProTotalSamples - totalProPassSamples;
            totalProNtfSamples = totalProPassSamples - totalProFpySamples;
            yieldTotalProcessesDto.setFpySamples(totalProFpySamples);
            yieldTotalProcessesDto.setNgSamples(totalProNgSamples);
            yieldTotalProcessesDto.setNtfSamples(totalProNtfSamples);
            yieldTotalProcessesDto.setPassSamples(totalProPassSamples);
            yieldTotalProcessesDto.setTotalSamples(totalProTotalSamples);
            yieldTotalProcessesDto.setFpyPercent((double) (totalProFpySamples / totalProTotalSamples));
            yieldTotalProcessesDto.setNgPercent((double) (totalProNgSamples / totalProTotalSamples));
            yieldTotalProcessesDto.setNtfPercent((double) (totalProNtfSamples / totalProTotalSamples));
        }
        for (int i = 0; i < configDto.getTopN(); i++) {
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
    public List<YieldViewDataDto> getViewData(SearchDataFrame searchDataFrame, SearchViewDataConditionDto searchViewDataConditionDto) {
        return null;
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
