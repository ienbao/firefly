/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.spc;


import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.plugin.Plugin;
import com.dmsoft.firefly.sdk.plugin.PluginImageContext;
import com.dmsoft.firefly.sdk.ui.PluginUI;
import com.dmsoft.firefly.sdk.utils.enums.InitModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * spc plugin
 */
public class SpcPlugin extends Plugin {
    Logger logger = LoggerFactory.getLogger(SpcPlugin.class);

    @Override
    public void initialize(InitModel model) {

        RuntimeContext.getBean(PluginImageContext.class).registerPluginInstance("com.dmsoft.dap.SpcPlugin", "com.dmsoft.firefly.plugin.spc.SpcService", new SpcService());
        System.out.println("SPC Initialized!!!!!!");
        logger.info("Plugin-SPC Initialized.");
    }

    @Override
    public void start() throws Exception {
        Pane root = FXMLLoader.load(SpcPlugin.class.getClassLoader().getResource("view/spc.fxml"));
        RuntimeContext.getBean(PluginUI.class).registerMainBody("spc", root);
//        RuntimeContext.getBean(PluginUI.class);
        logger.info("Plugin-SPC Started.");
        System.out.println("SPC Started!!!!!!");
    }

    @Override
    public void destroy() {
        System.out.println("SPC Destroyed!!!!!!");
    }
}
