/*
 * Copyright (c) 2016. For Intelligent Group.
 */
package com.dmsoft.firefly.gui.controller.template;

import com.dmsoft.firefly.gui.GuiApplication;
import com.dmsoft.firefly.gui.components.utils.ImageUtils;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.gui.components.window.WindowMessageFactory;
import com.dmsoft.firefly.gui.model.StateBarTemplateModel;
import com.dmsoft.firefly.gui.model.TemplateItemModel;
import com.dmsoft.firefly.gui.utils.GuiConst;
import com.dmsoft.firefly.gui.utils.GuiFxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.utils.MenuFactory;
import com.dmsoft.firefly.gui.utils.ResourceMassages;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.SpecificationDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TemplateSettingDto;
import com.dmsoft.firefly.sdk.dai.dto.TimePatternDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.TemplateService;
import com.dmsoft.firefly.sdk.utils.DeepCopy;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ResourceBundle;

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
    private TableView<TemplateItemModel> itemTable;
    @FXML
    private TableColumn<TemplateItemModel, String> testItem;
    @FXML
    private TableColumn<TemplateItemModel, String> type;
    @FXML
    private TableColumn<TemplateItemModel, String> lsl;
    @FXML
    private TableColumn<TemplateItemModel, String> usl;
    @FXML
    private TextFieldFilter itemFilter;
    private ObservableList<TemplateItemModel> items = FXCollections.observableArrayList();
    private FilteredList<TemplateItemModel> filteredList = items.filtered(p -> p.getTestItemName().startsWith(""));
    private SortedList<TemplateItemModel> personSortedList = new SortedList<>(filteredList);

    @FXML
    private TextFieldFilter nameFilter;
    @FXML
    private ListView<String> templateName;
    private ObservableList<String> templateNames = FXCollections.observableArrayList();
    private FilteredList<String> nameFilterList = templateNames.filtered(p -> p.startsWith(""));
    private SortedList<String> nameSortedList = new SortedList<>(nameFilterList);

    private TemplateService templateService = RuntimeContext.getBean(TemplateService.class);
    private EnvService envService = RuntimeContext.getBean(EnvService.class);

    private LinkedHashMap<String, TemplateSettingDto> allTemplate = Maps.newLinkedHashMap();
    private TemplateSettingDto currTemplate;

    @FXML
    private void initialize() {
        decimal.setItems(FXCollections.observableArrayList(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)));
        initButton();
        initDefault();
        itemTable.setItems(personSortedList);
        personSortedList.comparatorProperty().bind(itemTable.comparatorProperty());
        itemTable.setEditable(true);
        testItem.setCellValueFactory(cellData -> cellData.getValue().testItemNameProperty());
        type.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableArrayList("VARIABLE", "ATTRIBUTE")));
        lsl.setCellFactory(TextFieldTableCell.forTableColumn());
        usl.setCellFactory(TextFieldTableCell.forTableColumn());

        type.setCellValueFactory(cellData -> cellData.getValue().dataTypeProperty());
        lsl.setCellValueFactory(cellData -> cellData.getValue().lslFailProperty());
        usl.setCellValueFactory(cellData -> cellData.getValue().uslPassProperty());

        initEvent();
        templateName.setItems(nameSortedList);
    }

    private void initButton() {
        rename.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_rename_normal.png")));
        add.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_add_normal.png")));
        copy.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_copy_normal.png")));
        delete.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_del_normal.png")));
        addTime.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_add_normal.png")));
        addRow.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_add_normal.png")));
        pattern.getStyleClass().add("message-tip-question");
        pattern.setStyle("-fx-background-color: #0096ff");

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
        initData(GuiConst.DEFAULT_TEMPLATE_NAME);
        templateName.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> initData(newValue));
        templateName.getSelectionModel().select(0);
    }

    private void initEvent() {
        nameFilter.getTextField().textProperty().addListener((observable, oldValue, newValue) ->
                nameFilterList.setPredicate(p -> p.contains(nameFilter.getTextField().getText()))
        );
        itemFilter.getTextField().textProperty().addListener((observable, oldValue, newValue) ->
                filteredList.setPredicate(p -> p.getTestItemName().contains(itemFilter.getTextField().getText()))
        );
        add.setOnAction(event -> buildNewTemplateDialog());
        rename.setOnAction(event -> {
            if (templateName.getSelectionModel().getSelectedItem() == null) {
                WindowMessageFactory.createWindowMessageHasOk("Message", "Please select a template.");
                return;
            }
            buildRenameTemplateDialog();
        });
        copy.setOnAction(event -> {
            if (templateName.getSelectionModel().getSelectedItem() == null) {
                WindowMessageFactory.createWindowMessageHasOk("Message", "Please select a template.");
                return;
            }
            buildCopyTemplateDialog();
        });
        delete.setOnAction(event -> {
            if (templateName.getSelectionModel().getSelectedItem() != null
                    && !templateName.getSelectionModel().getSelectedItem().equals(GuiConst.DEFAULT_TEMPLATE_NAME)) {
                allTemplate.remove(templateName.getSelectionModel().getSelectedItem());
                templateNames.remove(templateName.getSelectionModel().getSelectedItem());
            }
        });
        pattern.setOnAction(event -> buildPatternDia());
        addRow.setOnAction(event -> buildAddItemDia());
        addTime.setOnAction(event -> timeKeys.getChildren().add(new TimePane()));
        ok.setOnAction(event -> {
            saveCache();
            if (allTemplate != null) {
                templateService.saveAllAnalysisTemplate(Lists.newArrayList(allTemplate.values()));
            }
            StageMap.closeStage("template");
            refreshMainTemplate();
        });
        apply.setOnAction(event -> {
            saveCache();
            if (allTemplate != null) {
                templateService.saveAllAnalysisTemplate(Lists.newArrayList(allTemplate.values()));
            }
            refreshMainTemplate();
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

    private void saveCache() {
        if (currTemplate != null) {
            currTemplate.setDecimalDigit(decimal.getValue());

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
            currTemplate.getTimePatternDto().setPattern(patternText.getText());
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
        }
    }

    private void clear() {
        title.setText(GuiConst.DEFAULT_TEMPLATE_NAME);
        decimal.setValue(6);
        timeKeys.getChildren().clear();
        patternText.setText("yyy/MM/dd HH:mm:ss SSSSSS");
        items.clear();
    }

    private void buildNewTemplateDialog() {
        Pane root;
        try {
            FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/new_template.fxml");
            NewNameController newNameController = new NewNameController();
            newNameController.setPaneName("newTemplate");
            fxmlLoader.setController(newNameController);
            root = fxmlLoader.load();
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

            Stage newStage = WindowFactory.createOrUpdateSimpleWindowAsModel("newTemplate", "New Template", root);
            newStage.setOnCloseRequest(event -> newNameController.getName().setText(""));
            newStage.toFront();
            newStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void buildRenameTemplateDialog() {
        Pane root;
        try {
            FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/new_template.fxml");
            NewNameController renameTemplateController = new NewNameController();
            renameTemplateController.setPaneName("renameTemplate");

            fxmlLoader.setController(renameTemplateController);
            root = fxmlLoader.load();
            renameTemplateController.getOk().setOnAction(event -> {
                TextField n = renameTemplateController.getName();
                if (StringUtils.isNotEmpty(n.getText()) && !n.getText().equals(templateName.getSelectionModel().getSelectedItem())) {
                    for (int i = 0; i < templateNames.size(); i++) {
                        if (templateNames.get(i).equals(templateName.getSelectionModel().getSelectedItem())) {
                            allTemplate.put(n.getText(), allTemplate.get(templateNames.get(i)));
                            allTemplate.remove(templateNames.get(i));
                            templateNames.set(i, n.getText());
                        }
                    }
                }
                StageMap.closeStage("renameTemplate");
            });
            Stage renameStage = WindowFactory.createOrUpdateSimpleWindowAsModel("renameTemplate", "Rename Template", root);
            renameTemplateController.getName().setText(templateName.getSelectionModel().getSelectedItem());
            renameStage.toFront();
            renameStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void buildCopyTemplateDialog() {
        Pane root;
        try {
            FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/new_template.fxml");
            NewNameController copyTemplateController = new NewNameController();
            copyTemplateController.setPaneName("copyTemplate");

            fxmlLoader.setController(copyTemplateController);
            root = fxmlLoader.load();
            copyTemplateController.getOk().setOnAction(event -> {
                TextField n = copyTemplateController.getName();
                if (StringUtils.isNotEmpty(n.getText())) {
                    if (!templateNames.contains(n.getText())) {
                        TemplateSettingDto copyDto = new TemplateSettingDto();
                        try {
                            copyDto = (TemplateSettingDto) DeepCopy.deepCopy(currTemplate);
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        copyDto.setName(n.getText());
                        allTemplate.put(n.getText(), copyDto);
                        templateNames.add(n.getText());
                        templateName.getSelectionModel().select(n.getText());
                    }
                }
                n.setText("");
                StageMap.closeStage("copyTemplate");
            });
            Stage copyStage = WindowFactory.createOrUpdateSimpleWindowAsModel("copyTemplate", "Copy Template", root);
            copyTemplateController.getName().setText(templateName.getSelectionModel().getSelectedItem());
            copyStage.toFront();
            copyStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildPatternDia() {
        Pane root;
        try {
            FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/pattern.fxml");
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createSimpleWindowAsModel("pattern", GuiFxmlAndLanguageUtils.getString(ResourceMassages.TIME_PATTERN), root, getResource("css/platform_app.css").toExternalForm());
            stage.toFront();
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void buildAddItemDia() {
        Pane root;
        try {
            FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/additem.fxml");

            AddItemController addItem = new AddItemController();
            addItem.setItemTableData(items);

            fxmlLoader.setController(addItem);
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("addItem", GuiFxmlAndLanguageUtils.getString(ResourceMassages.ADD_ITEM), root, getResource("css/platform_app.css").toExternalForm());
            stage.toFront();
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void refreshMainTemplate() {
        ObservableList<StateBarTemplateModel> templateList = FXCollections.observableArrayList();
        TemplateSettingDto templateSettingDto = envService.findActivatedTemplate();
        if (allTemplate != null) {
            allTemplate.keySet().forEach(name -> {
                StateBarTemplateModel stateBarTemplateModel = new StateBarTemplateModel(name, false);
                if (templateSettingDto != null && name.equals(templateSettingDto.getName())) {
                    stateBarTemplateModel.setIsChecked(true);
                }
                templateList.add(stateBarTemplateModel);
            });
            MenuFactory.getMainController().refreshTemplate(templateList);
        }
    }

    public ObservableList<String> getTemplateNames() {
        return templateNames;
    }
}
