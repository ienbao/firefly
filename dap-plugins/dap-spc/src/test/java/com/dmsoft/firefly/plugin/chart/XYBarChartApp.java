package com.dmsoft.firefly.plugin.chart;

import com.dmsoft.firefly.plugin.spc.charts.XYBarChart;
import com.dmsoft.firefly.plugin.spc.charts.data.XYChartData;
import com.dmsoft.firefly.plugin.spc.dto.chart.BarCategoryData;
import com.dmsoft.firefly.plugin.spc.dto.chart.BarChartData;
import com.google.common.collect.Lists;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Created by cherry on 2018/2/27.
 */
public class XYBarChartApp extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        List<BarCategoryData<Double, Double>> barCategoryData = Lists.newArrayList();
        Double[] x = new Double[]{1D, 4D, 7D, 10D, 13D, 16D, 19D, 22D, 25D, 28D};
        Double[] y = new Double[10];
        Double[] barWidth = new Double[10];

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

        XYChartData<Double, Double> xyChartData = new XYChartData();
        xyChartData.setX(barWidth);
        xyChartData.setY(y);
        xyChartData.setColor(barColor);

        XYBarChart<Double, Double> chart = new XYBarChart(xAxis, yAxis, barChartData);
//        chart.addAreaSeries(barChartData);
//        chart.addAreaSeries(xyChartData);

        VBox vBox = new VBox();
        HBox hBox = new HBox();
        Button saveBtn = new Button("Save as");
        hBox.getChildren().add(saveBtn);
        vBox.getChildren().add(chart);
        vBox.getChildren().add(hBox);

        saveBtn.setOnMouseClicked(event -> {
            WritableImage image = chart.snapshot(new SnapshotParameters(), null);
            try {
                String filePath = "/Users/mac/Desktop/a.png";
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", new File(filePath));
                System.out.println("保存成功!");
            } catch (IOException ex) {
                System.out.println("保存失败:" + ex.getMessage());
            }
        });
        Scene scene = new Scene(vBox, 600, 400);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/charts.css").toExternalForm());
        primaryStage.setTitle("XY Chart panel example.");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


}
