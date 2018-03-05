/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.spc;


import com.dmsoft.firefly.plugin.spc.service.SpcAnalysisService;
import com.dmsoft.firefly.plugin.spc.service.SpcService;
import com.dmsoft.firefly.plugin.spc.service.impl.SpcAnalysisServiceImpl;
import com.dmsoft.firefly.plugin.spc.service.impl.SpcServiceImpl;
import com.dmsoft.firefly.plugin.spc.utils.SpcFxmlAndLanguageUtils;
import com.dmsoft.firefly.plugin.spc.utils.ViewResource;
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
    public static final String SPC_PLUGIN_NAME = "com.dmsoft.dap.SpcPlugin";
    private static final Logger logger = LoggerFactory.getLogger(SpcPlugin.class);

    @Override
    public void initialize(InitModel model) {
        SpcServiceImpl spcService = new SpcServiceImpl();
        SpcAnalysisServiceImpl spcAnalysisService = new SpcAnalysisServiceImpl();
        spcService.setAnalysisService(spcAnalysisService);
        RuntimeContext.registerBean(SpcService.class, spcService);
        RuntimeContext.registerBean(SpcAnalysisService.class, spcAnalysisService);
        RuntimeContext.getBean(PluginImageContext.class).registerPluginInstance(SPC_PLUGIN_NAME,
                "com.dmsoft.firefly.plugin.spc.service.impl.SpcServiceImpl", spcService);

        RuntimeContext.getBean(PluginImageContext.class).registerPluginInstance(SPC_PLUGIN_NAME,
                "com.dmsoft.firefly.plugin.spc.service.impl.SpcAnalysisServiceImpl", spcAnalysisService);
        logger.info("Plugin-SPC Initialized.");
    }

    @Override
    public void start() {
        RuntimeContext.getBean(PluginUIContext.class).registerMainBody("spc", new IMainBodyPane() {
            @Override
            public Pane getNewPane() {
                FXMLLoader fxmlLoader = SpcFxmlAndLanguageUtils.getLoaderFXML(ViewResource.SPC_VIEW_RES);
                //FXMLLoader fxmlLoader = SpcFxmlAndLanguageUtils.getInstance().getLoaderFXML("view/spc.fxml");
                Pane root = null;
                try {
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
