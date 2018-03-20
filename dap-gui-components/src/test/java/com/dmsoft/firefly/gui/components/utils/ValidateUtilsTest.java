package com.dmsoft.firefly.gui.components.utils;

public class ValidateUtilsTest {
    public static void main(String[] args) {
        String d = "1.3";
        System.out.println(ValidateUtils.validatePattern(d, ValidateUtils.POSITIVE_DOUBLE_PATTERN));
    }
}
