package com.dmsoft.firefly.plugin.grr.handler;

import com.dmsoft.firefly.plugin.grr.controller.GrrMainController;
import com.dmsoft.firefly.plugin.grr.dto.GrrSummaryDto;
import com.dmsoft.firefly.plugin.grr.dto.analysis.GrrAnalysisConfigDto;
import com.dmsoft.firefly.plugin.grr.service.GrrService;
import com.dmsoft.firefly.plugin.grr.utils.GrrExceptionCode;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.job.core.JobHandlerContext;
import com.dmsoft.firefly.sdk.job.core.JobInboundHandler;

import java.util.List;
import java.util.Map;

/**
 * Created by cherry on 2018/3/12.
 */
public class SummaryHandler implements JobInboundHandler {

    @Override
    public void doJob(JobHandlerContext context, Object... in) throws Exception {
        if (in == null || !(in[0] instanceof Map) || !(in[1] instanceof GrrMainController)) {
            throw new ApplicationException(GrrFxmlAndLanguageUtils.getString(GrrExceptionCode.ERR_12001));
        }
        Map<String, Object> param = (Map) in[0];
        GrrAnalysisConfigDto analysisConfigDto = (GrrAnalysisConfigDto) param.get(ParamKeys.SEARCH_GRR_ANALYSIS_CONFIG);
        List<TestItemWithTypeDto> itemWithTypeDtos = (List<TestItemWithTypeDto>) param.get(ParamKeys.SEARCH_GRR_ANALYSIS_TESTITEM);
        SearchDataFrame dataFrame = (SearchDataFrame) param.get(ParamKeys.SEARCH_DATA_FRAME);
        List<String> includeRows = (List<String>) param.get(ParamKeys.ANALYSIS_GRR_INCLUDE_ROWS);
        GrrService grrService = RuntimeContext.getBean(GrrService.class);
        List<GrrSummaryDto> summaryDtos = grrService.getSummaryResult(dataFrame,
                itemWithTypeDtos,
                includeRows,
                analysisConfigDto);
        context.returnValue(summaryDtos);
    }

    @Override
    public void exceptionCaught(JobHandlerContext context, Throwable cause) throws Exception {

    }
}
