/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.job;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;
import com.dmsoft.bamboo.common.monitor.ProcessMonitorListener;
import com.dmsoft.firefly.sdk.job.core.JobEventListener;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.UUID;

/**
 * Created by Garen.Pang on 2018/2/27.
 */
public class Job extends AbstractValueObject {

    private String jobId;
    private String jobName;

    private List<JobEventListener> jobEventListeners = Lists.newArrayList();
    private ProcessMonitorListener processMonitorListener;

    public Job(String jobName) {
        jobId = UUID.randomUUID().toString();
        this.jobName = jobName;
    }

    public String getJobId() {
        return jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public List<JobEventListener> getJobEventListeners() {
        return jobEventListeners;
    }

    public void addJobEventListener(JobEventListener jobEventListener) {
        this.jobEventListeners.add(jobEventListener);
    }

    public ProcessMonitorListener getProcessMonitorListener() {
        return processMonitorListener;
    }

    public void addProcessMonitorListener(ProcessMonitorListener processMonitorListener) {
        this.processMonitorListener = processMonitorListener;
    }
}
