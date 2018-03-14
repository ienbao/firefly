/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by Garen.Pang on 2018/3/14.
 */
public class SystemPath {

    private static volatile String filePath;

    private SystemPath() {}

    public static String getFilePath() {
        if (filePath == null) {
            synchronized (SystemPath.class) {
                if (filePath == null) {
                    filePath = ApplicationPathUtil.getCanonicalPath();
                    System.out.println(filePath);
                    InputStream fis = null;
                    try {
                        fis = new FileInputStream(new File(filePath + "application.properties"));
                    } catch (Exception e) {
                        filePath = ApplicationPathUtil.getPath(File.separator, "");
                    }
                }
            }
        }
        System.out.println(filePath);
        return filePath;
    }
}
