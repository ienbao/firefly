package com.dmsoft.firefly.plugin.yield.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

import java.util.List;

public class YieldViewDataResultDto extends AbstractValueObject {
    private String ItemName;
    private String primary;
    private List<String> resultlist;


    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public List<String> getResultlist() {
        return resultlist;
    }

    public void setResultlist(List<String> resultlist) {
        this.resultlist = resultlist;
    }

}
