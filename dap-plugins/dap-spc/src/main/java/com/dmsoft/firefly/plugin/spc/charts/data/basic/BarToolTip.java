package com.dmsoft.firefly.plugin.spc.charts.data.basic;

/**
 * Created by cherry on 2018/3/20.
 */
public class BarToolTip<X, Y> {

    private String seriesName;
    private X startValue;
    private X endValue;
    private Y value;

    public BarToolTip() {
    }

    public BarToolTip(String seriesName, X startValue, X endValue, Y value) {
        this.seriesName = seriesName;
        this.startValue = startValue;
        this.endValue = endValue;
        this.value = value;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public X getStartValue() {
        return startValue;
    }

    public void setStartValue(X startValue) {
        this.startValue = startValue;
    }

    public X getEndValue() {
        return endValue;
    }

    public void setEndValue(X endValue) {
        this.endValue = endValue;
    }

    public Y getValue() {
        return value;
    }

    public void setValue(Y value) {
        this.value = value;
    }
}
