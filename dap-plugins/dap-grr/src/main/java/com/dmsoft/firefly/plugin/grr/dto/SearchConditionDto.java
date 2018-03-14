/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.grr.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

/**
 * Created by Ethan.Yang on 2018/2/6.
 */
public class SearchConditionDto extends AbstractValueObject {

    private String key;
    private String itemName;
    private String condition;
    private String cusLsl;
    private String cusUsl;

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

    public String getCusLsl() {
        return cusLsl;
    }

    public void setCusLsl(String cusLsl) {
        this.cusLsl = cusLsl;
    }

    public String getCusUsl() {
        return cusUsl;
    }

    public void setCusUsl(String cusUsl) {
        this.cusUsl = cusUsl;
    }
}
