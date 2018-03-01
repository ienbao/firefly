/*
 * Copyright (c) 2016. For Intelligent Group.
 */
package com.dmsoft.firefly.gui.controller.template;

import com.dmsoft.firefly.gui.GuiApplication;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.gui.utils.ImageUtils;
import com.dmsoft.firefly.gui.utils.ResourceBundleUtils;
import com.dmsoft.firefly.gui.utils.ResourceMassages;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.beans.EventHandler;
import java.io.IOException;
import java.util.ResourceBundle;

import static com.google.common.io.Resources.getResource;

/**
 * Created by Guang.Li on 2018/2/10.
 */
public class TemplateController {
    @FXML
    private Button rename, add, copy, delete, addTime, pattern, addRow, ok, cancel, apply;
    private Stage dialog;
    @FXML
    private VBox timeKeys;
    @FXML
    private ListView templateName;
    private ObservableList<String> templateNames = FXCollections.observableArrayList();

    private NewTemplateController newTemplateController;
    private Stage newStage;
    private RenameTemplateController renameTemplateController;
    private Stage renameStage;

    @FXML
    private void initialize() {
        initButton();
        timeKeys.getChildren().add(new TimePane());
        initEvent();
        templateName.setItems(templateNames);
    }

    private void initButton() {
        rename.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_rename_normal.png")));
        add.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_add_normal.png")));
        copy.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_copy_normal.png")));
        delete.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_del_normal.png")));
//        deleteTime.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_delete_normal.png")));
        addTime.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_add_normal.png")));
//        pattern.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_add_normal.png")));
        addRow.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_add_normal.png")));

    }

    private void initEvent() {
        add.setOnAction(event -> buildNewTemplateDialog());
        rename.setOnAction(event -> {
            if (templateName.getSelectionModel().getSelectedItem() != null) {
                buildRenameTemplateDialog();
            }
        });
        pattern.setOnAction(event -> buildPatternDia());
        addRow.setOnAction(event -> buildAddItemDia());
        addTime.setOnAction(event -> timeKeys.getChildren().add(new TimePane()));
        ok.setOnAction(event -> {
            StageMap.closeStage("template");

        });
        apply.setOnAction(event -> {

        });
        cancel.setOnAction(event -> {
            StageMap.closeStage("template");
        });
    }

    public void initData() {

    }

    private void buildNewTemplateDialog() {
        if (newStage == null) {
            Pane root = null;
            try {
                FXMLLoader loader = new FXMLLoader(GuiApplication.class.getClassLoader().getResource("view/new_template.fxml"), ResourceBundle.getBundle("i18n.message_en_US_GUI"));
                newTemplateController = new NewTemplateController();
                newTemplateController.setTemplateController(this);
                loader.setController(newTemplateController);
                root = loader.load();

                newStage = WindowFactory.createSimpleWindowAsModel("newTemplate", "New Template", root);
                newStage.setOnCloseRequest(event -> renameTemplateController.setName(""));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        newStage.show();

    }

    private void buildRenameTemplateDialog() {
        if (renameStage == null) {
            Pane root = null;
            try {
                FXMLLoader loader = new FXMLLoader(GuiApplication.class.getClassLoader().getResource("view/new_template.fxml"), ResourceBundle.getBundle("i18n.message_en_US_GUI"));
                renameTemplateController = new RenameTemplateController();
                renameTemplateController.setNameList(templateNames);
                loader.setController(renameTemplateController);
                root = loader.load();

                renameStage = WindowFactory.createSimpleWindowAsModel("renameTemplate", "Rename Template", root);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        renameTemplateController.setName(templateName.getSelectionModel().getSelectedItem().toString());
        renameStage.show();
    }

    private void buildPatternDia() {
        Pane root = null;
        try {
            root = FXMLLoader.load(GuiApplication.class.getClassLoader().getResource("view/pattern.fxml"), ResourceBundle.getBundle("i18n.message_en_US_GUI"));
            Stage stage = WindowFactory.createSimpleWindowAsModel("pattern", ResourceBundleUtils.getString(ResourceMassages.TIME_PATTERN), root, getResource("css/platform_app.css").toExternalForm());
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void buildAddItemDia() {
        Pane root = null;
        try {
            root = FXMLLoader.load(GuiApplication.class.getClassLoader().getResource("view/additem.fxml"), ResourceBundle.getBundle("i18n.message_en_US_GUI"));
            Stage stage = WindowFactory.createSimpleWindowAsModel("addItem", ResourceBundleUtils.getString(ResourceMassages.ADD_ITEM), root, getResource("css/platform_app.css").toExternalForm());
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ObservableList<String> getTemplateNames() {
        return templateNames;
    }
}
