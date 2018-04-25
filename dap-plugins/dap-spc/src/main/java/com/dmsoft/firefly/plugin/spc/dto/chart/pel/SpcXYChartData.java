/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.dto.chart.pel;

import com.dmsoft.firefly.plugin.spc.charts.data.basic.IXYChartData;
import com.dmsoft.firefly.sdk.utils.DAPDoubleUtils;
import javafx.scene.paint.Color;

/**
 * Created by Ethan.Yang on 2018/3/20.
 */
public class SpcXYChartData implements IXYChartData<Double, Double> {
    private Double[] x = null;
    private Double[] y = null;
    private Object[] ids = null;

    //    Series index
    private int index;

    //    Series color
    private Color color;

    //    Series name
    private String seriesName;

    /**
     * Constructor for SpcXYChartData
     * @param x x coordinate
     * @param y y coordinate
     */
    public SpcXYChartData(Double[] x, Double[] y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int getLen() {
        return (x == null || y == null) ? 0 : (x.length < y.length) ? x.length : y.length;
    }

    @Override
    public Double getXValueByIndex(int index) {
        return (index >= 0 && index < getLen()) ? (DAPDoubleUtils.isBlank(x[index]) ? null : x[index]) : null;
    }

    @Override
    public Double getYValueByIndex(int index) {
        return (index >= 0 && index < getLen()) ? (DAPDoubleUtils.isBlank(y[index]) ? null : y[index]) : null;
    }

    @Override
    public Object getExtraValueByIndex(int index) {
        return (index >= 0 && index < getLen()) && (ids != null) && (ids.length > index) ? ids[index] : null;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public String getSeriesName() {
        return seriesName;
    }

    public void setX(Double[] x) {
        this.x = x;
    }

    public void setY(Double[] y) {
        this.y = y;
    }

    public void setIds(Object[] ids) {
        this.ids = ids;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public int getIndex() {
        return index;
    }
}
