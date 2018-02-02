package com.dmsoft.firefly.sdk.job;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * basic class for job manager
 *
 * @author Can Guan
 */
public interface JobManager {

    void createJob(String jobName, InitJobPipeline pipeline);

    Object doJobSyn(String jobName, Object object);

    Object doJobSyn(String jobName, Object object, long timeout, TimeUnit unit);

    void doJobASyn(String jobName, Object object, JobDoComplete complete);

    ExecutorService getExecutorService();

}
