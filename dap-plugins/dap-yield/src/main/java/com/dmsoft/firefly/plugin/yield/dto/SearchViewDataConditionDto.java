package com.dmsoft.firefly.plugin.yield.dto;

import com.dmsoft.firefly.plugin.yield.utils.YieldType;
/**
 * Created by Tod Dylan on 2018/10/16.
 */
public class SearchViewDataConditionDto {
    private String itemName;
    private YieldType yieldType;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public YieldType getYieldType() {
        return yieldType;
    }

    public void setYieldType(YieldType yieldType) {
        this.yieldType = yieldType;
    }
}
