package com.dmsoft.firefly.plugin.grr.dto.analysis;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

import java.util.List;

/**
 * dto class for grr analysis data
 *
 * @author Can Guan
 */
public class GrrAnalysisDataDto extends AbstractValueObject {
    private List<Double> dataList;
    private String usl;
    private String lsl;

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
}
