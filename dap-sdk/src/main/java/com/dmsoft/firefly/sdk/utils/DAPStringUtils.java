/*
 *
 *  * Copyright (c) 2016. For Intelligent Group.
 *
 */

package com.dmsoft.firefly.sdk.utils;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Peter on 15/12/2.
 * update by cherry on 16/09/18
 * Changed by Tony.yu on 16/09/26.  pattern = Pattern.compile("-?[0-9]+(\\.?)[0-9]*");
 * Changed by QiangChen on 17/05/18 Add judgment for Scientific notation.("^([+-]?[1-9].[0-9]+[Ee][+-]?[0-9]+)$")
 */
public class DAPStringUtils extends StringUtils {

    /**
     * Validate the source is numeric.
     *
     * @param str source string
     * @return true if it's numeric.
     */
    public static boolean isNumeric(String str) {//检测变量是否为数字或数字字符串
        if (isSpecialBlank(str)) {//判断是否为空白
            return false;
        }
        Pattern pattern = Pattern.compile("-?[0-9]+(\\.?)[0-9]*");//正则表达式
        return checkPattern(str, pattern);
    }

    static boolean checkPattern(String str, Pattern pattern) {
        if (pattern.matcher(str).matches()) {
            return true;
        } else {
            String scientificNotationRegx = "^([+-]?[1-9].[0-9]+[Ee][+-]?[0-9]+)$";
            pattern = Pattern.compile(scientificNotationRegx);
            return pattern.matcher(str).matches();
        }
    }

    /**
     * Validate the source is blank.
     * "N/A", "-","nil","UnKown Line", "_" will be seem the same as null or empty.
     *
     * @param data source string
     * @return true if it's blank.
     */
    public static boolean isSpecialBlank(String data) {
        return StringUtils.isBlank(data) || data.equalsIgnoreCase("NA") || data.equalsIgnoreCase("N/A")
                || data.equalsIgnoreCase("-") || data.equalsIgnoreCase("nil") || data.equalsIgnoreCase("UnKnown Line") || data.equalsIgnoreCase("_");
    }

    /**
     * Validate the source is blank.
     * "N/A", "-","nil","NaN", "_" will be seem the same as null or empty.
     *
     * @param d source string
     * @return true if it's blank.
     */
    public static boolean isBlankWithSpecialNumber(String d) {
        return StringUtils.isBlank(d) || (d.equalsIgnoreCase("N/A") || d.equalsIgnoreCase("-") || d.equalsIgnoreCase("NaN") || d.equalsIgnoreCase("nil") || d.equalsIgnoreCase("_"));
    }

    /**
     * Format double to string.
     *
     * @param value source string
     * @param digit decimal space.
     * @return result
     */
    public static String formatDouble(Double value, int digit) {
        return String.format("%." + digit + "f", value);
    }

    /**
     * Format BigDecimal to string.
     *
     * @param value source string
     * @return result
     */
    public static String formatBigDecimal(String value) {
        BigDecimal bd = null;
        if (StringUtils.isNotBlank(value)) {
            try {
                bd = new BigDecimal(value);
                value = bd.toPlainString();
            } catch (Exception e) {
                Pattern pattern = Pattern.compile("^([0-9]{1,3}(,[0-9]{3})*(\\.[0-9]+)?|\\.[0-9]+)$");
                if (StringUtils.isNotBlank(value) && pattern.matcher(value).matches()) {
                    DecimalFormat df = new DecimalFormat();
                    try {
                        value = df.parse(value).toString();
                        bd = new BigDecimal(value);
                        value = bd.toPlainString();
                    } catch (ParseException ignored) {
                    }
                }
            }
        }
        return value;
    }

    /**
     * Validate the source is blank.
     * "Infinity", "-Infinity", "NaN" will be seem the same as null or empty.
     *
     * @param d source string
     * @return true if it's Infinity or NaN.
     */
    public static boolean isCheckInfinityAndNaN(String d) {
        return StringUtils.isBlank(d) || d.equalsIgnoreCase("Infinity") || d.equalsIgnoreCase("-Infinity") || d.equalsIgnoreCase("NaN");
    }

    /**
     * @param str string to filter
     * @return String after filer
     */
    public static String filterSpeChars(String str) {
        char[] chars = str.toCharArray();
        int len = chars.length;
        for (int i = 0; i < len; i++) {
            if (chars[i] == '\\' || chars[i] == '@' || chars[i] == '#' || chars[i] == '$' || chars[i] == '%' || chars[i] == '^' || chars[i] == '&'
                    || chars[i] == '*' || chars[i] == '(' || chars[i] == ')' || chars[i] == '-' || chars[i] == '+' || chars[i] == '='
                    || chars[i] == '{' || chars[i] == '}' || chars[i] == '[' || chars[i] == ']' || chars[i] == '|' || chars[i] == '/'
                    || chars[i] == ';' || chars[i] == '"' || chars[i] == ' ' || chars[i] == '<' || chars[i] == '>' || chars[i] == '?' || chars[i] == ','
                    || chars[i] == '.' || chars[i] == '!' || chars[i] == '~' || chars[i] == '`' || chars[i] == ':') {
                chars[i] = '_';
            }
        }
        return new String(chars);
    }

    /**
     * filter special charts for mongo db
     *
     * @param str string
     * @return replaced string
     */
    public static String filterSpeChars4Mongo(String str) {
        if (isBlank(str)) {
            return null;
        }
        String s = str;
        if (s.length() > 60) {
            s = s.substring(60);
        }
        if (s.startsWith("System.")) {
            s = s.substring(7);
        }
        char[] chars = s.toCharArray();
        int len = chars.length;
        for (int i = 0; i < len; i++) {
            if (chars[i] == '.' || chars[i] == '$') {
                chars[i] = '_';
            }
        }
        return new String(chars);
    }

    /**
     * filter special charts for mongo db
     *
     * @param str string
     * @return replaced string
     */
    public static boolean isSpeChars4Mongo(String str) {
        if (isBlank(str)) {
            return true;
        }
        if (str.length() > 60) {
            return true;
        }
        char[] chars = str.toCharArray();
        int len = chars.length;
        for (int i = 0; i < len; i++) {
            if (chars[i] == '.' || chars[i] == '$') {
                return true;
            }
        }
        return false;
    }

    /**
     * method to check is infinity or nan
     *
     * @param d double
     * @return true :  infinity or nan, false : normal double
     */
    public static boolean isInfinityAndNaN(double d) {
        return Double.isNaN(d) || (d == Double.NEGATIVE_INFINITY) || (d == Double.POSITIVE_INFINITY) || (d >= Math.pow(10, 16)) || (d <= -Math.pow(10, 16));
    }

    /**
     * method to check is infinity or nan
     *
     * @param d double
     * @return true : inifity or nan, false : normal double
     */
    public static boolean isInfinityAndNaN(Double d) {
        return d == null || Double.isNaN(d) || (d == Double.NEGATIVE_INFINITY) || (d == Double.POSITIVE_INFINITY) || (d >= Math.pow(10, 16)) || (d <= -Math.pow(10, 16));
    }

    /**
     * method to check is integer or not
     *
     * @param input input string
     * @return true : integer, false : not
     */
    public static boolean isInteger(String input) {
        Matcher mer = Pattern.compile("^[0-9]+$").matcher(input);
        return mer.find();
    }

    /**
     * method to equal or not
     *
     * @param str1 string 1
     * @param str2 string 2
     * @return is equal or not
     */
    public static boolean compare(String str1, String str2) {
        return str1 == null && str2 == null || str1 == null || str2 == null || str1.equals(str2);
    }

    /**
     * method to equal or not
     *
     * @param str1 string 1
     * @param str2 string 2
     * @return is equal or not
     */
    public static boolean compareTrim(String str1, String str2) {
        return str1 == null && str2 == null || (str1 != null && str2 != null && str1.trim().equals(str2.trim()));
    }

    /**
     * to string from double
     *
     * @param value double
     * @return string
     */
    public static String toStringFromDouble(Double value) {
        if (value == null) {
            return "";
        }
        return String.valueOf(value);
    }

    /**
     * mongodb ket can not contains this char
     *
     * @param value value
     * @return string
     */
    public static String mongodbItemFormat(String value) {
        if (value.contains(".")) {
            value = value.replace(".", "_");
        }
        if (value.contains("/")) {
            value = value.replace("/", "_");
        }
        if (value.contains("\"")) {
            value = value.replace("\"", "_");
        }
        if (value.contains("$")) {
            value = value.replace("$", "_");
        }
        if (value.contains("*")) {
            value = value.replace("*", "_");
        }
        if (value.contains("<")) {
            value = value.replace("<", "_");
        }
        if (value.contains(">")) {
            value = value.replace(">", "_");
        }
        if (value.contains(":")) {
            value = value.replace(":", "_");
        }
        if (value.contains("|")) {
            value = value.replace("|", "_");
        }
        if (value.contains("?")) {
            value = value.replace("?", "_");
        }
        return value;
    }

    /**
     * judge s1 is equal to s2 or not
     *
     * @param v1 string 1
     * @param v2 stirng 2
     * @return is equal or not
     */
    public static boolean isEqualsString(String v1, String v2) {
        if (DAPStringUtils.isBlank(v1) && DAPStringUtils.isBlank(v2)) {
            return true;
        } else if (DAPStringUtils.isBlank(v1)) {
            return false;
        } else {
            if (DAPStringUtils.isNumeric(v1) && DAPStringUtils.isNumeric(v2)) {
                return Double.valueOf(v1).equals(Double.valueOf(v2));
            } else {
                return v1.equals(v2);
            }
        }
    }

    /**
     * sort list or not
     *
     * @param list  list
     * @param isDES is des or acs
     */
    public static void sortListString(List<String> list, boolean isDES) {
        list.sort((o1, o2) -> {
            if (isDES) {
                return o2.compareTo(o1);
            } else {
                return o1.compareTo(o2);
            }
        });
    }

}
