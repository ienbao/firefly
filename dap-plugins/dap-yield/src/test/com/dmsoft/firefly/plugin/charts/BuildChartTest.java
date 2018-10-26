package com.dmsoft.firefly.plugin.charts;

import com.dmsoft.firefly.plugin.yield.charts.ChartTooltip;
import com.dmsoft.firefly.plugin.yield.controller.BuildChart;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class BuildChartTest extends Application {


    private ChartTooltip chartTooltip;
    private Parent getContent(){
        VBox vBox = null;

            vBox = new VBox();
            BarChart barChart =null; //BuildChart.buildBarChartImage(null,null);
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
