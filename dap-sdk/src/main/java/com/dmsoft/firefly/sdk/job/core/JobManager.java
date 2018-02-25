/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.job.core;

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
    void createJob(String jobName, InitJobPipeline pipeline);

    void addJobEventListener(String jobName, JobEventListener listener);

    void removeJobEventListener(String jobName, JobEventListener listener);
    /**
     * doJobSyn
     *
     * @param jobName jobName
     * @param object  object
     * @return Object
     */
    Object doJobSyn(String jobName, Object object);

    /**
     * doJobSyn
     *
     * @param jobName jobName
     * @param object  object
     * @param timeout timeout
     * @param unit    unit
     * @return Object
     */
    Object doJobSyn(String jobName, Object object, long timeout, TimeUnit unit);

    /**
     * doJobASyn
     *
     * @param jobName  jobName
     * @param object   object
     * @param complete complete
     */
    void doJobASyn(String jobName, Object object, JobDoComplete complete);

    /**
     * getExecutorService
     *
     * @return
     */
    ExecutorService getExecutorService();

}
