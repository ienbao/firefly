package com.dmsoft.firefly.plugin.yield.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

import java.util.List;

/**
 * Created by Tod Dylan on 2018/10/16.
 */
public class YieldAnalysisDataDto extends AbstractValueObject {
    private List<Double> dataList;
    private String uslOrPass;
    private String lslOrPass;

    public List<Double> getDataList() {
        return dataList;
    }

    public void setDataList(List<Double> dataList) {
        this.dataList = dataList;
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
}
