package com.dmsoft.firefly.plugin.grr.handler;

import com.dmsoft.firefly.plugin.grr.dto.GrrDataFrameDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrSummaryDto;
import com.dmsoft.firefly.plugin.grr.dto.SearchConditionDto;
import com.dmsoft.firefly.plugin.grr.dto.analysis.GrrAnalysisConfigDto;
import com.dmsoft.firefly.plugin.grr.service.GrrService;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.job.core.AbstractBasicJobHandler;
import com.dmsoft.firefly.sdk.job.core.JobContext;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * handler for grr summary
 *
 * @author Can Guan, Cherry Peng
 */
public class SummaryHandler extends AbstractBasicJobHandler {

    @Autowired
    private GrrService grrService;
    /**
     * constructor
     */
    public SummaryHandler() {
        setName(ParamKeys.GRR_SUMMARY_RESULT_HANDLER);
    }

    @Override
    public void doJob(JobContext context) {
        SearchConditionDto searchConditionDto = context.getParam(ParamKeys.SEARCH_GRR_CONDITION_DTO, SearchConditionDto.class);
        GrrAnalysisConfigDto grrAnalysisConfigDto = context.getParam(ParamKeys.SEARCH_GRR_ANALYSIS_CONFIG, GrrAnalysisConfigDto.class);

        List<TestItemWithTypeDto> itemWithTypeDtos = searchConditionDto.getSelectedTestItemDtos();

        SearchDataFrame dataFrame = context.getParam(ParamKeys.SEARCH_DATA_FRAME, SearchDataFrame.class);
        GrrDataFrameDto grrDataFrameDto = context.getParam(ParamKeys.SEARCH_VIEW_DATA_FRAME, GrrDataFrameDto.class);

        List<String> includeRows = Lists.newLinkedList();
        grrDataFrameDto.getIncludeDatas().forEach(grrViewDataDto -> includeRows.add(grrViewDataDto.getRowKey()));

//        GrrService grrService = RuntimeContext.getBean(GrrService.class);
        List<GrrSummaryDto> summaryDtos = grrService.getSummaryResult(dataFrame,
                itemWithTypeDtos,
                includeRows,
                grrAnalysisConfigDto);

        context.put(ParamKeys.GRR_SUMMARY_DTO_LIST, summaryDtos);
    }
}
