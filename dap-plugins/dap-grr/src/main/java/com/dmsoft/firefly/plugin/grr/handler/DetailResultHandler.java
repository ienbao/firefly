package com.dmsoft.firefly.plugin.grr.handler;

import com.dmsoft.firefly.plugin.grr.dto.GrrDataFrameDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrDetailDto;
import com.dmsoft.firefly.plugin.grr.dto.SearchConditionDto;
import com.dmsoft.firefly.plugin.grr.dto.analysis.GrrAnalysisConfigDto;
import com.dmsoft.firefly.plugin.grr.service.GrrService;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.job.core.AbstractBasicJobHandler;
import com.dmsoft.firefly.sdk.job.core.JobContext;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * handler for find detail result
 *
 * @author Can Guan, Cherry Peng
 */
public class DetailResultHandler extends AbstractBasicJobHandler {
    /**
     * constructor
     */
    public DetailResultHandler() {
        setName(ParamKeys.GRR_DETAIL_RESULT_HANDLER);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void doJob(JobContext context) {
        SearchConditionDto searchConditionDto = (SearchConditionDto) context.get(ParamKeys.SEARCH_GRR_CONDITION_DTO);
        GrrAnalysisConfigDto analysisConfigDto = (GrrAnalysisConfigDto) context.get(ParamKeys.SEARCH_GRR_ANALYSIS_CONFIG);
        List<TestItemWithTypeDto> itemWithTypeDtos = searchConditionDto.getSelectedTestItemDtos();
        GrrDataFrameDto grrDataFrameDto = (GrrDataFrameDto) context.get(ParamKeys.SEARCH_VIEW_DATA_FRAME);

        List<String> includeRows = Lists.newLinkedList();
        grrDataFrameDto.getIncludeDatas().forEach(grrViewDataDto -> includeRows.add(grrViewDataDto.getRowKey()));

        GrrService grrService = RuntimeContext.getBean(GrrService.class);
        String itemName = itemWithTypeDtos.get(0).getTestItemName();
        GrrDetailDto grrDetailDto = grrService.getDetailResult(grrDataFrameDto.getDataFrame().getDataColumn(itemName, null),
                itemWithTypeDtos.get(0),
                includeRows,
                analysisConfigDto);

        context.put(ParamKeys.GRR_DETAIL_DTO, grrDetailDto);
    }
}
