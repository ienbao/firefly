/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.gui.handler.importcsv;

import com.dmsoft.firefly.sdk.job.AbstractProcessMonitorAutoAdd;
import com.dmsoft.firefly.sdk.job.ProcessMonitorAuto;
import com.dmsoft.firefly.sdk.job.core.JobHandlerContext;
import com.dmsoft.firefly.sdk.job.core.JobInboundHandler;
import com.dmsoft.firefly.sdk.plugin.apis.IDataParser;

/**
 * Created by Garen.Pang on 2018/3/2.
 */
public class CsvImportHandler implements JobInboundHandler {

    @Override
    public void doJob(JobHandlerContext context, Object... in) throws Exception {

        if (in == null || in.length != 2 || !(in[0] instanceof String)) {
            //throw exception.
        }
        IDataParser parser = (IDataParser) in[1];
        if (parser instanceof AbstractProcessMonitorAutoAdd) {
            ProcessMonitorAuto monitor = (ProcessMonitorAuto) parser;
            monitor.addProcessMonitorListener(context.getContextProcessMonitorListenerIfExists());
        }
        parser.importFile((String) in[0]);
    }

    @Override
    public void exceptionCaught(JobHandlerContext context, Throwable cause) throws Exception {

    }
}
