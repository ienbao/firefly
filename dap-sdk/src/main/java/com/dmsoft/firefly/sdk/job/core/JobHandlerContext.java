/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.job.core;

import com.dmsoft.firefly.sdk.job.JobEvent;

/**
 * Created by Garen.Pang on 2018/2/2.
 */
public interface JobHandlerContext {

    /**
     * fireDoJob
     *
     * @param param param
     * @return JobHandlerContext
     */
    JobHandlerContext fireDoJob(Object... param);

    /**
     * returnValue
     *
     * @param returnValue returnValue
     */
    void returnValue(Object returnValue);

    /**
     * pipeline
     *
     * @return
     */
    JobPipeline pipeline();

    /**
     * handler
     *
     * @return
     */
    JobHandler handler();

    /**
     * name
     *
     * @return
     */
    String name();

    <T> void fireJobEvent(T result);
}
