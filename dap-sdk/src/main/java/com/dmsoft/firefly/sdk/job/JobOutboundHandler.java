/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.job;

/**
 * Created by Garen.Pang on 2018/2/2.
 */
public interface JobOutboundHandler extends JobHandler {

    void returnValue(JobHandlerContext context, Object returnValue);
}
