package com.dmsoft.firefly.sdk.job.core;

/**
 * interface class for job handler
 *
 * @author Can Guan
 */
public interface JobHandler extends JobRunnable {
    /**
     * method to get weight
     *
     * @return weight
     */
    Double getWeight();

    /**
     * method to set weight
     *
     * @param weight weight
     */
    JobHandler setWeight(Double weight);

    /**
     * method to get name
     *
     * @return handler name
     */
    String getName();

    /**
     * method to set name
     *
     * @param name name
     */
    JobHandler setName(String name);
}
