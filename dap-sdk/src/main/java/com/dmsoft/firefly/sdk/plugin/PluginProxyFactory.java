package com.dmsoft.firefly.sdk.plugin;

/**
 * Factory for proxying plugin open api
 *
 * @author Li Guang, Can Guan
 */
public interface PluginProxyFactory {

    /**
     * proxy method for target method name
     * runtime will find the methods with same method name, then will match the args in proxy method.
     *
     * @param pluginId   plugin id
     * @param className  class name
     * @param methodName method name
     * @return the plugin proxy method
     */
    PluginProxyMethod proxyMethod(String pluginId, String className, String methodName);
}
