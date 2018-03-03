/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.job;

import com.dmsoft.bamboo.common.monitor.ProcessMonitorListener;
import com.dmsoft.firefly.sdk.job.core.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by Garen.Pang on 2018/2/2.
 */
public class DefaultJobManager implements JobManager {

    private Map<String, InitJobPipeline> jobMap = Maps.newConcurrentMap();
    private Map<String, List<JobEventListener>> jobEvent = Maps.newConcurrentMap();
    private Logger logger = LoggerFactory.getLogger(DefaultJobManager.class);
//    private ExecutorService service = new JobThreadPoolExecutor(5, 5,
//                                      0L, TimeUnit.MILLISECONDS,
//                                      new LinkedBlockingQueue<Runnable>(), new DefaultThreadFactory());

    @Override
    public synchronized void initializeJob(String jobName, InitJobPipeline pipeline) {
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
    public synchronized void addJobEventListenerByName(String jobName, JobEventListener listener) {
        if (jobEvent.containsKey(jobName)) {
            List<JobEventListener> listenerList = jobEvent.get(jobName);
            listenerList.add(listener);
        } else {
            List<JobEventListener> listenerList = Lists.newArrayList();
            listenerList.add(listener);
            jobEvent.put(jobName, listenerList);
        }
    }

    @Override
    public synchronized void removeJobEventListener(String jobName, JobEventListener listener) {
        if (jobEvent.containsKey(jobName)) {
            List<JobEventListener> listenerList = jobEvent.get(jobName);
            listenerList.remove(listener);
        }
    }

    @Override
    public void addJobProcessListener(String jobName, ProcessMonitorListener listener) {

    }

    @Override
    public void removeJobProcessListener(String jobName, ProcessMonitorListener listener) {

    }

    @Override
    public synchronized Object doJobSyn(Job job, long timeout, TimeUnit unit, Object... object) {
        if (StringUtils.isBlank(job.getJobName())) {
            logger.error("jobName is empty.");
            return null;
        }
        if (!jobMap.containsKey(job.getJobName())) {
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
        doJobASyn(job, complete, object);
        try {
            countDownLatch.await(timeout, unit);
        } catch (InterruptedException e) {

        }
        return returnFinal.get(0);

    }

    @Override
    public synchronized Object doJobSyn(Job job, Object... object) {
        if (StringUtils.isBlank(job.getJobName())) {
            logger.error("jobName is empty.");
            return null;
        }
        if (!jobMap.containsKey(job.getJobName())) {
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
        doJobASyn(job, complete, object);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {

        }
        return returnFinal.get(0);

    }

    @Override
    public synchronized void doJobASyn(Job job, JobDoComplete complete, Object... object) {
        if (StringUtils.isBlank(job.getJobName())) {
            logger.error("jobName is empty.");
            return;
        }
        if (!jobMap.containsKey(job.getJobName())) {
            logger.error("jobName is not exist.");
            return;
        }
        JobThread thread = new JobThread(){

            @Override
            public void run() {
                DefaultJobPipeline defaultJobPipeline = new DefaultJobPipeline(complete, null, jobEvent.containsKey(job.getJobName()) ? jobEvent.get(job.getJobName()) : Lists.newArrayList(), job);
                jobMap.get(job.getJobName()).initJobPipeline(defaultJobPipeline);
                defaultJobPipeline.fireDoJob(object);
            }
        };
        thread.addProcessMonitorListener(job.getProcessMonitorListener());
        thread.start();
//        service.execute(thread);
    }

    @Override
    public void doJobASyn(Job job, Object... object) {
        if (StringUtils.isBlank(job.getJobName())) {
            logger.error("jobName is empty.");
            return;
        }
        if (!jobMap.containsKey(job.getJobName())) {
            logger.error("jobName is not exist.");
            return;
        }
        JobThread thread = new JobThread(){

            @Override
            public void run() {
                DefaultJobPipeline defaultJobPipeline = new DefaultJobPipeline(returnValue -> {
                    logger.info(job.getJobId() + " do complete.");
                }, null, jobEvent.containsKey(job.getJobName()) ? jobEvent.get(job.getJobName()) : Lists.newArrayList(), job);
                jobMap.get(job.getJobName()).initJobPipeline(defaultJobPipeline);
                defaultJobPipeline.fireDoJob(object);
            }
        };
        thread.addProcessMonitorListener(job.getProcessMonitorListener());
        thread.start();
//        service.execute(thread);
    }

    public ExecutorService getExecutorService() {
        return null;
    }


}
