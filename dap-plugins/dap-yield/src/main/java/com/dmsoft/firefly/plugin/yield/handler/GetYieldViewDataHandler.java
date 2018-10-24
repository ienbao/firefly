package com.dmsoft.firefly.plugin.yield.handler;

import com.dmsoft.firefly.plugin.yield.dto.*;
import com.dmsoft.firefly.plugin.yield.service.YieldService;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.job.core.AbstractBasicJobHandler;
import com.dmsoft.firefly.sdk.job.core.JobContext;

import java.util.List;

public class GetYieldViewDataHandler extends AbstractBasicJobHandler {

    /**
     * constructor
     */
    public GetYieldViewDataHandler() {
        setName(ParamKeys.YIELD_VIEW_DATA_JOB_PIPELINE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void doJob(JobContext context) {
        SearchDataFrame dataFrame = context.getParam(ParamKeys.SEARCH_DATA_FRAME, SearchDataFrame.class);
        List<SearchConditionDto> searchConditionDtoList = (List<SearchConditionDto>) context.get(ParamKeys.SEARCH_CONDITION_DTO_LIST);
        YieldAnalysisConfigDto analysisConfigDto = (YieldAnalysisConfigDto) context.get(ParamKeys.YIELD_ANALYSIS_CONFIG_DTO);
        YieldService yieldService = RuntimeContext.getBean(YieldService.class);
        YieldSettingDto yieldSettingDto = (YieldSettingDto) context.get(ParamKeys.YIELD_SETTING_DTO);
        List<YieldViewDataResultDto> yieldViewDataResultDtoList = yieldService.getViewData(dataFrame, searchConditionDtoList, analysisConfigDto);
//        List<YieldOverviewDto> yieldOverviewDtoList = yieldResultDtoList.get(0).getYieldOverviewDtos();
//        List<YieldOverviewResultAlarmDto> yieldOverviecalResultAlarm(yieldOverviewDtoList, yieldSettingDto);
        context.put(ParamKeys.YIELD_VIEW_DATA_RESULT_DTO_LIST, yieldViewDataResultDtoList);
    }
}
