package com.dmsoft.firefly.plugin.spc.charts.utils;

/**
 * Created by cherry on 2018/3/8.
 */
public class MathUtils {

    public static double getMax(double[] array) {
        double max = array[0];
        for (int x = 1; x < array.length; x++) {
            max = Math.max(array[x], max);
        }
        return max;
    }

    public static double getMin(double[] array) {
        double min = array[0];
        for (int x = 1; x < array.length; x++) {
            min = Math.min(array[x], min);
        }
        return min;
    }
}
