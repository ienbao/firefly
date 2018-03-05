package com.dmsoft.firefly.plugin.spc.dto.analysis;

/**
 * run chart dto
 *
 * @author Can Guan¬
 */
public class RunCResultDto {
    private Double[] x;
    private Double[] y;
    private Double[] cls;
    private Double usl;
    private Double lsl;

    public Double[] getX() {
        return x;
    }

    public void setX(Double[] x) {
        this.x = x;
    }

    public Double[] getY() {
        return y;
    }

    public void setY(Double[] y) {
        this.y = y;
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
