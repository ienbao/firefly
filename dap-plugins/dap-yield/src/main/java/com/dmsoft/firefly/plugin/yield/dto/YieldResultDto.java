package com.dmsoft.firefly.plugin.yield.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

import java.util.List;

public class YieldResultDto extends AbstractValueObject {

    private List<YieldOverviewDto> yieldOverviewDtos;
    private List<YieldNTFChartDto> yieldNTFChartDtos;
    private List<YieldTotalProcessesDto> totalProcessesDtos;
    private  YieldTotalProcessesDto yieldTotalProcessesDto;

    public YieldTotalProcessesDto getYieldTotalProcessesDto() {
        return yieldTotalProcessesDto;
    }

    public void setYieldTotalProcessesDto(YieldTotalProcessesDto yieldTotalProcessesDto) {
        this.yieldTotalProcessesDto = yieldTotalProcessesDto;
    }

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

    public List<YieldTotalProcessesDto> getTotalProcessesDtos() {
        return totalProcessesDtos;
    }

    public void setTotalProcessesDtos(List<YieldTotalProcessesDto> totalProcessesDtos) {
        this.totalProcessesDtos = totalProcessesDtos;
    }
}
