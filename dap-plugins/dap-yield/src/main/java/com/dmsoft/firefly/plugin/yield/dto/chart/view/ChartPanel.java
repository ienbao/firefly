package com.dmsoft.firefly.plugin.yield.dto.chart.view;

import com.dmsoft.firefly.gui.components.chart.ChartUtils;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;

public class ChartPanel<T extends XYChart> extends VBox {
    private T chart;
    private ChartUtils chartUtils;

    public T getChart() {
        return chart;
    }

    public void setChart(T chart) {
        this.chart = chart;
    }

    public ChartUtils getChartUtils() {
        return chartUtils;
    }

    public void setChartUtils(ChartUtils chartUtils) {
        this.chartUtils = chartUtils;
    }
}
