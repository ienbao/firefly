/*
 * Copyright (c) 2016. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.plugin.spc.model.AdvanceHelpModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Guang.Li on 2018/2/27.
 */
public class AdvanceDiaController implements Initializable {
    private final ObservableList<AdvanceHelpModel> helpItems = FXCollections.observableArrayList(
            new AdvanceHelpModel(">", "Greater than"),
            new AdvanceHelpModel("<", "Less than"),
            new AdvanceHelpModel("=", "Equal to"),
            new AdvanceHelpModel(">=", "Greater than or equal to"),
            new AdvanceHelpModel("<=", "Less then or equal to"),
            new AdvanceHelpModel("%=", "Like")
    );
    @FXML
    private TableColumn<AdvanceHelpModel, String> operator;
    @FXML
    private TableColumn<AdvanceHelpModel, String> description;
    @FXML
    private TableView helpTable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        operator.setCellValueFactory(cellData -> cellData.getValue().operatorProperty());
        description.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

        helpTable.setItems(helpItems);
    }
}
