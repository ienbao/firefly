package com.dmsoft.firefly.plugin.spc.charts.data.basic;

import javafx.scene.paint.Color;

/**
 * Created by cherry on 2018/2/27.
 */
public interface IBarChartData<X, Y> {

//    y value
    Y getValueByIndex(int index);

//    bar start value
    X getStartValueByIndex(int index);

//    bar width
    X getBarWidthByIndex(int index);

//    bar end value
    X getEndValueByIndex(int index);

//    bar count
    int getLen();

//    bar color
    default Color getColor() {
        return null;
    }

//    bar description
    default String getSeriesName() {
        return "";
    }

    default String getTooltipContent(int index) {

        return getSeriesName() + "\n" + "X[" + getStartValueByIndex(index) + ", " +
                getEndValueByIndex(index) + "]" + "\n" + "Y = " + getValueByIndex(index);
    }
}
