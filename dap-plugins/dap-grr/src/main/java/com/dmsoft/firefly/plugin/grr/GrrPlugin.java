/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.grr;


import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.plugin.grr.handler.*;
import com.dmsoft.firefly.plugin.grr.service.*;
import com.dmsoft.firefly.plugin.grr.service.impl.*;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
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
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * grr plugin
 */
@Component
public class GrrPlugin extends Plugin {
    public static final String GRR_PLUGIN_ID = "com.dmsoft.dap.GrrPlugin";

    public static final String GRR_SERVICE_PACKAGE = "com.dmsoft.firefly.plugin.grr.service.impl.";
    public static final String GRR_SERVICE_ANALYSIS_NAME = "GrrAnalysisServiceImpl";
    public static final String GRR_SERVICE_RESULT_NAME = "GrrServiceImpl";
    public static final String GRR_SERVICE_CONFIG_NAME = "GrrConfigServiceImpl";
    public static final String GRR_SERVICE_FILTER = "GrrFilterServiceImpl";
    private static final Double D100 = 100.0;

    private static final Logger LOGGER = LoggerFactory.getLogger(GrrPlugin.class);



    @Autowired
    private GrrServiceImpl grrService;
    @Autowired
    private GrrAnalysisService grrAnalysisService;

    @Autowired
    private GrrConfigService grrConfigService;

    @Autowired
    private GrrFilterService grrFilterService;

    @Autowired
    private GrrExportService grrExportService;

    @Autowired
    private PluginImageContext pluginImageContext;

    @Autowired
    private PluginUIContext pluginUIContext;
    @Autowired
    private JobManager jobManager;
    @Autowired
    private JobFactory jobFactory;
    @Autowired
    private GrrFxmlLoadService grrFxmlLoadService;
    @Autowired
    private ApplicationContext context;

    @Override
    public void initialize(InitModel model) {
//        GrrServiceImpl grrService = new GrrServiceImpl();
//        GrrAnalysisService grrAnalysisService = new GrrAnalysisServiceImpl();
//        GrrConfigService grrConfigService = new GrrConfigServiceImpl();
//        GrrFilterService grrFilterService = new GrrFilterServiceImpl();

//        grrService.setAnalysisService(grrAnalysisService);
//        RuntimeContext.registerBean(GrrService.class, grrService);
//        RuntimeContext.registerBean(GrrConfigService.class, grrConfigService);
//        RuntimeContext.registerBean(GrrAnalysisService.class, grrAnalysisService);
//        RuntimeContext.registerBean(GrrFilterService.class, grrFilterService);
//        RuntimeContext.registerBean(GrrExportService.class, new GrrExportServiceImpl());


        this.pluginImageContext.registerPluginInstance(GRR_PLUGIN_ID, GRR_SERVICE_PACKAGE + GRR_SERVICE_CONFIG_NAME, grrConfigService);
        this.pluginImageContext.registerPluginInstance(GRR_PLUGIN_ID, GRR_SERVICE_PACKAGE + GRR_SERVICE_RESULT_NAME, grrService);
        this.pluginImageContext.registerPluginInstance(GRR_PLUGIN_ID, GRR_SERVICE_PACKAGE + GRR_SERVICE_ANALYSIS_NAME, grrAnalysisService);
        this.pluginImageContext.registerPluginInstance(GRR_PLUGIN_ID, GRR_SERVICE_PACKAGE + GRR_SERVICE_FILTER, grrFilterService);
//        RuntimeContext.getBean(PluginImageContext.class).registerPluginInstance(GRR_PLUGIN_ID, GRR_SERVICE_PACKAGE + GRR_SERVICE_CONFIG_NAME, grrConfigService);
//        RuntimeContext.getBean(PluginImageContext.class).registerPluginInstance(GRR_PLUGIN_ID, GRR_SERVICE_PACKAGE + GRR_SERVICE_RESULT_NAME, grrService);
//        RuntimeContext.getBean(PluginImageContext.class).registerPluginInstance(GRR_PLUGIN_ID, GRR_SERVICE_PACKAGE + GRR_SERVICE_ANALYSIS_NAME, grrAnalysisService);
//        RuntimeContext.getBean(PluginImageContext.class).registerPluginInstance(GRR_PLUGIN_ID, GRR_SERVICE_PACKAGE + GRR_SERVICE_FILTER, grrFilterService);

        GrrFxmlAndLanguageUtils.setContext(this.context);
        LOGGER.info("Plugin-GRR Initialized.");
    }

    @Override
    public void start() {
        this.pluginUIContext.registerMainBody("GRR", new IMainBodyPane() {
            @Override
            public Pane getNewPane() {
                SvgImageLoaderFactory.install();
                Pane root = null;
                try {
//                    FXMLLoader fxmlLoader = GrrFxmlAndLanguageUtils.getLoaderFXML("view/grr.fxml");
//                    root = fxmlLoader.load();
                    root = grrFxmlLoadService.loadFxml("view/grr.fxml");
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

        this.pluginUIContext.registerMenu(new MenuBuilder("com.dmsoft.dap.GrrPlugin",
                MenuBuilder.MenuType.MENU_ITEM, "Grr Settings", MenuBuilder.MENU_PREFERENCE).addMenu(menuItem));

        jobManager.initializeJob(ParamKeys.GRR_DETAIL_ANALYSIS_JOB_PIPELINE, jobFactory.createJobPipeLine()
                .addLast(new GrrConfigHandler())
                .addLast(new DetailResultHandler().setWeight(D100)));

        jobManager.initializeJob(ParamKeys.GRR_VIEW_DATA_JOB_PIPELINE, jobFactory.createJobPipeLine()
                .addLast(new FindTestDataHandler().setWeight(D100))
                .addLast(new DataFrameHandler())
                .addLast(new ValidateParamHandler())
                .addLast(new GrrConfigHandler())
                .addLast(new ViewDataHandler())
                .addLast(new SummaryHandler().setWeight(D100))
                .addLast(new DetailResultHandler()));

        jobManager.initializeJob(ParamKeys.GRR_EXPORT_JOB_PIPELINE, jobFactory.createJobPipeLine()
                .addLast(new FindTestDataHandler().setWeight(D100))
                .addLast(new DataFrameHandler())
                .addLast(new ValidateParamHandler())
                .addLast(new GrrConfigHandler())
                .addLast(new ViewDataHandler())
                .addLast(new SummaryHandler().setWeight(D100))
                .addLast(new ExportSummaryHandler().setWeight(D100)));

        jobManager.initializeJob(ParamKeys.GRR_EXPORT_DETAIL_JOB_PIPELINE, jobFactory.createJobPipeLine()
                .addLast(new FindTestDataHandler().setWeight(D100))
                .addLast(new DataFrameHandler())
                .addLast(new ValidateParamHandler())
                .addLast(new GrrConfigHandler())
                .addLast(new ViewDataHandler())
                .addLast(new FindExportDetailHandler().setWeight(D100))
                .addLast(new ExportDetailResultHandler().setWeight(D100)));

        jobManager.initializeJob(ParamKeys.GRR_REFRESH_JOB_PIPELINE, jobFactory.createJobPipeLine()
                .addLast(new GrrConfigHandler())
                .addLast(new RefreshHandler().setWeight(D100)));

        jobManager.initializeJob(ParamKeys.GRR_EXPORT_VIEW_DATA, jobFactory.createJobPipeLine()
                .addLast(new FindTestDataHandler().setWeight(D100))
                .addLast(new DataFrameHandler()));
    }

    @Override
    public void destroy() {
        System.out.println("Plugin-GRR Destroyed.");
    }

    private void build() {
        Pane root = null;
        try {
//            FXMLLoader fxmlLoader = GrrFxmlAndLanguageUtils.getLoaderFXML("view/grr_setting.fxml");
//            root = fxmlLoader.load();
            root = this.grrFxmlLoadService.loadFxml("view/grr_setting.fxml");
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("grrSetting", GrrFxmlAndLanguageUtils.getString("GRR_SETTINGS"), root, getClass().getClassLoader().getResource("css/grr_app.css").toExternalForm());
            stage.setResizable(false);
            stage.toFront();
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
