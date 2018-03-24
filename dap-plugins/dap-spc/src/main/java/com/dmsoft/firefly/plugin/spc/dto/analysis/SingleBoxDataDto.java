package com.dmsoft.firefly.plugin.spc.dto.analysis;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

/**
 * box chart dto for single box
 *
 * @author Can Guan
 */
public class SingleBoxDataDto extends AbstractValueObject {
    private Double x;
    private Double median;
    private Double q1;
    private Double q3;
    private Double[] abnormalPoints;
    private Double upperWhisker;
    private Double lowerWhisker;
    private Double cl;

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getMedian() {
        return median;
    }

    public void setMedian(Double median) {
        this.median = median;
    }

    public Double getQ1() {
        return q1;
    }

    public void setQ1(Double q1) {
        this.q1 = q1;
    }

    public Double getQ3() {
        return q3;
    }

    public void setQ3(Double q3) {
        this.q3 = q3;
    }

    public Double[] getAbnormalPoints() {
        return abnormalPoints;
    }

    public void setAbnormalPoints(Double[] abnormalPoints) {
        this.abnormalPoints = abnormalPoints;
    }

    public Double getUpperWhisker() {
        return upperWhisker;
    }

    public void setUpperWhisker(Double upperWhisker) {
        this.upperWhisker = upperWhisker;
    }

    public Double getLowerWhisker() {
        return lowerWhisker;
    }

    public void setLowerWhisker(Double lowerWhisker) {
        this.lowerWhisker = lowerWhisker;
    }

    public Double getCl() {
        return cl;
    }

    public void setCl(Double cl) {
        this.cl = cl;
    }
}
