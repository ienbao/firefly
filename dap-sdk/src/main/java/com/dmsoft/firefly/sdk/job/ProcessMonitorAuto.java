/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.job;

import com.dmsoft.bamboo.common.monitor.ProcessMonitor;

/**
 * Created by Garen.Pang on 2018/3/2.
 */
public interface ProcessMonitorAuto extends ProcessMonitor {

    void push(int process, int end, String msg, long allTime);

    void push(int process, int end, String msg);
}
