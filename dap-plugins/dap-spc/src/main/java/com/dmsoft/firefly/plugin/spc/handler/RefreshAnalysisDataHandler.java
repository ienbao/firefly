package com.dmsoft.firefly.plugin.spc.handler;

import com.dmsoft.firefly.plugin.spc.dto.*;
import com.dmsoft.firefly.plugin.spc.service.SpcService;
import com.dmsoft.firefly.plugin.spc.service.SpcSettingService;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.job.core.AbstractBasicJobHandler;
import com.dmsoft.firefly.sdk.job.core.JobContext;

import java.util.List;

/**
 * handler for new refresh analysis data handler
 *
 * @author Can Guan, Ethan Yang
 */
public class RefreshAnalysisDataHandler extends AbstractBasicJobHandler {
    /**
     * constructor
     */
    public RefreshAnalysisDataHandler() {
        setName(ParamKeys.SPC_REFRESH_RESULT_HANDLER);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void doJob(JobContext context) {
        SpcAnalysisConfigDto analysisConfigDto = context.getParam(ParamKeys.SPC_ANALYSIS_CONFIG_DTO, SpcAnalysisConfigDto.class);
        SearchDataFrame statisticalDataFrame = context.getParam(ParamKeys.STATISTICAL_SEARCH_DATA_FRAME, SearchDataFrame.class);
        List<SearchConditionDto> statisticalSearchConditionDtoList = (List<SearchConditionDto>) context.get(ParamKeys.STATISTICAL_SEARCH_CONDITION_DTO_LIST);

        SpcService spcService = RuntimeContext.getBean(SpcService.class);
        SpcSettingDto spcSettingDto = context.getParam(ParamKeys.SPC_SETTING_DTO, SpcSettingDto.class);
        List<SpcStatsDto> spcStatsDtoList = spcService.getStatisticalResult(statisticalDataFrame, statisticalSearchConditionDtoList, analysisConfigDto);
        List<SpcStatisticalResultAlarmDto> spcStatisticalResultAlarmDtoList = RuntimeContext.getBean(SpcSettingService.class).setStatisticalResultAlarm(spcStatsDtoList, spcSettingDto);

        SearchDataFrame chartDtaFrame = context.getParam(ParamKeys.CHART_SEARCH_DATA_FRAME, SearchDataFrame.class);
        List<SearchConditionDto> chartSearchConditionDtoList = (List<SearchConditionDto>) context.get(ParamKeys.CHART_SEARCH_CONDITION_DTO_LIST);
        List<SpcChartDto> spcChartDtoList = null;
        if(chartSearchConditionDtoList != null) {
            spcChartDtoList = spcService.getChartResult(chartDtaFrame, chartSearchConditionDtoList, analysisConfigDto);
            RuntimeContext.getBean(SpcSettingService.class).setControlChartRuleAlarm(spcChartDtoList, spcSettingDto);
        }

        context.put(ParamKeys.STATISTICAL_ANALYSIS_RESULT, spcStatisticalResultAlarmDtoList);
        context.put(ParamKeys.CHART_ANALYSIS_RESULT, spcChartDtoList);
    }
}
