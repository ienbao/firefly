package com.dmsoft.firefly.plugin.grr.dto.analysis;


import com.dmsoft.firefly.plugin.utils.enums.GrrResultName;

/**
 * Created by GuangLi on 2017/7/21.
 */
public class GrrAnovaDto {
    private GrrResultName name;
    private Double df;
    private Double ss;
    private Double ms;
    private Double f;
    private Double probF;

    public GrrResultName getName() {
        return name;
    }

    public void setName(GrrResultName name) {
        this.name = name;
    }

    public Double getDf() {
        return df;
    }

    public void setDf(Double df) {
        this.df = df;
    }

    public Double getSs() {
        return ss;
    }

    public void setSs(Double ss) {
        this.ss = ss;
    }

    public Double getMs() {
        return ms;
    }

    public void setMs(Double ms) {
        this.ms = ms;
    }

    public Double getF() {
        return f;
    }

    public void setF(Double f) {
        this.f = f;
    }

    public Double getProbF() {
        return probF;
    }

    public void setProbF(Double probF) {
        this.probF = probF;
    }
}
