package com.dmsoft.firefly.plugin.grr.controller;

import com.dmsoft.firefly.gui.components.chart.ChartOperatorUtils;
import com.dmsoft.firefly.plugin.grr.charts.LinearChart;
import com.dmsoft.firefly.plugin.grr.charts.data.ILineData;
import com.dmsoft.firefly.plugin.grr.charts.data.RuleLineData;
import com.dmsoft.firefly.plugin.grr.charts.data.VerticalCutLine;
import com.dmsoft.firefly.plugin.grr.dto.GrrImageDto;
import com.dmsoft.firefly.plugin.grr.dto.analysis.*;
import com.dmsoft.firefly.plugin.grr.utils.FileUtils;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.dmsoft.firefly.plugin.grr.utils.MathUtils;
import com.dmsoft.firefly.plugin.grr.utils.UIConstant;
import com.dmsoft.firefly.plugin.grr.utils.charts.ChartUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
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
import javafx.scene.chart.*;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by GuangLi on 2018/3/19.
 */
public class BuildChart {
    private static final Float F9 = 0.9f;
    private static Group vBox;
    private static Scene scene;
    private static int digNum = 6;

    /**
     * method to build image
     *
     * @param grrDetailResultDto grr detail result dto
     * @param parts              parts
     * @param appraisers         appraisers
     * @return grr image dto
     */
    public static GrrImageDto buildImage(GrrDetailResultDto grrDetailResultDto,
                                         List<String> parts,
                                         List<String> appraisers,
                                         Map<String, Boolean> exportParam) {
        digNum = RuntimeContext.getBean(EnvService.class).findActivatedTemplate().getDecimalDigit();
        vBox = new Group();
        scene = new Scene(vBox);
        scene.getStylesheets().add(BuildChart.class.getClassLoader().getResource("css/grr_chart.css").toExternalForm());
        GrrImageDto images = new GrrImageDto();
//        LineChart partAppraiserChart = buildScatterChart();
        boolean partAppraiserValid = exportParam.containsKey(GrrFxmlAndLanguageUtils.getString(UIConstant.CHART_5));
        partAppraiserValid = partAppraiserValid && exportParam.get(GrrFxmlAndLanguageUtils.getString(UIConstant.CHART_5));
        partAppraiserValid = partAppraiserValid && grrDetailResultDto.getPartAppraiserChartDto() != null;
        if (partAppraiserValid) {
            LineChart partAppraiserChart = new LineChart(new CategoryAxis(), new NumberAxis());
            partAppraiserChart.setAnimated(false);
            partAppraiserChart.setLegendVisible(false);
            setPartAppraiserChart(partAppraiserChart, grrDetailResultDto.getPartAppraiserChartDto(), parts, appraisers);
            images.setGrrAPlotImagePath(exportImages("partAppraiserChart", partAppraiserChart));
        }
        boolean xBarAppraiserValid = exportParam.containsKey(GrrFxmlAndLanguageUtils.getString(UIConstant.CHART_4));
        xBarAppraiserValid = xBarAppraiserValid && exportParam.get(GrrFxmlAndLanguageUtils.getString(UIConstant.CHART_4));
        xBarAppraiserValid = xBarAppraiserValid && grrDetailResultDto.getXbarAppraiserChartDto() != null;
        if (xBarAppraiserValid) {
            LinearChart xBarAppraiserChart = buildControlChart(parts);
            setControlChartData(grrDetailResultDto.getXbarAppraiserChartDto(), xBarAppraiserChart, parts, appraisers);
            images.setGrrXBarImagePath(exportImages("xBarAppraiserChart", xBarAppraiserChart));
        }

        boolean rangeAppraiserValid = exportParam.containsKey(GrrFxmlAndLanguageUtils.getString(UIConstant.CHART_3));
        rangeAppraiserValid = rangeAppraiserValid && exportParam.get(GrrFxmlAndLanguageUtils.getString(UIConstant.CHART_3));
        rangeAppraiserValid = rangeAppraiserValid && grrDetailResultDto.getRangeAppraiserChartDto() != null;
        if (rangeAppraiserValid) {
            LinearChart rangeAppraiserChart = buildControlChart(parts);
            setControlChartData(grrDetailResultDto.getRangeAppraiserChartDto(), rangeAppraiserChart, parts, appraisers);
            images.setGrrRChartImagePath(exportImages("rangeAppraiserChart", rangeAppraiserChart));
        }
        boolean rrByAppraiserValid = exportParam.containsKey(GrrFxmlAndLanguageUtils.getString(UIConstant.CHART_2));
        rrByAppraiserValid = rrByAppraiserValid && exportParam.get(GrrFxmlAndLanguageUtils.getString(UIConstant.CHART_2));
        rrByAppraiserValid = rrByAppraiserValid && grrDetailResultDto.getRrbyAppraiserChartDto() != null;
        if (rrByAppraiserValid) {
            LineChart rrByAppraiserChart = buildScatterChart();
            setScatterChartData(grrDetailResultDto.getRrbyAppraiserChartDto(), rrByAppraiserChart);
            images.setGrrRPlotChartAppImagePath(exportImages("rrByAppraiserChart", rrByAppraiserChart));
        }
        boolean rrByPartValid = exportParam.containsKey(GrrFxmlAndLanguageUtils.getString(UIConstant.CHART_1));
        rrByPartValid = rrByPartValid && exportParam.get(GrrFxmlAndLanguageUtils.getString(UIConstant.CHART_1));
        rrByPartValid = rrByPartValid && grrDetailResultDto.getRrbyPartChartDto() != null;
        if (rrByPartValid) {
            LineChart rrbyPartChart = buildScatterChart();
            setScatterChartData(grrDetailResultDto.getRrbyPartChartDto(), rrbyPartChart);
            images.setGrrRPlotChartPartImagePath(exportImages("rrbyPartChart", rrbyPartChart));
        }
        boolean componentValid = exportParam.containsKey(GrrFxmlAndLanguageUtils.getString(UIConstant.CHART_6));
        componentValid = componentValid && exportParam.get(GrrFxmlAndLanguageUtils.getString(UIConstant.CHART_6));
        componentValid = componentValid && grrDetailResultDto.getComponentChartDto() != null;
        if (componentValid) {
            BarChart componentChart = new BarChart(new CategoryAxis(), new NumberAxis());
            componentChart.setAnimated(false);
            componentChart.setLegendVisible(false);
            setComponentChart(grrDetailResultDto.getComponentChartDto(), componentChart);
            images.setGrrComponentsImagePath(exportImages("componentChart", componentChart));
        }
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
        chart.setAnimated(false);
        chart.setLegendVisible(false);
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
        chart.setAnimated(false);
        chart.setLegendVisible(false);
        return chart;
    }

    private static void setComponentChart(GrrComponentCResultDto componentCResult, BarChart componentChart) {

        if (componentCResult == null) {
            return;
        }
        XYChart.Series series1 = new XYChart.Series();
        XYChart.Series series2 = new XYChart.Series();
        XYChart.Series series3 = new XYChart.Series();
        Double[] array = getArrayValue(componentCResult);
        Double yMax = MathUtils.getMax(array);
        Double yMin = MathUtils.getMin(array);
        if (yMax == null || yMin == null) {
            return;
        }
        NumberAxis yAxis = (NumberAxis) componentChart.getYAxis();
        final double factor = 0.01;
        double reserve = (yMax - yMin) * factor;
        yAxis.setAutoRanging(false);
        yMax += reserve;
        yMin -= reserve;
        Map<String, Object> yAxisRangeData = ChartOperatorUtils.getAdjustAxisRangeData(yMax, yMin, (int) Math.ceil(yMax - yMin));
        double newYMin = (Double) yAxisRangeData.get(ChartOperatorUtils.KEY_MIN);
        double newYMax = (Double) yAxisRangeData.get(ChartOperatorUtils.KEY_MAX);
        yAxis.setLowerBound(newYMin);
        yAxis.setUpperBound((newYMax > 120) ? 120 : newYMax);
        ChartOperatorUtils.updateAxisTickUnit(yAxis);

        series1.getData().add(new XYChart.Data<>(GrrFxmlAndLanguageUtils.getString(UIConstant.COMPONENTS_GAGE_R),
                DAPStringUtils.isInfinityAndNaN(componentCResult.getGrrContri()) ? 0 : componentCResult.getGrrContri()));
        series1.getData().add(new XYChart.Data<>(GrrFxmlAndLanguageUtils.getString(UIConstant.COMPONENTS_REPEATABILITY),
                DAPStringUtils.isInfinityAndNaN(componentCResult.getRepeatContri()) ? 0 : componentCResult.getRepeatContri()));
        series1.getData().add(new XYChart.Data<>(GrrFxmlAndLanguageUtils.getString(UIConstant.COMPONENTS_REPRODUCIBILITY),
                DAPStringUtils.isInfinityAndNaN(componentCResult.getReprodContri()) ? 0 : componentCResult.getReprodContri()));
        series1.getData().add(new XYChart.Data<>(GrrFxmlAndLanguageUtils.getString(UIConstant.COMPONENTS_PART),
                DAPStringUtils.isInfinityAndNaN(componentCResult.getPartContri()) ? 0 : componentCResult.getPartContri()));
        series2.getData().add(new XYChart.Data<>(GrrFxmlAndLanguageUtils.getString(UIConstant.COMPONENTS_GAGE_R),
                DAPStringUtils.isInfinityAndNaN(componentCResult.getGrrVar()) ? 0 : componentCResult.getGrrVar()));
        series2.getData().add(new XYChart.Data<>(GrrFxmlAndLanguageUtils.getString(UIConstant.COMPONENTS_REPEATABILITY),
                DAPStringUtils.isInfinityAndNaN(componentCResult.getRepeatVar()) ? 0 : componentCResult.getRepeatVar()));
        series2.getData().add(new XYChart.Data<>(GrrFxmlAndLanguageUtils.getString(UIConstant.COMPONENTS_REPRODUCIBILITY),
                DAPStringUtils.isInfinityAndNaN(componentCResult.getReprodVar()) ? 0 : componentCResult.getReprodVar()));
        series2.getData().add(new XYChart.Data<>(GrrFxmlAndLanguageUtils.getString(UIConstant.COMPONENTS_PART),
                DAPStringUtils.isInfinityAndNaN(componentCResult.getPartVar()) ? 0 : componentCResult.getPartVar()));
        series3.getData().add(new XYChart.Data<>(GrrFxmlAndLanguageUtils.getString(UIConstant.COMPONENTS_GAGE_R),
                DAPStringUtils.isInfinityAndNaN(componentCResult.getGrrTol()) ? 0 : componentCResult.getGrrTol()));
        series3.getData().add(new XYChart.Data<>(GrrFxmlAndLanguageUtils.getString(UIConstant.COMPONENTS_REPEATABILITY),
                DAPStringUtils.isInfinityAndNaN(componentCResult.getRepeatVar()) ? 0 : componentCResult.getRepeatVar()));
        series3.getData().add(new XYChart.Data<>(GrrFxmlAndLanguageUtils.getString(UIConstant.COMPONENTS_REPRODUCIBILITY),
                DAPStringUtils.isInfinityAndNaN(componentCResult.getReprodVar()) ? 0 : componentCResult.getReprodVar()));
        series3.getData().add(new XYChart.Data<>(GrrFxmlAndLanguageUtils.getString(UIConstant.COMPONENTS_PART),
                DAPStringUtils.isInfinityAndNaN(componentCResult.getPartVar()) ? 0 : componentCResult.getPartVar()));
        componentChart.getData().addAll(series1, series2, series3);

        String[] CHART_COMPONENT_CATEGORY = new String[]{
                GrrFxmlAndLanguageUtils.getString(UIConstant.COMPONENTS_CONTRIBUTION),
                GrrFxmlAndLanguageUtils.getString(UIConstant.COMPONENTS_VARIATION),
                GrrFxmlAndLanguageUtils.getString(UIConstant.COMPONENTS_TOLERANCE)};

        String[] colors = new String[CHART_COMPONENT_CATEGORY.length + 2];
        for (int i = 0; i < CHART_COMPONENT_CATEGORY.length; i++) {
            XYChart.Series series = (XYChart.Series) componentChart.getData().get(i);
            series.setName(CHART_COMPONENT_CATEGORY[i]);
            colors[i] = "default-color" + i;
        }
        colors[CHART_COMPONENT_CATEGORY.length] = "bar-legend-symbol";
        colors[CHART_COMPONENT_CATEGORY.length + 1] = "chart-bar";
        int digNumber = digNum <= 2 ? 0 : digNum - 2;

        //Chart text format
        ChartUtils.setChartText(componentChart.getData(), s -> {
            if (DAPStringUtils.isNumeric(s)) {
                Double value = Double.valueOf(s);
                if (!DAPStringUtils.isInfinityAndNaN(value)) {
                    return DAPStringUtils.formatDouble(value, digNumber) + "%";
                }
            }
            return s + "%";
        });
    }

    private static void setPartAppraiserChart(LineChart partAppraiserChart, GrrPACResultDto partAppraiserChartDto,
                                              List<String> parts,
                                              List<String> appraisers) {
        double[][] data = partAppraiserChartDto.getDatas();
        Double yMax = MathUtils.getMax(data);
        Double yMin = MathUtils.getMin(data);
        if (DAPStringUtils.isInfinityAndNaN(yMax) || DAPStringUtils.isInfinityAndNaN(yMin)) {
            return;
        }
        NumberAxis yAxis = (NumberAxis) partAppraiserChart.getYAxis();
        final double factor = 0.01;
        double reserve = (yMax - yMin) * factor;
        yAxis.setAutoRanging(false);
        yMax += reserve;
        yMin -= reserve;
        Map<String, Object> yAxisRangeData = ChartOperatorUtils.getAdjustAxisRangeData(yMax, yMin, (int) Math.ceil(yMax - yMin));
        double newYMin = (Double) yAxisRangeData.get(ChartOperatorUtils.KEY_MIN);
        double newYMax = (Double) yAxisRangeData.get(ChartOperatorUtils.KEY_MAX);
        yAxis.setLowerBound(newYMin);
        yAxis.setUpperBound(newYMax);
        ChartOperatorUtils.updateAxisTickUnit(yAxis);
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
    }

    private static void setControlChartData(GrrControlChartDto chartData,
                                            LinearChart chart,
                                            List<String> parts,
                                            List<String> appraisers) {

        int partCount = parts.size();
        Double[] x = chartData.getX();
        Double[] y = chartData.getY();
        Double[] ruleData = new Double[]{chartData.getUcl(), chartData.getCl(), chartData.getLcl()};
        Double yMax = MathUtils.getMax(y, ruleData);
        Double yMin = MathUtils.getMin(y, ruleData);
        if (DAPStringUtils.isInfinityAndNaN(yMax) || DAPStringUtils.isInfinityAndNaN(yMin)) {
            return;
        }
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        final double factor = 0.01;
        double reserve = (yMax - yMin) * factor;
        yAxis.setAutoRanging(false);
        yMax += reserve;
        yMin -= reserve;
        Map<String, Object> yAxisRangeData = ChartOperatorUtils.getAdjustAxisRangeData(yMax, yMin, (int) Math.ceil(yMax - yMin));
        double newYMin = (Double) yAxisRangeData.get(ChartOperatorUtils.KEY_MIN);
        double newYMax = (Double) yAxisRangeData.get(ChartOperatorUtils.KEY_MAX);
        yAxis.setLowerBound(newYMin);
        yAxis.setUpperBound(newYMax);
        ChartOperatorUtils.updateAxisTickUnit(yAxis);
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

        RuleLineData uclLineData = new RuleLineData(GrrFxmlAndLanguageUtils.getString(UIConstant.CHART_LINE_NAME_UCL), chartData.getUcl());
        RuleLineData clLineData = new RuleLineData(GrrFxmlAndLanguageUtils.getString(UIConstant.CHART_LINE_NAME_AVG), chartData.getCl());
        RuleLineData lclLineData = new RuleLineData(GrrFxmlAndLanguageUtils.getString(UIConstant.CHART_LINE_NAME_LCL), chartData.getLcl());
        uclLineData.setColor(Color.rgb(102, 102, 102));
        lclLineData.setColor(Color.rgb(178, 178, 178));
        uclLineData.setLineClass("dashed2-line");
        clLineData.setLineClass("solid-line");
        lclLineData.setLineClass("dashed1-line");
        horizonalLineData.add(uclLineData);
        horizonalLineData.add(clLineData);
        horizonalLineData.add(lclLineData);

        chart.getData().add(series);
//        button.setDisable(false);

        chart.buildValueMarkerWithoutTooltip(verticalLineData);
        chart.buildValueMarkerWithoutTooltip(horizonalLineData);
    }

    private static void setScatterChartData(GrrScatterChartDto scatterChartData, LineChart chart) {

        Double[] x = scatterChartData.getX();
        Double[] y = scatterChartData.getY();
        Double[] clX = scatterChartData.getClX();
        Double[] clY = scatterChartData.getClY();
        Double yMax = MathUtils.getMax(y, clY);
        Double yMin = MathUtils.getMin(y, clY);
        if (DAPStringUtils.isInfinityAndNaN(yMax) || DAPStringUtils.isInfinityAndNaN(yMin)) {
            return;
        }
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        final double factor = 0.01;
        Double reserve = (yMax - yMin) * factor;
        yAxis.setAutoRanging(false);
        yMax += reserve;
        yMin -= reserve;
        Map<String, Object> yAxisRangeData = ChartOperatorUtils.getAdjustAxisRangeData(yMax, yMin, (int) Math.ceil(yMax - yMin));
        double newYMin = (Double) yAxisRangeData.get(ChartOperatorUtils.KEY_MIN);
        double newYMax = (Double) yAxisRangeData.get(ChartOperatorUtils.KEY_MAX);
        yAxis.setLowerBound(newYMin);
        yAxis.setUpperBound(newYMax);
        ChartOperatorUtils.updateAxisTickUnit(yAxis);
        XYChart.Series scatterSeries = new XYChart.Series();
        XYChart.Series lineSeries = new XYChart.Series();
        for (int i = 0; i < x.length; i++) {
            scatterSeries.getData().add(new XYChart.Data<>(x[i], y[i]));
        }
        for (int i = 0; i < clX.length; i++) {
            lineSeries.getData().add(new XYChart.Data<>(clX[i], clY[i]));
        }
        chart.getData().addAll(scatterSeries, lineSeries);
        scatterSeries.getNode().getStyleClass().add("chart-series-hidden-line");
    }

    /**
     * method to export image
     *
     * @param name name
     * @param node node
     * @return path
     */
    public static String exportImages(String name, Node node) {
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
     * private class`
     */
    private static class WriteImage {
        private WritableImage image;
    }

    private static Double[] getArrayValue(GrrComponentCResultDto resultDto) {
        Double[] value = new Double[12];
        value[0] = resultDto.getGrrContri();
        value[1] = resultDto.getGrrTol();
        value[2] = resultDto.getGrrVar();
        value[3] = resultDto.getPartContri();
        value[4] = resultDto.getPartTol();
        value[5] = resultDto.getPartVar();
        value[6] = resultDto.getRepeatContri();
        value[7] = resultDto.getRepeatTol();
        value[8] = resultDto.getRepeatVar();
        value[9] = resultDto.getReprodContri();
        value[10] = resultDto.getReprodTol();
        value[11] = resultDto.getReprodVar();
        return value;
    }
}
