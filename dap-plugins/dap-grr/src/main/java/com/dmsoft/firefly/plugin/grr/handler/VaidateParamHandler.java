package com.dmsoft.firefly.plugin.grr.handler;

import com.dmsoft.firefly.plugin.grr.controller.GrrMainController;
import com.dmsoft.firefly.plugin.grr.dto.GrrParamDto;
import com.dmsoft.firefly.plugin.grr.dto.SearchConditionDto;
import com.dmsoft.firefly.plugin.grr.service.GrrFilterService;
import com.dmsoft.firefly.plugin.grr.utils.GrrExceptionCode;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.job.AbstractProcessMonitorAutoAdd;
import com.dmsoft.firefly.sdk.job.ProcessMonitorAuto;
import com.dmsoft.firefly.sdk.job.core.JobHandlerContext;
import com.dmsoft.firefly.sdk.job.core.JobInboundHandler;

import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Julia on 2018/3/16.
 */
public class VaidateParamHandler implements JobInboundHandler {

    @Override
    public void doJob(JobHandlerContext context, Object... in) throws Exception {
        if (in == null || !(in[0] instanceof Map) || !(in[1] instanceof GrrMainController)) {
            throw new ApplicationException(GrrFxmlAndLanguageUtils.getString(GrrExceptionCode.ERR_12001));
        }
        Map<String, Object> param = (Map) in[0];
        SearchDataFrame dataFrame = (SearchDataFrame) param.get(ParamKeys.SEARCH_DATA_FRAME);
        SearchConditionDto searchConditionDto = (SearchConditionDto) param.get(ParamKeys.SEARCH_GRR_CONDITION_DTO);

        // progress
        GrrFilterService grrFilterService = RuntimeContext.getBean(GrrFilterService.class);
        if (grrFilterService instanceof AbstractProcessMonitorAutoAdd) {
            ProcessMonitorAuto monitor = (ProcessMonitorAuto) grrFilterService;
            monitor.addProcessMonitorListener(context.getContextProcessMonitorListenerIfExists());
        }

        GrrParamDto grrParamDto = grrFilterService.validateGrrParam(dataFrame, searchConditionDto);
        if (in[1] != null && in[1] instanceof GrrMainController) {
            GrrMainController grrMainController = (GrrMainController) in[1];
            grrMainController.setGrrParamDto(grrParamDto);
        }
        if (grrParamDto.getErrors() == null || grrParamDto.getErrors().isEmpty()) {
            searchConditionDto.setParts(new LinkedList<>(grrParamDto.getParts()));
            if (grrParamDto.getAppraisers() != null && !grrParamDto.getAppraisers().isEmpty()) {
                searchConditionDto.setAppraisers(new LinkedList<>(grrParamDto.getAppraisers()));
            }
            param.put(ParamKeys.SEARCH_GRR_CONDITION_DTO, searchConditionDto);
            context.fireDoJob(param, in[1]);
        } else {
            context.returnValue(grrParamDto);
        }
    }

    @Override
    public void exceptionCaught(JobHandlerContext context, Throwable cause) throws Exception {
        System.out.println("view validate exception");
    }
}
