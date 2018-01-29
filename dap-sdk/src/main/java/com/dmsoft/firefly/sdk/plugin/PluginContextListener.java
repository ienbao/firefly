/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.plugin;

/**
 * Plugin context listener interface
 */
public interface PluginContextListener {
    /**
     * event change api for listener
     *
     * @param event event
     */
    void contextChange(PluginContextEvent event);
}
