/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.plugin.spc.model.StatisticalTableRowData;
import com.dmsoft.firefly.plugin.spc.utils.ImageUtils;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.initStatisticalResultHeader();
        this.initBtnIcon();
        this.initComponentEvent();
    }

    private void initStatisticalResultHeader(){
        List<String> colName = asList(UIConstant.SPC_SR_ALL);
        for(String columnN : colName){
            TableColumn<StatisticalTableRowData, String> col = new TableColumn();
            col.setText(columnN);
            col.setCellValueFactory(cellData -> cellData.getValue().getRowDataMap().get(columnN));
            statisticalResultTb.getColumns().add(col);
        }
    }



    private void initComponentEvent() {
        chooseColumnBtn.setOnAction(event -> getChooseColumnBtnEvent());
        filterTestItemTf.setOnAction(event -> getFilterTestItemTfEvent());
    }

    private void getChooseColumnBtnEvent() {

    }

    private void getFilterTestItemTfEvent() {

    }

    private void initBtnIcon() {
        chooseColumnBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_choose_test_items_normal.png")));
    }
}
