/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.gui.model;

import com.dmsoft.firefly.gui.utils.TableCheckBox;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by Ethan.Yang on 2018/2/11.
 */
public class ChooseTableRowData {
    private TableCheckBox selector = new TableCheckBox();
    private SimpleStringProperty value;
    private double progress;
    private boolean isImport = false;
    private boolean error = false;

    /**
     * constructor
     *
     * @param isSelect is selected or not
     * @param value    value
     */
    public ChooseTableRowData(boolean isSelect, String value) {
        selector.setValue(isSelect);
        this.value = new SimpleStringProperty(value);
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public boolean isImport() {
        return isImport;
    }

    public void setImport(boolean anImport) {
        isImport = anImport;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public TableCheckBox getSelector() {
        return selector;
    }

    public void setSelector(TableCheckBox selector) {
        this.selector = selector;
    }

    public boolean isSelect() {
        return selector.isSelected();
    }

    public String getValue() {
        return value.get();
    }

    /**
     * method to set value
     *
     * @param value value
     */
    public void setValue(String value) {
        this.value.set(value);
    }

    /**
     * method to get value property
     *
     * @return string property
     */
    public SimpleStringProperty valueProperty() {
        return value;
    }

    /**
     * method to judge contains substring or not
     *
     * @param rex rex
     * @return true : contain; false : not contain
     */
    public boolean containsRex(String rex) {
        return value.getValue().contains(rex);
    }
}
