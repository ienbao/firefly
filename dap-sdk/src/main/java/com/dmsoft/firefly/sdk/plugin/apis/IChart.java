package com.dmsoft.firefly.sdk.plugin.apis;

import com.dmsoft.firefly.sdk.chart.dto.AbstractChartAlarmDto;
import com.dmsoft.firefly.sdk.chart.dto.AbstractChartDto;

import java.awt.*;
import java.util.List;


/**
 * chart api
 * {@link com.dmsoft.firefly.sdk.plugin.annotation.Chart}
 * Created by GuangLi on 2018/1/26.
 */
public interface IChart {

    /**
     * init all chart data
     *
     * @param chartDtos chart datas
     */
    void initData(List<AbstractChartDto> chartDtos);

    /**
     * add chart layer data
     *
     * @param chartDtos chart data
     */
    void addChartData(List<AbstractChartDto> chartDtos);

    /**
     * remove chart layer data
     *
     * @param chartDtos chart data
     */
    void removeChartData(List<AbstractChartDto> chartDtos);

    /**
     * set alarm data in chart
     *
     * @param chartAlarmDtos chart alarm dto
     */
    void setAlarmChartData(List<AbstractChartAlarmDto> chartAlarmDtos);

    /**
     * add alarm data in chart
     *
     * @param chartAlarmDtos chart alarm dto
     */
    void addAlarmChartData(List<AbstractChartAlarmDto> chartAlarmDtos);

    /**
     * remove alarm data in chart
     *
     * @param chartType chart type
     * @param alarmType alarm type
     */
    void removeAlarmChartData(String chartType, List<String> alarmType);

    /**
     * set each chart layer's color
     *
     * @param layerKey layer key
     * @param color    color
     */
    void setLayerColor(String layerKey, Color color);

    /**
     * set chart top layer
     *
     * @param layerKey layer key
     */
    void setTopLayer(String layerKey);
}
