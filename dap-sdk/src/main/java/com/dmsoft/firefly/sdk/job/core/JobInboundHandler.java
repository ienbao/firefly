/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.job.core;

/**
 * Created by Garen.Pang on 2018/2/2.
 */
public interface JobInboundHandler extends JobHandler {

    /**
     * doJob
     *
     * @param context context
     * @param in      in
     * @throws Exception Exception
     */
    void doJob(JobHandlerContext context, Object in) throws Exception;
}
