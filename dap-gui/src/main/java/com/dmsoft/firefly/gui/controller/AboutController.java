/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.gui.controller;

import com.dmsoft.firefly.gui.viewmodel.AboutVm;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 *
 * @author Ethan.Yang
 * @date 2018/4/11
 */
@Component
public class AboutController {

    @FXML
    private Label versionLabel;

    @FXML
    private Label osLabel;

    private AboutVm aboutVm;

    @FXML
    private void initialize() {
        this.aboutVm = new AboutVm();
        versionLabel.setText(aboutVm.getVersion());
        osLabel.setText(aboutVm.getOsName());
    }
}
