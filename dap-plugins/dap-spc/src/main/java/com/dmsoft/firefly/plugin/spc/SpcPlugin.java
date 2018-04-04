/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.spc;

import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.plugin.spc.controller.SpcSettingController;
import com.dmsoft.firefly.plugin.spc.handler.*;
import com.dmsoft.firefly.plugin.spc.service.SpcAnalysisService;
import com.dmsoft.firefly.plugin.spc.service.SpcService;
import com.dmsoft.firefly.plugin.spc.service.SpcSettingService;
import com.dmsoft.firefly.plugin.spc.service.impl.SpcAnalysisServiceImpl;
import com.dmsoft.firefly.plugin.spc.service.impl.SpcServiceImpl;
import com.dmsoft.firefly.plugin.spc.service.impl.SpcSettingServiceImpl;
import com.dmsoft.firefly.plugin.spc.utils.SpcFxmlAndLanguageUtils;
import com.dmsoft.firefly.plugin.spc.utils.StateKey;
import com.dmsoft.firefly.plugin.spc.utils.ViewResource;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.job.core.JobFactory;
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
 * spc plugin
 */
public class SpcPlugin extends Plugin {
    public static final String SPC_PLUGIN_NAME = "com.dmsoft.dap.SpcPlugin";
    private static final Logger LOGGER = LoggerFactory.getLogger(SpcPlugin.class);
    private static final Double D100 = 100.0;
    private SpcSettingController spcSettingController;

    @Override
    public void initialize(InitModel model) {
        SpcServiceImpl spcService = new SpcServiceImpl();
        SpcAnalysisServiceImpl spcAnalysisService = new SpcAnalysisServiceImpl();
        SpcSettingService spcSettingService = new SpcSettingServiceImpl();
        spcService.setAnalysisService(spcAnalysisService);
        RuntimeContext.registerBean(SpcService.class, spcService);
        RuntimeContext.registerBean(SpcAnalysisService.class, spcAnalysisService);
        RuntimeContext.registerBean(SpcSettingService.class, spcSettingService);
        RuntimeContext.getBean(PluginImageContext.class).registerPluginInstance(SPC_PLUGIN_NAME,
                "com.dmsoft.firefly.plugin.spc.service.impl.SpcServiceImpl", spcService);

        RuntimeContext.getBean(PluginImageContext.class).registerPluginInstance(SPC_PLUGIN_NAME,
                "com.dmsoft.firefly.plugin.spc.service.impl.SpcAnalysisServiceImpl", spcAnalysisService);

        RuntimeContext.getBean(PluginImageContext.class).registerPluginInstance(SPC_PLUGIN_NAME,
                "com.dmsoft.firefly.plugin.spc.service.impl.SpcSettingServiceImpl", spcSettingService);
        LOGGER.info("Plugin-SPC Initialized.");
    }

    @Override
    public void start() {
        RuntimeContext.getBean(PluginUIContext.class).registerMainBody("SPC", new IMainBodyPane() {
            @Override
            public Pane getNewPane() {
                FXMLLoader fxmlLoader = SpcFxmlAndLanguageUtils.getLoaderFXML(ViewResource.SPC_VIEW_RES);
                //FXMLLoader fxmlLoader = SpcFxmlAndLanguageUtils.getInstance().getLoaderFXML("view/spc.fxml");
                Pane root = null;
                try {
                    root = fxmlLoader.load();
                    root.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
                    root.getStylesheets().add(getClass().getClassLoader().getResource("css/spc_app.css").toExternalForm());
                    root.getStylesheets().add(getClass().getClassLoader().getResource("css/charts.css").toExternalForm());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return root;
            }
        });

        LOGGER.debug("Plugin-SPC UI register done.");

        LOGGER.info("Plugin-SPC started.");

        //register spc setting menu
        MenuItem menuItem = new MenuItem(SpcFxmlAndLanguageUtils.getString("MENU_SPC_SETTING"));
        menuItem.setId("spcSetting");
        menuItem.setMnemonicParsing(true);
        menuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
        menuItem.setOnAction(event -> {
            if (StageMap.getStage(StateKey.SPC_SETTING) == null) {
                initSpcSettingDialog();
            } else {
                if (spcSettingController != null) {
                    spcSettingController.initData();
                }
                StageMap.showStage(StateKey.SPC_SETTING);
            }
        });
        RuntimeContext.getBean(PluginUIContext.class).registerMenu(new MenuBuilder("com.dmsoft.dap.SpcPlugin",
                MenuBuilder.MenuType.MENU_ITEM, "Spc Settings", MenuBuilder.MENU_PREFERENCE).addMenu(menuItem));

        JobManager jobManager = RuntimeContext.getBean(JobManager.class);
        JobFactory jobFactory = RuntimeContext.getBean(JobFactory.class);
        jobManager.initializeJob(ParamKeys.SPC_ANALYSIS_JOB_PIPELINE, jobFactory.createJobPipeLine()
                .addLast(new FindSpcSettingDataHandler())
                .addLast(new FindTestDataHandler())
                .addLast(new DataFrameHandler())
                .addLast(new GetSpcStatsResultHandler().setWeight(D100)));
        jobManager.initializeJob(ParamKeys.SPC_ANALYSIS_EXPORT_JOB_PIPELINE, jobFactory.createJobPipeLine()
                .addLast(new FindSpcSettingDataHandler())
                .addLast(new FindTestDataHandler())
                .addLast(new DataFrameHandler())
                .addLast(new GetSpcStatsResultHandler().setWeight(D100)));

        jobManager.initializeJob(ParamKeys.SPC_REFRESH_CHART_JOB_PIPELINE, jobFactory.createJobPipeLine()
                .addLast(new GetSpcChartResultHandler().setWeight(D100)));
        jobManager.initializeJob(ParamKeys.SPC_REFRESH_CHART_EXPORT_JOB_PIPELINE, jobFactory.createJobPipeLine()
                .addLast(new GetSpcChartResultHandler().setWeight(D100)));

        jobManager.initializeJob(ParamKeys.SPC_RESET_JOB_PIPELINE, jobFactory.createJobPipeLine()
                .addLast(new GetSpcStatsResultHandler().setWeight(D100)));

        jobManager.initializeJob(ParamKeys.SPC_REFRESH_STATISTICAL_JOB_PIPELINE, jobFactory.createJobPipeLine()
                .addLast(new GetSpcStatsResultHandler().setWeight(D100)));

        jobManager.initializeJob(ParamKeys.SPC_REFRESH_ANALYSIS_JOB_PIPELINE, jobFactory.createJobPipeLine()
                .addLast(new RefreshAnalysisDataHandler().setWeight(D100)));

        jobManager.initializeJob(ParamKeys.FIND_SPC_SETTING_DATA_JOP_PIPELINE, jobFactory.createJobPipeLine()
                .addLast(new FindSpcSettingDataHandler()));

        jobManager.initializeJob(ParamKeys.SAVE_SPC_SETTING_DATA_JOP_PIPELINE, jobFactory.createJobPipeLine()
                .addLast(new SaveSpcSettingDataHandler()));

        jobManager.initializeJob(ParamKeys.SPC_EXPORT_VIEW_DATA, jobFactory.createJobPipeLine()
                .addLast(new FindTestDataHandler())
                .addLast(new DataFrameHandler()));
    }

    @Override
    public void destroy() {
        System.out.println("Plugin-SPC Destroyed.");
    }

    private void initSpcSettingDialog() {
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = SpcFxmlAndLanguageUtils.getLoaderFXML("view/spc_setting.fxml");
            root = fxmlLoader.load();
            spcSettingController = fxmlLoader.getController();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel(StateKey.SPC_SETTING, "Spc Setting", root, getClass().getClassLoader().getResource("css/spc_app.css").toExternalForm());
            stage.setResizable(false);
            stage.toFront();
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
