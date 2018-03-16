package com.dmsoft.firefly.plugin.grr.handler;

import com.dmsoft.firefly.plugin.grr.controller.GrrMainController;
import com.dmsoft.firefly.plugin.grr.dto.*;
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
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

/**
 * Created by cherry on 2018/3/12.
 */
public class DetailResultHandler1 implements JobInboundHandler {

    @Override
    public void doJob(JobHandlerContext context, Object... in) throws Exception {
        if (in == null || !(in[0] instanceof Map) || !(in[1] instanceof GrrMainController)) {
            throw new ApplicationException(GrrFxmlAndLanguageUtils.getString(GrrExceptionCode.ERR_12001));
        }
        Map<String, Object> param = (Map) in[0];

//        detailParamMap.put(ParamKeys.SEARCH_DATA_COLUMN, grrDataFrameDto.getDataFrame().getDataColumn(itemName, null));
//        detailParamMap.put(ParamKeys.SEARCH_GRR_ANALYSIS_TESTITEM, conditionDto.getSelectedTestItemDtos().get(0));
//        detailParamMap.put(ParamKeys.ANALYSIS_GRR_INCLUDE_ROWS, includeRows);
//        detailParamMap.put(ParamKeys.SEARCH_GRR_ANALYSIS_CONFIG, analysisConfigDto);
//
        SearchConditionDto searchConditionDto = (SearchConditionDto) param.get(ParamKeys.SEARCH_GRR_CONDITION_DTO);
        GrrAnalysisConfigDto analysisConfigDto = (GrrAnalysisConfigDto) param.get(ParamKeys.SEARCH_GRR_ANALYSIS_CONFIG);
        List<TestItemWithTypeDto> itemWithTypeDtos = searchConditionDto.getSelectedTestItemDtos();
        GrrDataFrameDto grrDataFrameDto = (GrrDataFrameDto) param.get(ParamKeys.SEARCH_VIEW_DATA_FRAME);

        List<String> includeRows = Lists.newLinkedList();
        grrDataFrameDto.getIncludeDatas().forEach(grrViewDataDto -> includeRows.add(grrViewDataDto.getRowKey()));

        GrrService grrService = RuntimeContext.getBean(GrrService.class);
        String itemName = itemWithTypeDtos.get(0).getTestItemName();
        GrrDetailDto grrDetailDto = grrService.getDetailResult(grrDataFrameDto.getDataFrame().getDataColumn(itemName, null),
                itemWithTypeDtos.get(0),
                includeRows,
                analysisConfigDto);
        if (in[1] != null && in[1] instanceof GrrMainController) {
            GrrMainController grrMainController = (GrrMainController) in[1];
            grrMainController.setGrrDetailDto(grrDetailDto);
        }
        context.returnValue(grrDetailDto);
    }

    @Override
    public void exceptionCaught(JobHandlerContext context, Throwable cause) throws Exception {

    }
}
