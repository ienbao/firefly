/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.job.core;

/**
 * Created by Garen.Pang on 2018/2/2.
 */
public interface JobOutboundHandler extends JobHandler {

    /**
     * returnValue
     *
     * @param context     context
     * @param returnValue returnValue
     */
    void returnValue(JobHandlerContext context, Object returnValue);
}
