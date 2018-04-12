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
import com.dmsoft.firefly.sdk.plugin.PluginContext;
import com.dmsoft.firefly.sdk.plugin.PluginImageContext;
import com.dmsoft.firefly.sdk.plugin.PluginInfo;
import com.dmsoft.firefly.sdk.plugin.PluginProxyMethodFactory;
import com.dmsoft.firefly.sdk.ui.PluginUIContext;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.dmsoft.firefly.sdk.utils.PropertyConfig;
import com.dmsoft.firefly.sdk.utils.enums.InitModel;
import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.Properties;


/**
 * DAP Application starter include basic flows.
 *
 * @author Can Guan
 */
public class DAPApplication {

    private static Logger logger = LoggerFactory.getLogger(DAPApplication.class);

    /**
     * methdod to init env
     */
    public static void initEnv() {
        String propertiesURL = ApplicationPathUtil.getPath("application.properties");
        Properties properties = PropertyConfig.getProperties(propertiesURL);
        String mongoHost = properties.getProperty("MongoHost", "localhost");
        String mongoPort = properties.getProperty("MongoPort", "27017");
        String mongoDB = properties.getProperty("MongoDB");
        // prepare env start
        PluginContextImpl pluginInfoContextImpl = new PluginContextImpl(InitModel.INIT_WITH_UI);
        PluginImageContextImpl pluginImageContext = new PluginImageContextImpl();
        pluginInfoContextImpl.addListener(pluginImageContext);
        PluginProxyMethodFactoryImpl pluginProxy = new PluginProxyMethodFactoryImpl();
        PluginUIContextImpl pluginUIContext = new PluginUIContextImpl();
        TemplateServiceImpl templateService = new TemplateServiceImpl();
        UserPreferenceServiceImpl userPreferenceService = new UserPreferenceServiceImpl();
        UserServiceImpl userService = new UserServiceImpl();
        EventContextImpl eventContext = new EventContextImpl();
        BasicDataFrameFactoryImpl dataFrameFactory = new BasicDataFrameFactoryImpl();

        int port = DAPStringUtils.isNumeric(mongoPort) ? Integer.valueOf(mongoPort) : 27017;
        MongoClient mongoClient = new MongoClient(mongoHost, port);
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, mongoDB);
        SourceDataServiceImpl sourceDataService = new SourceDataServiceImpl();
        TestDataCacheFactory factory = new TestDataCacheFactory();


        RuntimeContext.registerBean(PluginContext.class, pluginInfoContextImpl);
        RuntimeContext.registerBean(PluginImageContext.class, pluginImageContext);
        RuntimeContext.registerBean(PluginProxyMethodFactory.class, pluginProxy);
        RuntimeContext.registerBean(PluginUIContext.class, pluginUIContext);
        RuntimeContext.registerBean(TemplateService.class, templateService);
        RuntimeContext.registerBean(UserPreferenceService.class, userPreferenceService);
        RuntimeContext.registerBean(UserService.class, userService);

        EnvServiceImpl envService = new EnvServiceImpl();
        RuntimeContext.registerBean(EnvService.class, envService);

        RuntimeContext.registerBean(EventContext.class, eventContext);
        RuntimeContext.registerBean(MongoTemplate.class, mongoTemplate);
        RuntimeContext.registerBean(DataFrameFactory.class, dataFrameFactory);
        RuntimeContext.registerBean(SourceDataService.class, sourceDataService);
        RuntimeContext.registerBean(TestDataCacheFactory.class, factory);

    }

    /**
     * method to start application
     *
     * @param activePlugins plugins to be excluded
     */
    public static void startPlugin(List<String> activePlugins) {
        // prepare env done
        try {
            String propertiesURL = ApplicationPathUtil.getPath("application.properties");
            Properties properties = PropertyConfig.getProperties(propertiesURL);
            String pluginFolderPath = PropertiesUtils.getPluginsPath(properties);
            logger.info("start scan... pluginFolderPath = " + pluginFolderPath);
            List<PluginInfo> scannedPlugins = PluginScanner.scanPluginByPath(pluginFolderPath);
            logger.info("end scan... PluginInfo = " + scannedPlugins.toString());
            RuntimeContext.getBean(PluginContext.class).installPlugin(scannedPlugins);
            RuntimeContext.getBean(PluginContext.class).enablePlugin(activePlugins);
        } catch (Exception e) {
            e.printStackTrace();
        }
        RuntimeContext.getBean(PluginContext.class).startPlugin(activePlugins);
    }

    /**
     * main method
     *
     * @param args arguments
     */
    public static void main(String[] args) {
        initEnv();
        startPlugin(Lists.newArrayList("com.dmsoft.dap.SpcPlugin"));
    }

}
