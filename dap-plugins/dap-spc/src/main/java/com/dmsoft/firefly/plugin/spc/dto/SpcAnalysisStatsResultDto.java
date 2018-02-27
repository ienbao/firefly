package com.dmsoft.firefly.plugin.spc.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

/**
 * dto class for spc analysis stats result dto
 *
 * @author Can Guan
 */
public class SpcAnalysisStatsResultDto extends AbstractValueObject {
    private String samples;
    private String avg;
    private String max;
    private String min;
    private String stDev;
    private String lsl;
    private String usl;
    private String center;
    private String range;
    private String lcl;
    private String ucl;
    private String kurtosis;
    private String skewness;
    private String cpk;
    private String ca;
    private String cp;
    private String cpl;
    private String cpu;
    private String withinPPM;
    private String overallPPM;
    private String pp;
    private String ppk;
    private String ppl;
    private String ppu;

    public String getSamples() {
        return samples;
    }

    public void setSamples(String samples) {
        this.samples = samples;
    }

    public String getAvg() {
        return avg;
    }

    public void setAvg(String avg) {
        this.avg = avg;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getStDev() {
        return stDev;
    }

    public void setStDev(String stDev) {
        this.stDev = stDev;
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

    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getLcl() {
        return lcl;
    }

    public void setLcl(String lcl) {
        this.lcl = lcl;
    }

    public String getUcl() {
        return ucl;
    }

    public void setUcl(String ucl) {
        this.ucl = ucl;
    }

    public String getKurtosis() {
        return kurtosis;
    }

    public void setKurtosis(String kurtosis) {
        this.kurtosis = kurtosis;
    }

    public String getSkewness() {
        return skewness;
    }

    public void setSkewness(String skewness) {
        this.skewness = skewness;
    }

    public String getCpk() {
        return cpk;
    }

    public void setCpk(String cpk) {
        this.cpk = cpk;
    }

    public String getCa() {
        return ca;
    }

    public void setCa(String ca) {
        this.ca = ca;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getCpl() {
        return cpl;
    }

    public void setCpl(String cpl) {
        this.cpl = cpl;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getWithinPPM() {
        return withinPPM;
    }

    public void setWithinPPM(String withinPPM) {
        this.withinPPM = withinPPM;
    }

    public String getOverallPPM() {
        return overallPPM;
    }

    public void setOverallPPM(String overallPPM) {
        this.overallPPM = overallPPM;
    }

    public String getPp() {
        return pp;
    }

    public void setPp(String pp) {
        this.pp = pp;
    }

    public String getPpk() {
        return ppk;
    }

    public void setPpk(String ppk) {
        this.ppk = ppk;
    }

    public String getPpl() {
        return ppl;
    }

    public void setPpl(String ppl) {
        this.ppl = ppl;
    }

    public String getPpu() {
        return ppu;
    }

    public void setPpu(String ppu) {
        this.ppu = ppu;
    }
}
