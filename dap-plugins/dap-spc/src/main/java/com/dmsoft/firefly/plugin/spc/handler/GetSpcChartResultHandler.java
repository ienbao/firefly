package com.dmsoft.firefly.plugin.spc.handler;

import com.dmsoft.firefly.plugin.spc.dto.SearchConditionDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcAnalysisConfigDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcChartDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcSettingDto;
import com.dmsoft.firefly.plugin.spc.service.SpcService;
import com.dmsoft.firefly.plugin.spc.service.SpcSettingService;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.job.core.AbstractBasicJobHandler;
import com.dmsoft.firefly.sdk.job.core.JobContext;

import java.util.List;

/**
 * handler for get spc chart result
 *
 * @author Can Guan
 */
public class GetSpcChartResultHandler extends AbstractBasicJobHandler {
    /**
     * constructor
     */
    public GetSpcChartResultHandler() {
        setName(ParamKeys.SPC_CHART_RESULT_HANDLER);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void doJob(JobContext context) {
        SearchDataFrame dataFrame = context.getParam(ParamKeys.SEARCH_DATA_FRAME, SearchDataFrame.class);
        List<SearchConditionDto> searchConditionDtoList = (List<SearchConditionDto>) context.get(ParamKeys.SEARCH_CONDITION_DTO_LIST);
        SpcAnalysisConfigDto analysisConfigDto = context.getParam(ParamKeys.SPC_ANALYSIS_CONFIG_DTO, SpcAnalysisConfigDto.class);
        SpcService spcService = RuntimeContext.getBean(SpcService.class);
        SpcSettingDto spcSettingDto = (SpcSettingDto) context.get(ParamKeys.SPC_SETTING_DTO);
        List<SpcChartDto> spcChartDtoList = spcService.getChartResult(dataFrame, searchConditionDtoList, analysisConfigDto);
        RuntimeContext.getBean(SpcSettingService.class).setControlChartRuleAlarm(spcChartDtoList, spcSettingDto);
        context.put(ParamKeys.SPC_CHART_DTO_LIST, spcChartDtoList);
    }
}
