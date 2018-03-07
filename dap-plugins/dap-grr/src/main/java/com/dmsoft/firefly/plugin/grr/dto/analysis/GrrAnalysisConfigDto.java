package com.dmsoft.firefly.plugin.grr.dto.analysis;

import com.dmsoft.firefly.plugin.grr.utils.enums.GrrAnalysisMethod;

/**
 * analysis config dto for grr
 *
 * @author Can Guan
 */
public class GrrAnalysisConfigDto {
    private Integer part;
    private Integer appraiser;
    private Integer trial;
    private GrrAnalysisMethod method;
    private Double coverage = 6.0;
    private Double significance = 0.05;

    public Integer getPart() {
        return part;
    }

    public void setPart(Integer part) {
        this.part = part;
    }

    public Integer getAppraiser() {
        return appraiser;
    }

    public void setAppraiser(Integer appraiser) {
        this.appraiser = appraiser;
    }

    public Integer getTrial() {
        return trial;
    }

    public void setTrial(Integer trial) {
        this.trial = trial;
    }

    public GrrAnalysisMethod getMethod() {
        return method;
    }

    public void setMethod(GrrAnalysisMethod method) {
        this.method = method;
    }

    public Double getCoverage() {
        return coverage;
    }

    public void setCoverage(Double coverage) {
        this.coverage = coverage;
    }

    public Double getSignificance() {
        return significance;
    }

    public void setSignificance(Double significance) {
        this.significance = significance;
    }
}
