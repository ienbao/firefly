package com.dmsoft.firefly.plugin.spc.charts;

/**
 * Created by cherry on 2018/3/2.
 */
public interface AxisRange {

    Number getXLowerBound(Number[] value);

    Number getXUpperBound(Number[] value);

    Number getYLowerBound(Number[] value);

    Number getYUpperBound(Number[] value);
}
