package com.dmsoft.firefly.gui.controller;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.gui.components.utils.JsonFileUtil;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.model.UserModel;
import com.dmsoft.firefly.gui.utils.GuiConst;
import com.dmsoft.firefly.gui.utils.GuiFxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.utils.MenuFactory;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.plugin.PluginClass;
import com.dmsoft.firefly.sdk.plugin.PluginClassType;
import com.dmsoft.firefly.sdk.plugin.PluginImageContext;
import com.dmsoft.firefly.sdk.plugin.apis.IConfig;
import com.dmsoft.firefly.sdk.ui.IMenu;
import com.dmsoft.firefly.sdk.ui.PluginUIContext;
import com.google.common.collect.Lists;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.apache.commons.lang3.StringUtils;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.dmsoft.firefly.sdk.ui.MenuBuilder.MenuType;

public class AppController {
    private final Logger logger = LoggerFactory.getLogger(AppController.class);

    @FXML
    private GridPane menuPane;
    @FXML
    private MenuBar menuSystem;

    private MenuItem menuLoginOut;
    private MenuItem menuChangePassword;

    @FXML
    private void initialize() {
        updateMenuSystem();
        initMenuBar();
    }

    private void initMainMenuButton() {
        menuPane.getChildren().clear();
        MenuButton loginMenuBtn = new MenuButton();
        UserModel userModel = UserModel.getInstance();
        if (userModel != null && userModel.getUser() != null) {
            loginMenuBtn.setText(userModel.getUser().getUserName());
            menuLoginOut = new MenuItem(GuiFxmlAndLanguageUtils.getString("MENU_LOGIN_OUT"));
            menuChangePassword = new MenuItem(GuiFxmlAndLanguageUtils.getString("MENU_CHANGE_PASSWORD"));
            loginMenuBtn.getItems().addAll(menuChangePassword, menuLoginOut);
            menuPane.addColumn(1, loginMenuBtn);
            initEvent();
        }
    }

    private void initLoginMenuButton() {
        menuPane.getChildren().clear();
        Button loginMenuBtn = new Button();
        loginMenuBtn.getStyleClass().add("btn-txt");
        loginMenuBtn.setText(GuiFxmlAndLanguageUtils.getString("MENU_PLEASE_LOGIN"));
        menuPane.addColumn(1, loginMenuBtn);
        loginMenuBtn.setOnMouseClicked(event -> {
            GuiFxmlAndLanguageUtils.buildLoginDialog();
        });
    }

    public void resetMenu() {
        updateMenuSystem();
        menuPane.addColumn(0, menuSystem);
    }

    public void updateMenuSystem() {
        UserModel userModel = UserModel.getInstance();
        if (userModel != null && userModel.getUser() != null) {
            menuSystem.setDisable(false);
            initMainMenuButton();
        } else {
            menuSystem.setDisable(true);
            initLoginMenuButton();
        }
    }

    private void initEvent() {
        menuChangePassword.setOnAction(event -> {
            GuiFxmlAndLanguageUtils.buildChangePasswordDia();
        });
        menuLoginOut.setOnAction(event -> {
            UserModel.getInstance().setUser(null);
            StageMap.unloadStage(GuiConst.PLARTFORM_STAGE_LOGIN);
            resetMenu();
            MenuFactory.getMainController().resetMain();
            GuiFxmlAndLanguageUtils.buildLoginDialog();
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
        menuPane.addColumn(0, menuSystem);
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


    public void importAllConfig() {
        String str = System.getProperty("user.home");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Spc Config export");
        fileChooser.setInitialDirectory(new File(str));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json")
        );
        Stage fileStage = null;
        File file = fileChooser.showOpenDialog(fileStage);

        if (file != null) {
            String json = JsonFileUtil.readJsonFile(file);
            JsonMapper jsonMapper = JsonMapper.defaultMapper();
            if (StringUtils.isNotEmpty(json)) {
                PluginImageContext pluginImageContext = RuntimeContext.getBean(PluginImageContext.class);
                List<PluginClass> pluginClasses = pluginImageContext.getPluginClassByType(PluginClassType.CONFIG);
                Map<String, String> config = jsonMapper.fromJson(json, Map.class);
                pluginClasses.forEach(v -> {
                    IConfig service = (IConfig) v.getInstance();
                    String name = service.getConfigName();
                    if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(config.get(name))) {
                        service.importConfig(config.get(name).getBytes());
                    }
                });
            }
        }
    }
}
