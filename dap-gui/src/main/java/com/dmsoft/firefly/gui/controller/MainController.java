package com.dmsoft.firefly.gui.controller;

import com.dmsoft.bamboo.common.utils.base.Platforms;
import com.dmsoft.bamboo.common.utils.base.PropertiesUtil;
import com.dmsoft.firefly.gui.utils.PropertiesResource;
import de.codecentric.centerdevice.MenuToolkit;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;

import java.util.Properties;

public class MainController {

    @FXML
    private MenuBar mnuSystem;

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

    }
}
