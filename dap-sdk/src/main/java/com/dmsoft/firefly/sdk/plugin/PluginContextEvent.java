/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.plugin;

import java.util.List;

/**
 * Plugin Context Event
 */
public class PluginContextEvent {
    private EventType eventType;
    private List<String> pluginInfoIdList;
    /**
     * constructor
     *
     * @param eventType        event type
     * @param pluginInfoIdList plugin info list
     */
    public PluginContextEvent(EventType eventType, List<String> pluginInfoIdList) {
        this.eventType = eventType;
        this.pluginInfoIdList = pluginInfoIdList;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public List<String> getPluginInfoIdList() {
        return pluginInfoIdList;
    }

    public void setPluginInfoIdList(List<String> pluginInfoIdList) {
        this.pluginInfoIdList = pluginInfoIdList;
    }

    /**
     * enum class for plugin context event type
     */
    public enum EventType {
        INSTALL, UNINSTALL, ENABLE, DISABLE
    }
}
