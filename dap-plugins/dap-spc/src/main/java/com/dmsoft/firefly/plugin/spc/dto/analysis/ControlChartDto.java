package com.dmsoft.firefly.plugin.spc.dto.analysis;

/**
 * control chart dto for service
 *
 * @author Can Guan
 */
public class ControlChartDto {
    private Double[] x;
    private Double[] y;
    private Double[] ucl;
    private Double[] lcl;
    private Double[] cl;

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

    public Double[] getUcl() {
        return ucl;
    }

    public void setUcl(Double[] ucl) {
        this.ucl = ucl;
    }

    public Double[] getLcl() {
        return lcl;
    }

    public void setLcl(Double[] lcl) {
        this.lcl = lcl;
    }

    public Double[] getCl() {
        return cl;
    }

    public void setCl(Double[] cl) {
        this.cl = cl;
    }
}
