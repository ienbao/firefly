/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.core.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Properties;

/**
 * properties utils class for parsing basic info from application.properties
 */
public class PropertiesUtils {
    /**
     * method to get plugins path from properties
     *
     * @param props properties
     * @return plugin path string
     */
    public static String getPluginsPath(Properties props) {
        String pluginsPath = props.getProperty(PropertiesConstants.KEY_PLUGINS_PATH);
        if (StringUtils.isNoneBlank(pluginsPath)) {
            return pluginsPath;
        } else {
            String pluginFolderName = props.getProperty(PropertiesConstants.KEY_PLUGINS_FOLDER_NAME, PropertiesConstants.VALUE_DEFAULT_PLUGINS_FOLDER_NAME);
            return ApplicationPathUtil.getPath(pluginFolderName);
        }
    }
}
