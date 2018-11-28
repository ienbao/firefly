package com.dmsoft.firefly.sdk.event;

/**
 * event for platform, used to notice user
 *
 * @author Can Guan
 */
public class PlatformEvent {
    private EventType eventType;
    private Object msg;

    /**
     * constructor
     *
     * @param eventType event type
     * @param msg   msg
     */
    public PlatformEvent(EventType eventType, Object msg) {
        this.eventType = eventType;
        this.msg = msg;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public EventType getEventType() {
        return eventType;
    }

    public Object getMsg() {
        return msg;
    }
}
