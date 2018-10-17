package com.dmsoft.firefly.plugin.spc.dto.analysis;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

/**
 * nd chart result dto
 *
 * @author Can Guan
 */
public class NDCResultDto extends AbstractValueObject {
    private Double[] histX;
    private Double[] histY;
    private Double[] curveX;
    private Double[] curveY;
    private Double[] cls;
    private Double usl;//upper spec limit(规格上线)
    private Double lsl;//lower spec limit(规格线下)

    public Double[] getHistX() {
        return histX;
    }

    public void setHistX(Double[] histX) {
        this.histX = histX;
    }

    public Double[] getHistY() {
        return histY;
    }

    public void setHistY(Double[] histY) {
        this.histY = histY;
    }

    public Double[] getCurveX() {
        return curveX;
    }

    public void setCurveX(Double[] curveX) {
        this.curveX = curveX;
    }

    public Double[] getCurveY() {
        return curveY;
    }

    public void setCurveY(Double[] curveY) {
        this.curveY = curveY;
    }

    public Double[] getCls() {
        return cls;
    }

    public void setCls(Double[] cls) {
        this.cls = cls;
    }

    public Double getUsl() {
        return usl;
    }

    public void setUsl(Double usl) {
        this.usl = usl;
    }

    public Double getLsl() {
        return lsl;
    }

    public void setLsl(Double lsl) {
        this.lsl = lsl;
    }
}
