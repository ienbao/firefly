package com.dmsoft.firefly.sdk.job.core;

/**
 * job context factory
 *
 * @author Can Guan, Garen Pang
 */
public interface JobFactory {
    /**
     * method to create job context
     *
     * @return job context
     */
    JobContext createJobContext();

    /**
     * method to create job pie
     *
     * @return new pipe line
     */
    JobPipeline createJobPipeLine();
}
