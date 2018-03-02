/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.gui.components.table.TableMenuRowEvent;
import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.plugin.spc.dto.SpcStatsDto;
import com.dmsoft.firefly.plugin.spc.model.ChooseTableRowData;
import com.dmsoft.firefly.plugin.spc.model.StatisticalTableModel;
import com.dmsoft.firefly.plugin.spc.model.StatisticalTableRowData;
import com.dmsoft.firefly.plugin.spc.utils.*;
import com.google.common.collect.Lists;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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

    private SpcMainController spcMainController;

    private ChooseDialogController chooseDialogController;

    private StatisticalTableModel statisticalTableModel;

    private List<String> selectStatisticalResultName = Lists.newArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.buildChooseColumnDialog();
        this.initStatisticalResultTable();
        this.initBtnIcon();
        this.initComponentEvent();
    }

    /**
     * init main controller
     *
     * @param spcMainController main controller
     */
    public void init(SpcMainController spcMainController) {
        this.spcMainController = spcMainController;
    }

    /**
     * set statistical result table data
     *
     * @param list the data list
     */
    public void setStatisticalResultTableData(List<SpcStatsDto> list) {
        statisticalTableModel.initData(list);
    }

    private void buildChooseColumnDialog() {
        FXMLLoader fxmlLoader = FXMLLoaderUtils.getInstance().getLoaderFXML(ViewResource.SPC_CHOOSE_STATISTICAL_VIEW_RES);
        Pane root = null;
        try {
            root = fxmlLoader.load();
            chooseDialogController = fxmlLoader.getController();
            chooseDialogController.setValueColumnText("Statistical Result");
            this.initChooseStatisticalResultTableData();
            WindowFactory.createSimpleWindowAsModel("spcStatisticalResult", "Choose Statistical Results", root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initChooseStatisticalResultTableData() {
        List<String> value = asList(UIConstant.SPC_CHOOSE_RESULT);
        List<ChooseTableRowData> chooseTableRowDataList = Lists.newArrayList();
        value.forEach(v -> {
            ChooseTableRowData chooseTableRowData = new ChooseTableRowData(false, v);
            chooseTableRowDataList.add(chooseTableRowData);
        });
        chooseDialogController.setTableData(chooseTableRowDataList);
    }

    private void initStatisticalResultTable() {
////        checkBoxColumn.setCellFactory(p -> new CheckBoxTableCell<>());
//        checkBoxColumn.setCellValueFactory(cellData -> cellData.getValue().getSelector().getCheckBox());
//        allCheckBox = new CheckBox();
//        checkBoxColumn.setGraphic(allCheckBox);
////        checkBoxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkBoxColumn));
//        List<String> colName = asList(UIConstant.SPC_SR_ALL);
//        StringConverter<String> sc = new StringConverter<String>() {
//            @Override
//            public String toString(String t) {
//                return t == null ? null : t.toString();
//            }
//
//            @Override
//            public String fromString(String string) {
//                return string;
//            }
//        };
//        for (String columnN : colName) {
//            TableColumn<StatisticalTableRowData, String> col = new TableColumn();
//            col.setText(columnN);
//            col.setCellValueFactory(cellData -> cellData.getValue().getRowDataMap().get(columnN));
//            statisticalResultTb.getColumns().add(col);
//
//            if (columnN.equals("LSL") || columnN.equals("USL")) {
//                col.setEditable(true);
//                col.setCellFactory(TextFieldTableCell.forTableColumn(sc));
//            }
//        }
//
//        statisticalTableRowDataObservableList = FXCollections.observableArrayList();
//        statisticalTableRowDataFilteredList = statisticalTableRowDataObservableList.filtered(p -> true);
//        statisticalTableRowDataSortedList = new SortedList<>(statisticalTableRowDataFilteredList);
//        statisticalResultTb.setItems(statisticalTableRowDataSortedList);
//        statisticalTableRowDataSortedList.comparatorProperty().bind(statisticalResultTb.comparatorProperty());

        statisticalTableModel = new StatisticalTableModel();
        TableViewWrapper wrapper = new TableViewWrapper(statisticalResultTb, statisticalTableModel);
        ChooseColorMenuEvent colorMenuEvent = new ChooseColorMenuEvent();
        wrapper.addTableRowEvent(colorMenuEvent);
        List<String> editedStyleClass = new ArrayList<>();
        List<String> errorStyleClass = new ArrayList<>();
        errorStyleClass.add("error");
        editedStyleClass.add("edited");
        wrapper.addEditedCellStyleClass(editedStyleClass);
        wrapper.addCustomCellStyleClass("GG", "HH", errorStyleClass);

        wrapper.update();

        selectStatisticalResultName.addAll(Arrays.asList(UIConstant.SPC_CHOOSE_RESULT));
    }

    private void initComponentEvent() {
        chooseColumnBtn.setOnAction(event -> getChooseColumnBtnEvent());
        filterTestItemTf.textProperty().addListener((observable, oldValue, newValue) -> getFilterTestItemTfEvent());
        chooseDialogController.getChooseOkButton().setOnAction(event -> getChooseStatisticalResultEvent());
    }

    private void getChooseColumnBtnEvent() {
        chooseDialogController.setSelectResultName(selectStatisticalResultName);
        StageMap.showStage("spcStatisticalResult");
    }

    private void getFilterTestItemTfEvent() {
        statisticalTableModel.filterTestItem(filterTestItemTf.getText());
    }

    private void getChooseStatisticalResultEvent(){
        selectStatisticalResultName = chooseDialogController.getSelectResultName();
        statisticalTableModel.updateStatisticalResultColumn(selectStatisticalResultName);
        StageMap.closeStage("spcStatisticalResult");
    }

    private void initBtnIcon() {
        chooseColumnBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_choose_test_items_normal.png")));
    }

    class ChooseColorMenuEvent implements TableMenuRowEvent{

        @Override
        public String getMenuName() {
            return ResourceBundleUtils.getString(ResourceMassages.CHOOSE_COLOR_MENU);
        }

        @Override
        public void handleAction(String rowKey, ActionEvent event) {
            System.out.println("select color");
        }
    }

}
