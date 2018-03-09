package com.dmsoft.firefly.plugin.chart;

import com.dmsoft.firefly.plugin.spc.charts.LinearChart;
import com.dmsoft.firefly.plugin.spc.charts.data.XYChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.AbnormalPointData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.BrokenLineData;
import com.dmsoft.firefly.plugin.spc.dto.chart.LineData;
import com.dmsoft.firefly.plugin.spc.charts.utils.enums.LineType;
import com.dmsoft.firefly.plugin.spc.charts.view.ChartOperateButton;
import com.dmsoft.firefly.plugin.spc.charts.view.ChartPanel;
import com.dmsoft.firefly.plugin.spc.dto.chart.RuleXYChartData;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by cherry on 2018/2/9.
 */
public class XYChartPanelApp extends Application {

    private LinearChart xBarChar;
    private ChartPanel<LinearChart> xBarChartPane;
    private RuleXYChartData ruleXYChartData = new RuleXYChartData();
    private Map<String, Boolean> operated = Maps.newHashMap();

    private Button uBtn;
    private Button lclBtn;
    private Button uclBtn;
    private Button pointBtn;
    private Button lineConnectBtn;
    private Button abnormalPointBtn;
    private Button clearBtn;
    private Button addBtn;
    private Button changeColorBtn;

    private String seriesName = "A1::All";
    private String pointName = "Point";
    private String connectLine = "Connect Line";

    @Override
    public void start(Stage primaryStage) throws Exception {

        initRuleData();
        initPanel();

        VBox vBox = new VBox();
        HBox hBox = new HBox();
        String[] lineCharts = UIConstant.SPC_CHART_XBAR_EXTERN_MENU;
        lclBtn = new Button(lineCharts[0]);
        uBtn = new Button(lineCharts[1]);
        uclBtn = new Button(lineCharts[2]);
        pointBtn = new Button(lineCharts[3]);
        lineConnectBtn = new Button(lineCharts[4]);
        abnormalPointBtn = new Button("Abnormal Point");
        clearBtn = new Button("Clear");
        addBtn = new Button("Add data");
        changeColorBtn = new Button("Change color");

        hBox.getChildren().addAll(lclBtn, uBtn, uclBtn, pointBtn, lineConnectBtn, abnormalPointBtn, clearBtn, addBtn, changeColorBtn);
        vBox.getChildren().add(xBarChartPane);
        vBox.getChildren().add(hBox);

        Scene scene = new Scene(vBox, 600, 400);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/charts.css").toExternalForm());
        primaryStage.setTitle("XY Chart panel example.");
        primaryStage.setScene(scene);
        primaryStage.show();

        initData();
        initEvent();

        operated.put(pointName, true);
        operated.put(connectLine, true);
    }

    private void initRuleData() {

        Double x[] = new Double[]{1D, 2D, 3D, 4D, 5D, 6D, 7D, 8D, 9D, 10D};
        Double y[] = new Double[]{1D, 10D, 100D, 1000D, 2D, 20D, 30D, 40D, 200D, 2000D};
        Double ids[] = new Double[]{1D, 2D, 3D, 4D, 5D, 6D, 7D, 8D, 9D, 10D};
        Double abnormalPointX[] = new Double[]{2D, 5D, 6D, 8D, 10D};
        Double abnormalPointY[] = new Double[]{20D, 50D, 600D, 800D, 1000D};
        Double brokenValue[] = new Double[]{9D, 35D, 1000D};

        Map<String, LineData> lineDataMap = Maps.newHashMap();
        Map<String, BrokenLineData> brokenLineDataMap = Maps.newHashMap();
        Map<String, AbnormalPointData> abnormalPointDataMap = Maps.newHashMap();

        String[] lineNames = UIConstant.SPC_CHART_LINE_NAME;
        Random rand = new Random();
        for (String lineName : lineNames) {
            LineData lineData = new LineData();
            lineData.setPlotOrientation(Orientation.HORIZONTAL);
            LineType lineType = UIConstant.SPC_CHART_LINE_NAME[3].equalsIgnoreCase(lineName) ?
                    LineType.SOLID : LineType.DASHED;
            String lineClass = lineType == LineType.SOLID ? "solid-line" : "dashed-line";
            lineData.setTitle(seriesName);
            lineData.setName(lineName);
            lineData.setColor(Color.RED);
            lineData.setLineClass(lineClass);
            lineData.setLineType(lineType);
            lineData.setValue(rand.nextInt(2000));
            lineDataMap.put(lineName, lineData);
        }

        BrokenLineData brokenLineData = new BrokenLineData();
        String brokenName = "Broke Line";
        brokenLineData.setName(brokenName);
        brokenLineData.setColor(Color.RED);
        brokenLineData.setValue(brokenValue);
        brokenLineDataMap.put(brokenName, brokenLineData);

        AbnormalPointData abnormalPointData = new AbnormalPointData();
        abnormalPointData.setX(abnormalPointX);
        abnormalPointData.setY(abnormalPointY);
        abnormalPointData.setColor(Color.RED);
        abnormalPointData.setName("R1");
        abnormalPointData.setVisible(true);
        abnormalPointDataMap.put("R1", abnormalPointData);

        XYChartData<Double, Double> xyChartData = new XYChartData();
        xyChartData.setX(x);
        xyChartData.setY(y);
        xyChartData.setIds(ids);
        xyChartData.setColor(Color.BLUE);
        xyChartData.setSeriesName(seriesName);
        ruleXYChartData = new RuleXYChartData();
        ruleXYChartData.setXyChartData(xyChartData);
        ruleXYChartData.setLineDataMap(lineDataMap);
    }

    private void initPanel() {

        ChartOperateButton button = new ChartOperateButton();
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickMarkVisible(false);
        yAxis.setTickMarkVisible(false);
        button.setListViewData(Arrays.asList(UIConstant.SPC_CHART_XBAR_EXTERN_MENU));
        button.setListViewSize(140, 150);
        xBarChar = new LinearChart(xAxis, yAxis);
        xBarChar.setAnimated(false);
        xBarChartPane = new ChartPanel<>(xBarChar);
        xBarChartPane.getCustomPane().getChildren().add(button);
    }

    private void initData() {

        XYChartData xyChartData = ruleXYChartData.getXyChartData();
        LinearChart chart = xBarChartPane.getChart();
        chart.createChartSeries(xyChartData, seriesName, null);

        xBarChartPane.activeChartDragging();

        Map<String, LineData> lineDataMap = ruleXYChartData.getLineDataMap();
//        Map<String, BrokenLineData> brokenLineDataMap = ruleXYChartData.getBrokenLineDataMap();
//        Map<String, AbnormalPointData> abnormalPointDataMap = ruleXYChartData.getAbnormalPointDataMap();
        List<LineData> lineDataList = Lists.newArrayList();
        for (Map.Entry<String, LineData> lineDataEntry: lineDataMap.entrySet()) {
            lineDataList.add(lineDataEntry.getValue());
            operated.put(lineDataEntry.getKey(), true);
        }
        chart.addValueMarker(lineDataList, seriesName);

//        brokenLineDataMap.forEach((key, value) -> {
//            chart.createBrokenLineData(value);
//            operated.put(key, true);
//        });
//        abnormalPointDataMap.forEach((key, value) -> {
//            chart.createAbnormalPointData(value);
//            operated.put(key, true);
//        });
    }

    private void initEvent() {
        String[] lineNames = UIConstant.SPC_CHART_LINE_NAME;

        lclBtn.setOnAction(event -> {
            String name = lineNames[0];
            operatorLine(name);
        });

        uclBtn.setOnAction(event -> {
            String name = lineNames[6];
            operatorLine(name);
        });

        uBtn.setOnAction(event -> {
            String name = lineNames[3];
            operatorLine(name);
        });

        pointBtn.setOnAction(event -> operatorChartPoint(pointName));
        lineConnectBtn.setOnAction(event -> operatorChartLine(connectLine));
        clearBtn.setOnAction(event -> clearChart());
        addBtn.setOnAction(event -> initData());
        changeColorBtn.setOnAction(event -> xBarChar.updateChartColor(seriesName, Color.YELLOW));

//        LinearChart chart = xBarChartPane.getChart();
//        ObservableList<XYChart.Series<Number, Number>> series = chart.getData();
//
//        for (int i = 0; i < series.size(); i++) {
//            XYChart.Series oneSeries = series.get(i);
//            if (i < 1) {
//                chart.setShowAnnotation(true);
//                chart.setSeriesAnnotationEvent(oneSeries, null);
//            }
//        }

    }

    private void operatorLine(String lineName) {
        boolean hasLine = operated.get(lineName);
        if (hasLine) {
            xBarChar.hiddenValueMarker(lineName);
            operated.put(lineName, false);
        } else {
            xBarChar.showValueMarker(lineName);
            operated.put(lineName, true);
        }
    }

    private void operatorChartLine(String name) {
        boolean hasShow = operated.get(name);
        LinearChart chart = xBarChartPane.getChart();
        ObservableList<XYChart.Series> series = chart.getData();

        for (int i = 0; i < series.size(); i++) {
            XYChart.Series oneSeries = series.get(i);
            if (i < 1) {
                if (hasShow) {
                    oneSeries.getNode().getStyleClass().add("chart-series-hidden-line");
                    operated.put(name, false);
                } else {
                    oneSeries.getNode().getStyleClass().remove("chart-series-hidden-line");
                    operated.put(name, true);
                }
            }
        }
    }

    private void operatorChartPoint(String name) {
        boolean hasShow = operated.get(name);
        LinearChart chart = xBarChartPane.getChart();
        ObservableList<XYChart.Series<Number, Number>> series = chart.getData();

//        if (hasShow) {
//            chart.setCreateSymbols(false);
//            operated.put(name, false);
//        } else {
//            chart.setCreateSymbols(true);
//            operated.put(name, true);
//        }

        for (int i = 0; i < series.size(); i++) {
            XYChart.Series oneSeries = series.get(i);
            if (i < 1) {
                ObservableList<XYChart.Data<Number, Number>> data = oneSeries.getData();
                data.forEach(oneData -> {
                    if (hasShow) {
                        oneData.getNode().getStyleClass().add("chart-line-hidden-symbol");
                        operated.put(name, false);
                    } else {
                        oneData.getNode().getStyleClass().remove("chart-line-hidden-symbol");
                        operated.put(name, true);
                    }
                });
            }
        }
    }

    private void clearChart() {
        LinearChart chart = xBarChartPane.getChart();
        chart.removeAllChildren();
    }
}
