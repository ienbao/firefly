/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.gui.model;

import com.dmsoft.firefly.gui.utils.TableCheckBox;
import com.dmsoft.firefly.sdk.plugin.PluginInfo;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by Ethan.Yang on 2018/2/11.
 */
public class PluginTableRowData {
    private TableCheckBox selector = new TableCheckBox();
    private SimpleStringProperty value;
    private PluginInfo info;

    public PluginTableRowData(boolean isSelect, String value, PluginInfo info) {
        selector.setValue(isSelect);
        this.value = new SimpleStringProperty(value);
        this.info = info;
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

    public SimpleStringProperty valueProperty() {
        return value;
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    public boolean containsRex(String rex) {
        if (value.getValue().contains(rex)) {
            return true;
        }
        return false;
    }

    public PluginInfo getInfo() {
        return info;
    }

    public void setInfo(PluginInfo info) {
        this.info = info;
    }
}
