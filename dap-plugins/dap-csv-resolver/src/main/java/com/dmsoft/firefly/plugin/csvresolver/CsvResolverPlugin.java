/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.csvresolver;


import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.plugin.Plugin;
import com.dmsoft.firefly.sdk.plugin.PluginContext;
import com.dmsoft.firefly.sdk.plugin.PluginImageContext;
import com.dmsoft.firefly.sdk.ui.Action;
import com.dmsoft.firefly.sdk.ui.IMainBodyPane;
import com.dmsoft.firefly.sdk.ui.PluginUIContext;
import com.dmsoft.firefly.sdk.ui.window.WindowPane;
import com.dmsoft.firefly.sdk.utils.enums.InitModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ResourceBundle;

/**
 * spc plugin
 */
public class CsvResolverPlugin extends Plugin {
    private static final Logger logger = LoggerFactory.getLogger(CsvResolverPlugin.class);

    @Override
    public void initialize(InitModel model) {
        RuntimeContext.getBean(PluginImageContext.class).registerPluginInstance("com.dmsoft.dap.CsvResolverPlugin", "CsvResolverService", new CsvResolverService());
        logger.info("Plugin-CsvResolver Initialized.");
    }

    @Override
    public void start() {
        RuntimeContext.getBean(PluginUIContext.class).registerMenu("csvresolver", new Action() {

            @Override
            public void handleEvent(ActionEvent event) {
                Pane root = null;
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("view/csv_resolver.fxml"), ResourceBundle.getBundle("i18n.message_en_US"));
                    fxmlLoader.setClassLoader(RuntimeContext.getBean(PluginContext.class).getDAPClassLoader("com.dmsoft.dap.CsvResolverPlugin"));
                    root = fxmlLoader.load();
                    Stage dialog = new Stage();
                    WindowPane windowPane = new WindowPane(dialog, "CSV-Resolver", root);

                    Scene scene =  new Scene(windowPane, 845, 565);
                    windowPane.setMinWidth(845);
                    windowPane.setMinHeight(565);
                    scene.setFill(Color.TRANSPARENT);
                    scene.getStylesheets().add(getClass().getClassLoader().getResource("css/app.css").toExternalForm());

                    dialog.initStyle(StageStyle.TRANSPARENT);
                    dialog.setScene(scene);
                    windowPane.initEvent();
                    dialog.show();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        logger.debug("Plugin-CsvResolver UI register done.");

        logger.info("Plugin-CsvResolver started.");
    }

    @Override
    public void destroy() {
        System.out.println("Plugin-CsvResolver Destroyed.");
    }

}
