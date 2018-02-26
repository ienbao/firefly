package com.dmsoft.firefly.core.sdkimpl.plugin;

import com.dmsoft.firefly.core.utils.ClassUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.plugin.PluginContext;
import com.dmsoft.firefly.sdk.plugin.PluginImageContext;
import com.dmsoft.firefly.sdk.plugin.PluginProxyMethod;
import com.dmsoft.firefly.sdk.plugin.PluginProxyMethodFactory;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Impl class for plugin proxy method factory
 *
 * @author Li Guang, Can Guan
 */
public class PluginProxyMethodFactoryImpl implements PluginProxyMethodFactory {
    @Override
    public PluginProxyMethod proxyMethod(String pluginId, String className, String methodName) {
        PluginImageContext pluginImageContext = RuntimeContext.getBean(PluginImageContext.class);
        Object o = pluginImageContext.getPluginInstance(pluginId, className);
        List<Method> methodList = pluginImageContext.getRegisterAPI(pluginId, className, methodName);
        if (methodList == null || methodList.isEmpty()) {
            return null;
        }
        return new PluginProxyMethod() {
            @Override
            public <T> T doSomething(Class<T> resultClass, Object... args) {
                Method foundMethod = null;
                for (Method m : methodList) {
                    if (ClassUtils.isParamTypeEqual(args, m.getParameterTypes())) {
                        foundMethod = m;
                    }
                }
                if (foundMethod == null) {
                    return null;
                }
                ClassLoader previousCL = Thread.currentThread().getContextClassLoader();
                PluginContext context = RuntimeContext.getBean(PluginContext.class);
                Thread.currentThread().setContextClassLoader(context.getDAPClassLoader(pluginId));
                Object result = null;
                try {
                    result = foundMethod.invoke(o, args);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                    Thread.currentThread().setContextClassLoader(previousCL);
                }
                return (T) result;
            }
        };
    }
}
