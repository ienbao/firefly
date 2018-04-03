package com.dmsoft.firefly.sdk.utils.enums;

/**
 * enum class for test item type
 *
 * @author Can Guan
 */
public enum TestItemType {
    VARIABLE("Variable"),
    ATTRIBUTE("Attribute");
    private String code;

    TestItemType(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
