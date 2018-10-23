package com.dmsoft.firefly.plugin.yield.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

public class YieldChartResultDto extends AbstractValueObject {
    private Double FPY;
    private Double NTF;
    private Double NG;

    public Double getFPY() {
        return FPY;
    }

    public void setFPY(Double FPY) {
        this.FPY = FPY;
    }

    public Double getNTF() {
        return NTF;
    }

    public void setNTF(Double NTF) {
        this.NTF = NTF;
    }

    public Double getNG() {
        return NG;
    }

    public void setNG(Double NG) {
        this.NG = NG;
    }
}
