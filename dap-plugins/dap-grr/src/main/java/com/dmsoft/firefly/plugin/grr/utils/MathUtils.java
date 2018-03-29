package com.dmsoft.firefly.plugin.grr.utils;

import com.dmsoft.firefly.sdk.utils.DAPStringUtils;

/**
 * Created by cherry on 2018/3/8.
 */
public class MathUtils {

    public static Double getMax(double[] array) {
        if (array == null || array.length <= 0) {
            return null;
        }
        Double max = array[0];
        for (int x = 0; x < array.length; x++) {
            max = Math.max(array[x], max);
        }
        return max;
    }

    public static Double getMin(double[] array) {
        if (array == null || array.length <= 0) {
            return null;
        }
        Double min = array[0];
        for (int x = 0; x < array.length; x++) {
            min = Math.min(array[x], min);
        }
        return min;

    }

    public static Double getMax(double[][] array) {
        if (array == null || array.length <= 0) {
            return null;
        }
        Double max = array[0][0];
        for (int i = 0; i < array.length; i++) {
            for (int x = 0; x < array[i].length; x++) {
                if (max < array[i][x]) {
                    max = array[i][x];
                } else {
                    max = Math.max(array[i][x], max);
                }
            }
        }
        return max;
    }

    public static Double getMin(double[][] array) {
        if (array == null) {
            return null;
        }
        Double min = array[0][0];
        for (int i = 0; i < array.length; i++) {
            for (int x = 0; x < array[i].length; x++) {
                min = Math.min(array[i][x], min);
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
                if (DAPStringUtils.isInfinityAndNaN(array[i][x])) {
                    continue;
                }
                if (DAPStringUtils.isInfinityAndNaN(max)) {
                    max = array[i][x];
                    continue;
                }
                max = Math.max(array[i][x], max);
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
                if (DAPStringUtils.isInfinityAndNaN(array[i][x])) {
                    continue;
                }
                if (DAPStringUtils.isInfinityAndNaN(min)) {
                    min = array[i][x];
                    continue;
                }
                min = Math.min(array[i][x], min);
            }
        }
        return min;
    }
}
