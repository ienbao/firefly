package com.dmsoft.firefly.plugin.spc.handler;

import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.job.core.AbstractBasicJobHandler;
import com.dmsoft.firefly.sdk.job.core.JobContext;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * handler for finding test data
 *
 * @author Can Guan
 */
public class FindTestDataHandler extends AbstractBasicJobHandler {
    /**
     * constructor
     */
    public FindTestDataHandler() {
        setName(ParamKeys.FIND_TEST_DATA_HANDLER);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void doJob(JobContext context) {
        List<String> projectNameList = (List<String>) context.get(ParamKeys.PROJECT_NAME_LIST);
        List<TestItemWithTypeDto> testItemWithTypeDtoList = (List<TestItemWithTypeDto>) context.get(ParamKeys.TEST_ITEM_WITH_TYPE_DTO_LIST);
        List<String> testItemNames = Lists.newArrayList();
        for (TestItemWithTypeDto testItemWithTypeDto : testItemWithTypeDtoList) {
            testItemNames.add(testItemWithTypeDto.getTestItemName());
        }
        SourceDataService sourceDataService = RuntimeContext.getBean(SourceDataService.class);
        List<RowDataDto> dataDtoList = sourceDataService.findTestData(projectNameList, testItemNames);
        context.put(ParamKeys.ROW_DATA_DTO_LIST, dataDtoList);
    }
}
