/*
 * Copyright (c) 2016. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Guang.Li on 2018/2/27.
 */
public class AdvanceHelpModel {
    private StringProperty operator;
    private StringProperty description;

    public AdvanceHelpModel(String operator, String description) {
        this.operator = new SimpleStringProperty(operator);
        this.description = new SimpleStringProperty(description);
    }

    public String getOperator() {
        return operator.get();
    }

    public void setOperator(String operator) {
        this.operator.set(operator);
    }

    public StringProperty operatorProperty() {
        return operator;
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public StringProperty descriptionProperty() {
        return description;
    }
}
