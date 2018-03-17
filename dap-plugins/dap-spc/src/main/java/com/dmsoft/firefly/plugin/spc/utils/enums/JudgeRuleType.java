/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.utils.enums;

/**
 * Created by Ethan.Yang on 2018/3/17.
 */
public enum JudgeRuleType {

    R1("R1"),
    R2("R2"),
    R3("R3"),
    R4("R4"),
    R5("R5"),
    R6("R6"),
    R7("R7"),
    R8("R8"),
    R9("R9");

    private String code;

    JudgeRuleType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /**
     * Get judgeRuleType by code name.
     *
     * @param codeName the name of code
     * @return judgeRuleType
     */
    public static JudgeRuleType getByCode(String codeName) {
        switch (codeName) {
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
}
