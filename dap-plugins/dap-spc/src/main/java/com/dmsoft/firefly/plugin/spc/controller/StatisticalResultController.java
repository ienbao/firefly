/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.gui.components.window.WindowPane;
import com.dmsoft.firefly.plugin.spc.dto.SpcStatisticalResultDto;
import com.dmsoft.firefly.plugin.spc.model.StatisticalTableRowData;
import com.dmsoft.firefly.plugin.spc.utils.ImageUtils;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

import java.io.IOException;
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
    private TableColumn<StatisticalTableRowData,CheckBox> checkBoxColumn;

    private ObservableList<StatisticalTableRowData> statisticalTableRowDataObservableList;
    private FilteredList<StatisticalTableRowData> statisticalTableRowDataFilteredList;
    private SortedList<StatisticalTableRowData> statisticalTableRowDataSortedList;


    private SpcMainController spcMainController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.buildChooseColumnDialog();
        this.initStatisticalResultTable();
        this.initBtnIcon();
        this.initComponentEvent();
    }

    public void init(SpcMainController spcMainController) {
        this.spcMainController = spcMainController;
    }


    public void setStatisticalResultTableData(List<SpcStatisticalResultDto> list) {
        statisticalTableRowDataObservableList.clear();
        allCheckBox.setSelected(false);
        list.forEach(dto -> {
            statisticalTableRowDataObservableList.add(new StatisticalTableRowData(dto));
        });
    }

    private void buildChooseColumnDialog(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("view/choose_statistical_result.fxml"), ResourceBundle.getBundle("i18n.message_en_US"));
//        fxmlLoader.setClassLoader(RuntimeContext.getBean(PluginContext.class).getDAPClassLoader("com.dmsoft.dap.CsvResolverPlugin"));
        Pane root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        chooseDialog = new Stage();
        WindowPane windowPane = new WindowPane(chooseDialog, "Choose Statistical Results", root);

        Scene scene =  new Scene(windowPane, 430, 370);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/app.css").toExternalForm());

        chooseDialog.initStyle(StageStyle.TRANSPARENT);
        chooseDialog.setScene(scene);
//        chooseDialog.setAlwaysOnTop(true);
        chooseDialog.setResizable(false);
        windowPane.init();

    }

    private void initStatisticalResultTable(){
//        checkBoxColumn.setCellFactory(p -> new CheckBoxTableCell<>());
        checkBoxColumn.setCellValueFactory(cellData -> cellData.getValue().getSelector().getCheckBox());
        allCheckBox = new CheckBox();
        checkBoxColumn.setGraphic(allCheckBox);
//        checkBoxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkBoxColumn));
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

        statisticalTableRowDataObservableList = FXCollections.observableArrayList();
        statisticalTableRowDataFilteredList = statisticalTableRowDataObservableList.filtered(p -> true);
        statisticalTableRowDataSortedList = new SortedList<>(statisticalTableRowDataFilteredList);
        statisticalResultTb.setItems(statisticalTableRowDataSortedList);
        statisticalTableRowDataSortedList.comparatorProperty().bind(statisticalResultTb.comparatorProperty());
    }

    private void initComponentEvent() {
        chooseColumnBtn.setOnAction(event -> getChooseColumnBtnEvent());
        filterTestItemTf.textProperty().addListener((observable, oldValue, newValue) -> getFilterTestItemTfEvent());
        allCheckBox.setOnAction(event -> getAllSelectEvent());
    }

    private void getChooseColumnBtnEvent() {
        chooseDialog.show();
    }

    private void getFilterTestItemTfEvent() {
        statisticalTableRowDataFilteredList.setPredicate(p -> p.getRowDataMap().get(UIConstant.TEST_ITEM).getValue().contains(filterTestItemTf.getText()));
    }

    private void getAllSelectEvent(){
        if (statisticalTableRowDataSortedList != null) {
            for (StatisticalTableRowData rowData : statisticalTableRowDataSortedList) {
                rowData.getSelector().setValue(allCheckBox.isSelected());
            }
        }
    }


    private void initBtnIcon() {
        chooseColumnBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_choose_test_items_normal.png")));
    }

    private Stage chooseDialog;
    private CheckBox allCheckBox;
}
