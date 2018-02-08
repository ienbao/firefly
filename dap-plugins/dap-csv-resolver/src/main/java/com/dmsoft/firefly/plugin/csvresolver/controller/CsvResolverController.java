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
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

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

    private CsvResolverService service = new CsvResolverService();

    private ObservableList<RowDataModel> rowDataList = FXCollections.observableArrayList();
    private ObservableList<String> options = FXCollections.observableArrayList(
            "", "Row1", "Row2", "Row3", "Row4", "Row5", "Row6", "Row7", "Row8", "Row9", "Row10");
    private List<String[]> rowData = Lists.newArrayList();
    private String[] row = {"Row1", "Row2", "Row3", "Row4", "Row5", "Row6", "Row7", "Row8", "Row9", "Row10"};

    @FXML
    private void initialize() {
        header.setItems(options);
        usl.setItems(options);
        lsl.setItems(options);
        item.setItems(options);
        unit.setItems(options);
        data.setItems(options);

        initComponentEvent();

        columnO.setCellValueFactory(cellData -> cellData.getValue().rowProperty());
        columnTo.setCellValueFactory(cellData -> cellData.getValue().col1Property());
        columnTh.setCellValueFactory(cellData -> cellData.getValue().col2Property());
        columnF.setCellValueFactory(cellData -> cellData.getValue().col3Property());
        initData();

    }

    private void initComponentEvent() {
        browse.setOnAction(event -> {
            String str = System.getProperty("user.home");
            if (!StringUtils.isEmpty(path.getText())) {
                str = path.getText();
            }
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Csv Choose");
            fileChooser.setInitialDirectory(new File(str));
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("CSV", "*.csv")
            );
            Stage fileStage = null;
            File file = fileChooser.showOpenDialog(fileStage);

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
        apply.setOnAction(event -> save());
    }

    private void initData() {
        String[] empty = {"", "", ""};
        for (int i = 0; i < 10; i++) {
            RowDataModel rowDataModel = new RowDataModel(row[i], empty);
            rowDataList.add(rowDataModel);
        }
        refreshData();
        rowTable.setItems(rowDataList);
    }

    private void refreshData() {
//        rowDataList.clear();
        CsvTemplateDto csvTemplateDto = service.findCsvTemplate();
        if (csvTemplateDto != null) {
            if (!StringUtils.isEmpty(csvTemplateDto.getFilePath())) {
                path.setText(csvTemplateDto.getFilePath());
                rowData = service.rowParser(csvTemplateDto.getFilePath());
            }
            if (csvTemplateDto.getHeader() != null) {
                header.setValue(row[csvTemplateDto.getHeader() - 1]);
            }
            if (csvTemplateDto.getLsl() != null) {
                lsl.setValue(row[csvTemplateDto.getLsl() - 1]);
            }
            if (csvTemplateDto.getUsl() != null) {
                usl.setValue(row[csvTemplateDto.getUsl() - 1]);
            }
            if (csvTemplateDto.getUnit() != null) {
                unit.setValue(row[csvTemplateDto.getUnit() - 1]);
            }
            if (csvTemplateDto.getData() != null) {
                data.setValue(row[csvTemplateDto.getData() - 1]);
            }
            if (csvTemplateDto.getItem() != null) {
                item.setValue(row[csvTemplateDto.getItem() - 1]);
            }
        }
        for (int i = 0; i < rowData.size(); i++) {
//            RowDataModel rowDataModel = new RowDataModel(row[i], rowData.get(i));
            if (rowDataList != null && rowDataList.get(i) != null) {
                rowDataList.get(i).setData(rowData.get(i));
            }
        }
    }

    private void save() {
        CsvTemplateDto csvTemplateDto = new CsvTemplateDto();
        csvTemplateDto.setFilePath(path.getText());
        csvTemplateDto.setHeader(Integer.valueOf(header.getValue() == null ? "" : header.getValue().toString().substring(3, 4)));
        csvTemplateDto.setItem(Integer.valueOf(item.getValue() == null ? "" : item.getValue().toString().substring(3, 4)));
        csvTemplateDto.setUsl(Integer.valueOf(usl.getValue() == null ? "" : usl.getValue().toString().substring(3, 4)));
        csvTemplateDto.setLsl(Integer.valueOf(lsl.getValue() == null ? "" : lsl.getValue().toString().substring(3, 4)));
        csvTemplateDto.setUnit(Integer.valueOf(unit.getValue() == null ? "" : unit.getValue().toString().substring(3, 4)));
        csvTemplateDto.setData(Integer.valueOf(data.getValue() == null ? "" : data.getValue().toString().substring(3, 4)));

        service.saveCsvTemplate(csvTemplateDto);
    }
}
