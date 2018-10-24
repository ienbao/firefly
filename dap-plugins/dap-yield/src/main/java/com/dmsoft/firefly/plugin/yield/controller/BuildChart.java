package com.dmsoft.firefly.plugin.yield.controller;

import com.dmsoft.firefly.gui.components.chart.ChartOperatorUtils;
import com.dmsoft.firefly.plugin.yield.dto.YieldChartResultDto;
import com.dmsoft.firefly.plugin.yield.dto.YieldDetailChartDto;
import com.dmsoft.firefly.plugin.yield.dto.chart.YieldImageDto;
import com.dmsoft.firefly.plugin.yield.utils.UIConstant;
import com.dmsoft.firefly.plugin.yield.utils.YieldFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class BuildChart {
    private static final Float F9 = 0.9f;
    private static int digNum = 6;

    public static YieldImageDto buildBarChartImage(YieldDetailChartDto yieldDetailChartDto,Map<String, Boolean> exportParam ){

        YieldImageDto images = new YieldImageDto();

        boolean barChartValid = exportParam.containsKey(YieldFxmlAndLanguageUtils.getString(UIConstant.CHART_1));
        barChartValid = barChartValid && exportParam.get(YieldFxmlAndLanguageUtils.getString(UIConstant.CHART_1));
        barChartValid = barChartValid && yieldDetailChartDto.getYieldChartResultDto()!= null;

        if (barChartValid) {
            BarChart barChart = new BarChart(new CategoryAxis(), new NumberAxis());
            barChart.setAnimated(false);
            barChart.setLegendVisible(false);
            setBarChartData(yieldDetailChartDto.getYieldChartResultDto(),barChart);
            images.setYieldImagePath(exportBarChartImages("barChartImages",barChart));

        }

    return null;

    }

    private static String exportBarChartImages(String name, Node node) {
        Group vBox = new Group();
        Scene scene = new Scene(vBox);
        scene.getStylesheets().add(BuildChart.class.getClassLoader().getResource("css/grr_chart.css").toExternalForm());
        vBox.getChildren().clear();
        vBox.getChildren().add(node);
        WriteImage image = new WriteImage();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            image.image = node.snapshot(new SnapshotParameters(), null);
            countDownLatch.countDown();
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException ignored) {
        }

        String savePicPath = FileUtils.getAbsolutePath("../export/temp");
        File file = new File(savePicPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        String path = savePicPath + "/" + name + new Date().getTime() + ".png";

        try {
            file = new File(path);
            ChartOperatorUtils.saveImageUsingJPGWithQuality(SwingFXUtils.fromFXImage(image.image, null), file, F9);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    private static void setBarChartData(YieldChartResultDto yieldChartResultDto, BarChart barChart) {
        if(yieldChartResultDto ==null){
            return ;
        }
        Double[] barChartData = getArrayValue(yieldChartResultDto);
        XYChart.Series series1 = new XYChart.Series();
//        XYChart.Series series1 = new XYChart.Series();
//        series1.getData().add(new XYChart.Data("123",12));
//        series1.getData().add(new XYChart.Data("456",23));
//        series1.getData().add(new XYChart.Data("789",10));
        series1.getData().add(new XYChart.Data(YieldFxmlAndLanguageUtils.getString(UIConstant.BARCHART_FPY), DAPStringUtils.isInfinityAndNaN(yieldChartResultDto.getFPY()) ? 0 : yieldChartResultDto.getFPY()));
        series1.getData().add(new XYChart.Data(YieldFxmlAndLanguageUtils.getString(UIConstant.BARCHART_NTF),DAPStringUtils.isInfinityAndNaN(yieldChartResultDto.getNTF()) ?0 : yieldChartResultDto.getNTF()));
        series1.getData().add(new XYChart.Data(YieldFxmlAndLanguageUtils.getString(UIConstant.BARCHART_NG),DAPStringUtils.isInfinityAndNaN(yieldChartResultDto.getNG()) ? 0 : yieldChartResultDto.getNG()));
        barChart.getData().addAll(series1);
    }
    private static Double[] getArrayValue(YieldChartResultDto yieldChartResultDto) {
        Double[] value = new Double[3];
        value[0] = yieldChartResultDto.getFPY();
        value[1] = yieldChartResultDto.getNTF();
        value[2] = yieldChartResultDto.getNG();
        return value;
    }

    public static BarChart initYieldXhartData() {
    List<YieldChartResultDto> yieldChartResultDtos = Lists.newArrayList();
    BarChart barChart = initYeildChartData();
    return barChart;
}
    public static BarChart initYeildChartData(){
        //List<NDBarChartData> ndcChartDataList = Lists.newArrayList();
        BarChart barChart = buildYieldNDChart();
        return barChart;
    }
    public static BarChart buildYieldNDChart(  ) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
//        //设置x轴数字标记线是否显示
        xAxis.setTickMarkVisible(false);
        //设置y轴数字标记线是否显示
        yAxis.setTickMarkVisible(false);
        //设置y轴两个数字之间的刻度线是否显示
        yAxis.setMinorTickVisible(false);
        xAxis.setAutoRanging(true);
        yAxis.setAutoRanging(true);

        String[] years = {"%FPY", "%NTF", "%NG"};

        ObservableList<BarChart.Series> barChartData =
                FXCollections.observableArrayList(
                        new BarChart.Series("Lemons",
                                FXCollections.observableArrayList(
                                        new BarChart.Data(years[0], 10),
                                        new BarChart.Data(years[1], 16),
                                        new BarChart.Data(years[2], 14)))

                );

        BarChart barChart = new BarChart(xAxis, yAxis,barChartData,75);
        barChart.setLegendVisible(false);





//        XYChart.Series series1 = new XYChart.Series();
//        series1.getData().add(new XYChart.Data("123",12));
//        series1.getData().add(new XYChart.Data("456",23));
//        series1.getData().add(new XYChart.Data("789",10));
//        barChart.getCategoryGap();
//        BarChart barChart = new BarChart(new CategoryAxis(),new NumberAxis());
//        XYChart.Series series1 = new XYChart.Series();
//        series1.getData().add(new XYChart.Data("123",12));
//        series1.getData().add(new XYChart.Data("456",23));
//        series1.getData().add(new XYChart.Data("789",10));
//        barChart.getData().addAll(series1);

//        barChart.setAnimated(false);
//        barChart.setLegendVisible(false);

        return barChart;
    }


    private static class WriteImage {
        private WritableImage image;
    }

}
