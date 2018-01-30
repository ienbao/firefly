/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.spc;


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

/**
 * spc plugin
 */
public class SpcPlugin extends Plugin {
    private static final Logger logger = LoggerFactory.getLogger(SpcPlugin.class);

    @Override
    public void initialize(InitModel model) {
        RuntimeContext.getBean(PluginImageContext.class).registerPluginInstance("com.dmsoft.dap.SpcPlugin", "com.dmsoft.firefly.plugin.spc.SpcService", new SpcService());
        logger.info("Plugin-SPC Initialized.");
    }

    @Override
    public void start() {
        RuntimeContext.getBean(PluginUIContext.class).registerMainBody("spc", new IMainBodyPane() {
            @Override
            public Pane getNewPane() {
                Pane root = null;
                try {
                    root = FXMLLoader.load(getClass().getClassLoader().getResource("view/spc.fxml"));
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
