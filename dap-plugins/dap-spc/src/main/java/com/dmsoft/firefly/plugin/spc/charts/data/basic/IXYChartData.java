package com.dmsoft.firefly.plugin.spc.charts.data.basic;

import javafx.scene.paint.Color;

import java.util.function.Function;

/**
 * Created by cherry on 2018/2/27.
 */

/**
 * XY chart data
 *
 * @param <X> x data class
 * @param <Y> y data class
 */
public interface IXYChartData<X, Y> {

    /**
     * Get point count
     *
     * @return point count
     */
    int getLen();

    /**
     * Get x value by index
     *
     * @param index index
     * @return x value
     */
    X getXValueByIndex(int index);

    /**
     * Get y value by index
     *
     * @param index index
     * @return y value
     */
    Y getYValueByIndex(int index);

    /**
     * Get extra value by index
     *
     * @param index index
     * @return extra value
     */
    Object getExtraValueByIndex(int index);

    /**
     * Get current series color
     *
     * @return series color
     */
    default Color getColor() {
        return null;
    }

    /**
     * Get series name
     *
     * @return series name
     */
    default String getSeriesName() {
        return "";
    }

    /**
     * Get point rule function
     *
     * @return point rule function
     */
    default Function<PointRule, PointStyle> getPointFunction() {
        return null;
    }
}
