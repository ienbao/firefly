/*
 * Copyright (c) 2016. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.tm.csvresolver.controller;

import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.gui.components.window.WindowMessageFactory;
import com.dmsoft.firefly.plugin.tm.csvresolver.service.CsvResolverService;
import com.dmsoft.firefly.plugin.tm.csvresolver.dto.CsvTemplateDto;
import com.dmsoft.firefly.plugin.tm.csvresolver.model.RowDataModel;
import com.dmsoft.firefly.plugin.tm.csvresolver.utils.ResourceMassages;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by Guang.Li on 2018/2/3.
 */
public class TMCsvResolverController {

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
    private Map<String, Integer> cache = Maps.newHashMap();

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
        columnO.setCellFactory(new Callback<TableColumn<RowDataModel, String>, TableCell<RowDataModel, String>>() {
            public TableCell call(TableColumn<RowDataModel, String> param) {
                return new TableCell<RowDataModel, String>() {
                    private ObservableValue ov;

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            this.setStyle("-fx-background-color: #f8f8f8; -fx-border-width: 0 1 0 0; -fx-border-color: #DCDCDC; -fx-border-style: dotted");
                            // Get fancy and change color based on data
                            setText(item);
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });

    }

    private void initComponentEvent() {
        header.setOnAction(event -> validate(header, "header"));
        usl.setOnAction(event -> validate(usl, "usl"));
        unit.setOnAction(event -> validate(unit, "unit"));
        lsl.setOnAction(event -> validate(lsl, "lsl"));

        item.setOnAction(event -> {
            validate(item, "item");
            if (item.getValue() == null || StringUtils.isEmpty(item.getValue().toString())) {
                TooltipUtil.installWarnTooltip(item, "Can not be empty!");
                item.getStyleClass().add("combo-box-error");
            } else {
                TooltipUtil.uninstallWarnTooltip(item);
                item.getStyleClass().removeAll("combo-box-error");
            }
        });
        data.setOnAction(event -> {
            validate(data, "data");
            if (data.getValue() == null || StringUtils.isEmpty(data.getValue().toString())) {
                TooltipUtil.installWarnTooltip(data, "Can not be empty!");
                data.getStyleClass().add("combo-box-error");
            } else {
                TooltipUtil.uninstallWarnTooltip(data);
                data.getStyleClass().removeAll("combo-box-error");
                if (cache != null) {
                    cache.values().forEach(value -> {
                        if (value != null && Integer.valueOf(data.getValue().toString().substring(3, 4)) < value) {
                            TooltipUtil.installWarnTooltip(data, "Test Data row must be maximum!");
                            data.getStyleClass().add("combo-box-error");
                        }
                    });
                }
            }
        });
        browse.setOnAction(event -> {
            String str = System.getProperty("user.home");
            if (!StringUtils.isEmpty(path.getText())) {
                str = path.getText();
            }
            File filePath = new File(str);
            if (!filePath.exists()) {
                WindowMessageFactory.createWindowMessageHasOk("Message", "File is not exist.");
                return;
            }
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Csv Choose");
            if (filePath.isDirectory()) {
                fileChooser.setInitialDirectory(filePath);
            } else {
                fileChooser.setInitialDirectory(new File(filePath.getParent()));

            }
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("CSV", "*.csv")
            );
            File file = fileChooser.showOpenDialog(StageMap.getStage(ResourceMassages.TM_STAGE_CSV));

            if (file != null) {
                path.setText(file.getPath());
                rowData = service.rowParser(file.getPath());
                for (int i = 0; i < rowData.size(); i++) {
                    if (rowDataList != null && rowDataList.get(i) != null) {
                        rowDataList.get(i).setData(rowData.get(i));
                    }
                }
            }

        });
        ok.setOnAction(event -> {
            if (item.getStyleClass().contains("combo-box-error") || data.getStyleClass().contains("combo-box-error")) {
                WindowMessageFactory.createWindowMessageHasCancel("Message", "Test Item Name or Test Data param error");
                return;
            }
            save();
            StageMap.closeStage(ResourceMassages.TM_STAGE_CSV);
        });
        cancel.setOnAction(event -> {
            StageMap.closeStage(ResourceMassages.TM_STAGE_CSV);
        });
        apply.setOnAction(event -> {
            if (item.getStyleClass().contains("combo-box-error") || data.getStyleClass().contains("combo-box-error")) {
                WindowMessageFactory.createWindowMessageHasCancel("Message", "Test Item Name or Test Data param error");
                return;
            }
            save();
        });
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
                cache.put("header", csvTemplateDto.getHeader());
            }
            if (csvTemplateDto.getLsl() != null) {
                lsl.setValue(row[csvTemplateDto.getLsl() - 1]);
                cache.put("lsl", csvTemplateDto.getLsl());
            }
            if (csvTemplateDto.getUsl() != null) {
                usl.setValue(row[csvTemplateDto.getUsl() - 1]);
                cache.put("usl", csvTemplateDto.getUsl());
            }
            if (csvTemplateDto.getUnit() != null) {
                unit.setValue(row[csvTemplateDto.getUnit() - 1]);
                cache.put("unit", csvTemplateDto.getUnit());
            }
            if (csvTemplateDto.getData() != null) {
                data.setValue(row[csvTemplateDto.getData() - 1]);
            }
            if (csvTemplateDto.getItem() != null) {
                item.setValue(row[csvTemplateDto.getItem() - 1]);
                cache.put("item", csvTemplateDto.getItem());
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
        csvTemplateDto.setHeader(header.getValue() == null || StringUtils.isEmpty(header.getValue().toString()) ? null : Integer.valueOf(header.getValue().toString().substring(3, 4)));

        csvTemplateDto.setItem(Integer.valueOf(item.getValue().toString().substring(3, 4)));
        csvTemplateDto.setUsl(usl.getValue() == null || StringUtils.isEmpty(usl.getValue().toString()) ? null : Integer.valueOf(usl.getValue().toString().substring(3, 4)));
        csvTemplateDto.setLsl(lsl.getValue() == null || StringUtils.isEmpty(lsl.getValue().toString()) ? null : Integer.valueOf(lsl.getValue().toString().substring(3, 4)));
        csvTemplateDto.setUnit(unit.getValue() == null || StringUtils.isEmpty(unit.getValue().toString()) ? null : Integer.valueOf(unit.getValue().toString().substring(3, 4)));
        csvTemplateDto.setData(Integer.valueOf(data.getValue().toString().substring(3, 4)));

        service.saveCsvTemplate(csvTemplateDto);
    }

    private void validate(ComboBox node, String key) {
        if (node.getValue() == null || StringUtils.isEmpty(node.getValue().toString())) {
            cache.remove(key, null);
            return;
        }
        cache.put(key, Integer.valueOf(node.getValue().toString().substring(3, 4)));

        if (data.getValue() != null && !StringUtils.isEmpty(data.getValue().toString())) {
            if (!node.equals(data) && Integer.valueOf(node.getValue().toString().substring(3, 4)) > Integer.valueOf(data.getValue().toString().substring(3, 4))) {
                TooltipUtil.installWarnTooltip(data, "Test Data row must be maximum!");
                data.getStyleClass().add("combo-box-error");
            }
        }
        if (!node.equals(header) && node.getValue().equals(header.getValue())) {
            header.setValue("");
            cache.remove("header");
        } else if (!node.equals(item) && node.getValue().equals(item.getValue())) {
            item.setValue("");
            cache.remove("item");
            TooltipUtil.installWarnTooltip(item, "Can not be empty!");
            item.getStyleClass().add("combo-box-error");
        } else if (!node.equals(usl) && node.getValue().equals(usl.getValue())) {
            usl.setValue("");
            cache.remove("usl");
        } else if (!node.equals(unit) && node.getValue().equals(unit.getValue())) {
            unit.setValue("");
            cache.remove("unit");
        } else if (!node.equals(lsl) && node.getValue().equals(lsl.getValue())) {
            lsl.setValue("");
            cache.remove("lsl");
        } else if (!node.equals(data) && node.getValue().equals(data.getValue())) {
            data.setValue("");
            TooltipUtil.installWarnTooltip(data, "Can not be empty!");
            data.getStyleClass().add("combo-box-error");
        }
    }
}
