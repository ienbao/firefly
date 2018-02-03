/*
 * Copyright (c) 2016. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.csvresolver.controller;

import com.dmsoft.firefly.plugin.csvresolver.model.RowDataModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Created by Guang.Li on 2018/2/3.
 */
public class CsvResolverController {
    @FXML
    private TextField path;
    @FXML
    private Button browse, ok, cancel, apply;
    @FXML
    private ComboBox header, item, usl, lsl, unit, data;
    @FXML
    private TableView<RowDataModel> rowTable;
    @FXML
    private TableColumn<RowDataModel, String> columnO;
    @FXML
    private TableColumn<RowDataModel, String> columnTo;
    @FXML
    private TableColumn<RowDataModel, String> columnTh;
    @FXML
    private TableColumn<RowDataModel, String> columnF;
    private ObservableList<RowDataModel> rowDataList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        columnO.setCellValueFactory(cellData -> cellData.getValue().rowProperty());
        columnTo.setCellValueFactory(cellData -> cellData.getValue().col1Property());
        columnTh.setCellValueFactory(cellData -> cellData.getValue().col2Property());
        columnF.setCellValueFactory(cellData -> cellData.getValue().col3Property());

        initData();
    }

    private void initData() {
        refreshData();
        rowTable.setItems(rowDataList);
    }

    private void refreshData() {
        String[] row = {"Row1", "Row2", "Row3", "Row4", "Row5", "Row6", "Row7", "Row8", "Row9", "Row10"};
        for (int i = 0; i < 10; i++) {
            String[] rowData = {"1", "2", "3"};
            RowDataModel rowDataModel = new RowDataModel(row[i], rowData);
            rowDataList.add(rowDataModel);
        }
    }
}
