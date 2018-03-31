package com.dmsoft.firefly.sdk.job.core;

/**
 * runnable inter face
 *
 * @author Can Guan, Garen Pang
 */
@FunctionalInterface
public interface JobRunnable {
    /**
     * method to override for job
     *
     * @param context job context
     */
    void doJob(JobContext context);
}
