/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.dto;

import java.util.Map;

/**
 * Created by Ethan.Yang on 2018/3/12.
 */
public class SpcStatisticalResultAlarmDto {
    private String key;
    private String itemName;
    private String condition;
    private Map<String, StatisticalAlarmDto> statisticalAlarmDtoMap;

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

    public Map<String, StatisticalAlarmDto> getStatisticalAlarmDtoMap() {
        return statisticalAlarmDtoMap;
    }

    public void setStatisticalAlarmDtoMap(Map<String, StatisticalAlarmDto> statisticalAlarmDtoMap) {
        this.statisticalAlarmDtoMap = statisticalAlarmDtoMap;
    }
}
