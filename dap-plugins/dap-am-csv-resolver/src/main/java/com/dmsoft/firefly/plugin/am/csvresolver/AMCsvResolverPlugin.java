/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.am.csvresolver;


import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.plugin.am.csvresolver.service.CsvConfigService;
import com.dmsoft.firefly.plugin.am.csvresolver.service.CsvResolverService;
import com.dmsoft.firefly.plugin.am.csvresolver.utils.CsvFxmlAndLanguageUtils;
import com.dmsoft.firefly.plugin.am.csvresolver.utils.ResourceMassages;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.plugin.Plugin;
import com.dmsoft.firefly.sdk.plugin.PluginImageContext;
import com.dmsoft.firefly.sdk.ui.MenuBuilder;
import com.dmsoft.firefly.sdk.ui.PluginUIContext;
import com.dmsoft.firefly.sdk.utils.enums.InitModel;
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
public class AMCsvResolverPlugin extends Plugin {
    private static final Logger logger = LoggerFactory.getLogger(AMCsvResolverPlugin.class);

    @Autowired
    private PluginImageContext pluginImageContext;
    @Autowired
    private PluginUIContext pluginUIContext;

    @Override
    public void initialize(InitModel model) {
        this.pluginImageContext.registerPluginInstance("com.dmsoft.dap.AMCsvResolverPlugin", "com.dmsoft.firefly.plugin.am.csvresolver.service.CsvResolverService", new CsvResolverService());
        this.pluginImageContext.registerPluginInstance("com.dmsoft.dap.AMCsvResolverPlugin", "com.dmsoft.firefly.plugin.am.csvresolver.service.CsvConfigService", new CsvConfigService());

        logger.info("Plugin-CsvResolver Initialized.");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void start() {
        MenuItem menuItem = new MenuItem(CsvFxmlAndLanguageUtils.getString("MENU_AM_CSV_RESOLVER"));
        menuItem.setId("amcsvResolver");
        menuItem.setOnAction(event -> build());

        this.pluginUIContext.registerMenu(new MenuBuilder("com.dmsoft.dap.AMCsvResolverPlugin",
                MenuBuilder.MenuType.MENU_ITEM, "amcsvResolver", MenuBuilder.MENU_PREFERENCE).addMenu(menuItem));

        logger.debug("Plugin-AM-CsvResolver UI register done.");
        logger.info("Plugin-AM-CsvResolver started.");
    }

    private void build(){
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = CsvFxmlAndLanguageUtils.getLoaderFXML("view/csv_resolver.fxml");
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel(ResourceMassages.AM_STAGE_CSV, CsvFxmlAndLanguageUtils.getString("MODEL_AM_TITLE"), root, getClass().getClassLoader().getResource("css/csv_app.css").toExternalForm());
            stage.toFront();
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        System.out.println("Plugin-AM-CsvResolver Destroyed.");
    }

}
