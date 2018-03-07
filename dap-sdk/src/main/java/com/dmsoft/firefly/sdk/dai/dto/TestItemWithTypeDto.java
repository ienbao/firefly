package com.dmsoft.firefly.sdk.dai.dto;

import com.dmsoft.firefly.sdk.utils.enums.TestItemType;

/**
 * test item dto with test item type
 *
 * @author Can Guan
 */
public class TestItemWithTypeDto extends TestItemDto {
    private TestItemType testItemType = TestItemType.VARIABLE;

    public TestItemType getTestItemType() {
        return testItemType;
    }

    public void setTestItemType(TestItemType testItemType) {
        this.testItemType = testItemType;
    }
}
