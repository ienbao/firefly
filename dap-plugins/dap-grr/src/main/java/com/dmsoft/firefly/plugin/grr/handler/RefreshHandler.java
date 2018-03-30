package com.dmsoft.firefly.plugin.grr.handler;

import com.dmsoft.firefly.plugin.grr.dto.GrrDataFrameDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrDetailDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrSummaryDto;
import com.dmsoft.firefly.plugin.grr.dto.SearchConditionDto;
import com.dmsoft.firefly.plugin.grr.dto.analysis.GrrAnalysisConfigDto;
import com.dmsoft.firefly.plugin.grr.service.GrrService;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.job.core.AbstractBasicJobHandler;
import com.dmsoft.firefly.sdk.job.core.JobContext;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * handler for refreshing
 *
 * @author Can Guan, Cherry Peng
 */
public class RefreshHandler extends AbstractBasicJobHandler {
    /**
     * constructor
     */
    public RefreshHandler() {
        setName(ParamKeys.GRR_REFRESH_HANDLER);
    }

    @Override
    public void doJob(JobContext context) {
        String itemName = "";
        GrrAnalysisConfigDto analysisConfigDto = context.getParam(ParamKeys.SEARCH_GRR_ANALYSIS_CONFIG, GrrAnalysisConfigDto.class);
        GrrDataFrameDto grrDataFrameDto = context.getParam(ParamKeys.SEARCH_VIEW_DATA_FRAME, GrrDataFrameDto.class);
        SearchConditionDto searchConditionDto = context.getParam(ParamKeys.SEARCH_GRR_CONDITION_DTO, SearchConditionDto.class);
        List<TestItemWithTypeDto> itemWithTypeDtos = searchConditionDto.getSelectedTestItemDtos();
        List<String> includeRows = Lists.newLinkedList();
        grrDataFrameDto.getIncludeDatas().forEach(grrViewDataDto -> includeRows.add(grrViewDataDto.getRowKey()));
        if (context.containsKey(ParamKeys.TEST_ITEM_NAME)) {
            itemName = (String) context.get(ParamKeys.TEST_ITEM_NAME);
        }

        GrrService grrService = RuntimeContext.getBean(GrrService.class);
        List<GrrSummaryDto> summaryDtos = grrService.getSummaryResult(grrDataFrameDto.getDataFrame(),
                itemWithTypeDtos,
                includeRows,
                analysisConfigDto);
        context.put(ParamKeys.GRR_SUMMARY_DTO_LIST, summaryDtos);
        if (DAPStringUtils.isNotBlank(itemName)) {
            GrrDetailDto grrDetailDto = grrService.getDetailResult(grrDataFrameDto.getDataFrame().getDataColumn(itemName, null),
                    itemWithTypeDtos.get(0),
                    includeRows,
                    analysisConfigDto);
            context.put(ParamKeys.GRR_DETAIL_DTO, grrDetailDto);
        }
    }
}
