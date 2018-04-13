package com.dmsoft.firefly.gui.components.searchcombobox;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ComboBoxTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        HBox hBox = new HBox();
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll("每隔5分钟");
        Label label = new Label("每隔5分钟");
        label.setPrefHeight(22);
        Button button = new Button("每隔5分钟");
        hBox.getChildren().addAll(comboBox, label, button);
        hBox.setStyle("-fx-background-color: white");
        Scene scene = new Scene(hBox, 300, 300);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main_cn.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
