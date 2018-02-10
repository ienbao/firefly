/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.plugin.spc.dto.SpcStatisticalResultDto;
import com.dmsoft.firefly.plugin.spc.model.StatisticalTableRowData;
import com.dmsoft.firefly.plugin.spc.utils.ImageUtils;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static java.util.Arrays.asList;

/**
 * Created by Ethan.Yang on 2018/2/2.
 */
public class StatisticalResultController implements Initializable {
    @FXML
    private Button chooseColumnBtn;
    @FXML
    private TextField filterTestItemTf;

    @FXML
    private TableView statisticalResultTb;

    @FXML
    private TableColumn<StatisticalTableRowData,Boolean> checkBoxColumn;

    private ObservableList<StatisticalTableRowData> observableList;


    private SpcMainController spcMainController;

    public void init(SpcMainController spcMainController) {
        this.spcMainController = spcMainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.initStatisticalResultHeader();
        this.initBtnIcon();
        this.initComponentEvent();
        this.initData();
    }

    private void initStatisticalResultHeader(){
        checkBoxColumn.setCellFactory(p -> new CheckBoxTableCell<>());
        checkBoxColumn.setCellValueFactory(cellData -> cellData.getValue().selectorProperty());
        checkBoxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkBoxColumn));
        List<String> colName = asList(UIConstant.SPC_SR_ALL);
        StringConverter<String> sc = new StringConverter<String>() {
            @Override
            public String toString(String t) {
                return t == null ? null : t.toString();
            }

            @Override
            public String fromString(String string) {
                return string;
            }
        };
        for(String columnN : colName){
            TableColumn<StatisticalTableRowData, String> col = new TableColumn();
            col.setText(columnN);
            col.setCellValueFactory(cellData -> cellData.getValue().getRowDataMap().get(columnN));
            statisticalResultTb.getColumns().add(col);

            if(columnN.equals("LSL") || columnN.equals("USL")){
                col.setEditable(true);
                col.setCellFactory(TextFieldTableCell.forTableColumn(sc));
            }
        }
    }

    private void initData() {
        observableList = FXCollections.observableArrayList();
        statisticalResultTb.setItems(observableList);
    }

    public void refreshData(List<SpcStatisticalResultDto> list) {
        list.forEach(dto -> {
            observableList.add(new StatisticalTableRowData(dto));
        });
    }

    private void initComponentEvent() {
        chooseColumnBtn.setOnAction(event -> getChooseColumnBtnEvent());
        filterTestItemTf.setOnAction(event -> getFilterTestItemTfEvent());
    }

    private void getChooseColumnBtnEvent() {

    }

    private void getFilterTestItemTfEvent() {

    }

    public ObservableList<StatisticalTableRowData> getObservableList() {
        return observableList;
    }

    public void setObservableList(ObservableList<StatisticalTableRowData> observableList) {
        this.observableList = observableList;
    }

    private void initBtnIcon() {
        chooseColumnBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_choose_test_items_normal.png")));
    }


}

