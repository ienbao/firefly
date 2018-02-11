/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.plugin.spc.dto.SpcViewDataDto;
import com.dmsoft.firefly.plugin.spc.model.ViewDataRowData;
import com.dmsoft.firefly.plugin.spc.utils.FXMLLoaderUtils;
import com.dmsoft.firefly.plugin.spc.utils.ImageUtils;
import com.dmsoft.firefly.plugin.spc.utils.ViewResource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.io.IOException;
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
    private TableColumn<ViewDataRowData, CheckBox> checkBoxColumn;
    private CheckBox allCheckBox;
    private SpcMainController spcMainController;

    private ObservableList<ViewDataRowData> viewDataRowDataObservableList;
    private FilteredList<ViewDataRowData> viewDataRowDataFilteredList;
    private SortedList<ViewDataRowData> viewDataRowDataSortedList;

    private QuickSearchController quickSearchController;
    private ChooseDialogController chooseDialogController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.buildQuickSearchDialog();
        this.buildChooseColumnDialog();
        this.initBtnIcon();
        this.initViewDataTable();
        this.initComponentEvent();
    }

    /**
     * set view data table dataList
     * @param spcViewDataDtoList the data list
     */
    public void setViewData(List<SpcViewDataDto> spcViewDataDtoList) {
        if (spcViewDataDtoList == null) {
            return;
        }
        this.clearViewDataTable();
        if (spcViewDataDtoList.get(0) != null) {
            Map<String, Object> data = spcViewDataDtoList.get(0).getTestData();
            data.forEach((string, object) -> {
                this.buildViewDataColumn(string);
            });
        }
        spcViewDataDtoList.forEach(dto -> {
            viewDataRowDataObservableList.add(new ViewDataRowData(dto));
        });
    }

    /**
     * clear view data Table
     */
    public void clearViewDataTable() {
        viewDataTable.getColumns().remove(1, viewDataTable.getColumns().size());
        viewDataRowDataObservableList.clear();
        allCheckBox.setSelected(false);
    }

    private void buildViewDataColumn(String title) {
        TableColumn<ViewDataRowData, String> col = new TableColumn();
        Label label = new Label(title);
        Button filterButton = new Button();
        filterButton.setPrefSize(20, 20);
        filterButton.setOnAction(event -> getFilterBtnEvent());
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().addAll(label, filterButton);
        col.setGraphic(hBox);
//        col.setText(title);
        col.setCellValueFactory(cellData -> cellData.getValue().getRowDataMap().get(title));
        viewDataTable.getColumns().add(col);
    }

    private void buildQuickSearchDialog() {
        FXMLLoader fxmlLoader = FXMLLoaderUtils.getInstance().getLoaderFXML(ViewResource.SPC_QUICK_SEARCH_VIEW_RES);
        Pane root = null;
        try {
            root = fxmlLoader.load();
            quickSearchController = fxmlLoader.getController();
            WindowFactory.createSimpleWindowAsModel("spcQuickSearch", "Quick Search", root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildChooseColumnDialog() {
        FXMLLoader fxmlLoader = FXMLLoaderUtils.getInstance().getLoaderFXML(ViewResource.SPC_CHOOSE_STATISTICAL_VIEW_RES);
        Pane root = null;
        try {
            root = fxmlLoader.load();
            chooseDialogController = fxmlLoader.getController();
            WindowFactory.createSimpleWindowAsModel("spcViewDataColumn", "Choose Test Items", root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initViewDataTable() {
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
        quickSearchController.getCancelBtn().setOnAction(event -> closeQuickSearchDialogEvent());
    }

    private void getClearFilterBtnEvent() {

    }

    private void getFilterTextFieldEvent() {
        viewDataRowDataFilteredList.setPredicate(p ->
                p.containsRex(filterTf.getText())
        );
    }

    private void getChooseColumnBtnEvent() {
        StageMap.showStage("spcViewDataColumn");
    }

    private void closeQuickSearchDialogEvent(){
        StageMap.closeStage("spcQuickSearch");
    }

    private void getUnSelectedCheckBoxEvent() {
        if (viewDataRowDataObservableList != null) {
            for (ViewDataRowData rowData : viewDataRowDataObservableList) {
                rowData.getSelector().setValue(!rowData.getSelector().isSelected());
            }
        }
    }

    private void getAllSelectEvent() {
        if (viewDataRowDataSortedList != null) {
            for (ViewDataRowData rowData : viewDataRowDataSortedList) {
                rowData.getSelector().setValue(allCheckBox.isSelected());
            }
        }
    }

    private void getFilterBtnEvent() {
        StageMap.showStage("spcQuickSearch");
    }

    /**
     * init main controller
     *
     * @param spcMainController main controller
     */
    public void init(SpcMainController spcMainController) {
        this.spcMainController = spcMainController;
    }

    private void initBtnIcon() {
        clearFilterBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_clear_filter_normal.png")));
        chooseItemBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_choose_test_items_normal.png")));
    }

}
