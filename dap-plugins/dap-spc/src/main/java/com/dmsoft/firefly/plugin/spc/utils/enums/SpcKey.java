/*
 * Copyright (c) 2016. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.spc.utils.enums;


/**
 * Created by Peter on 2015/12/9.
 */
public enum SpcKey {

    DECIMAL_DIGIT("decimalDigit"),
    GROUP_NUMBER("customGroupNumber"),

    CA("CA"),
    CP("CP"),
    CPK("CPK"),
    CPL("CPL"),
    CPU("CPU"),

    PP("PP"),
    PPK("PPK"),
    PPL("PPL"),
    PPU("PPU"),

    R1("R1"),
    R2("R2"),
    R3("R3"),
    R4("R4"),
    R5("R5"),
    R6("R6"),
    R7("R7"),
    R8("R8"),
    R9("R9"),

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


    EXCELLENT("Excellent"),
    GOOD("Good"),
    ACCEPTABLE("Acceptable"),
    RECTIFICATION("Rectification"),
    BAD("Bad"),

    PASS("Pass"),
    FAIL("Fail"),

    NORMAL("Normal");
    private String code;

    SpcKey(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    /**
     * Get RuleLevelType enum value by given code.
     *
     * @param code code.
     * @return RuleLevelType
     */
    public SpcKey getByCode(String code) {
        switch (code) {
            case "decimalDigit":
                return SpcKey.DECIMAL_DIGIT;
            case "customGroupNumber":
                return SpcKey.GROUP_NUMBER;
            case "ca":
                return SpcKey.CA;
            case "cp":
                return SpcKey.CP;
            case "cpk":
                return SpcKey.CPK;
            case "cpl":
                return SpcKey.CPL;
            case "cpu":
                return SpcKey.CPU;
            case "pp":
                return SpcKey.PP;
            case "ppk":
                return SpcKey.PPK;
            case "ppl":
                return SpcKey.PPL;
            case "ppu":
                return SpcKey.PPU;
            case "R1":
                return R1;
            case "R2":
                return R2;
            case "R3":
                return R3;
            case "R4":
                return R4;
            case "R5":
                return R5;
            case "R6":
                return R6;
            case "R7":
                return R7;
            case "R8":
                return R8;
            case "R9":
                return R9;
            default:
                return null;
        }
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


