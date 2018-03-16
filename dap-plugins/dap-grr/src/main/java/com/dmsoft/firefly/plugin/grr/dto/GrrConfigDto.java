package com.dmsoft.firefly.plugin.grr.dto;

import com.dmsoft.firefly.plugin.grr.utils.enums.GrrAnalysisMethod;

import java.util.List;
import java.util.Map;

/**
 * Created by GuangLi on 2018/3/7.
 */
public class GrrConfigDto {
    private GrrAnalysisMethod analysisMethod = GrrAnalysisMethod.ANOVA;
    private Double coverage;
    private String signLevel;
    private String sortMethod;
    private List<Double> alarmSetting;
    private Map<String, Boolean> export;

    private int parts;
    private int appraisers;
    private int trials;

    public int getParts() {
        return parts;
    }

    public void setParts(int parts) {
        this.parts = parts;
    }

    public int getAppraisers() {
        return appraisers;
    }

    public void setAppraisers(int appraisers) {
        this.appraisers = appraisers;
    }

    public int getTrials() {
        return trials;
    }

    public void setTrials(int trials) {
        this.trials = trials;
    }

    public GrrAnalysisMethod getAnalysisMethod() {
        return analysisMethod;
    }

    public void setAnalysisMethod(GrrAnalysisMethod analysisMethod) {
        this.analysisMethod = analysisMethod;
    }

    public Double getCoverage() {
        return coverage;
    }

    public void setCoverage(Double coverage) {
        this.coverage = coverage;
    }

    public String getSignLevel() {
        return signLevel;
    }

    public void setSignLevel(String signLevel) {
        this.signLevel = signLevel;
    }

    public String getSortMethod() {
        return sortMethod;
    }

    public void setSortMethod(String sortMethod) {
        this.sortMethod = sortMethod;
    }

    public List<Double> getAlarmSetting() {
        return alarmSetting;
    }

    public void setAlarmSetting(List<Double> alarmSetting) {
        this.alarmSetting = alarmSetting;
    }

    public Map<String, Boolean> getExport() {
        return export;
    }

    public void setExport(Map<String, Boolean> export) {
        this.export = export;
    }
}
