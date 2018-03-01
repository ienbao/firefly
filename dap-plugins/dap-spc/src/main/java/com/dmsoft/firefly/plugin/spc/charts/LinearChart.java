package com.dmsoft.firefly.plugin.spc.charts;

import com.dmsoft.firefly.plugin.spc.charts.annotation.AnnotationFetch;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.*;
import com.dmsoft.firefly.plugin.spc.charts.annotation.AnnotationNode;
import com.google.common.collect.Maps;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by cherry on 2018/2/7.
 */
public class LinearChart<X, Y> extends LineChart<X, Y> {

    private ValueMarker valueMarker = new ValueMarker();

    private Map<XYChart.Series, String> seriesColorMap = Maps.newHashMap();
    private Map<XYChart.Data, Boolean> dataHasAnnotationMap = Maps.newHashMap();
    private Map<XYChart.Data, AnnotationNode> dataAnnotationMap = Maps.newHashMap();
    private ObservableList<AnnotationNode> annotationNodes = FXCollections.observableArrayList();

    private boolean showTooltip = true;
    private boolean showAnnotation = false;

    private String textColor = "#e92822";

    /* This will be our update listener, to be invoked whenever the chart changes or annotations are added */
    private final InvalidationListener listener = observable -> {
        if (showAnnotation) {
            update();
        }
    };

    /**
     * Construct a new LinearChart with the given axis.
     *
     * @param xAxis
     * @param yAxis
     */
    public LinearChart(Axis<X> xAxis, Axis<Y> yAxis) {

        super(xAxis, yAxis, FXCollections.observableArrayList());
        annotationNodes.addListener(listener);
    }

    /**
     * @param xyChartData
     */
    public void addDataToChart(List<IXYChartData> xyChartData,
                               Function<PointTooltip, String> pointTooltipFunction) {

        xyChartData.forEach(xyOneChartData -> {
            this.createChartSeries(xyOneChartData, pointTooltipFunction);
        });
    }

    /**
     * Create chart series
     *
     * @param xyOneChartData
     * @param pointTooltipFunction
     */
    public void createChartSeries(IXYChartData<X, Y> xyOneChartData,
                                  Function<PointTooltip, String> pointTooltipFunction) {

        XYChart.Series oneSeries = this.buildSeries(xyOneChartData);
        this.getData().add(oneSeries);
        this.setSeriesDataStyleByDefault(oneSeries, xyOneChartData.getColor(), true);
        this.setSeriesDataTooltip(oneSeries, pointTooltipFunction);
        this.seriesColorMap.put(oneSeries, xyOneChartData.getColor());
    }

    private XYChart.Series buildSeries(IXYChartData<X, Y> xyOneChartData) {

        XYChart.Series oneSeries = new XYChart.Series();
        oneSeries.setName(xyOneChartData.getSeriesName());
        int length = xyOneChartData.getLen();
        for (int i = 0; i < length; i++) {
            X xValue = xyOneChartData.getXValueByIndex(i);
            Y yValue = xyOneChartData.getYValueByIndex(i);
            if (xValue == null || yValue == null) {
                continue;
            }
            XYChart.Data data = new XYChart.Data<>(xValue, yValue);
            Object extraValue = xyOneChartData.getExtraValueByIndex(i) != null ? "" :
                    xyOneChartData.getExtraValueByIndex(i);
            data.setExtraValue(extraValue);
            oneSeries.getData().add(data);
        }
        return oneSeries;
    }

    private void setSeriesDataTooltip(XYChart.Series<X, Y> series,
                                      Function<PointTooltip, String> pointTooltipFunction) {

        if (!showTooltip) {
            return;
        }
        series.getData().forEach(dataItem -> {
            if (pointTooltipFunction == null) {
                this.populateTooltip(series, dataItem);
            } else {
                String content = pointTooltipFunction.apply(new PointTooltip(series.getName(), dataItem));
                Tooltip.install(dataItem.getNode(), new Tooltip(content));
            }
        });
    }

//    public void createBrokenLineData(BrokenLineData<Y> brokenLineData) {
//
//        XYChart.Series oneSeries = new XYChart.Series();
//        String seriesName = brokenLineData.getName();
//        oneSeries.setName(seriesName);
//        Y[] y = brokenLineData.getValue();
//        for (int i = 0; i < y.length; i++) {
//            XYChart.Data data = new XYChart.Data<>(i + 1, y[i]);
//            oneSeries.getData().add(data);
//        }
//        this.getData().add(oneSeries);
//        this.setSeriesDataStyleByDefault(oneSeries, brokenLineData.getColor(), true);
//    }

//    public void createAbnormalPointData(AbnormalPointData<X, Y> abnormalPointData) {
//
//        String seriesName = abnormalPointData.getName();
//        XYChart.Series oneSeries = new XYChart.Series();
//        oneSeries.setName(seriesName);
//        X[] x = abnormalPointData.getX();
//        Y[] y = abnormalPointData.getY();
//        for (int i = 0; i < x.length; i++) {
//            XYChart.Data data = new XYChart.Data<>(x[i], y[i]);
//            oneSeries.getData().add(data);
//        }
//        this.getData().add(oneSeries);
//        this.setSeriesDataStyleByDefault(oneSeries, abnormalPointData.getColor(), false);
//    }

    public void addValueMarker(LineData lineData) {

        Line line = valueMarker.buildValueMarker(lineData);
        getPlotChildren().add(line);
    }

    public void toggleValueMarker(String lineName, boolean showed) {

        valueMarker.toggleValueMarker(lineName, showed);
    }

    public void hiddenValueMarker(String lineName) {

        valueMarker.hiddenValueMarker(lineName);
    }

    public void showValueMarker(String lineName) {

        valueMarker.showValueMarker(lineName);
    }

    public void toggleSeriesLine(XYChart.Series<X, Y> series, boolean showed) {

        if (!showed) {
            series.getNode().getStyleClass().add("chart-series-hidden-line");
        } else {
            series.getNode().getStyleClass().remove("chart-series-hidden-line");
        }
    }

    public void toggleSeriesPoint(XYChart.Data<X, Y> data, boolean showed) {

        if (!showed) {
            data.getNode().getStyleClass().add("chart-line-hidden-symbol");
        } else {
            data.getNode().getStyleClass().remove("chart-line-hidden-symbol");
        }
    }

    @Override
    protected void layoutPlotChildren() {

        super.layoutPlotChildren();
//        paint line
        valueMarker.paintValueMaker(this);
    }

    /**
     * Populates the tooltip with chart
     */
    private void populateTooltips() {

        if (this.getData() == null) {
            return;
        }

        this.getData().forEach(series -> {
            series.getData().forEach(dataItem -> {
                populateTooltip(series, dataItem);
            });
        });
    }

    /**
     * Populates the tooltip with data (chart-type independent).
     *
     * @param series The series
     * @param data   The data
     */
    public void populateTooltip(final Series<X, Y> series, final Data<X, Y> data) {

        String seriesName = series.getName();
        Tooltip tooltip = new Tooltip(
                seriesName + "\n(" + data.getXValue() + ", " + data.getYValue() + ")");
        Tooltip.install(data.getNode(), tooltip);
    }

    public void setSeriesAnnotationEvent(XYChart.Series series,
                                         AnnotationFetch fetch) {

        ObservableList<Data<X, Y>> data = series.getData();
        data.forEach(dataItem -> {
            dataItem.getNode().setOnMouseClicked(event -> {

                dataItem.getNode().getStyleClass().remove("chart-line-symbol");
                dataItem.getNode().getStyleClass().add("chart-symbol-triangle");
                StackPane pane = (StackPane) dataItem.getNode();
                Text text = new Text("default");
                text.setStyle(" -fx-fill: " + textColor);
                pane.getChildren().add(text);
            });
        });
    }

    public void setSeriesDataStyleByRule(XYChart.Series<X, Y> series,
                                         List<String> ruleNames,
                                         Function<PointRule, PointStyle> pointRuleSetFunction) {


//        series.getNode().setStyle("-fx-stroke: " + color);
//        series.getNode().getStyleClass().add("chart-series-line");
        series.getData().forEach(dataItem -> {
            PointRule pointRule = new PointRule(dataItem);
            if (seriesColorMap.containsKey(series)) {
                String color = seriesColorMap.get(series);
                pointRule.setNormalColor(color);
            }
            pointRule.setActiveRule(ruleNames);
            PointStyle pointStyle = pointRuleSetFunction.apply(pointRule);
            dataItem.getNode().setStyle(pointStyle.getStyle());
            Set<String> classStyle = pointStyle.getClassStyle();
            if (classStyle != null) {
                classStyle.forEach(s -> {
                    if (!dataItem.getNode().getStyleClass().contains(s)) {
                        dataItem.getNode().getStyleClass().add(s);
                    }
                });
            }
        });
    }

    private void setSeriesDataStyleByDefault(XYChart.Series series, String color, boolean showLined) {

        ObservableList<Data<X, Y>> data = series.getData();
        String seriesClass = showLined ? "chart-series-line" : "chart-series-hidden-line";
        series.getNode().getStyleClass().add(seriesClass);
        series.getNode().setStyle("-fx-stroke: " + color);
        data.forEach(dataItem -> {
            dataItem.getNode().getStyleClass().add("chart-line-symbol");
            dataItem.getNode().setStyle("-fx-background-color: " + color);
        });
    }

    /**
     * Invoked whenever the chart changes or annotations are added. This basically does a relayout of the annotation nodes.
     */
    private void update() {
        Pane pane = (Pane) this.getParent();
        pane.getChildren().clear();
        pane.getChildren().add(this);
        final Axis<X> xAxis = this.getXAxis();
        final Axis<Y> yAxis = this.getYAxis();
        /* For each annotation, add a circle indicating the position and the custom node right next to it */
        for (AnnotationNode annotation : annotationNodes) {
            final double x = xAxis.localToParent(xAxis.getDisplayPosition((X) annotation.getX()), 0).getX() + this.getPadding().getLeft();
            final double y = yAxis.localToParent(0, yAxis.getDisplayPosition((Y) annotation.getY())).getY() + this.getPadding().getTop();
            final Node node = annotation.getNode();
            Polygon triangle = new Polygon();
            triangle.getPoints().addAll(x, y - 5, x - 5, y + 5, x + 5, y + 5);
            triangle.setStyle("-fx-fill: " + annotation.getColor());
            pane.getChildren().add(triangle);
            pane.getChildren().add(node);
            node.relocate(x, y - 15);
            node.autosize();
        }
    }

    /**
     * Add a new annotation for the given display coordinate.
     */
    private void addAnnotation(XYChart.Data<X, Y> value, String text, String color) {

        final Axis<X> xAxis = this.getXAxis();
        final Axis<Y> yAxis = this.getYAxis();
        X x = value.getXValue();
        Y y = value.getYValue();
        if (xAxis.isValueOnAxis(x) && yAxis.isValueOnAxis(y)) {
            Label label = new Label(text);
            label.setStyle("-fx-text-fill: " + textColor);
            AnnotationNode annotationNode = new AnnotationNode(label, color, x, y);
            annotationNodes.add(annotationNode);
            dataAnnotationMap.put(value, annotationNode);
            dataHasAnnotationMap.put(value, true);
            value.getNode().getStyleClass().add("chart-line-hidden-symbol");

//            final Text dataText = new Text(text);
//            annotationNode.getNode().parentProperty().addListener((ov, oldParent, parent) -> {
//                Group parentGroup = (Group) parent;
//                parentGroup.getChildren().add(dataText);
//            });

        }
    }

    private void removeAnnotation(XYChart.Data<X, Y> value) {

        value.getNode().getStyleClass().remove("chart-line-hidden-symbol");
        AnnotationNode annotationNode = dataAnnotationMap.get(value);
        dataHasAnnotationMap.put(value, false);
        annotationNodes.remove(annotationNode);
        dataAnnotationMap.remove(value);
    }

    public void setShowTooltip(boolean showTooltip) {
        this.showTooltip = showTooltip;
    }

    public void setShowAnnotation(boolean showAnnotation) {
        this.showAnnotation = showAnnotation;
    }
}
