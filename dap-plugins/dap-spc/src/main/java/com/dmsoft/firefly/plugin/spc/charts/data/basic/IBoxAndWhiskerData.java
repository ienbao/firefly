package com.dmsoft.firefly.plugin.spc.charts.data.basic;

import javafx.scene.paint.Color;

/**
 * Created by cherry on 2018/2/10.
 */
public interface IBoxAndWhiskerData {

    /**
     * Get x position by index
     *
     * @param index index
     * @return x position
     */
    Double getXPosByIndex(int index);

    /**
     * Get mean value by index
     *
     * @param index index
     * @return mean value
     */
    Double getMeanByIndex(int index);   //cl

    /**
     * Get median value by index
     *
     * @param index index
     * @return median value
     */
    Double getMedianByIndex(int index);

    /**
     * Get q1 value by index
     *
     * @param index index
     * @return q1 value
     */
    Double getQ1ByIndex(int index);

    /**
     * Get q3 value by index
     *
     * @param index index
     * @return q3 value
     */
    Double getQ3ByIndex(int index);

    /**
     * Get min value by index
     *
     * @param index index
     * @return min value
     */
    Double getMinRegularValueByIndex(int index);

    /**
     * Get max value by index
     *
     * @param index index
     * @return max value
     */
    Double getMaxRegularValueByIndex(int index);

    /**
     * Get series color
     *
     * @return color
     */
    default Color getColor() {
        return null;
    }

    /**
     * Get box count
     *
     * @return box count
     */
    int getLen();
}
