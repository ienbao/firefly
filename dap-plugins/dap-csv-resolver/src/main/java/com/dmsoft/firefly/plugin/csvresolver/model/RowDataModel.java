/*
 * Copyright (c) 2016. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.csvresolver.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Guang.Li on 2018/2/3.
 */
public class RowDataModel {
    private StringProperty row;
    private StringProperty col1;
    private StringProperty col2;
    private StringProperty col3;

    public RowDataModel(String row, String[] data) {
        this.row = new SimpleStringProperty(row);
        this.col1 = new SimpleStringProperty(data[0]);
        this.col2 = new SimpleStringProperty(data[1]);
        this.col3 = new SimpleStringProperty(data[2]);
    }

    public String getRow() {
        return row.get();
    }

    public StringProperty rowProperty() {
        return row;
    }

    public void setRow(String row) {
        this.row.set(row);
    }

    public String getCol1() {
        return col1.get();
    }

    public StringProperty col1Property() {
        return col1;
    }

    public void setCol1(String col1) {
        this.col1.set(col1);
    }

    public String getCol2() {
        return col2.get();
    }

    public StringProperty col2Property() {
        return col2;
    }

    public void setCol2(String col2) {
        this.col2.set(col2);
    }

    public String getCol3() {
        return col3.get();
    }

    public StringProperty col3Property() {
        return col3;
    }

    public void setCol3(String col3) {
        this.col3.set(col3);
    }
}
