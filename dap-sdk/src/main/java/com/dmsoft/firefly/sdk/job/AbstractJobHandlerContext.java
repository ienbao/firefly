/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.job;

import com.dmsoft.bamboo.common.monitor.ProcessMonitorListener;
import com.dmsoft.bamboo.common.monitor.ProcessResult;
import com.dmsoft.firefly.sdk.job.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by Garen.Pang on 2018/2/2.
 */
public abstract class AbstractJobHandlerContext implements JobHandlerContext {

    private final JobPipeline jobPipeline;
    private final JobDoComplete jobDoComplete;
    private final boolean inbound;
    private final boolean outbound;
    private final String name;
    private final List<JobEventListener> eventListeners;
    private final Job session;
    volatile AbstractJobHandlerContext next;
    volatile AbstractJobHandlerContext prev;
    private Logger logger = LoggerFactory.getLogger(AbstractJobHandlerContext.class);
    private ExecutorService executorService;
    private volatile boolean doNextInbound;
    private volatile boolean doNextOutbound;

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
        jobPipeline.addProcess(handler().getWeight());
        JobThread thread = new JobThread() {
            @Override
            public void run() {
                next.invokeDoJob(param);
            }
        };
        thread.setCurrentProcess(jobPipeline.getCurrentProcess());
        thread.setWeight(100 * next.handler().getWeight() / (jobPipeline.getAllWeight() == 0 ? 1 : jobPipeline.getAllWeight()));
        thread.addProcessMonitorListener(getContextProcessMonitorListenerIfExists());
        thread.start();
        handler().remove();
//        executorService.execute(thread);
        return this;
    }

    private void invokeDoJob(Object... param) {
        try {
            ((JobInboundHandler) handler()).doJob(this, param);
            if (!doNextInbound && !doNextOutbound) {
                handler().remove();
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
        jobPipeline.addProcess(handler().getWeight());
        JobThread thread = new JobThread() {
            @Override
            public void run() {
                next.invokeReturnValue(returnValue);
            }
        };
        thread.setCurrentProcess(jobPipeline.getCurrentProcess());
        thread.setWeight(100 * next.handler().getWeight() / (jobPipeline.getAllWeight() == 0 ? 1 : jobPipeline.getAllWeight()));
        thread.addProcessMonitorListener(getContextProcessMonitorListenerIfExists());
        thread.start();
        handler().remove();
//        executorService.execute(thread);
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

    @Override
    public void fireProcessEvent(ProcessResult process) {
        if (session.getProcessMonitorListener() != null) {
            session.getProcessMonitorListener().onProcessChange(process);
        }
    }

    @Override
    public ProcessMonitorListener getContextProcessMonitorListenerIfExists() {
        if (session != null && session.getProcessMonitorListener() != null) {
            return session.getProcessMonitorListener();
        }
        return null;
    }

    public abstract JobHandler handler();

    private void notifyHandlerException(Throwable cause) {
        try {
            handler().exceptionCaught(this, cause);
        } catch (Exception e) {
            logger.error("handler exception Caught method error. ");
        } finally {
            jobPipeline.returnValue(cause);
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
