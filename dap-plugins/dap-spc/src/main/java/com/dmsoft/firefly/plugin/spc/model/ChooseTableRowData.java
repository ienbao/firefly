/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.model;

import com.dmsoft.firefly.plugin.spc.utils.TableCheckBox;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by Ethan.Yang on 2018/2/11.
 */
public class ChooseTableRowData {
    private TableCheckBox selector = new TableCheckBox();
    private SimpleStringProperty value;

    public ChooseTableRowData(boolean isSelect, String value) {
        this.value = new SimpleStringProperty(value);
    }

    public TableCheckBox getSelector() {
        return selector;
    }

    public void setSelector(TableCheckBox selector) {
        this.selector = selector;
    }

    public String getValue() {
        return value.get();
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    public SimpleStringProperty valueProperty() {
        return value;
    }
}
