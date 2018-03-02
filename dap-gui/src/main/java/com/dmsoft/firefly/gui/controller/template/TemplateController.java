/*
 * Copyright (c) 2016. For Intelligent Group.
 */
package com.dmsoft.firefly.gui.controller.template;

import com.dmsoft.firefly.gui.GuiApplication;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.gui.model.TemplateItemModel;
import com.dmsoft.firefly.gui.utils.ImageUtils;
import com.dmsoft.firefly.gui.utils.ResourceBundleUtils;
import com.dmsoft.firefly.gui.utils.ResourceMassages;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.SpecificationDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TemplateSettingDto;
import com.dmsoft.firefly.sdk.dai.dto.TimePatternDto;
import com.dmsoft.firefly.sdk.dai.service.TemplateService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

import static com.google.common.io.Resources.getResource;

/**
 * Created by Guang.Li on 2018/2/10.
 */
public class TemplateController {
    @FXML
    private ComboBox<Integer> decimal;
    @FXML
    private Label title;
    @FXML
    private TextField patternText;
    @FXML
    private Button rename, add, copy, delete, addTime, pattern, addRow, ok, cancel, apply;
    @FXML
    private VBox timeKeys;
    @FXML
    private TableView itemTable;

    private ObservableList<TemplateItemModel> items = FXCollections.observableArrayList();
    private FilteredList<TemplateItemModel> filteredList = items.filtered(p -> p.getTestItemName().startsWith(""));
    private SortedList<TemplateItemModel> personSortedList = new SortedList<>(filteredList);

    @FXML
    private TextField nameFilter;
    @FXML
    private ListView templateName;
    private ObservableList<String> templateNames = FXCollections.observableArrayList();
    private FilteredList<String> nameFilterList = templateNames.filtered(p -> p.startsWith(""));
    private SortedList<String> nameSortedList = new SortedList<>(nameFilterList);

    private NewNameController newNameController;
    private Stage newStage;
    private NewNameController renameTemplateController;
    private Stage renameStage;
    private NewNameController copyTemplateController;
    private Stage copyStage;

    private TemplateService templateService = RuntimeContext.getBean(TemplateService.class);

    private Map<String, TemplateSettingDto> allTemplate = Maps.newHashMap();
    private TemplateSettingDto currTemplate;

    @FXML
    private void initialize() {
        decimal.setItems(FXCollections.observableArrayList(Arrays.asList(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9})));
        initButton();
        initDefault();
        itemTable.setItems(personSortedList);
        personSortedList.comparatorProperty().bind(itemTable.comparatorProperty());

        initEvent();
        templateName.setItems(nameSortedList);
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

    private void initDefault() {
        templateNames.clear();
        List<TemplateSettingDto> allTemplates = templateService.findAllTemplate();
        if (allTemplates != null) {
            allTemplates.forEach(dto -> {
                allTemplate.put(dto.getName(), dto);
                templateNames.add(dto.getName());
            });
        }
        initData("Default");
        templateName.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                initData(newValue.toString());
            }
        });
    }

    private void initEvent() {
        nameFilter.textProperty().addListener((observable, oldValue, newValue) ->
                nameFilterList.setPredicate(p -> p.contains(nameFilter.getText()))
        );
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
                allTemplate.remove(templateName.getSelectionModel().getSelectedItem().toString());
            }
        });
        pattern.setOnAction(event -> buildPatternDia());
        addRow.setOnAction(event -> buildAddItemDia());
        addTime.setOnAction(event -> timeKeys.getChildren().add(new TimePane()));
        ok.setOnAction(event -> {
            if (allTemplate != null) {
                templateService.saveAllAnalysisTemplate(Lists.newArrayList(allTemplate.values()));
            }
            StageMap.closeStage("template");
        });
        apply.setOnAction(event -> {
            if (allTemplate != null) {
                templateService.saveAllAnalysisTemplate(Lists.newArrayList(allTemplate.values()));
            }
        });
        cancel.setOnAction(event -> {
            StageMap.closeStage("template");
        });
    }

    private void initData(String name) {
        saveCache();
        currTemplate = null;
        clear();
        if (allTemplate != null && allTemplate.get(name) != null) {
            currTemplate = allTemplate.get(name);
            title.setText(name);
            decimal.setValue(currTemplate.getDecimalDigit());
            timeKeys.getChildren().clear();
            if (currTemplate.getTimePatternDto() != null) {
                if (currTemplate.getTimePatternDto().getTimeKeys() != null) {
                    currTemplate.getTimePatternDto().getTimeKeys().forEach(s -> {
                        timeKeys.getChildren().add(new TimePane(s));
                    });
                }
                if (StringUtils.isNotEmpty(currTemplate.getTimePatternDto().getPattern())) {
                    patternText.setText(currTemplate.getTimePatternDto().getPattern());
                } else {
                    patternText.setText("yyy/MM/dd HH:mm:ss SSSSSS");
                }
            }
            if (currTemplate.getSpecificationDatas() != null) {
                currTemplate.getSpecificationDatas().values().forEach(data -> {
                    TemplateItemModel model = new TemplateItemModel(data);
                    items.add(model);
                });
            }
        }
    }

    private void copyTemplate(String template, String newTemplate) {

    }

    private void removeTemplate(String template) {

    }

    private void saveCache() {
        if (currTemplate != null) {
            currTemplate.setDecimalDigit(decimal.getValue().intValue());

            List<String> timeKeyItem = Lists.newArrayList();
            for (int i = 0; i < timeKeys.getChildren().size(); i++) {
                TimePane node = (TimePane) timeKeys.getChildren().get(i);
                timeKeyItem.add(node.getSelectItem());
            }
            if (currTemplate.getTimePatternDto() == null) {
                TimePatternDto timePatternDto = new TimePatternDto();
                currTemplate.setTimePatternDto(timePatternDto);
            }
            currTemplate.getTimePatternDto().setTimeKeys(timeKeyItem);
            currTemplate.getTimePatternDto().setPattern(patternText.getText().toString());
            if (currTemplate.getSpecificationDatas() == null) {
                LinkedHashMap<String, SpecificationDataDto> map = Maps.newLinkedHashMap();
                currTemplate.setSpecificationDatas(map);
            }
            items.forEach(model -> {
                SpecificationDataDto dataDto = new SpecificationDataDto();
                dataDto.setTestItemName(model.getTestItemName());
                dataDto.setDataType(model.getDataType());
                dataDto.setLslFail(model.getLslFail());
                dataDto.setUslPass(model.getUslPass());
                currTemplate.getSpecificationDatas().put(model.getTestItemName(), dataDto);
            });
//            allTemplate.put(currTemplate.getName(), currTemplate);
        }
    }

    private void clear() {
        title.setText("Default");
        decimal.setValue(6);
        timeKeys.getChildren().clear();
        patternText.setText("yyy/MM/dd HH:mm:ss SSSSSS");
        items.clear();
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
                            TemplateSettingDto newDto = new TemplateSettingDto();
                            newDto.setName(n.getText());
                            allTemplate.put(n.getText(), newDto);
                            initData(n.getText());
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
                                allTemplate.put(n.getText(), allTemplate.get(templateNames.get(i)));
                                allTemplate.remove(templateNames.get(i));
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
            FXMLLoader loader = new FXMLLoader(GuiApplication.class.getClassLoader().getResource("view/additem.fxml"), ResourceBundle.getBundle("i18n.message_en_US_GUI"));
            AddItemController addItem = new AddItemController();
            addItem.setItemTableData(items);

            loader.setController(addItem);
            root = loader.load();
//            root = FXMLLoader.load(GuiApplication.class.getClassLoader().getResource("view/additem.fxml"), ResourceBundle.getBundle("i18n.message_en_US_GUI"));
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("addItem", ResourceBundleUtils.getString(ResourceMassages.ADD_ITEM), root, getResource("css/platform_app.css").toExternalForm());
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ObservableList<String> getTemplateNames() {
        return templateNames;
    }
}
