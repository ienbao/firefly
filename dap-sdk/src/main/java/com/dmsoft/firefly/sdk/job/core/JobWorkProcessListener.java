/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.job.core;

import com.dmsoft.bamboo.common.monitor.ProcessMonitorListener;

/**
 * Created by Garen.Pang on 2018/3/3.
 */
public interface JobWorkProcessListener {

    void addProcessMonitorListener(ProcessMonitorListener l);

    ProcessMonitorListener getListener();
}
