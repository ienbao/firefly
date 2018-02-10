/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Ethan.Yang on 2018/2/2.
 */
public class ChartResultController implements Initializable {

    private SpcMainController spcMainController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void init(SpcMainController spcMainController) {
        this.spcMainController = spcMainController;
    }
}
