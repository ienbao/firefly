package com.dmsoft.firefly.sdk.job.core;

@FunctionalInterface
public interface JobRunnable {
    /**
     * method to override for job
     *
     * @param context job context
     */
    void doJob(JobContext context);
}
