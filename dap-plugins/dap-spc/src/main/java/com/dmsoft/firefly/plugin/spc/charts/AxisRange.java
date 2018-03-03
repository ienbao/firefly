package com.dmsoft.firefly.plugin.spc.charts;

/**
 * Created by cherry on 2018/3/2.
 */
public interface AxisRange {

    Number getLowerBound(Number[] value);

    Number getUpperBound(Number[] value);
}
