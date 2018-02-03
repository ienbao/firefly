/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.job;

/**
 * Created by Garen.Pang on 2018/2/2.
 */
public interface InitJobPipeline {

    /**
     * initJobPipeline
     *
     * @return JobPipeline
     */
    JobPipeline initJobPipeline();

    /**
     * initJobPipeline
     *
     * @param complete complete
     * @return JobPipeline
     */
    JobPipeline initJobPipeline(JobDoComplete complete);
}
