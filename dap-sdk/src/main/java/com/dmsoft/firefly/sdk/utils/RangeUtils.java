package com.dmsoft.firefly.sdk.utils;

import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.dmsoft.firefly.sdk.utils.enums.TestItemType;

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
            if (valueD >= ul) {
                return false;
            }
        }
        if (DAPStringUtils.isNumeric(lowerLimit)) {
            Double ll = Double.valueOf(lowerLimit);
            return valueD > ll;
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
            if (valueD >= ul) {
                return true;
            }
        }
        if (DAPStringUtils.isNumeric(lowerLimit)) {
            Double ll = Double.valueOf(lowerLimit);
            return valueD <= ll;
        }
        return false;
    }

    /**
     * method to judge value is pass or not
     *
     * @param value   value
     * @param typeDto type dto
     * @return is or not
     */
    public static boolean isPass(String value, TestItemWithTypeDto typeDto) {
        if (value != null && typeDto != null && DAPStringUtils.isNumeric(value) && TestItemType.VARIABLE.equals(typeDto.getTestItemType())) {
            Double valueD = Double.valueOf(value);
            if (DAPStringUtils.isNumeric(typeDto.getUsl())) {
                Double ul = Double.valueOf(typeDto.getUsl());
                if (valueD > ul) {
                    return false;
                }
            }
            if (DAPStringUtils.isNumeric(typeDto.getLsl())) {
                Double ll = Double.valueOf(typeDto.getLsl());
                return valueD >= ll;
            }
        }
        return true;
    }
}
