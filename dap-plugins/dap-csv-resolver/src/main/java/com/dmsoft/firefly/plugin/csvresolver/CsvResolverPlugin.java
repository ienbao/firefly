/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.csvresolver;


import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.plugin.csvresolver.service.CsvResolverService;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.plugin.Plugin;
import com.dmsoft.firefly.sdk.plugin.PluginImageContext;
import com.dmsoft.firefly.sdk.ui.MenuBuilder;
import com.dmsoft.firefly.sdk.ui.PluginUIContext;
import com.dmsoft.firefly.sdk.utils.enums.InitModel;
import com.dmsoft.firefly.plugin.csvresolver.utils.CsvFxmlAndLanguageUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * spc plugin
 */
public class CsvResolverPlugin extends Plugin {
    private static final Logger logger = LoggerFactory.getLogger(CsvResolverPlugin.class);

    @Override
    public void initialize(InitModel model) {
        RuntimeContext.getBean(PluginImageContext.class).registerPluginInstance("com.dmsoft.dap.CsvResolverPlugin", "com.dmsoft.firefly.plugin.csvresolver.service.CsvResolverService", new CsvResolverService());
        logger.info("Plugin-CsvResolver Initialized.");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void start() {
        MenuItem menuItem = new MenuItem("CsvResolver");
        menuItem.setId("csvResolver");
        menuItem.setOnAction(event -> build());

        RuntimeContext.getBean(PluginUIContext.class).registerMenu(new MenuBuilder("com.dmsoft.dap.CsvResolverPlugin",
                MenuBuilder.MenuType.MENU_ITEM, "csvResolver", MenuBuilder.MENU_DATASOURCE_RESOLVER).addMenu(menuItem));

        logger.debug("Plugin-CsvResolver UI register done.");
        logger.info("Plugin-CsvResolver started.");
    }

    private void build(){
        Pane root = null;
        try {

            /*FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("view/csv_resolver.fxml"), ResourceBundle.getBundle("i18n.message_en_US"));
            fxmlLoader.setClassLoader(RuntimeContext.getBean(PluginContext.class).getDAPClassLoader("com.dmsoft.dap.CsvResolverPlugin"));*/
            FXMLLoader fxmlLoader = CsvFxmlAndLanguageUtils.getLoaderFXML("view/csv_resolver.fxml");
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createSimpleWindowAsModel("csv", "CSV-Resolver", root, getClass().getClassLoader().getResource("css/csv_app.css").toExternalForm());
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        System.out.println("Plugin-CsvResolver Destroyed.");
    }

}
