/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.core;

import com.dmsoft.firefly.core.dai.*;
import com.dmsoft.firefly.core.job.DefaultJobManager;
import com.dmsoft.firefly.core.sdkimpl.PluginContextImpl;
import com.dmsoft.firefly.core.sdkimpl.PluginImageContextImpl;
import com.dmsoft.firefly.core.sdkimpl.PluginProxyMethodFactoryImpl;
import com.dmsoft.firefly.core.sdkimpl.PluginUIContextImpl;
import com.dmsoft.firefly.core.utils.ApplicationPathUtil;
import com.dmsoft.firefly.core.utils.PluginScanner;
import com.dmsoft.firefly.core.utils.PropertiesUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.service.*;
import com.dmsoft.firefly.sdk.job.JobManager;
import com.dmsoft.firefly.sdk.plugin.*;
import com.dmsoft.firefly.sdk.ui.PluginUIContext;
import com.dmsoft.firefly.sdk.utils.enums.InitModel;
import com.google.common.collect.Lists;

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
    /**
     * method to start application
     *
     * @param activePlugins plugins to be excluded
     * @return plugin context
     */
    public static PluginContextImpl run(List<String> activePlugins) {
        // prepare env start
        PluginContextImpl pluginInfoContextImpl = new PluginContextImpl(InitModel.INIT_WITH_UI);
        PluginImageContextImpl pluginImageContext = new PluginImageContextImpl();
        pluginInfoContextImpl.addListener(pluginImageContext);
        PluginProxyMethodFactoryImpl pluginProxy = new PluginProxyMethodFactoryImpl();
        PluginUIContextImpl pluginUIContext = new PluginUIContextImpl();
        DefaultJobManager jobManager = new DefaultJobManager();
        SourceDataServiceImpl sourceDataService = new SourceDataServiceImpl();
        TemplateServiceImpl templateService = new TemplateServiceImpl();
        UserPreferenceServiceImpl userPreferenceService = new UserPreferenceServiceImpl();
        UserServiceImpl userService = new UserServiceImpl();
        EnvServiceImpl envService = new EnvServiceImpl();

        RuntimeContext.registerBean(PluginContext.class, pluginInfoContextImpl);
        RuntimeContext.registerBean(PluginImageContext.class, pluginImageContext);
        RuntimeContext.registerBean(PluginProxyMethodFactory.class, pluginProxy);
        RuntimeContext.registerBean(PluginUIContext.class, pluginUIContext);
        RuntimeContext.registerBean(JobManager.class, jobManager);
        RuntimeContext.registerBean(SourceDataService.class, sourceDataService);
        RuntimeContext.registerBean(TemplateService.class, templateService);
        RuntimeContext.registerBean(UserPreferenceService.class, userPreferenceService);
        RuntimeContext.registerBean(UserService.class, userService);
        RuntimeContext.registerBean(EnvService.class, envService);


        // prepare env done
        String propertiesURL = ApplicationPathUtil.getPath("resources", "application.properties");
        InputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(propertiesURL.toString()));
            Properties properties = new Properties();
            properties.load(inputStream);
            String pluginFolderPath = PropertiesUtils.getPluginsPath(properties);
            List<PluginInfo> scannedPlugins = PluginScanner.scanPluginByPath(pluginFolderPath);
            RuntimeContext.getBean(PluginContext.class).installPlugin(scannedPlugins);
            RuntimeContext.getBean(PluginContext.class).enablePlugin(activePlugins);
//            DAPClassLoader loader = RuntimeContext.getBean(PluginContext.class).getDAPClassLoader("com.dmsoft.dap.SpcPlugin");
//            Class c = loader.loadClass("com.dmsoft.firefly.plugin.spc.SpcService");
//            System.out.println(c);
//            PluginProxyMethod method = RuntimeContext.getBean(PluginProxyMethodFactory.class).proxyMethod("com.dmsoft.dap.SpcPlugin", "com.dmsoft.firefly.plugin.spc.SpcService", "say");
//            method.doSomething(null, "AA");
//            System.out.println("SADF");
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
        pluginInfoContextImpl.startPlugin(activePlugins);
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
