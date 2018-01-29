/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.plugin;


import com.dmsoft.firefly.sdk.utils.ArrayUtils;
import com.dmsoft.firefly.sdk.utils.FilePathUtils;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Plugin Class Loader extends ClassLoader to load the class from jars in plugin libs file.
 *
 * @author Can Guan
 */
public class PluginClassLoader extends URLClassLoader {
    private PluginInfo pluginInfo;

    /**
     * constructor
     *
     * @param urls       url array to find class
     * @param parent     parent class loader
     * @param pluginInfo plugin info
     */
    public PluginClassLoader(URL[] urls, ClassLoader parent, PluginInfo pluginInfo) {
        super(ArrayUtils.addAll(urls, parseToURL(pluginInfo)), parent);
        this.pluginInfo = pluginInfo;
    }

    /**
     * constructor
     *
     * @param urls       url array to find class
     * @param pluginInfo plugin info
     */
    public PluginClassLoader(URL[] urls, PluginInfo pluginInfo) {
        super(ArrayUtils.addAll(urls, parseToURL(pluginInfo)));
        this.pluginInfo = pluginInfo;
    }

    /**
     * constructor
     *
     * @param urls       url array to find class
     * @param parent     parent class loader
     * @param factory    url stream handler to find class
     * @param pluginInfo plugin info
     */
    public PluginClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory, PluginInfo pluginInfo) {
        super(ArrayUtils.addAll(urls, parseToURL(pluginInfo)), parent, factory);
        this.pluginInfo = pluginInfo;
    }

    /**
     * method to parse plugin info into url array
     *
     * @param pluginInfo plugin info
     * @return array of urls
     */
    public static URL[] parseToURL(PluginInfo pluginInfo) {
        String libPath = FilePathUtils.buildFilePath(pluginInfo.getFolderPath(), PluginConstants.FOLDER_NAME_LIB);
        List<URL> result = new ArrayList<>();
        File file = new File(libPath);
        File[] files = file.listFiles();
        if (null == files) {
            return result.toArray(new URL[0]);
        }
        for (File f : files) {
            if (f.isFile()) {
                try {
                    result.add(f.toURI().toURL());
                } catch (Exception e) {
                }
            }
        }
        return result.toArray(new URL[result.size()]);
    }

    public PluginInfo getPluginInfo() {
        return pluginInfo;
    }
}
