package com.dmsoft.firefly.gui;

import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.gui.utils.GuiFxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.utils.ResourceMassages;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.swing.*;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;

import static com.google.common.io.Resources.getResource;

/**
 * Created by QiangChen on 2017/4/8.
 */

public class SystemStartUpProcessorBarController {
    private static final String BGPATH = "/images/initialize_bg.png";
    private static final String LOGOPATH = "/images/initialize_logo.png";

    @FXML
    private ProgressBar progrossBar;

    @FXML
    private ImageView imageViewLogo;

    @FXML
    private void initialize() {
        imageViewLogo.setImage(new Image(LOGOPATH));
        progrossBar.setProgress(0);
        System.out.println("init process");
    }

    public ProgressBar getProgrossBar() {
        return progrossBar;
    }
}