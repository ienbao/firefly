/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.grr.utils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Garen.Pang on 2018/3/15.
 */
public class FileUtils {

    public FileUtils() {
    }

    public static boolean deleteFile(String sPath) {
        try {
            File file = new File(sPath);
            return file.isFile() && file.exists() ? file.delete() : false;
        } catch (Exception var2) {
            var2.printStackTrace();
            return false;
        }
    }

    public static boolean deleteFile(String[] path) {
        boolean flag = false;
        String[] var2 = path;
        int var3 = path.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            String key = var2[var4];
            flag = deleteFile(key);
        }

        return flag;
    }

    public static boolean createFile(String sPath) {
        File file = new File(sPath);
        return !file.exists() && !file.isDirectory() ? file.mkdir() : false;
    }

    public static void createParentPath(String filePath) {
        File file = new File(filePath);
        if (!file.exists() && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

    }

    public static void createSpecificFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException var3) {
                var3.printStackTrace();
            }
        }

    }

    public static void createDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }

    }

    public static void deleteDir(String dir) {
        File dirFile = new File(dir);
        File[] childFiles = dirFile.listFiles();
        if (childFiles != null) {
            File[] var3 = childFiles;
            int var4 = childFiles.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                File file = var3[var5];
                if (file.isDirectory()) {
                    deleteDir(file.getPath());
                    file.delete();
                } else {
                    file.delete();
                }
            }
        }

        dirFile.delete();
    }

    public static String getAbsolutePath(String filePath) {
        String absolutePath = "";

        try {
            File file = new File(filePath);
            absolutePath = file.getCanonicalPath();
        } catch (IOException var3) {
            var3.printStackTrace();
        }

        return absolutePath;
    }
}
