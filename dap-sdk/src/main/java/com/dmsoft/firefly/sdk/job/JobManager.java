package com.dmsoft.firefly.sdk.job;

import java.util.concurrent.ExecutorService;

/**
 * basic class for job manager
 *
 * @author Can Guan
 */
public interface JobManager {

    void createJob(String jobName, InitJobPipeline pipeline);

    Object doJobSyn(String jobName, Object object);

    void doJobASyn(String jobName, Object object, JobDoComplete complete);

    ExecutorService getService();

}
