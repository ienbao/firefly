package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.plugin.spc.charts.BoxPlotChart;
import com.dmsoft.firefly.plugin.spc.charts.LinearChart;
import com.dmsoft.firefly.plugin.spc.charts.NDChart;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.*;
import com.dmsoft.firefly.plugin.spc.dto.SpcChartDto;
import com.dmsoft.firefly.plugin.spc.dto.analysis.SpcChartResultDto;
import com.dmsoft.firefly.plugin.spc.dto.chart.*;
import com.dmsoft.firefly.plugin.spc.utils.FileUtils;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import com.dmsoft.firefly.sdk.utils.ColorUtils;
import com.google.common.collect.Maps;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by GuangLi on 2018/3/14.
 */
public class BuildChart {

    private static Group vBox;
    private static Scene scene;
    public static Map<String, Map<String, String>> initSpcChartData(List<SpcChartDto> spcChartDtoList, Map<String, Color> colorCache) {
        vBox = new Group();
        scene = new Scene(vBox);
        scene.getStylesheets().add(BuildChart.class.getClassLoader().getResource("css/charts.css").toExternalForm());

        Map<String, Map<String, String>> result = Maps.newHashMap();
//        List<INdcChartData> ndcChartDataList = Lists.newArrayList();
//        List<IRunChartData> runChartDataList = Lists.newArrayList();
//        List<IControlChartData> xBarChartDataList = Lists.newArrayList();
//        List<IControlChartData> rangeChartDataList = Lists.newArrayList();
//        List<IControlChartData> sdChartDataList = Lists.newArrayList();
//        List<IControlChartData> medianChartDataList = Lists.newArrayList();
//        List<IBoxChartData> boxChartDataList = Lists.newArrayList();
//        List<IControlChartData> mrChartDataList = Lists.newArrayList();

        NDChart nd = buildND();
        LinearChart run = buildRunOrXbar();
        LinearChart xbar = buildRunOrXbar();
        LinearChart range = new LinearChart(new NumberAxis(), new NumberAxis());
        LinearChart sd = new LinearChart(new NumberAxis(), new NumberAxis());
        LinearChart med = new LinearChart(new NumberAxis(), new NumberAxis());
        LinearChart mr = new LinearChart(new NumberAxis(), new NumberAxis());
        BoxPlotChart box = buildBox();

        for (SpcChartDto spcChartDto : spcChartDtoList) {
            String key = spcChartDto.getKey();

            javafx.scene.paint.Color color = ColorUtils.toFxColorFromAwtColor(colorCache.get(key));
            SpcChartResultDto spcChartResultDto = spcChartDto.getResultDto();
            if (spcChartResultDto == null) {
                continue;
            }
            //nd chart
            INdcChartData iNdcChartData = new SpcNdChartData(key, spcChartResultDto.getNdcResult(), color);
//            ndcChartDataList.add(iNdcChartData);
            //run chart
            IRunChartData iRunChartData = new SpcRunChartData(key, spcChartResultDto.getRunCResult(), color);
//            runChartDataList.add(iRunChartData);
            //x bar chart
            IControlChartData xBarChartData = new SpcControlChartData(key, spcChartResultDto.getXbarCResult(), color);
//            xBarChartDataList.add(xBarChartData);
            //range chart
            IControlChartData rangeChartData = new SpcControlChartData(key, spcChartResultDto.getRangeCResult(), color);
//            rangeChartDataList.add(rangeChartData);
            //sd chart
            IControlChartData sdChartData = new SpcControlChartData(key, spcChartResultDto.getSdCResult(), color);
//            sdChartDataList.add(sdChartData);
            //median chart
            IControlChartData medianChartData = new SpcControlChartData(key, spcChartResultDto.getMedianCResult(), color);
//            medianChartDataList.add(medianChartData);
            //box chart
            IBoxChartData iBoxChartData = new SpcBoxChartData(key, spcChartResultDto.getBoxCResult(), color);
//            boxChartDataList.add(iBoxChartData);
            //mr chart
            IControlChartData mrChartData = new SpcControlChartData(key, spcChartResultDto.getMrCResult(), color);
//            mrChartDataList.add(mrChartData);
            BuildChart.setNdChartData(nd, iNdcChartData);
            BuildChart.setRunChartData(run, iRunChartData);
            BuildChart.setControlChartData(xbar, xBarChartData);
            BuildChart.setControlChartData(range, rangeChartData);
            BuildChart.setControlChartData(sd, sdChartData);
            BuildChart.setControlChartData(med, medianChartData);
            BuildChart.setBoxPlotChartData(box, iBoxChartData);
            BuildChart.setControlChartData(mr, mrChartData);
            Map<String, Node> charts = Maps.newHashMap();
            charts.put(UIConstant.SPC_CHART_NAME[0], nd);
            charts.put(UIConstant.SPC_CHART_NAME[1], run);
            charts.put(UIConstant.SPC_CHART_NAME[2], xbar);
            charts.put(UIConstant.SPC_CHART_NAME[3], range);
            charts.put(UIConstant.SPC_CHART_NAME[4], sd);
            charts.put(UIConstant.SPC_CHART_NAME[5], med);
            charts.put(UIConstant.SPC_CHART_NAME[6], box);
            charts.put(UIConstant.SPC_CHART_NAME[7], mr);

            Map<String, String> chartPath = Maps.newHashMap();
            chartPath.put(key, BuildChart.exportImages(UIConstant.SPC_CHART_NAME[0], nd));
            chartPath.put(key, BuildChart.exportImages(UIConstant.SPC_CHART_NAME[1], run));
            chartPath.put(key, BuildChart.exportImages(UIConstant.SPC_CHART_NAME[2], xbar));
            chartPath.put(key, BuildChart.exportImages(UIConstant.SPC_CHART_NAME[3], range));
            chartPath.put(key, BuildChart.exportImages(UIConstant.SPC_CHART_NAME[4], sd));
            chartPath.put(key, BuildChart.exportImages(UIConstant.SPC_CHART_NAME[5], med));
            chartPath.put(key, BuildChart.exportImages(UIConstant.SPC_CHART_NAME[6], box));
            chartPath.put(key, BuildChart.exportImages(UIConstant.SPC_CHART_NAME[7], mr));

            result.put(key, chartPath);
        }
        return result;
    }

    private static NDChart buildND() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickMarkVisible(false);
        yAxis.setTickMarkVisible(false);
        yAxis.setMinorTickVisible(false);
        NDChart<Double, Double> ndChart = new NDChart(xAxis, yAxis);
        ndChart.setAnimated(false);
        ndChart.setLegendVisible(false);
        return ndChart;
    }

    private static LinearChart buildRunOrXbar() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickMarkVisible(false);
        yAxis.setTickMarkVisible(false);
        xAxis.setMinorTickVisible(false);
        yAxis.setMinorTickVisible(false);
        LinearChart runChart = new LinearChart(xAxis, yAxis);
        runChart.setAnimated(false);
        runChart.setLegendVisible(false);
        return runChart;
    }

    private static BoxPlotChart buildBox() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickMarkVisible(false);
        yAxis.setTickMarkVisible(false);
        xAxis.setMinorTickVisible(false);
        yAxis.setMinorTickVisible(false);
        BoxPlotChart boxPlotChart = new BoxPlotChart(xAxis, yAxis);
        boxPlotChart.setAnimated(false);
        boxPlotChart.setLegendVisible(false);
        return boxPlotChart;

    }

    private static void setNdChartData(NDChart chart, INdcChartData chartData) {
//        NDChart chart = ndChartPane.getChart();
//        chart.removeAllChildren();
        IBarChartData barChartData = chartData.getBarData();
        IXYChartData curveData = chartData.getCurveData();
        List<ILineData> lineData = chartData.getLineData();
//          add area data
        chart.addAreaSeries(curveData, "ALL", chartData.getColor());
//          add bar chart data
        chart.createChartSeries(barChartData, "ALL", chartData.getColor());
//                add line data
        if (lineData != null) {
            chart.addValueMarker(lineData, "ALL");
        }
    }

    private static void setRunChartData(LinearChart chart, IRunChartData chartData) {
        chart.removeAllChildren();

        IXYChartData xyChartData = chartData.getXYChartData();
        List<ILineData> lineData = chartData.getLineData();
        chart.createChartSeries(xyChartData, "ALL", null);
        if (lineData != null) {
            chart.addValueMarker(lineData, "ALL");
        }
    }

    private static void setControlChartData(LinearChart chart, IControlChartData chartData) {
        chart.removeAllChildren();

        IXYChartData xyChartData = chartData.getChartData();
        List<ILineData> lineData = chartData.getLineData();
        List<IPathData> pathData = chartData.getBrokenLineData();
//            add chart data
        chart.createChartSeries(xyChartData, "ALL", null);
        if (lineData != null) {
            chart.addValueMarker(lineData, "ALL");
        }
        if (pathData != null) {
            chart.addPathMarker(pathData, "ALL");
        }
    }

    private static void setBoxPlotChartData(BoxPlotChart chart, IBoxChartData chartData) {
        chart.removeAllChildren();

        IBoxAndWhiskerData boxAndWhiskerData = chartData.getBoxAndWhiskerData();
        IPoint points = chartData.getPoints();
        chart.createChartSeries(boxAndWhiskerData, "ALL");
        chart.addPoints(points, javafx.scene.paint.Color.GREEN);
    }

    public static String exportImages(String name, Node node) {
//        node.getStyleClass().add(BuildChart.class.getClassLoader().getResource("css/charts.css").toExternalForm());
        vBox.getChildren().clear();
        vBox.getChildren().add(node);
        SnapshotParameters parameters = new SnapshotParameters();
        WritableImage image = node.snapshot(parameters, null);
//        // 重置图片大小
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(600);
        imageView.setFitHeight(220);
        WritableImage exportImage = node.snapshot(parameters, null);
        String savePicPath = FileUtils.getAbsolutePath("../export/temp");
        File file = new File(savePicPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        String path = savePicPath + "/" + name + new Date().getTime() + ".png";

        try {
            file = new File(path);
            ImageIO.write(SwingFXUtils.fromFXImage(exportImage, null), "png", file);
//            AlertDialog.showAlertDialog("保存成功!");
        } catch (IOException ex) {
//            AlertDialog.showAlertDialog("保存失败:" + ex.getMessage());
        }
        return path;
    }
}
