/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.core.job;

import com.dmsoft.firefly.sdk.job.InitJobPipeline;
import com.dmsoft.firefly.sdk.job.JobDoComplete;
import com.dmsoft.firefly.sdk.job.JobPipeline;

/**
 * Created by Garen.Pang on 2018/2/2.
 */
public abstract class InitJobPipelineAdapter implements InitJobPipeline {

    @Override
    public JobPipeline initJobPipeline() {
        return null;
    }

    @Override
    public JobPipeline initJobPipeline(JobDoComplete complete) {
        return null;
    }
}
