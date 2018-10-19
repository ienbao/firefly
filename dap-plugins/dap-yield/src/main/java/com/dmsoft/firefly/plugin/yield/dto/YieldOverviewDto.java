package com.dmsoft.firefly.plugin.yield.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

/**
 * Created by Tod Dylan on 2018/10/16.
 */
public class YieldOverviewDto extends AbstractValueObject {

    private String key;
    private String itemName;
    private String uslOrPass;
    private String lslOrPass;
    private Integer totalSamples;
    private Integer fpySamples;
    private Integer passSamples;
    private Integer ntfSamples;
    private Integer ngSamples;
    private Double fpyPercent;
    private Double ntfPercent;
    private Double ngPersent;

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

    public String getUslOrPass() {
        return uslOrPass;
    }

    public void setUslOrPass(String uslOrPass) {
        this.uslOrPass = uslOrPass;
    }

    public String getLslOrPass() {
        return lslOrPass;
    }

    public void setLslOrPass(String lslOrPass) {
        this.lslOrPass = lslOrPass;
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

    public Double getNgPersent() {
        return ngPersent;
    }

    public void setNgPersent(Double ngPersent) {
        this.ngPersent = ngPersent;
    }
}
