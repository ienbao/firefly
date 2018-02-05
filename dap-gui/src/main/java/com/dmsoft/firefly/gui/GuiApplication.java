package com.dmsoft.firefly.gui;

import com.dmsoft.firefly.core.DAPApplication;
import com.dmsoft.firefly.gui.component.WindowPane;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.plugin.PluginImageContext;
import com.google.common.collect.Lists;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.AbstractList;

public class GuiApplication extends Application {

    static {
        System.getProperties().put("javafx.pseudoClassOverrideEnabled", "true");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        DAPApplication.run(Lists.newArrayList("com.dmsoft.dap.SpcPlugin",  "com.dmsoft.dap.GrrPlugin"));

        //Parent root = FXMLLoader.load(getClass().getResource("view/main.fxml"));
        Pane root = FXMLLoader.load(GuiApplication.class.getClassLoader().getResource("view/app_menu.fxml"));
        Pane main = FXMLLoader.load(GuiApplication.class.getClassLoader().getResource("view/main.fxml"));

        WindowPane windowPane = new WindowPane(primaryStage, root, main);

        Scene scene = new Scene(windowPane, 1000, 600);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/app.css").toExternalForm());
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);

        windowPane.initEvent();
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
