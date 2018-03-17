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
    private int nValue;
    private int mValue;
    private int sValue;

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

    public int getnValue() {
        return nValue;
    }

    public void setnValue(int nValue) {
        this.nValue = nValue;
    }

    public int getmValue() {
        return mValue;
    }

    public void setmValue(int mValue) {
        this.mValue = mValue;
    }

    public int getsValue() {
        return sValue;
    }

    public void setsValue(int sValue) {
        this.sValue = sValue;
    }
}
