package com.dmsoft.firefly.plugin.grr.dto.analysis;


import com.dmsoft.firefly.plugin.utils.enums.GrrResultName;

/**
 * Created by GuangLi on 2017/7/21.
 */
public class GrrSourceDto {
    private GrrResultName name;
    private Double sigma;
    private Double studyVar;
    private Double variation;
    private Double totalVariation;
    private Double contribution;
    private Double totalTolerance;

    public GrrResultName getName() {
        return name;
    }

    public void setName(GrrResultName name) {
        this.name = name;
    }

    public Double getSigma() {
        return sigma;
    }

    public void setSigma(Double sigma) {
        this.sigma = sigma;
    }

    public Double getStudyVar() {
        return studyVar;
    }

    public void setStudyVar(Double studyVar) {
        this.studyVar = studyVar;
    }

    public Double getVariation() {
        return variation;
    }

    public void setVariation(Double variation) {
        this.variation = variation;
    }

    public Double getTotalVariation() {
        return totalVariation;
    }

    public void setTotalVariation(Double totalVariation) {
        this.totalVariation = totalVariation;
    }

    public Double getContribution() {
        return contribution;
    }

    public void setContribution(Double contribution) {
        this.contribution = contribution;
    }

    public Double getTotalTolerance() {
        return totalTolerance;
    }

    public void setTotalTolerance(Double totalTolerance) {
        this.totalTolerance = totalTolerance;
    }
}
