package com.dmsoft.firefly.plugin.spc.charts.data.basic;

import javafx.scene.chart.XYChart;

/**
 * Created by cherry on 2018/2/26.
 */
public class PointTooltip {

    private String seriesName;
    private XYChart.Data data;

    public PointTooltip() {
    }

    public PointTooltip(String seriesName, XYChart.Data data) {
        this.seriesName = seriesName;
        this.data = data;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public XYChart.Data getData() {
        return data;
    }

    public void setData(XYChart.Data data) {
        this.data = data;
    }
}
