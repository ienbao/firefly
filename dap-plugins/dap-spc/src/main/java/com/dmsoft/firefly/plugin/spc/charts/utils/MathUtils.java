package com.dmsoft.firefly.plugin.spc.charts.utils;

import static java.lang.Double.NaN;

/**
 * Created by cherry on 2018/3/8.
 */
public class MathUtils {

    public static Double getMax(Double[] array) {
        if (array == null) {
            return NaN;
        }
        Double max = NaN;
        for (int x = 0; x < array.length; x++) {
            if (array[x] == null) {
                continue;
            }
            if (max == NaN) {
                max = array[x];
            } else {
                max = Math.max(array[x], max);
            }
        }
        return NaN;
    }

    public static Double getMin(Double[] array) {
        if (array == null) {
            return NaN;
        }
        Double min = NaN;
        for (int x = 0; x < array.length; x++) {
            if (array[x] == null) {
                continue;
            }
            if (min == NaN) {
                min = array[x];
            } else {
                min = Math.min(array[x], min);
            }
        }
        return min;
    }

    public static Double getMax(Double[]... array) {
        if (array == null) {
            return NaN;
        }
        Double max = NaN;
        for (int i = 0; i < array.length; i++) {
            for (int x = 0; x < array[i].length; x++) {
                if (array[i][x] == null) {
                    continue;
                }
                if (max == NaN) {
                    max = array[i][x];
                } else {
                    max = Math.max(array[i][x], max);
                }
            }
        }
        return max;
    }

    public static Double getMin(Double[]... array) {
        if (array == null) {
            return NaN;
        }
        Double min = NaN;
        for (int i = 0; i < array.length; i++) {
            for (int x = 0; x < array[i].length; x++) {
                if (array[i][x] == null) {
                    continue;
                }
                if (min == NaN) {
                    min = array[i][x];
                } else {
                    min = Math.min(array[i][x], min);
                }
            }
        }
        return min;
    }
}
