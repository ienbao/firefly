/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.grr;


import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.plugin.grr.service.impl.GrrConfigServiceImpl;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.plugin.Plugin;
import com.dmsoft.firefly.sdk.plugin.PluginImageContext;
import com.dmsoft.firefly.sdk.ui.IMainBodyPane;
import com.dmsoft.firefly.sdk.ui.MenuBuilder;
import com.dmsoft.firefly.sdk.ui.PluginUIContext;
import com.dmsoft.firefly.sdk.utils.enums.InitModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * grr plugin
 */
public class GrrPlugin extends Plugin {
    public static final String GRR_PLUGIN_ID = "com.dmsoft.dap.GrrPlugin";
    private static final Logger logger = LoggerFactory.getLogger(GrrPlugin.class);

    @Override
    public void initialize(InitModel model) {
        RuntimeContext.getBean(PluginImageContext.class).registerPluginInstance("com.dmsoft.dap.GrrPlugin", "com.dmsoft.firefly.plugin.grr.service.impl.GrrConfigServiceImpl", new GrrConfigServiceImpl());

        logger.info("Plugin-GRR Initialized.");
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
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return root;
            }

            @Override
            public void reset() {

            }
        });
        logger.debug("Plugin-GRR UI register done.");

        logger.info("Plugin-GRR started.");

        MenuItem menuItem = new MenuItem("Grr Settings");
        menuItem.setId("grrSetting");
        menuItem.setOnAction(event -> build());

        RuntimeContext.getBean(PluginUIContext.class).registerMenu(new MenuBuilder("com.dmsoft.dap.GrrPlugin",
                MenuBuilder.MenuType.MENU_ITEM, "Grr Settings", MenuBuilder.MENU_PREFERENCE).addMenu(menuItem));

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
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
