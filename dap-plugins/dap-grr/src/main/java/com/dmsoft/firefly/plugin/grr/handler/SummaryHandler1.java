package com.dmsoft.firefly.plugin.grr.handler;

import com.dmsoft.firefly.plugin.grr.controller.GrrMainController;
import com.dmsoft.firefly.plugin.grr.dto.GrrConfigDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrDataFrameDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrSummaryDto;
import com.dmsoft.firefly.plugin.grr.dto.SearchConditionDto;
import com.dmsoft.firefly.plugin.grr.dto.analysis.GrrAnalysisConfigDto;
import com.dmsoft.firefly.plugin.grr.service.GrrService;
import com.dmsoft.firefly.plugin.grr.utils.DigNumInstance;
import com.dmsoft.firefly.plugin.grr.utils.GrrExceptionCode;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.job.core.JobHandlerContext;
import com.dmsoft.firefly.sdk.job.core.JobInboundHandler;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

/**
 * Created by cherry on 2018/3/12.
 */
public class SummaryHandler1 implements JobInboundHandler {

    @Override
    public void doJob(JobHandlerContext context, Object... in) throws Exception {
        if (in == null || !(in[0] instanceof Map)) {
            throw new ApplicationException(GrrFxmlAndLanguageUtils.getString(GrrExceptionCode.ERR_12001));
        }
        Map<String, Object> param = (Map) in[0];

        SearchConditionDto searchConditionDto = (SearchConditionDto) param.get(ParamKeys.SEARCH_GRR_CONDITION_DTO);
        GrrAnalysisConfigDto grrAnalysisConfigDto = (GrrAnalysisConfigDto) param.get(ParamKeys.SEARCH_GRR_ANALYSIS_CONFIG);

        List<TestItemWithTypeDto> itemWithTypeDtos = searchConditionDto.getSelectedTestItemDtos();

        SearchDataFrame dataFrame = (SearchDataFrame) param.get(ParamKeys.SEARCH_DATA_FRAME);
        GrrDataFrameDto grrDataFrameDto = (GrrDataFrameDto) param.get(ParamKeys.SEARCH_VIEW_DATA_FRAME);
        int analysisSummaryType = param != null && param.containsKey(ParamKeys.SEARCH_GRR_SUMMARY_TYPE) ? (int) param.get(ParamKeys.SEARCH_GRR_SUMMARY_TYPE) : -1;

        List<String> includeRows = Lists.newLinkedList();
        grrDataFrameDto.getIncludeDatas().forEach(grrViewDataDto -> includeRows.add(grrViewDataDto.getRowKey()));

        GrrService grrService = RuntimeContext.getBean(GrrService.class);
        List<GrrSummaryDto> summaryDtos = grrService.getSummaryResult(dataFrame,
                itemWithTypeDtos,
                includeRows,
                grrAnalysisConfigDto);

        String selectedItemName = "";
        if (summaryDtos != null && analysisSummaryType != -1) {
            for (int i = 0; i < summaryDtos.size(); i++) {
                GrrSummaryDto summaryDto = summaryDtos.get(i);
                if (validGrr(summaryDto, analysisSummaryType)) {
                    selectedItemName = summaryDto.getItemName();
                    break;
                }
            }
        }
        //set selected test item
        param.put(ParamKeys.TEST_ITEM_NAME, selectedItemName);

        if (in[1] != null && in[1] instanceof GrrMainController) {
            GrrMainController grrMainController = (GrrMainController) in[1];
            grrMainController.setSummaryDtos(summaryDtos);
        }
        context.fireDoJob(param, in[1]);
        //context.returnValue(summaryDtos);
    }

    @Override
    public void exceptionCaught(JobHandlerContext context, Throwable cause) throws Exception {

    }

    private boolean validGrr(GrrSummaryDto summaryDto, int analysisType) {
        boolean valid = true;
        Double grr = analysisType == 1 ?
                summaryDto.getSummaryResultDto().getGrrOnTolerance() :
                summaryDto.getSummaryResultDto().getGrrOnContribution();
        if (grr == null || DAPStringUtils.isInfinityAndNaN(grr)) {
            valid = false;
        }
        return valid;
    }
}
