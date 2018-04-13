/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.gui.controller;

import com.dmsoft.firefly.gui.utils.GuiFxmlAndLanguageUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Properties;

/**
 * Created by Ethan.Yang on 2018/4/11.
 */
public class AboutController {
    private static final String LOGOPATH = "Initialize_logo.png";
    @FXML
    private Label versionLabel;
    @FXML
    private Label osLabel;

    @FXML
    private ImageView imageViewLogo;

    @FXML
    private void initialize() {
        imageViewLogo.setImage(new Image("/images/" + LOGOPATH));
        versionLabel.setText(GuiFxmlAndLanguageUtils.getString("VERSION") + " : " + GuiFxmlAndLanguageUtils.getString("STATE_BAR_VERSION"));
        Properties props=System.getProperties();
        String osName = props.getProperty("os.name");
        osLabel.setText(GuiFxmlAndLanguageUtils.getString("OPERATION_SYSTEM") + " : " + osName);
    }
}
