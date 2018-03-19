package com.dmsoft.firefly.plugin.grr.handler;

import com.dmsoft.firefly.plugin.grr.dto.GrrDataFrameDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrDetailDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrSummaryDto;
import com.dmsoft.firefly.plugin.grr.dto.SearchConditionDto;
import com.dmsoft.firefly.plugin.grr.dto.analysis.GrrAnalysisConfigDto;
import com.dmsoft.firefly.plugin.grr.service.GrrService;
import com.dmsoft.firefly.plugin.grr.utils.UIConstant;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.job.core.JobHandlerContext;
import com.dmsoft.firefly.sdk.job.core.JobInboundHandler;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * Created by cherry on 2018/3/18.
 */
public class RefreshHandler implements JobInboundHandler {

    @Override
    public void doJob(JobHandlerContext context, Object... in) throws Exception {
        Map<String, Object> param = (Map) in[0];
        String itemName = "";
        GrrAnalysisConfigDto analysisConfigDto = (GrrAnalysisConfigDto) param.get(ParamKeys.SEARCH_GRR_ANALYSIS_CONFIG);
        GrrDataFrameDto grrDataFrameDto = (GrrDataFrameDto) param.get(ParamKeys.SEARCH_VIEW_DATA_FRAME);
        SearchConditionDto searchConditionDto = (SearchConditionDto) param.get(ParamKeys.SEARCH_GRR_CONDITION_DTO);
        List<TestItemWithTypeDto> itemWithTypeDtos = searchConditionDto.getSelectedTestItemDtos();
        List<String> includeRows = Lists.newLinkedList();
        grrDataFrameDto.getIncludeDatas().forEach(grrViewDataDto -> includeRows.add(grrViewDataDto.getRowKey()));
        if (param.containsKey(ParamKeys.TEST_ITEM_NAME)) {
            itemName = (String) param.get(ParamKeys.TEST_ITEM_NAME);
        }

        GrrService grrService = RuntimeContext.getBean(GrrService.class);
        Map<String, Object> result = Maps.newHashMap();
        List<GrrSummaryDto> summaryDtos = grrService.getSummaryResult(grrDataFrameDto.getDataFrame(),
                itemWithTypeDtos,
                includeRows,
                analysisConfigDto);
        result.put(UIConstant.ANALYSIS_RESULT_SUMMARY, summaryDtos);
        if (DAPStringUtils.isNotBlank(itemName)) {
            GrrDetailDto grrDetailDto = grrService.getDetailResult(grrDataFrameDto.getDataFrame().getDataColumn(itemName, null),
                    itemWithTypeDtos.get(0),
                    includeRows,
                    analysisConfigDto);
            result.put(UIConstant.ANALYSIS_RESULT_DETAIL, grrDetailDto);
        }

        context.returnValue(result);
    }

    @Override
    public void exceptionCaught(JobHandlerContext context, Throwable cause) throws Exception {

    }
}
