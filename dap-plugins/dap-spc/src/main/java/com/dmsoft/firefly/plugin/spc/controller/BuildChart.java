package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.plugin.spc.charts.BoxPlotChart;
import com.dmsoft.firefly.plugin.spc.charts.ControlChart;
import com.dmsoft.firefly.plugin.spc.charts.LinearChart;
import com.dmsoft.firefly.plugin.spc.charts.NDChart;
import com.dmsoft.firefly.plugin.spc.charts.data.BoxPlotChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.ControlChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.*;
import com.dmsoft.firefly.plugin.spc.charts.utils.MathUtils;
import com.dmsoft.firefly.plugin.spc.dto.SpcChartDto;
import com.dmsoft.firefly.plugin.spc.dto.analysis.SpcChartResultDto;
import com.dmsoft.firefly.plugin.spc.dto.chart.*;
import com.dmsoft.firefly.plugin.spc.utils.FileUtils;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import com.dmsoft.firefly.sdk.utils.ColorUtils;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.NumberAxis;
import javafx.scene.image.WritableImage;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by GuangLi on 2018/3/14.
 */
public class BuildChart {

    private static Group vBox;
    private static Scene scene;

    public static Map<String, Map<String, String>> initSpcChartData(List<SpcChartDto> spcChartDtoList, int search, Map<String, Color> colorCache) {
        vBox = new Group();
        scene = new Scene(vBox);
        scene.getStylesheets().add(BuildChart.class.getClassLoader().getResource("css/charts.css").toExternalForm());

        List<INdcChartData> ndcChartDataList = Lists.newArrayList();
        List<ControlChartData> runChartDataList = Lists.newArrayList();
        List<ControlChartData> xBarChartDataList = Lists.newArrayList();
        List<ControlChartData> rangeChartDataList = Lists.newArrayList();
        List<ControlChartData> sdChartDataList = Lists.newArrayList();
        List<ControlChartData> medianChartDataList = Lists.newArrayList();
        List<BoxPlotChartData> boxChartDataList = Lists.newArrayList();
        List<ControlChartData> mrChartDataList = Lists.newArrayList();

        Map<String, Map<String, String>> result = Maps.newHashMap();

        NDChart nd = buildND();
        ControlChart run = buildControlChart();
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
            //nd chart
            INdcChartData iNdcChartData = new SpcNdChartData(key, spcChartResultDto.getNdcResult(), color);
            ndcChartDataList.add(iNdcChartData);
            //run chart
            SpcRunChartData1 iRunChartData = new SpcRunChartData1(key, spcChartResultDto.getRunCResult(), null, color);
            iRunChartData.setSeriesName(seriesName);
            runChartDataList.add(iRunChartData);
            //x bar chart
            SpcControlChartData1 xBarChartData = new SpcControlChartData1(key, spcChartResultDto.getXbarCResult(), color);
            xBarChartData.setSeriesName(seriesName);
            xBarChartDataList.add(xBarChartData);
            //range chart
            SpcControlChartData1 rangeChartData = new SpcControlChartData1(key, spcChartResultDto.getRangeCResult(), color);
            rangeChartData.setSeriesName(seriesName);
            rangeChartDataList.add(rangeChartData);
            //sd chart
            SpcControlChartData1 sdChartData = new SpcControlChartData1(key, spcChartResultDto.getSdCResult(), color);
            sdChartData.setSeriesName(seriesName);
            sdChartDataList.add(sdChartData);
            //median chart
            SpcControlChartData1 medianChartData = new SpcControlChartData1(key, spcChartResultDto.getMedianCResult(), color);
            medianChartData.setSeriesName(seriesName);
            medianChartDataList.add(medianChartData);
            //box chart
            SpcBoxChartData1 iBoxChartData = new SpcBoxChartData1(key, spcChartResultDto.getBoxCResult(), color);
            iBoxChartData.setSeriesName(seriesName);
            boxChartDataList.add(iBoxChartData);
            //mr chart
            SpcControlChartData1 mrChartData = new SpcControlChartData1(key, spcChartResultDto.getMrCResult(), color);
            mrChartData.setSeriesName(seriesName);
            mrChartDataList.add(mrChartData);

            BuildChart.setNdChartData(nd, iNdcChartData);
            BuildChart.setRunChartData(run, Lists.newArrayList(iRunChartData));
            BuildChart.setControlChartData(xbar, Lists.newArrayList(xBarChartData));
            BuildChart.setControlChartData(range, Lists.newArrayList(rangeChartData));
            BuildChart.setControlChartData(sd, Lists.newArrayList(sdChartData));
            BuildChart.setControlChartData(med, Lists.newArrayList(medianChartData));
            BuildChart.setBoxChartData(box, Lists.newArrayList(iBoxChartData));
            BuildChart.setControlChartData(mr, Lists.newArrayList(mrChartData));


            Map<String, String> chartPath = Maps.newHashMap();
            chartPath.put(UIConstant.SPC_CHART_NAME[0], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[0], nd));
            chartPath.put(UIConstant.SPC_CHART_NAME[1], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[1], run));
            chartPath.put(UIConstant.SPC_CHART_NAME[2], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[2], xbar));
            chartPath.put(UIConstant.SPC_CHART_NAME[3], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[3], range));
            chartPath.put(UIConstant.SPC_CHART_NAME[4], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[4], sd));
            chartPath.put(UIConstant.SPC_CHART_NAME[5], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[5], med));
            chartPath.put(UIConstant.SPC_CHART_NAME[6], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[6], box));
            chartPath.put(UIConstant.SPC_CHART_NAME[7], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[7], mr));
            result.put(key, chartPath);

            if (search > 1 && (i + 1) % search == 0) {
                BuildChart.setNdChartData(nd, ndcChartDataList);
                ndcChartDataList.clear();
                BuildChart.setRunChartData(run, runChartDataList);
                runChartDataList.clear();
                BuildChart.setControlChartData(xbar, xBarChartDataList);
                xBarChartDataList.clear();
                BuildChart.setControlChartData(range, rangeChartDataList);
                rangeChartDataList.clear();
                BuildChart.setControlChartData(sd, sdChartDataList);
                sdChartDataList.clear();
                BuildChart.setControlChartData(med, medianChartDataList);
                medianChartDataList.clear();
                BuildChart.setBoxChartData(box, boxChartDataList);
                boxChartDataList.clear();
                BuildChart.setControlChartData(mr, mrChartDataList);
                mrChartDataList.clear();

                Map<String, String> summaryPath = Maps.newHashMap();
                summaryPath.put(UIConstant.SPC_CHART_NAME[0], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[0], nd));
                summaryPath.put(UIConstant.SPC_CHART_NAME[1], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[1], run));
                summaryPath.put(UIConstant.SPC_CHART_NAME[2], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[2], xbar));
                summaryPath.put(UIConstant.SPC_CHART_NAME[3], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[3], range));
                summaryPath.put(UIConstant.SPC_CHART_NAME[4], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[4], sd));
                summaryPath.put(UIConstant.SPC_CHART_NAME[5], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[5], med));
                summaryPath.put(UIConstant.SPC_CHART_NAME[6], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[6], box));
                summaryPath.put(UIConstant.SPC_CHART_NAME[7], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[7], mr));
                result.put(key + "SubSummary", summaryPath);
            }
            i++;
        }
        return result;
    }

    private static NDChart buildND() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickMarkVisible(false);
        yAxis.setTickMarkVisible(false);
        yAxis.setMinorTickVisible(false);
        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);
        NDChart<Double, Double> ndChart = new NDChart(xAxis, yAxis);
        ndChart.setAnimated(false);
        ndChart.setLegendVisible(false);
        return ndChart;
    }

    private static ControlChart buildControlChart() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickMarkVisible(false);
        yAxis.setTickMarkVisible(false);
        xAxis.setMinorTickVisible(false);
        yAxis.setMinorTickVisible(false);
        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);

        ControlChart runChart = new ControlChart(xAxis, yAxis);
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
        yAxis.setAutoRanging(false);

        BoxPlotChart boxPlotChart = new BoxPlotChart(xAxis, yAxis);
        boxPlotChart.setAnimated(false);
        boxPlotChart.setLegendVisible(false);
        return boxPlotChart;

    }

    private static void setNdChartData(NDChart chart, INdcChartData chartData) {
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

    private static void setRunChartData(ControlChart chart, ControlChartData chartData) {
        chart.removeAllChildren();
        chart.setData(Lists.newArrayList(chartData), null);
    }

//    private static void setControlChartData(ControlChart chart, ControlChartData chartData) {
//        chart.removeAllChildren();
//        chart.setData(Lists.newArrayList(chartData), null);
//        Double[] ucl = chartData.getUclData();
//        Double[] lcl = chartData.getLclData();
//        chart.setSeriesDataStyleByRule(chartData.getUniqueKey(), ucl, lcl);
//    }

//    private static void setBoxPlotChartData(BoxPlotChart chart, SpcBoxChartData1 chartData) {
//        chart.removeAllChildren();
//        chart.setData(Lists.newArrayList(chartData), null);
//    }


    private static void setNdChartData(NDChart chart, List<INdcChartData> ndChartData) {
//        chart.removeAllChildren();

        Double[] xLower = new Double[ndChartData.size()];
        Double[] xUpper = new Double[ndChartData.size()];
        Double[] yLower = new Double[ndChartData.size()];
        Double[] yUpper = new Double[ndChartData.size()];
        for (int i = 0; i < ndChartData.size(); i++) {
            xLower[i] = (Double) ndChartData.get(i).getXLowerBound();
            xUpper[i] = (Double) ndChartData.get(i).getXUpperBound();
            yLower[i] = (Double) ndChartData.get(i).getYLowerBound();
            yUpper[i] = (Double) ndChartData.get(i).getYUpperBound();
        }
        double xMax = MathUtils.getMax(xUpper);
        double xMin = MathUtils.getMin(xLower);
        double yMax = MathUtils.getMax(yUpper);
        double yMin = MathUtils.getMin(yLower);
        NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        xAxis.setLowerBound(xMin);
        xAxis.setUpperBound(xMax);
        yAxis.setLowerBound(yMin);
        yAxis.setUpperBound(yMax);
        ndChartData.forEach(chartData -> {
            setNdChartData(chart, chartData);
        });
    }

    private static void setRunChartData(ControlChart chart, List<ControlChartData> runChartData) {
        Double[] xLower = new Double[runChartData.size()];
        Double[] xUpper = new Double[runChartData.size()];
        Double[] yLower = new Double[runChartData.size()];
        Double[] yUpper = new Double[runChartData.size()];
        for (int i = 0; i < runChartData.size(); i++) {
            xLower[i] = (Double) runChartData.get(i).getXLowerBound();
            xUpper[i] = (Double) runChartData.get(i).getXUpperBound();
            yLower[i] = (Double) runChartData.get(i).getYLowerBound();
            yUpper[i] = (Double) runChartData.get(i).getYUpperBound();
        }
        double xMax = MathUtils.getMax(xUpper);
        double xMin = MathUtils.getMin(xLower);
        double yMax = MathUtils.getMax(yUpper);
        double yMin = MathUtils.getMin(yLower);
        NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        double yReserve = (yMax - yMin) * UIConstant.FACTOR;
        double xReserve = (xMax - xMin) * UIConstant.FACTOR;
        xAxis.setLowerBound(xMin - xReserve);
        xAxis.setUpperBound(xMax + xReserve);
        yAxis.setLowerBound(yMin - yReserve);
        yAxis.setUpperBound(yMax + yReserve);

        chart.setData(runChartData, null);
    }

    private static void setControlChartData(ControlChart chart, List<ControlChartData> controlChartData) {
        Double[] xLower = new Double[controlChartData.size()];
        Double[] xUpper = new Double[controlChartData.size()];
        Double[] yLower = new Double[controlChartData.size()];
        Double[] yUpper = new Double[controlChartData.size()];
        for (int i = 0; i < controlChartData.size(); i++) {
            xLower[i] = (Double) controlChartData.get(i).getXLowerBound();
            xUpper[i] = (Double) controlChartData.get(i).getXUpperBound();
            yLower[i] = (Double) controlChartData.get(i).getYLowerBound();
            yUpper[i] = (Double) controlChartData.get(i).getYUpperBound();
        }
        double xMax = MathUtils.getMax(xUpper);
        double xMin = MathUtils.getMin(xLower);
        double yMax = MathUtils.getMax(yUpper);
        double yMin = MathUtils.getMin(yLower);
        NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        double yReserve = (yMax - yMin) * UIConstant.FACTOR;
        double xReserve = (xMax - xMin) * UIConstant.FACTOR;
        xAxis.setLowerBound(xMin - xReserve);
        xAxis.setUpperBound(xMax + xReserve);
        yAxis.setLowerBound(yMin - yReserve);
        yAxis.setUpperBound(yMax + yReserve);

        chart.setData(controlChartData, null);
        controlChartData.forEach(controlChartData1 -> {
            Double[] ucl = controlChartData1.getUclData();
            Double[] lcl = controlChartData1.getLclData();
            chart.setSeriesDataStyleByRule(controlChartData1.getUniqueKey(), ucl, lcl);
        });
    }

    private static void setBoxChartData(BoxPlotChart chart, List<BoxPlotChartData> boxChartData) {
        Double[] xLower = new Double[boxChartData.size()];
        Double[] xUpper = new Double[boxChartData.size()];
        Double[] yLower = new Double[boxChartData.size()];
        Double[] yUpper = new Double[boxChartData.size()];
        for (int i = 0; i < boxChartData.size(); i++) {
            xLower[i] = (Double) boxChartData.get(i).getXLowerBound();
            xUpper[i] = (Double) boxChartData.get(i).getXUpperBound();
            yLower[i] = (Double) boxChartData.get(i).getYLowerBound();
            yUpper[i] = (Double) boxChartData.get(i).getYUpperBound();
        }
        double xMax = MathUtils.getMax(xUpper);
        double xMin = MathUtils.getMin(xLower);
        double yMax = MathUtils.getMax(yUpper);
        double yMin = MathUtils.getMin(yLower);
        NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        double yReserve = (yMax - yMin) * UIConstant.FACTOR;
        double xReserve = (xMax - xMin) * UIConstant.FACTOR;
        xAxis.setLowerBound(xMin - xReserve);
        xAxis.setUpperBound(xMax + xReserve);
        yAxis.setLowerBound(yMin - yReserve);
        yAxis.setUpperBound(yMax + yReserve);

        chart.removeAllChildren();
        chart.setData(boxChartData, null);
    }

    private static String exportImages(String name, Node node) {
        vBox.getChildren().clear();
        vBox.getChildren().add(node);

//        SnapshotParameters parameters = new SnapshotParameters();
//        WritableImage image = node.snapshot(parameters, null);
//        // 重置图片大小
//        ImageView imageView = new ImageView(image);
//        imageView.setFitWidth(600);
//        imageView.setFitHeight(220);
        WritableImage exportImage = node.snapshot(new SnapshotParameters(), null);
        String savePicPath = FileUtils.getAbsolutePath("../export/temp");
        File file = new File(savePicPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        String path = savePicPath + "/" + name + new Date().getTime() + ".png";

        try {
            file = new File(path);
            saveImageUsingJPGWithQuality(SwingFXUtils.fromFXImage(exportImage, null), file, 0.9f);
//            AlertDialog.showAlertDialog("保存成功!");
        } catch (IOException ex) {
//            AlertDialog.showAlertDialog("保存失败:" + ex.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    private static void saveImageUsingJPGWithQuality(BufferedImage image,
                                                     File filePath, float quality) throws Exception {

        BufferedImage newBufferedImage = new BufferedImage(image.getWidth(),
                image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        newBufferedImage.getGraphics().drawImage(image, 0, 0, null);

        Iterator iter = ImageIO
                .getImageWritersByFormatName("jpeg");

        ImageWriter imageWriter = (ImageWriter) iter.next();
        ImageWriteParam iwp = imageWriter.getDefaultWriteParam();

        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        iwp.setCompressionQuality(quality);

        FileImageOutputStream fileImageOutput = new FileImageOutputStream(filePath);
        imageWriter.setOutput(fileImageOutput);
        IIOImage jpgimage = new IIOImage(newBufferedImage, null, null);
        imageWriter.write(null, jpgimage, iwp);
        imageWriter.dispose();
    }
}
