/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.core.utils;

/**
 * Util class for version.
 */
public class VersionUtils {
    /**
     * method to compare version. if version1 equals version2, return 0.
     * if version1 < version2, return -1;
     * if version1 > version1, return 1;
     *
     * @param version1 version1
     * @param version2 version2
     * @return the value {@code 0} if {@code version1 == version2};
     * -1 if {@code version1 < version2}; 1 if {@code version1 > version2}
     */
    public static int compareVersion(String version1, String version2) {
        if (version1 == null && version2 == null) {
            return 0;
        } else if (version1 == null) {
            return -1;
        } else if (version2 == null) {
            return 1;
        }
        if (version1.equals(version2)) {
            return 0;
        }
        String[] v1 = version1.split("\\.");
        String[] v2 = version2.split("\\.");
        for (int i = 0; i < Math.max(v1.length, v2.length); i++) {
            if (i > v1.length) {
                return -1;
            }
            if (i > v2.length) {
                return 1;
            }
            Integer i1 = Integer.valueOf(v1[i]);
            Integer i2 = Integer.valueOf(v2[i]);
            if (i1 > i2) {
                return 1;
            } else if (i1 < i2) {
                return -1;
            }
        }
        return 1;
    }
}
