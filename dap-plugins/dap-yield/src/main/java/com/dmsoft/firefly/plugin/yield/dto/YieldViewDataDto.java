package com.dmsoft.firefly.plugin.yield.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;
/**
 * Created by Tod Dylan on 2018/10/16.
 */
public class YieldViewDataDto extends AbstractValueObject {
    private String productName;
    private Double result;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String itemName) {
        this.productName = productName;
    }

    public Double getResult() {
        return result;
    }

    public void setResult(Double result) {
        this.result = result;
    }
}
