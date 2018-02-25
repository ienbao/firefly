/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.job.core;

import com.dmsoft.firefly.sdk.job.JobEvent;

/**
 * Created by Garen.Pang on 2018/2/25.
 */
public interface JobEventListener {

    void eventNotify(JobEvent event);
}
