/*
 * Copyright (c) 2016. For Intelligent Group.
 */
package com.dmsoft.firefly.gui.controller.template;

import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.gui.model.TemplateItemModel;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.SpecificationDataDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.utils.enums.TestItemType;
import com.google.common.collect.Lists;
import com.sun.prism.PixelFormat;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Guang.Li on 2018/2/11.
 */
public class AddItemController {
    @FXML
    private Button message;
    @FXML
    private Button addItemOk;
    @FXML
    private TableView itemTable;
    @FXML
    private TableColumn<ItemTableModel, CheckBox> select;
    @FXML
    private TableColumn<ItemTableModel, String> testItem;
    @FXML
    private TextField filter;
    @FXML
    private TextArea customizeItem;
    private ObservableList<String> selectTestItem;
    private ObservableList<ItemTableModel> items = FXCollections.observableArrayList();
    private FilteredList<ItemTableModel> filteredList = items.filtered(p -> p.getItem().startsWith(""));
    private SortedList<ItemTableModel> personSortedList = new SortedList<>(filteredList);
    private EnvService envService = RuntimeContext.getBean(EnvService.class);

    @FXML
    private void initialize() {
        message.getStyleClass().add("message-tip-question");
        message.setStyle("-fx-background-color: #0096ff");
        TooltipUtil.installNormalTooltip(message, "Please use \"Tab\" or \"Enter\" to separate item names");
        CheckBox box = new CheckBox();
        box.setOnAction(event -> {
            if (filteredList != null) {
                for (ItemTableModel model : filteredList) {
                    model.getSelector().setValue(box.isSelected());
                }
            }
        });
        select.setGraphic(box);
        select.setCellValueFactory(cellData -> cellData.getValue().getSelector().getCheckBox());
        testItem.setCellValueFactory(cellData -> cellData.getValue().itemProperty());

        initData();
        itemTable.setItems(personSortedList);
        personSortedList.comparatorProperty().bind(itemTable.comparatorProperty());
        filter.textProperty().addListener((observable, oldValue, newValue) ->
                filteredList.setPredicate(p -> p.getItem().toLowerCase().contains(filter.getText().toLowerCase()))
        );
//        addItemOk.setOnAction(event -> {
//            List<String> selectItems = getSelectItem();
//            selectItems.forEach(item -> {
//                SpecificationDataDto dataDto = new SpecificationDataDto();
//                dataDto.setTestItemName(item);
//                dataDto.setDataType(TestItemType.VARIABLE.toString());
//                itemTableData.add(new TemplateItemModel(dataDto));
//            });
//            StageMap.closeStage("addItem");
//        });
    }

    public void initData() {
        List<String> itemNames = envService.findTestItemNames();
        if (itemNames != null) {
            items.clear();
            if (selectTestItem != null) {
                selectTestItem.forEach(testItem -> itemNames.remove(testItem));
            }
            itemNames.forEach(item -> items.add(new ItemTableModel(item)));
        }
    }

    public List<String> getSelectItem() {
        List<String> selectItems = Lists.newArrayList();
        items.forEach(item -> {
            if (item.getSelector().isSelected()) {
                selectItems.add(item.getItem());
            }
        });
        if (StringUtils.isNotEmpty(customizeItem.getText())) {
            selectItems.addAll(Arrays.asList(customizeItem.getText().replaceAll("\t", "\n").split("\n")));
        }
        return selectItems;
    }

    public void setItemTableData(ObservableList<String> testItem) {
        this.selectTestItem = testItem;
    }

    public Button getAddItemOk() {
        return addItemOk;
    }
}
