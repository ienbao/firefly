/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.plugin;

import java.lang.reflect.Method;
import java.util.List;

/**
 * plugin image context
 *
 * @author Li Guang, Can Guan
 */
public interface PluginImageContext {
    /**
     * method to register plugin.
     *
     * @param pluginId plugin id
     */
    void registerPlugin(String pluginId);

    /**
     * method to register plugin.
     *
     * @param pluginIdList plugin id list
     */
    void registerPlugin(List<String> pluginIdList);

    /**
     * method to unregister plugin.
     *
     * @param pluginId plugin id
     */
    void unregisterPlugin(String pluginId);

    /**
     * method to unregister plugin.
     *
     * @param pluginIdList plugin id list
     */
    void unregisterPlugin(List<String> pluginIdList);

    /**
     * method to register plugin instance
     *
     * @param pluginId  plugin id
     * @param className class name
     * @param o         instance
     */
    void registerPluginInstance(String pluginId, String className, Object o);

    /**
     * method to get plugin instance
     *
     * @param pluginId  plugin id
     * @param className class name
     * @return class instance
     */
    Object getPluginInstance(String pluginId, String className);

    /**
     * method to get register api method
     *
     * @param pluginId   plugin id
     * @param className  class name
     * @param methodName method name
     * @return list of methods
     */
    List<Method> getRegisterAPI(String pluginId, String className, String methodName);

    /**
     * method to get plugin class by type
     *
     * @param type plugin class type
     * @return list of plugin class
     */
    List<PluginClass> getPluginClassByType(PluginClassType type);
}
