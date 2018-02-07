/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.plugin.spc.utils.ImageUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

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
        this.initBtnIcon();
        this.initComponentEvent();
    }

    private void initComponentEvent(){
        resetBtn.setOnAction(event -> getResetBtnEvent());
        printBtn.setOnAction(event -> getPrintBtnEvent());
        exportBtn.setOnAction(event -> getExportBtnEvent());
        exportBtn.setOnAction(event -> getChooseBtnEvent());
    }

    private void getResetBtnEvent(){

    }

    private void getPrintBtnEvent(){

    }

    private void getExportBtnEvent(){

    }

    private void getChooseBtnEvent(){

    }

    private void initBtnIcon(){
        resetBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_reset_normal.png")));
        printBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_print_normal.png")));
        exportBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_export_normal.png")));
        chooseBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/icon_choose_one_white.png")));
    }
}
