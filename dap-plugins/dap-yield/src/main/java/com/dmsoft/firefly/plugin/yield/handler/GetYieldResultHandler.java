package com.dmsoft.firefly.plugin.yield.handler;

import com.dmsoft.firefly.plugin.yield.dto.*;
import com.dmsoft.firefly.plugin.yield.service.YieldService;
import com.dmsoft.firefly.plugin.yield.service.YieldSettingService;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.job.core.AbstractBasicJobHandler;
import com.dmsoft.firefly.sdk.job.core.JobContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class GetYieldResultHandler extends AbstractBasicJobHandler {

    @Autowired
    private YieldService yieldService;
    @Autowired
    private YieldSettingService yieldSettingService;
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
//        YieldService yieldService = RuntimeContext.getBean(YieldService.class);

        YieldSettingDto yieldSettingDto = (YieldSettingDto) context.get(ParamKeys.YIELD_SETTING_DTO);

        YieldResultDto yieldResultDto = yieldService.getYieldResult(dataFrame, searchConditionDtoList, analysisConfigDto);
        context.put(ParamKeys.YIELD_RESULT_DTO, yieldResultDto);

        List<YieldOverviewResultAlarmDto> yieldOverviewResultAlarmDtoList = this.yieldSettingService.setStatisticalResultAlarm(yieldResultDto.getYieldOverviewDtos(), yieldSettingDto);
        context.put(ParamKeys.YIELD_STATISTICAL_RESULT_ALARM_DTO_LIST, yieldOverviewResultAlarmDtoList);

        YieldChartResultAlermDto yieldDetailChartAlarmDtos = this.yieldSettingService.setChartResultAlarm(yieldResultDto.getTotalProcessesDtos(),yieldSettingDto);
        context.put(ParamKeys.YIELD_DETAILCHART_ALARM_DTO,yieldDetailChartAlarmDtos);
    }
}
