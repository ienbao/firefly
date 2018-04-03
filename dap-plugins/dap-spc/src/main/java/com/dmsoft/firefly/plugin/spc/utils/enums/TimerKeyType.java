/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.utils.enums;

/**
 * Created by Ethan.Yang on 2018/4/3.
 */
public enum  TimerKeyType {
    FIVE_MIN("5min"),
    TEN_MIN("10min"),
    THIRTY_MIN("30min");

    private String code;
    TimerKeyType(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
