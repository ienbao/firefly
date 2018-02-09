package com.dmsoft.firefly.gui.controller;

import com.dmsoft.bamboo.common.utils.base.Platforms;
import com.dmsoft.bamboo.common.utils.base.PropertiesUtil;
import com.dmsoft.firefly.gui.utils.PropertiesResource;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.ui.MenuComponent;
import com.dmsoft.firefly.sdk.ui.PluginUIContext;
import com.dmsoft.firefly.sdk.utils.enums.MenuType;
import com.google.common.collect.Lists;
import de.codecentric.centerdevice.MenuToolkit;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class AppController {
    private final Logger logger = LoggerFactory.getLogger(AppController.class);

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
        initMenuBar();
    }

    private void initMenuBar() {
        PluginUIContext pc = RuntimeContext.getBean(PluginUIContext.class);
        Set<String> names = pc.getAllMenuLocations();
        List<String> secondNames = Lists.newLinkedList();
        names.forEach(name -> {
            MenuComponent menu = pc.getMenu(name);
            String parentLocation = menu.getParentLocation();
            if (StringUtils.isBlank(parentLocation)) {
                if (MenuType.MENU.equals(menu.getMenuType())) {
                    mnuSystem.getMenus().add(menu.getMenu());
                } else {
                    logger.debug("TMenu bar can not set menu item, only set menu.");
                }
            } else {
                secondNames.add(name);
            }
        });
        List<String> thirdNames = Lists.newLinkedList();
        secondNames.forEach(name -> {
            MenuComponent menu = pc.getMenu(name);
            String parentLocation = menu.getParentLocation();
            MenuComponent parentMenuComponent = pc.getMenu(parentLocation);
            if (parentMenuComponent == null) {
                logger.debug(" The parent menu does not exist .");
            } else if (MenuType.MENU.equals(menu.getMenuType())) {
                List<Menu> menus = mnuSystem.getMenus();
                for (Menu menu1 : menus) {
                    boolean result = updateMenu(parentLocation, menu, menu1);
                    if (result) {
                        break;
                    }
                }
            } else {
                thirdNames.add(name);
            }
        });

        thirdNames.forEach(name -> {
            MenuComponent menu = pc.getMenu(name);
            String parentLocation = menu.getParentLocation();
            MenuComponent parentMenuComponent = pc.getMenu(parentLocation);
            if (parentMenuComponent == null) {
                logger.debug(" The parent menu does not exist .");
            }  else {
                List<Menu> menus = mnuSystem.getMenus();
                for (Menu menu1 : menus) {
                    boolean result = updateMenu(parentLocation, menu, menu1);
                    if (result) {
                        break;
                    }
                }
            }
        });
    }


    private boolean updateMenu(String location, MenuComponent menuComponent, Menu menu) {
        AtomicBoolean result = new AtomicBoolean(false);
        if (StringUtils.isNotBlank(menu.getId()) && menu.getId().equals(location)) {
            menu.getItems().add(menuComponent.getMenu());
            result.set(true);
        } else {
            ObservableList<MenuItem> dd = menu.getItems();
            for (MenuItem menuItem : dd) {
                if (menuItem instanceof Menu) {
                    Menu menu2 = (Menu) menuItem;
                    if (StringUtils.isNotBlank(menu2.getId()) && menu2.getId().equals(location)) {
                        menu2.getItems().add(menuComponent.getMenu());
                        result.set(true);
                    }
                }
            }
        }
        return result.get();
    }

}
