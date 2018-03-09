package com.dmsoft.firefly.gui.components.Window;

import com.dmsoft.firefly.gui.components.utils.FxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.components.window.SimpleMessageController;
import com.dmsoft.firefly.gui.components.window.SimpleWindowFactory;
import com.dmsoft.firefly.sdk.ui.MenuBuilder;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class SimpleWindowFactoryTest extends Application{
    static {
        FxmlAndLanguageUtils.isDebug = true;
    }

    @Override
    public void start(Stage primaryStage) {
        Menu menu = new Menu("information");
        menu.setId(MenuBuilder.MENU_HELP);
        MenuItem okMenuItem = new MenuItem("OK");
        okMenuItem.setOnAction(event -> {
            SimpleMessageController simpleMessageController = SimpleWindowFactory.createSimpleMessageNoBtn("Message", "fdsffdsfsfsfsfsfsdfs fdsfsffdsfdf sfdsfds fsfdsfsfdsfdsfdsfsdfsfsdfsdf");
            simpleMessageController.showOk();
        });
        MenuItem okAndBtnMenuItem = new MenuItem("OKAndBtn");
        okAndBtnMenuItem.setOnAction(event -> {
            SimpleMessageController simpleMessageController = SimpleWindowFactory.createSimpleMessage("Message", "fdsffdsfsfsfsfsfsdfs fdsfsffdsfdf sfdsfds fsfdsfsfdsfdsfdsfsdfsfsdfsdf");
            simpleMessageController.showOk();
        });

        MenuItem cancelMenuItem = new MenuItem("Cancel");
        MenuItem okAndCancelMenuItem = new MenuItem("OKAndCancel");

        cancelMenuItem.setOnAction(event -> {
            SimpleMessageController simpleMessageController = SimpleWindowFactory.createSimpleMessageNoBtn("Message", "fdsffdsfsfsfsfsfsdfs fdsfsffdsfdf sfdsfds fsfdsfsfdsfdsfdsfsdfsfsdfsdf");
            simpleMessageController.showCancel();
        });

        okAndCancelMenuItem.setOnAction(event -> {
            SimpleMessageController simpleMessageController = SimpleWindowFactory.createSimpleMessage("Message", "fdsffdsfsfsfsfsfsdfs fdsfsffdsfdf sfdsfds fsfdsfsfdsfdsfdsfsdfsfsdfsdf");
            simpleMessageController.showOKAndCancel();
            simpleMessageController.getOk().setOnAction(event1 -> {
                System.out.println("fdsf");
            });
        });

        menu.getItems().add(okMenuItem);
        menu.getItems().add(okAndBtnMenuItem);
        menu.getItems().add(cancelMenuItem);
        menu.getItems().add(okAndCancelMenuItem);
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(menu);
        Scene scene = new Scene(menuBar, 1000, 250, Color.WHITE);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }


}
