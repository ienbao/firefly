package com.dmsoft.firefly.sdk.job;

/**
 * basic class for job manager
 *
 * @author Can Guan
 */
public interface JobManager {

    /**
     * create job
     *
     * @param jobName job name
     */
    void createJob(String jobName);

    /**
     * do job
     *
     * @param jobName job name
     * @param object  first param
     * @return result
     */
    Object doJob(String jobName, Object object);

    /**
     * method to register handler
     *
     * @param jobName job
     * @param handler handler
     */
    void registerHandler(String jobName, Handler handler);

    /**
     * method to register handler at first
     *
     * @param jobName job name
     * @param handler handler
     */
    void registerHandlerFirst(String jobName, Handler handler);

    /**
     * method to register handler at next
     *
     * @param jobName job name
     * @param index   index
     * @param handler handler
     */
    void registerHandlerNextIndex(String jobName, int index, Handler handler);
}
