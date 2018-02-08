package com.dmsoft.firefly.gui.controller;

import com.dmsoft.bamboo.common.utils.base.Platforms;
import com.dmsoft.bamboo.common.utils.base.PropertiesUtil;
import com.dmsoft.firefly.gui.utils.PropertiesResource;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.ui.Action;
import com.dmsoft.firefly.sdk.ui.PluginUIContext;
import de.codecentric.centerdevice.MenuToolkit;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import org.apache.commons.lang3.StringUtils;

import java.util.Properties;
import java.util.Set;

public class AppController {

    @FXML
    private MenuBar mnuSystem;
    @FXML
    private Menu edit;

    @FXML
    private void initialize() {
        buildSystemMenu();
    }

    private void buildSystemMenu() {
        if (Platforms.IS_MAC_OSX) {
            Properties properties = PropertiesUtil.loadFromFile("classpath://application.properties");
            MenuToolkit tk = MenuToolkit.toolkit();
            Menu defaultApplicationMenu = tk.createDefaultApplicationMenu(properties.get(PropertiesResource.PROJECT_NAME).toString());
            tk.setApplicationMenu(defaultApplicationMenu);

            mnuSystem.setUseSystemMenuBar(true);
            mnuSystem.setPrefWidth(0);
            mnuSystem.setMinWidth(0);
            mnuSystem.setMaxWidth(0);
        }
        initToolBar();
    }

    private void initToolBar() {

        PluginUIContext pc = RuntimeContext.getBean(PluginUIContext.class);
        Set<String> names = pc.getAllMenuLocations();

        names.forEach(name -> {
            Action action = pc.getMenuAction(name);
            String[] s = name.split(",");
            MenuItem menuItem = new MenuItem(s[0]);
            menuItem.setOnAction(event -> action.handleEvent(event));
            edit.getItems().add(menuItem);
        });
    }
}
