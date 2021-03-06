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
    private Double xPos;

    /**
     * The mean
     */
    private Double mean;   //cl

    /**
     * The median.
     */
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

    private Color color;

    /**
     * Constructor for BoxExtraData
     *
     * @param xPos            x position
     * @param mean            mean value
     * @param q3              q3 value
     * @param q1              q1 value
     * @param maxRegularValue max value
     * @param minRegularValue min value
     * @param median          median value
     */
    public BoxExtraData(Double xPos,
                        Double mean,
                        Double q3,
                        Double q1,
                        Double maxRegularValue,
                        Double minRegularValue,
                        Double median) {
        this.xPos = xPos;
        this.mean = mean;
        this.median = median;
        this.q1 = q1;
        this.q3 = q3;
        this.minRegularValue = minRegularValue;
        this.maxRegularValue = maxRegularValue;
    }

    /**
     * Constructor for BoxExtraData
     *
     * @param xPos            x position
     * @param mean            mean value
     * @param q3              q3 value
     * @param q1              q1 value
     * @param maxRegularValue max value
     * @param minRegularValue min value
     * @param median          median value
     * @param color           color
     */
    public BoxExtraData(Double xPos,
                        Double mean,
                        Double q3,
                        Double q1,
                        Double maxRegularValue,
                        Double minRegularValue,
                        Double median,
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

    /**
     * Get x position
     *
     * @return x position
     */
    public Double getxPos() {
        return xPos;
    }

    /**
     * Set x position
     *
     * @param xPos x position
     */
    public void setxPos(Double xPos) {
        this.xPos = xPos;
    }

    public Double getMean() {
        return mean;
    }

    public void setMean(Double mean) {
        this.mean = mean;
    }

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

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
