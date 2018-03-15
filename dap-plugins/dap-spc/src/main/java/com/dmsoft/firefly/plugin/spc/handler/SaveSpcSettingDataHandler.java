/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.handler;

import com.dmsoft.firefly.plugin.spc.dto.SpcSettingDto;
import com.dmsoft.firefly.plugin.spc.service.SpcSettingService;
import com.dmsoft.firefly.plugin.spc.utils.SpcExceptionCode;
import com.dmsoft.firefly.plugin.spc.utils.SpcFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.job.core.JobHandlerContext;
import com.dmsoft.firefly.sdk.job.core.JobInboundHandler;

/**
 * Created by Ethan.Yang on 2018/3/14.
 */
public class SaveSpcSettingDataHandler implements JobInboundHandler {
    @Override
    public void doJob(JobHandlerContext context, Object... in) throws Exception {
        if (in == null || !(in[0] instanceof SpcSettingDto)) {
            throw new ApplicationException(SpcFxmlAndLanguageUtils.getString(SpcExceptionCode.ERR_11001));
        }
        SpcSettingDto spcSettingDto = (SpcSettingDto) in[0];
        SpcSettingService spcSettingService = RuntimeContext.getBean(SpcSettingService.class);
        spcSettingService.saveSpcSetting(spcSettingDto);
    }

    @Override
    public void exceptionCaught(JobHandlerContext context, Throwable cause) throws Exception {

    }
}