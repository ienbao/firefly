package com.dmsoft.firefly.plugin.grr.handler;

import com.dmsoft.firefly.plugin.grr.dto.GrrConfigDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrExportConfigDto;
import com.dmsoft.firefly.plugin.grr.dto.SearchConditionDto;
import com.dmsoft.firefly.plugin.grr.dto.analysis.GrrAnalysisConfigDto;
import com.dmsoft.firefly.plugin.grr.service.GrrConfigService;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TemplateSettingDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.job.core.AbstractBasicJobHandler;
import com.dmsoft.firefly.sdk.job.core.JobContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * handler for find grr config
 *
 * @author Can Guan, Cherry Peng
 */
public class GrrConfigHandler extends AbstractBasicJobHandler {

    @Autowired
    private GrrConfigService grrConfigService;

    @Autowired
    private EnvService envService;
    /**
     * constructor
     */
    public GrrConfigHandler() {
        setName(ParamKeys.GRR_CONFIG_HANDLER);
    }

    @Override
    public void doJob(JobContext context) {
//        GrrConfigService grrConfigService = RuntimeContext.getBean(GrrConfigService.class);
        GrrConfigDto grrConfigDto = grrConfigService.findGrrConfig();
        context.put(ParamKeys.SEARCH_GRR_CONFIG_DTO, grrConfigDto);

//        EnvService envService = RuntimeContext.getBean(EnvService.class);
        TemplateSettingDto templateSettingDto = envService.findActivatedTemplate();
        context.put(ParamKeys.SEARCH_TEMPLATE_SETTING_DTO, templateSettingDto);
        if (context.containsKey(ParamKeys.GRR_EXPORT_CONFIG_DTO)) {
            GrrExportConfigDto exportConfigDto = (GrrExportConfigDto) context.get(ParamKeys.GRR_EXPORT_CONFIG_DTO);
            exportConfigDto.setGrrConfigDto(grrConfigDto);
        }

        SearchConditionDto searchConditionDto = context.getParam(ParamKeys.SEARCH_GRR_CONDITION_DTO, SearchConditionDto.class);
        GrrAnalysisConfigDto analysisConfigDto = new GrrAnalysisConfigDto();
        analysisConfigDto.setAppraiser(searchConditionDto.getAppraiserInt());
        analysisConfigDto.setTrial(searchConditionDto.getTrialInt());
        analysisConfigDto.setPart(searchConditionDto.getPartInt());
        analysisConfigDto.setCoverage(grrConfigDto.getCoverage());
        analysisConfigDto.setMethod(grrConfigDto.getAnalysisMethod());
        analysisConfigDto.setSignificance(Double.valueOf(grrConfigDto.getSignLevel()));
        context.put(ParamKeys.SEARCH_GRR_ANALYSIS_CONFIG, analysisConfigDto);
    }
}
