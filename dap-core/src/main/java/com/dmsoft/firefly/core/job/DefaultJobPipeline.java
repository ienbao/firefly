/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.core.job;

import com.dmsoft.firefly.sdk.job.*;

import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;

/**
 * Created by Garen.Pang on 2018/2/2.
 */
public class DefaultJobPipeline implements JobPipeline {

    final AbstractJobHandlerContext head;
    final AbstractJobHandlerContext tail;
    final JobDoComplete doComplete;
    private Object result;
    private final ExecutorService executorService;

    public DefaultJobPipeline(JobDoComplete doComplete, ExecutorService executorService) {
        this.doComplete = doComplete;
        this.executorService = executorService;
        head = new HeadContext(this, doComplete);
        tail = new TailContext(this, doComplete);

        head.next = tail;
        tail.prev = head;
    }

    @Override
    public JobPipeline addFirst(String name, JobHandler handler) {
        final AbstractJobHandlerContext newCtx;
        synchronized (this) {
            newCtx = newContext(name, handler);
            addFirst0(newCtx);
        }
        return this;
    }

    @Override
    public JobPipeline addLast(String name, JobHandler handler) {
        final AbstractJobHandlerContext newCtx;
        synchronized (this) {
            newCtx = newContext(name, handler);
            addLast0(newCtx);
        }
        return this;
    }

    @Override
    public JobPipeline addBefore(String baseName, String name, JobHandler handler) {
        final AbstractJobHandlerContext newCtx;
        final AbstractJobHandlerContext ctx;
        synchronized (this) {
            ctx = getContext(baseName);
            newCtx = newContext(name, handler);
            addBefore0(ctx, newCtx);
        }
        return this;
    }



    @Override
    public JobPipeline addAfter(String baseName, String name, JobHandler handler) {
        final AbstractJobHandlerContext newCtx;
        final AbstractJobHandlerContext ctx;
        synchronized (this) {
            ctx = getContext(baseName);
            newCtx = newContext(name, handler);
            addAfter0(ctx, newCtx);
        }
        return this;
    }

    @Override
    public JobPipeline addFirst(JobHandler... handlers) {
        return null;
    }

    @Override
    public JobPipeline addLast(JobHandler... handlers) {
        return null;
    }

    @Override
    public JobPipeline remove(JobHandler handler) {
        return null;
    }

    @Override
    public JobHandler remove(String name) {
        return null;
    }

    @Override
    public <T extends JobHandler> T remove(Class<T> handlerType) {
        return null;
    }

    @Override
    public JobHandler removeFirst() {
        return null;
    }

    @Override
    public JobHandler removeLast() {
        return null;
    }

    @Override
    public JobPipeline replace(JobHandler oldHandler, String newName, JobHandler newHandler) {
        return null;
    }

    @Override
    public JobHandler replace(String oldName, String newName, JobHandler newHandler) {
        return null;
    }

    @Override
    public <T extends JobHandler> T replace(Class<T> oldHandlerType, String newName, JobHandler newHandler) {
        return null;
    }

    @Override
    public JobHandler first() {
        return null;
    }

    @Override
    public JobHandler last() {
        return null;
    }

    @Override
    public JobHandler get(String name) {
        return null;
    }

    @Override
    public <T extends JobHandler> T get(Class<T> handlerType) {
        return null;
    }

    @Override
    public JobPipeline fireDoJob(Object param) {
        head.fireDoJob(param);
//        if (this.getResult() == null) {
//            head.returnValue(null);
//        }
        return this;
    }

    @Override
    public void returnValue(Object returnValue) {
        head.returnValue(returnValue);
    }

    private void addFirst0(AbstractJobHandlerContext newCtx) {
        AbstractJobHandlerContext nextCtx = head.next;
        newCtx.prev = head;
        newCtx.next = nextCtx;
        head.next = newCtx;
        nextCtx.prev = newCtx;
    }

    private void addLast0(AbstractJobHandlerContext newCtx) {
        AbstractJobHandlerContext prev = tail.prev;
        newCtx.prev = prev;
        newCtx.next = tail;
        prev.next = newCtx;
        tail.prev = newCtx;
    }

    private AbstractJobHandlerContext getContext(String name) {
        AbstractJobHandlerContext ctx = (AbstractJobHandlerContext) context0(name);
        if (ctx == null) {
            throw new NoSuchElementException(name);
        } else {
            return ctx;
        }
    }

    private AbstractJobHandlerContext context0(String name) {
        AbstractJobHandlerContext context = head.next;
        while (context != tail) {
            if (context.name().equals(name)) {
                return context;
            }
            context = context.next;
        }
        return null;
    }

    private static void addBefore0(AbstractJobHandlerContext ctx, AbstractJobHandlerContext newCtx) {
        newCtx.prev = ctx.prev;
        newCtx.next = ctx;
        ctx.prev.next = newCtx;
        ctx.prev = newCtx;
    }

    private static void addAfter0(AbstractJobHandlerContext ctx, AbstractJobHandlerContext newCtx) {
        newCtx.prev = ctx;
        newCtx.next = ctx.next;
        ctx.next.prev = newCtx;
        ctx.next = newCtx;
    }

    private AbstractJobHandlerContext newContext(String name, JobHandler handler) {
        return new DefaultJobHandlerContext(this, doComplete, name, executorService, handler);
    }

    final class TailContext extends AbstractJobHandlerContext implements JobInboundHandler {

        public TailContext(JobPipeline jobPipeline, JobDoComplete complete) {
            super(jobPipeline, complete, true, false, "TailContext", executorService);
        }

        @Override
        public JobHandler handler() {
            return this;
        }


        @Override
        public void exceptionCaught(JobHandlerContext context, Throwable cause) throws Exception {

        }

        @Override
        public void doJob(JobHandlerContext context, Object in) throws Exception {
            context.fireDoJob(in);
        }
    }

    final class HeadContext extends AbstractJobHandlerContext implements JobOutboundHandler {

        public HeadContext(JobPipeline jobPipeline, JobDoComplete complete) {
            super(jobPipeline, complete, false, true, "HeadContext", executorService);
        }

        @Override
        public JobHandler handler() {
            return this;
        }

        @Override
        public void exceptionCaught(JobHandlerContext context, Throwable cause) throws Exception {

        }

    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
