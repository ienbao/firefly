/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.core.job.handler;

import com.dmsoft.firefly.sdk.job.JobEvent;
import com.dmsoft.firefly.sdk.job.core.JobHandlerContext;
import com.dmsoft.firefly.sdk.job.core.JobInboundHandler;

import java.util.UUID;

/**
 * Created by Garen.Pang on 2018/2/2.
 */
public class JobInboundHandler2 implements JobInboundHandler {

    @Override
    public void doJob(JobHandlerContext context, Object in) throws Exception {
        StringBuffer param = new StringBuffer();
        param.append(in.toString() + " \n " + Thread.currentThread().getName() + " inbound handler2");
        context.fireJobEvent(new JobEvent(UUID.randomUUID().toString(), "hello 2..."));
        context.fireDoJob(param.toString());
    }

    @Override
    public void exceptionCaught(JobHandlerContext context, Throwable cause) throws Exception {

    }
}
