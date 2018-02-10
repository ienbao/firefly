/*
 * Copyright (c) 2016. For Intelligent Group.
 */
package com.dmsoft.firefly.gui.controller;

import com.dmsoft.firefly.gui.GuiApplication;
import com.dmsoft.firefly.gui.utils.ImageUtils;
import com.dmsoft.firefly.gui.utils.ResourceBundleUtils;
import com.dmsoft.firefly.gui.utils.ResourceMassages;
import com.dmsoft.firefly.sdk.ui.window.WindowPane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ResourceBundle;

import static com.google.common.io.Resources.getResource;

/**
 * Created by Guang.Li on 2018/2/10.
 */
public class TemplateController {
    @FXML
    private Button rename, add, copy, delete, deleteTime, addTime, pattern, addRow, ok, cancel, apply;

    @FXML
    private void initialize() {
        initButton();
        initEvent();
    }

    private void initButton() {
        rename.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_rename_normal.png")));
        add.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_add_normal.png")));
        copy.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_copy_normal.png")));
        delete.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_del_normal.png")));
        deleteTime.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_delete_normal.png")));
        addTime.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_add_normal.png")));
//        pattern.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_add_normal.png")));
        addRow.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_add_normal.png")));

    }

    private void initEvent() {
        pattern.setOnAction(event -> buildPatternDia());
    }

    private void buildPatternDia() {
        Pane root = null;
        try {
            root = FXMLLoader.load(GuiApplication.class.getClassLoader().getResource("view/pattern.fxml"), ResourceBundle.getBundle("i18n.message_en_US_GUI"));
            Stage dialog;
            dialog = new Stage();
            WindowPane windowPane = new WindowPane(dialog, ResourceBundleUtils.getString(ResourceMassages.TIME_PATTERN), root);

            Scene scene = new Scene(windowPane, 430, 380);
            windowPane.setMinSize(430, 380);
            scene.setFill(Color.TRANSPARENT);
            scene.getStylesheets().add(getResource("css/app.css").toExternalForm());

            dialog.initStyle(StageStyle.TRANSPARENT);
            dialog.setScene(scene);
            windowPane.init();
            dialog.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
