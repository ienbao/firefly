package com.dmsoft.firefly.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GuiApplication extends Application{

    static {
        System.getProperties().put("javafx.pseudoClassOverrideEnabled", "true");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        //Parent root = FXMLLoader.load(getClass().getResource("view/main.fxml"));
        Parent root = FXMLLoader.load(GuiApplication.class.getClassLoader().getResource("view/main.fxml"));
        //root.getStylesheets().add("/main.css");

        Scene scene = new Scene(root,1000,600);
//        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/demo.css").toExternalForm());
//        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
