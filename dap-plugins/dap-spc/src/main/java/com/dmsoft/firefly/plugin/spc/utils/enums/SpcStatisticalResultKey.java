/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.utils.enums;

/**
 * Created by Ethan.Yang on 2018/3/21.
 */
public enum SpcStatisticalResultKey {
    SAMPLES("Samples"),
    AVG("AVG"),
    MAX("Max"),
    MIN("Min"),
    ST_DEV("StDev"),
    LSL("LSL"),
    USL("USL"),
    CENTER("Center"),
    RANGE("Range"),
    LCL("μ-3σ"),
    UCL("μ+3σ"),
    KURTOSIS("Kurtosis"),
    SKEWNESS("Skewness"),
    WITHIN_PPM("Within PPM"),
    OVERALL_PPM("OverAll PPM"),
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
    SpcStatisticalResultKey(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public static boolean isAbilityAlarmResultName(String resultName){
        switch (resultName) {
            case "CA":
                return true;
            case "CP":
                return true;
            case "CPK":
                return true;
            case "CPL":
                return true;
            case "CPU":
                return true;
            case "PP":
                return true;
            case "PPK":
                return true;
            case "PPL":
                return true;
            case "PPU":
                return true;
            default:
                return false;
        }
    }

    public static boolean isCustomAlarmResultName(String resultName){
        switch (resultName) {
            case "AVG":
                return true;
            case "Max":
                return true;
            case "Min":
                return true;
            case "StDev":
                return true;
            case "Center":
                return true;
            case "Range":
                return true;
            case "μ-3σ":
                return true;
            case "μ+3σ":
                return true;
            case "Kurtosis":
                return true;
            case "Skewness":
                return true;
            default:
                return false;
        }
    }
}
