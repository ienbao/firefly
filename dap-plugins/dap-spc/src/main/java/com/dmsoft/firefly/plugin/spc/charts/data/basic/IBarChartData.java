package com.dmsoft.firefly.plugin.spc.charts.data.basic;

/**
 * Created by cherry on 2018/2/27.
 */
public interface IBarChartData<X, Y> {

//    X getStartValue(Data data);

    Y getValueByIndex(int index);

    X getStartValueByIndex(int index);

    X getBarWidthByIndex(int index);

    Object getEndValueByIndex(int index);

//    X getBarWidth(Data data);

    int getLen();

//    void addBarChartData(X startValue, X width, Y value, Data data);

    String getColor();

    String getSeriesName();
}
