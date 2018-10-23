package com.dmsoft.firefly.plugin.yield.controller;

import com.dmsoft.firefly.plugin.yield.charts.NDChart;
import com.dmsoft.firefly.plugin.yield.charts.data.NDBarChartData;
import com.dmsoft.firefly.plugin.yield.dto.YieldChartResultDto;
import com.dmsoft.firefly.plugin.yield.dto.YieldDetailChartDto;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.javafx.collections.MappingChange;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import sun.applet.resources.MsgAppletViewer;

import java.util.List;
import java.util.Map;

public class BuildChart {
    public static BarChart initYieldXhartData() {
    List<YieldChartResultDto> yieldChartResultDtos = Lists.newArrayList();
    BarChart barChart = initYeildChartData();
//    for (YieldDetailChartDto yieldDetailChartDto : yieldDetailChartDtoList){
//
//        if (yieldChartResultDtos ==null){
//            continue;
//        }
//            yieldDetailChartDto.getYieldChartResultDto();
//    }
    return barChart;
}
    public static BarChart initYeildChartData( ){
        //List<NDBarChartData> ndcChartDataList = Lists.newArrayList();
        BarChart barChart = buildYieldNDChart();
        return barChart;
    }
    public static BarChart buildYieldNDChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
//        //设置x轴数字标记线是否显示
        xAxis.setTickMarkVisible(false);
        //设置y轴数字标记线是否显示
        yAxis.setTickMarkVisible(false);
        //设置y轴两个数字之间的刻度线是否显示
        yAxis.setMinorTickVisible(true);
        xAxis.setAutoRanging(true);
        yAxis.setAutoRanging(true);
        yAxis.setAutoRanging(true);
        String[] years = {"%FPY", "%NTF", "%NG"};
        ObservableList<BarChart.Series> barChartData =
                FXCollections.observableArrayList(
                        new BarChart.Series("Lemons",
                                FXCollections.observableArrayList(
                                        new BarChart.Data(years[0], 10),
                                        new BarChart.Data(years[1], 16),
                                        new BarChart.Data(years[2], 14)))

                );

        BarChart barChart = new BarChart(xAxis, yAxis,barChartData,75);
        barChart.setLegendVisible(false);





//        XYChart.Series series1 = new XYChart.Series();
//        series1.getData().add(new XYChart.Data("123",12));
//        series1.getData().add(new XYChart.Data("456",23));
//        series1.getData().add(new XYChart.Data("789",10));
//        barChart.getCategoryGap();
//        BarChart barChart = new BarChart(new CategoryAxis(),new NumberAxis());
//        XYChart.Series series1 = new XYChart.Series();
//        series1.getData().add(new XYChart.Data("123",12));
//        series1.getData().add(new XYChart.Data("456",23));
//        series1.getData().add(new XYChart.Data("789",10));
//        barChart.getData().addAll(series1);

//        barChart.setAnimated(false);
//        barChart.setLegendVisible(false);

        return barChart;
    }




}
