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
public class StatisticalResultController {
    @FXML
    private Button chooseColumnBtn;

    @FXML
    private void initialize(){
        initBtnIcon();
    }

    private void initBtnIcon(){
        chooseColumnBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_choose_test_items_normal.png")));
    }
}
