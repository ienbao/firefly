/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.plugin.spc.utils.FilterType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Ethan.Yang on 2018/2/11.
 */
public class QuickSearchController implements Initializable {
    @FXML
    private RadioButton allDataRadioBtn;
    @FXML
    private RadioButton withinRangeRadioBtn;
    @FXML
    private RadioButton withoutRangeRadioBtn;
    @FXML
    private TextField withinLowerTf;
    @FXML
    private TextField withinUpperTf;
    @FXML
    private TextField withoutLowerTf;
    @FXML
    private TextField withoutUpperTf;

    @FXML
    private ToggleGroup toggleGroup;
    @FXML
    private Button searchBtn;
    @FXML
    private Button cancelBtn;

    private Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.initComponentEvent();
    }

    private void initComponentEvent() {
        allDataRadioBtn.setOnAction(event -> activeAllData());
        withinRangeRadioBtn.setOnAction(event -> activeWithinRange());
        withoutRangeRadioBtn.setOnAction(event -> activeWithoutRange());
        searchBtn.setOnAction(event -> getSearchBtnEvent());
    }

    private void getSearchBtnEvent() {

    }

    /**
     * method to active all data radio btn
     */
    public void activeAllData() {
        this.setWithinRangeTextFieldDisable(allDataRadioBtn.isSelected());
        this.setWithoutRangeTextFieldDisable(allDataRadioBtn.isSelected());
    }

    /**
     * method to active without range radio btn
     */
    public void activeWithinRange() {
        this.setWithinRangeTextFieldDisable(!withinRangeRadioBtn.isSelected());
        this.setWithoutRangeTextFieldDisable(withinRangeRadioBtn.isSelected());
    }

    /**
     * method to active without range radio btn
     */
    public void activeWithoutRange() {
        this.setWithinRangeTextFieldDisable(withoutRangeRadioBtn.isSelected());
        this.setWithoutRangeTextFieldDisable(!withoutRangeRadioBtn.isSelected());
    }

    private void setWithinRangeTextFieldDisable(boolean isDisable) {
        withinLowerTf.setDisable(isDisable);
        withinUpperTf.setDisable(isDisable);
    }

    private void setWithoutRangeTextFieldDisable(boolean isDisable) {
        withoutLowerTf.setDisable(isDisable);
        withoutUpperTf.setDisable(isDisable);
    }

    public Button getSearchBtn() {
        return searchBtn;
    }

    public Button getCancelBtn() {
        return cancelBtn;
    }

    public TextField getWithinLowerTf() {
        return withinLowerTf;
    }

    public TextField getWithinUpperTf() {
        return withinUpperTf;
    }

    public TextField getWithoutLowerTf() {
        return withoutLowerTf;
    }

    public TextField getWithoutUpperTf() {
        return withoutUpperTf;
    }

    /**
     * method to get filter type
     *
     * @return filter type
     */
    public FilterType getFilterType() {
        if (this.allDataRadioBtn.isSelected()) {
            return FilterType.ALL_DATA;
        }
        if (this.withinRangeRadioBtn.isSelected()) {
            return FilterType.WITHIN_RANGE;
        }
        return FilterType.WITHOUT_RANGE;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
