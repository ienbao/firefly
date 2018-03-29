package com.dmsoft.firefly.sdk.job.core;

/**
 * job event
 *
 * @author Can Guan, Garen Pang
 */
public class JobEvent {
    private String eventName;
    private Double progress;
    private Object eventObject;

    /**
     * constructor
     *
     * @param eventName   event name
     * @param progress    progress
     * @param eventObject event object
     */
    public JobEvent(String eventName, Double progress, Object eventObject) {
        this.eventName = eventName;
        this.progress = progress;
        this.eventObject = eventObject;
    }

    /**
     * constructor
     */
    public JobEvent() {
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Double getProgress() {
        return progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
    }

    public Object getEventObject() {
        return eventObject;
    }

    public void setEventObject(Object eventObject) {
        this.eventObject = eventObject;
    }
}
