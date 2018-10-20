package com.dmsoft.firefly.plugin.yield.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

import java.util.List;

public class YieldViewDataResultDto extends AbstractValueObject {
    private String ItemName;
    private List<YieldViewDataDto> yieldViewDataDto;

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public List<YieldViewDataDto> getYieldViewDataDto() {
        return yieldViewDataDto;
    }

    public void setYieldViewDataDto(List<YieldViewDataDto> yieldViewDataDto) {
        this.yieldViewDataDto = yieldViewDataDto;
    }
}
