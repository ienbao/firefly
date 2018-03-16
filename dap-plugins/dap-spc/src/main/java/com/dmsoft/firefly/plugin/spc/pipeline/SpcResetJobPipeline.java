/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.pipeline;

import com.dmsoft.firefly.plugin.spc.handler.GetSpcStatsResultHandler;
import com.dmsoft.firefly.plugin.spc.handler.ParamKeys;
import com.dmsoft.firefly.sdk.job.core.InitJobPipeline;
import com.dmsoft.firefly.sdk.job.core.JobPipeline;

/**
 * Created by Ethan.Yang on 2018/3/16.
 */
public class SpcResetJobPipeline implements InitJobPipeline {

    @Override
    public void initJobPipeline(JobPipeline pipeline) {
        pipeline.addLast(ParamKeys.SPC_STATS_RESULT_HANDLER, new GetSpcStatsResultHandler().setWeight(100));
    }
}