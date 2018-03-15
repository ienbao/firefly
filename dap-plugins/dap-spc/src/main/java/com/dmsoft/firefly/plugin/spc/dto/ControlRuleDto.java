/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.dto;

/**
 * Created by Ethan.Yang on 2018/3/14.
 */
public class ControlRuleDto {
    private boolean isUsed;
    private String ruleName;
    private Double nValue;
    private Double mValue;
    private Double sValue;

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public Double getnValue() {
        return nValue;
    }

    public void setnValue(Double nValue) {
        this.nValue = nValue;
    }

    public Double getmValue() {
        return mValue;
    }

    public void setmValue(Double mValue) {
        this.mValue = mValue;
    }

    public Double getsValue() {
        return sValue;
    }

    public void setsValue(Double sValue) {
        this.sValue = sValue;
    }
}
