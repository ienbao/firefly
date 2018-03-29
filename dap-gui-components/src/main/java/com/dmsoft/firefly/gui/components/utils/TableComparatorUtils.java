package com.dmsoft.firefly.gui.components.utils;

import com.dmsoft.firefly.sdk.utils.DAPStringUtils;

import java.util.Comparator;

/**
 * Created by cherry on 2018/3/29.
 */
public class TableComparatorUtils {

    public static Comparator<String> getContainsPercentColumnComparator() {
        return (o1, o2) -> {
            String o1Str = String.valueOf(o1);
            String o2Str = String.valueOf(o2);
            o1Str = o1Str.contains("%") ? o1Str.split("%")[0] : o1Str;
            o2Str = o2Str.contains("%") ? o2Str.split("%")[0] : o2Str;
            return getComparatorValue(o1Str, o2Str);
        };
    }

    private static int getComparatorValue(String o1Str, String o2Str) {
        boolean o1Flag = DAPStringUtils.isNumeric(o1Str);
        boolean o2Flag = DAPStringUtils.isNumeric(o2Str);
        if (o1Str != null && o2Str != null) {
            if (o1Flag && o2Flag) {
                Double delta = Double.valueOf(o2Str) - Double.valueOf(o1Str);
                if (delta == 0) {
                    return 0;
                } else if (delta > 0) {
                    return -1;
                } else {
                    return 1;
                }
            } else if (o1Flag) {
                return -1;
            } else if (o2Flag) {
                return 1;
            } else {
                return -o2Str.compareTo(o1Str);
            }
        } else {
            return 0;
        }
    }
}
