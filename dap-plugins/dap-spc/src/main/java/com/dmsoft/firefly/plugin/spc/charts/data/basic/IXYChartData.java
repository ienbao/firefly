package com.dmsoft.firefly.plugin.spc.charts.data.basic;

/**
 * Created by cherry on 2018/2/27.
 */
public interface IXYChartData<X, Y> {

    int getLen();

    X getXValueByIndex(int index);
    Y getYValueByIndex(int index);

    Object getExtraValueByIndex(int index);

    String getColor();

    String getSeriesName();
}
