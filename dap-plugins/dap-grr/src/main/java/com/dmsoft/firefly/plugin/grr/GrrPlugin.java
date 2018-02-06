/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.grr;


import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.plugin.Plugin;
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
 * grr plugin
 */
public class GrrPlugin extends Plugin {
    private static final Logger logger = LoggerFactory.getLogger(GrrPlugin.class);

    @Override
    public void initialize(InitModel model) {
        RuntimeContext.getBean(PluginImageContext.class).registerPluginInstance("com.dmsoft.dap.GrrPlugin", "com.dmsoft.firefly.plugin.grr.GrrService", new GrrService());
        logger.info("Plugin-GRR Initialized.");
    }

    @Override
    public void start() {
        RuntimeContext.getBean(PluginUIContext.class).registerMainBody("grr", new IMainBodyPane() {
            @Override
            public Pane getNewPane() {
                Pane root = null;
                try {
                    root = FXMLLoader.load(getClass().getClassLoader().getResource("view/grr.fxml"), ResourceBundle.getBundle("i18n.message_en_US"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return root;
            }

            @Override
            public void reset() {

            }
        });
        logger.debug("Plugin-GRR UI register done.");

        logger.info("Plugin-GRR started.");
    }

    @Override
    public void destroy() {
        System.out.println("Plugin-GRR Destroyed.");
    }
}
