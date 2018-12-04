package com.dmsoft.firefly.plugin.grr.handler;

import com.dmsoft.firefly.plugin.grr.dto.GrrDataFrameDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrExportDetailDto;
import com.dmsoft.firefly.plugin.grr.dto.SearchConditionDto;
import com.dmsoft.firefly.plugin.grr.dto.analysis.GrrAnalysisConfigDto;
import com.dmsoft.firefly.plugin.grr.service.GrrService;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.job.core.AbstractBasicJobHandler;
import com.dmsoft.firefly.sdk.job.core.JobContext;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

/**
 * handler for detail export
 *
 * @author Can Guan, Cherry Peng
 */
public class FindExportDetailHandler extends AbstractBasicJobHandler {

    @Autowired
    private GrrService grrService;
    /**
     * constructor
     */
    public FindExportDetailHandler() {
        setName(ParamKeys.GRR_FIND_EXPORT_DETAIL_HANDLER);
    }

    @Override
    public void doJob(JobContext context) {

        SearchConditionDto searchConditionDto = context.getParam(ParamKeys.SEARCH_GRR_CONDITION_DTO, SearchConditionDto.class);
        GrrAnalysisConfigDto analysisConfigDto = context.getParam(ParamKeys.SEARCH_GRR_ANALYSIS_CONFIG, GrrAnalysisConfigDto.class);
        List<TestItemWithTypeDto> itemWithTypeDtos = searchConditionDto.getSelectedTestItemDtos();
        GrrDataFrameDto grrDataFrameDto = context.getParam(ParamKeys.SEARCH_VIEW_DATA_FRAME, GrrDataFrameDto.class);

        List<String> includeRows = Lists.newLinkedList();
        Set<String> appraisers = Sets.newHashSet();
        grrDataFrameDto.getIncludeDatas().forEach(grrViewDataDto -> {
            includeRows.add(grrViewDataDto.getRowKey());
            appraisers.add(grrViewDataDto.getOperator());
        });
        searchConditionDto.setAppraisers(Lists.newArrayList(appraisers));
//        GrrService grrService = RuntimeContext.getBean(GrrService.class);

        List<GrrExportDetailDto> grrDetailDtos = Lists.newArrayList();
        for (TestItemWithTypeDto item : itemWithTypeDtos) {
            GrrExportDetailDto grrDetailDto = grrService.getExportDetailResult(grrDataFrameDto.getDataFrame().getDataColumn(item.getTestItemName(), null),
                    item,
                    includeRows,
                    analysisConfigDto);
            grrDetailDtos.add(grrDetailDto);
        }
        context.put(ParamKeys.GRR_DETAIL_DTO_LIST, grrDetailDtos);
    }
}
