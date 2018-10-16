package com.dmsoft.firefly.plugin.yield.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;
/**
 * Created by Tod Dylan on 2018/10/16.
 */
public class YieldViewDataDto extends AbstractValueObject {
    private String itemName;
    private Double result;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Double getResult() {
        return result;
    }

    public void setResult(Double result) {
        this.result = result;
    }
}
