package com.dmsoft.firefly.sdk.dai.dto;

import java.util.Map;

public class NewRowDataDto {
    private String rowKey;
    private Boolean inUsed;
    private Map<String, String> data;

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public Boolean getInUsed() {
        return inUsed;
    }

    public void setInUsed(Boolean inUsed) {
        this.inUsed = inUsed;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
