package com.dmsoft.firefly.plugin.spc.charts.data.basic;

import javafx.scene.chart.XYChart;

import java.util.List;

/**
 * Created by cherry on 2018/2/26.
 */
public class PointRule {

    private XYChart.Data data;
    private String normalColor;
    private List<String> activeRule;

    public PointRule() {
    }

    public PointRule(XYChart.Data data, String normalColor) {
        this.data = data;
        this.normalColor = normalColor;
    }

    public PointRule(XYChart.Data data) {
        this.data = data;
    }

    public String getNormalColor() {
        return normalColor;
    }

    public void setNormalColor(String normalColor) {
        this.normalColor = normalColor;
    }

    public XYChart.Data getData() {
        return data;
    }

    public void setData(XYChart.Data data) {
        this.data = data;
    }

    public List<String> getActiveRule() {
        return activeRule;
    }

    public void setActiveRule(List<String> activeRule) {
        this.activeRule = activeRule;
    }
}
