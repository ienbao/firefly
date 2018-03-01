package com.dmsoft.firefly.plugin.spc.charts.data.basic;

/**
 * Created by cherry on 2018/2/27.
 */
public class BarData {

    private Number value;
    private double startBoundary;
    private double endBoundary;

    public Number getValue() {
        return value;
    }

    public void setValue(Number value) {
        this.value = value;
    }

    public double getStartBoundary() {
        return startBoundary;
    }

    public void setStartBoundary(double startBoundary) {
        this.startBoundary = startBoundary;
    }

    public double getEndBoundary() {
        return endBoundary;
    }

    public void setEndBoundary(double endBoundary) {
        this.endBoundary = endBoundary;
    }
}
