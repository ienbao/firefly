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
 * handler for get spc stats result
 *
 * @author Can Guan
 */
public class GetSpcStatsResultHandler extends AbstractBasicJobHandler {
    /**
     * constructor
     */
    public GetSpcStatsResultHandler() {
        setName(ParamKeys.SPC_STATS_RESULT_HANDLER);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void doJob(JobContext context) {
        SearchDataFrame dataFrame = context.getParam(ParamKeys.SEARCH_DATA_FRAME, SearchDataFrame.class);
        List<SearchConditionDto> searchConditionDtoList = (List<SearchConditionDto>) context.get(ParamKeys.SEARCH_CONDITION_DTO_LIST);
        SpcAnalysisConfigDto analysisConfigDto = (SpcAnalysisConfigDto) context.get(ParamKeys.SPC_ANALYSIS_CONFIG_DTO);
        SpcService spcService = RuntimeContext.getBean(SpcService.class);
        SpcSettingDto spcSettingDto = (SpcSettingDto) context.get(ParamKeys.SPC_SETTING_DTO);
        List<SpcStatsDto> spcStatsDtoList = spcService.getStatisticalResult(dataFrame, searchConditionDtoList, analysisConfigDto);
        List<SpcStatisticalResultAlarmDto> spcStatisticalResultAlarmDtoList = RuntimeContext.getBean(SpcSettingService.class).setStatisticalResultAlarm(spcStatsDtoList, spcSettingDto);
        context.put(ParamKeys.SPC_STATISTICAL_RESULT_ALARM_DTO_LIST, spcStatisticalResultAlarmDtoList);
    }
}
