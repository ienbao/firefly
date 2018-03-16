/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.grr.service.impl.export.enums;

/**
 * Created by Administrator on 2017/7/31.
 */
public enum RuleLevelType {
    EXCELLENT("Excellent"),
    ADEQUATE("Adequate"),
    MARGINAL("Marginal"),
    BAD("Bad");

    private String value;

    RuleLevelType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
