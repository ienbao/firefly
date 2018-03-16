/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.grr.dto;

/**
 * Created by GuangLi on 2017/7/21.
 */
public class GrrAnovaDto {
    private String name;
    private String df;
    private String ss;
    private String ms;
    private String f;
    private String probF;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDf() {
        return df;
    }

    public void setDf(String df) {
        this.df = df;
    }

    public String getSs() {
        return ss;
    }

    public void setSs(String ss) {
        this.ss = ss;
    }

    public String getMs() {
        return ms;
    }

    public void setMs(String ms) {
        this.ms = ms;
    }

    public String getF() {
        return f;
    }

    public void setF(String f) {
        this.f = f;
    }

    public String getProbF() {
        return probF;
    }

    public void setProbF(String probF) {
        this.probF = probF;
    }
}
