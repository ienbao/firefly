/*
 * Copyright (c) 2016. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.spc.utils.enums;

/**
 * Created by Peter on 2015/12/9.
 */
public enum SpcRuleLevelType {
    NORMAL("NORMAL"),
    EXCELLENT("EXCELLENT"),
    GOOD("GOOD"),
    ACCEPTABLE("ACCEPTABLE"),
    ABOVE_RECTIFICATION("ABOVE_RECTIFICATION"),
    RECTIFICATION("RECTIFICATION");

    private String code;

    SpcRuleLevelType(String code) {
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
    public SpcRuleLevelType getByCode(String code) {
        switch (code) {
            case "NORMAL":
                return SpcRuleLevelType.NORMAL;
            case "EXCELLENT":
                return SpcRuleLevelType.EXCELLENT;
            case "GOOD":
                return SpcRuleLevelType.GOOD;
            case "ACCEPTABLE":
                return SpcRuleLevelType.ACCEPTABLE;
            case "ABOVE_RECTIFICATION":
                return SpcRuleLevelType.ABOVE_RECTIFICATION;
            case "RECTIFICATION":
                return SpcRuleLevelType.RECTIFICATION;
            default:
                return SpcRuleLevelType.NORMAL;
        }
    }

}


