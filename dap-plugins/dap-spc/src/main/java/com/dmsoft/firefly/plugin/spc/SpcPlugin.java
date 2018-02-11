/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.spc;


import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.plugin.spc.controller.SpcMainController;
import com.dmsoft.firefly.plugin.spc.service.SpcServiceImpl;
import com.dmsoft.firefly.plugin.spc.utils.ViewResource;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.plugin.Plugin;
import com.dmsoft.firefly.sdk.plugin.PluginContext;
import com.dmsoft.firefly.sdk.plugin.PluginImageContext;
import com.dmsoft.firefly.sdk.ui.IMainBodyPane;
import com.dmsoft.firefly.sdk.ui.PluginUIContext;
import com.dmsoft.firefly.sdk.utils.enums.InitModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ResourceBundle;

/**
 * spc plugin
 */
public class SpcPlugin extends Plugin {
    private static final Logger logger = LoggerFactory.getLogger(SpcPlugin.class);

    @Override
    public void initialize(InitModel model) {
        RuntimeContext.getBean(PluginImageContext.class).registerPluginInstance("com.dmsoft.dap.SpcPlugin", "com.dmsoft.firefly.plugin.spc.service.SpcServiceImpl", new SpcServiceImpl());
        logger.info("Plugin-SPC Initialized.");
    }

    @Override
    public void start() {
        RuntimeContext.getBean(PluginUIContext.class).registerMainBody("spc", new IMainBodyPane() {
            @Override
            public Pane getNewPane() {
                Pane root = null;
                try {

                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("view/spc.fxml"), ResourceBundle.getBundle("i18n.message_en_US"));
                    fxmlLoader.setClassLoader(RuntimeContext.getBean(PluginContext.class).getDAPClassLoader("com.dmsoft.dap.SpcPlugin"));

                    root = fxmlLoader.load();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return root;
            }

            @Override
            public void reset() {

            }
        });
        logger.debug("Plugin-SPC UI register done.");

        logger.info("Plugin-SPC started.");
    }

    @Override
    public void destroy() {
        System.out.println("Plugin-SPC Destroyed.");
    }
}
