package com.dmsoft.firefly.plugin.spc.charts.data.basic;

import javafx.scene.paint.Color;

/**
 * Created by cherry on 2018/3/8.
 */

/**
 * Point data
 *
 * @param <X> x data class
 * @param <Y> y data class
 */
public interface IPoint<X, Y> {

    /**
     * Get x value by index
     *
     * @param index index
     * @return x value
     */
    X getXByIndex(int index);

    /**
     * Get y value by index
     *
     * @param index index
     * @return y value
     */
    Y getYByIndex(int index);

    /**
     * Get point count
     *
     * @return point count
     */
    int getLen();

    default Color getColor() {
        return Color.RED;
    }
}
