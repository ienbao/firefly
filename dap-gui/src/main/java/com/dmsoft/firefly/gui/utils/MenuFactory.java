package com.dmsoft.firefly.gui.utils;

import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.window.*;
import com.dmsoft.firefly.gui.controller.AppController;
import com.dmsoft.firefly.gui.controller.MainController;
import com.dmsoft.firefly.gui.controller.template.PluginManageController;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.UserPreferenceService;
import com.dmsoft.firefly.sdk.plugin.PluginClass;
import com.dmsoft.firefly.sdk.plugin.PluginClassType;
import com.dmsoft.firefly.sdk.plugin.PluginImageContext;
import com.dmsoft.firefly.sdk.plugin.apis.IConfig;
import com.dmsoft.firefly.sdk.ui.MenuBuilder;
import com.dmsoft.firefly.sdk.ui.PluginUIContext;
import com.dmsoft.firefly.sdk.utils.enums.LanguageType;
import com.google.common.collect.Maps;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;


import java.util.List;
import java.util.Map;

import static com.google.common.io.Resources.getResource;

public class MenuFactory {
    private static MainController mainController;
    private static AppController appController;
    private static EnvService envService = RuntimeContext.getBean(EnvService.class);
    private static UserPreferenceService userPreferenceService = RuntimeContext.getBean(UserPreferenceService.class);

    public final static String ROOT_MENU = "root";
    public final static String PLATFORM_ID = "Platform";
    private static boolean isChangeEnLanguage = true;
    private static boolean isChangeZhLanguage = true;

    public static String getParentMenuId() {
        return PLATFORM_ID + "_" + ROOT_MENU;
    }

    public static MenuBuilder getParentMenuBuilder() {
        return new MenuBuilder(PLATFORM_ID, MenuBuilder.MenuType.MENU);
    }

    public static MenuBuilder getParentMenuItemBuilder() {
        return new MenuBuilder(PLATFORM_ID, MenuBuilder.MenuType.MENU_ITEM);
    }

    public static void initMenu() {
        RuntimeContext.getBean(PluginUIContext.class).registerMenu(createFileMenu());
        RuntimeContext.getBean(PluginUIContext.class).registerMenu(createPreferenceMenu());
        RuntimeContext.getBean(PluginUIContext.class).registerMenu(createHelpMenu());
    }

    private static MenuBuilder createFileMenu() {

        Menu menu = new Menu(GuiFxmlAndLanguageUtils.getString("MENU_FILE"));
        menu.setId(MenuBuilder.MENU_FILE);
        MenuItem selectDataSourceMenuItem = new MenuItem(GuiFxmlAndLanguageUtils.getString("MENU_SELECT_DATA_SOURCE"));
        selectDataSourceMenuItem.setMnemonicParsing(true);
        selectDataSourceMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));
        MenuItem importMenuItem = new MenuItem(GuiFxmlAndLanguageUtils.getString("MENU_IMPORT_SETTING"));
        importMenuItem.setMnemonicParsing(true);
        importMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.I, KeyCombination.SHORTCUT_DOWN));
        MenuItem exportMenuItem = new MenuItem(GuiFxmlAndLanguageUtils.getString("MENU_EXPORT_SETTING"));
        exportMenuItem.setMnemonicParsing(true);
        exportMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.SHORTCUT_DOWN));
        MenuItem restoreMenuItem = new MenuItem(GuiFxmlAndLanguageUtils.getString("MENU_RESTORE_SETTING"));
        restoreMenuItem.setMnemonicParsing(true);
        restoreMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN));
        restoreMenuItem.setOnAction(event -> {
            WindowMessageController controller = WindowMessageFactory.createWindowMessageHasOkAndCancel("Message", GuiFxmlAndLanguageUtils.getString("GLOBAL_RESTORE_SYSTEM"));
            controller.addProcessMonitorListener(new WindowCustomListener() {
                @Override
                public boolean onShowCustomEvent() {
                    return false;
                }

                @Override
                public boolean onCloseAndCancelCustomEvent() {
                    return false;
                }

                @Override
                public boolean onOkCustomEvent() {
                    PluginImageContext pluginImageContext = RuntimeContext.getBean(PluginImageContext.class);
                    List<PluginClass> pluginClasses = pluginImageContext.getPluginClassByType(PluginClassType.CONFIG);
                    Platform.runLater(() -> {
                        StageMap.getAllStage().clear();
                        userPreferenceService.resetPreference();
                        pluginClasses.forEach(v -> {
                            IConfig service = (IConfig) v.getInstance();
                            service.restoreConfig();
                        });
                        envService.setActivatedTemplate(GuiConst.DEFAULT_TEMPLATE_NAME);
                        envService.setActivatedProjectName(null);
                        envService.setTestItems(null);
                        envService.setLanguageType(LanguageType.EN);
                        Runtime.getRuntime().gc();
                        initMenu();
                        appController.resetMenu();
                        mainController.resetMain();
                    });
                    return false;
                }
            });
        });
        MenuItem exitMenuItem = new MenuItem(GuiFxmlAndLanguageUtils.getString("MENU_EXIT"));
        exitMenuItem.setMnemonicParsing(true);
        exitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.SHORTCUT_DOWN));
        exitMenuItem.setOnAction(event -> {
            Stage stage = StageMap.getPrimaryStage(GuiConst.PLARTFORM_STAGE_MAIN);
            stage.close();
        });
        selectDataSourceMenuItem.setOnAction(event -> GuiFxmlAndLanguageUtils.buildSelectDataSource());
        importMenuItem.setOnAction(event -> appController.importAllConfig());
        exportMenuItem.setOnAction(event -> buildeSettingExportDia());
        menu.getItems().add(selectDataSourceMenuItem);
        menu.getItems().add(importMenuItem);
        menu.getItems().add(exportMenuItem);
        menu.getItems().add(restoreMenuItem);
        menu.getItems().add(exitMenuItem);

        return getParentMenuBuilder().setParentLocation(ROOT_MENU).addMenu(menu);
    }

    private static MenuBuilder createPreferenceMenu() {
        Menu menu = new Menu(GuiFxmlAndLanguageUtils.getString("MENU_PREFERENCE"));
        menu.setId(MenuBuilder.MENU_PREFERENCE);
        MenuItem dataSourceSettingMenuItem = new MenuItem(GuiFxmlAndLanguageUtils.getString("MENU_DATA_SOURCE_SETTING"));
        dataSourceSettingMenuItem.setMnemonicParsing(true);
        dataSourceSettingMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.D, KeyCombination.SHORTCUT_DOWN));
        dataSourceSettingMenuItem.setOnAction(event -> buildSourceSettingDia());

        MenuItem analysisMenuItem = new MenuItem(GuiFxmlAndLanguageUtils.getString("MENU_ANALYSIS_TEMPLATE"));
        analysisMenuItem.setMnemonicParsing(true);
        analysisMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.T, KeyCombination.SHORTCUT_DOWN));
        analysisMenuItem.setOnAction(event -> GuiFxmlAndLanguageUtils.buildTemplateDia());

        MenuItem pluginMenuItem = new MenuItem(GuiFxmlAndLanguageUtils.getString("MENU_PLUGIN_MANAGER"));
        pluginMenuItem.setMnemonicParsing(true);
        pluginMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCombination.SHORTCUT_DOWN));
        pluginMenuItem.setOnAction(event -> {
            buildPluginManageDialog();
        });

        menu.getItems().add(dataSourceSettingMenuItem);
        menu.getItems().add(analysisMenuItem);
        menu.getItems().add(pluginMenuItem);
        menu.getItems().add(initLanguageMenu());
        return getParentMenuBuilder().setParentLocation(ROOT_MENU).addMenu(menu);
    }

    private static Menu initLanguageMenu() {
        Menu language = new Menu(GuiFxmlAndLanguageUtils.getString("MENU_LANGUAGE"));
        RadioMenuItem zh = new RadioMenuItem(GuiFxmlAndLanguageUtils.getString("LANGUAGE_ZH"));
        RadioMenuItem en = new RadioMenuItem(GuiFxmlAndLanguageUtils.getString("LANGUAGE_EN"));
        if (LanguageType.ZH.equals(envService.getLanguageType())) {
            zh.setSelected(true);
        } else {
            en.setSelected(true);
        }
        en.selectedProperty().addListener((ov, b1, b2) -> {
           if (b2 && isChangeZhLanguage) {
               WindowMessageController controller = WindowMessageFactory.createWindowMessageHasOkAndCancel("Message", GuiFxmlAndLanguageUtils.getString("GLOBAL_CHANGE_LANGUAGE"));
               controller.addProcessMonitorListener(new WindowCustomListener() {
                    @Override
                    public boolean onShowCustomEvent() {
                        return false;
                    }

                    @Override
                    public boolean onCloseAndCancelCustomEvent() {
                        isChangeEnLanguage = false;
                        zh.setSelected(true);
                        envService.setLanguageType(LanguageType.ZH);
                        return false;
                    }

                    @Override
                    public boolean onOkCustomEvent() {
                        Platform.runLater(() -> {
                            StageMap.getAllStage().clear();
                            isChangeEnLanguage = true;
                            envService.setLanguageType(LanguageType.EN);
                            initMenu();
                            appController.resetMenu();
                            mainController.resetMain();
                        });
                        return false;
                    }
               });
            }
        });

        zh.selectedProperty().addListener((ov, b1, b2) -> {
            if (b2 && isChangeEnLanguage) {
                WindowMessageController controller = WindowMessageFactory.createWindowMessageHasOkAndCancel("Message", GuiFxmlAndLanguageUtils.getString("GLOBAL_CHANGE_LANGUAGE"));
                controller.addProcessMonitorListener(new WindowCustomListener() {
                    @Override
                    public boolean onShowCustomEvent() {
                        return false;
                    }

                    @Override
                    public boolean onCloseAndCancelCustomEvent() {
                        isChangeZhLanguage = false;
                        en.setSelected(true);
                        envService.setLanguageType(LanguageType.EN);
                        return false;
                    }

                    @Override
                    public boolean onOkCustomEvent() {
                        Platform.runLater(() -> {
                            StageMap.getAllStage().clear();
                            isChangeZhLanguage = true;
                            envService.setLanguageType(LanguageType.ZH);
                            initMenu();
                            appController.resetMenu();
                            mainController.resetMain();
                        });

                        return false;
                    }
                });
            }
        });
        ToggleGroup toggleGroup = new ToggleGroup();
        en.setToggleGroup(toggleGroup);
        zh.setToggleGroup(toggleGroup);
        language.getItems().addAll(zh, en);
        return language;
    }

    private static MenuBuilder createHelpMenu() {
        Menu menu = new Menu(GuiFxmlAndLanguageUtils.getString("MENU_HELP"));
        menu.setId(MenuBuilder.MENU_HELP);
        MenuItem legalMenuItem = new MenuItem(GuiFxmlAndLanguageUtils.getString("MENU_LEGAL_NOTICE"));
        legalMenuItem.setOnAction(event -> {
            GuiFxmlAndLanguageUtils.buildLegalDialog();
        });
        MenuItem aboutMenuItem = new MenuItem(GuiFxmlAndLanguageUtils.getString("MENU_ABOUT_DAP"));
        aboutMenuItem.setOnAction(event -> {
            GuiFxmlAndLanguageUtils.buildAboutDialog();
        });
//        MenuItem dapMenuItem = new MenuItem(GuiFxmlAndLanguageUtils.getString("MENU_ABOUT_DAP"));
//        MenuItem updateMenuItem = new MenuItem(GuiFxmlAndLanguageUtils.getString("MENU_CHECK_UPDATE"));

//        dapMenuItem.setOnAction(event -> {
//            System.out.println("dap");
//        });
//
//        updateMenuItem.setOnAction(event -> {
//            System.out.println("update");
//        });
        menu.getItems().add(legalMenuItem);
        menu.getItems().add(aboutMenuItem);
//        menu.getItems().add(dapMenuItem);
//        menu.getItems().add(updateMenuItem);
        return getParentMenuBuilder().setParentLocation(ROOT_MENU).addMenu(menu);
    }

    private static void buildSourceSettingDia(){
        List<String> projectNames= envService.findActivatedProjectName();
        if (projectNames == null || projectNames.isEmpty()) {
           WindowMessageFactory.createWindowMessageHasOk("Message", GuiFxmlAndLanguageUtils.getString("DATA_SOURCE_SETTING_NO_SELECT_FILE"));
        } else {
            Pane root = null;
            try {
                FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/data_source_setting.fxml");
                root = fxmlLoader.load();
                Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("sourceSetting", GuiFxmlAndLanguageUtils.getString(ResourceMassages.SOURCE_SETTING), root, getResource("css/platform_app.css").toExternalForm());
                stage.toFront();
                stage.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private static void buildeSettingExportDia() {
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/export_setting.fxml");
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("exportSetting", GuiFxmlAndLanguageUtils.getString(ResourceMassages.GLOBAL_EXPORT_SETTING), root, getResource("css/platform_app.css").toExternalForm());
            stage.toFront();
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void buildPluginManageDialog() {
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/plugin.fxml");
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("pluginManage", GuiFxmlAndLanguageUtils.getString(ResourceMassages.PLUGIN_MANAGE), root, getResource("css/platform_app.css").toExternalForm());
            PluginManageController controller = fxmlLoader.getController();
            stage.setOnCloseRequest(controller.getOnCloseRequest());
            stage.toFront();
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void setMainController(MainController mainController) {
        MenuFactory.mainController = mainController;
    }

    public static void setAppController(AppController appController) {
        MenuFactory.appController = appController;
    }

    public static MainController getMainController() {
        return mainController;
    }

    public static AppController getAppController() {
        return appController;
    }
}
