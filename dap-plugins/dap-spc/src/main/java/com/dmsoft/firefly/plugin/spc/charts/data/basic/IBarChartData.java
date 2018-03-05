package com.dmsoft.firefly.plugin.spc.charts.data.basic;

import javafx.scene.paint.Color;

/**
 * Created by cherry on 2018/2/27.
 */
public interface IBarChartData<X, Y> {

    Y getValueByIndex(int index);

    X getStartValueByIndex(int index);

    X getBarWidthByIndex(int index);

    Object getEndValueByIndex(int index);

    int getLen();

    Color getColor();

    String getSeriesName();
}
