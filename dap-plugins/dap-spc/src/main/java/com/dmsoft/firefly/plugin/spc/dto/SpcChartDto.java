/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;
import com.dmsoft.firefly.plugin.spc.dto.analysis.SpcChartResultDto;

import java.util.List;

/**
 * Created by Ethan.Yang on 2018/2/6.
 */
public class SpcChartDto extends AbstractValueObject {
    private String key;
    private String itemName;
    private String condition;
    private SpcChartResultDto resultDto;
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

    public SpcChartResultDto getResultDto() {
        return resultDto;
    }

    public void setResultDto(SpcChartResultDto resultDto) {
        this.resultDto = resultDto;
    }

    public List<String> getAnalyzedRowKeys() {
        return analyzedRowKeys;
    }

    public void setAnalyzedRowKeys(List<String> analyzedRowKeys) {
        this.analyzedRowKeys = analyzedRowKeys;
    }
}
