/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.core.utils;

/**
 * class array utils for class[]
 *
 * @author Can Guan
 */
public class ClassUtils {
    /**
     * method to judge param type equal or not
     *
     * @param args    param args object
     * @param classes method param types
     * @return true : eq, false : not eq
     */
    public static boolean isParamTypeEqual(Object[] args, Class<?>[] classes) {
        if (args == null) {
            return classes == null || classes.length == 0;
        }

        if (classes == null) {
            return args.length == 0;
        }

        if (args.length != classes.length) {
            return false;
        }

        for (int i = 0; i < args.length; i++) {
            if (!args[i].getClass().equals(classes[i])) {
                return false;
            }
        }

        return true;
    }
}
