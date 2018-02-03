package com.dmsoft.firefly.core.job;

import com.dmsoft.firefly.sdk.job.InitJobPipeline;
import com.dmsoft.firefly.sdk.job.JobDoComplete;
import com.dmsoft.firefly.sdk.job.JobManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * Created by Garen.Pang on 2018/2/2.
 */
public class DefaultJobManager implements JobManager {

    private Map<String, InitJobPipeline> jobMap = Maps.newConcurrentMap();
    private Logger logger = LoggerFactory.getLogger(DefaultJobManager.class);
    private ExecutorService service = Executors.newFixedThreadPool(5);

    @Override
    public void createJob(String jobName, InitJobPipeline pipeline) {
        if (StringUtils.isBlank(jobName)) {
            logger.error("jobName is empty.");
            return;
        }
        if (jobMap.containsKey(jobName)) {
            logger.error("jobName is exist.");
            return;
        }
        jobMap.put(jobName, pipeline);
    }

    @Override
    public Object doJobSyn(String jobName, Object object, long timeout, TimeUnit unit) {
        if (StringUtils.isBlank(jobName)) {
            logger.error("jobName is empty.");
            return null;
        }
        if (!jobMap.containsKey(jobName)) {
            logger.error("jobName is not exist.");
            return null;
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        final List returnFinal = Lists.newArrayList();
        JobDoComplete complete = new JobDoComplete() {
            @Override
            public void doComplete(Object returnValue) {
                returnFinal.add(returnValue);
                countDownLatch.countDown();
            }
        };
        doJobASyn(jobName, object, complete);
        try {
            countDownLatch.await(timeout, unit);
        } catch (InterruptedException e) {

        }
        return returnFinal.get(0);

    }

    @Override
    public Object doJobSyn(String jobName, Object object) {
        if (StringUtils.isBlank(jobName)) {
            logger.error("jobName is empty.");
            return null;
        }
        if (!jobMap.containsKey(jobName)) {
            logger.error("jobName is not exist.");
            return null;
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        final List returnFinal = Lists.newArrayList();
        JobDoComplete complete = new JobDoComplete() {
            @Override
            public void doComplete(Object returnValue) {
                returnFinal.add(returnValue);
                countDownLatch.countDown();
            }
        };
        doJobASyn(jobName, object, complete);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {

        }
        return returnFinal.get(0);

    }

    @Override
    public void doJobASyn(String jobName, Object object, JobDoComplete complete) {
        if (StringUtils.isBlank(jobName)) {
            logger.error("jobName is empty.");
            return;
        }
        if (!jobMap.containsKey(jobName)) {
            logger.error("jobName is not exist.");
            return;
        }
        service.execute(new Runnable() {
            @Override
            public void run() {
                DefaultJobPipeline defaultJobPipeline = new DefaultJobPipeline(complete, service);
                jobMap.get(jobName).initJobPipeline(defaultJobPipeline);
                defaultJobPipeline.fireDoJob(object);
            }
        });

    }

    public ExecutorService getExecutorService() {
        return service;
    }
}
