package com.dmsoft.firefly.plugin.spc.handler;

import com.dmsoft.firefly.plugin.spc.dto.SearchConditionDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcAnalysisConfigDto;
import com.dmsoft.firefly.plugin.spc.service.impl.SpcService;
import com.dmsoft.firefly.plugin.spc.utils.SpcExceptionCode;
import com.dmsoft.firefly.plugin.spc.utils.SpcFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.job.core.JobHandlerContext;
import com.dmsoft.firefly.sdk.job.core.JobInboundHandler;

import java.util.List;
import java.util.Map;

/**
 * handler for get spc chart result handler
 *
 * @author Can Guan
 */
public class GetSpcChartResultHandler implements JobInboundHandler {
    @Override
    @SuppressWarnings("unchecked")
    public void doJob(JobHandlerContext context, Object... in) throws Exception {
        if (in == null || !(in[0] instanceof Map)) {
            throw new ApplicationException(SpcFxmlAndLanguageUtils.getString(SpcExceptionCode.ERR_11001));
        }
        Map<String, Object> param = (Map) in[0];
        SearchDataFrame dataFrame = (SearchDataFrame) param.get(ParamKeys.SEARCH_DATA_FRAME);
        List<SearchConditionDto> searchConditionDtoList = (List<SearchConditionDto>) param.get(ParamKeys.SEARCH_CONDITION_DTO_LIST);
        SpcAnalysisConfigDto analysisConfigDto = (SpcAnalysisConfigDto) param.get(ParamKeys.SPC_ANALYSIS_CONFIG_DTO);
        //TODO progress
        context.returnValue(RuntimeContext.getBean(SpcService.class).getChartResult(dataFrame, searchConditionDtoList, analysisConfigDto));
    }

    @Override
    public void exceptionCaught(JobHandlerContext context, Throwable cause) throws Exception {

    }
}
