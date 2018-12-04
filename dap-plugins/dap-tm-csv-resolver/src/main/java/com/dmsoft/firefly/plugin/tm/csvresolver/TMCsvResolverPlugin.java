/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.tm.csvresolver;


import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.plugin.tm.csvresolver.service.CsvConfigService;
import com.dmsoft.firefly.plugin.tm.csvresolver.service.CsvResolverService;
import com.dmsoft.firefly.plugin.tm.csvresolver.utils.ResourceMassages;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.plugin.Plugin;
import com.dmsoft.firefly.sdk.plugin.PluginImageContext;
import com.dmsoft.firefly.sdk.ui.MenuBuilder;
import com.dmsoft.firefly.sdk.ui.PluginUIContext;
import com.dmsoft.firefly.sdk.utils.enums.InitModel;
import com.dmsoft.firefly.plugin.tm.csvresolver.utils.CsvFxmlAndLanguageUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuItem;
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
public class TMCsvResolverPlugin extends Plugin {
    private static final Logger logger = LoggerFactory.getLogger(TMCsvResolverPlugin.class);

    @Autowired
    private PluginImageContext pluginImageContext;
    @Autowired
    private PluginUIContext pluginUIContext;
    @Override
    public void initialize(InitModel model) {
        this.pluginImageContext.registerPluginInstance("com.dmsoft.dap.TMCsvResolverPlugin", "com.dmsoft.firefly.plugin.tm.csvresolver.service.CsvResolverService", new CsvResolverService());
        this.pluginImageContext.registerPluginInstance("com.dmsoft.dap.TMCsvResolverPlugin", "com.dmsoft.firefly.plugin.tm.csvresolver.service.CsvConfigService", new CsvConfigService());

        logger.info("Plugin-TM-CsvResolver Initialized.");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void start() {
        MenuItem menuItem = new MenuItem(CsvFxmlAndLanguageUtils.getString("MENU_TM_CSV_RESOLVER"));
        menuItem.setId("tmcsvResolver");
        menuItem.setOnAction(event -> build());

        this.pluginUIContext.registerMenu(new MenuBuilder("com.dmsoft.dap.TMCsvResolverPlugin",
                MenuBuilder.MenuType.MENU_ITEM, "tmcsvResolver", MenuBuilder.MENU_PREFERENCE).addMenu(menuItem));

        logger.debug("Plugin-TM-CsvResolver UI register done.");
        logger.info("Plugin-TM-CsvResolver started.");
    }

    private void build(){
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = CsvFxmlAndLanguageUtils.getLoaderFXML("view/csv_resolver.fxml");
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel(ResourceMassages.TM_STAGE_CSV, CsvFxmlAndLanguageUtils.getString("MODEL_TM_TITLE"), root, getClass().getClassLoader().getResource("css/csv_app.css").toExternalForm());
            stage.toFront();
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        System.out.println("Plugin-TM-CsvResolver Destroyed.");
    }

}
