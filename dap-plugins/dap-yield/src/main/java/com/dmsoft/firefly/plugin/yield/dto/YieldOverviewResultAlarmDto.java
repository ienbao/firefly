package com.dmsoft.firefly.plugin.yield.dto;

import java.util.Map;

public class YieldOverviewResultAlarmDto {
    private String key;
    private String itemName;
    private String lslOrFail;
    private String uslOrPass;
    private Integer totalSamples;
    private Integer fpySamples;
    private Integer passSamples;
    private Integer ntfSamples;
    private Integer ngSamples;
    private Map<String, OverviewAlarmDto> overviewAlarmDtoMap;

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

    public String getLslOrFail() {
        return lslOrFail;
    }

    public void setLslOrFail(String lslOrFail) {
        this.lslOrFail = lslOrFail;
    }

    public String getUslOrPass() {
        return uslOrPass;
    }

    public void setUslOrPass(String uslOrPass) {
        this.uslOrPass = uslOrPass;
    }

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

    public Map<String, OverviewAlarmDto> getOverviewAlarmDtoMap() {
        return overviewAlarmDtoMap;
    }

    public void setOverviewAlarmDtoMap(Map<String, OverviewAlarmDto> overviewAlarmDtoMap) {
        this.overviewAlarmDtoMap = overviewAlarmDtoMap;
    }
}
