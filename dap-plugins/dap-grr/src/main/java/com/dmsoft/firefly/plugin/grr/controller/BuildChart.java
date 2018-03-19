package com.dmsoft.firefly.plugin.grr.controller;

import com.dmsoft.firefly.plugin.grr.charts.LinearChart;
import com.dmsoft.firefly.plugin.grr.charts.data.ILineData;
import com.dmsoft.firefly.plugin.grr.charts.data.RuleLineData;
import com.dmsoft.firefly.plugin.grr.charts.data.VerticalCutLine;
import com.dmsoft.firefly.plugin.grr.dto.GrrImageDto;
import com.dmsoft.firefly.plugin.grr.dto.analysis.*;
import com.dmsoft.firefly.plugin.grr.utils.DigNumInstance;
import com.dmsoft.firefly.plugin.grr.utils.FileUtils;
import com.dmsoft.firefly.plugin.grr.utils.MathUtils;
import com.dmsoft.firefly.plugin.grr.utils.UIConstant;
import com.dmsoft.firefly.plugin.grr.utils.charts.ChartUtils;
import com.dmsoft.firefly.plugin.grr.utils.charts.LegendUtils;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.javafx.charts.Legend;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * Created by GuangLi on 2018/3/19.
 */
public class BuildChart {
    private static Group vBox;
    private static Scene scene;

    public static GrrImageDto buildImage(GrrDetailResultDto grrDetailResultDto, List<String> parts, List<String> appraisers) {
        vBox = new Group();
        scene = new Scene(vBox);
        scene.getStylesheets().add(BuildChart.class.getClassLoader().getResource("css/grr_chart.css").toExternalForm());
        GrrImageDto images = new GrrImageDto();
        LineChart partAppraiserChart = buildScatterChart();
        setPartAppraiserChart(partAppraiserChart, grrDetailResultDto.getPartAppraiserChartDto(), parts, appraisers);
        images.setGrrAPlotImagePath(exportImages("partAppraiserChart", partAppraiserChart));

        LinearChart xBarAppraiserChart = buildControlChart(parts);
        setControlChartData(grrDetailResultDto.getXbarAppraiserChartDto(), xBarAppraiserChart, parts, appraisers);
        images.setGrrXBarImagePath(exportImages("xBarAppraiserChart", xBarAppraiserChart));

        LinearChart rangeAppraiserChart = buildControlChart(parts);
        setControlChartData(grrDetailResultDto.getRangeAppraiserChartDto(), rangeAppraiserChart, parts, appraisers);
        images.setGrrRChartImagePath(exportImages("rangeAppraiserChart", rangeAppraiserChart));

        LineChart rrByAppraiserChart = buildScatterChart();
        setScatterChartData(grrDetailResultDto.getRrbyAppraiserChartDto(), rrByAppraiserChart);
        images.setGrrRPlotChartAppImagePath(exportImages("rrByAppraiserChart", rrByAppraiserChart));

        LineChart rrbyPartChart = buildScatterChart();
        setScatterChartData(grrDetailResultDto.getRrbyPartChartDto(), rrbyPartChart);
        images.setGrrRPlotChartPartImagePath(exportImages("rrbyPartChart", rrbyPartChart));

        BarChart componentChart = new BarChart(new NumberAxis(), new NumberAxis());
        setComponentChart(grrDetailResultDto.getComponentChartDto(), componentChart);
        images.setGrrComponentsImagePath(exportImages("componentChart", componentChart));

        return images;
    }

    private static LinearChart buildControlChart(List<String> parts) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return (Double) object % ((+parts.size()) / 2) == 0 ? String.valueOf(object) : null;
            }

            @Override
            public Number fromString(String string) {
                return null;
            }
        });
        xAxis.setMinorTickVisible(false);
        xAxis.setTickMarkVisible(false);
        yAxis.setMinorTickVisible(false);
        yAxis.setTickMarkVisible(false);
        yAxis.setAutoRanging(false);
        LinearChart chart = new LinearChart(xAxis, yAxis);
        return chart;
    }

    private static LineChart buildScatterChart() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setMinorTickVisible(false);
        xAxis.setMinorTickVisible(false);
        xAxis.setTickMarkVisible(false);
        yAxis.setMinorTickVisible(false);
        yAxis.setTickMarkVisible(false);
        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return (Double) object % 1.0 == 0 ? String.valueOf(object) : null;
            }

            @Override
            public Number fromString(String string) {
                return null;
            }
        });
        LineChart chart = new LineChart(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setVerticalGridLinesVisible(false);
        chart.setHorizontalGridLinesVisible(false);
        return chart;
    }

    private static void setComponentChart(GrrComponentCResultDto componentCResult, BarChart componentChart) {
        XYChart.Series series1 = new XYChart.Series();
        XYChart.Series series2 = new XYChart.Series();
        XYChart.Series series3 = new XYChart.Series();
        series1.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[0],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getGrrContri()) ? 0 : componentCResult.getGrrContri()));
        series1.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[1],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getRepeatContri()) ? 0 : componentCResult.getRepeatContri()));
        series1.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[2],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getReprodContri()) ? 0 : componentCResult.getReprodContri()));
        series1.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[3],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getPartContri()) ? 0 : componentCResult.getPartContri()));
        series2.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[0],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getGrrVar()) ? 0 : componentCResult.getGrrVar()));
        series2.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[1],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getRepeatVar()) ? 0 : componentCResult.getRepeatVar()));
        series2.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[2],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getReprodVar()) ? 0 : componentCResult.getReprodVar()));
        series2.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[3],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getPartVar()) ? 0 : componentCResult.getPartVar()));
        series3.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[0],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getGrrTol()) ? 0 : componentCResult.getGrrTol()));
        series3.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[1],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getRepeatVar()) ? 0 : componentCResult.getRepeatVar()));
        series3.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[2],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getReprodVar()) ? 0 : componentCResult.getReprodVar()));
        series3.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[3],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getPartVar()) ? 0 : componentCResult.getPartVar()));
        componentChart.getData().addAll(series1, series2, series3);
        String[] colors = new String[UIConstant.CHART_COMPONENT_CATEGORY.length + 2];
        for (int i = 0; i < UIConstant.CHART_COMPONENT_CATEGORY.length; i++) {
            XYChart.Series series = (XYChart.Series) componentChart.getData().get(i);
            series.setName(UIConstant.CHART_COMPONENT_CATEGORY[i]);
            colors[i] = "default-color" + i;
        }
        colors[UIConstant.CHART_COMPONENT_CATEGORY.length] = "bar-legend-symbol";
        colors[UIConstant.CHART_COMPONENT_CATEGORY.length + 1] = "chart-bar";
        Legend legend = LegendUtils.buildLegend(componentChart.getData(), colors);
//        componentBp.setLeft(legend);
        int digNum = DigNumInstance.newInstance().getDigNum() - 2;
//        componentBp.setMargin(legend, new Insets(0, 0, 1, 0));

        //Chart text format
        ChartUtils.setChartText(componentChart.getData(), s -> {
            if (DAPStringUtils.isNumeric(s)) {
                Double value = Double.valueOf(s);
                if (!DAPStringUtils.isInfinityAndNaN(value)) {
                    return DAPStringUtils.formatDouble(value, digNum) + "%";
                }
            }
            return s + "%";
        });
    }

    private static void setPartAppraiserChart(LineChart partAppraiserChart, GrrPACResultDto partAppraiserChartDto,
                                              List<String> parts,
                                              List<String> appraisers) {
        double[][] data = partAppraiserChartDto.getDatas();
        double max = MathUtils.getMax(data);
        double min = MathUtils.getMin(data);
        NumberAxis yAxis = (NumberAxis) partAppraiserChart.getYAxis();
        final double factor = 0.20;
        double reserve = (max - min) * factor;
        yAxis.setAutoRanging(false);
        yAxis.setUpperBound(max + reserve);
        yAxis.setLowerBound(min - reserve);
        ObservableList<XYChart.Series> seriesData = FXCollections.observableArrayList();
        for (int i = 0; i < data.length; i++) {
            XYChart.Series series = new XYChart.Series();
            series.setName(appraisers.get(i));
            double[] appraiser = data[i];
            for (int j = 0; j < appraiser.length; j++) {
                if (!DAPStringUtils.isInfinityAndNaN(appraiser[j])) {
                    series.getData().add(new XYChart.Data<>(parts.get(j), appraiser[j], appraisers.get(i)));
                }
            }
            seriesData.add(series);
        }
        partAppraiserChart.getData().addAll(seriesData);

        Legend legend = LegendUtils.buildLegend(partAppraiserChart.getData(),
                "chart-line-symbol", "line-legend-symbol");
        ChartUtils.setChartToolTip(partAppraiserChart.getData(), pointTooltip -> {
            Double value = (Double) pointTooltip.getData().getYValue();
            int digNum = DigNumInstance.newInstance().getDigNum();
            return pointTooltip == null ? "" :
                    "(" + pointTooltip.getData().getExtraValue() + "," +
                            pointTooltip.getData().getXValue() + ")" + "=" + DAPStringUtils.formatDouble(value, digNum);
        });
//        partAppraiserBp.setLeft(legend);
//        partAppraiserBp.setMargin(legend, new Insets(0, 0, 1, 0));
    }

    private static void setControlChartData(GrrControlChartDto chartData,
                                            LinearChart chart,
                                            List<String> parts,
                                            List<String> appraisers) {

        int partCount = parts.size();
        Double[] x = chartData.getX();
        Double[] y = chartData.getY();
        Double[] ruleData = new Double[]{chartData.getUcl(), chartData.getCl(), chartData.getLcl()};
        double max = MathUtils.getMax(y, ruleData);
        double min = MathUtils.getMin(y, ruleData);
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        final double factor = 0.20;
        double reserve = (max - min) * factor;
        yAxis.setAutoRanging(false);
        yAxis.setUpperBound(max + reserve);
        yAxis.setLowerBound(min - reserve);
        List<ILineData> horizonalLineData = Lists.newArrayList();
        List<ILineData> verticalLineData = Lists.newArrayList();
        XYChart.Series series = new XYChart.Series();
//        draw vertical line
        for (int i = 0; i < x.length; i++) {
            if ((i + 1) % partCount == 0 && i != x.length - 1) {
                double value = (x[i] + x[i + 1]) / 2;
                verticalLineData.add(new VerticalCutLine(value));
            }
            series.getData().add(new XYChart.Data<>(x[i], y[i], parts.get(i % partCount)));
        }

        RuleLineData uclLineData = new RuleLineData(UIConstant.CHART_OPERATE_NAME[0], chartData.getUcl());
        RuleLineData clLineData = new RuleLineData(UIConstant.CHART_OPERATE_NAME[1], chartData.getCl());
        RuleLineData lclLineData = new RuleLineData(UIConstant.CHART_OPERATE_NAME[2], chartData.getLcl());
        uclLineData.setColor(Color.rgb(102, 102, 102));
        lclLineData.setColor(Color.rgb(178, 178, 178));
        uclLineData.setLineClass("dashed2-line");
        clLineData.setLineClass("solid-line");
        lclLineData.setLineClass("dashed1-line");
        horizonalLineData.add(uclLineData);
        horizonalLineData.add(clLineData);
        horizonalLineData.add(lclLineData);
        int digNum = DigNumInstance.newInstance().getDigNum();
        chart.getData().add(series);
//        button.setDisable(false);

        chart.buildValueMarkerWithoutTooltip(verticalLineData);
        chart.buildValueMarkerWithTooltip(horizonalLineData, new Function<ILineData, String>() {
            @Override
            public String apply(ILineData oneLineData) {
                return oneLineData.getTitle() + "\n" + oneLineData.getName() + "="
                        + DAPStringUtils.formatDouble(oneLineData.getValue(), digNum);
            }
        });
        ChartUtils.setChartToolTip(chart.getData(), pointTooltip -> {
            Double value = (Double) pointTooltip.getData().getYValue();
            return pointTooltip == null ? "" :
                    "(" + pointTooltip.getData().getExtraValue() + "," +
                            pointTooltip.getData().getXValue() + ")" + "=" + DAPStringUtils.formatDouble(value, digNum);
        });

        Legend legend = LegendUtils.buildLegend(chart.getData(),
                "chart-line-symbol", "line-legend-symbol");
//        borderPane.setLeft(legend);
//        borderPane.setMargin(legend, new Insets(0, 0, 1, 0));
    }

    private static void setScatterChartData(GrrScatterChartDto scatterChartData, LineChart chart) {

        Double[] x = scatterChartData.getX();
        Double[] y = scatterChartData.getY();
        Double[] clX = scatterChartData.getClX();
        Double[] clY = scatterChartData.getClY();
        double max = MathUtils.getMax(y, clY);
        double min = MathUtils.getMin(y, clY);
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        final double factor = 0.20;
        double reserve = (max - min) * factor;
        yAxis.setAutoRanging(false);
        yAxis.setUpperBound(max + reserve);
        yAxis.setLowerBound(min - reserve);
        XYChart.Series scatterSeries = new XYChart.Series();
        XYChart.Series lineSeries = new XYChart.Series();
        for (int i = 0; i < x.length; i++) {
            scatterSeries.getData().add(new XYChart.Data<>(x[i], y[i]));
        }
        for (int i = 0; i < clX.length; i++) {
            lineSeries.getData().add(new XYChart.Data<>(clX[i], clY[i]));
        }
        chart.getData().addAll(scatterSeries, lineSeries);
        ChartUtils.setChartToolTip(chart.getData(), pointTooltip -> {
            Double value = (Double) pointTooltip.getData().getYValue();
            int digNum = DigNumInstance.newInstance().getDigNum();
            return pointTooltip == null ? "" :
                    "(" + DAPStringUtils.formatDouble(value, digNum) + ")";
        });
        scatterSeries.getNode().getStyleClass().add("chart-series-hidden-line");
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
