package com.dmsoft.firefly.plugin.spc.charts.utils;

import com.dmsoft.firefly.sdk.utils.DAPStringUtils;

/**
 * Created by cherry on 2018/3/8.
 */
public class MathUtils {

    /**
     * Get max value by double array
     *
     * @param array double array
     * @return max value
     */
    public static Double getMax(Double[] array) {
        if (array == null || array.length <= 0) {
            return null;
        }
        Double max = null;
        for (int x = 0; x < array.length; x++) {
            if (DAPStringUtils.isInfinityAndNaN(array[x])) {
                continue;
            }
            if (DAPStringUtils.isInfinityAndNaN(max)) {
                max = array[x];
                continue;
            }
            max = Math.max(array[x], max);
        }
        return max;
    }

    /**
     * Get min value by double array
     *
     * @param array double array
     * @return min value
     */
    public static Double getMin(Double[] array) {
        if (array == null || array.length <= 0) {
            return null;
        }
        Double min = null;
        for (int x = 0; x < array.length; x++) {
            if (DAPStringUtils.isInfinityAndNaN(array[x])) {
                continue;
            }
            if (DAPStringUtils.isInfinityAndNaN(min)) {
                min = array[x];
                continue;
            }
            min = Math.min(array[x], min);
        }
        return min;
    }

    /**
     * Get max value by more double array
     *
     * @param array more double array
     * @return max value
     */
    public static Double getMax(Double[]... array) {
        if (array == null || array.length <= 0) {
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

    /**
     * Get min value by more double array
     *
     * @param array more double array
     * @return min value
     */
    public static Double getMin(Double[]... array) {
        if (array == null || array.length <= 0) {
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
