/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.handler;

import com.dmsoft.firefly.plugin.spc.dto.SpcSettingDto;
import com.dmsoft.firefly.plugin.spc.service.SpcSettingService;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.job.core.JobHandlerContext;
import com.dmsoft.firefly.sdk.job.core.JobInboundHandler;

/**
 * Created by Ethan.Yang on 2018/3/14.
 */
public class FindSpcSettingDataHandler implements JobInboundHandler {
    @Override
    public void doJob(JobHandlerContext context, Object... in) throws Exception {
        SpcSettingService spcSettingService = RuntimeContext.getBean(SpcSettingService.class);
        SpcSettingDto spcSettingDto = spcSettingService.findSpcSetting();
        context.returnValue(spcSettingDto);
    }

    @Override
    public void exceptionCaught(JobHandlerContext context, Throwable cause) throws Exception {

    }
}
