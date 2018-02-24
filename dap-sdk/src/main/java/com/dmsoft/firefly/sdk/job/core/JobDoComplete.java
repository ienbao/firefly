/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.job.core;

/**
 * Created by Garen.Pang on 2018/2/2.
 */
public interface JobDoComplete {

    /**
     * doComplete event
     *
     * @param returnValue returnValue
     */
    void doComplete(Object returnValue);
}
