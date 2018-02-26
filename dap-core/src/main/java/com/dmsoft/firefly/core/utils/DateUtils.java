/*
 * Copyright (c) 2015. For Intelligent Group.
 */

package com.dmsoft.firefly.core.utils;

import org.apache.commons.lang3.time.FastDateFormat;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by julia on 16/8/9.
 */
public class DateUtils {

//    /**
//     * Format string to date.
//     *
//     * @param date   date
//     * @param format format string
//     * @return Date
//     */
//    public static Date string2Date(String date, String format) {
//        Date dateResult = null;
//
//        try {
//            if (StringUtils.isSpecialBlank(date)) {
//                dateResult = null;
//            } else {
//                if (date.split(":").length == 1) {
//                    dateResult = null;
//                } else {
//                    if (date.split(":").length == 2 && date.endsWith(":")) {
//                        date = date + "00";
//                    } else if (date.split(":").length == 2 && !date.endsWith(":")) {
//                        date = date + ":00";
//                    }
//                }
//
//                dateResult = DateTime.parse(date, DateTimeFormat.forPattern(format)).toDate();
//            }
//        } catch (Exception e) {
//            SimpleDateFormat sdf = new SimpleDateFormat(format);
//            sdf.setLenient(false);
//
//            SimpleDateFormat sdf1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);
//
//            try {
//                dateResult = DateTime.parse(sdf.format(sdf1.parse(date)), DateTimeFormat.forPattern(format)).toDate();
//            } catch (ParseException ex) {
//                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//                sdf2.setLenient(false);
//
//                try {
//                    dateResult = DateTime.parse(sdf.format(sdf2.parse(date)), DateTimeFormat.forPattern(format)).toDate();
//                } catch (ParseException exx) {
//                    SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");
//                    sdf3.setLenient(false);
//
//                    try {
//                        dateResult = DateTime.parse(sdf.format(sdf3.parse(date)), DateTimeFormat.forPattern(format)).toDate();
//                    } catch (ParseException exxx) {
//                        exxx.printStackTrace();
//                        return null;
//                    }
//                }
//            }
//        }
//
//        return dateResult;
//    }

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
//            DateTimeFormatter.ofPattern(format).parse(date);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
