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
    private Integer nValue;
    private Integer mValue;
    private Integer sValue;

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

    public Integer getnValue() {
        return nValue;
    }

    public void setnValue(Integer nValue) {
        this.nValue = nValue;
    }

    public Integer getmValue() {
        return mValue;
    }

    public void setmValue(Integer mValue) {
        this.mValue = mValue;
    }

    public Integer getsValue() {
        return sValue;
    }

    public void setsValue(Integer sValue) {
        this.sValue = sValue;
    }
}
