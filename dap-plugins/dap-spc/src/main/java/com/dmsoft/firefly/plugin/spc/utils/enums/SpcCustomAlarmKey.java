/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.utils.enums;

/**
 * Created by Ethan.Yang on 2018/3/15.
 */
public enum SpcCustomAlarmKey {

    AVG("AVG"),
    MAX("Max"),
    MIN("Min"),
    ST_DEV("StDev"),
    CENTER("Center"),
    RANGE("Range"),
    LCL("LCL"),
    UCL("UCL"),
    KURTOSIS("Kurtosis"),
    SKEWNESS("Skewness");

    private String code;

    SpcCustomAlarmKey(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
