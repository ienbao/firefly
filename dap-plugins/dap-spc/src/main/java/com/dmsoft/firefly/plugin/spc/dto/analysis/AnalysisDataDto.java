package com.dmsoft.firefly.plugin.spc.dto.analysis;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

import java.util.List;

/**
 * analysis data dto
 *
 * @author Can Guan
 */
public class AnalysisDataDto extends AbstractValueObject {
    private List<Double> dataList;
    private String usl;
    private String lsl;
    private Double ndcMax;
    private Double ndcMin;

    public List<Double> getDataList() {
        return dataList;
    }

    public void setDataList(List<Double> dataList) {
        this.dataList = dataList;
    }

    public String getUsl() {
        return usl;
    }

    public void setUsl(String usl) {
        this.usl = usl;
    }

    public String getLsl() {
        return lsl;
    }

    public void setLsl(String lsl) {
        this.lsl = lsl;
    }

    public Double getNdcMax() {
        return ndcMax;
    }

    public void setNdcMax(Double ndcMax) {
        this.ndcMax = ndcMax;
    }

    public Double getNdcMin() {
        return ndcMin;
    }

    public void setNdcMin(Double ndcMin) {
        this.ndcMin = ndcMin;
    }
}
