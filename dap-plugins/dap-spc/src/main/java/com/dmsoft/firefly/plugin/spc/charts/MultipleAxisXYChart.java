package com.dmsoft.firefly.plugin.spc.charts;

import com.google.common.collect.Lists;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.util.List;
import java.util.Set;

/**
 * Created by cherry on 2018/3/1.
 */
public class MultipleAxisXYChart extends StackPane {

    private final XYChart baseChart;
    private final double yAxisWidth = 10;
    private final double yAxisSeparation = 10;
    private final ObservableList<XYChart> backgroundCharts = FXCollections.observableArrayList();

    public MultipleAxisXYChart(XYChart baseChart) {
        this.baseChart = baseChart;
        styleBaseChart(baseChart);
        setFixedAxisWidth(baseChart);
        setAlignment(Pos.CENTER_LEFT);
        rebuildChart();
        backgroundCharts.addListener((Observable observable) -> rebuildChart());
    }

    private void setFixedAxisWidth(XYChart chart) {
        chart.getYAxis().setPrefWidth(yAxisWidth);
        chart.getYAxis().setMaxWidth(yAxisWidth);
    }

    private void rebuildChart() {
        getChildren().clear();
        getChildren().add(resizeBaseChart(baseChart));
        for (XYChart chart : backgroundCharts) {
            getChildren().add(resizeBackgroundChart(chart));
        }
    }

    private Node resizeBaseChart(XYChart chart) {
        HBox hBox = new HBox(chart);
        hBox.setAlignment(Pos.TOP_LEFT);
        hBox.prefHeightProperty().bind(heightProperty());
        hBox.prefWidthProperty().bind(widthProperty());
        chart.minWidthProperty().bind(widthProperty().subtract((yAxisWidth + yAxisSeparation) * backgroundCharts.size()));
        chart.prefWidthProperty().bind(widthProperty().subtract((yAxisWidth + yAxisSeparation) * backgroundCharts.size()));
        chart.maxWidthProperty().bind(widthProperty().subtract((yAxisWidth + yAxisSeparation) * backgroundCharts.size()));
        return chart;
    }

    private Node resizeBackgroundChart(XYChart chart) {
        HBox hBox = new HBox(chart);
        hBox.setAlignment(Pos.TOP_LEFT);
        hBox.prefHeightProperty().bind(heightProperty());
        hBox.prefWidthProperty().bind(widthProperty());
        hBox.setMouseTransparent(true);
        chart.minWidthProperty().bind(widthProperty().subtract((yAxisWidth + yAxisSeparation) * backgroundCharts.size()));
        chart.prefWidthProperty().bind(widthProperty().subtract((yAxisWidth + yAxisSeparation) * backgroundCharts.size()));
        chart.maxWidthProperty().bind(widthProperty().subtract((yAxisWidth + yAxisSeparation) * backgroundCharts.size()));
//        chart.translateXProperty().bind(baseChart.getYAxis().widthProperty());
//        chart.getYAxis().setTranslateX((yAxisWidth + yAxisSeparation) * backgroundCharts.indexOf(chart));
        chart.setTranslateY(5);
        return hBox;
    }

    public void createBackgroundChart(ObservableList<XYChart.Series> series, String color, String chartType) {
        NumberAxis yAxis = new NumberAxis();
        NumberAxis xAxis = new NumberAxis();
        // style x-axis
        xAxis.setAutoRanging(false);
        xAxis.setVisible(false);
        xAxis.setOpacity(0.0);
//         style y-axis
        yAxis.setVisible(false);
        yAxis.setOpacity(0.0);
//         somehow the upper setVisible does not work
        xAxis.lowerBoundProperty().bind(((NumberAxis) baseChart.getXAxis()).lowerBoundProperty());
        xAxis.upperBoundProperty().bind(((NumberAxis) baseChart.getXAxis()).upperBoundProperty());
        xAxis.tickUnitProperty().bind(((NumberAxis) baseChart.getXAxis()).tickUnitProperty());
        // create chart
        XYChart chart = null;
        if ("CurveFittedAreaChart".equalsIgnoreCase(chartType)) {
            chart = buildAreaBackgroundChart(xAxis, yAxis);
        } else {
            chart = new LineChart(xAxis, yAxis);
        }
        chart.setAnimated(false);
        chart.setLegendVisible(false);
        chart.getData().addAll(series);
        styleBackgroundChart(chart, color);
        setFixedAxisWidth(chart);
        backgroundCharts.add(chart);
    }

    private XYChart buildAreaBackgroundChart(NumberAxis xAxis, NumberAxis yAxis) {
        XYChart chart = new CurveFittedAreaChart(xAxis, yAxis);
        return chart;
    }

    private void styleBaseChart(XYChart chart) {
        chart.setLegendVisible(false);
        chart.getXAxis().setAutoRanging(false);
        chart.getXAxis().setAnimated(false);
        chart.getYAxis().setAnimated(false);
    }

    private void styleBackgroundChart(XYChart chart, String color) {
        if (chart instanceof LineChart) {
            styleChartLine(chart, color);
        }
        if (chart instanceof AreaChart) {
            styleChartArea(chart, color);
        }
        Node contentBackground = chart.lookup(".chart-content").lookup(".chart-plot-background");
        contentBackground.setStyle("-fx-background-color: transparent; -fx-border-width: 0px");
        chart.setVerticalZeroLineVisible(false);
        chart.setHorizontalZeroLineVisible(false);
        chart.setVerticalGridLinesVisible(false);
        chart.setHorizontalGridLinesVisible(false);
    }

    private void styleChartLine(XYChart chart, String color) {
        Set<Node> seriesLines = chart.lookupAll(".chart-series-line");
        Set<Node> dataNodes = chart.lookupAll(".chart-line-symbol");
        seriesLines.forEach(seriesLine -> {
            seriesLine.setStyle("-fx-stroke: " + color);
        });
        dataNodes.forEach(dataNode -> {
            dataNode.setStyle("-fx-stroke: " + color);
        });
    }

    private void styleChartArea(XYChart chart, String color) {
        Set<Node> seriesLines = chart.lookupAll(".chart-series-area-line");
        Set<Node> seriesFills = chart.lookupAll(".chart-series-area-fill");
        seriesLines.forEach(seriesLine -> {
            seriesLine.setStyle("-fx-stroke: " + color);
        });
        seriesFills.forEach(seriesFill -> {
            seriesFill.setStyle("-fx-fill: " + color);
        });
    }

    public List<XYChart> getAllChart() {
        List<XYChart> charts = Lists.newArrayList();
        charts.add(baseChart);
        charts.addAll(backgroundCharts);
        return charts;
    }
}



