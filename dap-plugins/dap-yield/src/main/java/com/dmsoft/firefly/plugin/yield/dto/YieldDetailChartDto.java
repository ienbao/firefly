package com.dmsoft.firefly.plugin.yield.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

import java.util.List;

public class YieldDetailChartDto extends AbstractValueObject {
    private  YieldChartResultDto yieldChartResultDto;

    public YieldDetailChartDto(YieldChartResultDto yieldChartResultDto) {
        this.yieldChartResultDto = yieldChartResultDto;
    }

    public YieldChartResultDto getYieldChartResultDto() {
        return yieldChartResultDto;
    }

    public void setYieldChartResultDto(YieldChartResultDto yieldChartResultDto) {
        this.yieldChartResultDto = yieldChartResultDto;
    }
}
