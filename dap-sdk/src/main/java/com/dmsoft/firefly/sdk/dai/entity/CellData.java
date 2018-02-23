package com.dmsoft.firefly.sdk.dai.entity;

public class CellData {

    private String rowKey;
    private Object value;

    public CellData() {

    }

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
