package com.dmsoft.firefly.gui.components.utils;

import com.dmsoft.firefly.sdk.utils.DAPStringUtils;

import java.util.regex.Pattern;

/**
 * validate util class
 *
 * @author Can Guan
 */
public class ValidateUtils {
    public static final String DOUBLE_PATTERN = "^[-]?\\d*[.]?\\d*$";
    public static final String INTEGER_PATTERN = "^[-]?\\d*$";
    public static final String POSITIVE_INTEGER_PATTERN = "^\\d*$";
    public static final String POSITIVE_DOUBLE_PATTERN = "^\\d*[.]?\\d*$";

    /**
     * method to validate is not empty or not
     *
     * @param str string
     * @return true : is not empty, false : is empty
     */
    public static boolean validateNotEmpty(String str) {
        return !DAPStringUtils.isBlank(str);
    }

    /**
     * method to validate within range or not
     *
     * @param str      string
     * @param maxValue max value
     * @param minValue min value
     * @return true : within range, false : without range
     */
    public static boolean validateWithinRange(String str, Double maxValue, Double minValue) {
        if (maxValue == null && minValue == null) {
            return true;
        }
        if (DAPStringUtils.isBlank(str) || !DAPStringUtils.isNumeric(str)) {
            return false;
        }
        Double value = Double.parseDouble(str);
        return (maxValue == null || value <= maxValue) && (minValue == null || value >= minValue);
    }

    /**
     * method to validate pattern or not
     *
     * @param str        string
     * @param patternStr pattern string
     * @return true : match pattern, false : no match pattern
     */
    public static boolean validatePattern(String str, String patternStr) {
        return DAPStringUtils.isBlank(patternStr) || DAPStringUtils.isBlank(str) || Pattern.matches(patternStr, str);
    }

    /**
     * method to validate within length or not
     *
     * @param str       string
     * @param maxLength max length
     * @return true : within max length, false : longer than max length
     */
    public static boolean validateWithLength(String str, int maxLength) {
        return maxLength == -1 || str != null && str.length() < maxLength;
    }
}
