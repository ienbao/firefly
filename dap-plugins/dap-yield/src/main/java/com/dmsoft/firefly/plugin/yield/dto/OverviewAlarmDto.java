package com.dmsoft.firefly.plugin.yield.dto;

public class OverviewAlarmDto {
    private Double value;
    private String level;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
