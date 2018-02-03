/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.core.job;

import com.dmsoft.firefly.core.job.handler.*;
import com.dmsoft.firefly.sdk.job.JobDoComplete;
import com.dmsoft.firefly.sdk.job.JobManager;
import com.dmsoft.firefly.sdk.job.JobPipeline;

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

                //注意 ： 1.Inbound是add的顺序执行 2.Outbound 是add逆序执行
                //3.在handler中执行returnValue后，后面的handler都不会执行，包括outbound 和 inbound
                DefaultJobPipeline pipeline = new DefaultJobPipeline(complete, jobManager.getExecutorService());
                pipeline.addLast("test4", new JobOutboundHandler1());
                pipeline.addLast("test5", new JobOutboundHandler2());
                pipeline.addLast("test1", new JobInboundHandler1());
                pipeline.addLast("test2", new JobInboundHandler2());
                pipeline.addLast("test3", new JobInboundHandler3());
                return pipeline;
            }
        });

//        for (int i = 0; i < 100; i++) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
                    Object o = jobManager.doJobSyn("test1", "begin");
                    if (o == null) {
                        System.out.println("null");
                    } else {
                        System.out.println(o.toString());
//                    }
//                }
//            }).start();
        }
//        try {
//            Thread.sleep(Integer.MAX_VALUE);
//        } catch (InterruptedException e) {
//
//        }


        jobManager.doJobASyn("test1", "begin", new JobDoComplete() {
            @Override
            public void doComplete(Object returnValue) {
                System.out.println("ASyn result = " + (returnValue == null ? "null" : returnValue));
            }
        });

    }
}
