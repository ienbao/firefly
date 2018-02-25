/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.job;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

/**
 * Created by Garen.Pang on 2018/2/25.
 */
public class JobEvent extends AbstractValueObject {

    private String eventId;
    private Object object;

    public JobEvent(String eventId, Object object) {
        this.eventId = eventId;
        this.object = object;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
