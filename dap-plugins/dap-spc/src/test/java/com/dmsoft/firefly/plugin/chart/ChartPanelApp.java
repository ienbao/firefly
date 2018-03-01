package com.dmsoft.firefly.plugin.chart;

import com.dmsoft.firefly.plugin.spc.charts.view.ChartPanel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Created by cherry on 2018/2/28.
 */
public class ChartPanelApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

//        VBox chartPane = new VBox();
        LineChart chart = new LineChart(new NumberAxis(), new NumberAxis());
        ChartPanel<XYChart> chartChartPanel = new ChartPanel<XYChart>(chart);
        chartChartPanel.setMaxSize(1280,704);
//        chartPane.getChildren().add(chart);
        VBox vBox = new VBox();
        vBox.getChildren().add(new Button("sssssss"));
        Scene scene = new Scene(chartChartPanel);
        primaryStage.setScene(scene);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/spc_app.css").toExternalForm());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/charts.css").toExternalForm());
        primaryStage.show();
    }
}
