/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.gui.components.utils.TextFieldWrapper;
import com.dmsoft.firefly.gui.components.utils.ValidateRule;
import com.dmsoft.firefly.gui.components.utils.ValidateUtils;
import com.dmsoft.firefly.plugin.spc.utils.FilterType;
import com.dmsoft.firefly.plugin.spc.utils.ResourceMassages;
import com.dmsoft.firefly.plugin.spc.utils.SpcFxmlAndLanguageUtils;
import com.dmsoft.firefly.plugin.spc.utils.SpcSettingValidateUtil;
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
        this.validRangeText();
    }

    private void validRangeText(){
        ValidateRule rule = new ValidateRule();
        rule.setMaxLength(SpcSettingValidateUtil.ANALYSIS_SETTING_MAX_INT);
        rule.setPattern(ValidateUtils.DOUBLE_PATTERN);
        rule.setErrorStyle("text-field-error");
        rule.setEmptyErrorMsg(SpcFxmlAndLanguageUtils.getString(ResourceMassages.SPC_VALIDATE_NOT_BE_EMPTY));
        TextFieldWrapper.decorate(withinLowerTf, rule);
        TextFieldWrapper.decorate(withinUpperTf, rule);

        TextFieldWrapper.decorate(withoutLowerTf, rule);
        TextFieldWrapper.decorate(withoutUpperTf, rule);
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
        this.allDataRadioBtn.setSelected(true);
        this.setWithinRangeTextFieldDisable(allDataRadioBtn.isSelected());
        this.setWithoutRangeTextFieldDisable(allDataRadioBtn.isSelected());
    }

    /**
     * method to active without range radio btn
     */
    public void activeWithinRange() {
        this.withinRangeRadioBtn.setSelected(true);
        this.setWithinRangeTextFieldDisable(!withinRangeRadioBtn.isSelected());
        this.setWithoutRangeTextFieldDisable(withinRangeRadioBtn.isSelected());
    }

    /**
     * method to active without range radio btn
     */
    public void activeWithoutRange() {
        this.withoutRangeRadioBtn.setSelected(true);
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

    public boolean isError(){
        if(withinRangeRadioBtn.isSelected()){
            if (withinUpperTf.getStyleClass().contains("text-field-error") || withinLowerTf.getStyleClass().contains("text-field-error")) {
                return true;
            }
        } else if(withoutRangeRadioBtn.isSelected()){
            if (withoutLowerTf.getStyleClass().contains("text-field-error") || withoutUpperTf.getStyleClass().contains("text-field-error")) {
                return true;
            }
        }
        return false;
    }
}
