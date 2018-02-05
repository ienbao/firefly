/*
 * Copyright (c) 2016. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.csvresolver.controller;

import com.dmsoft.firefly.plugin.csvresolver.CsvResolverService;
import com.dmsoft.firefly.plugin.csvresolver.CsvTemplateDto;
import com.dmsoft.firefly.plugin.csvresolver.model.RowDataModel;
import com.google.common.collect.Lists;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.List;

/**
 * Created by Guang.Li on 2018/2/3.
 */
public class CsvResolverController {

    @FXML
    private AnchorPane mainPane;
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

    @Autowired
    private CsvResolverService service;

    private ObservableList<RowDataModel> rowDataList = FXCollections.observableArrayList();
    private ObservableList<String> options =
            FXCollections.observableArrayList(
                    "Row1", "Row2", "Row3", "Row4", "Row5", "Row6", "Row7", "Row8", "Row9", "Row10"
            );
    private List<String[]> rowData = Lists.newArrayList();

    @FXML
    private void initialize() {
        header.setItems(options);
        usl.setItems(options);
        lsl.setItems(options);
        item.setItems(options);
        unit.setItems(options);
        data.setItems(options);

        columnO.setCellValueFactory(cellData -> cellData.getValue().rowProperty());
        columnTo.setCellValueFactory(cellData -> cellData.getValue().col1Property());
        columnTh.setCellValueFactory(cellData -> cellData.getValue().col2Property());
        columnF.setCellValueFactory(cellData -> cellData.getValue().col3Property());
        browse.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Csv Choose");
            fileChooser.setInitialDirectory(
                    new File(System.getProperty("user.home"))
            );
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("CSV", "*.csv")
            );
            File file = fileChooser.showOpenDialog(mainPane.getScene().getWindow());

            if (file != null) {
                path.setText(file.getPath());
                rowData = service.rowParser(file.getPath());
                refreshData();
            }
        });
        ok.setOnAction(event -> {
            save();
        });
        cancel.setOnAction(event -> {

        });
        apply.setOnAction(event -> {
            save();
        });
        initData();
    }

    private void initData() {
        refreshData();
        rowTable.setItems(rowDataList);
    }

    private void refreshData() {
        rowDataList.clear();
        String[] row = {"Row1", "Row2", "Row3", "Row4", "Row5", "Row6", "Row7", "Row8", "Row9", "Row10"};
        for (int i = 0; i < rowData.size(); i++) {
            RowDataModel rowDataModel = new RowDataModel(row[i], rowData.get(i));
            rowDataList.add(rowDataModel);
        }
    }

    private void save() {
        CsvTemplateDto csvTemplateDto = new CsvTemplateDto();
        csvTemplateDto.setFilePath(path.getText());
        csvTemplateDto.setHeader(Integer.valueOf(header.getValue().toString()));
        csvTemplateDto.setItem(Integer.valueOf(item.getValue().toString()));
        csvTemplateDto.setUsl(Integer.valueOf(usl.getValue().toString()));
        csvTemplateDto.setLsl(Integer.valueOf(lsl.getValue().toString()));
        csvTemplateDto.setUnit(Integer.valueOf(unit.getValue().toString()));
        csvTemplateDto.setData(Integer.valueOf(data.getValue().toString()));

        service.saveCsvTemplate(csvTemplateDto);
    }
}
