package com.dmsoft.firefly.gui;

import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


/**
 * Created by QiangChen on 2017/4/8.
 */

public class SystemStartUpProcessorBarController {
    private static final String LOGOPATH = "/images/initialize_logo.png";

    @FXML
    private ProgressBar progressBar;

    @FXML
    private ImageView imageViewLogo;

    @FXML
    private void initialize() {
        imageViewLogo.setImage(new Image(LOGOPATH));
        progressBar.setProgress(0);
        System.out.println("init process");
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }
}