/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;
import com.dmsoft.firefly.plugin.spc.dto.analysis.SpcStatsResultDto;

/**
 * Created by Ethan.Yang on 2018/2/6.
 */
public class SpcStatsDto extends AbstractValueObject {
    private String key;
    private String itemName;
    private String condition;
    private SpcStatsResultDto statsResultDto;


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

    public SpcStatsResultDto getStatsResultDto() {
        return statsResultDto;
    }

    public void setStatsResultDto(SpcStatsResultDto statsResultDto) {
        this.statsResultDto = statsResultDto;
    }
}
