package com.dmsoft.firefly.plugin.chart;

import com.dmsoft.firefly.plugin.spc.charts.NDChart;
import com.dmsoft.firefly.plugin.spc.charts.data.XYChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.BarCategoryData;
import com.dmsoft.firefly.plugin.spc.dto.chart.BarChartData;
import com.google.common.collect.Lists;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.List;
import java.util.Random;

/**
 * Created by cherry on 2018/3/5.
 */
public class NDChartApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        List<BarCategoryData<Double, Double>> barCategoryData = Lists.newArrayList();
        List<BarCategoryData<Double, Double>> _barCategoryData = Lists.newArrayList();
        Double[] x = new Double[]{1D, 4D, 7D, 10D, 13D, 16D, 19D, 22D, 25D, 28D};
        Double[] y = new Double[10];
        Double[] y1 = new Double[10];
        Double[] barWidth = new Double[10];

        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            BarCategoryData<Double, Double> barCategoryData1 = new BarCategoryData();
            BarCategoryData<Double, Double> barCategoryData2 = new BarCategoryData();
            double startValue = x[i];
            double endValue = startValue + 2;
            double value = random.nextInt(10);
            y[i] = value;
            y1[i] = Double.valueOf(random.nextInt(10));
            barWidth[i] = (startValue + endValue) / 2;
            System.out.println("start value: " + startValue);
            System.out.println("value: " + y[i]);

            barCategoryData1.setStartValue(startValue);
            barCategoryData1.setEndValue(endValue);
            barCategoryData1.setBarWidth(endValue - startValue);
            barCategoryData1.setValue(y[i]);
            barCategoryData.add(barCategoryData1);

            barCategoryData2.setStartValue(startValue);
            barCategoryData2.setEndValue(endValue);
            barCategoryData2.setBarWidth(endValue - startValue);
            barCategoryData2.setValue(y1[i]);
            _barCategoryData.add(barCategoryData2);
        }

        Color barColor = Color.RED;
        String seriesName = "A1:All";
        BarChartData<Double, Double> barChartData = new BarChartData<>(seriesName);
        barChartData.setBarData(barCategoryData);
        barChartData.setColor(barColor);

        XYChartData<Double, Double> xyChartData = new XYChartData();
        xyChartData.setX(x);
        xyChartData.setY(y);
        xyChartData.setColor(barColor);

        NDChart<Double, Double> chart = new NDChart(xAxis, yAxis, barChartData);
        chart.addAreaSeries(xyChartData);

        barChartData.setSeriesName("aaaaa");
        barChartData.setBarData(_barCategoryData);
        barChartData.setColor(Color.GREEN);

        chart.createChartSeries(barChartData);
        xyChartData.setColor(Color.GREEN);
        xyChartData.setY(y1);
        chart.addAreaSeries(xyChartData);

        VBox vBox = new VBox();
        HBox hBox = new HBox();
        Button saveBtn = new Button("Save as");
        hBox.getChildren().add(saveBtn);
        vBox.getChildren().add(chart);
        vBox.getChildren().add(hBox);

        Scene scene = new Scene(vBox, 600, 400);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/charts.css").toExternalForm());
        primaryStage.setTitle("XY Chart panel example.");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
