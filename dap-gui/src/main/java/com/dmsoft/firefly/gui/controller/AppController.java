package com.dmsoft.firefly.gui.controller;

import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.model.UserModel;
import com.dmsoft.firefly.gui.utils.GuiConst;
import com.dmsoft.firefly.gui.utils.GuiFxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.utils.MenuFactory;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.ui.IMenu;
import com.dmsoft.firefly.sdk.ui.PluginUIContext;
import com.google.common.collect.Lists;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.dmsoft.firefly.sdk.ui.MenuBuilder.MenuType;

public class AppController {
    private final Logger logger = LoggerFactory.getLogger(AppController.class);

    @FXML
    private MenuBar menuSystem;

    @FXML
    private MenuItem menuLoginOut;

    @FXML
    private MenuItem menuChangePassword;

    @FXML
    private MenuButton loginMenuBtn;

    @FXML
    private void initialize() {
        updateLoginMenuBtn();
        initMenuBar();
        initEvent();
    }

    public void resetMenu() {
        menuSystem.getMenus().clear();
        initialize();
    }

    public void updateLoginMenuBtn() {
        loginMenuBtn.setText(GuiFxmlAndLanguageUtils.getString("MENU_PLEASE_LOGIN"));
        UserModel userModel = UserModel.getInstance();
        if (userModel != null && userModel.getUser() != null) {
            loginMenuBtn.setText(userModel.getUser().getUserName());
        }
    }

    private void initEvent() {
        menuChangePassword.setOnAction(event -> {
            GuiFxmlAndLanguageUtils.buildChangePasswordDia();
        });
        menuLoginOut.setOnAction(event -> {
            UserModel.getInstance().setUser(null);
            StageMap.unloadStage(GuiConst.PLARTFORM_STAGE_LOGIN);
            GuiFxmlAndLanguageUtils.buildLoginDialog();
            updateLoginMenuBtn();

        });
    }
    private void initMenuBar() {
        PluginUIContext pc = RuntimeContext.getBean(PluginUIContext.class);
        Set<String> names = pc.getAllMenuLocations();
        List<String> secondNames = Lists.newLinkedList();
        List<String> thirdNames = Lists.newLinkedList();
        names.forEach(name -> {
            IMenu menu = pc.getMenu(name);
            String pluginId = menu.getPluginId();
            String parentLocation = pluginId + "_" + menu.getParentLocation();
            if (parentLocation.equals(MenuFactory.getParentMenuId())) {
                if (MenuType.MENU.equals(menu.getMenuType())) {
                    menuSystem.getMenus().add(menu.getMenu());
                } else {
                    logger.debug("TMenu bar can not set menu item, only set menu.");
                }
            } else {
                if (MenuType.MENU.equals(menu.getMenuType())) {
                    List<Menu> menus = menuSystem.getMenus();
                    boolean result1 = false;
                    for (Menu menu1 : menus) {
                         boolean result = updateMenu(menu, menu1);
                        if (result) {
                            result1 = true;
                            break;
                        }
                    }
                    if (!result1) {
                        secondNames.add(name);
                    }
                } else {
                    secondNames.add(name);
                }
            }
        });
        secondNames.forEach(name -> {
            IMenu menu = pc.getMenu(name);
            if (isHasParentMenu(menu, pc)) {
                if (MenuType.MENU.equals(menu.getMenuType())) {
                    List<Menu> menus = menuSystem.getMenus();
                    for (Menu menu1 : menus) {
                        boolean result = updateMenu(menu, menu1);
                        if (result) {
                            break;
                        }
                    }
                } else {
                    thirdNames.add(name);
                }
           }
        });

        thirdNames.forEach(name -> {
            IMenu menu = pc.getMenu(name);
            if (isHasParentMenu(menu, pc)) {
                List<Menu> menus = menuSystem.getMenus();
                for (Menu menu1 : menus) {
                    boolean result = updateMenu(menu, menu1);
                    if (result) {
                        break;
                    }
                }
            }
        });
    }


    private boolean updateMenu(IMenu menuComponent, Menu menu) {
        String parentLocation = menuComponent.getPluginId() + "_" + menuComponent.getParentLocation();
        String PlatformParentLocation = MenuFactory.PLATFORM_ID + "_" + menuComponent.getParentLocation();

        AtomicBoolean result = new AtomicBoolean(false);
        if (StringUtils.isNotBlank(menu.getId()) && (menu.getId().equals(parentLocation) || (menu.getId().equals(PlatformParentLocation)))) {
            menu.getItems().add(menuComponent.getMenu());
            result.set(true);
        } else {
            ObservableList<MenuItem> dd = menu.getItems();
            for (MenuItem menuItem : dd) {
                if (menuItem instanceof Menu) {
                    Menu menu2 = (Menu) menuItem;
                    this.updateMenu(menuComponent, menu2);
                }
            }
        }
        return result.get();
    }

    private boolean isHasParentMenu(IMenu menu, PluginUIContext pc) {
        String parentLocation = menu.getPluginId() + "_" + menu.getParentLocation();
        IMenu parentMenuComponent = pc.getMenu(parentLocation);
        if (parentMenuComponent == null) {
            parentLocation = MenuFactory.PLATFORM_ID + "_" + menu.getParentLocation();
            parentMenuComponent = pc.getMenu(parentLocation);
            if (parentMenuComponent == null) {
                logger.debug(" The parent menu does not exist. parentLocation={}", parentLocation);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }


}
