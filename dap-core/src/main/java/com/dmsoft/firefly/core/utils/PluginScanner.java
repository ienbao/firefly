/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.core.utils;

import com.dmsoft.firefly.sdk.plugin.PluginInfo;
import com.google.common.collect.Lists;

import java.io.File;
import java.util.List;


/**
 * Plugin Scanner will scan the plugins by plugin path and register all the plugins in application.
 *
 * @author Can Guan
 */
public class PluginScanner {
    /**
     * method to scan plugins by path, and will register plugin in plugin context
     *
     * @param path folder path to scan, plugins root folder
     * @return List of plugin info
     */
    public static List<PluginInfo> scanPluginByPath(String path) {
        if (path == null) {
            return Lists.newArrayList();
        }
        File pathFile = new File(path);
        if (pathFile.isDirectory()) {
            File[] innerFiles = pathFile.listFiles();
            if (innerFiles == null) {
                return Lists.newArrayList();
            }
            List<String> pluginFolderUris = Lists.newArrayList();
            for (File file : innerFiles) {
                if (file.isDirectory()) {
                    pluginFolderUris.add(file.toString());
                }
            }
            return PluginXMLParser.parseXML(pluginFolderUris);
        }
        return Lists.newArrayList();
    }
}
