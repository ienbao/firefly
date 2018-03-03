/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.job;

import com.dmsoft.bamboo.common.monitor.ProcessMonitorListener;
import com.dmsoft.bamboo.common.monitor.ProcessResult;
import com.dmsoft.bamboo.common.monitor.ThreadTask;
import com.dmsoft.firefly.sdk.job.core.JobWorkProcessListener;

/**
 * Created by Garen.Pang on 2018/3/3.
 */
public class JobThread extends Thread implements ProcessMonitorAuto, JobWorkProcessListener {


    public JobThread() {
    }

    public JobThread(Runnable target) {
        super(target);
    }

    public JobThread(ThreadGroup group, Runnable target) {
        super(group, target);
    }

    public JobThread(String name) {
        super(name);
    }

    public JobThread(ThreadGroup group, String name) {
        super(group, name);
    }

    public JobThread(Runnable target, String name) {
        super(target, name);
    }

    public JobThread(ThreadGroup group, Runnable target, String name) {
        super(group, target, name);
    }

    public JobThread(ThreadGroup group, Runnable target, String name, long stackSize) {
        super(group, target, name, stackSize);
    }

    private volatile ProcessMonitorListener listener;
    private volatile ProcessResult processResult;

    @Override
    public void push(ProcessResult processResult) {
        this.processResult = processResult;
        if (listener != null) {
            listener.onProcessChange(processResult);
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

    @Override
    public void addProcessMonitorListener(ProcessMonitorListener l) {
        listener = l;
    }

    @Override
    public void push(int process, int end, String msg, long allTime) {
        ThreadTask threadTask = new ThreadTask() {
            public void run() {
                double second = Math.ceil((double) (allTime / 1000L));
                int average = (int) ((double) (end - process) / second);

                for (int i = 0; (double) i < second && !this.isStop(); ++i) {
                    if (i == 0) {
                        push(process, msg);
                    } else {
                        push(process + average * i);
                    }

                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException var6) {
                        var6.printStackTrace();
                    }
                }

                push(end);
            }
        };
        threadTask.start();
    }

    @Override
    public void push(int process, int end, String msg) {
        ThreadTask threadTask = new ThreadTask() {
            public void run() {
                for (int i = 0; i < end - process && !this.isStop(); ++i) {
                    if (i == 0) {
                        push(process, msg);
                    } else {
                        push(process + i);
                    }

                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException var3) {
                        var3.printStackTrace();
                    }
                }

                push(end);
            }
        };
        threadTask.start();
    }

    public ProcessMonitorListener getListener() {
        return listener;
    }
}
