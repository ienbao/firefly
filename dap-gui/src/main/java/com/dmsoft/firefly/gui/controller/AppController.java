package com.dmsoft.firefly.gui.controller;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.gui.components.utils.JsonFileUtil;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.model.UserModel;
import com.dmsoft.firefly.gui.utils.GuiConst;
import com.dmsoft.firefly.gui.utils.GuiFxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.utils.MenuFactory;
import com.dmsoft.firefly.gui.view.MenuCreation;
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
import com.dmsoft.firefly.sdk.ui.PluginUIContext;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    @FXML
    private void initialize() {
        resetMenu();
        registEvent();
    }

    /**
     * 注册监听事件
     */
    private void registEvent() {
        EventContext eventContext = RuntimeContext.getBean(EventContext.class);
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
        PluginUIContext pc = RuntimeContext.getBean(PluginUIContext.class);
        MenuCreation menuCreation = new MenuCreation(menuSystem,logger);
        menuCreation.createMenu(pc);
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
