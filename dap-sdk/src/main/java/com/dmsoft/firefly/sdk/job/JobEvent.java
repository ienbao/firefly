/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.job;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

/**
 * Created by Garen.Pang on 2018/2/25.
 */
public class JobEvent extends AbstractValueObject {

    private String jobId;
    private Object object;

    public JobEvent(String jobId, Object object) {
        this.jobId = jobId;
        this.object = object;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
