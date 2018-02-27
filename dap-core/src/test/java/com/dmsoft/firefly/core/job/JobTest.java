/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.core.job;

import com.dmsoft.firefly.core.job.handler.*;
import com.dmsoft.firefly.sdk.job.*;
import com.dmsoft.firefly.sdk.job.core.JobDoComplete;
import com.dmsoft.firefly.sdk.job.core.JobManager;

/**
 * Created by Garen.Pang on 2018/2/2.
 */
public class JobTest {

    public static void main(String[] args) {

        JobManager jobManager = new DefaultJobManager();
        jobManager.initializeJob("test1", (pipeline) -> {

            //注意 ： 1.Inbound是add的顺序执行 2.Outbound 是add逆序执行
            //3.在handler中执行returnValue后，后面的handler都不会执行，包括outbound 和 inbound
            pipeline.addLast("test4", new JobOutboundHandler1());
            pipeline.addLast("test5", new JobOutboundHandler2());
            pipeline.addLast("test1", new JobInboundHandler1());
            pipeline.addLast("test2", new JobInboundHandler2());
            pipeline.addLast("test3", new JobInboundHandler3());
        });

        jobManager.addJobEventListenerByName("test1", event -> {
            System.out.println(event.getJobId() + " " + event.getObject().toString());
        });

        Job job = new Job("test1");
        System.out.println(job.getJobId());
        Object o = jobManager.doJobSyn(job, "begin");
        if (o == null) {
            System.out.println("null");
        } else {
            System.out.println(o.toString());
        }


        jobManager.doJobASyn(job, "begin", new JobDoComplete() {
            @Override
            public void doComplete(Object returnValue) {
                System.out.println("ASyn result = " + (returnValue == null ? "null" : returnValue));
            }
        });

    }
}
