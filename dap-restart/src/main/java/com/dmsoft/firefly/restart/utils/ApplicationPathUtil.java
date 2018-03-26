/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.restart.utils;

/**
 * Created by Peter on 2016/7/15.
 */
public class ApplicationPathUtil {
    public static final String OS_NAME;
    private static final String OS_WIN = "win";

    static {
        OS_NAME = System.getProperty("os.name");
    }

    public static String getCanonicalPath() {
        return System.getProperty("user.dir") + "/";
    }

    /**
     * Return the file path by name.
     *
     * @param name file name.
     * @return file path
     */
    public static String getFilePath(String name) {
        String path = ApplicationPathUtil.getCanonicalPath();
        path += name;
        return path;
    }


}
