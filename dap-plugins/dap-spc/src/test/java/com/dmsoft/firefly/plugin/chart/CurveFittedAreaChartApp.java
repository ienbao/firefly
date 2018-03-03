package com.dmsoft.firefly.plugin.chart;

import com.dmsoft.firefly.plugin.spc.charts.CurveFittedAreaChart;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Created by cherry on 2018/3/2.
 */
public class CurveFittedAreaChartApp extends Application {

    private CurveFittedAreaChart chart;
    private NumberAxis xAxis;
    private NumberAxis yAxis;

    public Parent createContent() {
        xAxis = new NumberAxis(0, 1000, 250);
        yAxis = new NumberAxis(0, 1000, 200);
        chart = new CurveFittedAreaChart(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);
        chart.setAlternativeColumnFillVisible(false);
        chart.setAlternativeRowFillVisible(false);
        final XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.getData().addAll(
                new XYChart.Data<Number, Number>(0, 950),
                new XYChart.Data<Number, Number>(200, 100),
                new XYChart.Data<Number, Number>(500, 200),
                new XYChart.Data<Number, Number>(750, 180),
                new XYChart.Data<Number, Number>(1000, 100));
        chart.getData().add(series);
        chart.setStyle("-fx-background-color: #0096ff");
        return chart;
    }

    @Override public void start(Stage primaryStage) throws Exception {

        StackPane stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: #462300");
        stackPane.getChildren().add(createContent());
        Scene scene = new Scene(stackPane);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/charts.css").toExternalForm());
        primaryStage.show();
    }

    /**
     * Java main for when running without JavaFX launcher
     */
    public static void main(String[] args) {
        launch(args);
    }
}
