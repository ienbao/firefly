package com.dmsoft.firefly.sdk.plugin;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * plugin class for caching plugin class
 */
public class PluginClass {
    private PluginClassType type;
    private String pluginId;
    private String className;
    private Object instance;
    private Set<Method> methodSet;

    public PluginClassType getType() {
        return type;
    }

    public void setType(PluginClassType type) {
        this.type = type;
    }

    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public Set<Method> getMethodSet() {
        return methodSet;
    }

    public void setMethodSet(Set<Method> methodSet) {
        this.methodSet = methodSet;
    }

    /**
     * method to get instance and revert into target class
     *
     * @param clazz class
     * @param <T>   class type
     * @return instance
     */
    public <T> T getInstance(Class<T> clazz) {
        return (T) instance;
    }
}
