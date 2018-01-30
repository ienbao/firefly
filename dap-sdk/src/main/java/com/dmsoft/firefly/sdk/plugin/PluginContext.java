/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.plugin;

import java.util.List;
import java.util.Map;

/**
 * interface for plugin path context.
 *
 * @author Can Guan
 */
public interface PluginContext {

    /**
     * method register plugin info.
     *
     * @param pluginInfo plugin info
     */
    void installPlugin(PluginInfo pluginInfo);

    /**
     * method to register plugin info.
     *
     * @param pluginInfoList plugin info list
     */
    void installPlugin(List<PluginInfo> pluginInfoList);

    /**
     * method to uninstall plugin
     *
     * @param pluginId plugin id
     */
    void uninstallPlugin(String pluginId);

    /**
     * method to uninstall plugin id
     *
     * @param pluginIdList plugin id list
     */
    void uninstallPlugin(List<String> pluginIdList);

    /**
     * method to get register plugin info (excluded invalid plugin info).
     *
     * @param pluginInfoId plugin info id
     * @return plugin info
     */
    PluginInfo getEnabledPluginInfo(String pluginInfoId);

    /**
     * method to get register plugin info (excluded invalid plugin info).
     *
     * @param pluginIdList plugin info id list
     * @return map of plugin info, key : plugin info id
     */
    Map<String, PluginInfo> getEnabledPluginInfo(List<String> pluginIdList);

    /**
     * method to get all enabled plugin info.
     *
     * @return plugin info map, key : plugin info id
     */
    Map<String, PluginInfo> getAllEnabledPluginInfo();

    /**
     * method to get all register plugin info (excluded invalid plugin info).
     *
     * @return plugin info map, key : plugin info id
     */
    Map<String, PluginInfo> getAllInstalledPluginInfo();

    /**
     * method to enable plugin.
     *
     * @param pluginIdList plugin Id List
     */
    void enablePlugin(List<String> pluginIdList);

    /**
     * method to enable plugin.
     *
     * @param pluginId plugin Id
     */
    void enablePlugin(String pluginId);

    /**
     * method to disable plugin.
     *
     * @param pluginIdList plugin Id Listl
     */
    void disablePlugin(List<String> pluginIdList);

    /**
     * method to unregister plugin info.
     *
     * @param pluginId plugin id
     */
    void disablePlugin(String pluginId);

    /**
     * method to get dap class loader for required plugin info list.
     *
     * @param pluginIdList plugin id list
     * @return dap class loader
     */
    DAPClassLoader getDAPClassLoader(List<String> pluginIdList);

    /**
     * method to get dap class loader for required plugin info id
     *
     * @param pluginId plugin info id
     * @return dap class loader
     */
    DAPClassLoader getDAPClassLoader(String pluginId);

    /**
     * method to add listener for plugin context
     *
     * @param listener listener
     */
    void addListener(PluginContextListener listener);

    /**
     * method to remove listener for plugin context
     *
     * @param listener listener
     */
    void removeListener(PluginContextListener listener);

    /**
     * method to start plugin
     *
     * @param pluginId plugin Id
     */
    void startPlugin(String pluginId);

    /**
     * method to start plugin
     *
     * @param pluginIdList plugin Id List
     */
    void startPlugin(List<String> pluginIdList);
}
