/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.core.job.handler;

import com.dmsoft.firefly.sdk.job.JobHandlerContext;
import com.dmsoft.firefly.sdk.job.JobInboundHandler;

/**
 * Created by Garen.Pang on 2018/2/2.
 */
public class JobInboundHandler2 implements JobInboundHandler {

    @Override
    public void doJob(JobHandlerContext context, Object in) throws Exception {
        StringBuffer param = new StringBuffer();
        param.append(in.toString() + " inbound handler2");
        context.fireDoJob(param.toString());
    }

    @Override
    public void exceptionCaught(JobHandlerContext context, Throwable cause) throws Exception {

    }
}
