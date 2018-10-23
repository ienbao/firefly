package com.dmsoft.firefly.plugin.charts;

import com.dmsoft.firefly.plugin.yield.charts.ChartTooltip;
import com.dmsoft.firefly.plugin.yield.charts.NDChart;
import com.dmsoft.firefly.plugin.yield.charts.data.NDBarChartData;
import com.dmsoft.firefly.plugin.yield.charts.data.basic.IBarChartData;
import com.dmsoft.firefly.plugin.yield.charts.data.basic.IXYChartData;
import com.dmsoft.firefly.plugin.yield.controller.BuildChart;
import com.dmsoft.firefly.plugin.yield.dto.YieldChartResultDto;
import com.dmsoft.firefly.plugin.yield.dto.YieldDetailChartDto;
import com.google.common.collect.Lists;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.springframework.cglib.beans.BulkBean;

import java.util.List;

public class BuildChartTest extends Application {
    //private BarChart barChart;
    private YieldChartResultDto yieldChartResultDto;
    private ChartTooltip chartTooltip;
    private Parent getContent(){
        VBox vBox = new VBox();
        BarChart barChart = BuildChart.buildYieldNDChart();
        vBox.getChildren().add(barChart);
        return vBox;
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(getContent(), 415, 315);
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
