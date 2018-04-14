package com.dmsoft.firefly.plugin.tm.csvresolver.utils;

/**
 * util class for double id
 *
 * @author Can Guan
 */
public class DoubleIdUtils {
    private static final String SEPARATOR = "_!@#_";
    private static final int SEPARATOR_LEN = SEPARATOR.length();

    /**
     * method to combine ids
     *
     * @param id0 id0
     * @param id1 id1
     * @return combined id
     */
    public static String combineIds(String id0, String id1) {
        return id0 + SEPARATOR + id1;
    }

    /**
     * method to combine ids
     *
     * @param id0 id0
     * @param id1 id1
     * @return combined id
     */
    public static String combineIds(String id0, int id1) {
        return id0 + SEPARATOR + id1;
    }

    /**
     * method to get id0
     *
     * @param combineId combined id
     * @return id0
     */
    public static String getId0(String combineId) {
        if (combineId.length() < SEPARATOR_LEN) {
            return "";
        }
        return combineId.substring(0, combineId.indexOf(SEPARATOR));
    }

    /**
     * method to get id1
     *
     * @param combineId combined id
     * @return id1
     */
    public static String getId1(String combineId) {
        if (combineId.length() < SEPARATOR_LEN) {
            return "";
        }
        return combineId.substring(combineId.indexOf(SEPARATOR) + SEPARATOR_LEN);
    }
}
