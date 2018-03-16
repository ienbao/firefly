/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.model;

import com.dmsoft.firefly.plugin.spc.dto.CustomAlarmDto;

import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by Ethan.Yang on 2018/3/14.
 */
public class StatisticsResultRuleRowData {

    private SimpleStringProperty statisticName;
    private SimpleStringProperty lowerLimit;
    private SimpleStringProperty upperLimit;
    /**
     * constructor
     * @param customAlarmDto dto
     */
    public StatisticsResultRuleRowData(CustomAlarmDto customAlarmDto){
        if (customAlarmDto == null) {
            return;
        }
        this.statisticName = new SimpleStringProperty(customAlarmDto.getStatisticName());
        this.lowerLimit = new SimpleStringProperty(DAPStringUtils.toStringFromDouble(customAlarmDto.getLowerLimit()));
        this.upperLimit = new SimpleStringProperty(DAPStringUtils.toStringFromDouble(customAlarmDto.getUpperLimit()));
        upperLimit.addListener((ov, b1, b2) -> {
            if (!DAPStringUtils.isNumeric(b2)) {
                return;
            }
            customAlarmDto.setUpperLimit(Double.valueOf(b2));
        });
        lowerLimit.addListener((ov, b1, b2) -> {
            if (!DAPStringUtils.isNumeric(b2)) {
                return;
            }
            customAlarmDto.setLowerLimit(Double.valueOf(b2));
        });
    }

    public String getStatisticName() {
        return statisticName.get();
    }

    public SimpleStringProperty statisticNameProperty() {
        return statisticName;
    }

    public void setStatisticName(String statisticName) {
        this.statisticName.set(statisticName);
    }

    public String getLowerLimit() {
        return lowerLimit.get();
    }

    public SimpleStringProperty lowerLimitProperty() {
        return lowerLimit;
    }

    public void setLowerLimit(String lowerLimit) {
        this.lowerLimit.set(lowerLimit);
    }

    public String getUpperLimit() {
        return upperLimit.get();
    }

    public SimpleStringProperty upperLimitProperty() {
        return upperLimit;
    }

    public void setUpperLimit(String upperLimit) {
        this.upperLimit.set(upperLimit);
    }
}
