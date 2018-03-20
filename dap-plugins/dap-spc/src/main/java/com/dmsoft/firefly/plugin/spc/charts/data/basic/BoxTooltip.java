package com.dmsoft.firefly.plugin.spc.charts.data.basic;

/**
 * Created by cherry on 2018/3/20.
 */
public class BoxTooltip {

    private Double median;

    /**
     * The first quarter.
     */
    private Double q1;

    /**
     * The third quarter.
     */
    private Double q3;

    /**
     * The minimum regular value.
     */
    private Double minRegularValue;

    /**
     * The maximum regular value.
     */
    private Double maxRegularValue;

    public Double getMedian() {
        return median;
    }

    public void setMedian(Double median) {
        this.median = median;
    }

    public Double getQ1() {
        return q1;
    }

    public void setQ1(Double q1) {
        this.q1 = q1;
    }

    public Double getQ3() {
        return q3;
    }

    public void setQ3(Double q3) {
        this.q3 = q3;
    }

    public Double getMinRegularValue() {
        return minRegularValue;
    }

    public void setMinRegularValue(Double minRegularValue) {
        this.minRegularValue = minRegularValue;
    }

    public Double getMaxRegularValue() {
        return maxRegularValue;
    }

    public void setMaxRegularValue(Double maxRegularValue) {
        this.maxRegularValue = maxRegularValue;
    }
}
