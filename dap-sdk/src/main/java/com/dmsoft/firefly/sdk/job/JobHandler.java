/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.job;

/**
 * Created by Garen.Pang on 2018/2/2.
 */
public interface JobHandler {

    /**
     * exceptionCaught
     *
     * @param context context
     * @param cause   cause
     * @throws Exception Exception
     */
    void exceptionCaught(JobHandlerContext context, Throwable cause) throws Exception;

}
