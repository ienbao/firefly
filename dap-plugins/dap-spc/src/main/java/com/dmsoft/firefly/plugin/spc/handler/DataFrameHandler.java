package com.dmsoft.firefly.plugin.spc.handler;

import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dataframe.DataFrameFactory;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.job.core.AbstractBasicJobHandler;
import com.dmsoft.firefly.sdk.job.core.JobContext;

import java.util.List;

/**
 * data frame handler for change row data into data frame
 *
 * @author Can Guan
 */
public class DataFrameHandler extends AbstractBasicJobHandler {
    /**
     * constructor
     */
    public DataFrameHandler() {
        setName(ParamKeys.DATA_FRAME_HANDLER);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void doJob(JobContext context) {
        List<RowDataDto> rowDataDtoList = context.getParam(ParamKeys.ROW_DATA_DTO_LIST, List.class);
        context.remove(ParamKeys.ROW_DATA_DTO_LIST);
        DataFrameFactory dataFrameFactory = RuntimeContext.getBean(DataFrameFactory.class);
        SearchDataFrame dataFrame = dataFrameFactory.createSearchDataFrame((List<TestItemWithTypeDto>) context.get(ParamKeys.TEST_ITEM_WITH_TYPE_DTO_LIST), rowDataDtoList);
        context.put(ParamKeys.SEARCH_DATA_FRAME, dataFrame);
    }
}
