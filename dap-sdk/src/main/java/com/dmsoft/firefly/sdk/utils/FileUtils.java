/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Utils class to help build file path
 *
 * @author Can Guan
 */
public class FileUtils {

    /**
     * The Unix separator character.
     */
    private static final char UNIX_SEPARATOR = '/';

    /**
     * The Windows separator character.
     */
    private static final char WINDOWS_SEPARATOR = '\\';

    /**
     * method to build file path by append root dir, file separator and args.
     * e.g.
     * in mac os:
     * rootDir: "/etc"
     * args: "help", "readme.md"
     * return: "/etc/help/readme.md"
     *
     * @param rootDir root directory
     * @param args    arguments
     * @return file path
     */
    public static String buildFilePath(String rootDir, String... args) {
        StringBuilder sb = new StringBuilder(separatorsToUnix(rootDir));
        if (sb.toString().endsWith("/")) {
            sb.deleteCharAt(sb.length());
        }
        for (String s : args) {
            sb.append('/');
            sb.append(s);
        }
        return sb.toString();
    }

    /**
     * converts separators to the Unix separator
     *
     * @param path file path
     * @return converted path
     */
    public static String separatorsToUnix(String path) {
        if (path == null || path.indexOf(WINDOWS_SEPARATOR) == -1) {
            return path;
        }
        return path.replace(WINDOWS_SEPARATOR, UNIX_SEPARATOR);
    }

    /**
     * delete folder
     *
     * @param folderPath String
     * @return boolean
     */
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath);
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * delete all file
     *
     * @param path String
     * @return boolean
     */
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + File.separator + tempList[i]);
                delFolder(path + File.separator + tempList[i]);
                flag = true;
            }
        }
        return flag;
    }

    /**
     * copy folder
     *
     * @param oldPath String
     * @param newPath String
     * @return boolean
     */
    public static void copyFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs();
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + File.separator +
                            (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {
                    copyFolder(oldPath + File.separator + file[i], newPath + File.separator + file[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * change file authority
     *
     * @param filePath String
     */
    public static void changeFileAuthority(String filePath) {
        File dirFile = new File(filePath);
        dirFile.setReadable(true, false);
        dirFile.setExecutable(true, false);
        dirFile.setWritable(true, false);
    }
}
