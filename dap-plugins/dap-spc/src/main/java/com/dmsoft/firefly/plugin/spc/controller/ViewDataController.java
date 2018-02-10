/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.plugin.spc.dto.SpcViewDataDto;
import com.dmsoft.firefly.plugin.spc.model.StatisticalTableRowData;
import com.dmsoft.firefly.plugin.spc.model.ViewDataRowData;
import com.dmsoft.firefly.plugin.spc.utils.ImageUtils;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Ethan.Yang on 2018/2/2.
 */
public class ViewDataController implements Initializable {
    @FXML
    private Button clearFilterBtn;
    @FXML
    private Button chooseItemBtn;
    @FXML
    private CheckBox unSelectedCheckBox;
    @FXML
    private TextField filterTf;
    @FXML
    private TableView viewDataTable;
    @FXML
    private TableColumn<ViewDataRowData,CheckBox> checkBoxColumn;
    private CheckBox allCheckBox;
    private SpcMainController spcMainController;

    private ObservableList<ViewDataRowData> viewDataRowDataObservableList;
    private FilteredList<ViewDataRowData> viewDataRowDataFilteredList;
    private SortedList<ViewDataRowData> viewDataRowDataSortedList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.initBtnIcon();
        this.initViewDataTable();
        this.initComponentEvent();
    }

    public void setViewData(List<SpcViewDataDto> spcViewDataDtoList){
        if(spcViewDataDtoList == null){
            return;
        }
        this.clearViewDataTable();
        if(spcViewDataDtoList.get(0) != null){
            Map<String,Object> data = spcViewDataDtoList.get(0).getTestData();
            data.forEach((String,Object) ->{
                TableColumn<ViewDataRowData, String> col = new TableColumn();
                col.setText(String);
                col.setCellValueFactory(cellData -> cellData.getValue().getRowDataMap().get(String));
                viewDataTable.getColumns().add(col);
            });
        }
        spcViewDataDtoList.forEach(dto -> {
            viewDataRowDataObservableList.add(new ViewDataRowData(dto));
        });
    }

    public void clearViewDataTable(){
        viewDataTable.getColumns().remove(1,viewDataTable.getColumns().size());
        viewDataRowDataObservableList.clear();
        allCheckBox.setSelected(false);
    }

    private void initViewDataTable(){
        checkBoxColumn.setCellValueFactory(cellData -> cellData.getValue().getSelector().getCheckBox());
        allCheckBox = new CheckBox();
        checkBoxColumn.setGraphic(allCheckBox);

        viewDataRowDataObservableList = FXCollections.observableArrayList();
        viewDataRowDataFilteredList = viewDataRowDataObservableList.filtered(p -> true);
        viewDataRowDataSortedList = new SortedList<>(viewDataRowDataFilteredList);
        viewDataTable.setItems(viewDataRowDataSortedList);
        viewDataRowDataSortedList.comparatorProperty().bind(viewDataTable.comparatorProperty());
    }

    private void initComponentEvent() {
        clearFilterBtn.setOnAction(event -> getClearFilterBtnEvent());
        filterTf.textProperty().addListener((observable, oldValue, newValue) -> getFilterTextFieldEvent());
        chooseItemBtn.setOnAction(event -> getChooseColumnBtnEvent());
        unSelectedCheckBox.setOnAction(event -> getUnSelectedCheckBoxEvent());
        allCheckBox.setOnAction(event -> getAllSelectEvent());
    }

    private void getClearFilterBtnEvent(){

    }

    private void getFilterTextFieldEvent(){
        viewDataRowDataFilteredList.setPredicate(p ->
            p.containsRex(filterTf.getText())
        );
    }

    private void getChooseColumnBtnEvent(){

    }

    private void getUnSelectedCheckBoxEvent(){
        if (viewDataRowDataObservableList != null) {
            for (ViewDataRowData rowData : viewDataRowDataObservableList) {
                rowData.getSelector().setValue(!rowData.getSelector().isSelected());
            }
        }
    }

    private void getAllSelectEvent(){
        if (viewDataRowDataSortedList != null) {
            for (ViewDataRowData rowData : viewDataRowDataSortedList) {
                rowData.getSelector().setValue(allCheckBox.isSelected());
            }
        }
    }

    public void init(SpcMainController spcMainController) {
        this.spcMainController = spcMainController;
    }

    private void initBtnIcon(){
        clearFilterBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_clear_filter_normal.png")));
        chooseItemBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_choose_test_items_normal.png")));
    }

}
