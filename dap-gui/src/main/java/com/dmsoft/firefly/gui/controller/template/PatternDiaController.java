/*
 * Copyright (c) 2016. For Intelligent Group.
 */
package com.dmsoft.firefly.gui.controller.template;

import com.dmsoft.firefly.gui.model.PatternHelpModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * Created by Guang.Li on 2018/2/27.
 */
public class PatternDiaController {
    private final ObservableList<PatternHelpModel> helpItems = FXCollections.observableArrayList(
            new PatternHelpModel("y", "Year"),
            new PatternHelpModel("M", "Month in year"),
            new PatternHelpModel("d", "Day in month"),
            new PatternHelpModel("H", "Hour in day(0-23)"),
            new PatternHelpModel("m", "Minute in hour"),
            new PatternHelpModel("s", "Second in minute"),
            new PatternHelpModel("S", "Microsecond")
    );
    @FXML
    private TableColumn<PatternHelpModel, String> character;
    @FXML
    private TableColumn<PatternHelpModel, String> description;
    @FXML
    private TableView<PatternHelpModel> patternHelp;

    @FXML
    private void initialize() {
        character.setCellValueFactory(cellData -> cellData.getValue().characterProperty());
        description.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        patternHelp.setItems(helpItems);
    }
}
