package com.dmsoft.firefly.plugin.utils;

import com.dmsoft.firefly.plugin.spc.utils.ChartUtils;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

public class ChartUtilsTest extends Application {
    private StackedAreaChart chart;
    private NumberAxis xAxis;
    private NumberAxis yAxis;

    public XYChart createContent() {
        xAxis = new NumberAxis("X Values", 1.0d, 9.0d, 2.0d);
        yAxis = new NumberAxis("Y Values", 0.0d, 30.0d, 2.0d);

        ObservableList<StackedAreaChart.Series> areaChartData =
                FXCollections.observableArrayList(
                        new StackedAreaChart.Series("Series 1",
                                FXCollections.observableArrayList(
                                        new StackedAreaChart.Data(0, 4),
                                        new StackedAreaChart.Data(2, 5),
                                        new StackedAreaChart.Data(4, 4),
                                        new StackedAreaChart.Data(6, 2),
                                        new StackedAreaChart.Data(8, 6),
                                        new StackedAreaChart.Data(10, 8)
                                )),
                        new StackedAreaChart.Series("Series 2",
                                FXCollections.observableArrayList(
                                        new StackedAreaChart.Data(0, 8),
                                        new StackedAreaChart.Data(2, 2),
                                        new StackedAreaChart.Data(4, 9),
                                        new StackedAreaChart.Data(6, 7),
                                        new StackedAreaChart.Data(8, 5),
                                        new StackedAreaChart.Data(10, 7)
                                )),
                        new StackedAreaChart.Series("Series 3",
                                FXCollections.observableArrayList(
                                        new StackedAreaChart.Data(0, 2),
                                        new StackedAreaChart.Data(2, 5),
                                        new StackedAreaChart.Data(4, 8),
                                        new StackedAreaChart.Data(6, 6),
                                        new StackedAreaChart.Data(8, 9),
                                        new StackedAreaChart.Data(10, 7)
                                ))
                );
        chart = new StackedAreaChart(xAxis, yAxis, areaChartData);
        return chart;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        XYChart chart = createContent();
        ChartUtils utils = new ChartUtils(chart);
        utils.activeChartDraggable();
        Button btn = new Button();
        btn.setText("+");
        btn.setOnAction(event -> {
            System.out.println("Hello World! + ");
            utils.zoomInChart();
        });
        Button btn1 = new Button();
        btn1.setText("-");
        btn1.setOnAction(event -> {
            System.out.println("Hello World! - ");
            utils.zoomOutChart();
        });
        GridPane pane = new GridPane();
        RowConstraints btnRow = new RowConstraints(30);
        RowConstraints btn1Row = new RowConstraints(30);
        RowConstraints chartRow = new RowConstraints(500);
        pane.getRowConstraints().addAll(btnRow, btn1Row, chartRow);
        pane.addRow(0, btn);
        pane.addRow(1, btn1);
        pane.addRow(2, chart);
        pane.setPrefWidth(800);
        pane.setPrefHeight(800);
        primaryStage.setScene(new Scene(pane));
        primaryStage.show();
    }

    /**
     * Java main for when running without JavaFX launcher
     */
    public static void main(String[] args) {
        launch(args);
    }
}