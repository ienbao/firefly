package com.dmsoft.firefly.plugin.grr.handler;

import com.dmsoft.firefly.plugin.grr.controller.GrrMainController;
import com.dmsoft.firefly.plugin.grr.dto.SearchConditionDto;
import com.dmsoft.firefly.plugin.grr.utils.GrrExceptionCode;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dataframe.DataFrameFactory;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.job.AbstractProcessMonitorAutoAdd;
import com.dmsoft.firefly.sdk.job.ProcessMonitorAuto;
import com.dmsoft.firefly.sdk.job.core.JobHandlerContext;
import com.dmsoft.firefly.sdk.job.core.JobInboundHandler;

import java.util.List;
import java.util.Map;

/**
 * data frame handler for change row data into data frame
 *
 * @author Can Guan
 */
public class DataFrameHandler implements JobInboundHandler {
    @Override
    public void doJob(JobHandlerContext context, Object... in) throws Exception {
        if (in == null || !(in[0] instanceof Map) || !(in[1] instanceof GrrMainController)) {
            throw new ApplicationException(GrrFxmlAndLanguageUtils.getString(GrrExceptionCode.ERR_12001));
        }
        Map<String, Object> param = (Map) in[0];
        List<RowDataDto> rowDataDtoList = (List<RowDataDto>) param.get(ParamKeys.ROW_DATA_DTO_LIST);
        SearchConditionDto searchConditionDto = (SearchConditionDto) param.get(ParamKeys.SEARCH_GRR_CONDITION_DTO);

        // progress
        DataFrameFactory dataFrameFactory = RuntimeContext.getBean(DataFrameFactory.class);
        if (dataFrameFactory instanceof AbstractProcessMonitorAutoAdd) {
            ProcessMonitorAuto monitor = (ProcessMonitorAuto) dataFrameFactory;
            monitor.addProcessMonitorListener(context.getContextProcessMonitorListenerIfExists());
        }
        SearchDataFrame dataFrame = dataFrameFactory.
                createSearchDataFrame((List<TestItemWithTypeDto>) param.get(ParamKeys.TEST_ITEM_WITH_TYPE_DTO_LIST), rowDataDtoList);
        param.put(ParamKeys.SEARCH_DATA_FRAME, dataFrame);

        List<String> searchConditions = searchConditionDto.getSearchCondition();
        if (searchConditions != null && !searchConditions.isEmpty()) {
            dataFrame.addSearchCondition(searchConditions);
            dataFrame.shrink();
        }

        context.fireDoJob(param, in[1]);
    }

    @Override
    public void exceptionCaught(JobHandlerContext context, Throwable cause) throws Exception {
        //TODO
    }
}
