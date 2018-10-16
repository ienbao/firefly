package com.dmsoft.firefly.plugin.yield.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

/**
 * Created by Tod Dylan on 2018/10/16.
 */
public class YieldNTFChartDto  extends AbstractValueObject {
    private String itemName;
    private Double ntfPercent;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Double getNtfPercent() {
        return ntfPercent;
    }

    public void setNtfPercent(Double ntfPercent) {
        this.ntfPercent = ntfPercent;
    }
}
