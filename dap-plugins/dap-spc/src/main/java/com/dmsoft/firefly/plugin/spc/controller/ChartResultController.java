/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.plugin.spc.charts.*;
import com.dmsoft.firefly.plugin.spc.charts.annotation.AnnotationFetch;
import com.dmsoft.firefly.plugin.spc.charts.data.BoxAndWhiskerData;
import com.dmsoft.firefly.plugin.spc.charts.data.XYChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.*;
import com.dmsoft.firefly.plugin.spc.charts.shape.LineType;
import com.dmsoft.firefly.plugin.spc.charts.view.ChartAnnotationButton;
import com.dmsoft.firefly.plugin.spc.charts.view.ChartOperateButton;
import com.dmsoft.firefly.plugin.spc.charts.view.ChartPanel;
import com.dmsoft.firefly.plugin.spc.dto.chart.RuleXYChartData;
import com.dmsoft.firefly.plugin.spc.utils.ImageUtils;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.chart.*;
import javafx.scene.control.Tab;

import java.net.URL;
import java.util.*;
import java.util.function.Function;

/**
 * Created by Ethan.Yang on 2018/2/2.
 */
public class ChartResultController implements Initializable {

    private SpcMainController spcMainController;
    @FXML
    private Tab runTab;
    @FXML
    private Tab ndcTab;
    @FXML
    private Tab xBarTab;
    @FXML
    private Tab rangeTab;
    @FXML
    private Tab sdTab;
    @FXML
    private Tab medianTab;
    @FXML
    private Tab boxTab;
    @FXML
    private Tab mrTab;

    private ChartAnnotationButton editBtn;

    private String textColor = "#e92822";

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
        String color = pointRule.getNormalColor();
        color = (value > 300) && pointRule.getActiveRule().contains("R1") ? "#e92822" : color;
        pointStyle.setStyle("-fx-background-color: " + color);
        return pointStyle;
    };
    private Function pointTooltipFunc = (Function<PointTooltip, String>) pointTooltip ->
            pointTooltip.getSeriesName() + "\n("
                    + pointTooltip.getData().getXValue() +
                    ", " + pointTooltip.getData().getYValue() + ")";

    private String seriesName = "A1::All";
    private String pointName = "Point";
    private String connectLine = "Connect Line";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.initChartPane();
        this.initChartData();
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
        ndcTab.setContent(ndChartPane);
        runTab.setContent(runChartPane);
        xBarTab.setContent(xBarChartPane);
        rangeTab.setContent(rangeChartPane);
        sdTab.setContent(sdChartPane);
        medianTab.setContent(medianChartPane);
        boxTab.setContent(boxChartPane);
        mrTab.setContent(mrChartPane);
    }

    private void initChartData() {
        this.createNDChartData();
        this.createRunChartData();
        this.createXBarChartData();
        this.createBoxChartData();
    }

    private void initNDChartPane() {
        int checkBoxIndex = 0;
        String[] columns = new String[]{"1", "2"};
        ChartOperateButton button = new ChartOperateButton(columns, checkBoxIndex);
        button.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_choose_lines_normal.png")));
        button.setTableRowKeys(Arrays.asList(UIConstant.SPC_CHART_NDC_EXTERN_MENU));
        button.setTableViewSize(160, 240);
        button.getStyleClass().add("btn-icon-b");
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickMarkVisible(false);
        yAxis.setTickMarkVisible(false);
        yAxis.setMinorTickVisible(false);
        NDChart ndChart = new NDChart(xAxis, yAxis);
        ndChartPane = new ChartPanel<>(ndChart);
        ndChartPane.getCustomPane().getChildren().add(button);
    }

    private void initRunChartPane() {
        int checkBoxIndex = 0;
        String[] columns = new String[]{"1", "2"};
        String[] itemNames = new String[] {"", "item0", "item1", "item2"};

        ChartOperateButton button = new ChartOperateButton(columns, checkBoxIndex);
        editBtn = new ChartAnnotationButton();
        ChartOperateButton rRuleBtn = new ChartOperateButton(columns, checkBoxIndex, false);
        button.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_choose_lines_normal.png")));
        rRuleBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_rule_normal.png")));
        editBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_tracing_point_normal.png")));
        button.setTableRowKeys(Arrays.asList(UIConstant.SPC_CHART_RUN_EXTERN_MENU));
        rRuleBtn.setTableRowKeys(Arrays.asList(UIConstant.SPC_RULE_R));
        editBtn.setData(Arrays.asList(itemNames));
        button.setTableViewSize(155, 180);
        rRuleBtn.setTableViewSize(155, 200);

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickMarkVisible(false);
        yAxis.setTickMarkVisible(false);
        xAxis.setMinorTickVisible(false);
        yAxis.setMinorTickVisible(false);
        LinearChart runChart = new LinearChart(xAxis, yAxis);
        runChartPane = new ChartPanel<>(runChart);
        runChartPane.getCustomPane().getChildren().add(rRuleBtn);
        runChartPane.getCustomPane().getChildren().add(button);
        runChartPane.getCustomPane().getChildren().add(editBtn);

        editBtn.setClearCallBack(() -> {
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
        int checkBoxIndex = 0;
        String[] columns = new String[]{"1", "2"};
        ChartOperateButton button = new ChartOperateButton(columns, checkBoxIndex);
        button.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_choose_lines_normal.png")));
        button.setTableRowKeys(Arrays.asList(UIConstant.SPC_CHART_XBAR_EXTERN_MENU));
        button.setTableViewSize(155, 120);
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

    }

    private void initSdChartPane() {

    }

    private void initMedianChartPane() {

    }

    private void initBoxChartPane() {

        int checkBoxIndex = 0;
        String[] columns = new String[]{"1", "2"};
        ChartOperateButton button = new ChartOperateButton(columns, checkBoxIndex);
        button.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_choose_lines_normal.png")));
        button.setTableRowKeys(Arrays.asList(UIConstant.SPC_CHART_BOX_EXTERN_MENU));
        button.setTableViewSize(120, 100);
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

        AreaChart.Series areaChartData = new AreaChart.Series("Series 1", FXCollections.observableArrayList(
                new AreaChart.Data("0", 4),
                new AreaChart.Data("2", 5),
                new AreaChart.Data("4", 4),
                new AreaChart.Data("6", 2),
                new AreaChart.Data("8", 6),
                new AreaChart.Data("10", 8)
        ));

        ObservableList<BarChart.Series> barChartData = FXCollections.observableArrayList(
                new BarChart.Series("Series 2", FXCollections.observableArrayList(
                        new BarChart.Data("0", 4),
                        new BarChart.Data("2", 5),
                        new BarChart.Data("4", 4),
                        new BarChart.Data("6", 2),
                        new BarChart.Data("8", 6),
                        new BarChart.Data("10", 8)
                )));

        NDChart ndChart = ndChartPane.getChart();
        ndChart.setData(barChartData);
        ndChart.addAreaSeries(areaChartData);
//        ndChartPane.activeChartDragging();
    }

    private void createBoxChartData() {

        List<BoxAndWhiskerData> chartData = Lists.newArrayList();
        chartData.add(new BoxAndWhiskerData(1, 25, 20, 32, 16, 20));
        chartData.add(new BoxAndWhiskerData(2, 26, 30, 33, 22, 25));
        chartData.add(new BoxAndWhiskerData(3, 30, 38, 40, 20, 32));
        chartData.add(new BoxAndWhiskerData(4, 24, 30, 34, 22, 30));
        chartData.add(new BoxAndWhiskerData(5, 26, 36, 40, 24, 32));
        chartData.add(new BoxAndWhiskerData(6, 28, 38, 45, 25, 34));
        chartData.add(new BoxAndWhiskerData(7, 36, 30, 44, 28, 39));
        chartData.add(new BoxAndWhiskerData(8, 30, 18, 36, 16, 31));
        chartData.add(new BoxAndWhiskerData(9, 40, 50, 52, 36, 41));
        chartData.add(new BoxAndWhiskerData(10, 30, 34, 38, 28, 36));
        chartData.add(new BoxAndWhiskerData(11, 24, 12, 30, 8, 32.4));
        chartData.add(new BoxAndWhiskerData(12, 28, 40, 46, 25, 31.6));
        chartData.add(new BoxAndWhiskerData(13, 28, 18, 36, 14, 32.6));
        chartData.add(new BoxAndWhiskerData(14, 38, 30, 40, 26, 30.6));
        chartData.add(new BoxAndWhiskerData(15, 28, 33, 40, 28, 30.6));
        chartData.add(new BoxAndWhiskerData(16, 25, 10, 32, 6, 30.1));
        chartData.add(new BoxAndWhiskerData(17, 26, 30, 42, 18, 27.3));
        chartData.add(new BoxAndWhiskerData(18, 20, 18, 30, 10, 21.9));
        chartData.add(new BoxAndWhiskerData(19, 20, 10, 30, 5, 21.9));
        chartData.add(new BoxAndWhiskerData(20, 26, 16, 32, 10, 17.9));
        chartData.add(new BoxAndWhiskerData(21, 38, 40, 44, 32, 18.9));
        chartData.add(new BoxAndWhiskerData(22, 26, 40, 41, 12, 18.9));
        chartData.add(new BoxAndWhiskerData(23, 30, 18, 34, 10, 18.9));
        chartData.add(new BoxAndWhiskerData(24, 12, 23, 26, 12, 18.2));
        chartData.add(new BoxAndWhiskerData(25, 30, 40, 45, 16, 18.9));
        chartData.add(new BoxAndWhiskerData(26, 25, 35, 38, 20, 21.4));
        chartData.add(new BoxAndWhiskerData(27, 24, 12, 30, 8, 19.6));
        chartData.add(new BoxAndWhiskerData(28, 23, 44, 46, 15, 22.2));
        chartData.add(new BoxAndWhiskerData(29, 28, 18, 30, 12, 23));
        chartData.add(new BoxAndWhiskerData(30, 28, 18, 30, 12, 23.2));
        chartData.add(new BoxAndWhiskerData(31, 28, 18, 30, 12, 22));

        XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
        chartData.forEach(data -> {
            series.getData().add(
                    new XYChart.Data(
                            data.getxPos(),
                            data.getQ3(),
                            data)
            );
        });

        BoxPlotChart chart = boxChartPane.getChart();
        ObservableList<XYChart.Series<Number, Number>> data = chart.getData();
        if (data == null) {
            data = FXCollections.observableArrayList(series);
            chart.setData(data);
        } else {
            chart.getData().add(series);
        }

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
            lineData.setColor("#5fb222");
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
        xyChartData.setColor("#5fb222");
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
}
