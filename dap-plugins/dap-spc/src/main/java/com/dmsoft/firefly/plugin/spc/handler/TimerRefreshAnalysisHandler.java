/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.handler;

import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.job.core.AbstractBasicJobHandler;
import com.dmsoft.firefly.sdk.job.core.JobContext;


/**
 * Created by Ethan.Yang on 2018/4/4.
 */
public class TimerRefreshAnalysisHandler extends AbstractBasicJobHandler {

    /**
     * constructor
     */
    public TimerRefreshAnalysisHandler() {
        setName(ParamKeys.TIMER_REFRESH_ANALYSIS_HANDLER);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void doJob(JobContext context) {
        SearchDataFrame dataFrame = context.getParam(ParamKeys.SEARCH_DATA_FRAME, SearchDataFrame.class);
        context.put(ParamKeys.STATISTICAL_SEARCH_DATA_FRAME, dataFrame);
        context.put(ParamKeys.CHART_SEARCH_DATA_FRAME, dataFrame);
    }
}
