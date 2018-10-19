package com.dmsoft.firefly.plugin.yield.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

import java.util.List;

public class YieldChartDto extends AbstractValueObject {
    private String key;             //key值
    private String itemName;        //item名称
    private String condition;       //状况
    private YieldChartResultDto resultDto;

    public YieldChartResultDto getResultDto() {
        return resultDto;
    }

    public void setResultDto(YieldChartResultDto resultDto) {
        this.resultDto = resultDto;
    }

    private List<String> analyzedRowKeys;

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


    public List<String> getAnalyzedRowKeys() {
        return analyzedRowKeys;
    }

    public void setAnalyzedRowKeys(List<String> analyzedRowKeys) {
        this.analyzedRowKeys = analyzedRowKeys;
    }
}
