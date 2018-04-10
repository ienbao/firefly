package com.dmsoft.firefly.plugin.grr.charts.data;

import javafx.scene.chart.XYChart;

/**
 * Created by cherry on 2018/2/26.
 */
public class PointTooltip {

    private String seriesName;
    private XYChart.Data data;

    /**
     * Construct a new PointTooltip
     */
    public PointTooltip() {
    }

    /**
     * Construct a new PointTooltip with given seriesName and data
     *
     * @param seriesName series name
     * @param data       XYChart Data
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
