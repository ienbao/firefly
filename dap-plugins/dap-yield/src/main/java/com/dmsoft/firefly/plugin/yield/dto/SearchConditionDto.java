package com.dmsoft.firefly.plugin.yield.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;
import com.dmsoft.firefly.sdk.utils.enums.TestItemType;

/**
 * Created by Tod Dylan on 2018/10/16.
 */
public class SearchConditionDto extends AbstractValueObject {
    private String key;
    private String itemName;
    private String condition;
    private String lslOrFail;
    private String uslOrPass;
    private TestItemType testItemType;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getLslOrFail() {
        return lslOrFail;
    }

    public void setLslOrFail(String lslOrFail) {
        this.lslOrFail = lslOrFail;
    }

    public String getUslOrPass() {
        return uslOrPass;
    }

    public void setUslOrPass(String uslOrPass) {
        this.uslOrPass = uslOrPass;
    }

    public TestItemType getTestItemType() {
        return testItemType;
    }

    public void setTestItemType(TestItemType testItemType) {
        this.testItemType = testItemType;
    }
}
