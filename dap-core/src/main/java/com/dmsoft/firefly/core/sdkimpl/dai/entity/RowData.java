package com.dmsoft.firefly.core.sdkimpl.dai.entity;

import org.bson.types.ObjectId;

import java.util.Map;

/**
 * entity class for row data
 *
 * @author Can Guan
 */
public class RowData {
    private ObjectId id;
    private String rowKey;
    private Boolean inUsed;
    private Map<String, String> data;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

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
