package com.dmsoft.firefly.plugin.spc.handler;

import com.dmsoft.firefly.plugin.spc.utils.SpcExceptionCode;
import com.dmsoft.firefly.plugin.spc.utils.SpcFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.job.core.JobHandlerContext;
import com.dmsoft.firefly.sdk.job.core.JobInboundHandler;
import com.google.common.collect.Lists;

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
        if (in == null || !(in[0] instanceof Map)) {
            throw new ApplicationException(SpcFxmlAndLanguageUtils.getString(SpcExceptionCode.ERR_11001));
        }
        Map<String, Object> param = (Map) in[0];
        List<String> projectNameList = (List<String>) param.get(ParamKeys.PROJECT_NAME_LIST);
        List<TestItemWithTypeDto> testItemWithTypeDtoList = (List<TestItemWithTypeDto>) param.get(ParamKeys.TEST_ITEM_WITH_TYPE_DTO_LIST);
        List<String> testItemNames = Lists.newArrayList();
        for (TestItemWithTypeDto testItemWithTypeDto : testItemWithTypeDtoList) {
            testItemNames.add(testItemWithTypeDto.getTestItemName());
        }
        List<RowDataDto> dataDtoList = RuntimeContext.getBean(SourceDataService.class).findTestData(projectNameList, testItemNames);
        param.put(ParamKeys.ROW_DATA_DTO_LIST, dataDtoList);
    }

    @Override
    public void exceptionCaught(JobHandlerContext context, Throwable cause) throws Exception {
        //TODO
    }
}
