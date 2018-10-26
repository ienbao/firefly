package com.dmsoft.firefly.plugin.yield.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

import java.util.List;

public class YieldResultDto extends AbstractValueObject {

    private List<YieldOverviewDto> yieldOverviewDtos;
    private List<YieldNTFChartDto> yieldNTFChartDtos;
    private YieldTotalProcessesDto totalProcessesDtos;

    public List<YieldOverviewDto> getYieldOverviewDtos() {
        return yieldOverviewDtos;
    }

    public void setYieldOverviewDtos(List<YieldOverviewDto> yieldOverviewDtos) {
        this.yieldOverviewDtos = yieldOverviewDtos;
    }

    public List<YieldNTFChartDto> getYieldNTFChartDtos() {
        return yieldNTFChartDtos;
    }

    public void setYieldNTFChartDtos(List<YieldNTFChartDto> yieldNTFChartDtos) {
        this.yieldNTFChartDtos = yieldNTFChartDtos;
    }

    public YieldTotalProcessesDto getTotalProcessesDtos() {
        return totalProcessesDtos;
    }

    public void setTotalProcessesDtos(YieldTotalProcessesDto totalProcessesDtos) {
        this.totalProcessesDtos = totalProcessesDtos;
    }
}
