/*
 * Copyright (c) 2017. For Intelligent Group.
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Ethan.Yang on 2018/1/29.
 */
public class TestApplication extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        //Parent root = FXMLLoader.load(getClass().getResource("view/main.fxml"));
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("view/spc.fxml"),ResourceBundle.getBundle("i18n.message_en_US"));
        //root.getStylesheets().add("/main.css");

        Scene scene = new Scene(root,1280,704);
//        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/main.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}