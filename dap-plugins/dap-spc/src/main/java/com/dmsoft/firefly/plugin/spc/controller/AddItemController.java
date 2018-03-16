/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.plugin.spc.model.AddItemTableModel;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
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
    private Label tipLabel;
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

    private void initComponent() {
        addItemTableModel = new AddItemTableModel();
        TableViewWrapper.decorate(testItemTable, addItemTableModel);
        ((TableColumn) testItemTable.getColumns().get(1)).setPrefWidth(145);
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
        String value = null;
        if (!DAPStringUtils.isBlank(textAreaTestItem.getText()) && !testItem.contains(textAreaTestItem.getText())) {
            value = textAreaTestItem.getText();
        }
        spcSettingController.addCustomAlarmSettingData(testItem, value);
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
//        tipLabel.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_add_normal.png")));
    }

    public void setSpcSettingController(SpcSettingController spcSettingController) {
        this.spcSettingController = spcSettingController;
    }
}
