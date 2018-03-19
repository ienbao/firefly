package com.dmsoft.firefly.plugin.grr.pipeline;

import com.dmsoft.firefly.plugin.grr.handler.GrrConfigHandler;
import com.dmsoft.firefly.plugin.grr.handler.ParamKeys;
import com.dmsoft.firefly.plugin.grr.handler.RefreshHandler;
import com.dmsoft.firefly.sdk.job.core.InitJobPipeline;
import com.dmsoft.firefly.sdk.job.core.JobPipeline;

/**
 * Created by cherry on 2018/3/18.
 */
public class GrrRefreshJobPipeline implements InitJobPipeline {

    @Override
    public void initJobPipeline(JobPipeline pipeline) {
        pipeline.addLast(ParamKeys.GRR_CONFIG_HANDLER, new GrrConfigHandler());
        pipeline.addLast(ParamKeys.GRR_REFRESH_HANDLER, new RefreshHandler());
    }
}
