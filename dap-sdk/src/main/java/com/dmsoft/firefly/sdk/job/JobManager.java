package com.dmsoft.firefly.sdk.job;

/**
 * basic class for job manager
 * @author Can Guan
 */
public interface JobManager {

    void createJob(String jobName);

    Object doJob(String jobName, Object object);

    void registerHandler(String jobName, Handler handler);

    void registerHandlerFirst(String jobName, Handler handler);

    void registerHandlerNextIndex(String jobName, int index, Handler handler);


}
