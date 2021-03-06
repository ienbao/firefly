/*
 * Copyright (c) 2016. For Intelligent Group.
 */
package com.dmsoft.firefly.gui.controller.template;

import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.gui.components.window.WindowCustomListener;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.gui.components.window.WindowMessageController;
import com.dmsoft.firefly.gui.components.window.WindowMessageFactory;
import com.dmsoft.firefly.gui.model.StateBarTemplateModel;
import com.dmsoft.firefly.gui.model.TemplateItemDFModel;
import com.dmsoft.firefly.gui.model.TemplateItemModel;
import com.dmsoft.firefly.gui.utils.DapUtils;
import com.dmsoft.firefly.gui.utils.GuiConst;
import com.dmsoft.firefly.gui.utils.GuiFxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.utils.MenuFactory;
import com.dmsoft.firefly.gui.utils.ResourceMassages;
import com.dmsoft.firefly.sdk.dai.dto.SpecificationDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TemplateSettingDto;
import com.dmsoft.firefly.sdk.dai.dto.TimePatternDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.TemplateService;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.dmsoft.firefly.sdk.utils.enums.TestItemType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.google.common.io.Resources.getResource;

/**
 * Created by Guang.Li on 2018/2/10.
 */
@Component
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
//    private ObservableList<TemplateItemModel> items = FXCollections.observableArrayList();
//    private FilteredList<TemplateItemModel> filteredList = items.filtered(p -> p.getTestItemName().startsWith(""));
//    private SortedList<TemplateItemModel> personSortedList = new SortedList<>(filteredList);

    @FXML
    private TextFieldFilter nameFilter;
    @FXML
    private ListView<String> templateName;
    private ObservableList<String> templateNames = FXCollections.observableArrayList();
    private FilteredList<String> nameFilterList = templateNames.filtered(p -> p.startsWith(""));
    private SortedList<String> nameSortedList = new SortedList<>(nameFilterList);

    @Autowired
    private TemplateService templateService;
    @Autowired
    private EnvService envService;

    private LinkedHashMap<String, TemplateSettingDto> allTemplate = Maps.newLinkedHashMap();
    private TemplateSettingDto currTemplate;
    private TemplateSettingDto templateSettingDto;

    private TemplateItemDFModel templateItemDFModel;

    private boolean isModifiedActiveTemplate = false;

    @FXML
    private void initialize() {
        decimal.setItems(FXCollections.observableArrayList(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)));
        initButton();
        initDefault();
//        itemTable.setItems(personSortedList);
//        personSortedList.comparatorProperty().bind(itemTable.comparatorProperty());


        itemTable.setEditable(true);
        itemFilter.getTextField().setPromptText(GuiFxmlAndLanguageUtils.getString(ResourceMassages.FILTER));
        nameFilter.getTextField().setPromptText(GuiFxmlAndLanguageUtils.getString(ResourceMassages.FILTER));
//        testItem.setCellValueFactory(cellData -> cellData.getValue().testItemNameProperty());
//        type.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableArrayList("VARIABLE", "ATTRIBUTE")));
//        lsl.setCellFactory(TextFieldTableCell.forTableColumn());
//        usl.setCellFactory(TextFieldTableCell.forTableColumn());
//
//        type.setCellValueFactory(cellData -> cellData.getValue().dataTypeProperty());
//        lsl.setCellValueFactory(cellData -> cellData.getValue().lslFailProperty());
//        usl.setCellValueFactory(cellData -> cellData.getValue().uslPassProperty());

        initEvent();
        templateName.setItems(nameSortedList);
    }

    private void initButton() {
        TooltipUtil.installNormalTooltip(rename, GuiFxmlAndLanguageUtils.getString(ResourceMassages.RENAME_TEMPLATE));
        TooltipUtil.installNormalTooltip(add, GuiFxmlAndLanguageUtils.getString(ResourceMassages.ADD_TEMPLATE));
        TooltipUtil.installNormalTooltip(copy, GuiFxmlAndLanguageUtils.getString(ResourceMassages.COPY_TEMPLATE));
        TooltipUtil.installNormalTooltip(delete, GuiFxmlAndLanguageUtils.getString(ResourceMassages.DELETE_TEMPLATE));
        TooltipUtil.installNormalTooltip(addTime, GuiFxmlAndLanguageUtils.getString(ResourceMassages.ADD_TIME_KEYS));
        TooltipUtil.installNormalTooltip(addRow, GuiFxmlAndLanguageUtils.getString(ResourceMassages.ADD_ITEM));
        pattern.getStyleClass().setAll("message-tip-question","template-pattern");
        TooltipUtil.installNormalTooltip(pattern, GuiFxmlAndLanguageUtils.getString(ResourceMassages.HELP));
    }

    private void initDefault() {
        templateSettingDto = envService.findActivatedTemplate();
        templateNames.clear();
        List<TemplateSettingDto> allTemplates = templateService.findAllTemplate();
        if (allTemplates != null) {
            allTemplates.forEach(dto -> {
                allTemplate.put(dto.getName(), dto);
                templateNames.add(dto.getName());
            });
        }
        String selectName = GuiConst.DEFAULT_TEMPLATE_NAME;
        if (templateSettingDto != null) {
            selectName = templateSettingDto.getName();
        }
        templateItemDFModel = new TemplateItemDFModel();
        TableViewWrapper.decorate(itemTable, templateItemDFModel);

        ((TableColumn<String, String>) itemTable.getColumns().get(1))
                .setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableArrayList(TestItemType.VARIABLE.getCode(), TestItemType.ATTRIBUTE.getCode())));

        initData(selectName);
        templateName.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> initData(newValue));
    }

    private void initEvent() {
        nameFilter.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
                    if (nameFilter.getTextField().getText() == null) {
                        return;
                    }
                    nameFilterList.setPredicate(p -> p.toLowerCase().contains(nameFilter.getTextField().getText().toLowerCase()));
                }
        );
        itemFilter.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
                    templateItemDFModel.filterTestItem(newValue);
                }
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
            String selectTemplateName = templateName.getSelectionModel().getSelectedItem();
            if (selectTemplateName != null) {
                if (templateSettingDto != null && selectTemplateName.equals(templateSettingDto.getName())) {
                    WindowMessageFactory.createWindowMessageHasOk(GuiFxmlAndLanguageUtils.getString(ResourceMassages.WARN_HEADER), GuiFxmlAndLanguageUtils.getString(ResourceMassages.TEMPLATE_NAME_DELETE_WARN));
                    return;
                }
                WindowMessageController messageController = WindowMessageFactory.createWindowMessageHasOkAndCancel(GuiFxmlAndLanguageUtils.getString(ResourceMassages.WARN_HEADER),
                        GuiFxmlAndLanguageUtils.getString(ResourceMassages.TEMPLATE_DELETE_WARN_MESSAGE));
                messageController.addProcessMonitorListener(new WindowCustomListener() {
                    @Override
                    public boolean onShowCustomEvent() {
                        return false;
                    }

                    @Override
                    public boolean onCloseAndCancelCustomEvent() {
                        return false;
                    }

                    @Override
                    public boolean onOkCustomEvent() {
                        allTemplate.remove(templateName.getSelectionModel().getSelectedItem());
                        templateNames.remove(templateName.getSelectionModel().getSelectedItem());
                        return false;
                    }
                });

            } else {
                WindowMessageFactory.createWindowMessageHasOk("Message", "Please select a template.");
                return;
            }
        });
        pattern.setOnAction(event -> buildPatternDia());
        addRow.setOnAction(event -> buildAddItemDia());
        addTime.setOnAction(event -> timeKeys.getChildren().add(new TimePane()));
        ok.setOnAction(event -> {
            if (templateItemDFModel.hasErrorEditValue()) {
                WindowMessageFactory.createWindowMessageHasOk(GuiFxmlAndLanguageUtils.getString(ResourceMassages.WARN_HEADER), GuiFxmlAndLanguageUtils.getString(ResourceMassages.TEMPLATE_APPLY_WARN_MESSAGE));
                return;
            }
            saveCache();
            if (allTemplate != null) {
                templateService.saveAllAnalysisTemplate(Lists.newArrayList(allTemplate.values()));
            }
            StageMap.closeStage("template");
            refreshMainTemplate();
        });
        apply.setOnAction(event -> {
            if (templateItemDFModel.hasErrorEditValue()) {
                WindowMessageFactory.createWindowMessageHasOk(GuiFxmlAndLanguageUtils.getString(ResourceMassages.WARN_HEADER), GuiFxmlAndLanguageUtils.getString(ResourceMassages.TEMPLATE_APPLY_WARN_MESSAGE));
                return;
            }
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
        name = name == null ? templateNames.get(0) : name;
        templateName.getSelectionModel().select(name);
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
                    patternText.setText("yyy/MM/dd HH:mm:ss.SSS");
                }
            }
//            if (currTemplate.getSpecificationDatas() != null) {
//                currTemplate.getSpecificationDatas().values().forEach(data -> {
//                    TemplateItemModel model = new TemplateItemModel(data);
//                    items.add(model);
//                });
//            }
            templateItemDFModel.initData(currTemplate.getSpecificationDatas());
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
//            items.forEach(model -> {
//                SpecificationDataDto dataDto = new SpecificationDataDto();
//                dataDto.setTestItemName(model.getTestItemName());
//                dataDto.setDataType(model.getDataType());
//                dataDto.setLslFail(model.getLslFail());
//                dataDto.setUslPass(model.getUslPass());
//                currTemplate.getSpecificationDatas().put(model.getTestItemName(), dataDto);
//            });
            if (currTemplate.getName().equals(templateSettingDto.getName())) {
                isModifiedActiveTemplate = this.isModifiedTemplate(templateSettingDto, currTemplate);
            }
        }
    }

    private void clear() {
        title.setText(GuiConst.DEFAULT_TEMPLATE_NAME);
        decimal.setValue(6);
        timeKeys.getChildren().clear();
        patternText.setText("yyy/MM/dd HH:mm:ss.SSS");
//        items.clear();
    }

    private void buildNewTemplateDialog() {
        Pane root;
//        try {
//            FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/new_template.fxml");
//            NewNameController newNameController = new NewNameController();
//            newNameController.setPaneName("newTemplate");
//            fxmlLoader.setController(newNameController);
            root = DapUtils.loadFxml("view/new_template.fxml");
//            newNameController.getOk().setOnAction(event -> {
//                if (newNameController.isError()) {
//                    //WindowMessageFactory.createWindowMessageHasOk(GuiFxmlAndLanguageUtils.getString(ResourceMassages.WARN_HEADER), GuiFxmlAndLanguageUtils.getString(ResourceMassages.TEMPLATE_NAME_EMPTY_WARN));
//                    return;
//                }
//                String newTemplateName = newNameController.getName().getText();
//                if (templateNames.contains(newTemplateName)) {
//                    WindowMessageFactory.createWindowMessageHasOk(GuiFxmlAndLanguageUtils.getString(ResourceMassages.WARN_HEADER), GuiFxmlAndLanguageUtils.getString(ResourceMassages.TEMPLATE_NAME_REPEAT_WARN));
//                    return;
//                }
//                if (StringUtils.isNotEmpty(newTemplateName)) {
//                    if (!templateNames.contains(newTemplateName)) {
//                        templateNames.add(newTemplateName);
//                        TemplateSettingDto newDto = new TemplateSettingDto();
//                        newDto.setName(newTemplateName);
//                        allTemplate.put(newTemplateName, newDto);
//                        initData(newTemplateName);
//                    }
//                }
//                StageMap.closeStage("newTemplate");
//            });

            Stage newStage = WindowFactory.createOrUpdateSimpleWindowAsModel("newTemplate", GuiFxmlAndLanguageUtils.getString(ResourceMassages.NEW_TEMPLATE), root);
//            newStage.setOnCloseRequest(event -> newNameController.getName().setText(""));
            newStage.toFront();
            newStage.show();

//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    private void buildRenameTemplateDialog() {
        Pane root;
//        try {
//            FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/new_template.fxml");
//            NewNameController renameTemplateController = new NewNameController();
//            renameTemplateController.setPaneName("renameTemplate");

//            fxmlLoader.setController(renameTemplateController);
            root = DapUtils.loadFxml("view/new_template.fxml");
//            renameTemplateController.getOk().setOnAction(event -> {
//                if (renameTemplateController.isError()) {
//                    //WindowMessageFactory.createWindowMessageHasOk(GuiFxmlAndLanguageUtils.getString(ResourceMassages.WARN_HEADER), GuiFxmlAndLanguageUtils.getString(ResourceMassages.TEMPLATE_NAME_EMPTY_WARN));
//                    return;
//                }
//                String newTemplateName = renameTemplateController.getName().getText();
//                if (!newTemplateName.equals(templateName.getSelectionModel().getSelectedItem()) && templateNames.contains(newTemplateName)) {
//                    WindowMessageFactory.createWindowMessageHasOk(GuiFxmlAndLanguageUtils.getString(ResourceMassages.WARN_HEADER), GuiFxmlAndLanguageUtils.getString(ResourceMassages.TEMPLATE_NAME_REPEAT_WARN));
//                    return;
//                }
//                String selectTemplateName = templateName.getSelectionModel().getSelectedItem();
//                if (selectTemplateName.equals(newTemplateName)) {
//                    return;
//                }
//                if (selectTemplateName.equals(templateSettingDto.getName())) {
//                    templateSettingDto.setName(newTemplateName);
//                }
//                TemplateSettingDto selectDto = allTemplate.get(selectTemplateName);
//                selectDto.setName(newTemplateName);
//                allTemplate.put(newTemplateName, selectDto);
//                allTemplate.remove(selectTemplateName);
//
//                int index = templateNames.indexOf(templateName.getSelectionModel().getSelectedItem());
////                templateNames.remove(templateName.getSelectionModel().getSelectedItem());
////                templateNames.add(index, newTemplateName);
//                templateNames.set(index, newTemplateName);
//
//
////                if (StringUtils.isNotEmpty(n.getText()) && !n.getText().equals(templateName.getSelectionModel().getSelectedItem())) {
////                    for (int i = 0; i < templateNames.size(); i++) {
////                        if (templateNames.get(i).equals(templateName.getSelectionModel().getSelectedItem())) {
////                            allTemplate.put(n.getText(), allTemplate.get(templateNames.get(i)));
////                            allTemplate.remove(templateNames.get(i));
////                            templateNames.set(i, n.getText());
////                        }
////                    }
////                }
//                StageMap.closeStage("renameTemplate");
//            });
            Stage renameStage = WindowFactory.createOrUpdateSimpleWindowAsModel("renameTemplate", GuiFxmlAndLanguageUtils.getString(ResourceMassages.RENAME_TEMPLATE_TITLE), root);
//            renameTemplateController.getName().setText(templateName.getSelectionModel().getSelectedItem());
            renameStage.setResizable(false);
            renameStage.toFront();
            renameStage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    private void buildCopyTemplateDialog() {
        Pane root;
//        try {
//            FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/new_template.fxml");
//            NewNameController copyTemplateController = new NewNameController();
//            copyTemplateController.setPaneName("copyTemplate");

//            fxmlLoader.setController(copyTemplateController);
            root = DapUtils.loadFxml("view/new_template.fxml");
//            copyTemplateController.getOk().setOnAction(event -> {
//                if (copyTemplateController.isError()) {
//                    //WindowMessageFactory.createWindowMessageHasOk(GuiFxmlAndLanguageUtils.getString(ResourceMassages.WARN_HEADER), GuiFxmlAndLanguageUtils.getString(ResourceMassages.TEMPLATE_NAME_EMPTY_WARN));
//                    return;
//                }
//                String newTemplateName = copyTemplateController.getName().getText();
//                if (templateNames.contains(newTemplateName)) {
//                    WindowMessageFactory.createWindowMessageHasOk(GuiFxmlAndLanguageUtils.getString(ResourceMassages.WARN_HEADER), GuiFxmlAndLanguageUtils.getString(ResourceMassages.TEMPLATE_NAME_REPEAT_WARN));
//                    return;
//                }
//                if (StringUtils.isNotEmpty(newTemplateName)) {
//                    if (!templateNames.contains(newTemplateName)) {
//                        TemplateSettingDto copyDto = new TemplateSettingDto();
//                        try {
//                            copyDto = (TemplateSettingDto) DeepCopy.deepCopy(currTemplate);
//                        } catch (IOException | ClassNotFoundException e) {
//                            e.printStackTrace();
//                        }
//                        copyDto.setName(newTemplateName);
//                        allTemplate.put(newTemplateName, copyDto);
//                        templateNames.add(newTemplateName);
//                        templateName.getSelectionModel().select(newTemplateName);
//                    }
//                }
//                StageMap.closeStage("copyTemplate");
//            });
            Stage copyStage = WindowFactory.createOrUpdateSimpleWindowAsModel("copyTemplate",  GuiFxmlAndLanguageUtils.getString(ResourceMassages.COPY_TEMPLATE_TITLE), root);
//            copyTemplateController.getName().setText(templateName.getSelectionModel().getSelectedItem());
            copyStage.toFront();
            copyStage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void buildPatternDia() {
        Pane root;
        try {
//            FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/pattern.fxml");
            root = DapUtils.loadFxml("view/pattern.fxml");
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("pattern", GuiFxmlAndLanguageUtils.getString(ResourceMassages.TIME_PATTERN), root, getResource("css/platform_app.css").toExternalForm());
            stage.toFront();
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void buildAddItemDia() {
        Pane root;
        try {
//            FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/additem.fxml");

//            AddItemController addItem = new AddItemController();
//            addItem.setItemTableData(templateItemDFModel.getRowKeyArray());
//            fxmlLoader.setController(addItem);
            root = DapUtils.loadFxml("view/additem.fxml");
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("addItem", GuiFxmlAndLanguageUtils.getString(ResourceMassages.ADD_ITEM), root, getResource("css/platform_app.css").toExternalForm());
//            addItem.getAddItemOk().setOnAction(event -> {
//                List<String> selectItems = addItem.getSelectItem();
//                selectItems.forEach(item -> {
//                    SpecificationDataDto dataDto = new SpecificationDataDto();
//                    dataDto.setTestItemName(item);
//                    dataDto.setDataType(TestItemType.VARIABLE.getCode());
//                    templateItemDFModel.addTestItem(dataDto);
//                });
//                StageMap.closeStage("addItem");
//            });
            stage.setResizable(false);
            stage.toFront();
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void refreshMainTemplate() {
        if (isModifiedActiveTemplate) {
            MenuFactory.getMainController().refreshActiveTemplate();
        }
        ObservableList<StateBarTemplateModel> templateList = FXCollections.observableArrayList();
        if (allTemplate != null) {
            allTemplate.keySet().forEach(name -> {
                StateBarTemplateModel stateBarTemplateModel = new StateBarTemplateModel(name, false);
                if (templateSettingDto != null && name.equals(templateSettingDto.getName())) {
                    stateBarTemplateModel.setIsChecked(true);
                    envService.setActivatedTemplate(name);
                }
                templateList.add(stateBarTemplateModel);
            });
            MenuFactory.getMainController().refreshTemplate(templateList);
        }
    }

    public ObservableList<String> getTemplateNames() {
        return templateNames;
    }

    private boolean isModifiedTemplate(TemplateSettingDto oldTemplateDto, TemplateSettingDto newTemplateDto) {
        if (oldTemplateDto == null || newTemplateDto == null) {
            return false;
        }
        boolean isModified = false;
        if (oldTemplateDto.getDecimalDigit() != newTemplateDto.getDecimalDigit()) {
            isModified = true;
            return isModified;
        }
        TimePatternDto oldTimePatternDto = oldTemplateDto.getTimePatternDto();
        TimePatternDto newTimePatternDto = newTemplateDto.getTimePatternDto();
        if (!oldTimePatternDto.getPattern().equals(newTimePatternDto.getPattern())) {
            isModified = true;
            return isModified;
        }
        List<String> oldTimeKeyList = oldTimePatternDto.getTimeKeys();
        List<String> newTimeKeyList = newTimePatternDto.getTimeKeys();
        if (oldTimeKeyList != null && newTimeKeyList != null) {
            if (oldTimeKeyList.size() != newTimeKeyList.size()) {
                isModified = true;
                return isModified;
            } else {
                int i = 0;
                for (String oldTimeKey : oldTimeKeyList) {
                    if (!oldTimeKey.equals(newTimeKeyList.get(i))) {
                        isModified = true;
                        return isModified;
                    }
                    i++;
                }
            }
        } else if (oldTimeKeyList == null && newTimeKeyList == null) {

        } else {
            isModified = true;
            return isModified;
        }
        LinkedHashMap<String, SpecificationDataDto> oldSpecificationDataMap = oldTemplateDto.getSpecificationDatas();
        LinkedHashMap<String, SpecificationDataDto> newSpecificationDataMap = newTemplateDto.getSpecificationDatas();

        if (oldSpecificationDataMap != null && newSpecificationDataMap != null) {
            if (oldSpecificationDataMap.size() != newSpecificationDataMap.size()) {
                isModified = true;
                return isModified;
            } else {
                for (Map.Entry<String, SpecificationDataDto> entry : oldSpecificationDataMap.entrySet()) {
                    SpecificationDataDto newDto = newSpecificationDataMap.get(entry.getKey());
                    SpecificationDataDto oldDto = entry.getValue();
                    if (!newDto.getDataType().equals(oldDto.getDataType())) {
                        isModified = true;
                        return isModified;
                    } else if (!DAPStringUtils.isEqualsString(newDto.getLslFail(), oldDto.getLslFail())) {
                        isModified = true;
                        return isModified;
                    } else if (!DAPStringUtils.isEqualsString(newDto.getUslPass(), oldDto.getUslPass())) {
                        isModified = true;
                        return isModified;
                    }
                }
            }
        }


        return isModified;
    }
}
