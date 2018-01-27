/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.utils;

/**
 * Utils class to help build file path
 *
 * @author Can Guan
 */
public class FilePathUtils {

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
}
