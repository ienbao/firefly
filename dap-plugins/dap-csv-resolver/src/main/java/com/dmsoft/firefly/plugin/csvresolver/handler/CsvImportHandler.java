/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.csvresolver.handler;

import com.dmsoft.bamboo.common.monitor.AbstractProcessMonitor;
import com.dmsoft.bamboo.common.monitor.ProcessMonitor;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.job.core.JobHandlerContext;
import com.dmsoft.firefly.sdk.job.core.JobInboundHandler;
import com.dmsoft.firefly.sdk.plugin.apis.IDataParser;

/**
 * Created by Garen.Pang on 2018/3/2.
 */
public class CsvImportHandler implements JobInboundHandler {

    @Override
    public void doJob(JobHandlerContext context, Object... in) throws Exception {

        if (in == null || !(in[0] instanceof String)) {
            //throw exception.
        }
        IDataParser parser = RuntimeContext.getBean(IDataParser.class);
        if (parser instanceof AbstractProcessMonitor) {
            ProcessMonitor monitor = (ProcessMonitor) parser;
            monitor.addProcessMonitorListener(context.getContextProcessMonitorListenerIfExists());
        }
        parser.importFile((String) in[0]);
    }

    @Override
    public void exceptionCaught(JobHandlerContext context, Throwable cause) throws Exception {

    }
}
