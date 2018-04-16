
/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.gui.controller.template;

import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.gui.model.ChooseTableRowData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Ethan.Yang on 2018/2/11.
 */
public class ChooseColDialogController implements Initializable {

    @FXML
    private TextFieldFilter chooseFilterTf;
    @FXML
    private CheckBox chooseUnSelected;
    @FXML
    private TableView<ChooseTableRowData> chooseColumnTable;
    @FXML
    private TableColumn<ChooseTableRowData, CheckBox> chooseCheckBoxColumn;
    @FXML
    private TableColumn<ChooseTableRowData, String> chooseValueColumn;
    @FXML
    private Button chooseOkButton;
    private CheckBox allCheckBox;

    private ObservableList<ChooseTableRowData> chooseTableRowDataObservableList;
    private FilteredList<ChooseTableRowData> chooseTableRowDataFilteredList;
    private SortedList<ChooseTableRowData> chooseTableRowDataSortedList;
    private Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.initTable();
        this.initComponentEvent();
    }

    /**
     * initTable.
     */
    private void initTable() {
        allCheckBox = new CheckBox();
        chooseCheckBoxColumn.setGraphic(allCheckBox);

        chooseCheckBoxColumn.setCellValueFactory(cellData -> cellData.getValue().getSelector().getCheckBox());
        chooseValueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
        chooseTableRowDataObservableList = FXCollections.observableArrayList();
        chooseTableRowDataFilteredList = chooseTableRowDataObservableList.filtered(p -> true);
        chooseTableRowDataSortedList = new SortedList<>(chooseTableRowDataFilteredList);
        chooseColumnTable.setItems(chooseTableRowDataSortedList);
        chooseTableRowDataSortedList.comparatorProperty().bind(chooseColumnTable.comparatorProperty());

    }

    /**
     * initComponentEvent.
     */
    private void initComponentEvent() {
        chooseFilterTf.getTextField().textProperty().addListener((observable, oldValue, newValue) -> getFilterValueEvent());
        chooseUnSelected.setOnAction(event -> getUnSelectedCheckBoxEvent());
        allCheckBox.setOnAction(event -> getAllSelectEvent());
    }

    /**
     * getFilterValueEvent.
     */
    private void getFilterValueEvent() {
        chooseTableRowDataFilteredList.setPredicate(p ->
                p.getValue().toLowerCase().contains(chooseFilterTf.getTextField().getText().toLowerCase())
        );
    }

    /**
     * getUnSelectedCheckBoxEvent.
     */
    private void getUnSelectedCheckBoxEvent() {
        if (chooseTableRowDataObservableList != null) {
            for (ChooseTableRowData data : chooseTableRowDataObservableList) {
                data.getSelector().setValue(!data.getSelector().isSelected());
            }
        }
    }

    /**
     * getAllSelectEvent.
     */
    private void getAllSelectEvent() {
        if (chooseTableRowDataSortedList != null) {
            for (ChooseTableRowData data : chooseTableRowDataSortedList) {
                data.getSelector().setValue(allCheckBox.isSelected());
            }
        }
    }

    /**
     * getStage
     * @return stage stage
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * setStage
     * @param stage stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
