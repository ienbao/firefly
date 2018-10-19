package com.dmsoft.firefly.plugin.yield.dto;

import java.util.Map;

public class YieldOverviewResultAlarmDto {
    private String itemName;
    private Map<String, OverviewAlarmDto> overviewAlarmDtoMap;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Map<String, OverviewAlarmDto> getOverviewAlarmDtoMap() {
        return overviewAlarmDtoMap;
    }

    public void setOverviewAlarmDtoMap(Map<String, OverviewAlarmDto> overviewAlarmDtoMap) {
        this.overviewAlarmDtoMap = overviewAlarmDtoMap;
    }
}
