/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.core.job;

import com.dmsoft.firefly.sdk.job.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Garen.Pang on 2018/2/2.
 */
public abstract class AbstractJobHandlerContext implements JobHandlerContext {

    private Logger logger = LoggerFactory.getLogger(AbstractJobHandlerContext.class);
    private final JobPipeline jobPipeline;
    private final JobDoComplete jobDoComplete;
    volatile AbstractJobHandlerContext next;
    volatile AbstractJobHandlerContext prev;
    private final boolean inbound;
    private final boolean outbound;
    private final String name;

    public AbstractJobHandlerContext(JobPipeline jobPipeline, JobDoComplete jobDoComplete, boolean inbound, boolean outbound, String name) {
        this.jobPipeline = jobPipeline;
        this.jobDoComplete = jobDoComplete;
        this.inbound = inbound;
        this.outbound = outbound;
        this.name = name;
    }


    @Override
    public JobHandlerContext fireDoJob(Object param) {
        AbstractJobHandlerContext next = findContextInbound();
        next.invokeDoJob(param);
        return this;
    }

    private void invokeDoJob(Object param) {
        try {
            ((JobInboundHandler) handler()).doJob(this, param);
        } catch (Exception e) {
            notifyHandlerException(e);
        }
    }

    @Override
    public void returnValue(Object returnValue) {
        if (this.prev == null) {
            jobDoComplete.doComplete(returnValue);
            jobPipeline.setResult(returnValue);
            return;
        }
        AbstractJobHandlerContext next = findContextOutbound();
        next.invokeReturnValue(returnValue);
    }

    private void invokeReturnValue(Object returnValue) {
        try {
            ((JobOutboundHandler) handler()).returnValue(returnValue);
        } catch (Exception e) {
            notifyHandlerException(e);
        }
    }

    @Override
    public JobPipeline pipeline() {
        return jobPipeline;
    }

    @Override
    public String name() {
        return name;
    }

    public abstract JobHandler handler();

    private void notifyHandlerException(Throwable cause) {
        try {
            handler().exceptionCaught(this, cause);
        } catch (Exception e) {
            logger.error("handler exception Caught method error. ");
        }
    }

    private AbstractJobHandlerContext findContextInbound() {
        AbstractJobHandlerContext ctx = this;
        do {
            ctx = ctx.next;
        } while (!ctx.inbound);
        return ctx;
    }


    private AbstractJobHandlerContext findContextOutbound() {
        AbstractJobHandlerContext ctx = this;
        do {
            ctx = ctx.prev;
        } while (!ctx.outbound);
        return ctx;
    }


}
