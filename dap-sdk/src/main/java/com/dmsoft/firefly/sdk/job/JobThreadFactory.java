/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.job;

/**
 * Created by Garen.Pang on 2018/3/3.
 */
public interface JobThreadFactory {

    Thread newThread(JobThreadPoolExecutor.Worker r);
}
