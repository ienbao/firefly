package com.dmsoft.firefly.plugin.chart;

import com.dmsoft.firefly.plugin.spc.charts.NDChart;
import com.dmsoft.firefly.plugin.spc.charts.data.NDBarChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.XYChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.BarCategoryData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IBarChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.ILineData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IXYChartData;
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

    private NDChart<Double, Double> chart;
    String seriesName = "A1:All";

    private void initData() {

        List<BarCategoryData<Double, Double>> barCategoryData = Lists.newArrayList();
        List<BarCategoryData<Double, Double>> _barCategoryData = Lists.newArrayList();
        Double[] x = new Double[]{1D, 4D, 7D, 10D, 13D, 16D, 19D, 22D, 25D, 28D};
        Double[] y = new Double[10];
        Double[] y1 = new Double[10];
        Double[] barWidth = new Double[10];

        Random random = new Random();
        for (int i = 0; i < 10; i++) {
//            BarCategoryData<Double, Double> barCategoryData1 = new BarCategoryData();
//            BarCategoryData<Double, Double> barCategoryData2 = new BarCategoryData();
            double startValue = x[i];
            double endValue = startValue + 2;
            double value = random.nextInt(10);
            y[i] = value;
            y1[i] = Double.valueOf(random.nextInt(10));
            barWidth[i] = (startValue + endValue) / 2;
            System.out.println("start value: " + startValue);
            System.out.println("value: " + y[i]);

//            barCategoryData1.setStartValue(startValue);
//            barCategoryData1.setEndValue(endValue);
//            barCategoryData1.setBarWidth(endValue - startValue);
//            barCategoryData1.setValue(y[i]);
//            barCategoryData.add(barCategoryData1);
//
//            barCategoryData2.setStartValue(startValue);
//            barCategoryData2.setEndValue(endValue);
//            barCategoryData2.setBarWidth(endValue - startValue);
//            barCategoryData2.setValue(y1[i]);
//            _barCategoryData.add(barCategoryData2);
        }

        Color barColor = Color.RED;

        BarChartData barChartData = new BarChartData(x, y);
        List<NDBarChartData> ndBarChartDataList = Lists.newArrayList();
        NDBarChartData ndBarChartData = new NDBarChartData() {
            @Override
            public Number getXLowerBound() {
                return null;
            }

            @Override
            public Number getXUpperBound() {
                return null;
            }

            @Override
            public Number getYLowerBound() {
                return null;
            }

            @Override
            public Number getYUpperBound() {
                return null;
            }

            @Override
            public IBarChartData getBarChartData() {
                return barChartData;
            }

            @Override
            public IXYChartData getXYChartData() {
                return null;
            }

            @Override
            public List<ILineData> getLineData() {
                return null;
            }

            @Override
            public Color getColor() {
                return Color.BLUE;
            }

            @Override
            public String getUniqueKey() {
                return "unique key";
            }

            @Override
            public String getSeriesName() {
                return "ssss";
            }
        };
        ndBarChartDataList.add(ndBarChartData);
        chart.setData(ndBarChartDataList, null);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        chart = new NDChart(xAxis, yAxis);

        initData();

//        barChartData.setSeriesName("aaaaa");
//        barChartData.setBarData(_barCategoryData);
//        barChartData.setColor(Color.GREEN);

//        chart.createChartSeries(barChartData);
//        xyChartData.setColor(Color.GREEN);
//        xyChartData.setY(y1);
//        chart.addAreaSeries(xyChartData);

        VBox vBox = new VBox();
        HBox hBox = new HBox();
        Button clearBtn = new Button("Clear");
        Button addBtn = new Button("Add Data");
        Button changeColorBtn = new Button("ChangeColor");
        hBox.getChildren().add(clearBtn);
        hBox.getChildren().add(addBtn);
        hBox.getChildren().add(changeColorBtn);

        vBox.getChildren().add(chart);
        vBox.getChildren().add(hBox);
//        clearBtn.setOnAction(event -> {
//            chart.removeAllChildren();
//        });
//        addBtn.setOnAction(event -> initData());

        changeColorBtn.setOnAction(event -> chart.updateChartColor(seriesName, Color.RED));

        Scene scene = new Scene(vBox, 600, 400);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/charts.css").toExternalForm());
        primaryStage.setTitle("XY Chart panel example.");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
