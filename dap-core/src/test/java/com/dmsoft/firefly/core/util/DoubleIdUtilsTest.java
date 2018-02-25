package com.dmsoft.firefly.core.util;

import com.dmsoft.firefly.core.utils.DoubleIdUtils;
import org.junit.Test;

public class DoubleIdUtilsTest {
    @Test
    public void combinedTest() {
        System.out.println(DoubleIdUtils.combineIds("AA", "CC"));
    }

    @Test
    public void getId0Test() {
        System.out.println(DoubleIdUtils.getId0("AA_!@#_CC"));
    }

    @Test
    public void getId1Test() {
        System.out.println(DoubleIdUtils.getId1("AA_!@#_CC"));
    }
}
