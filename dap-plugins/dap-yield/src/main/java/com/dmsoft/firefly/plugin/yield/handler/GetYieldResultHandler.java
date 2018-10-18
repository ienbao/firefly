package com.dmsoft.firefly.plugin.yield.handler;

import com.dmsoft.firefly.plugin.yield.dto.SearchConditionDto;
import com.dmsoft.firefly.plugin.yield.dto.YieldAnalysisConfigDto;
import com.dmsoft.firefly.plugin.yield.service.YieldService;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.job.core.AbstractBasicJobHandler;
import com.dmsoft.firefly.sdk.job.core.JobContext;

import java.util.List;

public class GetYieldResultHandler extends AbstractBasicJobHandler {
    /**
     * constructor
     */
    public GetYieldResultHandler() {
        setName(ParamKeys.YIELD_STATS_RESULT_HANDLER);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void doJob(JobContext context) {
        SearchDataFrame dataFrame = context.getParam(ParamKeys.SEARCH_DATA_FRAME, SearchDataFrame.class);
        List<SearchConditionDto> searchConditionDtoList = (List<SearchConditionDto>) context.get(ParamKeys.SEARCH_CONDITION_DTO_LIST);
        YieldAnalysisConfigDto analysisConfigDto = (YieldAnalysisConfigDto) context.get(ParamKeys.YIELD_ANALYSIS_CONFIG_DTO);
        YieldService yieldService = RuntimeContext.getBean(YieldService.class);
//        YieldSettingDto yieldSettingDto = (YieldSettingDto) context.get(ParamKeys.YIELD_SETTING_DTO);
//        List<SpcStatsDto> spcStatsDtoList = spcService.getStatisticalResult(dataFrame, searchConditionDtoList, analysisConfigDto);
//        List<SpcStatisticalResultAlarmDto> spcStatisticalResultAlarmDtoList = RuntimeContext.getBean(SpcSettingService.class).setStatisticalResultAlarm(spcStatsDtoList, spcSettingDto);
//        context.put(ParamKeys.SPC_STATISTICAL_RESULT_ALARM_DTO_LIST, spcStatisticalResultAlarmDtoList);
    }
}
