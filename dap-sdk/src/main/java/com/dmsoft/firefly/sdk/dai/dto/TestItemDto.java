package com.dmsoft.firefly.sdk.dai.dto;

/**
 * Created by GuangLi on 2018/1/24.
 */
public class TestItemDto {
    private String projectName;
    private String itemName;
    private Boolean isNumeric;
    private String lsl;
    private String usl;
    private String unit;

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

    public Boolean getNumeric() {
        return isNumeric;
    }

    public void setNumeric(Boolean numeric) {
        isNumeric = numeric;
    }

    public String getLsl() {
        return lsl;
    }

    public void setLsl(String lsl) {
        this.lsl = lsl;
    }

    public String getUsl() {
        return usl;
    }

    public void setUsl(String usl) {
        this.usl = usl;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
