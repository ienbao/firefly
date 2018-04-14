/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.gui.model;

import com.dmsoft.firefly.gui.utils.TableCheckBox;
import com.dmsoft.firefly.sdk.plugin.PluginInfo;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * Created by Ethan.Yang on 2018/2/11.
 */
public class PluginTableRowData {
    private TableCheckBox selector = new TableCheckBox();
    private SimpleStringProperty value;
    private PluginInfo info;

    /**
     * constructor
     *
     * @param isSelect is selcted
     * @param value    value
     * @param info     plugin info
     */
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
     * method to judge contain rex or not
     *
     * @param rex rex string
     * @return true : contains; false : not constains
     */
    public boolean containsRex(String rex) {
        return value.getValue().contains(rex);
    }

    /**
     * method to set on action
     *
     * @param value event handler
     */
    public void setOnAction(EventHandler<ActionEvent> value) {
        selector.getCheckBox().getValue().setOnAction(value);
    }

    public PluginInfo getInfo() {
        return info;
    }

    public void setInfo(PluginInfo info) {
        this.info = info;
    }
}
