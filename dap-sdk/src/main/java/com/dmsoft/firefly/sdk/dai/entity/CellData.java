package com.dmsoft.firefly.sdk.dai.entity;

public class CellData {

    private int lineNo;
    private Object value;

    public CellData() {

    }

    public int getLineNo() {
        return lineNo;
    }

    public void setLineNo(int lineNo) {
        this.lineNo = lineNo;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
