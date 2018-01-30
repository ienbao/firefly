package com.dmsoft.firefly.sdk.plugin;

import java.util.Map;

/**
 * Created by GuangLi on 2018/1/23.
 */
public class PluginImage {
    //plugin id
    private String pluginId;
    private Map<String, PluginClass> pluginClassMap;

    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    public Map<String, PluginClass> getPluginClassMap() {
        return pluginClassMap;
    }

    public void setPluginClassMap(Map<String, PluginClass> pluginClassMap) {
        this.pluginClassMap = pluginClassMap;
    }
}
