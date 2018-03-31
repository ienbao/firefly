package com.dmsoft.firefly.gui;

import com.dmsoft.firefly.core.utils.ApplicationPathUtil;
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
    private static final Logger logger = LoggerFactory.getLogger(SystemProcessorController.class);

    private static final String LOGOPATH = "Initialize_logo.png";

    @FXML
    private ProgressBar progressBar;

    @FXML
    private ImageView imageViewLogo;

    @FXML
    private void initialize() {
        logger.debug("The processor bar is start.");
        imageViewLogo.setImage(new Image("/images/" + LOGOPATH));
        progressBar.setProgress(0);
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }
}