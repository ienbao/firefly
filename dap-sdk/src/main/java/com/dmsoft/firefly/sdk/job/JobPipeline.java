/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.job;

/**
 * Created by Garen.Pang on 2018/2/2.
 */
public interface JobPipeline {

    JobPipeline addFirst(String name, JobHandler handler);

    JobPipeline addLast(String name, JobHandler handler);

    JobPipeline addBefore(String baseName, String name, JobHandler handler);

    JobPipeline addAfter(String baseName, String name, JobHandler handler);

    JobPipeline addFirst(JobHandler... handlers);

    JobPipeline addLast(JobHandler... handlers);

    JobPipeline remove(JobHandler handler);

    JobHandler remove(String name);

    <T extends JobHandler> T remove(Class<T> handlerType);

    JobHandler removeFirst();

    JobHandler removeLast();

    JobPipeline replace(JobHandler oldHandler, String newName, JobHandler newHandler);

    JobHandler replace(String oldName, String newName, JobHandler newHandler);

    <T extends JobHandler> T replace(Class<T> oldHandlerType, String newName,
                                     JobHandler newHandler);

    JobHandler first();

    JobHandler last();

    JobHandler get(String name);

    <T extends JobHandler> T get(Class<T> handlerType);

    JobPipeline fireDoJob(Object param);

    Object getResult();

    void setResult(Object result);
}
