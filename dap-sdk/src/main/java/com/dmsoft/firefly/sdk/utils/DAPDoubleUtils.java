/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.sdk.utils;

/**
 * Created by Ethan.Yang on 2018/3/20.
 */
public class DAPDoubleUtils {

    /**
     * is blank
     *
     * @param data data
     * @return boolean
     */
    public static boolean isBlank(Double data) {
        return data == null ? true : (data == Double.NaN ? true : false);
    }

    /**
     * is special Number
     *
     * @param data data
     * @return boolean
     */
    public static boolean isSpecialNumber(Double data) {
        return isBlank(data) || Double.isInfinite(data);
    }
}
