package com.dmsoft.firefly.plugin.grr.pipeline;

import com.dmsoft.firefly.plugin.grr.handler.*;
import com.dmsoft.firefly.sdk.job.core.InitJobPipeline;
import com.dmsoft.firefly.sdk.job.core.JobPipeline;

/**
 * Created by julia on 2018/3/14.
 */
public class GrrViewDataJobPipeline implements InitJobPipeline {

    @Override
    public void initJobPipeline(JobPipeline pipeline) {
        pipeline.addLast(ParamKeys.FIND_TEST_DATA_HANDLER, new FindTestDataHandler());
        pipeline.addLast(ParamKeys.DATA_FRAME_HANDLER, new DataFrameHandler());
//        pipeline.addLast(ParamKeys.VALIDATE_PARAM_HANDLER, new VaidateParamHandler());
        pipeline.addLast(ParamKeys.GRR_CONFIG_HANDLER, new GrrConfigHandler());
        pipeline.addLast(ParamKeys.GRR_VIEW_DATA_RESULT_HANDLER, new ViewDataHandler());
        pipeline.addLast(ParamKeys.GRR_SUMMARY_RESULT_HANDLER, new SummaryHandler1());
        pipeline.addLast(ParamKeys.GRR_DETAIL_RESULT_HANDLER, new DetailResultHandler1());

    }
}
