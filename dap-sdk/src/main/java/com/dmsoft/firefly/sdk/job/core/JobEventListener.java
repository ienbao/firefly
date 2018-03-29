package com.dmsoft.firefly.sdk.job.core;

/**
 * listener for job event
 */
@FunctionalInterface
public interface JobEventListener {

    /**
     * method to notify event
     *
     * @param event job event
     */
    void eventNotify(JobEvent event);
}
