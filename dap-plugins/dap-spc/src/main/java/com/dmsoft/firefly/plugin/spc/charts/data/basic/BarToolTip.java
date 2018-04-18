package com.dmsoft.firefly.plugin.spc.charts.data.basic;

/**
 * Created by cherry on 2018/3/20.
 */

/**
 * Bar tooltip
 *
 * @param <X> x data class
 * @param <Y> y data class
 */
public class BarToolTip<X, Y> {

    private String seriesName;
    private X startValue;
    private X endValue;
    private Y value;
    private boolean lastData;

    /**
     * Constructor for BarToolTip
     */
    public BarToolTip() {
    }

    /**
     * Constructor for BarToolTip
     *
     * @param seriesName series name
     * @param startValue start value
     * @param endValue   end value
     * @param value      bar value
     */
    public BarToolTip(String seriesName, X startValue, X endValue, Y value, boolean lastData) {
        this.seriesName = seriesName;
        this.startValue = startValue;
        this.endValue = endValue;
        this.value = value;
        this.lastData = lastData;
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

    public boolean isLastData() {
        return lastData;
    }

    public void setLastData(boolean lastData) {
        this.lastData = lastData;
    }
}
