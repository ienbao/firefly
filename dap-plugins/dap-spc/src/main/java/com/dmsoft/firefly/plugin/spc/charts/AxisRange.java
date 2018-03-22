package com.dmsoft.firefly.plugin.spc.charts;

/**
 * Created by cherry on 2018/3/2.
 */
public interface AxisRange<X extends Number, Y extends Number> {

    /**
     * Get x lower value
     *
     * @return x lower value
     */
    X getXLowerBound();

    /**
     * Get x upper value
     *
     * @return x upper value
     */
    X getXUpperBound();

    /**
     * Get y lower value
     *
     * @return y lower value
     */
    Y getYLowerBound();

    /**
     * Get y upper value
     *
     * @return y upper value
     */
    Y getYUpperBound();
}
