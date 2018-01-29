/*
 * Copyright (c) 2017. For intelligent group.
 */

package com.dmsoft.firefly.sdk.dai.dto;

/**
 * Created by Eligi.Ran on 2017/6/6.
 */
public class AlarmDataDto {

    private String lowerLimit;
    private String upperLimit;

    public AlarmDataDto() {

    }

    public AlarmDataDto(String lowerLimit, String upperLimit) {
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
    }

    public String getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(String lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public String getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(String upperLimit) {
        this.upperLimit = upperLimit;
    }

    public boolean compareWith(AlarmDataDto other) {
        if (other == null) {
            return false;
        }
        return lowerLimit.equals(other.lowerLimit) && upperLimit.equals(other.upperLimit);
    }
}
