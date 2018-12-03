package com.dmsoft.firefly.gui.handler.importcsv;

import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemDataset;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.job.core.AbstractBasicJobHandler;
import com.dmsoft.firefly.sdk.job.core.JobContext;
import com.google.common.collect.Lists;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * handler for finding test data
 *
 * @author Can Guan
 */
@Component
public class FindTestDataHandler extends AbstractBasicJobHandler {

    @Autowired
    private SourceDataService sourceDataService;
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

        //获取相应列名字段
        List<String> testItemNames = Lists.newArrayList();
        for (TestItemWithTypeDto testItemWithTypeDto : testItemWithTypeDtoList) {
            testItemNames.add(testItemWithTypeDto.getTestItemName());
        }


        TestItemDataset testItemDataset = sourceDataService.findTestDataset(projectNameList, testItemNames, null);
        context.put(ParamKeys.ROW_DATA_DTO_LIST, testItemDataset);
    }
}
