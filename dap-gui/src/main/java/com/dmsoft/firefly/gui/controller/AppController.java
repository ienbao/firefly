package com.dmsoft.firefly.gui.controller;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.gui.components.utils.JsonFileUtil;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.gui.utils.GuiFxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.utils.MenuFactory;
import com.dmsoft.firefly.gui.utils.ResourceMassages;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.plugin.PluginClass;
import com.dmsoft.firefly.sdk.plugin.PluginClassType;
import com.dmsoft.firefly.sdk.plugin.PluginImageContext;
import com.dmsoft.firefly.sdk.plugin.apis.IConfig;
import com.dmsoft.firefly.sdk.plugin.apis.IDataParser;
import com.dmsoft.firefly.sdk.ui.IMenu;
import com.dmsoft.firefly.sdk.ui.PluginUIContext;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.dmsoft.firefly.sdk.ui.MenuBuilder.MenuType;
import static com.google.common.io.Resources.getResource;

public class AppController {
    private final Logger logger = LoggerFactory.getLogger(AppController.class);

    @FXML
    private MenuBar menuSystem;

    @FXML
    private MenuItem menuLoginOut;

    @FXML
    private MenuItem menuChangePassword;

    @FXML
    private void initialize() {
        initMenuBar();
        initEvent();
    }


    private void initEvent() {
        menuChangePassword.setOnAction(event -> {
            buildChangePasswordDia();
        });
        menuLoginOut.setOnAction(event -> {

        });
    }

    public void resetMenu() {
        menuSystem.getMenus().clear();
        initialize();
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

    private void buildChangePasswordDia() {
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/change_password.fxml");
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("platform_gui_change_password", GuiFxmlAndLanguageUtils.getString("CHANGE_PASSWORD"), root, getResource("css/platform_app.css").toExternalForm());
            stage.setResizable(false);
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void exportAllConfig() {
        PluginImageContext pluginImageContext = RuntimeContext.getBean(PluginImageContext.class);
        List<PluginClass> pluginClasses = pluginImageContext.getPluginClassByType(PluginClassType.CONFIG);
//        List<String> name = Lists.newArrayList();
        Map<String, String> config = Maps.newHashMap();
        pluginClasses.forEach(v -> {
            IConfig service = (IConfig) v.getInstance();
//            name.add(((IConfig) v.getInstance()).getConfigName());
            String name = service.getConfigName();
            if (StringUtils.isNotEmpty(name)) {
                config.put(service.getConfigName(), new String(service.exportConfig()));
            }
        });
        String str = System.getProperty("user.home");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Spc Config export");
        fileChooser.setInitialDirectory(new File(str));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json")
        );
        Stage fileStage = null;
        File file = fileChooser.showSaveDialog(fileStage);

        if (file != null) {
            if (config != null) {
                JsonFileUtil.writeJsonFile(config, file);
                logger.debug("Export success");
            }
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
