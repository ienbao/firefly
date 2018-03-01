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
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.ResourceBundle;

import static com.google.common.io.Resources.getResource;

/**
 * Created by Guang.Li on 2018/2/10.
 */
public class TemplateController {
    @FXML
    private ComboBox<Integer> decimal;
    @FXML
    private Button rename, add, copy, delete, addTime, pattern, addRow, ok, cancel, apply;
    @FXML
    private VBox timeKeys;
    @FXML
    private ListView templateName;
    private ObservableList<String> templateNames = FXCollections.observableArrayList();

    private NewNameController newNameController;
    private Stage newStage;
    private NewNameController renameTemplateController;
    private Stage renameStage;
    private NewNameController copyTemplateController;
    private Stage copyStage;

    @FXML
    private void initialize() {
        initButton();
        templateNames.add("Default");
        decimal.setItems(FXCollections.observableArrayList(Arrays.asList(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9})));
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
        copy.setOnAction(event -> {
            if (templateName.getSelectionModel().getSelectedItem() != null) {
                buildCopyTemplateDialog();
            }
        });
        delete.setOnAction(event -> {
            if (templateName.getSelectionModel().getSelectedItem() != null) {
                templateNames.remove(templateName.getSelectionModel().getSelectedItem().toString());
                removeTemplate(templateName.getSelectionModel().getSelectedItem().toString());
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

    private void initData() {

    }

    private void copyTemplate(String template, String newTemplate) {

    }

    private void removeTemplate(String template) {

    }

    private void buildNewTemplateDialog() {
        if (newStage == null) {
            Pane root = null;
            try {
                FXMLLoader loader = new FXMLLoader(GuiApplication.class.getClassLoader().getResource("view/new_template.fxml"), ResourceBundle.getBundle("i18n.message_en_US_GUI"));
                newNameController = new NewNameController();
                newNameController.setPaneName("newTemplate");
                loader.setController(newNameController);
                root = loader.load();
                newNameController.getOk().setOnAction(event -> {
                    TextField n = newNameController.getName();
                    if (StringUtils.isNotEmpty(n.getText())) {
                        if (!templateNames.contains(n.getText())) {
                            templateNames.add(n.getText());
                            initData();
                        }
                    }
                    n.setText("");
                    StageMap.closeStage("newTemplate");
                });

                newStage = WindowFactory.createSimpleWindowAsModel("newTemplate", "New Template", root);
                newStage.setOnCloseRequest(event -> newNameController.getName().setText(""));
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
                renameTemplateController = new NewNameController();
                renameTemplateController.setPaneName("renameTemplate");

                loader.setController(renameTemplateController);
                root = loader.load();
                renameTemplateController.getOk().setOnAction(event -> {
                    TextField n = renameTemplateController.getName();
                    if (StringUtils.isNotEmpty(n.getText()) && !n.getText().equals(templateName.getSelectionModel().getSelectedItem().toString())) {
                        for (int i = 0; i < templateNames.size(); i++) {
                            if (templateNames.get(i).equals(templateName.getSelectionModel().getSelectedItem().toString())) {
                                templateNames.set(i, n.getText());
                            }
                        }
                    }
                    StageMap.closeStage("renameTemplate");
                });
                renameStage = WindowFactory.createSimpleWindowAsModel("renameTemplate", "Rename Template", root);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        renameTemplateController.getName().setText(templateName.getSelectionModel().getSelectedItem().toString());
        renameStage.show();
    }

    private void buildCopyTemplateDialog() {
        if (copyStage == null) {
            Pane root = null;
            try {
                FXMLLoader loader = new FXMLLoader(GuiApplication.class.getClassLoader().getResource("view/new_template.fxml"), ResourceBundle.getBundle("i18n.message_en_US_GUI"));
                copyTemplateController = new NewNameController();
                copyTemplateController.setPaneName("copyTemplate");

                loader.setController(copyTemplateController);
                root = loader.load();
                copyTemplateController.getOk().setOnAction(event -> {
                    TextField n = copyTemplateController.getName();
                    if (StringUtils.isNotEmpty(n.getText())) {
                        if (!templateNames.contains(n.getText())) {
                            templateNames.add(n.getText());
                            copyTemplate(templateName.getSelectionModel().getSelectedItem().toString(), n.getText());
                        }
                    }
                    n.setText("");
                    StageMap.closeStage("copyTemplate");
                });
                copyStage = WindowFactory.createSimpleWindowAsModel("copyTemplate", "Copy Template", root);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        copyTemplateController.getName().setText(templateName.getSelectionModel().getSelectedItem().toString());
        copyStage.show();
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
