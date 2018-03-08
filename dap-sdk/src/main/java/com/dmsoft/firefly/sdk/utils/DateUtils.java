/*
 * Copyright (c) 2015. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.utils;

import org.apache.commons.lang3.time.FastDateFormat;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by julia on 16/8/9.
 */
public class DateUtils {

    /**
     * Format string to date.
     *
     * @param date   date
     * @param format format string
     * @return Date
     */
    public static Date string2Date(String date, String format) {
        final FastDateFormat df = FastDateFormat.getInstance(format);
        try {
            return df.parse(date);
        } catch (ParseException e) {
        }
        return new Date();
    }

    /**
     * method to translate date to string
     *
     * @param date   date
     * @param format format
     * @return string
     */
    public static String date2String(Date date, String format) {
        final FastDateFormat df = FastDateFormat.getInstance(format);
        return df.format(date);
    }

    /**
     * df.parse(date);
     * check if the date matches the format.
     *
     * @param date   date
     * @param format format string
     * @return boolean
     */
    public static boolean checkString(String date, String format) {
        try {
            final FastDateFormat df = FastDateFormat.getInstance(format);
            df.parse(date);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
