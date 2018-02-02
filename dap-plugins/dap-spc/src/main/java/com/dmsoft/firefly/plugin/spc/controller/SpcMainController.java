/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;

/**
 * Created by Ethan.Yang on 2018/2/2.
 */
public class SpcMainController {

    @FXML
    private Button resetBtn;
    @FXML
    private Button printBtn;
    @FXML
    private Button exportBtn;
    @FXML
    private Button chooseBtn;

    @FXML
    private void initialize(){
        initBtnIcon();
    }

    private void initBtnIcon(){
        ImageView imageReset = new ImageView(new Image(getClass().getResourceAsStream("/images/btn_reset_normal.png")));
        imageReset.setFitHeight(16);
        imageReset.setFitWidth(16);
        resetBtn.setGraphic(imageReset);
    }
}
