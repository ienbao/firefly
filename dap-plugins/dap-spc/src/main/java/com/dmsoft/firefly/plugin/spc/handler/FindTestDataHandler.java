package com.dmsoft.firefly.plugin.spc.handler;

import com.dmsoft.firefly.plugin.spc.controller.SpcMainController;
import com.dmsoft.firefly.plugin.spc.utils.SpcExceptionCode;
import com.dmsoft.firefly.plugin.spc.utils.SpcFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.job.core.JobHandlerContext;
import com.dmsoft.firefly.sdk.job.core.JobInboundHandler;

import java.util.List;
import java.util.Map;

/**
 * handler for finding source data
 *
 * @author Can Guan
 */
public class FindTestDataHandler implements JobInboundHandler {
    @Override
    @SuppressWarnings("unchecked")
    public void doJob(JobHandlerContext context, Object... in) throws Exception {
        if (in == null || !(in[0] instanceof Map) || !(in[1] instanceof SpcMainController)) {
            throw new ApplicationException(SpcFxmlAndLanguageUtils.getString(SpcExceptionCode.ERR_11001));
        }
        Map<String, Object> param = (Map) in[0];
        List<String> projectNameList = (List<String>) param.get(ParamKeys.PROJECT_NAME_LIST);
        List<String> testItemNames = (List<String>) param.get(ParamKeys.SPC_ANALYSIS_TEST_ITEM);

        List<RowDataDto> dataDtoList = RuntimeContext.getBean(SourceDataService.class).findTestData(projectNameList, testItemNames);
        param.put(ParamKeys.ROW_DATA_DTO_LIST, dataDtoList);
        context.fireDoJob(param, in[1]);
    }

    @Override
    public void exceptionCaught(JobHandlerContext context, Throwable cause) throws Exception {
        //TODO
    }
}
