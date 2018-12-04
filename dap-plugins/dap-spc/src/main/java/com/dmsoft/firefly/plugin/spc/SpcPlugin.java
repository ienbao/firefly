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
import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * spc plugin
 */
@Component
public class SpcPlugin extends Plugin {
    public static final String SPC_PLUGIN_NAME = "com.dmsoft.dap.SpcPlugin";
    private static final Logger LOGGER = LoggerFactory.getLogger(SpcPlugin.class);
    private static final Double D100 = 100.0;
    private SpcSettingController spcSettingController;

    @Autowired
    private PluginUIContext pluginUIContext;
    @Autowired
    private SpcService spcService;
    @Autowired
    private  SpcAnalysisService spcAnalysisService;
    @Autowired
    private SpcSettingService spcSettingService;
    @Autowired
    private PluginImageContext pluginImageContext;
    @Autowired
    private JobManager jobManager;
    @Autowired
    private JobFactory jobFactory;

    @Override
    public void initialize(InitModel model) {
        this.pluginImageContext.registerPluginInstance(SPC_PLUGIN_NAME,
                "com.dmsoft.firefly.plugin.spc.service.impl.SpcServiceImpl", spcService);
        this.pluginImageContext.registerPluginInstance(SPC_PLUGIN_NAME,
                "com.dmsoft.firefly.plugin.spc.service.impl.SpcAnalysisServiceImpl", spcAnalysisService);
        this.pluginImageContext.registerPluginInstance(SPC_PLUGIN_NAME,
                "com.dmsoft.firefly.plugin.spc.service.impl.SpcSettingServiceImpl", spcSettingService);
        LOGGER.info("Plugin-SPC Initialized.");
    }

    @Override
    public void start() {
        this.pluginUIContext.registerMainBody("SPC", new IMainBodyPane() {
            @Override
            public Pane getNewPane() {
                SvgImageLoaderFactory.install();
                FXMLLoader fxmlLoader = SpcFxmlAndLanguageUtils.getLoaderFXML(ViewResource.SPC_VIEW_RES);
                //FXMLLoader fxmlLoader = SpcFxmlAndLanguageUtils.getInstance().getLoaderFXML("view/spc.fxml");
                Pane root = null;
                try {
                    root = fxmlLoader.load();
                    root.getStylesheets().addAll(WindowFactory.checkStyles());
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
        this.pluginUIContext.registerMenu(new MenuBuilder("com.dmsoft.dap.SpcPlugin",
                MenuBuilder.MenuType.MENU_ITEM, "Spc Settings", MenuBuilder.MENU_PREFERENCE).addMenu(menuItem));

        jobManager.initializeJob(ParamKeys.SPC_ANALYSIS_JOB_PIPELINE, jobFactory.createJobPipeLine()
                .addLast(new FindSpcSettingDataHandler())
                .addLast(new FindTestDataHandler().setWeight(D100))
                .addLast(new DataFrameHandler())
                .addLast(new GetSpcStatsResultHandler().setWeight(D100)));
        jobManager.initializeJob(ParamKeys.SPC_ANALYSIS_EXPORT_JOB_PIPELINE, jobFactory.createJobPipeLine()
                .addLast(new FindSpcSettingDataHandler())
                .addLast(new FindTestDataHandler().setWeight(D100))
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
                .addLast(new FindTestDataHandler().setWeight(D100))
                .addLast(new DataFrameHandler()));

        jobManager.initializeJob(ParamKeys.SPC_TIMER_REFRESH_ANALYSIS_JOB_PIPELINE, jobFactory.createJobPipeLine()
                .addLast(new FindSpcSettingDataHandler())
                .addLast(new FindTestDataHandler().setWeight(D100))
                .addLast(new DataFrameHandler())
                .addLast(new TimerRefreshAnalysisHandler())
                .addLast(new RefreshAnalysisDataHandler().setWeight(D100)));
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
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel(StateKey.SPC_SETTING, SpcFxmlAndLanguageUtils.getString("SPC_SETTINGS"), root, getClass().getClassLoader().getResource("css/spc_app.css").toExternalForm());
            stage.setResizable(false);
            stage.toFront();
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
