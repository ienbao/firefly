package com.dmsoft.firefly.plugin.spc.charts.utils;

import static java.lang.Double.NaN;

/**
 * Created by cherry on 2018/3/8.
 */
public class MathUtils {

    public static Double getMax(Double[] array) {
        if (array == null || array.length <= 0) {
            return null;
        }
        Double max = null;
        for (int x = 0; x < array.length; x++) {
            if (array[x] == null || array[x] == NaN) {
                continue;
            }
            if (max == null || max == NaN) {
                max = array[x];
                continue;
            }
            max = Math.max(array[x], max);
        }
        return max;
    }

    public static Double getMin(Double[] array) {
        if (array == null || array.length <= 0) {
            return null;
        }
        Double min = null;
        for (int x = 0; x < array.length; x++) {
            if (array[x] == null || array[x] == NaN) {
                continue;
            }
            if (min == null || min == NaN) {
                min = array[x];
                continue;
            }
            min = Math.min(array[x], min);
        }
        return min;
    }

    public static Double getMax(Double[]... array) {
        if (array == null || array.length <= 0) {
            return null;
        }
        Double max = null;
        for (int i = 0; i < array.length; i++) {
            for (int x = 0; x < array[i].length; x++) {
                if (array[i][x] == null || array[i][x] == NaN) {
                    continue;
                }
                if (max == null || max == NaN) {
                    max = array[i][x];
                    continue;
                }
                max = Math.max(array[i][x], max);
            }
        }
        return max;
    }

    public static Double getMin(Double[]... array) {
        if (array == null || array.length <= 0) {
            return null;
        }
        Double min = null;
        for (int i = 0; i < array.length; i++) {
            for (int x = 0; x < array[i].length; x++) {
                if (array[i][x] == null || array[i][x] == NaN) {
                    continue;
                }
                if (min == null || min == NaN) {
                    min = array[i][x];
                    continue;
                }
                min = Math.min(array[i][x], min);
            }
        }
        return min;
    }
}
