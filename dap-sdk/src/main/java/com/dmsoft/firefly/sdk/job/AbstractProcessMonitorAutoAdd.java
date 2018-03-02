/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.job;

import com.dmsoft.bamboo.common.monitor.ThreadTask;

/**
 * Created by Garen.Pang on 2018/3/2.
 */
public abstract class AbstractProcessMonitorAutoAdd extends AbstractProcessMonitorAuto implements ProcessMonitorAuto {

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
        if (getCurrentThread() != null && (getCurrentThread().getState().equals(Thread.State.RUNNABLE) || getCurrentThread().getState().equals(Thread.State.TIMED_WAITING))) {
            getCurrentThread().setStop();
            setCurrentThread((ThreadTask) null);
        }
        setCurrentThread(threadTask);
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
        if (getCurrentThread() != null && (getCurrentThread().getState().equals(Thread.State.RUNNABLE) || getCurrentThread().getState().equals(Thread.State.TIMED_WAITING))) {
            getCurrentThread().setStop();
            setCurrentThread((ThreadTask) null);
        }

        setCurrentThread(threadTask);
        threadTask.start();
    }

}
