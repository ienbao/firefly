package com.dmsoft.firefly.plugin.spc.charts;

import com.dmsoft.firefly.plugin.spc.charts.annotation.AnnotationFetch;
import com.dmsoft.firefly.plugin.spc.charts.data.XYChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.*;
import com.dmsoft.firefly.plugin.spc.charts.annotation.AnnotationNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by cherry on 2018/2/7.
 */
public class LinearChart<X, Y, Z> extends LineChart<X, Y> {

    public final static Orientation horizontalType = Orientation.HORIZONTAL;
    public final static Orientation verticalType = Orientation.VERTICAL;
    private ObservableList<AnnotationNode> annotationNodes;
    private ObservableList<Data<X, Y>> horizontalMarkers;
    private ObservableList<Data<X, Y>> verticalMarkers;

    private Map<XYChart.Series, String> seriesColorMap = Maps.newHashMap();
    private Map<XYChart.Data, Boolean> dataHasAnnotationMap = Maps.newHashMap();
    private Map<XYChart.Data, AnnotationNode> dataAnnotationMap = Maps.newHashMap();

    private Map<String, Line> lineMap = Maps.newHashMap();

    private boolean showTooltip = true;
    private boolean showAnnotation = false;

    private String textColor = "#e92822";

    /* This will be our update listener, to be invoked whenever the chart changes or annotations are added */
    private final InvalidationListener listener = new InvalidationListener() {
        @Override
        public void invalidated(final Observable observable) {
            if (showAnnotation) {
                update();
            }
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
        horizontalMarkers = FXCollections.observableArrayList(d -> new Observable[]{d.YValueProperty()});
        verticalMarkers = FXCollections.observableArrayList(d -> new Observable[]{d.YValueProperty()});
        annotationNodes = FXCollections.observableArrayList();
        annotationNodes.addListener(listener);
    }

    /**
     * @param xyChartData
     */
    public void addDataToChart(List<XYChartData> xyChartData,
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
    public void createChartSeries(XYChartData<X, Y, Z> xyOneChartData,
                                  Function<PointTooltip, String> pointTooltipFunction) {

        XYChart.Series oneSeries = this.buildSeries(xyOneChartData);
        this.getData().add(oneSeries);
        this.setSeriesDataStyleByDefault(oneSeries, xyOneChartData.getColor(), true);
        this.setSeriesDataTooltip(oneSeries, pointTooltipFunction);
        this.seriesColorMap.put(oneSeries, xyOneChartData.getColor());
    }

    private XYChart.Series buildSeries(XYChartData<X, Y, Z> xyOneChartData) {

        XYChart.Series oneSeries = new XYChart.Series();
        oneSeries.setName(xyOneChartData.getCurrentGroupKey());
        X[] x = xyOneChartData.getX();
        Y[] y = xyOneChartData.getY();
        Z[] ids = xyOneChartData.getIds();
        for (int i = 0; i < x.length; i++) {
            XYChart.Data data = new XYChart.Data<>(x[i], y[i]);
            Object extraValue = ids != null && ids.length < i ? "" : ids[i];
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

    public void createBrokenLineData(BrokenLineData<Y> brokenLineData) {

        XYChart.Series oneSeries = new XYChart.Series();
        String seriesName = brokenLineData.getName();
        oneSeries.setName(seriesName);
        Y[] y = brokenLineData.getValue();
        for (int i = 0; i < y.length; i++) {
            XYChart.Data data = new XYChart.Data<>(i + 1, y[i]);
            oneSeries.getData().add(data);
        }
        this.getData().add(oneSeries);
        this.setSeriesDataStyleByDefault(oneSeries, brokenLineData.getColor(), true);
    }

    public void createAbnormalPointData(AbnormalPointData<X, Y> abnormalPointData) {

        String seriesName = abnormalPointData.getName();
        XYChart.Series oneSeries = new XYChart.Series();
        oneSeries.setName(seriesName);
        X[] x = abnormalPointData.getX();
        Y[] y = abnormalPointData.getY();
        for (int i = 0; i < x.length; i++) {
            XYChart.Data data = new XYChart.Data<>(x[i], y[i]);
            oneSeries.getData().add(data);
        }
        this.getData().add(oneSeries);
        this.setSeriesDataStyleByDefault(oneSeries, abnormalPointData.getColor(), false);
    }

    public void addValueMarker(LineData lineData) {

        Line line = new Line();
        Orientation orientationType = lineData.getPlotOrientation();
        XYChart.Data marker = horizontalType == orientationType ?
                new XYChart.Data(0, lineData.getValue()) :
                new XYChart.Data(lineData.getValue(), 0);
        marker.setNode(line);
        getPlotChildren().add(line);
        lineMap.put(lineData.getName(), line);

        if (horizontalType == orientationType) {
            horizontalMarkers.add(marker);
        }
        if (verticalType == orientationType) {
            verticalMarkers.add(marker);
        }
        if (lineData.getLineClass() != null) {
            line.getStyleClass().setAll("line", lineData.getLineClass());
        }
        if (lineData.getColor() != null) {
            line.setStyle("-fx-stroke:" + lineData.getColor());
        }

        line.setOnMouseEntered(event -> {
            Tooltip tooltip = new Tooltip(lineData.getTitle() + "\n"
                    + lineData.getName() + "=" + lineData.getValue());
            Tooltip.install(line, tooltip);
        });
    }

    public void toggleValueMarker(String lineName, boolean showed) {

        if (showed) {
            showValueMarker(lineName);
        } else {
            hiddenValueMarker(lineName);
        }
    }

    public void hiddenValueMarker(String lineName) {

        if (lineMap.containsKey(lineName)) {
            lineMap.get(lineName).getStyleClass().add("hidden-line");
        }
    }

    public void showValueMarker(String lineName) {

        if (lineMap.containsKey(lineName)) {
            lineMap.get(lineName).getStyleClass().remove("hidden-line");
        }
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

//        Draw stroke chart
        super.layoutPlotChildren();

//        Draw horizontal markers
        for (Data<X, Y> horizontalMarker : horizontalMarkers) {
            double lower = ((ValueAxis) getXAxis()).getLowerBound();
            double upper = ((ValueAxis) getXAxis()).getUpperBound();
            X lowerX = getXAxis().toRealValue(lower);
            X upperX = getXAxis().toRealValue(upper);
            Line line = (Line) horizontalMarker.getNode();
            line.setStartX(getXAxis().getDisplayPosition(lowerX));
            line.setEndX(getXAxis().getDisplayPosition(upperX));
            line.setStartY(getYAxis().getDisplayPosition(horizontalMarker.getYValue()));
            line.setEndY(line.getStartY());
        }

//        Draw vertical markers
        for (Data<X, Y> verticalMarker : verticalMarkers) {
            double lower = ((ValueAxis) getYAxis()).getLowerBound();
            double upper = ((ValueAxis) getYAxis()).getUpperBound();
            Y lowerY = getYAxis().toRealValue(lower);
            Y upperY = getYAxis().toRealValue(upper);
            Line line = (Line) verticalMarker.getNode();
            line.setStartX(getXAxis().getDisplayPosition(verticalMarker.getXValue()));
            line.setEndX(line.getStartX());
            line.setStartY((getYAxis()).getDisplayPosition(lowerY));
            line.setEndY(getYAxis().getDisplayPosition(upperY));
        }
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
                seriesName + "\n(" + data.getXValue().toString() + ", " + data.getYValue() + ")");
        Tooltip.install(data.getNode(), tooltip);
    }

    public void setSeriesAnnotationEvent(XYChart.Series series,
                                         String color,
                                         AnnotationFetch fetch) {

        ObservableList<Data<X, Y>> data = series.getData();
        data.forEach(dataItem -> {
            dataItem.getNode().setOnMouseClicked(event -> {
                String value = "default";
                if (fetch != null) {
                    fetch.getValue(dataItem.getExtraValue());
                }
                if (!dataHasAnnotationMap.containsKey(dataItem) || !dataHasAnnotationMap.get(dataItem)) {
                    addAnnotation(dataItem, value, color);
                } else {
                    removeAnnotation(dataItem);
                }
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
