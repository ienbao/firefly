package com.dmsoft.firefly.sdk.dai.entity;

import org.bson.types.ObjectId;

import java.util.List;

/**
 * Created by cherry on 2018/1/16.
 */
public class TestData {

    private ObjectId id;
    private String projectName;
    private String itemName;
    private String usl;
    private String lsl;
    private String unit;
    private List<CellData> data;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getUsl() {
        return usl;
    }

    public void setUsl(String usl) {
        this.usl = usl;
    }

    public String getLsl() {
        return lsl;
    }

    public void setLsl(String lsl) {
        this.lsl = lsl;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public List<CellData> getData() {
        return data;
    }

    public void setData(List<CellData> data) {
        this.data = data;
    }
}
