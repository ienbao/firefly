package com.dmsoft.firefly.plugin.chart;

import com.dmsoft.firefly.plugin.spc.charts.*;
import com.dmsoft.firefly.plugin.spc.charts.data.XYChartData;
import com.dmsoft.firefly.plugin.spc.dto.chart.BarCategoryData;
import com.dmsoft.firefly.plugin.spc.dto.chart.BarChartData;
import com.google.common.collect.Lists;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

/**
 * Created by cherry on 2018/3/1.
 */
public class MultipleAxisXYChartApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
//        yAxis.setLabel("Series 1");

        Double[] x = new Double[]{1D, 4D, 7D, 10D, 13D, 16D, 19D, 22D, 25D, 28D};
        Double[] y = new Double[10];
        Double[] barWidth = new Double[10];

        List<BarCategoryData<Double, Double>> barCategoryData = Lists.newArrayList();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            BarCategoryData<Double, Double> barCategoryData1 = new BarCategoryData();
            double startValue = x[i];
            double endValue = startValue + 2;
            double value = random.nextInt(10);
            y[i] = value;
            barWidth[i] = (startValue + endValue) / 2;
            System.out.println("start value: " + startValue);
            System.out.println("end value: " + endValue);
            System.out.println("bar width: " + (endValue - startValue));
            System.out.println("value: " + value);
            barCategoryData1.setStartValue(startValue);
            barCategoryData1.setEndValue(endValue);
            barCategoryData1.setBarWidth(endValue - startValue);
            barCategoryData1.setValue(value);
            barCategoryData.add(barCategoryData1);
        }

        String barColor = "#5fb222";
//        String barColor = "#e92822";
        String seriesName = "A1:All";
        BarChartData<Double, Double> barChartData = new BarChartData<>(seriesName);
        barChartData.setBarData(barCategoryData);
        barChartData.setColor(barColor);

        Double xi[] = new Double[]{1D, 4D, 7D, 10D, 13D, 16D, 19D, 22D, 25D, 28D};
        Double yi[] = new Double[]{1D, 10D, 100D, 1000D, 2D, 20D, 30D, 40D, 200D, 2000D};
        Double ids[] = new Double[]{1D, 2D, 3D, 4D, 5D, 6D, 7D, 8D, 9D, 10D};
        XYChartData<Double, Double> xyChartData = new XYChartData();
        xyChartData.setX(xi);
        xyChartData.setY(yi);
        xyChartData.setIds(ids);
        xyChartData.setColor("#5fb222");
        xyChartData.setSeriesName(seriesName);

        XYBarChart baseChart = new XYBarChart(xAxis, yAxis);
        baseChart.createChartSeries(barChartData);

//        LinearChart baseChart = new LinearChart(xAxis, yAxis);
//        baseChart.createChartSeries(xyChartData, null);
//        baseChart.getData().add(prepareSeries("Series 1", (variable) -> (double)variable));

        final XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Series 2");
        series.getData().addAll(
                new XYChart.Data<Number, Number>(0, 950),
                new XYChart.Data<Number, Number>(200, 100),
                new XYChart.Data<Number, Number>(500, 120),
                new XYChart.Data<Number, Number>(750, 180),
                new XYChart.Data<Number, Number>(1000, 200));

        MultipleAxisXYChart chart = new MultipleAxisXYChart(baseChart);
        chart.createBackgroundChart(FXCollections.observableArrayList(series), "#5fb222", "CurveFittedAreaChart");
        chart.setPrefSize(500, 250);
        chart.setStyle("-fx-background-color: #462300");
        baseChart.setStyle("-fx-background-color: #0096ff");
        Scene scene = new Scene(chart);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/charts.css").toExternalForm());
        primaryStage.setTitle("MultipleAxesLineChart");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private XYChart.Series<Number, Number> prepareSeries(String name, Function<Integer, Double> function) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(name);
        for (int i = 0; i < 100; i++) {
            series.getData().add(new XYChart.Data<>(i, function.apply(i)));
        }
        return series;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
