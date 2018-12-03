package com.dmsoft.firefly.gui.controller;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.gui.components.utils.JsonFileUtil;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.model.UserModel;
import com.dmsoft.firefly.gui.utils.GuiConst;
import com.dmsoft.firefly.gui.utils.GuiFxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.utils.MenuFactory;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.UserDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.dai.service.TemplateService;
import com.dmsoft.firefly.sdk.event.EventContext;
import com.dmsoft.firefly.sdk.event.EventType;
import com.dmsoft.firefly.sdk.plugin.PluginClass;
import com.dmsoft.firefly.sdk.plugin.PluginClassType;
import com.dmsoft.firefly.sdk.plugin.PluginImageContext;
import com.dmsoft.firefly.sdk.plugin.apis.IConfig;
import com.dmsoft.firefly.sdk.ui.IMenu;
import com.dmsoft.firefly.sdk.ui.PluginUIContext;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.dmsoft.firefly.sdk.ui.MenuBuilder.MenuType;

/**
 * app controller for main pane
 *
 * @author Julia
 */
@Component
public class AppController {
    private final Logger logger = LoggerFactory.getLogger(AppController.class);
    private TemplateService templateService = RuntimeContext.getBean(TemplateService.class);
    @Autowired
    private SourceDataService sourceDataService ;
    @Autowired
    private EnvService envService;

    @FXML
    private MenuBar menuSystem;
    @FXML
    private Button loginMenuBtn;
    @FXML
    private MenuButton menuBtn;
    private MenuItem menuLoginOut;
    private MenuItem menuChangePassword;
    @Autowired
    private PluginUIContext pluginUIContext;
    @Autowired
    private EventContext eventContext;

    @FXML
    private void initialize() {
        resetMenu();
        registEvent();
    }

    /**
     * 注册监听事件
     */
    private void registEvent() {
        eventContext.addEventListener(EventType.SYSTEM_LOGIN_SUCCESS_ACTION, event -> {
            Platform.runLater(() -> {
                UserDto userDto = (UserDto) event.getMsg();
                UserModel.getInstance().setUser(userDto);
                resetMenu();
            });
        });


        eventContext.addEventListener(EventType.PLATFORM_RESET_MAIN, event -> {
            Platform.runLater(() ->{
                resetMenu();
            });
        });

    }

    private void initMainMenuButton() {
        menuBtn.setVisible(true);
        loginMenuBtn.setVisible(false);
        menuBtn.getItems().clear();
        UserModel userModel = UserModel.getInstance();
        if (userModel != null && userModel.getUser() != null) {
            menuBtn.setText(userModel.getUser().getUserName());
            menuLoginOut = new MenuItem(GuiFxmlAndLanguageUtils.getString("MENU_LOGIN_OUT"));
            menuChangePassword = new MenuItem(GuiFxmlAndLanguageUtils.getString("MENU_CHANGE_PASSWORD"));
            menuBtn.getItems().addAll(menuChangePassword, menuLoginOut);
            
        }
    }

    private void initLoginMenuButton() {
        menuBtn.setVisible(false);
        menuBtn.getItems().clear();
        loginMenuBtn.setVisible(true);
        loginMenuBtn.setText(GuiFxmlAndLanguageUtils.getString("MENU_PLEASE_LOGIN"));
        loginMenuBtn.setOnMouseClicked(event -> {
            GuiFxmlAndLanguageUtils.buildLoginDialog();
        });
    }


    /**
     * method to update menu system
     */
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

    /**
     * method to reset menu
     */
    public void resetMenu() {
        menuSystem.getMenus().clear();
        updateMenuSystem();
        initMenuBar();
    }


    private void initEvent() {
        menuChangePassword.setOnAction(event -> {
            GuiFxmlAndLanguageUtils.buildChangePasswordDialog();
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
        Set<String> names = this.pluginUIContext.getAllMenuLocations();
        List<String> secondNames = Lists.newLinkedList();
        List<String> thirdNames = Lists.newLinkedList();
        names.forEach(name -> {
            IMenu menu = this.pluginUIContext.getMenu(name);
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
                    boolean isExist = false;
                    for (Menu tempMenu : menuSystem.getMenus()) {
                        if (isExist) {
                            break;
                        }
                        for (MenuItem menuItem : tempMenu.getItems()) {
                            MenuItem menuItem1 = (MenuItem) menu.getMenu();
                            if (menuItem.getText().equals(menuItem1.getText())) {
                                isExist = true;
                                break;
                            }
                        }
                    }
                    if (!isExist) {
                        secondNames.add(name);
                    }
                }
            }
        });
        secondNames.forEach(name -> {
            IMenu menu = this.pluginUIContext.getMenu(name);
            if (isHasParentMenu(menu, this.pluginUIContext)) {
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
            IMenu menu = this.pluginUIContext.getMenu(name);
            if (isHasParentMenu(menu, this.pluginUIContext)) {
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
        String platformParentLocation = MenuFactory.PLATFORM_ID + "_" + menuComponent.getParentLocation();

        AtomicBoolean result = new AtomicBoolean(false);
        if (StringUtils.isNotBlank(menu.getId()) && (menu.getId().equals(parentLocation) || (menu.getId().equals(platformParentLocation)))) {
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

    /**
     * method import all config
     */
    public void importAllConfig() {
        String str = System.getProperty("user.home");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(GuiFxmlAndLanguageUtils.getString("GLOBAL_TITLE_EXPORT_CONFIG"));
        fileChooser.setInitialDirectory(new File(str));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json")
        );
        File file = fileChooser.showOpenDialog(StageMap.getStage(GuiConst.PLARTFORM_STAGE_MAIN));

        if (file != null) {
            String json = JsonFileUtil.readJsonFile(file);
            JsonMapper jsonMapper = JsonMapper.defaultMapper();
            if (StringUtils.isNotEmpty(json)) {
                PluginImageContext pluginImageContext = RuntimeContext.getBean(PluginImageContext.class);
                List<PluginClass> pluginClasses = pluginImageContext.getPluginClassByType(PluginClassType.CONFIG);
                Map<String, String> config = jsonMapper.fromJson(json, Map.class);
                if (config != null && !config.isEmpty()) {
                    String templateConfigName =templateService.getConfigName();
                    if (DAPStringUtils.isNotBlank(config.get(templateConfigName))) {
                        templateService.importConfig(config.get(templateConfigName).getBytes());
                    }
                    pluginClasses.forEach(v -> {
                        IConfig service = (IConfig) v.getInstance();
                        String name = service.getConfigName();
                        if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(config.get(name))) {
                            service.importConfig(config.get(name).getBytes());
                        }
                    });
                    MenuFactory.getMainController().refreshActiveTemplate();
                }

            }
        }
    }
}
