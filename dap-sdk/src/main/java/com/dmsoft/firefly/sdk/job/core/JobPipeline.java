/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.job.core;

/**
 * Created by Garen.Pang on 2018/2/2.
 */
public interface JobPipeline {

    /**
     * addFirst
     *
     * @param name    name
     * @param handler handler
     * @return JobPipeline
     */
    JobPipeline addFirst(String name, JobHandler handler);

    /**
     * addFirst
     *
     * @param name    name
     * @param handler handler
     * @return JobPipeline
     */
    JobPipeline addLast(String name, JobHandler handler);

    /**
     * addFirst
     *
     * @param baseName baseName
     * @param name     name
     * @param handler  handler
     * @return JobPipeline
     */
    JobPipeline addBefore(String baseName, String name, JobHandler handler);

    /**
     * addFirst
     *
     * @param baseName baseName
     * @param name     name
     * @param handler  handler
     * @return JobPipeline
     */
    JobPipeline addAfter(String baseName, String name, JobHandler handler);

    /**
     * addFirst
     *
     * @param handlers handlers
     * @return JobPipeline
     */
    JobPipeline addFirst(JobHandler... handlers);

    /**
     * addFirst
     *
     * @param handlers handlers
     * @return JobPipeline
     */
    JobPipeline addLast(JobHandler... handlers);

    /**
     * addFirst
     *
     * @param handler handler
     * @return JobPipeline
     */
    JobPipeline remove(JobHandler handler);

    /**
     * remove
     *
     * @param name name
     * @return JobHandler
     */
    JobHandler remove(String name);

    /**
     * remove
     *
     * @param handlerType handlerType
     * @param <T>         JobHandler
     * @return JobHandler
     */
    <T extends JobHandler> T remove(Class<T> handlerType);

    /**
     * removeFirst
     *
     * @return
     */
    JobHandler removeFirst();

    /**
     * removeLast
     *
     * @return
     */
    JobHandler removeLast();

    /**
     * first
     *
     * @return
     */
    JobHandler first();

    /**
     * last
     *
     * @return
     */
    JobHandler last();

    /**
     * get
     *
     * @param name name
     * @return JobHandler
     */
    JobHandler get(String name);

    /**
     * get
     *
     * @param handlerType handlerType
     * @param <T>         JobHandler
     * @return JobHandler
     */
    <T extends JobHandler> T get(Class<T> handlerType);

    /**
     * fireDoJob
     *
     * @param param param
     * @return JobPipeline
     */
    JobPipeline fireDoJob(Object... param);

    /**
     * returnValue
     *
     * @param returnValue returnValue
     */
    void returnValue(Object returnValue);

    /**
     * getResult
     *
     * @return
     */
    Object getResult();

    /**
     * setResult
     *
     * @param result result
     */
    void setResult(Object result);

    int getCurrentProcess();

    void addProcess(int process);

    int getAllWeight();
}
