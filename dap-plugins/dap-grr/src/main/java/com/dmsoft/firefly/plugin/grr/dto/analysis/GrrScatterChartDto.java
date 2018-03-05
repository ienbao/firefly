package com.dmsoft.firefly.plugin.grr.dto.analysis;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

/**
 * grr scatter chart dto
 *
 * @author Can Guan
 */
public class GrrScatterChartDto extends AbstractValueObject {
    private Double[] x;
    private Double[] y;
    private Double[] clX;
    private Double[] clY;

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

    public Double[] getClX() {
        return clX;
    }

    public void setClX(Double[] clX) {
        this.clX = clX;
    }

    public Double[] getClY() {
        return clY;
    }

    public void setClY(Double[] clY) {
        this.clY = clY;
    }
}
