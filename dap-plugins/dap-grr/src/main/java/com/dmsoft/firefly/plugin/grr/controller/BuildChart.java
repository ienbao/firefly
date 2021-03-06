package com.dmsoft.firefly.plugin.grr.controller;

import com.dmsoft.firefly.gui.components.chart.ChartOperatorUtils;
import com.dmsoft.firefly.plugin.grr.charts.LinearChart;
import com.dmsoft.firefly.plugin.grr.charts.data.ILineData;
import com.dmsoft.firefly.plugin.grr.charts.data.RuleLineData;
import com.dmsoft.firefly.plugin.grr.charts.data.VerticalCutLine;
import com.dmsoft.firefly.plugin.grr.dto.GrrImageDto;
import com.dmsoft.firefly.plugin.grr.dto.SearchConditionDto;
import com.dmsoft.firefly.plugin.grr.utils.FileUtils;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.dmsoft.firefly.plugin.grr.utils.MathUtils;
import com.dmsoft.firefly.plugin.grr.utils.UIConstant;
import com.dmsoft.firefly.plugin.grr.utils.charts.ChartUtils;
import com.dmsoft.firefly.plugin.grr.dto.analysis.*;
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
    private static int digNum = 6;

    /**
     * method to build image
     *
     * @param grrDetailResultDto grr detail result dto
     * @param searchConditionDto searchConditionDto
     * @return grr image dto
     */
    public static GrrImageDto buildImage(GrrDetailResultDto grrDetailResultDto,
                                         SearchConditionDto searchConditionDto,
                                         Map<String, Boolean> exportParam) {
        digNum = RuntimeContext.getBean(EnvService.class).findActivatedTemplate().getDecimalDigit();
        GrrImageDto images = new GrrImageDto();
        List<String> parts = searchConditionDto.getParts();
        List<String> appraisers = searchConditionDto.getAppraisers();
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
            LinearChart xBarAppraiserChart = buildControlChart();
            setControlChartData(grrDetailResultDto.getXbarAppraiserChartDto(), xBarAppraiserChart, parts);
            setControlChartXAxisLabel(appraisers, (NumberAxis) xBarAppraiserChart.getXAxis(), searchConditionDto.getAppraiser(), parts.size());
            images.setGrrXBarImagePath(exportImages("xBarAppraiserChart", xBarAppraiserChart));
        }

        boolean rangeAppraiserValid = exportParam.containsKey(GrrFxmlAndLanguageUtils.getString(UIConstant.CHART_3));
        rangeAppraiserValid = rangeAppraiserValid && exportParam.get(GrrFxmlAndLanguageUtils.getString(UIConstant.CHART_3));
        rangeAppraiserValid = rangeAppraiserValid && grrDetailResultDto.getRangeAppraiserChartDto() != null;
        if (rangeAppraiserValid) {
            LinearChart rangeAppraiserChart = buildControlChart();
            setControlChartData(grrDetailResultDto.getRangeAppraiserChartDto(), rangeAppraiserChart, parts);
            setControlChartXAxisLabel(appraisers, (NumberAxis) rangeAppraiserChart.getXAxis(), searchConditionDto.getAppraiser(), parts.size());
            images.setGrrRChartImagePath(exportImages("rangeAppraiserChart", rangeAppraiserChart));
        }
        boolean rrByAppraiserValid = exportParam.containsKey(GrrFxmlAndLanguageUtils.getString(UIConstant.CHART_2));
        rrByAppraiserValid = rrByAppraiserValid && exportParam.get(GrrFxmlAndLanguageUtils.getString(UIConstant.CHART_2));
        rrByAppraiserValid = rrByAppraiserValid && grrDetailResultDto.getRrbyAppraiserChartDto() != null;
        if (rrByAppraiserValid) {
            LineChart rrByAppraiserChart = buildScatterChart();
            setScatterChartData(grrDetailResultDto.getRrbyAppraiserChartDto(), rrByAppraiserChart);
            setScatterChartXAxisLabel(appraisers, (NumberAxis) rrByAppraiserChart.getXAxis());
            images.setGrrRPlotChartAppImagePath(exportImages("rrByAppraiserChart", rrByAppraiserChart));
        }
        boolean rrByPartValid = exportParam.containsKey(GrrFxmlAndLanguageUtils.getString(UIConstant.CHART_1));
        rrByPartValid = rrByPartValid && exportParam.get(GrrFxmlAndLanguageUtils.getString(UIConstant.CHART_1));
        rrByPartValid = rrByPartValid && grrDetailResultDto.getRrbyPartChartDto() != null;
        if (rrByPartValid) {
            LineChart rrbyPartChart = buildScatterChart();
            setScatterChartData(grrDetailResultDto.getRrbyPartChartDto(), rrbyPartChart);
            setScatterChartXAxisLabel(parts, (NumberAxis) rrbyPartChart.getXAxis());
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

    private static void setControlChartXAxisLabel(List<String> appraisers, NumberAxis xAxis, String appraiserKey, int partSize) {
        xAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(xAxis) {
            @Override
            public String toString(final Number object) {
                double standardValue = (partSize + 1) / 2.0;

                if (object != null && object instanceof Double) {
                    int partQuotient = (int) (object.doubleValue() / partSize);
                    double standardQuotient = ((Double) object - standardValue) % partSize;
                    double partRemainder = object.doubleValue() % partSize;
                    if (standardQuotient % 1.0 == 0 && partRemainder == standardValue) {
                        boolean appraiserValid = DAPStringUtils.isNotEmpty(appraiserKey);
                        appraiserValid = appraiserValid && appraisers != null && appraisers.size() > partQuotient;
                        return appraiserValid ? appraisers.get(partQuotient)
                                : GrrFxmlAndLanguageUtils.getString(UIConstant.AXIS_LBL_PREFIX_APPRAISER) + (partQuotient + 1);
                    }
                }
                return "";
            }
        });
    }

    private static void setScatterChartXAxisLabel(List<String> xAxisLabels, NumberAxis xAxis) {
        xAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(xAxis) {
            @Override
            public String toString(final Number object) {
                if (object != null && object instanceof Double && (Double) object % 1 == 0) {
                    int value = object.intValue();
                    boolean xAxisLabelValid = xAxisLabels != null;
                    xAxisLabelValid = xAxisLabelValid && !xAxisLabels.isEmpty();
                    xAxisLabelValid = xAxisLabelValid && xAxisLabels.size() >= value;
                    if (xAxisLabelValid && object.intValue() != 0) {
                        return xAxisLabels.get(object.intValue() - 1);
                    }
                }
                return "";
            }
        });
    }

    private static LinearChart buildControlChart() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        final double tickUnit = 0.5;
        xAxis.setTickUnit(tickUnit);
        xAxis.setMinorTickVisible(false);
        xAxis.setTickMarkVisible(false);
        yAxis.setMinorTickVisible(false);
        yAxis.setTickMarkVisible(false);
        yAxis.setAutoRanging(false);
        xAxis.setAutoRanging(false);
        LinearChart chart = new LinearChart(xAxis, yAxis);
        chart.setAnimated(false);
        chart.setLegendVisible(false);
        chart.setHorizontalZeroLineVisible(false);
        chart.setVerticalZeroLineVisible(false);
        chart.setVerticalGridLinesVisible(false);
        return chart;
    }

    private static LineChart buildScatterChart() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setMinorTickVisible(false);
        yAxis.setMinorTickVisible(false);
        xAxis.setTickMarkVisible(false);
        yAxis.setTickMarkVisible(false);
        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);
        xAxis.setTickUnit(1);
        LineChart chart = new LineChart(xAxis, yAxis);
        chart.setAnimated(false);
        chart.setLegendVisible(false);
        chart.setVerticalZeroLineVisible(false);
        chart.setHorizontalZeroLineVisible(false);
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
        Double yMax = MathUtils.getNaNToZoreMax(array);
        Double yMin = MathUtils.getNaNToZoreMin(array);
        if (yMax == null || yMin == null) {
            return;
        }
        NumberAxis yAxis = (NumberAxis) componentChart.getYAxis();
        final double factor = 0.2;
        double reserve = (yMax - yMin) * factor;
        yAxis.setAutoRanging(false);
        yMax += reserve;
        Map<String, Object> yAxisRangeData = ChartOperatorUtils.getAdjustAxisRangeData(yMax, yMin, (int) Math.ceil(yMax - yMin));
        double newYMin = (Double) yAxisRangeData.get(ChartOperatorUtils.KEY_MIN);
        double newYMax = (Double) yAxisRangeData.get(ChartOperatorUtils.KEY_MAX);
        yAxis.setLowerBound(newYMin);
        yAxis.setUpperBound(newYMax);
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
                DAPStringUtils.isInfinityAndNaN(componentCResult.getRepeatTol()) ? 0 : componentCResult.getRepeatTol()));
        series3.getData().add(new XYChart.Data<>(GrrFxmlAndLanguageUtils.getString(UIConstant.COMPONENTS_REPRODUCIBILITY),
                DAPStringUtils.isInfinityAndNaN(componentCResult.getReprodTol()) ? 0 : componentCResult.getReprodTol()));
        series3.getData().add(new XYChart.Data<>(GrrFxmlAndLanguageUtils.getString(UIConstant.COMPONENTS_PART),
                DAPStringUtils.isInfinityAndNaN(componentCResult.getPartTol()) ? 0 : componentCResult.getPartTol()));

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
            ObservableList<XYChart.Data<String, Number>> dataList = FXCollections.observableArrayList();
            double[] appraiser = data[i];
            for (int j = 0; j < appraiser.length; j++) {
                if (!DAPStringUtils.isInfinityAndNaN(appraiser[j])) {
                    dataList.add(new XYChart.Data<String, Number>(parts.get(j), appraiser[j], appraisers.get(i)));
                }
            }
            XYChart.Series series = new XYChart.Series(appraisers.get(i), dataList);
            seriesData.add(series);
        }
        partAppraiserChart.getData().addAll(seriesData);
    }

    private static void setControlChartData(GrrControlChartDto chartData,
                                            LinearChart chart,
                                            List<String> parts) {

        int partCount = parts.size();
        Double[] x = chartData.getX();
        Double[] y = chartData.getY();
        Double[] ruleData = new Double[]{chartData.getUcl(), chartData.getCl(), chartData.getLcl()};
        Double yMax = MathUtils.getMax(y, ruleData);
        Double yMin = MathUtils.getMin(y, ruleData);
        Double xMax = MathUtils.getMax(x);
        Double xMin = MathUtils.getMin(x);
        if (DAPStringUtils.isInfinityAndNaN(yMax) || DAPStringUtils.isInfinityAndNaN(yMin)) {
            return;
        }
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        NumberAxis xAxis = (NumberAxis) chart.getXAxis();
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
        xAxis.setLowerBound(xMin - UIConstant.X_FACTOR);
        xAxis.setUpperBound(xMax + UIConstant.X_FACTOR);
        ChartOperatorUtils.updateAxisTickUnit(yAxis);
        List<ILineData> horizontalLineData = Lists.newArrayList();
        List<ILineData> verticalLineData = Lists.newArrayList();
        ObservableList<XYChart.Data<Number, Number>> dataList = FXCollections.observableArrayList();
//        draw vertical line
        for (int i = 0; i < x.length; i++) {
            if ((i + 1) % partCount == 0 && i != x.length - 1) {
                double value = (x[i] + x[i + 1]) / 2;
                verticalLineData.add(new VerticalCutLine(value));
            }
            dataList.add(new XYChart.Data<Number, Number>(x[i], y[i], parts.get(i % partCount)));
        }

        RuleLineData uclLineData = new RuleLineData(GrrFxmlAndLanguageUtils.getString(UIConstant.CHART_LINE_NAME_UCL), chartData.getUcl());
        RuleLineData clLineData = new RuleLineData(GrrFxmlAndLanguageUtils.getString(UIConstant.CHART_LINE_NAME_AVG), chartData.getCl());
        RuleLineData lclLineData = new RuleLineData(GrrFxmlAndLanguageUtils.getString(UIConstant.CHART_LINE_NAME_LCL), chartData.getLcl());
        uclLineData.setColor(Color.rgb(102, 102, 102));
        lclLineData.setColor(Color.rgb(178, 178, 178));
        uclLineData.setLineClass("dashed2-line");
        clLineData.setLineClass("solid-line");
        lclLineData.setLineClass("dashed1-line");
        horizontalLineData.add(uclLineData);
        horizontalLineData.add(clLineData);
        horizontalLineData.add(lclLineData);

        chart.getData().add(new XYChart.Series<>(dataList));
//        button.setDisable(false);

        chart.buildValueMarkerWithoutTooltip(verticalLineData);
        chart.buildValueMarkerWithoutTooltip(horizontalLineData);
    }

    private static void setScatterChartData(GrrScatterChartDto scatterChartData, LineChart chart) {

        Double[] x = scatterChartData.getX();
        Double[] y = scatterChartData.getY();
        Double[] clX = scatterChartData.getClX();
        Double[] clY = scatterChartData.getClY();
        Double yMax = MathUtils.getMax(y, clY);
        Double yMin = MathUtils.getMin(y, clY);
        Double xMin = MathUtils.getMin(x, clX);
        Double xMax = MathUtils.getMax(x, clX);
        if (DAPStringUtils.isInfinityAndNaN(yMax)
                || DAPStringUtils.isInfinityAndNaN(yMin)
                || DAPStringUtils.isInfinityAndNaN(xMax)
                || DAPStringUtils.isInfinityAndNaN(xMin)) {
            return;
        }
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        final double factor = 0.01;
        Double reserve = (yMax - yMin) * factor;
        yAxis.setAutoRanging(false);
        yMax += reserve;
        yMin -= reserve;
        Map<String, Object> yAxisRangeData = ChartOperatorUtils.getAdjustAxisRangeData(yMax, yMin, (int) Math.ceil(yMax - yMin));
        double newYMin = (Double) yAxisRangeData.get(ChartOperatorUtils.KEY_MIN);
        double newYMax = (Double) yAxisRangeData.get(ChartOperatorUtils.KEY_MAX);
        xAxis.setLowerBound(xMin - UIConstant.X_FACTOR);
        xAxis.setUpperBound(xMax + UIConstant.X_FACTOR);
        yAxis.setLowerBound(newYMin);
        yAxis.setUpperBound(newYMax);
        ChartOperatorUtils.updateAxisTickUnit(yAxis);
        ObservableList<XYChart.Data<Number, Number>> scatterDataList = FXCollections.observableArrayList();
        ObservableList<XYChart.Data<Number, Number>> lineDataList = FXCollections.observableArrayList();
        for (int i = 0; i < x.length; i++) {
            scatterDataList.add(new XYChart.Data<>(x[i], y[i]));
        }
        for (int i = 0; i < clX.length; i++) {
            lineDataList.add(new XYChart.Data<>(clX[i], clY[i]));
        }
        XYChart.Series scatterSeries = new XYChart.Series(scatterDataList);
        XYChart.Series lineSeries = new XYChart.Series(lineDataList);
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

    /**
     * private class`
     */
    private static class WriteImage {
        private WritableImage image;
    }
}
