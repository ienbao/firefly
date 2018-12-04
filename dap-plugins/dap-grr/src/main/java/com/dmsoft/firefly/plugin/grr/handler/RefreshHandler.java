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
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * handler for refreshing
 *
 * @author Can Guan, Cherry Peng
 */
public class RefreshHandler extends AbstractBasicJobHandler {

    @Autowired
    private GrrService grrService;
    /**
     * constructor
     */
    public RefreshHandler() {
        setName(ParamKeys.GRR_REFRESH_HANDLER);
    }

    @Override
    public void doJob(JobContext context) {
        TestItemWithTypeDto testItemWithTypeDto = null;
        GrrAnalysisConfigDto analysisConfigDto = context.getParam(ParamKeys.SEARCH_GRR_ANALYSIS_CONFIG, GrrAnalysisConfigDto.class);
        GrrDataFrameDto grrDataFrameDto = context.getParam(ParamKeys.SEARCH_VIEW_DATA_FRAME, GrrDataFrameDto.class);
        SearchConditionDto searchConditionDto = context.getParam(ParamKeys.SEARCH_GRR_CONDITION_DTO, SearchConditionDto.class);
        List<TestItemWithTypeDto> itemWithTypeDtos = searchConditionDto.getSelectedTestItemDtos();
        List<String> includeRows = Lists.newLinkedList();
        grrDataFrameDto.getIncludeDatas().forEach(grrViewDataDto -> includeRows.add(grrViewDataDto.getRowKey()));
        if (context.containsKey(ParamKeys.TEST_ITEM_WITH_TYPE_DTO)) {
            testItemWithTypeDto = (TestItemWithTypeDto) context.get(ParamKeys.TEST_ITEM_WITH_TYPE_DTO);
        }

//        GrrService grrService = RuntimeContext.getBean(GrrService.class);
        List<GrrSummaryDto> summaryDtos = grrService.getSummaryResult(grrDataFrameDto.getDataFrame(),
                itemWithTypeDtos,
                includeRows,
                analysisConfigDto);
        context.put(ParamKeys.GRR_SUMMARY_DTO_LIST, summaryDtos);
        if (testItemWithTypeDto != null) {
            GrrDetailDto grrDetailDto = grrService.getDetailResult(grrDataFrameDto.getDataFrame().getDataColumn(testItemWithTypeDto.getTestItemName(), null),
                    testItemWithTypeDto,
                    includeRows,
                    analysisConfigDto);
            context.put(ParamKeys.GRR_DETAIL_DTO, grrDetailDto);
        }
    }
}
