/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.plugin;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * DAP ClassLoader is data analysis platform class loader which will load class from all plugin class loader
 *
 * @author Can Guan
 */
public class DAPClassLoader extends ClassLoader {
    // class loader map
    private List<PluginClassLoader> pluginClassLoaders;

    /**
     * constructor
     *
     * @param parent             parent class loader
     * @param pluginClassLoaders plugin class loader collection
     */
    public DAPClassLoader(ClassLoader parent, List<PluginClassLoader> pluginClassLoaders) {
        super(parent);
        this.pluginClassLoaders = pluginClassLoaders;
        if (this.pluginClassLoaders == null) {
            this.pluginClassLoaders = new ArrayList<>();
        }
    }


    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        for (PluginClassLoader pcl : pluginClassLoaders) {
            try {
                return pcl.loadClass(name);
            } catch (ClassNotFoundException e) {
            }
        }
        throw new ClassNotFoundException(name);
    }

    public List<PluginClassLoader> getPluginClassLoaders() {
        return pluginClassLoaders;
    }

    @Override
    protected URL findResource(String name) {
        for (PluginClassLoader pcl : pluginClassLoaders) {
            URL url = pcl.findResource(name);
            if (url != null) {
                return pcl.findResource(name);
            }
        }
        return super.findResource(name);
    }

    @Override
    protected Enumeration<URL> findResources(String name) throws IOException {
        List<URL> urlList = Collections.list(super.findResources(name));
        for (PluginClassLoader pcl : pluginClassLoaders) {
            urlList.addAll(Collections.list(pcl.findResources(name)));
        }
        return Collections.enumeration(urlList);
    }
}
