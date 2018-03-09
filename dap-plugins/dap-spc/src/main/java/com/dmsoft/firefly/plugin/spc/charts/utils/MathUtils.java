package com.dmsoft.firefly.plugin.spc.charts.utils;

/**
 * Created by cherry on 2018/3/8.
 */
public class MathUtils {

    public static Double getMax(Double[] array) {
        Double max = array[0];
        for (int x = 1; x < array.length; x++) {
            max = Math.max(array[x], max);
        }
        return max;
    }

    public static Double getMin(Double[] array) {
        double min = array[0];
        for (int x = 1; x < array.length; x++) {
            min = Math.min(array[x], min);
        }
        return min;
    }

    public static Double getMax(Double[]... array) {
        if (array == null) {
            return null;
        }
        Double max = array[0][0];
        for (int i = 0; i < array.length; i++) {
            for (int x = 1; x < array[i].length; x++) {
                max = Math.max(array[i][x], max);
            }
        }
        return max;
    }

    public static Double getMin(Double[]... array) {
        if (array == null) {
            return null;
        }
        Double min = array[0][0];
        for (int i = 0; i < array.length; i++) {
            for (int x = 1; x < array[i].length; x++) {
                min = Math.min(array[i][x], min);
            }
        }
        return min;
    }
}
