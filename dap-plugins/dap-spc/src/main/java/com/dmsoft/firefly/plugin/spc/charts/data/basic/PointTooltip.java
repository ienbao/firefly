package com.dmsoft.firefly.plugin.spc.charts.data.basic;

import javafx.scene.chart.XYChart;

/**
 * Created by cherry on 2018/2/26.
 */
public class PointTooltip {

    private String seriesName;
    private XYChart.Data data;

    /**
     * Constructor for PointTooltip
     */
    public PointTooltip() {
    }

    /**
     * Constructor for PointTooltip
     *
     * @param seriesName series name
     * @param data       point node data
     */
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
