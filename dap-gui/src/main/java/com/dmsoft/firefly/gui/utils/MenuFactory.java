package com.dmsoft.firefly.gui.utils;

import com.dmsoft.firefly.gui.GuiApplication;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.ui.MenuBuilder;
import com.dmsoft.firefly.sdk.ui.PluginUIContext;
import com.dmsoft.firefly.sdk.ui.window.WindowPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ResourceBundle;

import static com.google.common.io.Resources.getResource;

public class MenuFactory {

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
        RuntimeContext.getBean(PluginUIContext.class).registerMenu(createDataSourceMenu());
        RuntimeContext.getBean(PluginUIContext.class).registerMenu(createDataSourceResolverMenu());
        RuntimeContext.getBean(PluginUIContext.class).registerMenu(createAnalyseMenu());
        RuntimeContext.getBean(PluginUIContext.class).registerMenu(createPreferenceMenu());
        RuntimeContext.getBean(PluginUIContext.class).registerMenu(createHelpMenu());
    }

    private static MenuBuilder createFileMenu() {

        Menu menu = new Menu("File");
        menu.setId(MenuBuilder.MENU_FILE);
        MenuItem importMenuItem = new MenuItem("Import Settings(I)");
        MenuItem exportMenuItem = new MenuItem("Export Setting(E)");
        MenuItem restoreMenuItem = new MenuItem("Restore Setting(R)");
        MenuItem exitMenuItem = new MenuItem("Exit(X)");

        menu.getItems().add(importMenuItem);
        menu.getItems().add(exportMenuItem);
        menu.getItems().add(restoreMenuItem);
        menu.getItems().add(exitMenuItem);

        return getParentMenuBuilder().setParentLocation(ROOT_MENU).addMenu(menu);
    }

    private static MenuBuilder createDataSourceMenu() {
        Menu menu = new Menu("Data Source");
        menu.setId(MenuBuilder.MENU_DATASOURCE);
        MenuItem menuItem = new MenuItem("Select Data Source(S)");
        menuItem.setId("selectDataSource");
        menu.getItems().add(menuItem);
        return getParentMenuBuilder().setParentLocation(ROOT_MENU).addMenu(menu);
    }

    private static MenuBuilder createDataSourceResolverMenu() {
        Menu menu = new Menu("Resolver");
        menu.setId(MenuBuilder.MENU_DATASOURCE_RESOLVER);
        return getParentMenuBuilder().setParentLocation(MenuBuilder.MENU_DATASOURCE).addMenu(menu);
    }

    private static MenuBuilder createAnalyseMenu() {
        Menu menu = new Menu("Analyze(A)");
        menu.setId(MenuBuilder.MENU_ANALYSE);
        MenuItem analysisMenuItem = new MenuItem("Analysis Templete(A)");
        analysisMenuItem.setOnAction(event -> buildTemplateDia());
        menu.getItems().add(analysisMenuItem);
        return getParentMenuBuilder().setParentLocation(ROOT_MENU).addMenu(menu);
    }

    private static MenuBuilder createPreferenceMenu() {
        Menu menu = new Menu("Preference(P)");
        menu.setId(MenuBuilder.MENU_PREFERENCE);
        MenuItem importMenuItem = new MenuItem("Plugin-Manager(P)");
        Menu language = new Menu("Language");
        RadioMenuItem ch = new RadioMenuItem("中文");
        RadioMenuItem en = new RadioMenuItem("English");
        language.getItems().addAll(ch, en);

        menu.getItems().add(importMenuItem);
        menu.getItems().add(language);
        return getParentMenuBuilder().setParentLocation(ROOT_MENU).addMenu(menu);
    }

    private static MenuBuilder createHelpMenu() {
        Menu menu = new Menu("Help(H)");
        menu.setId(MenuBuilder.MENU_HELP);
        MenuItem legalMenuItem = new MenuItem("Legal Notice");
        MenuItem dapMenuItem = new MenuItem("About DAP");
        MenuItem updateMenuItem = new MenuItem("Check Update");

        menu.getItems().add(legalMenuItem);
        menu.getItems().add(dapMenuItem);
        menu.getItems().add(updateMenuItem);
        return getParentMenuBuilder().setParentLocation(ROOT_MENU).addMenu(menu);
    }

    private static void buildTemplateDia() {
        Pane root = null;
        try {
            root = FXMLLoader.load(GuiApplication.class.getClassLoader().getResource("view/template.fxml"), ResourceBundle.getBundle("i18n.message_en_US_GUI"));
            Stage dialog = new Stage();
            WindowPane windowPane = new WindowPane(dialog, ResourceBundleUtils.getString(ResourceMassages.TEMPLATE), root);

            Scene scene = new Scene(windowPane, 825, 595);
            windowPane.setMinSize(825, 595);
            scene.setFill(Color.TRANSPARENT);
            scene.getStylesheets().add(getResource("css/app.css").toExternalForm());

            dialog.initStyle(StageStyle.TRANSPARENT);
            dialog.setScene(scene);
            windowPane.init();
            dialog.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
