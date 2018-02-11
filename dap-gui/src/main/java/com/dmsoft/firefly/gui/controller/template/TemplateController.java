/*
 * Copyright (c) 2016. For Intelligent Group.
 */
package com.dmsoft.firefly.gui.controller.template;

import com.dmsoft.firefly.gui.GuiApplication;
import com.dmsoft.firefly.gui.utils.ImageUtils;
import com.dmsoft.firefly.gui.utils.ResourceBundleUtils;
import com.dmsoft.firefly.gui.utils.ResourceMassages;
import com.dmsoft.firefly.sdk.ui.window.WindowFactory;
import com.dmsoft.firefly.sdk.ui.window.WindowPane;
import com.dmsoft.firefly.sdk.utils.StageMap;
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
    private Stage dialog;

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
        add.setOnAction(event -> buildAddItemDia());
        ok.setOnAction(event -> {
            StageMap.closeStage("template");

        });
        apply.setOnAction(event -> {

        });
        cancel.setOnAction(event -> {
            StageMap.closeStage("template");
        });
    }

    private void buildPatternDia() {
        Pane root = null;
        try {
            root = FXMLLoader.load(GuiApplication.class.getClassLoader().getResource("view/pattern.fxml"), ResourceBundle.getBundle("i18n.message_en_US_GUI"));
            Stage stage = WindowFactory.createSimpleWindowAsModel("pattern", ResourceBundleUtils.getString(ResourceMassages.TIME_PATTERN), root, getResource("css/app.css").toExternalForm());
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void buildAddItemDia() {
        Pane root = null;
        try {
            root = FXMLLoader.load(GuiApplication.class.getClassLoader().getResource("view/additem.fxml"), ResourceBundle.getBundle("i18n.message_en_US_GUI"));
            Stage stage = WindowFactory.createSimpleWindowAsModel("addItem", ResourceBundleUtils.getString(ResourceMassages.ADD_ITEM), root, getResource("css/app.css").toExternalForm());
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
