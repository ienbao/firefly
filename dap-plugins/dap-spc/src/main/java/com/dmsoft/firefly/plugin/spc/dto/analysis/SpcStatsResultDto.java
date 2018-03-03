package com.dmsoft.firefly.plugin.spc.dto.analysis;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

/**
 * dto class for spc analysis stats result dto
 *
 * @author Can Guan
 */
public class SpcStatsResultDto extends AbstractValueObject {
    private Double samples;
    private Double avg;
    private Double max;
    private Double min;
    private Double stDev;
    private Double lsl;
    private Double usl;
    private Double center;
    private Double range;
    private Double lcl;
    private Double ucl;
    private Double kurtosis;
    private Double skewness;
    private Double cpk;
    private Double ca;
    private Double cp;
    private Double cpl;
    private Double cpu;
    private Double withinPPM;
    private Double overallPPM;
    private Double pp;
    private Double ppk;
    private Double ppl;
    private Double ppu;

    public Double getSamples() {
        return samples;
    }

    public void setSamples(Double samples) {
        this.samples = samples;
    }

    public Double getAvg() {
        return avg;
    }

    public void setAvg(Double avg) {
        this.avg = avg;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getStDev() {
        return stDev;
    }

    public void setStDev(Double stDev) {
        this.stDev = stDev;
    }

    public Double getLsl() {
        return lsl;
    }

    public void setLsl(Double lsl) {
        this.lsl = lsl;
    }

    public Double getUsl() {
        return usl;
    }

    public void setUsl(Double usl) {
        this.usl = usl;
    }

    public Double getCenter() {
        return center;
    }

    public void setCenter(Double center) {
        this.center = center;
    }

    public Double getRange() {
        return range;
    }

    public void setRange(Double range) {
        this.range = range;
    }

    public Double getLcl() {
        return lcl;
    }

    public void setLcl(Double lcl) {
        this.lcl = lcl;
    }

    public Double getUcl() {
        return ucl;
    }

    public void setUcl(Double ucl) {
        this.ucl = ucl;
    }

    public Double getKurtosis() {
        return kurtosis;
    }

    public void setKurtosis(Double kurtosis) {
        this.kurtosis = kurtosis;
    }

    public Double getSkewness() {
        return skewness;
    }

    public void setSkewness(Double skewness) {
        this.skewness = skewness;
    }

    public Double getCpk() {
        return cpk;
    }

    public void setCpk(Double cpk) {
        this.cpk = cpk;
    }

    public Double getCa() {
        return ca;
    }

    public void setCa(Double ca) {
        this.ca = ca;
    }

    public Double getCp() {
        return cp;
    }

    public void setCp(Double cp) {
        this.cp = cp;
    }

    public Double getCpl() {
        return cpl;
    }

    public void setCpl(Double cpl) {
        this.cpl = cpl;
    }

    public Double getCpu() {
        return cpu;
    }

    public void setCpu(Double cpu) {
        this.cpu = cpu;
    }

    public Double getWithinPPM() {
        return withinPPM;
    }

    public void setWithinPPM(Double withinPPM) {
        this.withinPPM = withinPPM;
    }

    public Double getOverallPPM() {
        return overallPPM;
    }

    public void setOverallPPM(Double overallPPM) {
        this.overallPPM = overallPPM;
    }

    public Double getPp() {
        return pp;
    }

    public void setPp(Double pp) {
        this.pp = pp;
    }

    public Double getPpk() {
        return ppk;
    }

    public void setPpk(Double ppk) {
        this.ppk = ppk;
    }

    public Double getPpl() {
        return ppl;
    }

    public void setPpl(Double ppl) {
        this.ppl = ppl;
    }

    public Double getPpu() {
        return ppu;
    }

    public void setPpu(Double ppu) {
        this.ppu = ppu;
    }
}
