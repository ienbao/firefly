package com.dmsoft.firefly.plugin.spc.dto.chart;

/**
 * Created by cherry on 2018/2/27.
 */
public class BarCategoryData<X, Y> {

    private X startValue;
    private X endValue;
    private X barWidth;
    private Y value;

    public BarCategoryData() {
    }

    public BarCategoryData(X startValue, X endValue, X barWidth, Y value) {
        this.startValue = startValue;
        this.endValue = endValue;
        this.barWidth = barWidth;
        this.value = value;
    }

    public BarCategoryData(X startValue, X barWidth, Y value) {
        this.startValue = startValue;
        this.barWidth = barWidth;
        this.value = value;
    }

    public X getStartValue() {
        return startValue;
    }

    public void setStartValue(X startValue) {
        this.startValue = startValue;
    }

    public X getBarWidth() {
        return barWidth;
    }

    public void setBarWidth(X barWidth) {
        this.barWidth = barWidth;
    }

    public Y getValue() {
        return value;
    }

    public void setValue(Y value) {
        this.value = value;
    }

    public X getEndValue() {
        return endValue;
    }

    public void setEndValue(X endValue) {
        this.endValue = endValue;
    }
}
