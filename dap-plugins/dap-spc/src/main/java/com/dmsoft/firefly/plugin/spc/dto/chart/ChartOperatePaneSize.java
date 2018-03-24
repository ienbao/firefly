package com.dmsoft.firefly.plugin.spc.dto.chart;

/**
 * Created by cherry on 2018/3/23.
 */
public class ChartOperatePaneSize {

    private double width;
    private double height;

    public ChartOperatePaneSize() {
    }

    public ChartOperatePaneSize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
