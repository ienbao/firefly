/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.job.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Garen.Pang on 2018/2/2.
 */
public interface JobHandler {

    Map<Object, Integer> WEIGHT = new ConcurrentHashMap<>();

    /**
     * exceptionCaught
     *
     * @param context context
     * @param cause   cause
     * @throws Exception Exception
     */
    void exceptionCaught(JobHandlerContext context, Throwable cause) throws Exception;

    default JobHandler setWeight(int weight) {
        WEIGHT.put(this, weight);
        return this;
    }

    default int getWeight() {
        return WEIGHT.containsKey(this) ? WEIGHT.get(this) : 0;
    }

    //have a bug :
    default void remove() {
        WEIGHT.remove(this);
    }

}
