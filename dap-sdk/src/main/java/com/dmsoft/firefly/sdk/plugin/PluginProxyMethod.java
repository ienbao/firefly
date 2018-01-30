package com.dmsoft.firefly.sdk.plugin;

/**
 * Proxy method for plugin open api
 *
 * @author Li Guang, Can Guan
 */
public interface PluginProxyMethod {

    /**
     * do some thing
     *
     * @param resultClass class to return
     * @param args        arguments
     * @param <T>         class
     * @return result class
     */
    <T> T doSomething(Class<T> resultClass, Object... args);
}
