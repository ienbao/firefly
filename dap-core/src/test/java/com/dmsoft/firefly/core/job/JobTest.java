/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.core.job;

import com.dmsoft.firefly.core.job.handler.JobInboundHandler1;
import com.dmsoft.firefly.core.job.handler.JobInboundHandler2;
import com.dmsoft.firefly.core.job.handler.JobInboundHandler3;
import com.dmsoft.firefly.sdk.job.InitJobPipeline;
import com.dmsoft.firefly.sdk.job.JobDoComplete;
import com.dmsoft.firefly.sdk.job.JobManager;
import com.dmsoft.firefly.sdk.job.JobPipeline;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by Garen.Pang on 2018/2/2.
 */
public class JobTest {

    public static void main(String[] args) {

        JobManager jobManager = new DefaultJobManager();
        jobManager.createJob("test1", new InitJobPipelineAdapter() {
            @Override
            public JobPipeline initJobPipeline(JobDoComplete complete) {
                DefaultJobPipeline pipeline = new DefaultJobPipeline(complete, jobManager.getService());
                pipeline.addLast("test1", new JobInboundHandler1());
                pipeline.addLast("test2", new JobInboundHandler2());
                pipeline.addLast("test3", new JobInboundHandler3());
                return pipeline;
            }
        });
        Object o = jobManager.doJobSyn("test1", "begin");
        if (o == null) {
            System.out.println("null");
        } else {
            System.out.println(o.toString());
        }

        jobManager.doJobASyn("test1", "begin", new JobDoComplete() {
            @Override
            public void doComplete(Object returnValue) {
                System.out.println("ASyn result = " + (returnValue == null ? "null" : returnValue));
            }
        });

    }
}
