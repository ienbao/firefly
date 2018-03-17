/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.plugin.spc.charts.*;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.*;
import com.dmsoft.firefly.plugin.spc.charts.utils.MathUtils;
import com.dmsoft.firefly.plugin.spc.charts.view.ChartAnnotationButton;
import com.dmsoft.firefly.plugin.spc.charts.view.ChartOperateButton;
import com.dmsoft.firefly.plugin.spc.charts.view.ChartPanel;
import com.dmsoft.firefly.plugin.spc.charts.view.VerticalTabPane;
import com.dmsoft.firefly.plugin.spc.dto.SpcChartDto;
import com.dmsoft.firefly.plugin.spc.dto.analysis.SpcChartResultDto;
import com.dmsoft.firefly.plugin.spc.dto.chart.*;
import com.dmsoft.firefly.plugin.spc.dto.chart.SpcNdChartData;
import com.dmsoft.firefly.plugin.spc.utils.ImageUtils;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import com.dmsoft.firefly.sdk.utils.ColorUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.chart.*;
import javafx.scene.control.Tab;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.*;
import java.util.function.Function;

/**
 * Created by Ethan.Yang on 2018/2/2.
 */
public class ChartResultController implements Initializable {

    private SpcMainController spcMainController;

    @FXML
    private Tab analysisChartTab;

    private VerticalTabPane chartTabPane;

    private ChartAnnotationButton editBtn;

    private String textColor = "#e92822";

    private Map<String, XYChart> chartMap = Maps.newHashMap();

    private ChartPanel<NDChart> ndChartPane;
    private ChartPanel<LinearChart> runChartPane;
    private ChartPanel<LinearChart> xBarChartPane;
    private ChartPanel<LinearChart> rangeChartPane;
    private ChartPanel<LinearChart> sdChartPane;
    private ChartPanel<LinearChart> medianChartPane;
    private ChartPanel<BoxPlotChart> boxChartPane;
    private ChartPanel<LinearChart> mrChartPane;

    private List<XYChart.Data> annotationData = Lists.newArrayList();

    private RuleXYChartData ruleXYChartData = new RuleXYChartData();
    private Function rulePointFunc = (Function<PointRule, PointStyle>) (PointRule pointRule) -> {
        PointStyle pointStyle = new PointStyle();
        Double value = (Double) pointRule.getData().getYValue();
        Color color = pointRule.getNormalColor();
        color = (value > 300) && pointRule.getActiveRule().contains("R1") ? Color.RED : color;
        pointStyle.setStyle("-fx-background-color: " + ColorUtils.toHexFromFXColor(color));
        return pointStyle;
    };
    private Function pointTooltipFunc = (Function<PointTooltip, String>) pointTooltip ->
            pointTooltip.getSeriesName() + "\n("
                    + pointTooltip.getData().getXValue() +
                    ", " + pointTooltip.getData().getYValue() + ")";

    private String seriesName = "A1::All";
    private String pointName = "Point";
    private String connectLine = "Connect Line";

    private String legend = "- - - LSL, USL  —— m Line   —— 6s Line";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.initChartPane();
    }

    private void initChartPane() {
        this.initNDChartPane();
        this.initRunChartPane();
        this.initXBarChartPane();
        this.initRangeChartPane();
        this.initSdChartPane();
        this.initMedianChartPane();
        this.initBoxChartPane();
        this.initMrChartPane();
        chartTabPane = new VerticalTabPane();
        ndChartPane.setId(UIConstant.SPC_CHART_NAME[0]);
        runChartPane.setId(UIConstant.SPC_CHART_NAME[1]);
        xBarChartPane.setId(UIConstant.SPC_CHART_NAME[2]);
        rangeChartPane.setId(UIConstant.SPC_CHART_NAME[3]);
        sdChartPane.setId(UIConstant.SPC_CHART_NAME[4]);
        medianChartPane.setId(UIConstant.SPC_CHART_NAME[5]);
        boxChartPane.setId(UIConstant.SPC_CHART_NAME[6]);
        mrChartPane.setId(UIConstant.SPC_CHART_NAME[7]);
        chartTabPane.addNode(ndChartPane, 0);
        chartTabPane.addNode(runChartPane, 1);
        chartTabPane.addNode(xBarChartPane, 2);
        chartTabPane.addNode(rangeChartPane, 3);
        chartTabPane.addNode(sdChartPane, 4);
        chartTabPane.addNode(medianChartPane, 5);
        chartTabPane.addNode(boxChartPane, 6);
        chartTabPane.addNode(mrChartPane, 7);
        chartTabPane.activeTabByIndex(0);

        analysisChartTab.setContent(chartTabPane);
    }

    private void initNDChartPane() {

        ChartOperateButton button = new ChartOperateButton(true);
        button.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_choose_lines_normal.png")));
        button.setListViewData(Arrays.asList(UIConstant.SPC_CHART_NDC_EXTERN_MENU));
        button.setListViewSize(140, 257);
        button.getStyleClass().add("btn-icon-b");
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickMarkVisible(false);
        yAxis.setTickMarkVisible(false);
        yAxis.setMinorTickVisible(false);

        button.setSelectCallBack((name, selected, selectedNames) -> {

            if (UIConstant.SPC_CHART_NDC_EXTERN_MENU[9].equalsIgnoreCase(name)) {
                ObservableList<XYChart.Series> series = ndChartPane.getChart().getData();
                series.forEach(oneSeries -> {
                    ndChartPane.getChart().toggleBarSeries(oneSeries, selected);
                });
            } else if (UIConstant.SPC_CHART_NDC_EXTERN_MENU[10].equalsIgnoreCase(name)) {
                ndChartPane.getChart().toggleAreaSeries(selected);
            } else {
                ndChartPane.getChart().toggleValueMarker(name, selected);
            }
        });
        NDChart<Double, Double> ndChart = new NDChart(xAxis, yAxis);
        ndChartPane = new ChartPanel(ndChart);
        ndChartPane.setLegend(legend);
        ndChartPane.getCustomPane().getChildren().add(button);
    }

    private void initRunChartPane() {

        String[] itemNames = new String[]{"", "item0", "item1", "item2"};
        ChartOperateButton button = new ChartOperateButton(true);
        editBtn = new ChartAnnotationButton();
        ChartOperateButton rRuleBtn = new ChartOperateButton(false, com.dmsoft.firefly.plugin.spc.charts.utils.enums.Orientation.BOTTOMLEFT);
        button.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_choose_lines_normal.png")));
        rRuleBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_rule_normal.png")));
        editBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_tracing_point_normal.png")));
        button.setListViewData(Arrays.asList(UIConstant.SPC_CHART_RUN_EXTERN_MENU));
        rRuleBtn.setListViewData(Arrays.asList(UIConstant.SPC_RULE_R));
        editBtn.setData(Arrays.asList(itemNames));
        button.setListViewSize(140, 211);
        rRuleBtn.setListViewSize(140, 211);
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickMarkVisible(false);
        yAxis.setTickMarkVisible(false);
        xAxis.setMinorTickVisible(false);
        yAxis.setMinorTickVisible(false);
        LinearChart runChart = new LinearChart(xAxis, yAxis);
        runChartPane = new ChartPanel<>(runChart);
        runChartPane.setLegend(legend);
        runChartPane.getCustomPane().getChildren().add(rRuleBtn);
        runChartPane.getCustomPane().getChildren().add(button);
        runChartPane.getCustomPane().getChildren().add(editBtn);
        runChartPane.getCustomPane().setMargin(editBtn, new Insets(0, 0, 0, 5));
        runChartPane.getCustomPane().setMargin(button, new Insets(0, 0, 0, 5));
//        runChartPane.getCustomPane().setMargin(rRuleBtn, new Insets(0, 0, 0, 5));
        editBtn.setCallBack(() -> runChart.clearAnnotation(annotationData));
        button.setSelectCallBack((name, selected, selectedNames) -> {
            if (UIConstant.SPC_CHART_RUN_EXTERN_MENU[8].equalsIgnoreCase(name)) {
                ObservableList<XYChart.Series> series = runChart.getData();
                series.forEach(oneSeries -> {
                    runChart.toggleSeriesLine(oneSeries, selected);
                });
            } else if (UIConstant.SPC_CHART_RUN_EXTERN_MENU[7].equalsIgnoreCase(name)) {
                ObservableList<XYChart.Series<Number, Number>> series = runChart.getData();
                series.forEach(oneSeries -> {
                    oneSeries.getData().forEach(dataItem -> {
                        runChart.toggleSeriesPoint(dataItem, selected);
                    });
                });
            } else {
                runChart.toggleValueMarker(name, selected);
            }
        });

        rRuleBtn.setSelectCallBack(((name, selected, selectedNames) -> {
            ObservableList<XYChart.Series> series = runChart.getData();
            series.forEach(oneSeries -> {
                runChart.setSeriesDataStyleByRule(oneSeries, Lists.newArrayList(selectedNames), rulePointFunc);
            });
        }));
    }

    private void initXBarChartPane() {

        ChartOperateButton button = new ChartOperateButton();
        button.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_choose_lines_normal.png")));
        button.setListViewData(Arrays.asList(UIConstant.SPC_CHART_XBAR_EXTERN_MENU));
        button.setListViewSize(140, 120);
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickMarkVisible(false);
        yAxis.setTickMarkVisible(false);
        xAxis.setMinorTickVisible(false);
        yAxis.setMinorTickVisible(false);
        LinearChart xBarChar = new LinearChart(xAxis, yAxis);
        xBarChartPane = new ChartPanel<>(xBarChar);
        xBarChartPane.getCustomPane().getChildren().add(button);

        button.setSelectCallBack((name, selected, selectedNames) -> {
            if (UIConstant.SPC_CHART_XBAR_EXTERN_MENU[4].equalsIgnoreCase(name)) {
                ObservableList<XYChart.Series> series = xBarChar.getData();
                series.forEach(oneSeries -> {
                    xBarChar.toggleSeriesLine(oneSeries, selected);
                });
            } else if (UIConstant.SPC_CHART_XBAR_EXTERN_MENU[3].equalsIgnoreCase(name)) {
                ObservableList<XYChart.Series<Number, Number>> series = xBarChar.getData();
                series.forEach(oneSeries -> {
                    oneSeries.getData().forEach(dataItem -> {
                        xBarChar.toggleSeriesPoint(dataItem, selected);
                    });
                });
            } else {
                xBarChar.toggleValueMarker(name, selected);
            }
        });
    }

    private void initRangeChartPane() {
        rangeChartPane = new ChartPanel(new LinearChart(new NumberAxis(), new NumberAxis()));
    }

    private void initSdChartPane() {
        sdChartPane = new ChartPanel(new LinearChart(new NumberAxis(), new NumberAxis()));
    }

    private void initMedianChartPane() {
        medianChartPane = new ChartPanel(new LinearChart(new NumberAxis(), new NumberAxis()));
    }

    private void initBoxChartPane() {

        ChartOperateButton button = new ChartOperateButton(true);
        button.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_choose_lines_normal.png")));
        button.setListViewData(Arrays.asList(UIConstant.SPC_CHART_BOX_EXTERN_MENU));
        button.setListViewSize(140, 50);
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickMarkVisible(false);
        yAxis.setTickMarkVisible(false);
        xAxis.setMinorTickVisible(false);
        yAxis.setMinorTickVisible(false);
        BoxPlotChart boxPlotChart = new BoxPlotChart(xAxis, yAxis);
        boxChartPane = new ChartPanel<>(boxPlotChart);
        boxChartPane.getCustomPane().getChildren().add(button);
        button.setSelectCallBack((name, selected, selectedNames) -> {
            if (name.equalsIgnoreCase(UIConstant.SPC_CHART_BOX_EXTERN_MENU[0])) {
                boxPlotChart.toggleStroke(selected);
            }
        });
    }

    private void initMrChartPane() {
        mrChartPane = new ChartPanel(new LinearChart(new NumberAxis(), new NumberAxis()));
    }


    /**
     * init main controller
     *
     * @param spcMainController main controller
     */
    public void init(SpcMainController spcMainController) {
        this.spcMainController = spcMainController;
    }

    /**
     * init spc chart data
     *
     * @param spcChartDtoList the list of chart data
     */
    public void initSpcChartData(List<SpcChartDto> spcChartDtoList) {
        Map<String, java.awt.Color> colorCache = spcMainController.getColorCache();
        List<INdcChartData> ndcChartDataList = Lists.newArrayList();
        List<IRunChartData> runChartDataList = Lists.newArrayList();
        List<IControlChartData> xBarChartDataList = Lists.newArrayList();
        List<IControlChartData> rangeChartDataList = Lists.newArrayList();
        List<IControlChartData> sdChartDataList = Lists.newArrayList();
        List<IControlChartData> medianChartDataList = Lists.newArrayList();
        List<IBoxChartData> boxChartDataList = Lists.newArrayList();
        List<IControlChartData> mrChartDataList = Lists.newArrayList();
        for (SpcChartDto spcChartDto : spcChartDtoList) {
            String key = spcChartDto.getKey();
            Color color = ColorUtils.toFxColorFromAwtColor(colorCache.get(key));
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

        }

        this.setNdChartData(UIConstant.SPC_CHART_NAME[0], ndcChartDataList);
        this.setRunChartData(UIConstant.SPC_CHART_NAME[1], runChartDataList);
        this.setControlChartData(UIConstant.SPC_CHART_NAME[2], xBarChartDataList);
        this.setControlChartData(UIConstant.SPC_CHART_NAME[3], rangeChartDataList);
        this.setControlChartData(UIConstant.SPC_CHART_NAME[4], sdChartDataList);
        this.setControlChartData(UIConstant.SPC_CHART_NAME[5], medianChartDataList);
        this.setBoxChartData(UIConstant.SPC_CHART_NAME[6], boxChartDataList);
        this.setControlChartData(UIConstant.SPC_CHART_NAME[7], mrChartDataList);
    }

    public void setNdChartData(String chartName, List<INdcChartData> ndChartData) {
        NDChart chart = ndChartPane.getChart();
        if (chartMap.containsKey(chartName)) {
//            clear chart
            chart.removeAllChildren();
        } else {
            chartMap.put(chartName, chart);
        }
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
        setNdChartData(ndChartData);
    }

    public void setRunChartData(String chartName, List<IRunChartData> runChartData) {
        LinearChart chart = runChartPane.getChart();
        if (chartMap.containsKey(chartName)) {
//            clear chart
            chart.removeAllChildren();
        } else {
            chartMap.put(chartName, chart);
        }
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
        setRunChartData(runChartData);
    }

    public void setControlChartData(String chartName, List<IControlChartData> controlChartData) {
        Object chart = getChartByName(chartName);
        if (chart != null && chart instanceof LinearChart) {
            LinearChart linearChart = (LinearChart) chart;
            if (chartMap.containsKey(chartName)) {
//            clear chart
                linearChart.removeAllChildren();
            } else {
                chartMap.put(chartName, linearChart);
            }
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
            setControlChartData(linearChart, controlChartData);
        }
    }

    public void setBoxChartData(String chartName, List<IBoxChartData> boxChartData) {
        BoxPlotChart chart = boxChartPane.getChart();
        if (chartMap.containsKey(chartName)) {
//            clear chart
            chart.removeAllChildren();
        } else {
            chartMap.put(chartName, chart);
        }
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
        xAxis.setLowerBound(xMin);
        xAxis.setUpperBound(xMax);
        yAxis.setLowerBound(yMin);
        yAxis.setUpperBound(yMax);
        setBoxPlotChartData(boxChartData);
    }

    public void clearChartData() {
        for (Map.Entry<String, XYChart> chartMap : chartMap.entrySet()) {
            if (chartMap.getValue() instanceof NDChart) {
                ((NDChart) chartMap.getValue()).removeAllChildren();
            } else if (chartMap.getValue() instanceof LinearChart) {
                ((LinearChart) chartMap.getValue()).removeAllChildren();
            } else if (chartMap.getValue() instanceof BoxPlotChart) {
                ((BoxPlotChart) chartMap.getValue()).removeAllChildren();
            }
        }
    }

    public void updateChartColor(String unique, Color color) {
        ndChartPane.getChart().updateChartColor(unique, color);
        xBarChartPane.getChart().updateChartColor(unique, color);
        rangeChartPane.getChart().updateChartColor(unique, color);
        sdChartPane.getChart().updateChartColor(unique, color);
        medianChartPane.getChart().updateChartColor(unique, color);
        boxChartPane.getChart().updateChartColor(unique, color);
        mrChartPane.getChart().updateChartColor(unique, color);
    }

    private void setNdChartData(List<INdcChartData> ndChartData) {
        NDChart chart = ndChartPane.getChart();
        ndChartData.forEach(chartData -> {
            IBarChartData barChartData = chartData.getBarData();
            IXYChartData curveData = chartData.getCurveData();
            List<ILineData> lineData = chartData.getLineData();
//          add area data
            chart.addAreaSeries(curveData, seriesName, chartData.getColor());
//          add bar chart data
            chart.createChartSeries(barChartData, seriesName, chartData.getColor());
//                add line data
            if (lineData != null) {
                chart.addValueMarker(lineData, seriesName);
            }
        });
    }

    private void setRunChartData(List<IRunChartData> runChartData) {
        LinearChart chart = runChartPane.getChart();
        runChartData.forEach(chartData -> {
            IXYChartData xyChartData = chartData.getXYChartData();
            List<ILineData> lineData = chartData.getLineData();
            chart.createChartSeries(xyChartData, seriesName, null);
            if (lineData != null) {
                chart.addValueMarker(lineData, seriesName);
            }
        });
    }

    private void setControlChartData(LinearChart chart, List<IControlChartData> controlChartData) {
        controlChartData.forEach(chartData -> {
            IXYChartData xyChartData = chartData.getChartData();
            List<ILineData> lineData = chartData.getLineData();
            List<IPathData> pathData = chartData.getBrokenLineData();
//            add chart data
            chart.createChartSeries(xyChartData, seriesName, null);
            if (lineData != null) {
                chart.addValueMarker(lineData, seriesName);
            }
            if (pathData != null) {
                chart.addPathMarker(pathData, seriesName);
            }
        });
    }

    private void setBoxPlotChartData(List<IBoxChartData> boxPlotChartData) {
        BoxPlotChart chart = boxChartPane.getChart();
        boxPlotChartData.forEach(chartData -> {
            IBoxAndWhiskerData boxAndWhiskerData = chartData.getBoxAndWhiskerData();
            IPoint points = chartData.getPoints();
            chart.createChartSeries(boxAndWhiskerData, seriesName);
            chart.addPoints(points, Color.GREEN);
        });
    }

    private Object getChartByName(String name) {
        if (UIConstant.SPC_CHART_NAME[0].equals(name)) {
            return ndChartPane.getChart();
        } else if (UIConstant.SPC_CHART_NAME[1].equals(name)) {
            return runChartPane.getChart();
        } else if (UIConstant.SPC_CHART_NAME[2].equals(name)) {
            return xBarChartPane.getChart();
        } else if (UIConstant.SPC_CHART_NAME[3].equals(name)) {
            return rangeChartPane.getChart();
        } else if (UIConstant.SPC_CHART_NAME[4].equals(name)) {
            return sdChartPane.getChart();
        } else if (UIConstant.SPC_CHART_NAME[5].equals(name)) {
            return medianChartPane.getChart();
        } else if (UIConstant.SPC_CHART_NAME[6].equals(name)) {
            return boxChartPane.getChart();
        } else if (UIConstant.SPC_CHART_NAME[7].equals(name)) {
            return mrChartPane.getChart();
        } else {
            return null;
        }
    }
}
