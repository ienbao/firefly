package com.dmsoft.firefly.plugin.chart;

import com.dmsoft.firefly.plugin.spc.charts.NDChart;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Created by cherry on 2018/2/11.
 */
public class NDChartApp extends Application {

    private NDChart chart;

    @Override
    public void start(Stage primaryStage) throws Exception {

        initData();
        VBox vBox = new VBox();
        HBox hBox = new HBox();
        Button saveBtn = new Button("Save as");
        hBox.getChildren().add(saveBtn);
        vBox.getChildren().add(chart);
        vBox.getChildren().add(hBox);
        Scene scene = new Scene(vBox);
        primaryStage.setScene(scene);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/charts.css").toExternalForm());
        primaryStage.show();

    }

    private void initData() {

        AreaChart.Series areaChartData = new AreaChart.Series("Series 1", FXCollections.observableArrayList(
                new AreaChart.Data("0", 4),
                new AreaChart.Data("2", 5),
                new AreaChart.Data("4", 4),
                new AreaChart.Data("6", 2),
                new AreaChart.Data("8", 6),
                new AreaChart.Data("10", 8)
        ));

        ObservableList<BarChart.Series> barChartData = FXCollections.observableArrayList(
                new BarChart.Series("Series 2", FXCollections.observableArrayList(
                        new BarChart.Data("0", 4),
                        new BarChart.Data("2", 5),
                        new BarChart.Data("4", 4),
                        new BarChart.Data("6", 2),
                        new BarChart.Data("8", 6),
                        new BarChart.Data("10", 8)
                )));

        chart = new NDChart(new CategoryAxis(), new NumberAxis());
        chart.setData(barChartData);
//        chart = new NDChart(new CategoryAxis(), new NumberAxis(), barChartData);
        chart.addAreaSeries(areaChartData);
    }

    /**
     * Java main for when running without JavaFX launcher
     */
    public static void main(String[] args) {
        launch(args);
    }
}
