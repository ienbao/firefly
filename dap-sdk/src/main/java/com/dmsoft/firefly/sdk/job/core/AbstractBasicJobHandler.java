package com.dmsoft.firefly.sdk.job.core;

/**
 * basic impl for basic job handler
 *
 * @author Can Guan, Garen Pang
 */
public abstract class AbstractBasicJobHandler implements JobHandler {
    private Double weight = 100.0;
    private String name;

    /**
     * constructor
     */
    public AbstractBasicJobHandler() {
    }

    /**
     * constructor
     * @param name
     */
    public AbstractBasicJobHandler(String name) {
        this.name = name;
    }

    @Override
    public Double getWeight() {
        return weight;
    }

    @Override
    public JobHandler setWeight(Double weight) {
        this.weight = weight;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public JobHandler setName(String name) {
        this.name = name;
        return this;
    }
}
