/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.job;

import com.dmsoft.firefly.sdk.job.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;

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
    private ExecutorService executorService;
    private volatile boolean doNextInbound;
    private volatile boolean doNextOutbound;
    private final List<JobEventListener> eventListeners;
    private final Job session;

//    public AbstractJobHandlerContext(JobPipeline jobPipeline, JobDoComplete jobDoComplete, boolean inbound, boolean outbound, String name) {
//        this.jobPipeline = jobPipeline;
//        this.jobDoComplete = jobDoComplete;
//        this.inbound = inbound;
//        this.outbound = outbound;
//        this.name = name;
//    }

    public AbstractJobHandlerContext(JobPipeline jobPipeline, JobDoComplete jobDoComplete, boolean inbound, boolean outbound, String name, ExecutorService executorService, List<JobEventListener> eventListeners, Job session) {
        this.jobPipeline = jobPipeline;
        this.jobDoComplete = jobDoComplete;
        this.inbound = inbound;
        this.outbound = outbound;
        this.name = name;
        this.executorService = executorService;
        this.eventListeners = eventListeners;
        this.session = session;
    }

    @Override
    public JobHandlerContext fireDoJob(Object... param) {
        AbstractJobHandlerContext next = findContextInbound();
        doNextInbound = true;
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                next.invokeDoJob(param);
            }
        });
        return this;
    }

    private void invokeDoJob(Object... param) {
        try {
            ((JobInboundHandler) handler()).doJob(this, param);
            if (!doNextInbound && !doNextOutbound) {
                jobPipeline.returnValue(null);
            }
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
        doNextOutbound = true;
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                next.invokeReturnValue(returnValue);
            }
        });
    }

    private void invokeReturnValue(Object returnValue) {
        try {
            ((JobOutboundHandler) handler()).returnValue(this, returnValue);
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

    @Override
    public <T> void fireJobEvent(T result) {
        if (eventListeners != null) {
            eventListeners.forEach(v -> {
                JobEvent event = new JobEvent(this.session.getJobId(), result);
                v.eventNotify(event);
            });
        }
        if (session.getJobEventListeners() != null) {
            session.getJobEventListeners().forEach(v -> {
                JobEvent event = new JobEvent(this.session.getJobId(), result);
                v.eventNotify(event);
            });
        }
    }

    public abstract JobHandler handler();

    private void notifyHandlerException(Throwable cause) {
        try {
            handler().exceptionCaught(this, cause);
        } catch (Exception e) {
            logger.error("handler exception Caught method error. ");
        } finally {
            jobPipeline.returnValue(null);
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
