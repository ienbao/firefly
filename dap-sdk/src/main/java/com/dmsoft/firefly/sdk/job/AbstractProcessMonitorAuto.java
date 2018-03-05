/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.job;

import com.dmsoft.bamboo.common.monitor.ProcessMonitorListener;
import com.dmsoft.bamboo.common.monitor.ProcessResult;
import com.dmsoft.bamboo.common.monitor.ThreadTask;

/**
 * Created by Garen.Pang on 2018/3/2.
 */
public abstract class AbstractProcessMonitorAuto implements ProcessMonitorAuto {

    private ThreadLocal<ProcessResult> threadLocal = new ThreadLocal();
    private volatile ThreadTask currentThread;
    private InheritableThreadLocal<ProcessMonitorListener> processMonitorListener = new InheritableThreadLocal<>();

    public AbstractProcessMonitorAuto() {
    }

    public void push(ProcessResult processResult) {
        this.threadLocal.set(processResult);
        if (this.processMonitorListener.get() != null) {
            this.processMonitorListener.get().onProcessChange((ProcessResult) this.threadLocal.get());
        }

    }

    public void push(int point, String msg) {
        this.push(new ProcessResult(point, msg));
    }

    public void push(int point) {
        this.push(new ProcessResult(point));
    }

    public void push(String msg) {
        this.push(new ProcessResult(msg));
    }

    public void push(int point, String msg, String subTitle) {
        this.push(new ProcessResult(point, msg, subTitle));
    }

    public void push(boolean exit) {
        this.push(new ProcessResult(exit));
    }

    public void push(int point, String msg, String subTitle, boolean exit) {
        this.push(new ProcessResult(point, msg, subTitle, exit));
    }

    public void pushSubTitle(String subTitle) {
        this.push(new ProcessResult((Integer) null, (String) null, subTitle));
    }

    public void pushErrorMsg(String msg) {
        ProcessResult processResult = new ProcessResult(msg);
        processResult.setErrorFlag(true);
        this.push(processResult);
    }

    public void pushErrorMsg(String msg, String subTitle) {
        ProcessResult processResult = new ProcessResult(msg);
        processResult.setErrorFlag(true);
        processResult.setSubTitle(subTitle);
        this.push(processResult);
    }

    public void addProcessMonitorListener(ProcessMonitorListener l) {
        this.processMonitorListener.set(l);
    }

    public ThreadTask getCurrentThread() {
        return this.currentThread;
    }

    public void setCurrentThread(ThreadTask currentThread) {
        this.currentThread = currentThread;
    }
}
