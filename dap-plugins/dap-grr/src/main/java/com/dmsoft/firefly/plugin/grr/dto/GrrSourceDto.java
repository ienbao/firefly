/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.grr.dto;

/**
 * Created by GuangLi on 2017/7/21.
 */
public class GrrSourceDto {
    private String name;
    private String sigma;
    private String studyVar;
    private String variation;
    private String totalVariation;
    private String contribution;
    private String totalTolerance;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSigma() {
        return sigma;
    }

    public void setSigma(String sigma) {
        this.sigma = sigma;
    }

    public String getStudyVar() {
        return studyVar;
    }

    public void setStudyVar(String studyVar) {
        this.studyVar = studyVar;
    }

    public String getVariation() {
        return variation;
    }

    public void setVariation(String variation) {
        this.variation = variation;
    }

    public String getTotalVariation() {
        return totalVariation;
    }

    public void setTotalVariation(String totalVariation) {
        this.totalVariation = totalVariation;
    }

    public String getContribution() {
        return contribution;
    }

    public void setContribution(String contribution) {
        this.contribution = contribution;
    }

    public String getTotalTolerance() {
        return totalTolerance;
    }

    public void setTotalTolerance(String totalTolerance) {
        this.totalTolerance = totalTolerance;
    }

}
