package com.dmsoft.firefly.plugin.grr.handler;

import com.dmsoft.firefly.plugin.grr.controller.GrrMainController;
import com.dmsoft.firefly.plugin.grr.dto.GrrConfigDto;
import com.dmsoft.firefly.plugin.grr.dto.SearchConditionDto;
import com.dmsoft.firefly.plugin.grr.dto.analysis.GrrAnalysisConfigDto;
import com.dmsoft.firefly.plugin.grr.service.GrrConfigService;
import com.dmsoft.firefly.plugin.grr.utils.GrrExceptionCode;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TemplateSettingDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.job.AbstractProcessMonitorAutoAdd;
import com.dmsoft.firefly.sdk.job.ProcessMonitorAuto;
import com.dmsoft.firefly.sdk.job.core.JobHandlerContext;
import com.dmsoft.firefly.sdk.job.core.JobInboundHandler;

import java.util.Map;

/**
 * Created by cherry on 2018/3/12.
 */
public class GrrConfigHandler implements JobInboundHandler {
    @Override
    public void doJob(JobHandlerContext context, Object... in) throws Exception {

        if (in == null || !(in[0] instanceof Map)) {
            throw new ApplicationException(GrrFxmlAndLanguageUtils.getString(GrrExceptionCode.ERR_12001));
        }
        Map<String, Object> param = (Map) in[0];
        // progress
        GrrConfigService grrConfigService = RuntimeContext.getBean(GrrConfigService.class);
        if (grrConfigService instanceof AbstractProcessMonitorAutoAdd) {
            ProcessMonitorAuto monitor = (ProcessMonitorAuto) grrConfigService;
            monitor.addProcessMonitorListener(context.getContextProcessMonitorListenerIfExists());
        }
        GrrConfigDto grrConfigDto = grrConfigService.findGrrConfig();
        param.put(ParamKeys.SEARCH_GRR_CONFIG_DTO, grrConfigDto);

        EnvService envService = RuntimeContext.getBean(EnvService.class);
        TemplateSettingDto templateSettingDto = envService.findActivatedTemplate();
        param.put(ParamKeys.SEARCH_TEMPLATE_SETTING_DTO, templateSettingDto);

        SearchConditionDto searchConditionDto = (SearchConditionDto) param.get(ParamKeys.SEARCH_GRR_CONDITION_DTO);
        GrrAnalysisConfigDto analysisConfigDto = new GrrAnalysisConfigDto();
        analysisConfigDto.setAppraiser(searchConditionDto.getAppraiserInt());
        analysisConfigDto.setTrial(searchConditionDto.getTrialInt());
        analysisConfigDto.setPart(searchConditionDto.getPartInt());
        analysisConfigDto.setCoverage(grrConfigDto.getCoverage());
        analysisConfigDto.setMethod(grrConfigDto.getAnalysisMethod());
        analysisConfigDto.setSignificance(Double.valueOf(grrConfigDto.getSignLevel()));
        param.put(ParamKeys.SEARCH_GRR_ANALYSIS_CONFIG, analysisConfigDto);
        if (in[1] != null && in[1] instanceof GrrMainController) {
            GrrMainController grrMainController = (GrrMainController) in[1];
            grrMainController.setGrrConfigDto(grrConfigDto);
            grrMainController.setActiveTemplateSettingDto(templateSettingDto);
        }
        context.fireDoJob(param, in[1]);
    }

    @Override
    public void exceptionCaught(JobHandlerContext context, Throwable cause) throws Exception {

    }
}
