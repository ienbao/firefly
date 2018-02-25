/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.gui.controller.template;

import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.model.ChooseTableRowData;
import com.dmsoft.firefly.gui.utils.ImageUtils;
import com.google.common.collect.Lists;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;


/**
 * Created by Garen.Pang on 2018/2/25.
 */
public class DataSourceController implements Initializable {

    @FXML
    private Button addFile, ok, cancel, search, delete;

    @FXML
    private TableView dataSourceTable;

    @FXML
    private TextField filterTf;

    @FXML
    private TableColumn<ChooseTableRowData, CheckBox> chooseCheckBoxColumn;

    @FXML
    private TableColumn<ChooseTableRowData, String> chooseValueColumn;

    private CheckBox allCheckBox;
    private ObservableList<ChooseTableRowData> chooseTableRowDataObservableList;
    private FilteredList<ChooseTableRowData> chooseTableRowDataFilteredList;
    private SortedList<ChooseTableRowData> chooseTableRowDataSortedList;

    private void initTable() {
        search.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_basic_search_normal.png")));
        delete.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_del_normal.png")));

        allCheckBox = new CheckBox();
        chooseCheckBoxColumn.setGraphic(allCheckBox);

        chooseCheckBoxColumn.setCellValueFactory(cellData -> cellData.getValue().getSelector().getCheckBox());
        chooseValueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
        chooseTableRowDataObservableList = FXCollections.observableArrayList();
        chooseTableRowDataFilteredList = chooseTableRowDataObservableList.filtered(p -> true);
        chooseTableRowDataSortedList = new SortedList<>(chooseTableRowDataFilteredList);
        dataSourceTable.setItems(chooseTableRowDataSortedList);
        chooseTableRowDataSortedList.comparatorProperty().bind(dataSourceTable.comparatorProperty());
    }

    private void initEvent() {
        ok.setOnAction(event -> {
            StageMap.closeStage("dataSource");

        });

        cancel.setOnAction(event -> {
            StageMap.closeStage("dataSource");
        });

        allCheckBox.setOnAction(event -> getAllSelectEvent());
        search.setOnAction(event -> {
            getFilterTextFieldEvent();
        });
        delete.setOnAction(event -> {

        });
        addFile.setOnAction(event -> {
            String str = System.getProperty("user.home");
//            if (!StringUtils.isEmpty(path.getText())) {
//                str = path.getText();
//            }
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open");
            fileChooser.setInitialDirectory(new File(str));
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("CSV", "*.csv")
            );
            Stage fileStage = null;
            File file = fileChooser.showOpenDialog(fileStage);
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTable();
        initEvent();
        initChooseStatisticalResultTableData();
    }

    private void getAllSelectEvent() {
        if (chooseTableRowDataSortedList != null) {
            for (ChooseTableRowData data : chooseTableRowDataSortedList) {
                data.getSelector().setValue(allCheckBox.isSelected());
            }
        }
    }

    private void initChooseStatisticalResultTableData() {
        List<String> value = Arrays.asList(new String[]{
                "00000000000001.csv", "00000000000002.csv", "00000000000003.csv", "00000000000004.csv", "00000000000005.csv", "00000000000006.csv", "00000000000007.csv", "00000000000008.csv", "00000000000009.csv", "00000000000010.csv", "00000000000011.csv", "00000000000012.csv", "00000000000013.csv", "00000000000014.csv",
                "00000000000015.csv", "00000000000016.csv", "00000000000017.csv", "00000000000018.csv", "00000000000019.csv", "00000000000020.csv", "00000000000021.csv", "00000000000022.csv", "00000000000023.csv", "00000000000024.csv"
        });
        List<ChooseTableRowData> chooseTableRowDataList = Lists.newArrayList();
        value.forEach(v -> {
            ChooseTableRowData chooseTableRowData = new ChooseTableRowData(false, v);
            chooseTableRowDataList.add(chooseTableRowData);
        });
        setTableData(chooseTableRowDataList);
    }

    public void setTableData(List<ChooseTableRowData> chooseTableRowDataList) {
        chooseTableRowDataObservableList.clear();
        chooseTableRowDataObservableList.addAll(chooseTableRowDataList);
    }

    private void getFilterTextFieldEvent() {
        chooseTableRowDataFilteredList.setPredicate(p ->
                p.containsRex(filterTf.getText())
        );
    }
}
