/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.csvresolver;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ResourceBundle;

/**
 * Created by Ethan.Yang on 2018/1/29.
 */
public class TestApplication extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //Parent root = FXMLLoader.load(getClass().getResource("view/main.fxml"));
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("view/csv_resolver.fxml"), ResourceBundle.getBundle("i18n.message_en_US"));
        //root.getStylesheets().add("/csv_app.css");

        Scene scene = new Scene(root, 1280, 704);
//        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/csv_app.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}