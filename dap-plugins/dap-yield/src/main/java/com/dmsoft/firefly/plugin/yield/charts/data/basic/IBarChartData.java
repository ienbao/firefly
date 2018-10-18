package com.dmsoft.firefly.plugin.yield.charts.data.basic;

import javafx.scene.paint.Color;

/**
 * Created by Tommy on 2018/10/18.
 */

/**
 * Bar chart data
 *
 * @param <X> x data class
 * @param <Y> y data class
 */
public interface IBarChartData<X, Y> {

//    y value

    /**
     * Get bar value by index
     *
     * @param index index
     * @return value
     */
    Y getValueByIndex(int index);

    //    bar start value

    /**
     * Get bar start value by index
     *
     * @param index index
     * @return start value
     */
    X getStartValueByIndex(int index);

    //    bar width

    /**
     * Get bar width by index
     *
     * @param index index
     * @return width
     */
    X getBarWidthByIndex(int index);

    //    bar end value

    /**
     * Get bar end value by index
     *
     * @param index index
     * @return end value
     */
    X getEndValueByIndex(int index);

    //    bar count

    /**
     * Get bar count
     *
     * @return bar count
     */
    int getLen();

    //    bar color

    /**
     * Get bar color
     *
     * @return bar color
     */
    default Color getColor() {
        return null;
    }

    //    bar description

    /**
     * Get series name
     *
     * @return series name
     */
    default String getSeriesName() {
        return "";
    }

    /**
     * Get tooltip content by index
     *
     * @param index index
     * @return tooltip content
     */
    default String getTooltipContent(int index) {
        return getSeriesName() + "\n" + "X[" + getStartValueByIndex(index) + ", "
                + getEndValueByIndex(index) + "]" + "\n" + "Y = " + getValueByIndex(index);
    }

}
