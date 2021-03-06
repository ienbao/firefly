/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.core.utils;

import java.net.URL;
import java.net.URLDecoder;

/**
 * Created by Peter on 2016/7/15.
 */
public class ApplicationPathUtil {
    public static final String OS_NAME;
    public static final String OS_WIN = "win";

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

    /**
     * Return the file path in target path with file name.
     *
     * @param searchPath target folder.
     * @param fileName   name.
     * @return file path.
     */
    public static String getPath(String searchPath, String fileName) {
        ResourceFinder rf = new ResourceFinder(searchPath);
        URL url = rf.findResource(fileName);
        if (url == null) {
            return null;
        }
        String path = url.getPath();

        if (OS_NAME.toLowerCase().startsWith(OS_WIN)) {
            path = path.substring(1, path.length());
        }
        try {
            return URLDecoder.decode(path, "UTF-8");
        } catch (Exception e) {
            return path;
        }
    }

    /**
     * method to get path from file name
     *
     * @param fileName name of file
     * @return file path
     */
    public static String getPath(String fileName) {
        ResourceFinder rf = new ResourceFinder();

        URL url = rf.findResource(fileName);
        if (url == null) {
            return null;
        }
        String path = url.getPath();

        if (OS_NAME.toLowerCase().startsWith(OS_WIN)) {
            path = path.substring(1, path.length());
        }
        try {
            return URLDecoder.decode(path, "UTF-8");
        } catch (Exception e) {
            return path;
        }
    }


    /**
     * 获取系统配置文件路径
     *
     * @return
     */
    public static String getApplicationPath(){
        String propertiesURL = ApplicationPathUtil.getPath("application.properties");
        if (propertiesURL == null) {
            propertiesURL = ApplicationPathUtil.getPath("dev", "application.properties");
        }

        return propertiesURL;
    }
}
