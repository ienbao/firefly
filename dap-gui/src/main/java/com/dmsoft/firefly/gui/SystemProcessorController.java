package com.dmsoft.firefly.gui;

import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by QiangChen on 2017/4/8.
 */

public class SystemProcessorController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemProcessorController.class);
    @FXML
    private ProgressBar progressBar;

    @FXML
    private void initialize() {
        LOGGER.debug("The processor bar is start.");
        progressBar.setProgress(0);
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }
}
