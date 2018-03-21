package com.dmsoft.firefly.plugin.chart;

import com.dmsoft.firefly.plugin.spc.charts.BoxPlotChart;
import com.dmsoft.firefly.plugin.spc.charts.data.BoxExtraData;
import com.dmsoft.firefly.plugin.spc.dto.chart.pel.BoxAndWhiskerData;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

/**
 * Created by cherry on 2018/2/10.
 */
public class BoxPlotChartApp extends Application {

    private Button addPointBtn;
    private Button removePointBtn;
    private Button showLineBtn;
    private Button removeLineBtn;
    private Button clearBtn;
    private Button addBtn;
    private Button changeColorBtn;
    private BoxPlotChart chart;

    private Map<String, XYChart.Data<Number, Number>> markers = Maps.newHashMap();
    private String seriesName = "A1";

    private void initComponents() {

        NumberAxis xAxis = new NumberAxis(0, 32, 1);
        NumberAxis yAxis = new NumberAxis();
        chart = new BoxPlotChart(xAxis, yAxis);
    }

    private void initData() {

//        List<BoxExtraData> chartData = Lists.newArrayList();
//        chartData.add(new BoxExtraData(1D, 27,25, 20, 32, 16, 20));
//        chartData.add(new BoxExtraData(2, 28,26, 30, 33, 22, 25));
//        chartData.add(new BoxExtraData(3, 29,30, 38, 40, 20, 32));
//        chartData.add(new BoxExtraData(4, 27,24, 30, 34, 22, 30));
//        chartData.add(new BoxExtraData(5, 28,26, 36, 40, 24, 32));
//        chartData.add(new BoxExtraData(6, 30,28, 38, 45, 25, 34));
//        chartData.add(new BoxExtraData(7, 36,36, 30, 44, 28, 39));
//        chartData.add(new BoxExtraData(8, 35,30, 18, 36, 16, 31));
//        chartData.add(new BoxExtraData(9, 42,40, 50, 52, 36, 41));
//        chartData.add(new BoxExtraData(10, 35,30, 34, 38, 28, 36));
//        chartData.add(new BoxExtraData(11, 30,24, 12, 30, 8, 32.4));
//        chartData.add(new BoxExtraData(12, 31,28, 40, 46, 25, 31.6));
//        chartData.add(new BoxExtraData(13, 34,28, 18, 36, 14, 32.6));
//        chartData.add(new BoxExtraData(14, 39,38, 30, 40, 26, 30.6));
//        chartData.add(new BoxExtraData(15, 30,28, 33, 40, 28, 30.6));
//        chartData.add(new BoxExtraData(16, 28,25, 10, 32, 6, 30.1));
//        chartData.add(new BoxExtraData(17, 30,26, 30, 42, 18, 27.3));
//        chartData.add(new BoxExtraData(18, 25,20, 18, 30, 10, 21.9));
//        chartData.add(new BoxExtraData(19, 21,20, 10, 30, 5, 21.9));
//        chartData.add(new BoxExtraData(20, 29,26, 16, 32, 10, 17.9));
//        chartData.add(new BoxExtraData(21, 40,38, 40, 44, 32, 18.9));
//        chartData.add(new BoxExtraData(22, 30,26, 40, 41, 12, 18.9));
//        chartData.add(new BoxExtraData(23, 32,30, 18, 34, 10, 18.9));
//        chartData.add(new BoxExtraData(24, 20,12, 23, 26, 12, 18.2));
//        chartData.add(new BoxExtraData(25, 32,30, 40, 45, 16, 18.9));
//        chartData.add(new BoxExtraData(26, 27,25, 35, 38, 20, 21.4));
//        chartData.add(new BoxExtraData(27, 29,24, 12, 30, 8, 19.6));
//        chartData.add(new BoxExtraData(28, 28,23, 44, 46, 15, 22.2));
//        chartData.add(new BoxExtraData(29, 30,28, 18, 30, 12, 23));
//        chartData.add(new BoxExtraData(30, 32,28, 18, 30, 12, 23.2));
//        chartData.add(new BoxExtraData(31, 29,28, 18, 30, 12, 22));

//        XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
//        chartData.forEach(data -> {
//            series.getCellData().add(
//                    new XYChart.Data(
//                            data.getxPos(),
//                            data.getQ3(),
//                            data)
//            );
//        });

//        BoxAndWhiskerData data = new BoxAndWhiskerData();
//        data.setData(chartData);
//        data.setColor(Color.RED);
//        chart.createChartSeries(data, seriesName);
//        chart.updateChartColor(seriesName, Color.YELLOW);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        initComponents();
        initData();
        addPointBtn = new Button("Add Point");
        removePointBtn = new Button("Remove Point");
        showLineBtn = new Button("Show Line");
        removeLineBtn = new Button("Remove Line");
        clearBtn = new Button("Clear");
        addBtn = new Button("Add");
        changeColorBtn = new Button("Change color");
        VBox vBox = new VBox();
        HBox hBox = new HBox();
        hBox.getChildren().addAll(addPointBtn, removePointBtn, showLineBtn, removeLineBtn, clearBtn, addBtn, changeColorBtn);
        vBox.getChildren().add(chart);
        vBox.getChildren().add(hBox);
        Scene scene = new Scene(vBox);
        primaryStage.setScene(scene);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/charts.css").toExternalForm());
        primaryStage.show();

        initEvent();
    }

    public void initEvent() {

        addPointBtn.setOnAction(event -> {
            XYChart.Data data = new XYChart.Data<>(5, 20);
            markers.put("Point", data);
//            chart.addSymbol(data, Color.RED);
        });

        removePointBtn.setOnAction(event -> {
//            chart.removeSymbol(markers.get("Point"));
        });

        showLineBtn.setOnAction(event -> {
            chart.addStroke();
        });

        removeLineBtn.setOnAction(event -> {
            chart.removeStroke();
        });

        clearBtn.setOnAction(event -> {
            chart.removeAllChildren();
        });

        addBtn.setOnAction(event -> {
            initData();
        });

        changeColorBtn.setOnAction(event -> chart.updateChartColor(seriesName, Color.YELLOW));

    }

    /**
     * Java main for when running without JavaFX launcher
     */
    public static void main(String[] args) {
        launch(args);
    }
}
