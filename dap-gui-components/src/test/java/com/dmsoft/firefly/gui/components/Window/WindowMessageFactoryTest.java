package com.dmsoft.firefly.gui.components.Window;

import com.dmsoft.firefly.gui.components.utils.FxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.components.window.WindowMessageController;
import com.dmsoft.firefly.gui.components.window.WindowMessageFactory;
import com.dmsoft.firefly.gui.components.window.WindowProgressTipController;
import com.dmsoft.firefly.sdk.ui.MenuBuilder;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class WindowMessageFactoryTest extends Application {
    static {
        FxmlAndLanguageUtils.setIsDebug(true);
    }

    @Override
    public void start(Stage primaryStage) {
        Menu menu = new Menu("information");
        menu.setId(MenuBuilder.MENU_HELP);
        MenuItem okMenuItem = new MenuItem("OK");
        okMenuItem.setOnAction(event -> {
            WindowMessageController windowMessageController = WindowMessageFactory.createWindowMessageNoBtn("Message", "fdsffdsfsfsfsfsfsdfs fdsfsffdsfdf sfdsfds fsfdsfsfdsfdsfdsfsdfsfsdfsdf");
            windowMessageController.showOk();
        });
        MenuItem okAndBtnMenuItem = new MenuItem("OKAndBtn");
        okAndBtnMenuItem.setOnAction(event -> {
            WindowMessageController windowMessageController = WindowMessageFactory.createWindowMessage("Message", "fdsffdsfsfsfsfsfsdfs fdsfsffdsfdf sfdsfds fsfdsfsfdsfdsfdsfsdfsfsdfsdf");
            windowMessageController.showOk();
        });

        MenuItem cancelMenuItem = new MenuItem("Cancel");
        MenuItem okAndCancelMenuItem = new MenuItem("OKAndCancel");

        cancelMenuItem.setOnAction(event -> {
            WindowMessageController windowMessageController = WindowMessageFactory.createWindowMessageNoBtn("Message", "fdsffdsfsfsfsfsfsdfs fdsfsffdsfdf sfdsfds fsfdsfsfdsfdsfdsfsdfsfsdfsdf");
            windowMessageController.showCancel();
        });

        okAndCancelMenuItem.setOnAction(event -> {
            WindowMessageController windowMessageController = WindowMessageFactory.createWindowMessage("Message", "fdsffdsfsfsfsfsfsdfs fdsfsffdsfdf sfdsfds fsfdsfsfdsfdsfdsfsdfsfsdfsdf");
            windowMessageController.showOKAndCancel();
        });

        MenuItem progressMenuItem = new MenuItem("Progress");
        progressMenuItem.setOnAction(event -> {
            WindowProgressTipController windowProgressTipController = WindowMessageFactory.createWindowProgressTip("Running Task");
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                windowProgressTipController.updateFailProgress(80, "first commit...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                windowProgressTipController.updateFailProgressNextLine("second commit...");
            }).start();

//            Platform.runLater(() -> {
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                windowProgressTipController.updateFailProgress(80);
//            });

        });

        menu.getItems().add(okMenuItem);
        menu.getItems().add(okAndBtnMenuItem);
        menu.getItems().add(cancelMenuItem);
        menu.getItems().add(okAndCancelMenuItem);
        menu.getItems().add(progressMenuItem);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(menu);
        Scene scene = new Scene(menuBar, 1000, 250, Color.WHITE);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }


}
