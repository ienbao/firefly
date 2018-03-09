package com.dmsoft.firefly.plugin.spc.charts.data.basic;

import javafx.scene.paint.Color;

/**
 * Created by cherry on 2018/2/27.
 */
public interface IXYChartData<X, Y> {

    int getLen();

    X getXValueByIndex(int index);

    Y getYValueByIndex(int index);

    Object getExtraValueByIndex(int index);

    default Color getColor() {
        return null;
    }

    default String getSeriesName() {
        return "";
    }
}
