package com.dmsoft.firefly.sdk.dai.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;
import com.dmsoft.firefly.sdk.utils.enums.TestItemType;

/**
 * Test item dto
 *
 * @author Can Guan
 */
public class TestItemDto extends AbstractValueObject {
    private String testItemName;
    private TestItemType itemType;
    private String usl;
    private String lsl;
    private String unit;


    public String getTestItemName() {
        return testItemName;
    }

    public void setTestItemName(String testItemName) {
        this.testItemName = testItemName;
    }

    public TestItemType getItemType() {
        return itemType;
    }

    public void setItemType(TestItemType itemType) {
        this.itemType = itemType;
    }

    public String getUsl() {
        return usl;
    }

    public void setUsl(String usl) {
        this.usl = usl;
    }

    public String getLsl() {
        return lsl;
    }

    public void setLsl(String lsl) {
        this.lsl = lsl;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
