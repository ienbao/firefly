/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.plugin.spc.charts.*;
import com.dmsoft.firefly.plugin.spc.charts.annotation.AnnotationFetch;
import com.dmsoft.firefly.plugin.spc.charts.data.BoxExtraData;
import com.dmsoft.firefly.plugin.spc.charts.data.XYChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.*;
import com.dmsoft.firefly.plugin.spc.charts.utils.enums.LineType;
import com.dmsoft.firefly.plugin.spc.charts.view.ChartAnnotationButton;
import com.dmsoft.firefly.plugin.spc.charts.view.ChartOperateButton;
import com.dmsoft.firefly.plugin.spc.charts.view.ChartPanel;
import com.dmsoft.firefly.plugin.spc.charts.view.VerticalTabPane;
import com.dmsoft.firefly.plugin.spc.dto.SpcChartDto;
import com.dmsoft.firefly.plugin.spc.dto.analysis.SpcChartResultDto;
import com.dmsoft.firefly.plugin.spc.dto.chart.*;
import com.dmsoft.firefly.plugin.spc.charts.data.BarCategoryData;
import com.dmsoft.firefly.plugin.spc.model.SpcNdChartData;
import com.dmsoft.firefly.plugin.spc.utils.ImageUtils;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import com.dmsoft.firefly.sdk.utils.ColorUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
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
    private Function rulePointFunc = (Function<PointRule, PointStyle>) pointRule -> {
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
//        this.initChartData();
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

    private void initChartData() {
        this.createNDChartData();
        this.createRunChartData();
        this.createXBarChartData();
        this.createBoxChartData();
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

        editBtn.setCallBack(() -> {
            runChart.clearAnnotation(annotationData);
        });

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

    private void createXBarChartData() {

        this.initLineRuleData();
        XYChartData xyChartData = ruleXYChartData.getXyChartData();
        LinearChart chart = xBarChartPane.getChart();
        chart.createChartSeries(xyChartData, pointTooltipFunc);
        xBarChartPane.activeChartDragging();
        Map<String, LineData> lineDataMap = ruleXYChartData.getLineDataMap();
        lineDataMap.forEach((key, value) -> {
            chart.addValueMarker(value);
        });
        ObservableList<XYChart.Series> series = chart.getData();
        series.forEach(oneSeries -> {
            chart.setSeriesDataStyleByRule(oneSeries, Lists.newArrayList("R1"), rulePointFunc);
        });
    }

    private void createRunChartData() {
        this.initLineRuleData();
        XYChartData xyChartData = ruleXYChartData.getXyChartData();
        LinearChart runChart = runChartPane.getChart();
        runChart.createChartSeries(xyChartData, pointTooltipFunc);

        runChartPane.activeChartDragging();

        Map<String, LineData> lineDataMap = ruleXYChartData.getLineDataMap();
        lineDataMap.forEach((key, value) -> {
            runChart.addValueMarker(value);
        });

        runChart.setSeriesAnnotationEvent(new AnnotationFetch() {
            @Override
            public String getValue(Object id) {
                return "part1";
            }

            @Override
            public String getTextColor() {
                return textColor;
            }

            @Override
            public boolean showedAnnotation() {
                return editBtn.isShowAnnotation();
            }

            @Override
            public void addData(XYChart.Data data) {
                annotationData.add(data);
            }
        });
    }

    private void createNDChartData() {

        Double[] x = new Double[]{1D, 4D, 7D, 10D, 13D, 16D, 19D, 22D, 25D, 28D};
        Double[] y = new Double[10];
        Double[] barWidth = new Double[10];

        List<BarCategoryData<Double, Double>> barCategoryData = Lists.newArrayList();
        XYChartData<Double, Double> xyChartData = new XYChartData();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            BarCategoryData<Double, Double> barCategoryData1 = new BarCategoryData();
            double startValue = x[i];
            double endValue = startValue + 2;
            double value = random.nextInt(10);
            y[i] = value;
            barWidth[i] = (startValue + endValue) / 2;
            barCategoryData1.setStartValue(startValue);
            barCategoryData1.setEndValue(endValue);
            barCategoryData1.setBarWidth(endValue - startValue);
            barCategoryData1.setValue(value);
            barCategoryData.add(barCategoryData1);
        }
        Color barColor = Color.BLUE;
        String seriesName = "A1:All";
        BarChartData<Double, Double> barChartData = new BarChartData<>(seriesName);
        barChartData.setBarData(barCategoryData);
        barChartData.setColor(barColor);

        xyChartData.setX(x);
        xyChartData.setY(y);
        xyChartData.setColor(barColor);
        xyChartData.setSeriesName(seriesName);
        ndChartPane.getChart().addAreaSeries(xyChartData, barColor);
        ndChartPane.getChart().createChartSeries(barChartData, barColor);
        String[] lineNames = UIConstant.SPC_NDCCHART_LINE_NAME;
        Random rand = new Random();
        for (String lineName : lineNames) {
            LineData lineData = new LineData();
            lineData.setPlotOrientation(Orientation.VERTICAL);
            LineType lineType = UIConstant.SPC_NDCCHART_LINE_NAME[4].equalsIgnoreCase(lineName) ?
                    LineType.SOLID : LineType.DASHED;
            String lineClass = lineType == LineType.SOLID ? "solid-line" : "dashed-line";
            lineData.setTitle(seriesName);
            lineData.setName(lineName);
            lineData.setColor(Color.GREEN);
            lineData.setLineClass(lineClass);
            lineData.setLineType(lineType);
            lineData.setValue(rand.nextInt(28));
            ndChartPane.getChart().addValueMarker(lineData);
        }
        ndChartPane.activeChartDragging();
    }

    private void createBoxChartData() {

        List<BoxExtraData> chartData = Lists.newArrayList();
        chartData.add(new BoxExtraData(1, 27, 25, 20, 32, 16, 20));
        chartData.add(new BoxExtraData(2, 28, 26, 30, 33, 22, 25));
        chartData.add(new BoxExtraData(3, 29, 30, 38, 40, 20, 32));
        chartData.add(new BoxExtraData(4, 27, 24, 30, 34, 22, 30));
        chartData.add(new BoxExtraData(5, 28, 26, 36, 40, 24, 32));
        chartData.add(new BoxExtraData(6, 30, 28, 38, 45, 25, 34));
        chartData.add(new BoxExtraData(7, 36, 36, 30, 44, 28, 39));
        chartData.add(new BoxExtraData(8, 35, 30, 18, 36, 16, 31));
        chartData.add(new BoxExtraData(9, 42, 40, 50, 52, 36, 41));
        chartData.add(new BoxExtraData(10, 35, 30, 34, 38, 28, 36));
        chartData.add(new BoxExtraData(11, 30, 24, 12, 30, 8, 32.4));
        chartData.add(new BoxExtraData(12, 31, 28, 40, 46, 25, 31.6));
        chartData.add(new BoxExtraData(13, 34, 28, 18, 36, 14, 32.6));
        chartData.add(new BoxExtraData(14, 39, 38, 30, 40, 26, 30.6));
        chartData.add(new BoxExtraData(15, 30, 28, 33, 40, 28, 30.6));
        chartData.add(new BoxExtraData(16, 28, 25, 10, 32, 6, 30.1));
        chartData.add(new BoxExtraData(17, 30, 26, 30, 42, 18, 27.3));
        chartData.add(new BoxExtraData(18, 25, 20, 18, 30, 10, 21.9));
        chartData.add(new BoxExtraData(19, 21, 20, 10, 30, 5, 21.9));
        chartData.add(new BoxExtraData(20, 29, 26, 16, 32, 10, 17.9));
        chartData.add(new BoxExtraData(21, 40, 38, 40, 44, 32, 18.9));
        chartData.add(new BoxExtraData(22, 30, 26, 40, 41, 12, 18.9));
        chartData.add(new BoxExtraData(23, 32, 30, 18, 34, 10, 18.9));
        chartData.add(new BoxExtraData(24, 20, 12, 23, 26, 12, 18.2));
        chartData.add(new BoxExtraData(25, 32, 30, 40, 45, 16, 18.9));
        chartData.add(new BoxExtraData(26, 27, 25, 35, 38, 20, 21.4));
        chartData.add(new BoxExtraData(27, 29, 24, 12, 30, 8, 19.6));
        chartData.add(new BoxExtraData(28, 28, 23, 44, 46, 15, 22.2));
        chartData.add(new BoxExtraData(29, 30, 28, 18, 30, 12, 23));
        chartData.add(new BoxExtraData(30, 32, 28, 18, 30, 12, 23.2));
        chartData.add(new BoxExtraData(31, 29, 28, 18, 30, 12, 22));

        BoxPlotChart chart = boxChartPane.getChart();
        com.dmsoft.firefly.plugin.spc.dto.chart.BoxAndWhiskerData data = new com.dmsoft.firefly.plugin.spc.dto.chart.BoxAndWhiskerData();
        data.setData(chartData);
        data.setColor(Color.RED);
        chart.createChartSeries(data);
        boxChartPane.activeChartDragging();
    }

    private void initLineRuleData() {

        Double x[] = new Double[]{1D, 2D, 3D, 4D, 5D, 6D, 7D, 8D, 9D, 10D};
        Double y[] = new Double[]{1D, 10D, 100D, 1000D, 2D, 20D, 30D, 40D, 200D, 2000D};
        Double ids[] = new Double[]{1D, 2D, 3D, 4D, 5D, 6D, 7D, 8D, 9D, 10D};
        Double abnormalPointX[] = new Double[]{2D, 5D, 6D, 8D, 10D};
        Double abnormalPointY[] = new Double[]{20D, 50D, 600D, 800D, 1000D};
        Double brokenValue[] = new Double[]{9D, 35D, 1000D};
        Map<String, LineData> lineDataMap = Maps.newHashMap();
        Map<String, BrokenLineData> brokenLineDataMap = Maps.newHashMap();
        Map<String, AbnormalPointData> abnormalPointDataMap = Maps.newHashMap();
        String[] lineNames = UIConstant.SPC_XBARCHART_LINE_NAME;
        Random rand = new Random();
        for (String lineName : lineNames) {
            LineData lineData = new LineData();
            lineData.setPlotOrientation(Orientation.HORIZONTAL);
            LineType lineType = UIConstant.SPC_XBARCHART_LINE_NAME[1].equalsIgnoreCase(lineName) ?
                    LineType.SOLID : LineType.DASHED;
            String lineClass = lineType == LineType.SOLID ? "solid-line" : "dashed-line";
            lineData.setTitle(seriesName);
            lineData.setName(lineName);
            lineData.setColor(Color.GREEN);
            lineData.setLineClass(lineClass);
            lineData.setLineType(lineType);
            lineData.setValue(rand.nextInt(2000));
            lineDataMap.put(lineName, lineData);
        }

//        BrokenLineData brokenLineData = new BrokenLineData();
//        String brokenName = "Broke Line";
//        brokenLineData.setName(brokenName);
//        brokenLineData.setColor("#e92822");
//        brokenLineData.setValue(brokenValue);
//        brokenLineDataMap.put(brokenName, brokenLineData);
//
//        AbnormalPointData abnormalPointData = new AbnormalPointData();
//        abnormalPointData.setX(abnormalPointX);
//        abnormalPointData.setY(abnormalPointY);
//        abnormalPointData.setColor("#e92822");
//        abnormalPointData.setName("R1");
//        abnormalPointData.setVisible(true);
//        abnormalPointDataMap.put("R1", abnormalPointData);

        XYChartData<Double, Double> xyChartData = new XYChartData();
        xyChartData.setX(x);
        xyChartData.setY(y);
        xyChartData.setIds(ids);
        xyChartData.setColor(Color.GREEN);
        xyChartData.setSeriesName(seriesName);
        ruleXYChartData = new RuleXYChartData();
        ruleXYChartData.setXyChartData(xyChartData);
        ruleXYChartData.setLineDataMap(lineDataMap);
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
        }

        this.setNdChartData("ND chart", ndcChartDataList);
    }

    public void setNdChartData(String chartName, List<INdcChartData> ndChartData) {
        NDChart chart = ndChartPane.getChart();
        if (chartMap.containsKey(chartName)) {
//            clear chart
            chart.removeAllChildren();
        } else {
            chartMap.put(chartName, chart);
        }
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
        setRunChartData(runChartData);
    }

    public void setControlChartData(String chartName, List<IControlChartData> controlChartData) {

    }

    public void setBoxChartData(String chartName, List<IBoxChartData> boxChartData) {

    }

    public void clearChartData() {

//        for (Map.Entry<String, XYChart> chartMap : chartMap.entrySet()) {
//            if (chartMap.getValue() instanceof NDChart) {
//                ((NDChart) chartMap.getValue()).removeAllChildren();
//            } else if (chartMap.getValue() instanceof LinearChart) {
//                ((LinearChart) chartMap.getValue()).removeAllChildren();
//            }
//        }
    }

    public void updateChartColor(Color color) {

    }

    private void setNdChartData(List<INdcChartData> ndChartData) {
        NDChart chart = ndChartPane.getChart();
        ndChartData.forEach(chartData -> {
            IBarChartData barChartData = chartData.getBarData();
            IXYChartData curveData = chartData.getCurveData();
            List<ILineData> lineData = chartData.getLineData();
//          add area data
            chart.addAreaSeries(curveData, Color.GREEN);
//          add bar chart data
            chart.createChartSeries(barChartData, Color.GREEN);
//                add line data
            if (lineData != null) {
                lineData.forEach(oneLine -> chart.addValueMarker(oneLine));
            }
        });
    }

    private void setRunChartData(List<IRunChartData> runChartData) {
        LinearChart chart = runChartPane.getChart();
        runChartData.forEach(chartData -> {
            IXYChartData xyChartData = chartData.getXYChartData();
            List<ILineData> lineData = chartData.getLineData();
            chart.createChartSeries(xyChartData, null);
            if (lineData != null) {
                lineData.forEach(oneLine -> chart.addValueMarker(oneLine));
            }
        });
    }
}
