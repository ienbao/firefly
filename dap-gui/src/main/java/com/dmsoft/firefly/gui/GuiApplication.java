package com.dmsoft.firefly.gui;

import com.dmsoft.firefly.gui.view.WindowPane;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GuiApplication extends Application {

    static {
        System.getProperties().put("javafx.pseudoClassOverrideEnabled", "true");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        //Parent root = FXMLLoader.load(getClass().getResource("view/main.fxml"));
        Pane root = FXMLLoader.load(GuiApplication.class.getClassLoader().getResource("view/main_menu.fxml"));

        WindowPane windowPane = new WindowPane(primaryStage, root, null);

        Scene scene = new Scene(windowPane, 1000, 600);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/demo.css").toExternalForm());
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);

        windowPane.initEvent();
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
