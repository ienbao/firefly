//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.dmsoft.firefly.plugin.spc.utils;

import com.dmsoft.firefly.sdk.utils.DAPStringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils extends DAPStringUtils {
    public static boolean isSpecialBlank(String data) {
        if (isBlank(data)) {
            return true;
        } else {
            return data.equalsIgnoreCase("N/A") || data.equalsIgnoreCase("-") || data.equalsIgnoreCase("nil") || data.equalsIgnoreCase("UnKown Line") || data.equalsIgnoreCase("_");
        }
    }

    public static boolean isBlankWithSpecialNumber(String d) {
        if (isBlank(d)) {
            return true;
        } else {
            return d.equalsIgnoreCase("N/A") || d.equalsIgnoreCase("-") || d.equalsIgnoreCase("NaN") || d.equalsIgnoreCase("nil") || d.equalsIgnoreCase("_");
        }
    }

    public static String formatDouble(Double value, int digit) {
        return String.format("%." + digit + "f", value);
    }

    public static String formatBigDecimal(String value) {
        BigDecimal bd = null;
        if (isNotBlank(value)) {
            try {
                bd = new BigDecimal(value);
                value = bd.toPlainString();
            } catch (Exception var7) {
                Pattern pattern = Pattern.compile("^([0-9]{1,3}(,[0-9]{3})*(\\.[0-9]+)?|\\.[0-9]+)$");
                if (isNotBlank(value) && pattern.matcher(value).matches()) {
                    DecimalFormat df = new DecimalFormat();

                    try {
                        value = df.parse(value).toString();
                        bd = new BigDecimal(value);
                        value = bd.toPlainString();
                    } catch (ParseException var6) {
                        ;
                    }
                }
            }
        }

        return value;
    }

    public static boolean isCheckInfinityAndNaN(String d) {
        if (isBlank(d)) {
            return true;
        } else {
            return d.equalsIgnoreCase("Infinity") || d.equalsIgnoreCase("-Infinity") || d.equalsIgnoreCase("NaN");
        }
    }

    public static String filterSpeChars(String str) {
        char[] chars = str.toCharArray();
        int len = chars.length;

        for (int i = 0; i < len; ++i) {
            if (chars[i] == '\\' || chars[i] == '@' || chars[i] == '#' || chars[i] == '$' || chars[i] == '%' || chars[i] == '^' || chars[i] == '&' || chars[i] == '*' || chars[i] == '(' || chars[i] == ')' || chars[i] == '-' || chars[i] == '+' || chars[i] == '=' || chars[i] == '{' || chars[i] == '}' || chars[i] == '[' || chars[i] == ']' || chars[i] == '|' || chars[i] == '/' || chars[i] == ';' || chars[i] == '"' || chars[i] == ' ' || chars[i] == '<' || chars[i] == '>' || chars[i] == '?' || chars[i] == ',' || chars[i] == '.' || chars[i] == '!' || chars[i] == '~' || chars[i] == '`' || chars[i] == ':') {
                chars[i] = '_';
            }
        }

        return new String(chars);
    }

    public static boolean isCheckInfinityAndNaN(double d) {
        return Double.isNaN(d) || d == -1.0D / 0.0 || d == 1.0D / 0.0 || d >= Math.pow(10.0D, 16.0D) || d <= -Math.pow(10.0D, 16.0D);
    }

    public static boolean isCheckInfinityAndNaN(Double d) {
        return d == null || Double.isNaN(d) || d == -1.0D / 0.0 || d == 1.0D / 0.0 || d >= Math.pow(10.0D, 16.0D) || d <= -Math.pow(10.0D, 16.0D);
    }

    public static boolean isInteger(String input) {
        Matcher mer = Pattern.compile("^[0-9]+$").matcher(input);
        return mer.find();
    }

    public static boolean compareTrim(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        } else {
            return str1.trim() != null && str2.trim() != null ? str1.equals(str2) : true;
        }
    }
}
