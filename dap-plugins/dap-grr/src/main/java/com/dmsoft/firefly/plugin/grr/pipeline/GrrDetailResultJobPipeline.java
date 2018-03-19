package com.dmsoft.firefly.plugin.grr.pipeline;

import com.dmsoft.firefly.plugin.grr.handler.*;
import com.dmsoft.firefly.sdk.job.core.InitJobPipeline;
import com.dmsoft.firefly.sdk.job.core.JobPipeline;

/**
 * Created by cherry on 2018/3/12.
 */
public class GrrDetailResultJobPipeline implements InitJobPipeline {

    @Override
    public void initJobPipeline(JobPipeline pipeline) {
        pipeline.addLast(ParamKeys.GRR_CONFIG_HANDLER, new GrrConfigHandler());
        pipeline.addLast(ParamKeys.GRR_DETAIL_RESULT_HANDLER, new DetailResultHandler1());
    }
}
