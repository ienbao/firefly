/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.job.core;

import com.dmsoft.bamboo.common.monitor.ProcessMonitorListener;
import com.dmsoft.firefly.sdk.job.Job;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * basic class for job manager
 *
 * @author Can Guan
 */
public interface JobManager {

    /**
     * createJob
     *
     * @param jobName  jobName
     * @param pipeline pipeline
     */
    void initializeJob(String jobName, InitJobPipeline pipeline);

    void addJobEventListenerByName(String jobName, JobEventListener listener);

    void removeJobEventListener(String jobName, JobEventListener listener);

    @Deprecated
    void addJobProcessListener(String jobName, ProcessMonitorListener listener);

    @Deprecated
    void removeJobProcessListener(String jobName, ProcessMonitorListener listener);

    /**
     * doJobSyn
     *
     * @param job    job
     * @param object object
     * @return Object
     */
    Object doJobSyn(Job job, Object... object);

    /**
     * doJobSyn
     *
     * @param job     job
     * @param object  object
     * @param timeout timeout
     * @param unit    unit
     * @return Object
     */
    Object doJobSyn(Job job, long timeout, TimeUnit unit, Object... object);

    /**
     * doJobASyn
     *
     * @param job      job
     * @param object   object
     * @param complete complete
     */
    void doJobASyn(Job job, JobDoComplete complete, Object... object);

    void doJobASyn(Job job, Object... object);

    /**
     * getExecutorService
     *
     * @return
     */
    ExecutorService getExecutorService();

}
