/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.core;

import com.dmsoft.firefly.core.sdkimpl.dai.*;
import com.dmsoft.firefly.core.sdkimpl.dataframe.BasicDataFrameFactoryImpl;
import com.dmsoft.firefly.core.sdkimpl.event.EventContextImpl;
import com.dmsoft.firefly.core.sdkimpl.plugin.PluginContextImpl;
import com.dmsoft.firefly.core.sdkimpl.plugin.PluginImageContextImpl;
import com.dmsoft.firefly.core.sdkimpl.plugin.PluginProxyMethodFactoryImpl;
import com.dmsoft.firefly.core.sdkimpl.plugin.PluginUIContextImpl;
import com.dmsoft.firefly.core.utils.ApplicationPathUtil;
import com.dmsoft.firefly.core.utils.PluginScanner;
import com.dmsoft.firefly.core.utils.PropertiesUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.service.*;
import com.dmsoft.firefly.sdk.dataframe.DataFrameFactory;
import com.dmsoft.firefly.sdk.event.EventContext;
import com.dmsoft.firefly.sdk.job.DefaultJobManager;
import com.dmsoft.firefly.sdk.job.core.JobManager;
import com.dmsoft.firefly.sdk.plugin.PluginContext;
import com.dmsoft.firefly.sdk.plugin.PluginImageContext;
import com.dmsoft.firefly.sdk.plugin.PluginInfo;
import com.dmsoft.firefly.sdk.plugin.PluginProxyMethodFactory;
import com.dmsoft.firefly.sdk.ui.PluginUIContext;
import com.dmsoft.firefly.sdk.utils.enums.InitModel;
import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
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
        TemplateServiceImpl templateService = new TemplateServiceImpl();
        UserPreferenceServiceImpl userPreferenceService = new UserPreferenceServiceImpl();
        UserServiceImpl userService = new UserServiceImpl();
        EnvServiceImpl envService = new EnvServiceImpl();
        EventContextImpl eventContext = new EventContextImpl();
        BasicDataFrameFactoryImpl dataFrameFactory = new BasicDataFrameFactoryImpl();
        MongoClient mongoClient = new MongoClient("localhost");
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, "test");
        SourceDataService sourceDataService = new SourceDataServiceImpl();

        RuntimeContext.registerBean(PluginContext.class, pluginInfoContextImpl);
        RuntimeContext.registerBean(PluginImageContext.class, pluginImageContext);
        RuntimeContext.registerBean(PluginProxyMethodFactory.class, pluginProxy);
        RuntimeContext.registerBean(PluginUIContext.class, pluginUIContext);
        RuntimeContext.registerBean(JobManager.class, jobManager);
        RuntimeContext.registerBean(TemplateService.class, templateService);
        RuntimeContext.registerBean(UserPreferenceService.class, userPreferenceService);
        RuntimeContext.registerBean(UserService.class, userService);
        RuntimeContext.registerBean(EnvService.class, proxyClass(envService));
        RuntimeContext.registerBean(EventContext.class, eventContext);
        RuntimeContext.registerBean(MongoTemplate.class, mongoTemplate);
        RuntimeContext.registerBean(DataFrameFactory.class, dataFrameFactory);
        RuntimeContext.registerBean(SourceDataService.class, sourceDataService);

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

    private static EnvService proxyClass(EnvService envService) {
        EnvService envServiceProxy = (EnvService) Proxy.newProxyInstance(envService.getClass().getClassLoader(), envService.getClass().getInterfaces(), new EnvServiceHandler(envService));
        return envServiceProxy;
    }

    static class EnvServiceHandler implements InvocationHandler {

        private EnvService target;

        public EnvServiceHandler(EnvService target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Field templateService = target.getClass().getDeclaredField("templateService");
            Field userPreferenceService = target.getClass().getDeclaredField("userPreferenceService");
            if (templateService != null) {
                templateService.setAccessible(true);
                templateService.set(target, RuntimeContext.getBean(TemplateService.class));
            }
            if (userPreferenceService != null) {
                userPreferenceService.setAccessible(true);
                userPreferenceService.set(target, RuntimeContext.getBean(UserPreferenceService.class));
            }
            return method.invoke(target, args);
        }
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
