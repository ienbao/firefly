package com.dmsoft.firefly.sdk.dai.entity;

public class CellData {

    private String lineNo;
    private Object value;

    public CellData() {

    }

    public String getLineNo() {
        return lineNo;
    }

    public void setLineNo(String lineNo) {
        this.lineNo = lineNo;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
