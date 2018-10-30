package com.dmsoft.firefly.plugin.yield.dto;

import java.util.Map;

public class YieldChartResultAlermDto {
    private Integer totalSamples;
    private Integer fpySamples;
    private Integer passSamples;
    private Integer ntfSamples;
    private Integer ngSamples;
    private Double fpyPercent;
    private Double ntfPercent;
    private Double ngPercent;
    Map<String ,YieldChartAlermDto> yieldChartResultAlermDtoMap;

    public Integer getTotalSamples() {
        return totalSamples;
    }

    public void setTotalSamples(Integer totalSamples) {
        this.totalSamples = totalSamples;
    }

    public Integer getFpySamples() {
        return fpySamples;
    }

    public void setFpySamples(Integer fpySamples) {
        this.fpySamples = fpySamples;
    }

    public Integer getPassSamples() {
        return passSamples;
    }

    public void setPassSamples(Integer passSamples) {
        this.passSamples = passSamples;
    }

    public Integer getNtfSamples() {
        return ntfSamples;
    }

    public void setNtfSamples(Integer ntfSamples) {
        this.ntfSamples = ntfSamples;
    }

    public Integer getNgSamples() {
        return ngSamples;
    }

    public void setNgSamples(Integer ngSamples) {
        this.ngSamples = ngSamples;
    }

    public Double getFpyPercent() {
        return fpyPercent;
    }

    public void setFpyPercent(Double fpyPercent) {
        this.fpyPercent = fpyPercent;
    }

    public Double getNtfPercent() {
        return ntfPercent;
    }

    public void setNtfPercent(Double ntfPercent) {
        this.ntfPercent = ntfPercent;
    }

    public Double getNgPercent() {
        return ngPercent;
    }

    public void setNgPercent(Double ngPercent) {
        this.ngPercent = ngPercent;
    }

    public Map<String, YieldChartAlermDto> getYieldChartResultAlermDtoMap() {
        return yieldChartResultAlermDtoMap;
    }

    public void setYieldChartResultAlermDtoMap(Map<String, YieldChartAlermDto> yieldChartResultAlermDtoMap) {
        this.yieldChartResultAlermDtoMap = yieldChartResultAlermDtoMap;
    }
}

