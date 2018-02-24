/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.core.job.handler;

import com.dmsoft.firefly.sdk.job.core.JobHandlerContext;
import com.dmsoft.firefly.sdk.job.core.JobOutboundHandler;

/**
 * Created by Garen.Pang on 2018/2/2.
 */
public class JobOutboundHandler2 implements JobOutboundHandler {
    
    @Override
    public void returnValue(JobHandlerContext context, Object returnValue) {
        StringBuffer param = new StringBuffer();
        param.append(returnValue.toString() + " \n " + Thread.currentThread().getName() + " outbound handler2");
        context.returnValue(param.toString());
    }

    @Override
    public void exceptionCaught(JobHandlerContext context, Throwable cause) throws Exception {

    }
}
