package com.dmsoft.firefly.gui.utils;

import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.window.*;
import com.dmsoft.firefly.gui.controller.AppController;
import com.dmsoft.firefly.gui.controller.MainController;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.ui.MenuBuilder;
import com.dmsoft.firefly.sdk.ui.PluginUIContext;
import com.dmsoft.firefly.sdk.utils.enums.LanguageType;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


import static com.google.common.io.Resources.getResource;

public class MenuFactory {
    private static MainController mainController;
    private static AppController appController;
    private static EnvService envService = RuntimeContext.getBean(EnvService.class);

    public final static String ROOT_MENU = "root";
    public final static String PLATFORM_ID = "Platform";

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
        MenuItem importMenuItem = new MenuItem(GuiFxmlAndLanguageUtils.getString("MENU_IMPORT_SETTING"));
        MenuItem exportMenuItem = new MenuItem(GuiFxmlAndLanguageUtils.getString("MENU_EXPORT_SETTING"));
        MenuItem restoreMenuItem = new MenuItem(GuiFxmlAndLanguageUtils.getString("MENU_RESTORE_SETTING"));
        MenuItem exitMenuItem = new MenuItem(GuiFxmlAndLanguageUtils.getString("MENU_EXIT"));
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

       /* Label labelIcon = new Label(GuiFxmlAndLanguageUtils.getString("MENU_DATA_SOURCE_SETTING_QUICK"));
        labelIcon.setContentDisplay(ContentDisplay.RIGHT);
        labelIcon.setAlignment(Pos.CENTER_RIGHT);
        Label label = new Label();
        label.setGraphic(labelIcon);
        label.setText(GuiFxmlAndLanguageUtils.getString("MENU_DATA_SOURCE_SETTING"));
        label.setContentDisplay(ContentDisplay.RIGHT);*/

        MenuItem dataSourceSettingMenuItem = new MenuItem(GuiFxmlAndLanguageUtils.getString("MENU_DATA_SOURCE_SETTING"));

        MenuItem analysisMenuItem = new MenuItem(GuiFxmlAndLanguageUtils.getString("MENU_ANALYSIS_TEMPLATE"));
        analysisMenuItem.setOnAction(event -> buildTemplateDia());

        MenuItem pluginMenuItem = new MenuItem(GuiFxmlAndLanguageUtils.getString("MENU_PLUGIN_MANAGER"));
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
        if (envService.getLanguageType().equals(LanguageType.ZH)) {
            zh.setSelected(true);
        } else {
            en.setSelected(true);
        }
        en.setOnAction(event -> {
            if (en.isSelected()) {
                envService.setLanguageType(LanguageType.EN);
                initMenu();
                appController.resetMenu();
                mainController.resetMain();
                StageMap.getAllStage().clear();
            }
        });
        zh.setOnAction(event -> {
            if (zh.isSelected()) {
                envService.setLanguageType(LanguageType.ZH);
                initMenu();
                appController.resetMenu();
                mainController.resetMain();
                StageMap.getAllStage().clear();
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
        MenuItem dapMenuItem = new MenuItem(GuiFxmlAndLanguageUtils.getString("MENU_ABOUT_DAP"));
        MenuItem updateMenuItem = new MenuItem(GuiFxmlAndLanguageUtils.getString("MENU_CHECK_UPDATE"));

        dapMenuItem.setOnAction(event -> {
            System.out.println("dap");
        });

        updateMenuItem.setOnAction(event -> {
            System.out.println("update");
        });
        menu.getItems().add(legalMenuItem);
        menu.getItems().add(dapMenuItem);
        menu.getItems().add(updateMenuItem);
        return getParentMenuBuilder().setParentLocation(ROOT_MENU).addMenu(menu);
    }

    private static void buildTemplateDia() {
        Pane root = null;
        try {
            //root = FXMLLoader.load(GuiApplication.class.getClassLoader().getResource("view/template.fxml"), ResourceBundle.getBundle("i18n.message_en_US_GUI"));
            FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/template.fxml");
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("template", GuiFxmlAndLanguageUtils.getString(ResourceMassages.TEMPLATE), root, getResource("css/platform_app.css").toExternalForm());
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void buildeSettingExportDia() {
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/export_setting.fxml");
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("exportSetting", GuiFxmlAndLanguageUtils.getString(ResourceMassages.EXPORTSETTING), root, getResource("css/platform_app.css").toExternalForm());
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
