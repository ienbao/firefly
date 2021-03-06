package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.gui.components.chart.ChartOperatorUtils;
import com.dmsoft.firefly.plugin.spc.charts.BoxPlotChart;
import com.dmsoft.firefly.plugin.spc.charts.ControlChart;
import com.dmsoft.firefly.plugin.spc.charts.NDChart;
import com.dmsoft.firefly.plugin.spc.charts.data.BoxPlotChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.ControlChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.NDBarChartData;
import com.dmsoft.firefly.plugin.spc.dto.SpcChartDto;
import com.dmsoft.firefly.plugin.spc.dto.analysis.SpcChartResultDto;
import com.dmsoft.firefly.plugin.spc.dto.chart.SpcBoxChartData;
import com.dmsoft.firefly.plugin.spc.dto.chart.SpcControlChartData;
import com.dmsoft.firefly.plugin.spc.dto.chart.SpcNdChartData;
import com.dmsoft.firefly.plugin.spc.dto.chart.SpcRunChartData;
import com.dmsoft.firefly.plugin.spc.utils.FileUtils;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import com.dmsoft.firefly.plugin.spc.utils.enums.SpcExportItemKey;
import com.dmsoft.firefly.sdk.utils.ColorUtils;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.NumberAxis;
import javafx.scene.image.WritableImage;

import java.awt.*;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by GuangLi on 2018/3/14.
 */
public class BuildChart {//生成图
    private static final Float F9 = 0.9f;

    /**
     * init spc chart and export
     *
     * @param spcChartDtoList list of spc chart dtos
     * @param search          search
     * @param colorCache      color set
     * @param exportParam     export param
     * @return exported chart path
     */
    public static Map<String, Map<String, String>> initSpcChartData(List<SpcChartDto> spcChartDtoList, int search, Map<String, Color> colorCache, Map<String, Boolean> exportParam) {//初始化spc图数据
        List<NDBarChartData> ndcChartDataList = Lists.newArrayList();
        List<ControlChartData> runChartDataList = Lists.newArrayList();
        List<ControlChartData> xBarChartDataList = Lists.newArrayList();
        List<ControlChartData> rangeChartDataList = Lists.newArrayList();
        List<ControlChartData> sdChartDataList = Lists.newArrayList();
        List<ControlChartData> medianChartDataList = Lists.newArrayList();
        List<BoxPlotChartData> boxChartDataList = Lists.newArrayList();
        List<ControlChartData> mrChartDataList = Lists.newArrayList();
        Map<String, Map<String, String>> result = Maps.newHashMap();
        NDChart nd = buildND();
        ControlChart run = buildRunChart();
        ControlChart xbar = buildControlChart();
        ControlChart range = buildControlChart();
        ControlChart sd = buildControlChart();
        ControlChart med = buildControlChart();
        ControlChart mr = buildControlChart();
        BoxPlotChart box = buildBox();

        int i = 0;
        for (SpcChartDto spcChartDto : spcChartDtoList) {
            String key = spcChartDto.getKey();
            String condition = (DAPStringUtils.isBlank(spcChartDto.getCondition())) ? "All" : spcChartDto.getCondition();
            String seriesName = spcChartDto.getItemName() + "::" + condition;
            javafx.scene.paint.Color color = ColorUtils.toFxColorFromAwtColor(colorCache.get(key));
            SpcChartResultDto spcChartResultDto = spcChartDto.getResultDto();
            if (spcChartResultDto == null) {
                continue;
            }

            Map<String, String> chartPath = Maps.newHashMap();
            if (exportParam.get(SpcExportItemKey.ND_CHART.getCode())) {
                //nd chart
                SpcNdChartData iNdcChartData = new SpcNdChartData(key, spcChartResultDto.getNdcResult(), color);
                iNdcChartData.setSeriesName(seriesName);
                ndcChartDataList.add(iNdcChartData);
                nd.setData(Lists.newArrayList(iNdcChartData), null);
                chartPath.put(UIConstant.SPC_CHART_NAME[0], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[0], nd));
            }
            if (exportParam.get(SpcExportItemKey.RUN_CHART.getCode())) {
                //run chart
                SpcRunChartData iRunChartData = new SpcRunChartData(key, spcChartResultDto.getRunCResult(), null, color);
                iRunChartData.setSeriesName(seriesName);
                runChartDataList.add(iRunChartData);
                run.setData(Lists.newArrayList(iRunChartData), null);
                chartPath.put(UIConstant.SPC_CHART_NAME[1], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[1], run));
            }
            if (exportParam.get(SpcExportItemKey.X_BAR_CHART.getCode())) {
                //x bar chart
                SpcControlChartData xBarChartData = new SpcControlChartData(key, spcChartResultDto.getXbarCResult(), color);
                xBarChartData.setSeriesName(seriesName);
                xBarChartDataList.add(xBarChartData);
                BuildChart.setControlChartData(xbar, Lists.newArrayList(xBarChartData));
                chartPath.put(UIConstant.SPC_CHART_NAME[2], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[2], xbar));
            }
            if (exportParam.get(SpcExportItemKey.RANGE_CHART.getCode())) {
                //range chart
                SpcControlChartData rangeChartData = new SpcControlChartData(key, spcChartResultDto.getRangeCResult(), color);
                rangeChartData.setSeriesName(seriesName);
                rangeChartDataList.add(rangeChartData);
                BuildChart.setControlChartData(range, Lists.newArrayList(rangeChartData));
                chartPath.put(UIConstant.SPC_CHART_NAME[3], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[3], range));
            }
            if (exportParam.get(SpcExportItemKey.SD_CHART.getCode())) {
                //sd chart
                SpcControlChartData sdChartData = new SpcControlChartData(key, spcChartResultDto.getSdCResult(), color);
                sdChartData.setSeriesName(seriesName);
                sdChartDataList.add(sdChartData);
                BuildChart.setControlChartData(sd, Lists.newArrayList(sdChartData));
                chartPath.put(UIConstant.SPC_CHART_NAME[4], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[4], sd));
            }
            if (exportParam.get(SpcExportItemKey.MEDIAN_CHART.getCode())) {
                //median chart
                SpcControlChartData medianChartData = new SpcControlChartData(key, spcChartResultDto.getMedianCResult(), color);
                medianChartData.setSeriesName(seriesName);
                medianChartDataList.add(medianChartData);
                BuildChart.setControlChartData(med, Lists.newArrayList(medianChartData));
                chartPath.put(UIConstant.SPC_CHART_NAME[5], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[5], med));
            }
            if (exportParam.get(SpcExportItemKey.BOX_CHART.getCode())) {
                //box chart
                SpcBoxChartData iBoxChartData = new SpcBoxChartData(key, spcChartResultDto.getBoxCResult(), color);
                iBoxChartData.setSeriesName(seriesName);
                boxChartDataList.add(iBoxChartData);
                box.setData(Lists.newArrayList(iBoxChartData), null);
                chartPath.put(UIConstant.SPC_CHART_NAME[6], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[6], box));
            }
            if (exportParam.get(SpcExportItemKey.MR_CHART.getCode())) {
                //mr chart
                SpcControlChartData mrChartData = new SpcControlChartData(key, spcChartResultDto.getMrCResult(), color);
                mrChartData.setSeriesName(seriesName);
                mrChartDataList.add(mrChartData);
                BuildChart.setControlChartData(mr, Lists.newArrayList(mrChartData));
                chartPath.put(UIConstant.SPC_CHART_NAME[7], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[7], mr));
            }
            result.put(key, chartPath);

            if (search > 1 && (i + 1) % search == 0) {
                nd.setData(ndcChartDataList, null);
                ndcChartDataList.clear();
                run.setData(runChartDataList, null);
                runChartDataList.clear();
                BuildChart.setControlChartData(xbar, xBarChartDataList);
                xBarChartDataList.clear();
                BuildChart.setControlChartData(range, rangeChartDataList);
                rangeChartDataList.clear();
                BuildChart.setControlChartData(sd, sdChartDataList);
                sdChartDataList.clear();
                BuildChart.setControlChartData(med, medianChartDataList);
                medianChartDataList.clear();
                box.setData(boxChartDataList, null);
                boxChartDataList.clear();
                BuildChart.setControlChartData(mr, mrChartDataList);
                mrChartDataList.clear();
                Map<String, String> summaryPath = Maps.newHashMap();
                if (exportParam.get(SpcExportItemKey.ND_CHART.getCode())) {
                    summaryPath.put(UIConstant.SPC_CHART_NAME[0], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[0], nd));
                }
                if (exportParam.get(SpcExportItemKey.RUN_CHART.getCode())) {
                    summaryPath.put(UIConstant.SPC_CHART_NAME[1], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[1], run));
                }
                if (exportParam.get(SpcExportItemKey.X_BAR_CHART.getCode())) {
                    summaryPath.put(UIConstant.SPC_CHART_NAME[2], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[2], xbar));
                }
                if (exportParam.get(SpcExportItemKey.RANGE_CHART.getCode())) {
                    summaryPath.put(UIConstant.SPC_CHART_NAME[3], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[3], range));
                }
                if (exportParam.get(SpcExportItemKey.SD_CHART.getCode())) {
                    summaryPath.put(UIConstant.SPC_CHART_NAME[4], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[4], sd));
                }
                if (exportParam.get(SpcExportItemKey.MEDIAN_CHART.getCode())) {
                    summaryPath.put(UIConstant.SPC_CHART_NAME[5], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[5], med));
                }
                if (exportParam.get(SpcExportItemKey.BOX_CHART.getCode())) {
                    summaryPath.put(UIConstant.SPC_CHART_NAME[6], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[6], box));
                }
                if (exportParam.get(SpcExportItemKey.MR_CHART.getCode())) {
                    summaryPath.put(UIConstant.SPC_CHART_NAME[7], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[7], mr));
                }
                result.put(key + "SubSummary", summaryPath);
            }
            i++;
        }
        return result;
    }

    private static NDChart buildND() {//创建直方分布图
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickMarkVisible(false);//设置x轴数字标记线是否显示
        yAxis.setTickMarkVisible(false);//设置y轴数字标记线是否显示
        yAxis.setMinorTickVisible(false);//设置y轴两个数字之间的刻度线是否显示
        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);
        NDChart<Double, Double> ndChart = new NDChart(xAxis, yAxis);
        ndChart.setAnimated(false);
        ndChart.setLegendVisible(false);
        return ndChart;
    }

    private static ControlChart buildRunChart() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickMarkVisible(false);//设置x轴数字标记线是否显示
        yAxis.setTickMarkVisible(false);//设置y轴数字标记线是否显示
        xAxis.setMinorTickVisible(false);//设置x轴两个数字之间的刻度线是否显示
        yAxis.setMinorTickVisible(false);//设置y轴两个数字之间的刻度线是否显示
        yAxis.setAutoRanging(false);//设置y轴自动变化量程
        yAxis.setForceZeroInRange(false);
        ControlChart runChart = new ControlChart(xAxis, yAxis);
        runChart.setAnimated(false);
        runChart.setLegendVisible(false);
        return runChart;
    }

    private static ControlChart buildControlChart() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickMarkVisible(false);//设置x轴数字标记线是否显示
        yAxis.setTickMarkVisible(false);//设置y轴数字标记线是否显示
        xAxis.setMinorTickVisible(false);//设置x轴两个数字之间的刻度线是否显示
        yAxis.setMinorTickVisible(false);//设置y轴两个数字之间的刻度线是否显示
        xAxis.setAutoRanging(false);//设置x轴自动变化量程
        yAxis.setAutoRanging(false);//设置y轴自动变化量程
        ControlChart runChart = new ControlChart(xAxis, yAxis);//创建控制图
        runChart.setAnimated(false);//设置runChart是否有动画效果
        runChart.setLegendVisible(false);//设置图例是否可见
        return runChart;
    }

    private static BoxPlotChart buildBox() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickMarkVisible(false);//设置x轴数字标记线是否显示
        yAxis.setTickMarkVisible(false);//设置y轴数字标记线是否显示
        xAxis.setMinorTickVisible(false);//设置x轴两个数字之间的刻度线是否显示
        yAxis.setMinorTickVisible(false);//设置y轴两个数字之间的刻度线是否显示
        yAxis.setAutoRanging(false);//设置y轴自动变化量程
        BoxPlotChart boxPlotChart = new BoxPlotChart(xAxis, yAxis);//创建箱型图对象
        boxPlotChart.setAnimated(false);//设置runChart是否有动画效果
        boxPlotChart.setLegendVisible(false);//设置图例是否可见
        return boxPlotChart;

    }

    private static void setControlChartData(ControlChart chart, List<ControlChartData> controlChartData) {
        chart.setData(controlChartData, null);
        controlChartData.forEach(controlChartData1 -> {
            Double[] ucl = controlChartData1.getUclData();
            Double[] lcl = controlChartData1.getLclData();
            chart.setSeriesDataStyleByRule(controlChartData1.getUniqueKey(), ucl, lcl);
        });
    }

    private static String exportImages(String name, Node node) {
        Group vBox = new Group();
        Scene scene = new Scene(vBox);
        scene.getStylesheets().add(BuildChart.class.getClassLoader().getResource("css/charts.css").toExternalForm());
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

    /**
     * private class
     */
    private static class WriteImage {
        private WritableImage image;
    }

}
