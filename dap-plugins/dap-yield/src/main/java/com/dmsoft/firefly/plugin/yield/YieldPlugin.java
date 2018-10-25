package com.dmsoft.firefly.plugin.yield;

import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.plugin.yield.controller.YieldSettingController;
import com.dmsoft.firefly.plugin.yield.service.YieldService;
import com.dmsoft.firefly.plugin.yield.service.YieldSettingService;
import com.dmsoft.firefly.plugin.yield.service.impl.YieldServiceImpl;
import com.dmsoft.firefly.plugin.yield.service.impl.YieldSettingServiceImpl;
import com.dmsoft.firefly.plugin.yield.utils.StateKey;
import com.dmsoft.firefly.plugin.yield.utils.ViewResource;
import com.dmsoft.firefly.plugin.yield.utils.YieldFxmlAndLanguageUtils;
import com.dmsoft.firefly.plugin.yield.handler.*;
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

public class YieldPlugin extends Plugin {
    public static final String Yield_PLUGIN_NAME = "com.dmsoft.dap.YieldPlugin";
    private static final Logger LOGGER = LoggerFactory.getLogger(YieldPlugin.class);
    private static final Double D100 = 100.0;
    private YieldSettingController yieldSettingController;

    @Override
    public void initialize(InitModel model) {
        YieldServiceImpl yieldService = new YieldServiceImpl();
//        SpcAnalysisServiceImpl spcAnalysisService = new SpcAnalysisServiceImpl();
        YieldSettingService yieldSettingService = new YieldSettingServiceImpl();
//        yieldService.setAnalysisService(spcAnalysisService);
        RuntimeContext.registerBean(YieldService.class, yieldService);
//        RuntimeContext.registerBean(SpcAnalysisService.class, spcAnalysisService);
        RuntimeContext.registerBean(YieldSettingService.class, yieldSettingService);
        RuntimeContext.getBean(PluginImageContext.class).registerPluginInstance(Yield_PLUGIN_NAME,
                "YieldServiceImpl", yieldService);

//        RuntimeContext.getBean(PluginImageContext.class).registerPluginInstance(Yield_PLUGIN_NAME,
//                "com.dmsoft.firefly.plugin.yield.service.impl.SpcAnalysisServiceImpl", spcAnalysisService);
//
//        RuntimeContext.getBean(PluginImageContext.class).registerPluginInstance(Yield_PLUGIN_NAME,
//                "com.dmsoft.firefly.plugin.yield.service.impl.SpcSettingServiceImpl", spcSettingService);
        LOGGER.info("Plugin-Yield Initialized.");
    }

    @Override
    public void start() {
        RuntimeContext.getBean(PluginUIContext.class).registerMainBody("Yield", new IMainBodyPane() {
            @Override
            public Pane getNewPane() {
                FXMLLoader fxmlLoader = YieldFxmlAndLanguageUtils.getLoaderFXML(ViewResource.Yield_VIEW_RES);
                //FXMLLoader fxmlLoader = SpcFxmlAndLanguageUtils.getInstance().getLoaderFXML("view/spc.fxml");
                Pane root = null;
                try {
                    root = fxmlLoader.load();
                    root.getStylesheets().addAll(WindowFactory.checkStyles());
                    root.getStylesheets().add(getClass().getClassLoader().getResource("css/yield_app.css").toExternalForm());
                    root.getStylesheets().add(getClass().getClassLoader().getResource("css/charts.css").toExternalForm());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return root;
            }
        });

        LOGGER.debug("Plugin-Yield UI register done.");

        LOGGER.info("Plugin-Yield started.");

        //register spc setting menu
        MenuItem menuItem = new MenuItem(YieldFxmlAndLanguageUtils.getString("MENU_Yield_SETTING"));
        menuItem.setId("yieldSetting");
        menuItem.setMnemonicParsing(true);
        menuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
        menuItem.setOnAction(event -> {
            if (StageMap.getStage(StateKey.YIELD_SETTING) == null) {
                initYieldSettingDialog();
            } else {
                if (yieldSettingController != null) {
                    yieldSettingController.initData();
                }
                StageMap.showStage(StateKey.YIELD_SETTING);
            }
        });
        RuntimeContext.getBean(PluginUIContext.class).registerMenu(new MenuBuilder("com.dmsoft.dap.YieldPlugin",
                MenuBuilder.MenuType.MENU_ITEM, "Spc Settings", MenuBuilder.MENU_PREFERENCE).addMenu(menuItem));

        JobManager jobManager = RuntimeContext.getBean(JobManager.class);
        JobFactory jobFactory = RuntimeContext.getBean(JobFactory.class);
        jobManager.initializeJob(ParamKeys.YIELD_ANALYSIS_JOB_PIPELINE, jobFactory.createJobPipeLine()
                .addLast(new FindYieldSettingDataHandler())
                .addLast(new FindTestDataHandler().setWeight(D100))
                .addLast(new DataFrameHandler())
                .addLast(new GetYieldResultHandler().setWeight(D100)));

        jobManager.initializeJob(ParamKeys.YIELD_ANALYSIS_EXPORT_JOB_PIPELINE, jobFactory.createJobPipeLine()
                .addLast(new FindYieldSettingDataHandler())
                .addLast(new FindTestDataHandler().setWeight(D100))
                .addLast(new DataFrameHandler())
                .addLast(new GetYieldResultHandler().setWeight(D100)));

        jobManager.initializeJob(ParamKeys.YIELD_VIEW_DATA_JOB_PIPELINE, jobFactory.createJobPipeLine()
                .addLast(new GetYieldViewDataHandler().setWeight(D100)));
//
//        jobManager.initializeJob(ParamKeys.SPC_REFRESH_CHART_JOB_PIPELINE, jobFactory.createJobPipeLine()
//                .addLast(new GetSpcChartResultHandler().setWeight(D100)));
//        jobManager.initializeJob(ParamKeys.SPC_REFRESH_CHART_EXPORT_JOB_PIPELINE, jobFactory.createJobPipeLine()
//                .addLast(new GetSpcChartResultHandler().setWeight(D100)));
//
        jobManager.initializeJob(ParamKeys.YIELD_RESET_JOB_PIPELINE, jobFactory.createJobPipeLine()
                .addLast(new GetYieldResultHandler().setWeight(D100)));

        jobManager.initializeJob(ParamKeys.SPC_REFRESH_STATISTICAL_JOB_PIPELINE, jobFactory.createJobPipeLine()
                .addLast(new FindYieldSettingDataHandler())
                .addLast(new FindTestDataHandler().setWeight(D100))
                .addLast(new DataFrameHandler())
                .addLast(new GetYieldResultHandler().setWeight(D100)));
//
//        jobManager.initializeJob(ParamKeys.SPC_REFRESH_ANALYSIS_JOB_PIPELINE, jobFactory.createJobPipeLine()
//                .addLast(new RefreshAnalysisDataHandler().setWeight(D100)));
//
        jobManager.initializeJob(ParamKeys.FIND_YIELD_SETTING_DATA_JOP_PIPELINE, jobFactory.createJobPipeLine()
                .addLast(new FindYieldSettingDataHandler()));
//
        jobManager.initializeJob(ParamKeys.SAVE_YIELD_SETTING_DATA_JOP_PIPELINE, jobFactory.createJobPipeLine()
                .addLast(new SaveYieldSettingDataHandler()));
//
//        jobManager.initializeJob(ParamKeys.SPC_EXPORT_VIEW_DATA, jobFactory.createJobPipeLine()
//                .addLast(new FindTestDataHandler().setWeight(D100))
//                .addLast(new DataFrameHandler()));
//
//        jobManager.initializeJob(ParamKeys.SPC_TIMER_REFRESH_ANALYSIS_JOB_PIPELINE, jobFactory.createJobPipeLine()
//                .addLast(new FindSpcSettingDataHandler())
//                .addLast(new FindTestDataHandler().setWeight(D100))
//                .addLast(new DataFrameHandler())
//                .addLast(new TimerRefreshAnalysisHandler())
//                .addLast(new RefreshAnalysisDataHandler().setWeight(D100)));
    }

    @Override
    public void destroy() {
        System.out.println("Plugin-Yield Destroyed.");
    }

    private void initYieldSettingDialog() {
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = YieldFxmlAndLanguageUtils.getLoaderFXML("view/yield_setting.fxml");
            root = fxmlLoader.load();
            yieldSettingController = fxmlLoader.getController();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel(StateKey.YIELD_SETTING, YieldFxmlAndLanguageUtils.getString("YIELD_SETTINGS"), root, getClass().getClassLoader().getResource("css/yield_app.css").toExternalForm());
            stage.setResizable(false);
            stage.toFront();
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
