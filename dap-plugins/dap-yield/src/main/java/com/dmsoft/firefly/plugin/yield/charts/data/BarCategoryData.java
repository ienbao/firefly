package com.dmsoft.firefly.plugin.yield.charts.data;

/**
 * Created by Tommy on 2018/10/218
 */

/**
 * Bar category data
 *
 * @param <X> x data class
 * @param <Y> y data class
 */
public class BarCategoryData<X, Y> {

    private X startValue;//初始值
    private X endValue;//结束值
    private X barWidth;//bar的宽度
    private Y value;//y值

    /**
     * No parameter constructor for BarCategoryData
     */
    public BarCategoryData() {
    }

    /**
     * Constructor for BarCategoryData
     *
     * @param startValue start value
     * @param endValue   end value
     * @param barWidth   bar width
     * @param value      bar value
     */
    public BarCategoryData(X startValue, X endValue, X barWidth, Y value) {
        this.startValue = startValue;
        this.endValue = endValue;
        this.barWidth = barWidth;
        this.value = value;
    }

    /**
     * Constructor for BarCategoryData
     *
     * @param startValue start value
     * @param barWidth   bar width
     * @param value      bar value
     */
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
