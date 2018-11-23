/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.plugin.spc.model.AddItemTableModel;
import com.dmsoft.firefly.plugin.spc.utils.ResourceMassages;
import com.dmsoft.firefly.plugin.spc.utils.SpcFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static com.dmsoft.firefly.plugin.spc.utils.StateKey.SPC_SETTING_ADD_ITEM;

/**
 * Created by Ethan.Yang on 2018/3/15.
 */
public class AddItemController implements Initializable {
    @FXML
    private TextFieldFilter filterTf;
    @FXML
    private Button message;
    @FXML
    private Button chooseOkButton;
    @FXML
    private TableView testItemTable;
    @FXML
    private TextArea textAreaTestItem;

    private AddItemTableModel addItemTableModel;
    private EnvService envService = RuntimeContext.getBean(EnvService.class);
    private SourceDataService sourceDataService = RuntimeContext.getBean(SourceDataService.class);
    private SpcSettingController spcSettingController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.initBtnIcon();
        this.initComponent();
        this.initComponentEvent();
    }

    /**
     * init data
     *
     * @param existTestItemList exist test Item
     */
    public void initData(List<String> existTestItemList) {
        List<String> projectNameList = envService.findActivatedProjectName();
        if (projectNameList == null) {
            return;
        }
        List<String> testItemList = sourceDataService.findAllTestItemName(projectNameList);
        addItemTableModel.initData(testItemList, existTestItemList);
    }

    /**
     * set filter prompt
     * @param promptText prompt
     */
    public void setFilterTFPrompt(String promptText){
        filterTf.getTextField().setPromptText(promptText);
    }
    //TODO td 表格列宽度
    private void initComponent() {
        addItemTableModel = new AddItemTableModel();
        TableViewWrapper.decorate(testItemTable, addItemTableModel);
//        ((TableColumn) testItemTable.getColumns().get(0)).setPrefWidth(30);
//        ((TableColumn) testItemTable.getColumns().get(1)).setPrefWidth(145);
        this.setFilterTFPrompt(SpcFxmlAndLanguageUtils.getString("FILTER_TEST_ITEM_PROMPT"));
    }

    private void initComponentEvent() {
        filterTf.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            addItemTableModel.filterTestItem(newValue);
        });
        addItemTableModel.getAllCheckBox().setOnAction(event -> getAllCheckBoxEvent());

        chooseOkButton.setOnAction(event -> getChooseOkButtonEvent());
    }

    private void getChooseOkButtonEvent() {
        List<String> testItem = addItemTableModel.getSelectTestItem();
        if (DAPStringUtils.isBlank(textAreaTestItem.getText()) && testItem.size() == 0) {
            StageMap.closeStage(SPC_SETTING_ADD_ITEM);
            return;
        }
        List<String> customItemList = Lists.newArrayList();
        if (!DAPStringUtils.isBlank(textAreaTestItem.getText()) && !testItem.contains(textAreaTestItem.getText())) {
            Arrays.asList(textAreaTestItem.getText().replaceAll("\t", "\n").split("\n")).forEach( item -> {
                if(!testItem.contains(item)){
                    customItemList.add(item);
                }
            });
            textAreaTestItem.setText("");
        }
        spcSettingController.addCustomAlarmSettingData(testItem, customItemList);
        StageMap.closeStage(SPC_SETTING_ADD_ITEM);
    }


    private void getAllCheckBoxEvent() {
        if (addItemTableModel.getAddItemRowDataFilteredList() != null) {
            Map<String, SimpleObjectProperty<Boolean>> checkMap = addItemTableModel.getCheckMap();
            for (String key : addItemTableModel.getAddItemRowDataFilteredList()) {
                if (checkMap.get(key) != null) {
                    checkMap.get(key).set(addItemTableModel.getAllCheckBox().isSelected());
                } else {
                    checkMap.put(key, new SimpleObjectProperty<>(addItemTableModel.getAllCheckBox().isSelected()));
                }
            }
        }
    }

    private void initBtnIcon() {
        message.getStyleClass().add("message-tip-question");
//        tipLabel.getStyleClass().add("message-tip-question");
//        tipLabel.setStyle("-fx-background-color: #0096ff");
        TooltipUtil.installNormalTooltip(message, SpcFxmlAndLanguageUtils.getString("CUSTOM_TEST_ITEM_TIP"));
//        tipLabel.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_add_normal.png")));
    }

    public void setSpcSettingController(SpcSettingController spcSettingController) {
        this.spcSettingController = spcSettingController;
    }
}
