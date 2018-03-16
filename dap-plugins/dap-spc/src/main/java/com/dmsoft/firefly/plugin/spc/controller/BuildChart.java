package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.plugin.spc.charts.BoxPlotChart;
import com.dmsoft.firefly.plugin.spc.charts.LinearChart;
import com.dmsoft.firefly.plugin.spc.charts.NDChart;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.*;
import com.dmsoft.firefly.plugin.spc.charts.utils.MathUtils;
import com.dmsoft.firefly.plugin.spc.dto.SpcChartDto;
import com.dmsoft.firefly.plugin.spc.dto.analysis.SpcChartResultDto;
import com.dmsoft.firefly.plugin.spc.dto.chart.*;
import com.dmsoft.firefly.plugin.spc.utils.FileUtils;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import com.dmsoft.firefly.sdk.utils.ColorUtils;
import com.google.common.collect.Lists;
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
        List<IRunChartData> runChartDataList = Lists.newArrayList();
        List<IControlChartData> xBarChartDataList = Lists.newArrayList();
        List<IControlChartData> rangeChartDataList = Lists.newArrayList();
        List<IControlChartData> sdChartDataList = Lists.newArrayList();
        List<IControlChartData> medianChartDataList = Lists.newArrayList();
        List<IBoxChartData> boxChartDataList = Lists.newArrayList();
        List<IControlChartData> mrChartDataList = Lists.newArrayList();

        Map<String, Map<String, String>> result = Maps.newHashMap();

        NDChart nd = buildND();
        LinearChart run = buildRunOrXbar();
        LinearChart xbar = buildRunOrXbar();
        LinearChart range = new LinearChart(new NumberAxis(), new NumberAxis());
        range.setAnimated(false);
        range.setLegendVisible(false);
        LinearChart sd = new LinearChart(new NumberAxis(), new NumberAxis());
        sd.setAnimated(false);
        sd.setLegendVisible(false);
        LinearChart med = new LinearChart(new NumberAxis(), new NumberAxis());
        med.setAnimated(false);
        med.setLegendVisible(false);
        LinearChart mr = new LinearChart(new NumberAxis(), new NumberAxis());
        mr.setAnimated(false);
        mr.setLegendVisible(false);
        BoxPlotChart box = buildBox();

        int i = 0;
        for (SpcChartDto spcChartDto : spcChartDtoList) {
            String key = spcChartDto.getKey();

            javafx.scene.paint.Color color = ColorUtils.toFxColorFromAwtColor(colorCache.get(key));
            SpcChartResultDto spcChartResultDto = spcChartDto.getResultDto();
            if (spcChartResultDto == null) {
                continue;
            }
            //nd chart
            INdcChartData iNdcChartData = new SpcNdChartData(key, spcChartResultDto.getNdcResult(), color);
            ndcChartDataList.add(iNdcChartData);
            //run chart
            IRunChartData iRunChartData = new SpcRunChartData(key, spcChartResultDto.getRunCResult(), color);
            runChartDataList.add(iRunChartData);
            //x bar chart
            IControlChartData xBarChartData = new SpcControlChartData(key, spcChartResultDto.getXbarCResult(), color);
            xBarChartDataList.add(xBarChartData);
            //range chart
            IControlChartData rangeChartData = new SpcControlChartData(key, spcChartResultDto.getRangeCResult(), color);
            rangeChartDataList.add(rangeChartData);
            //sd chart
            IControlChartData sdChartData = new SpcControlChartData(key, spcChartResultDto.getSdCResult(), color);
            sdChartDataList.add(sdChartData);
            //median chart
            IControlChartData medianChartData = new SpcControlChartData(key, spcChartResultDto.getMedianCResult(), color);
            medianChartDataList.add(medianChartData);
            //box chart
            IBoxChartData iBoxChartData = new SpcBoxChartData(key, spcChartResultDto.getBoxCResult(), color);
            boxChartDataList.add(iBoxChartData);
            //mr chart
            IControlChartData mrChartData = new SpcControlChartData(key, spcChartResultDto.getMrCResult(), color);
            mrChartDataList.add(mrChartData);

            BuildChart.setNdChartData(nd, iNdcChartData);
            BuildChart.setRunChartData(run, iRunChartData);
            BuildChart.setControlChartData(xbar, xBarChartData);
            BuildChart.setControlChartData(range, rangeChartData);
            BuildChart.setControlChartData(sd, sdChartData);
            BuildChart.setControlChartData(med, medianChartData);
            BuildChart.setBoxPlotChartData(box, iBoxChartData);
            BuildChart.setControlChartData(mr, mrChartData);


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
                BuildChart.setRunChartData(run, runChartDataList);
                BuildChart.setControlChartData(xbar, xBarChartDataList);
                BuildChart.setControlChartData(range, rangeChartDataList);
                BuildChart.setControlChartData(sd, sdChartDataList);
                BuildChart.setControlChartData(med, medianChartDataList);
                BuildChart.setBoxChartData(box, boxChartDataList);
                BuildChart.setControlChartData(mr, mrChartDataList);

                Map<String, String> summaryPath = Maps.newHashMap();
                summaryPath.put(UIConstant.SPC_CHART_NAME[0], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[0], nd));
                summaryPath.put(UIConstant.SPC_CHART_NAME[1], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[1], run));
                summaryPath.put(UIConstant.SPC_CHART_NAME[2], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[2], xbar));
                summaryPath.put(UIConstant.SPC_CHART_NAME[3], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[3], range));
                summaryPath.put(UIConstant.SPC_CHART_NAME[4], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[4], sd));
                summaryPath.put(UIConstant.SPC_CHART_NAME[5], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[5], med));
                summaryPath.put(UIConstant.SPC_CHART_NAME[6], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[6], box));
                summaryPath.put(UIConstant.SPC_CHART_NAME[7], BuildChart.exportImages(UIConstant.SPC_CHART_NAME[7], mr));
                result.put(key + "SubSummary", chartPath);
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

    private static LinearChart buildRunOrXbar() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickMarkVisible(false);
        yAxis.setTickMarkVisible(false);
        xAxis.setMinorTickVisible(false);
        yAxis.setMinorTickVisible(false);
        yAxis.setAutoRanging(false);

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


    public static void setNdChartData(NDChart chart, List<INdcChartData> ndChartData) {
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

    public static void setRunChartData(LinearChart chart, List<IRunChartData> runChartData) {
        chart.removeAllChildren();

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
        xAxis.setLowerBound(xMin);
        xAxis.setUpperBound(xMax);
        yAxis.setLowerBound(yMin);
        yAxis.setUpperBound(yMax);
        runChartData.forEach(chartData -> {
            setRunChartData(chart, chartData);
        });
    }

    public static void setControlChartData(LinearChart linearChart, List<IControlChartData> controlChartData) {
        linearChart.removeAllChildren();

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
        NumberAxis xAxis = (NumberAxis) linearChart.getXAxis();
        NumberAxis yAxis = (NumberAxis) linearChart.getYAxis();
        xAxis.setLowerBound(xMin);
        xAxis.setUpperBound(xMax);
        yAxis.setLowerBound(yMin);
        yAxis.setUpperBound(yMax);
        controlChartData.forEach(chartData -> {
            setControlChartData(linearChart, chartData);
        });
    }

    public static void setBoxChartData(BoxPlotChart chart, List<IBoxChartData> boxChartData) {
        chart.removeAllChildren();

//        Double[] xLower = new Double[boxChartData.size()];
//        Double[] xUpper = new Double[boxChartData.size()];
        Double[] yLower = new Double[boxChartData.size()];
        Double[] yUpper = new Double[boxChartData.size()];
        for (int i = 0; i < boxChartData.size(); i++) {
//            xLower[i] = (Double) boxChartData.get(i).getXLowerBound();
//            xUpper[i] = (Double) boxChartData.get(i).getXUpperBound();
            yLower[i] = (Double) boxChartData.get(i).getYLowerBound();
            yUpper[i] = (Double) boxChartData.get(i).getYUpperBound();
        }
//        double xMax = MathUtils.getMax(xUpper);
//        double xMin = MathUtils.getMin(xLower);
        double yMax = MathUtils.getMax(yUpper);
        double yMin = MathUtils.getMin(yLower);
        NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
//        xAxis.setLowerBound(xMin);
//        xAxis.setUpperBound(xMax);
        yAxis.setLowerBound(yMin);
        yAxis.setUpperBound(yMax);
        boxChartData.forEach(chartData -> {
            setBoxPlotChartData(chart, chartData);
        });
    }

    public static String exportImages(String name, Node node) {
        vBox.getChildren().clear();
        vBox.getChildren().add(node);

//        SnapshotParameters parameters = new SnapshotParameters();
//        WritableImage image = node.snapshot(parameters, null);
//        // 重置图片大小
//        ImageView imageView = new ImageView(image);
//        imageView.setFitWidth(600);
//        imageView.setFitHeight(220);
        WritableImage exportImage = scene.snapshot(null);
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

    public static void saveImageUsingJPGWithQuality(BufferedImage image,
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
