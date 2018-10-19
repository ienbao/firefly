/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.yield.utils;

/**
 * Created by Ethan.Yang on 2018/3/14.
 */
public enum YieldProCapAlarmKey {
    NTF("NTF"),
    FPY("FPY"),
    NG("NG");

    private String code;

    YieldProCapAlarmKey(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
