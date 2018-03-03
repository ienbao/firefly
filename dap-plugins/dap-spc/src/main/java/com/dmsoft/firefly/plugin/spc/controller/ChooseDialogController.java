/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.plugin.spc.model.ChooseTableRowData;
import com.google.common.collect.Lists;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Ethan.Yang on 2018/2/11.
 */
public class ChooseDialogController implements Initializable {

    @FXML
    private TextFieldFilter chooseFilterTf;
    @FXML
    private CheckBox chooseUnSelected;
    @FXML
    private TableView chooseColumnTable;
    @FXML
    private TableColumn<ChooseTableRowData, CheckBox> chooseCheckBoxColumn;
    @FXML
    private TableColumn<ChooseTableRowData, String> chooseValueColumn;
    @FXML
    private Button chooseOkButton;
    private CheckBox allCheckBox;

    private ObservableList<ChooseTableRowData> chooseTableRowDataObservableList;
    private FilteredList<ChooseTableRowData> chooseTableRowDataFilteredList;
    private SortedList<ChooseTableRowData> chooseTableRowDataSortedList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.initTable();
        this.initComponentEvent();
    }

    /**
     * set table data
     *
     * @param chooseTableRowDataList the data list
     */
    public void setTableData(List<ChooseTableRowData> chooseTableRowDataList) {
        chooseTableRowDataObservableList.clear();
        chooseTableRowDataObservableList.addAll(chooseTableRowDataList);
    }

    /**
     * set the column of value header text
     *
     * @param text the header title
     */
    public void setValueColumnText(String text) {
        chooseValueColumn.setText(text);
    }

    /**
     * get select result
     *
     * @return
     */
    public List<String> getSelectResultName() {
        List<String> resultName = Lists.newArrayList();
        if (chooseTableRowDataObservableList != null) {
            for (ChooseTableRowData data : chooseTableRowDataObservableList) {
                if (data.isSelect()) {
                    resultName.add(data.getValue());
                }
            }
        }
        return resultName;
    }

    /**
     * set select result name
     *
     * @param resultName result name
     */
    public void setSelectResultName(List<String> resultName) {
        if (chooseTableRowDataObservableList != null) {
            for (ChooseTableRowData data : chooseTableRowDataObservableList) {
                if (resultName.contains(data.getValue())) {
                    data.getSelector().setValue(true);
                } else {
                    data.getSelector().setValue(false);
                }
            }
        }
    }


    private void initTable() {
        allCheckBox = new CheckBox();
        chooseCheckBoxColumn.setGraphic(allCheckBox);

        chooseCheckBoxColumn.setCellValueFactory(cellData -> cellData.getValue().getSelector().getCheckBox());
        chooseValueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
        chooseTableRowDataObservableList = FXCollections.observableArrayList();
        chooseTableRowDataFilteredList = chooseTableRowDataObservableList.filtered(p -> true);
        chooseTableRowDataSortedList = new SortedList<>(chooseTableRowDataFilteredList);
        chooseColumnTable.setItems(chooseTableRowDataSortedList);
        chooseTableRowDataSortedList.comparatorProperty().bind(chooseColumnTable.comparatorProperty());

    }

    private void initComponentEvent() {
        chooseFilterTf.getTextField().textProperty().addListener((observable, oldValue, newValue) -> getFilterValueEvent());
        chooseUnSelected.setOnAction(event -> getUnSelectedCheckBoxEvent());
        allCheckBox.setOnAction(event -> getAllSelectEvent());
    }

    private void getFilterValueEvent() {
        chooseTableRowDataFilteredList.setPredicate(p ->
                p.getValue().contains(chooseFilterTf.getTextField().getText())
        );
    }

    private void getUnSelectedCheckBoxEvent() {
        if (chooseTableRowDataObservableList != null) {
            for (ChooseTableRowData data : chooseTableRowDataObservableList) {
                data.getSelector().setValue(!data.getSelector().isSelected());
            }
        }
    }

    private void getAllSelectEvent() {
        if (chooseTableRowDataSortedList != null) {
            for (ChooseTableRowData data : chooseTableRowDataSortedList) {
                data.getSelector().setValue(allCheckBox.isSelected());
            }
        }
    }

    public Button getChooseOkButton() {
        return chooseOkButton;
    }

}
