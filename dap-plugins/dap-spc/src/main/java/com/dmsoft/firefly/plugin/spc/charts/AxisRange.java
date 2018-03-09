package com.dmsoft.firefly.plugin.spc.charts;

/**
 * Created by cherry on 2018/3/2.
 */
public interface AxisRange <X extends Number, Y extends Number>{

    X getXLowerBound();

    X getXUpperBound();

    Y getYLowerBound();

    Y getYUpperBound();
}
