package com.dmsoft.firefly.plugin.spc.charts.data;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;
import javafx.scene.paint.Color;

/**
 * Created by cherry on 2018/2/10.
 */
public class BoxExtraData extends AbstractValueObject {

    /**
     * X coordinate
     */
    private Number xPos;

    /**
     * The mean
     */
    private Number mean;   //cl

    /**
     * The median.
     */
    private Number median;

    /**
     * The first quarter.
     */
    private Number q1;

    /**
     * The third quarter.
     */
    private Number q3;

    /**
     * The minimum regular value.
     */
    private Number minRegularValue;

    /**
     * The maximum regular value.
     */
    private Number maxRegularValue;

    private Color color;

    public BoxExtraData(Number xPos,
                        Number mean,
                        Number q3,
                        Number q1,
                        Number maxRegularValue,
                        Number minRegularValue,
                        Number median) {
        this.xPos = xPos;
        this.mean = mean;
        this.median = median;
        this.q1 = q1;
        this.q3 = q3;
        this.minRegularValue = minRegularValue;
        this.maxRegularValue = maxRegularValue;
    }

    /**
     * Creates a new box-and-whisker item.
     * @param xPos
     * @param median
     * @param q1
     * @param q3
     * @param minRegularValue
     * @param maxRegularValue
     */
    public BoxExtraData(Number xPos,
                        Number mean,
                        Number q3,
                        Number q1,
                        Number maxRegularValue,
                        Number minRegularValue,
                        Number median,
                        Color color) {
        this.xPos = xPos;
        this.mean = mean;
        this.median = median;
        this.q1 = q1;
        this.q3 = q3;
        this.color = color;
        this.minRegularValue = minRegularValue;
        this.maxRegularValue = maxRegularValue;
    }

    public Number getxPos() {
        return xPos;
    }

    public void setxPos(Number xPos) {
        this.xPos = xPos;
    }

    public Number getMean() {
        return mean;
    }

    public void setMean(Number mean) {
        this.mean = mean;
    }

    public Number getMedian() {
        return median;
    }

    public void setMedian(Number median) {
        this.median = median;
    }

    public Number getQ1() {
        return q1;
    }

    public void setQ1(Number q1) {
        this.q1 = q1;
    }

    public Number getQ3() {
        return q3;
    }

    public void setQ3(Number q3) {
        this.q3 = q3;
    }

    public Number getMinRegularValue() {
        return minRegularValue;
    }

    public void setMinRegularValue(Number minRegularValue) {
        this.minRegularValue = minRegularValue;
    }

    public Number getMaxRegularValue() {
        return maxRegularValue;
    }

    public void setMaxRegularValue(Number maxRegularValue) {
        this.maxRegularValue = maxRegularValue;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
