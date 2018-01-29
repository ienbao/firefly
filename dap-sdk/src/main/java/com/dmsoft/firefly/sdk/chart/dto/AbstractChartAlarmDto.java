package com.dmsoft.firefly.sdk.chart.dto;

import java.util.List;

/**
 * Created by GuangLi on 2018/1/26.
 */
public class AbstractChartAlarmDto {
    private String charType;
    private String alarmType;
    private List<AbstractLayerDto> layerDatas;

    public String getCharType() {
        return charType;
    }

    public void setCharType(String charType) {
        this.charType = charType;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public List<AbstractLayerDto> getLayerDatas() {
        return layerDatas;
    }

    public void setLayerDatas(List<AbstractLayerDto> layerDatas) {
        this.layerDatas = layerDatas;
    }
}
