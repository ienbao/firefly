package com.dmsoft.firefly.plugin.grr.controller;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

/**
 * Created by GuangLi on 2018/3/6.
 */
public class GrrSettingController {
    @FXML
    private Label defaultSetting;
    @FXML
    private Label alarmSetting;
    @FXML
    private Label exportSetting;

    @FXML
    private void initialize() {
        defaultSetting.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                defaultSetting.setStyle("-fx-background-color: #FFFFFF");
                alarmSetting.setStyle("-fx-background-color: #FOFOFO");
                exportSetting.setStyle("-fx-background-color: #FOFOFO");
            }
        });
        alarmSetting.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                defaultSetting.setStyle("-fx-background-color: #FOFOFO");
                alarmSetting.setStyle("-fx-background-color: #FFFFFF");
                exportSetting.setStyle("-fx-background-color: #FOFOFO");
            }
        });
        exportSetting.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                defaultSetting.setStyle("-fx-background-color: #FOFOFO");
                alarmSetting.setStyle("-fx-background-color: #FOFOFO");
                exportSetting.setStyle("-fx-background-color: #FFFFFF");
            }
        });
    }

}
