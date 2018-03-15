/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.utils.enums;

/**
 * Created by Ethan.Yang on 2018/3/14.
 */
public enum SpcProCapAlarmKey {

    CA("CA"),
    CP("CP"),
    CPK("CPK"),
    CPL("CPL"),
    CPU("CPU"),

    PP("PP"),
    PPK("PPK"),
    PPL("PPL"),
    PPU("PPU");

    private String code;

    SpcProCapAlarmKey(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
