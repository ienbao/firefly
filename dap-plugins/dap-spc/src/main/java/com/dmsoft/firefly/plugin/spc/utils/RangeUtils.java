package com.dmsoft.firefly.plugin.spc.utils;

import com.dmsoft.firefly.sdk.utils.DAPStringUtils;

/**
 * util class for range judgement
 *
 * @author Can Guan
 */
public class RangeUtils {
    /**
     * method to judge value within range or not
     *
     * @param value      value
     * @param upperLimit upper limit
     * @param lowerLimit lower limit
     * @return is or not
     */
    public static boolean isWithinRange(String value, String upperLimit, String lowerLimit) {
        if (!DAPStringUtils.isNumeric(value)) {
            return false;
        }
        Double valueD = Double.valueOf(value);
        if (DAPStringUtils.isNumeric(upperLimit)) {
            Double ul = Double.valueOf(upperLimit);
            if (valueD > ul) {
                return false;
            }
        }
        if (DAPStringUtils.isNumeric(lowerLimit)) {
            Double ll = Double.valueOf(lowerLimit);
            return valueD >= ll;
        }
        return true;
    }

    /**
     * method to judge value without range or not
     *
     * @param value      value
     * @param upperLimit upper limit
     * @param lowerLimit lower limit
     * @return is or not
     */
    public static boolean isWithoutRange(String value, String upperLimit, String lowerLimit) {
        if (!DAPStringUtils.isNumeric(value)) {
            return false;
        }
        Double valueD = Double.valueOf(value);
        if (DAPStringUtils.isNumeric(upperLimit)) {
            Double ul = Double.valueOf(upperLimit);
            if (valueD < ul) {
                return false;
            }
        }
        if (DAPStringUtils.isNumeric(lowerLimit)) {
            Double ll = Double.valueOf(lowerLimit);
            return valueD < ll;
        }
        return true;
    }
}
