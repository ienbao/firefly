/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.job;

import com.dmsoft.firefly.sdk.job.core.*;
import com.google.common.collect.Lists;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Garen.Pang on 2018/2/2.
 */
public class DefaultJobPipeline implements JobPipeline {

    final AbstractJobHandlerContext head;
    final AbstractJobHandlerContext tail;
    final JobDoComplete doComplete;
    private final ExecutorService executorService;
    private final List<JobEventListener> jobEventListeners;
    private final Job session;
    private Object result;
    private AtomicInteger process = new AtomicInteger(0);
    private volatile int allWeight = 0;
    private final Semaphore semaphore;

    private static final FastThreadLocal<Map<Class<?>, String>> nameCaches =
            new FastThreadLocal<Map<Class<?>, String>>() {
                @Override
                protected Map<Class<?>, String> initialValue() throws Exception {
                    return new WeakHashMap<Class<?>, String>();
                }
            };

    public DefaultJobPipeline(JobDoComplete doComplete, ExecutorService executorService, List<JobEventListener> jobEventListeners, Job session, Semaphore semaphore) {
        this.doComplete = doComplete;
        this.executorService = executorService;
        this.session = session;
        this.semaphore = semaphore;
        head = new HeadContext(this, doComplete);
        tail = new TailContext(this, doComplete);
        this.jobEventListeners = jobEventListeners;

        head.next = tail;
        tail.prev = head;
    }

    @Override
    public JobPipeline addFirst(String name, JobHandler handler) {
        final AbstractJobHandlerContext newCtx;
        synchronized (this) {
            newCtx = newContext(name, handler);
            addFirst0(newCtx);
            allWeight += handler.getWeight();
        }
        return this;
    }

    @Override
    public JobPipeline addLast(String name, JobHandler handler) {
        final AbstractJobHandlerContext newCtx;
        synchronized (this) {
            newCtx = newContext(name, handler);
            addLast0(newCtx);
            allWeight += handler.getWeight();
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
            allWeight += handler.getWeight();
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
            allWeight += handler.getWeight();
        }
        return this;
    }

    @Override
    public JobPipeline addFirst(JobHandler... handlers) {
        if (handlers == null) {
            throw new NullPointerException("handlers");
        }
        if (handlers.length == 0 || handlers[0] == null) {
            return this;
        }

        int size;
        for (size = 1; size < handlers.length; size++) {
            if (handlers[size] == null) {
                break;
            }
        }

        for (int i = size - 1; i >= 0; i--) {
            JobHandler h = handlers[i];
            addFirst(generateName(h), h);
        }

        return this;
    }

    @Override
    public JobPipeline addLast(JobHandler... handlers) {
        if (handlers == null) {
            throw new NullPointerException("handlers");
        }

        for (JobHandler h : handlers) {
            if (h == null) {
                break;
            }
            addLast(generateName(h), h);
        }

        return this;
    }

    @Override
    public JobPipeline remove(JobHandler handler) {
        remove(getContextOrDie(handler));
        return this;
    }

    @Override
    public JobHandler remove(String name) {
        return remove(getContextOrDie(name)).handler();
    }

    @Override
    public <T extends JobHandler> T remove(Class<T> handlerType) {
        return (T) remove(getContextOrDie(handlerType)).handler();
    }

    @Override
    public JobHandler removeFirst() {
        if (head.next == tail) {
            throw new NoSuchElementException();
        }
        return remove(head.next).handler();
    }

    @Override
    public JobHandler removeLast() {
        if (head.next == tail) {
            throw new NoSuchElementException();
        }
        return remove(tail.prev).handler();
    }


    @Override
    public JobHandler first() {
        AbstractJobHandlerContext first = firstContext();
        if (first == null) {
            return null;
        }
        return first.handler();
    }

    private AbstractJobHandlerContext firstContext() {
        AbstractJobHandlerContext first = head.next;
        if (first == tail) {
            return null;
        }
        return head.next;
    }

    @Override
    public JobHandler last() {
        AbstractJobHandlerContext last = tail.prev;
        if (last == head) {
            return null;
        }
        return last.handler();
    }

    @Override
    public JobHandler get(String name) {
        AbstractJobHandlerContext ctx = context(name);
        if (ctx == null) {
            return null;
        } else {
            return ctx.handler();
        }
    }

    @Override
    public <T extends JobHandler> T get(Class<T> handlerType) {
        AbstractJobHandlerContext ctx = context(handlerType);
        if (ctx == null) {
            return null;
        } else {
            return (T) ctx.handler();
        }
    }

    @Override
    public JobPipeline fireDoJob(Object... param) {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        head.fireDoJob(param);
        return this;
    }

    @Override
    public void returnValue(Object returnValue) {
        head.returnValue(returnValue);
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public int getCurrentProcess() {
        return process.get();
    }

    @Override
    public void addProcess(int process) {
        this.process.addAndGet(process);
    }

    @Override
    public int getAllWeight() {
        return allWeight;
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

    private AbstractJobHandlerContext newContext(String name, JobHandler handler) {
        return new DefaultJobHandlerContext(this, doComplete, name, executorService, handler, jobEventListeners, session, semaphore);
    }

    private AbstractJobHandlerContext context(String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }

        return context0(name);
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

    private AbstractJobHandlerContext remove(final AbstractJobHandlerContext ctx) {
        assert ctx != head && ctx != tail;

        synchronized (this) {
            remove0(ctx);
            allWeight -= ctx.handler().getWeight();
            return ctx;
        }
    }

    private static void remove0(AbstractJobHandlerContext ctx) {
        AbstractJobHandlerContext prev = ctx.prev;
        AbstractJobHandlerContext next = ctx.next;
        prev.next = next;
        next.prev = prev;
    }

    private String generateName(JobHandler handler) {
        Map<Class<?>, String> cache = nameCaches.get();
        Class<?> handlerType = handler.getClass();
        String name = cache.get(handlerType);
        if (name == null) {
            name = generateName0(handlerType);
            cache.put(handlerType, name);
        }

        if (context0(name) != null) {
            String baseName = name.substring(0, name.length() - 1); // Strip the trailing '0'.
            for (int i = 1;; i++) {
                String newName = baseName + i;
                if (context0(newName) == null) {
                    name = newName;
                    break;
                }
            }
        }
        return name;
    }

    private static String generateName0(Class<?> handlerType) {
        return StringUtil.simpleClassName(handlerType) + "#0";
    }

    private AbstractJobHandlerContext getContextOrDie(String name) {
        AbstractJobHandlerContext ctx = context(name);
        if (ctx == null) {
            throw new NoSuchElementException(name);
        } else {
            return ctx;
        }
    }

    private AbstractJobHandlerContext getContextOrDie(JobHandler handler) {
        AbstractJobHandlerContext ctx = context(handler);
        if (ctx == null) {
            throw new NoSuchElementException(handler.getClass().getName());
        } else {
            return ctx;
        }
    }

    private AbstractJobHandlerContext getContextOrDie(Class<? extends JobHandler> handlerType) {
        AbstractJobHandlerContext ctx = context(handlerType);
        if (ctx == null) {
            throw new NoSuchElementException(handlerType.getName());
        } else {
            return ctx;
        }
    }

    private AbstractJobHandlerContext context(JobHandler handler) {
        if (handler == null) {
            throw new NullPointerException("handler");
        }

        AbstractJobHandlerContext ctx = head.next;
        for (;;) {

            if (ctx == null) {
                return null;
            }

            if (ctx.handler() == handler) {
                return ctx;
            }

            ctx = ctx.next;
        }
    }

    private AbstractJobHandlerContext context(Class<? extends JobHandler> handlerType) {
        if (handlerType == null) {
            throw new NullPointerException("handlerType");
        }

        AbstractJobHandlerContext ctx = head.next;
        for (;;) {
            if (ctx == null) {
                return null;
            }
            if (handlerType.isAssignableFrom(ctx.handler().getClass())) {
                return ctx;
            }
            ctx = ctx.next;
        }
    }

    final class TailContext extends AbstractJobHandlerContext implements JobInboundHandler {

        public TailContext(JobPipeline jobPipeline, JobDoComplete complete) {
            super(jobPipeline, complete, true, false, "TailContext", executorService, Lists.newArrayList(), session, semaphore);
        }

        @Override
        public JobHandler handler() {
            return this;
        }


        @Override
        public void exceptionCaught(JobHandlerContext context, Throwable cause) throws Exception {

        }

        @Override
        public void doJob(JobHandlerContext context, Object... in) throws Exception {
            context.fireDoJob(in);
        }
    }

    final class HeadContext extends AbstractJobHandlerContext implements JobOutboundHandler {

        public HeadContext(JobPipeline jobPipeline, JobDoComplete complete) {
            super(jobPipeline, complete, false, true, "HeadContext", executorService, Lists.newArrayList(), session, semaphore);
        }

        @Override
        public JobHandler handler() {
            return this;
        }


        @Override
        public void exceptionCaught(JobHandlerContext context, Throwable cause) throws Exception {

        }

        @Override
        public void returnValue(JobHandlerContext context, Object returnValue) {
            context.returnValue(returnValue);
        }
    }
}
