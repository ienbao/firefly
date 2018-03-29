package com.dmsoft.firefly.sdk.job.core;

/**
 * listable interface for job
 *
 * @author Can Guan
 */
public interface JobListable {

    /**
     * method to add job event listener
     *
     * @param listener job event listener
     */
    void addJobEventListener(JobEventListener listener);

    /**
     * method to remove listener
     *
     * @param listener new job event listener
     * @return removed or not
     */
    boolean removeListener(JobEventListener listener);
}
