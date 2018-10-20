package com.dmsoft.firefly.plugin.yield.controller;

import com.dmsoft.firefly.plugin.yield.charts.NDChart;
import com.dmsoft.firefly.plugin.yield.charts.data.NDBarChartData;
import com.dmsoft.firefly.plugin.yield.dto.YieldChartDto;
import com.google.common.collect.Lists;
import javafx.scene.chart.NumberAxis;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Map;

public class BuildNDChart {
    public static Map<String,Map<String,String>> initYeildChartData(List<YieldChartDto> spcChartDtoList, int search, Map<String, Color> colorCache, Map<String, Boolean> exportParam){
        List<NDBarChartData> ndcChartDataList = Lists.newArrayList();
        NDChart nd = buildYieldNDChart();
        return null;
    }

    private static NDChart buildYieldNDChart() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        //设置x轴数字标记线是否显示
        xAxis.setTickMarkVisible(false);
        //设置y轴数字标记线是否显示
        yAxis.setTickMarkVisible(false);
        //设置y轴两个数字之间的刻度线是否显示
        yAxis.setMinorTickVisible(false);
        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);
        NDChart<Double, Double> ndChart = new NDChart(xAxis, yAxis);
        ndChart.setAnimated(false);
        ndChart.setLegendVisible(false);
        return ndChart;
    }
}
