/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.handler;

import com.dmsoft.firefly.plugin.spc.dto.*;
import com.dmsoft.firefly.plugin.spc.service.SpcService;
import com.dmsoft.firefly.plugin.spc.service.SpcSettingService;
import com.dmsoft.firefly.plugin.spc.utils.SpcExceptionCode;
import com.dmsoft.firefly.plugin.spc.utils.SpcFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.job.core.JobHandlerContext;
import com.dmsoft.firefly.sdk.job.core.JobInboundHandler;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * Created by Ethan.Yang on 2018/3/16.
 */
public class RefreshAnalysisDataHandler implements JobInboundHandler {

    @Override
    public void doJob(JobHandlerContext context, Object... in) throws Exception {
        if (in == null || !(in[0] instanceof Map)) {
            throw new ApplicationException(SpcFxmlAndLanguageUtils.getString(SpcExceptionCode.ERR_11002));
        }
        Map<String, Object> param = (Map) in[0];
        SearchDataFrame dataFrame = (SearchDataFrame) param.get(ParamKeys.SEARCH_DATA_FRAME);
        SpcAnalysisConfigDto analysisConfigDto = (SpcAnalysisConfigDto) param.get(ParamKeys.SPC_ANALYSIS_CONFIG_DTO);

        List<SearchConditionDto> statisticalSearchConditionDtoList = (List<SearchConditionDto>) param.get(ParamKeys.STATISTICAL_SEARCH_CONDITION_DTO_LIST);
        SpcService spcService = RuntimeContext.getBean(SpcService.class);
        List<SpcStatsDto> spcStatsDtoList = spcService.getStatisticalResult(dataFrame, statisticalSearchConditionDtoList, analysisConfigDto);
        List<SpcStatisticalResultAlarmDto> spcStatisticalResultAlarmDtoList = RuntimeContext.getBean(SpcSettingService.class).setStatisticalResultAlarm(spcStatsDtoList);

        List<SearchConditionDto> chartSearchConditionDtoList = (List<SearchConditionDto>) param.get(ParamKeys.CHART_SEARCH_CONDITION_DTO_LIST);
        List<SpcChartDto> spcChartDtoList = RuntimeContext.getBean(SpcService.class).getChartResult(dataFrame, chartSearchConditionDtoList, analysisConfigDto);

        Map<String, Object> analysisResultMap = Maps.newHashMap();
        analysisResultMap.put(ParamKeys.STATISTICAL_ANALYSIS_RESULT, spcStatisticalResultAlarmDtoList);
        analysisResultMap.put(ParamKeys.CHART_ANALYSIS_RESULT, spcChartDtoList);

        context.returnValue(analysisResultMap);
    }

    @Override
    public void exceptionCaught(JobHandlerContext context, Throwable cause) throws Exception {

    }
}
