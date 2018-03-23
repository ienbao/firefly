package com.dmsoft.firefly.plugin.spc.handler;

import com.dmsoft.firefly.plugin.spc.dto.*;
import com.dmsoft.firefly.plugin.spc.service.SpcService;
import com.dmsoft.firefly.plugin.spc.service.SpcSettingService;
import com.dmsoft.firefly.plugin.spc.utils.SpcExceptionCode;
import com.dmsoft.firefly.plugin.spc.utils.SpcFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.job.AbstractProcessMonitorAutoAdd;
import com.dmsoft.firefly.sdk.job.ProcessMonitorAuto;
import com.dmsoft.firefly.sdk.job.core.JobHandlerContext;
import com.dmsoft.firefly.sdk.job.core.JobInboundHandler;

import java.util.List;
import java.util.Map;

/**
 * handler for get spc stats result handler
 *
 * @author Can Guan
 */
public class GetSpcStatsResultHandler implements JobInboundHandler {
    @Override
    @SuppressWarnings("unchecked")
    public void doJob(JobHandlerContext context, Object... in) throws Exception {
        if (in == null || !(in[0] instanceof Map)) {
            throw new ApplicationException(SpcFxmlAndLanguageUtils.getString(SpcExceptionCode.ERR_11002));
        }
        Map<String, Object> param = (Map) in[0];
        SearchDataFrame dataFrame = (SearchDataFrame) param.get(ParamKeys.SEARCH_DATA_FRAME);
        List<SearchConditionDto> searchConditionDtoList = (List<SearchConditionDto>) param.get(ParamKeys.SEARCH_CONDITION_DTO_LIST);
        SpcAnalysisConfigDto analysisConfigDto = (SpcAnalysisConfigDto) param.get(ParamKeys.SPC_ANALYSIS_CONFIG_DTO);
        // progress
        SpcService spcService = RuntimeContext.getBean(SpcService.class);
        if (spcService instanceof AbstractProcessMonitorAutoAdd) {
            ProcessMonitorAuto monitor = (ProcessMonitorAuto) spcService;
            monitor.addProcessMonitorListener(context.getContextProcessMonitorListenerIfExists());
        }
        SpcSettingDto spcSettingDto = (SpcSettingDto) param.get(ParamKeys.SPC_SETTING_FILE_NAME);
        List<SpcStatsDto> spcStatsDtoList = spcService.getStatisticalResult(dataFrame, searchConditionDtoList, analysisConfigDto);
        List<SpcStatisticalResultAlarmDto> spcStatisticalResultAlarmDtoList =  RuntimeContext.getBean(SpcSettingService.class).setStatisticalResultAlarm(spcStatsDtoList, spcSettingDto);
        context.returnValue(spcStatisticalResultAlarmDtoList);
    }

    @Override
    public void exceptionCaught(JobHandlerContext context, Throwable cause) throws Exception {
        //TODO
    }
}
