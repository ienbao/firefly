package com.dmsoft.firefly.sdk.event;

import com.dmsoft.firefly.sdk.utils.enums.EventType;

/**
 * event for platform, used to notice user
 *
 * @author Can Guan
 */
public class PlatformEvent {
    private EventType eventType;
    private String message;

    /**
     * constructor
     *
     * @param eventType event type
     * @param message   message
     */
    public PlatformEvent(EventType eventType, String message) {
        this.eventType = eventType;
        this.message = message;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}