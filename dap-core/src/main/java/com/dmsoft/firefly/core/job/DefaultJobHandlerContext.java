/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.core.job;

import com.dmsoft.firefly.sdk.job.*;

import java.util.concurrent.ExecutorService;

/**
 * Created by Garen.Pang on 2018/2/2.
 */
public class DefaultJobHandlerContext extends AbstractJobHandlerContext {

    private final JobHandler handler;

//    public DefaultJobHandlerContext(JobPipeline jobPipeline, JobDoComplete jobDoComplete, String name, JobHandler handler) {
//        super(jobPipeline, jobDoComplete, isInbound(handler), isOutbound(handler), name);
//        if (handler == null) {
//            throw new NullPointerException("handler");
//        }
//        this.handler = handler;
//    }

    public DefaultJobHandlerContext(JobPipeline jobPipeline, JobDoComplete jobDoComplete, String name, ExecutorService executorService, JobHandler handler) {
        super(jobPipeline, jobDoComplete, isInbound(handler), isOutbound(handler), name, executorService);
        this.handler = handler;
    }

    @Override
    public JobHandler handler() {
        return handler;
    }

    private static boolean isInbound(JobHandler handler) {
        return handler instanceof JobInboundHandler;
    }

    private static boolean isOutbound(JobHandler handler) {
        return handler instanceof JobOutboundHandler;
    }
}
