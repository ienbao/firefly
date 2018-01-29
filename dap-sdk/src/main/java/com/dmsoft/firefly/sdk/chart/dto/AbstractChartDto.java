package com.dmsoft.firefly.sdk.chart.dto;

import java.util.List;

/**
 * Created by GuangLi on 2018/1/26.
 */
public class AbstractChartDto {
    private String charType;
    private String[] legend;
    private String[] attribute;
    private List<AbstractLayerDto> layerDatas;
//    private AbstractChartAlarmDto layerAlarmDatas;

    public String getCharType() {
        return charType;
    }

    public void setCharType(String charType) {
        this.charType = charType;
    }

    public String[] getLegend() {
        return legend;
    }

    public void setLegend(String[] legend) {
        this.legend = legend;
    }

    public String[] getAttribute() {
        return attribute;
    }

    public void setAttribute(String[] attribute) {
        this.attribute = attribute;
    }

    public List<AbstractLayerDto> getLayerDatas() {
        return layerDatas;
    }

    public void setLayerDatas(List<AbstractLayerDto> layerDatas) {
        this.layerDatas = layerDatas;
    }
}
