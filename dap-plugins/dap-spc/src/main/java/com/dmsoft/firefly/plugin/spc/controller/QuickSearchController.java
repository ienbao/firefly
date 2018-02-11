/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

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


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.initComponentEvent();
    }

    private void initComponentEvent() {
        allDataRadioBtn.setOnAction(event -> getAllDataRadioBtnEvent());
        withinRangeRadioBtn.setOnAction(event -> getWithinRangeRadioBtnEvent());
        withoutRangeRadioBtn.setOnAction(event -> getWithoutRangeRadioBtnEvent());
        searchBtn.setOnAction(event -> getSearchBtnEvent());
    }

    private void getSearchBtnEvent(){

    }

    private void getAllDataRadioBtnEvent() {
        this.setWithinRangeTextFieldDisable(allDataRadioBtn.isSelected());
        this.setWithoutRangeTextFieldDisable(allDataRadioBtn.isSelected());
    }

    private void getWithinRangeRadioBtnEvent() {
        this.setWithinRangeTextFieldDisable(!withinRangeRadioBtn.isSelected());
        this.setWithoutRangeTextFieldDisable(withinRangeRadioBtn.isSelected());
    }

    private void getWithoutRangeRadioBtnEvent() {
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
}
