/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.sdk.utils;

import java.util.regex.Pattern;

/**
 * Created by Ethan.Yang on 2018/3/5.
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    /**
     * Validate the source is numeric.
     *
     * @param str source string
     * @return true if it's numeric.
     */
    public static boolean isNumeric(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("-?[0-9]+(\\.?)[0-9]*");
        if (pattern.matcher(str).matches()) {
            return true;
        } else {
            String scientificNotationRegx = "^([+-]?[1-9].[0-9]+[Ee][+-]?[0-9]+)$";
            pattern = Pattern.compile(scientificNotationRegx);
            return pattern.matcher(str).matches();
        }
    }

    /**
     * validate is blank
     *
     * @param cs source string
     * @return
     */
    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs != null && (strLen = cs.length()) != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

}
