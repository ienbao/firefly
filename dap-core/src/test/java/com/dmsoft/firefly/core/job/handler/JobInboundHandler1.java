/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.core.job.handler;

/**
 * Created by Garen.Pang on 2018/2/2.
 */
public class JobInboundHandler1 implements JobInboundHandler {

    @Override
    public void doJob(JobHandlerContext context, Object... in) throws Exception {
        StringBuffer param = new StringBuffer();
        param.append(in[0].toString() + " \n " + Thread.currentThread().getName() + " inbound handler1");
        context.fireJobEvent("hello 1...");
        context.fireDoJob(param.toString());
    }

    @Override
    public void exceptionCaught(JobHandlerContext context, Throwable cause) throws Exception {

    }
}
