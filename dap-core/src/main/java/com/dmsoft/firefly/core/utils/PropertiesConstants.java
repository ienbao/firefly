/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.core.utils;

/**
 * constant class for properties file
 */
public class PropertiesConstants {
    /**
     * key for plugins path, value: e.g. "/User/DAP/Desktop/plugins"
     * default: class path
     */
    public static final String KEY_PLUGINS_PATH = "plugins.path";
    /**
     * key for plugins folder name, the files inside is the each plugin folder, value: e.g. "dapPlugins"
     */
    public static final String KEY_PLUGINS_FOLDER_NAME = "plugins.folder.name";
    /**
     * default value for plugins folder name.
     */
    public static final String VALUE_DEFAULT_PLUGINS_FOLDER_NAME = "plugins";

}
