package com.dmsoft.firefly.plugin.chart;

import com.dmsoft.firefly.plugin.spc.charts.LinearChart;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IPathData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IPoint;
import com.google.common.collect.Lists;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;

/**
 * Created by cherry on 2018/3/9.
 */
public class BrokenLineApp extends Application {

    public Parent createContent() {

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        LinearChart chart = new LinearChart(xAxis, yAxis);


//        BrokenLine brokenLine = new BrokenLine();
        IPoint point = new IPoint<Double, Double>() {
            Double x[] = new Double[]{1D, 2D, 2D, 3D, 3D, 6D, 6D, 8D, 8D, 10D};
            Double y[] = new Double[]{1D, 1D, 10D, 10D, 2D, 2D, 30D, 30D, 200D, 200D};

            @Override
            public Double getXByIndex(int index) {
                return x[index];
            }

            @Override
            public Double getYByIndex(int index) {
                return y[index];
            }

            @Override
            public int getLen() {
                return x == null ? 0 : x.length;
            }
        };

        chart.getData().add(new XYChart.Series<>(FXCollections.observableArrayList(
                new XYChart.Data<>(1D, 1D),
                new XYChart.Data<>(2D, 10D),
                new XYChart.Data<>(3D, 100D),
                new XYChart.Data<>(4D, 1000D),
                new XYChart.Data<>(5D, 2D),
                new XYChart.Data<>(6D, 20D),
                new XYChart.Data<>(7D, 30D),
                new XYChart.Data<>(8D, 40D),
                new XYChart.Data<>(9D, 200D),
                new XYChart.Data<>(10D, 2000D)
        )));

        IPathData data = new IPathData() {
            @Override
            public IPoint getPoints() {
                return point;
            }

            @Override
            public String getPathName() {
                return "name";
            }

            @Override
            public Color getColor() {
                return Color.BLUE;
            }
        };
        chart.addPathMarker(Lists.newArrayList(data), "");
        return chart;
    }


    @Override
    public void start(Stage primaryStage) throws Exception {

        VBox vBox = new VBox();
        vBox.getChildren().add(createContent());
        Scene scene = new Scene(vBox, 600, 400);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/charts.css").toExternalForm());
        primaryStage.setTitle("XY Chart panel example.");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
