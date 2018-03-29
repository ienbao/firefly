package com.dmsoft.firefly.sdk.job.core;

/**
 * basic class for job manager
 *
 * @author Can Guan
 */
public interface JobManager {
    /**
     * method to initialize job pipe line
     *
     * @param pipelineName job pipe name
     * @param jobPipeline  pipe line
     */
    void initializeJob(String pipelineName, JobPipeline jobPipeline);

    /**
     * method to get pipeline
     *
     * @param pipelineName pipe line name
     * @return pipe line
     */
    JobPipeline getPipeLine(String pipelineName);

    /**
     * method to do job synchronously
     *
     * @param pipelineName job pipe line name
     * @param context      job context
     * @return result
     */
    JobContext fireJobSyn(String pipelineName, JobContext context);

    /**
     * method to do job synchronously
     *
     * @param jobPipeline job pipe line
     * @param context  job context
     * @return job context
     */
    JobContext fireJobSyn(JobPipeline jobPipeline, JobContext context);

    /**
     * method to do job anti-synchronously
     *
     * @param pipelineName job pipe line name
     * @param context      job context
     * @return result
     */
    void fireJobASyn(String pipelineName, JobContext context);

    /**
     * method to do job anti-synchronously
     *
     * @param jobPipeline pipe line
     * @param context  job context
     */
    void fireJobASyn(JobPipeline jobPipeline, JobContext context);

    /**
     * method to find job context by thread
     *
     * @param thread thread
     * @return job context
     */
    JobContext findJobContext(Thread thread);
}
