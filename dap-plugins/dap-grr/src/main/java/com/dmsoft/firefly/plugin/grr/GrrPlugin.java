/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.grr;


import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.plugin.grr.handler.ParamKeys;
import com.dmsoft.firefly.plugin.grr.pipeline.*;
import com.dmsoft.firefly.plugin.grr.service.GrrAnalysisService;
import com.dmsoft.firefly.plugin.grr.service.GrrConfigService;
import com.dmsoft.firefly.plugin.grr.service.GrrFilterService;
import com.dmsoft.firefly.plugin.grr.service.GrrService;
import com.dmsoft.firefly.plugin.grr.service.impl.GrrAnalysisServiceImpl;
import com.dmsoft.firefly.plugin.grr.service.impl.GrrConfigServiceImpl;
import com.dmsoft.firefly.plugin.grr.service.impl.GrrFilterServiceImpl;
import com.dmsoft.firefly.plugin.grr.service.impl.GrrServiceImpl;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.job.core.JobManager;
import com.dmsoft.firefly.sdk.plugin.Plugin;
import com.dmsoft.firefly.sdk.plugin.PluginImageContext;
import com.dmsoft.firefly.sdk.ui.IMainBodyPane;
import com.dmsoft.firefly.sdk.ui.MenuBuilder;
import com.dmsoft.firefly.sdk.ui.PluginUIContext;
import com.dmsoft.firefly.sdk.utils.enums.InitModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * grr plugin
 */
public class GrrPlugin extends Plugin {
    public static final String GRR_PLUGIN_ID = "com.dmsoft.dap.GrrPlugin";

    public static final String GRR_SERVICE_PACKAGE = "com.dmsoft.firefly.plugin.grr.service.impl.";
    public static final String GRR_SERVICE_ANALYSIS_NAME = "GrrAnalysisServiceImpl";
    public static final String GRR_SERVICE_RESULT_NAME = "GrrServiceImpl";
    public static final String GRR_SERVICE_CONFIG_NAME = "GrrConfigServiceImpl";
    public static final String GRR_SERVICE_FILTER = "GrrFilterServiceImpl";

    private static final Logger LOGGER = LoggerFactory.getLogger(GrrPlugin.class);

    @Override
    public void initialize(InitModel model) {
        GrrServiceImpl grrService = new GrrServiceImpl();
        GrrAnalysisService grrAnalysisService = new GrrAnalysisServiceImpl();
        GrrConfigService grrConfigService = new GrrConfigServiceImpl();
        GrrFilterService grrFilterService = new GrrFilterServiceImpl();

        grrService.setAnalysisService(grrAnalysisService);
        RuntimeContext.registerBean(GrrService.class, grrService);
        RuntimeContext.registerBean(GrrConfigService.class, grrConfigService);
        RuntimeContext.registerBean(GrrAnalysisService.class, grrAnalysisService);
        RuntimeContext.registerBean(GrrFilterService.class, grrFilterService);
        RuntimeContext.getBean(PluginImageContext.class).registerPluginInstance(GRR_PLUGIN_ID, GRR_SERVICE_PACKAGE + GRR_SERVICE_CONFIG_NAME, grrConfigService);
        RuntimeContext.getBean(PluginImageContext.class).registerPluginInstance(GRR_PLUGIN_ID, GRR_SERVICE_PACKAGE + GRR_SERVICE_RESULT_NAME, grrService);
        RuntimeContext.getBean(PluginImageContext.class).registerPluginInstance(GRR_PLUGIN_ID, GRR_SERVICE_PACKAGE + GRR_SERVICE_ANALYSIS_NAME, grrAnalysisService);
        RuntimeContext.getBean(PluginImageContext.class).registerPluginInstance(GRR_PLUGIN_ID, GRR_SERVICE_PACKAGE + GRR_SERVICE_FILTER, grrFilterService);
        LOGGER.info("Plugin-GRR Initialized.");
    }

    @Override
    public void start() {
        RuntimeContext.getBean(PluginUIContext.class).registerMainBody("grr", new IMainBodyPane() {
            @Override
            public Pane getNewPane() {
                Pane root = null;
                try {
                    FXMLLoader fxmlLoader = GrrFxmlAndLanguageUtils.getLoaderFXML("view/grr.fxml");
                    root = fxmlLoader.load();
//                    root.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
                    root.getStylesheets().add(getClass().getClassLoader().getResource("css/grr_app.css").toExternalForm());
                    root.getStylesheets().add(getClass().getClassLoader().getResource("css/grr_chart.css").toExternalForm());

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return root;
            }
        });
        LOGGER.debug("Plugin-GRR UI register done.");

        LOGGER.info("Plugin-GRR started.");

        MenuItem menuItem = new MenuItem(GrrFxmlAndLanguageUtils.getString("MENU_GRR_SETTING"));
        menuItem.setId("grrSetting");
        menuItem.setOnAction(event -> build());
        menuItem.setMnemonicParsing(true);
        menuItem.setAccelerator(new KeyCodeCombination(KeyCode.G, KeyCombination.SHORTCUT_DOWN));

        RuntimeContext.getBean(PluginUIContext.class).registerMenu(new MenuBuilder("com.dmsoft.dap.GrrPlugin",
                MenuBuilder.MenuType.MENU_ITEM, "Grr Settings", MenuBuilder.MENU_PREFERENCE).addMenu(menuItem));

        JobManager manager = RuntimeContext.getBean(JobManager.class);
//        manager.initializeJob(ParamKeys.GRR_ANALYSIS_JOB_PIPELINE, new GrrSummaryJobPipeline());
        manager.initializeJob(ParamKeys.GRR_DETAIL_ANALYSIS_JOB_PIPELINE, new GrrDetailResultJobPipeline());
        manager.initializeJob(ParamKeys.GRR_VIEW_DATA_JOB_PIPELINE, new GrrViewDataJobPipeline());
        manager.initializeJob(ParamKeys.GRR_EXPORT_JOB_PIPELINE, new GrrExportJobPipeline());
        manager.initializeJob(ParamKeys.GRR_EXPORT_DETAIL_JOB_PIPELINE, new GrrExportDetailJobPipeline());

        manager.initializeJob(ParamKeys.GRR_REFRESH_JOB_PIPELINE, new GrrRefreshJobPipeline());

    }

    @Override
    public void destroy() {
        System.out.println("Plugin-GRR Destroyed.");
    }

    private void build() {
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = GrrFxmlAndLanguageUtils.getLoaderFXML("view/grr_setting.fxml");
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("grrSetting", "Grr Setting", root, getClass().getClassLoader().getResource("css/grr_app.css").toExternalForm());
            stage.toFront();
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
