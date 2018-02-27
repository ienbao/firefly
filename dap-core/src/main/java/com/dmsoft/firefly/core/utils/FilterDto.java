package com.dmsoft.firefly.core.utils;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

/**
 * Created by Can.Guan on 2017/2/24.
 */
public class FilterDto extends AbstractValueObject {
    private String itemName;
    private String operator;
    private String value;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
