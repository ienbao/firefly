package com.dmsoft.firefly.plugin.grr.handler;

import com.dmsoft.firefly.plugin.grr.controller.GrrMainController;
import com.dmsoft.firefly.plugin.grr.dto.GrrConfigDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrDataFrameDto;
import com.dmsoft.firefly.plugin.grr.dto.SearchConditionDto;
import com.dmsoft.firefly.plugin.grr.service.GrrFilterService;
import com.dmsoft.firefly.plugin.grr.utils.GrrExceptionCode;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TemplateSettingDto;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.job.AbstractProcessMonitorAutoAdd;
import com.dmsoft.firefly.sdk.job.ProcessMonitorAuto;
import com.dmsoft.firefly.sdk.job.core.JobHandlerContext;
import com.dmsoft.firefly.sdk.job.core.JobInboundHandler;

import java.util.Map;

/**
 * Created by cherry on 2018/3/12.
 */
public class ViewDataHandler implements JobInboundHandler {

    @Override
    public void doJob(JobHandlerContext context, Object... in) throws Exception {
        if (in == null || !(in[0] instanceof Map)) {
            throw new ApplicationException(GrrFxmlAndLanguageUtils.getString(GrrExceptionCode.ERR_12001));
        }
        Map<String, Object> param = (Map) in[0];
        SearchDataFrame dataFrame = (SearchDataFrame) param.get(ParamKeys.SEARCH_DATA_FRAME);
        GrrConfigDto grrConfigDto = (GrrConfigDto) param.get(ParamKeys.SEARCH_GRR_CONFIG_DTO);
        TemplateSettingDto templateSettingDto = (TemplateSettingDto) param.get(ParamKeys.SEARCH_TEMPLATE_SETTING_DTO);
        SearchConditionDto searchConditionDto = (SearchConditionDto) param.get(ParamKeys.SEARCH_GRR_CONDITION_DTO);

        // progress
        GrrFilterService grrFilterService = RuntimeContext.getBean(GrrFilterService.class);
        if (grrFilterService instanceof AbstractProcessMonitorAutoAdd) {
            ProcessMonitorAuto monitor = (ProcessMonitorAuto) grrFilterService;
            monitor.addProcessMonitorListener(context.getContextProcessMonitorListenerIfExists());
        }

        GrrDataFrameDto grrDataFrameDto = grrFilterService.getGrrViewData(dataFrame, grrConfigDto, templateSettingDto, searchConditionDto);
        if (in[1] != null && in[1] instanceof GrrMainController) {
            GrrMainController grrMainController = (GrrMainController) in[1];
            grrMainController.setGrrDataFrame(grrDataFrameDto);
            grrMainController.setBackGrrDataFrame(grrDataFrameDto);
        }
        param.put(ParamKeys.SEARCH_VIEW_DATA_FRAME, grrDataFrameDto);
        context.fireDoJob(param, in[1]);
    }

    @Override
    public void exceptionCaught(JobHandlerContext context, Throwable cause) throws Exception {
        System.out.println("view exception");
    }
}
