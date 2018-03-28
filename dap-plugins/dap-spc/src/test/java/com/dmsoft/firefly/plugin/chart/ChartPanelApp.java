package com.dmsoft.firefly.plugin.chart;

import com.dmsoft.firefly.plugin.spc.charts.view.ChartPanel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Created by cherry on 2018/2/28.
 */
public class ChartPanelApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        LineChart chart = new LineChart(new NumberAxis(), new NumberAxis());
        ChartPanel<XYChart> chartPanel = new ChartPanel<XYChart>(chart);
//        chartPanel.setMaxSize(1280,704);
        chartPanel.setLegend("- - - LSL, USL    —— m Line    —— 6s Line");
        Scene scene = new Scene(chartPanel, 315, 315);
        primaryStage.setScene(scene);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/spc_app.css").toExternalForm());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/charts.css").toExternalForm());
        primaryStage.show();
    }
}
