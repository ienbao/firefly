package com.dmsoft.firefly.sdk.plugin;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Created by GuangLi on 2018/1/23.
 */
public class PluginImage {
    //plugin id
    private String pluginId;
    //all class in this map(key: class name, value: class instance)
    private Map<String, Object> pluginInstanceMap;
    //all open api in this map(key: classname + '!@#' + methodName, key : method)
    private Map<String, List<Method>> methodMap;

    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    public Map<String, Object> getPluginInstanceMap() {
        return pluginInstanceMap;
    }

    public void setPluginInstanceMap(Map<String, Object> pluginInstanceMap) {
        this.pluginInstanceMap = pluginInstanceMap;
    }

    public Map<String, List<Method>> getMethodMap() {
        return methodMap;
    }

    public void setMethodMap(Map<String, List<Method>> methodMap) {
        this.methodMap = methodMap;
    }
}
