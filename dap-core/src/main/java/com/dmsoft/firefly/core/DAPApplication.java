/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.core;

import com.dmsoft.firefly.core.plugin.PluginContextImpl;
import com.dmsoft.firefly.core.plugin.PluginImageContextImpl;
import com.dmsoft.firefly.core.plugin.PluginProxyMethodFactoryImpl;
import com.dmsoft.firefly.core.utils.ApplicationPathUtil;
import com.dmsoft.firefly.core.utils.PluginScanner;
import com.dmsoft.firefly.core.utils.PropertiesUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.plugin.*;
import com.dmsoft.firefly.sdk.utils.enums.InitModel;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * DAP Application starter include basic flows.
 *
 * @author Can Guan
 */
public class DAPApplication {
    static Logger logger = LoggerFactory.getLogger(DAPApplication.class);
    /**
     * method to start application
     *
     * @param activePlugins plugins to be excluded
     * @return plugin context
     */
    public static PluginContextImpl run(List<String> activePlugins) {
        logger.info("run.");
        PluginContextImpl pluginInfoContextImpl = new PluginContextImpl(InitModel.INIT_WITH_UI);
        PluginImageContextImpl pluginImageContext = new PluginImageContextImpl();
        pluginInfoContextImpl.addListener(pluginImageContext);
        PluginProxyMethodFactoryImpl pluginProxy = new PluginProxyMethodFactoryImpl();
        RuntimeContext.registerBean(PluginContext.class, pluginInfoContextImpl);
        RuntimeContext.registerBean(PluginImageContext.class, pluginImageContext);
        RuntimeContext.registerBean(PluginProxyMethodFactory.class, pluginProxy);
        String propertiesURL = ApplicationPathUtil.getPath("resources", "application.properties");
        InputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(propertiesURL.toString()));
            Properties properties = new Properties();
            properties.load(inputStream);
            String pluginFolderPath = PropertiesUtils.getPluginsPath(properties);
            List<PluginInfo> scannedPlugins = PluginScanner.scanPluginByPath(pluginFolderPath);
            pluginInfoContextImpl.installPlugin(scannedPlugins);
            pluginInfoContextImpl.enablePlugin(activePlugins);
            DAPClassLoader loader = pluginInfoContextImpl.getDAPClassLoader("com.dmsoft.dap.SpcPlugin");
            Class c = loader.loadClass("com.dmsoft.firefly.plugin.spc.SpcService");
            System.out.println(c);
            PluginProxyMethod method = pluginProxy.proxyMethod("com.dmsoft.dap.SpcPlugin", "com.dmsoft.firefly.plugin.spc.SpcService", "say");
            method.doSomething(null, "AA");
            System.out.println("SADF");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return pluginInfoContextImpl;
    }

    /**
     * main method
     *
     * @param args arguments
     */
    public static void main(String[] args) {
        run(Lists.newArrayList("com.dmsoft.dap.SpcPlugin"));
    }
}
