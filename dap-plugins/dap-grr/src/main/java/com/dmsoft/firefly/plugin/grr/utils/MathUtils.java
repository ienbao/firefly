package com.dmsoft.firefly.plugin.grr.utils;

/**
 * Created by cherry on 2018/3/8.
 */
public class MathUtils {

    public static Double getMax(double[] array) {
        if (array == null) {
            return null;
        }
        Double max = null;
        for (int x = 0; x < array.length; x++) {
            if (max == null) {
                max = array[x];
            } else {
                max = Math.max(array[x], max);
            }
        }
        return max;
    }

    public static Double getMin(double[] array) {
        if (array == null) {
            return null;
        }
        Double min = null;
        for (int x = 1; x < array.length; x++) {
            if (min == null) {
                min = array[x];
            } else {
                min = Math.min(array[x], min);
            }
        }
        return min;
    }

    public static Double getMax(Double[]... array) {
        if (array == null) {
            return null;
        }
        Double max = null;
        for (int i = 0; i < array.length; i++) {
            for (int x = 0; x < array[i].length; x++) {
                if (array[i][x] == null) {
                    continue;
                }
                if (max == null) {
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
            return null;
        }
        Double min = null;
        for (int i = 0; i < array.length; i++) {
            for (int x = 0; x < array[i].length; x++) {
                if (array[i][x] == null) {
                    continue;
                }
                if (min == null) {
                    min = array[i][x];
                } else {
                    min = Math.min(array[i][x], min);
                }
            }
        }
        return min;
    }
}
