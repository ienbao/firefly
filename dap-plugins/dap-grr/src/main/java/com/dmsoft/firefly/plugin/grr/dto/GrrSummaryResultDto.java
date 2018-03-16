/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.grr.dto;

/**
 * Created by GuangLi on 2017/7/21.
 */
public class GrrSummaryResultDto {
    private String testItem;
    private String lsl;
    private String usl;
    private String tolerance;

    private String repeatabilityOnTolerance;
    private String reproducibilityOnTolerance;
    private String grrOnTolerance;
    private String levelOnTolerance;

    private String repeatabilityOnContribution;
    private String reproducibilityOnContribution;
    private String grrOnContribution;
    private String levelOnContribution;
    private Boolean hasAdd;

    public String getTestItem() {
        return testItem;
    }

    public void setTestItem(String testItem) {
        this.testItem = testItem;
    }

    public String getLsl() {
        return lsl;
    }

    public void setLsl(String lsl) {
        this.lsl = lsl;
    }

    public String getUsl() {
        return usl;
    }

    public void setUsl(String usl) {
        this.usl = usl;
    }

    public String getTolerance() {
        return tolerance;
    }

    public void setTolerance(String tolerance) {
        this.tolerance = tolerance;
    }

    public String getRepeatabilityOnTolerance() {
        return repeatabilityOnTolerance;
    }

    public void setRepeatabilityOnTolerance(String repeatabilityOnTolerance) {
        this.repeatabilityOnTolerance = repeatabilityOnTolerance;
    }

    public String getReproducibilityOnTolerance() {
        return reproducibilityOnTolerance;
    }

    public void setReproducibilityOnTolerance(String reproducibilityOnTolerance) {
        this.reproducibilityOnTolerance = reproducibilityOnTolerance;
    }

    public String getGrrOnTolerance() {
        return grrOnTolerance;
    }

    public void setGrrOnTolerance(String grrOnTolerance) {
        this.grrOnTolerance = grrOnTolerance;
    }

    public String getLevelOnTolerance() {
        return levelOnTolerance;
    }

    public void setLevelOnTolerance(String levelOnTolerance) {
        this.levelOnTolerance = levelOnTolerance;
    }

    public String getRepeatabilityOnContribution() {
        return repeatabilityOnContribution;
    }

    public void setRepeatabilityOnContribution(String repeatabilityOnContribution) {
        this.repeatabilityOnContribution = repeatabilityOnContribution;
    }

    public String getReproducibilityOnContribution() {
        return reproducibilityOnContribution;
    }

    public void setReproducibilityOnContribution(String reproducibilityOnContribution) {
        this.reproducibilityOnContribution = reproducibilityOnContribution;
    }

    public String getGrrOnContribution() {
        return grrOnContribution;
    }

    public void setGrrOnContribution(String grrOnContribution) {
        this.grrOnContribution = grrOnContribution;
    }

    public String getLevelOnContribution() {
        return levelOnContribution;
    }

    public void setLevelOnContribution(String levelOnContribution) {
        this.levelOnContribution = levelOnContribution;
    }

    public Boolean isHasAdd() {
        return hasAdd;
    }

    public void setHasAdd(Boolean hasAdd) {
        this.hasAdd = hasAdd;
    }

}
