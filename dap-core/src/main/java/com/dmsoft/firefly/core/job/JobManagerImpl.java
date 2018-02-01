package com.dmsoft.firefly.core.job;

import com.dmsoft.firefly.sdk.job.Handler;
import com.dmsoft.firefly.sdk.job.JobManager;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Lucien.Chen on 2018/1/30.
 */
public class JobManagerImpl implements JobManager {
    private static Logger logger = LoggerFactory.getLogger(JobManagerImpl.class);

    private List<Job> jobList;

    private ExecutorService pool;

    public JobManagerImpl() {
        jobList = Lists.newArrayList();
        //创建线程池
        pool = Executors.newFixedThreadPool(5);
    }

    @Override
    public void createJob(String name) {
        Job job = new Job(name);
        jobList.add(job);
    }

    @Override
    public Object doJob(String jobName, Object o) {
        Job job = findJob(jobName);
        if (job == null) {
            logger.debug("Don't find the job");
            return null;
        } else {//开启线程
            Callable c = new Callable() {
                @Override
                public Object call() throws Exception {
                    return job.getPipe().start(o);
                }

            };
            job.setCallable(c);
            Future future = pool.submit(c);
            try {
                return future.get();
            }catch (Exception e){
                return null;
            }
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
            job.getPipe().addNext(index, handler);
        }

    }


    private Job findJob(String jobName) {
        if (jobName == null || jobName.isEmpty()) {
            return null;
        }
        Job result = null;
        for (Job job : jobList) {
            if (jobName.equals(job.getName())) {
                result = job;
            }
        }
        return result;
    }
}
