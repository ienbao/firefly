package com.dmsoft.firefly.core.job;

import com.dmsoft.firefly.sdk.job.Handler;
import com.dmsoft.firefly.sdk.job.JobManager;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Lucien.Chen on 2018/1/30.
 */
public class JobManagerImpl implements JobManager {
    private static Logger logger = LoggerFactory.getLogger(JobManagerImpl.class);

    List<Job> jobList = Lists.newArrayList();


    @Override
    public void createJob(String name) {
        Job job = new Job(name);
        jobList.add(job);
    }

    @Override
    public void doJob(String jobName,Object o) {
        Job job = findJob(jobName);
        if (job == null) {
            logger.debug("Don't find the job");
        } else {
            job.startPipeInThread(o);
        }
    }

    @Override
    public void registerHandler(String jobName, Handler handler) {
        Job job = findJob(jobName);
        if (job == null) {
            logger.debug("Don't find the job");
        } else {
            job.getPipe().add(handler);
        }

    }

    @Override
    public void registerHandlerFirst(String jobName, Handler handler) {
        Job job = findJob(jobName);
        if (job == null) {
            logger.debug("Don't find the job");
        } else {
            job.getPipe().addFirst(handler);
        }

    }

    @Override
    public void registerHandlerNextIndex(String jobName, int index, Handler handler) {
        Job job = findJob(jobName);
        if (job == null) {
            logger.debug("Don't find the job");
        } else {
            job.getPipe().addNext(index,handler);
        }

    }


    private Job findJob(String jobName) {
        if (jobName == null || jobName.isEmpty()) {
            return null;
        }
        Job result = null;
        for (Job job : jobList) {
            if (jobName.equals(job.name)) {
                result = job;
            }
        }
        return result;
    }
}
