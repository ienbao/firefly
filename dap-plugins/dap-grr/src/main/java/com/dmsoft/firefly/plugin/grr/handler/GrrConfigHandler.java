package com.dmsoft.firefly.plugin.grr.handler;

import com.dmsoft.firefly.plugin.grr.controller.GrrMainController;
import com.dmsoft.firefly.plugin.grr.dto.GrrConfigDto;
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

        if (in == null || !(in[0] instanceof Map) || !(in[1] instanceof GrrMainController)) {
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
        context.fireDoJob(param, in[1]);
    }

    @Override
    public void exceptionCaught(JobHandlerContext context, Throwable cause) throws Exception {

    }
}
