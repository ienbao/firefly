package com.dmsoft.firefly.plugin.spc.charts;

import com.dmsoft.firefly.plugin.spc.charts.annotation.AnnotationFetch;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.*;
import com.dmsoft.firefly.sdk.utils.ColorUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by cherry on 2018/2/7.
 */
public class LinearChart<X, Y> extends LineChart<X, Y> {

    private Map<String, ValueMarker> valueMarkerMap = Maps.newHashMap();
    private Map<String, PathMarker> pathMarkerMap = Maps.newHashMap();
    private Map<String, XYChart.Series> seriesMap = Maps.newHashMap();
    private Map<XYChart.Series, Color> seriesColorMap = Maps.newHashMap();

    private boolean showTooltip = true;

    /**
     * Construct a new LinearChart with the given axis.
     *
     * @param xAxis
     * @param yAxis
     */
    public LinearChart(Axis<X> xAxis, Axis<Y> yAxis) {

        super(xAxis, yAxis, FXCollections.observableArrayList());
    }

    /**
     * @param xyChartData
     */
//    public void addDataToChart(List<IXYChartData> xyChartData,
//                               Function<PointTooltip, String> pointTooltipFunction) {
//
//        xyChartData.forEach(xyOneChartData -> {
//            this.createChartSeries(xyOneChartData, pointTooltipFunction);
//        });
//    }

    /**
     * Create chart series
     *
     * @param xyOneChartData
     * @param pointTooltipFunction
     */
    public void createChartSeries(IXYChartData<X, Y> xyOneChartData,
                                  String unique,
                                  Function<PointTooltip, String> pointTooltipFunction) {

        XYChart.Series oneSeries = this.buildSeries(xyOneChartData);
        this.seriesMap.put(unique, oneSeries);
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

    public void addValueMarker(List<ILineData> lineData, String unique) {
        ValueMarker valueMarker = new ValueMarker();
        lineData.forEach(oneLineData -> {
            Line line = valueMarker.buildValueMarker(oneLineData);
            getPlotChildren().add(line);
        });
        valueMarkerMap.put(unique, valueMarker);
    }

    public void addPathMarker(List<IPathData> pathData, String unique) {
        PathMarker pathMarker = new PathMarker();
        pathData.forEach(onePathData -> {
            Path path = pathMarker.buildPathMarker(onePathData);
            getPlotChildren().add(path);
        });
        pathMarkerMap.put(unique, pathMarker);
    }

    public void toggleValueMarker(String lineName, boolean showed) {
        for (Map.Entry<String, ValueMarker> valueMarkerEntry : valueMarkerMap.entrySet()) {
            valueMarkerEntry.getValue().toggleValueMarker(lineName, showed);
        }
    }

    public void hiddenValueMarker(String lineName) {
        for (Map.Entry<String, ValueMarker> valueMarkerEntry : valueMarkerMap.entrySet()) {
            valueMarkerEntry.getValue().hiddenValueMarker(lineName);
        }
    }

    public void showValueMarker(String lineName) {
        for (Map.Entry<String, ValueMarker> valueMarkerEntry : valueMarkerMap.entrySet()) {
            valueMarkerEntry.getValue().showValueMarker(lineName);
        }
    }

    public void togglePathMarker(String pathName, boolean showed) {
        for (Map.Entry<String, PathMarker> pathMarkerEntry : pathMarkerMap.entrySet()) {
            pathMarkerEntry.getValue().togglePathMarker(pathName, showed);
        }
    }

    public void hiddenPathMarker(String pathName) {
        for (Map.Entry<String, PathMarker> pathMarkerEntry : pathMarkerMap.entrySet()) {
            pathMarkerEntry.getValue().hiddenPathMarker(pathName);
        }
    }

    public void showPathMarker(String pathName) {
        for (Map.Entry<String, PathMarker> pathMarkerEntry : pathMarkerMap.entrySet()) {
            pathMarkerEntry.getValue().showPathMarker(pathName);
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

        super.layoutPlotChildren();
//        paint line
        for (Map.Entry<String, ValueMarker> valueMarkerEntry : valueMarkerMap.entrySet()) {
            valueMarkerEntry.getValue().paintValueMaker(this);
        }
        for (Map.Entry<String, PathMarker> pathMarkerEntry : pathMarkerMap.entrySet()) {
            pathMarkerEntry.getValue().paintPathMarker(this);
        }
    }

    public void removeAllChildren() {
        ObservableList<Node> nodes = getPlotChildren();
        getPlotChildren().removeAll(nodes);
        clearData();
        getData().setAll(FXCollections.observableArrayList());
    }

    private void clearData() {
        valueMarkerMap.clear();
        pathMarkerMap.clear();
        seriesColorMap.clear();
        seriesMap.clear();
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

    public void setSeriesAnnotationEvent(AnnotationFetch fetch) {

        ObservableList<Series<X, Y>> seriesObservableList = this.getData();
        seriesObservableList.forEach(series -> {
            ObservableList<Data<X, Y>> data = series.getData();
            data.forEach(dataItem -> {
                dataItem.getNode().setOnMouseClicked(event -> {
                    if (fetch != null && fetch.showedAnnotation()) {
                        String value = fetch.getValue(dataItem.getExtraValue());
                        setNodeAnnotation(dataItem, value, fetch.getTextColor());
                        fetch.addData(dataItem);
                    }
                });
            });
        });
    }

    private void setNodeAnnotation(Data<X, Y> data, String value, String textColor) {

        data.getNode().getStyleClass().clear();
        data.getNode().getStyleClass().add("chart-symbol-triangle");
        StackPane pane = (StackPane) data.getNode();
        pane.setAlignment(Pos.BOTTOM_CENTER);
        if (!pane.getChildren().isEmpty()) {
            for (int i = 0; i < pane.getChildren().size(); i++) {
                Node node = pane.getChildren().get(i);
                if (node instanceof Text) {
                    Text oldText = (Text) node;
                    oldText.setText(value);
                    oldText.setStyle("-fx-fill: " + textColor);
                    return;
                }
            }
        }
        Text text = new Text(value);
        text.setStyle(" -fx-fill: " + textColor);
        pane.getChildren().add(text);
        pane.setMargin(text, new Insets(0, 0, 10, 0));
    }

    public void clearAnnotation(List<Data<X, Y>> data) {
        data.forEach(dateItem -> {
            dateItem.getNode().getStyleClass().clear();
            dateItem.getNode().getStyleClass().add("chart-line-symbol");
            StackPane pane = (StackPane) dateItem.getNode();
            if (pane.getChildren().isEmpty()) {
                return;
            }
            for (int i = 0; i < pane.getChildren().size(); i++) {
                Node node = pane.getChildren().get(i);
                if (node instanceof Text) {
                    pane.getChildren().remove(node);
                }
            }
        });
    }

    public void setSeriesDataStyleByRule(XYChart.Series<X, Y> series,
                                         List<String> ruleNames,
                                         Function<PointRule, PointStyle> pointRuleSetFunction) {

        series.getData().forEach(dataItem -> {
            PointRule pointRule = new PointRule(dataItem);
            if (seriesColorMap.containsKey(series)) {
                Color color = seriesColorMap.get(series);
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

    private void setSeriesDataStyleByDefault(XYChart.Series series, Color color, boolean showLined) {

        ObservableList<Data<X, Y>> data = series.getData();
        String seriesClass = "chart-series-hidden-line";
        if (!showLined) {
            series.getNode().getStyleClass().add(seriesClass);
        }
        series.getNode().setStyle("-fx-stroke: " + ColorUtils.toHexFromFXColor(color));
        data.forEach(dataItem -> {
            dataItem.getNode().setStyle("-fx-background-color: " + ColorUtils.toHexFromFXColor(color));
        });
    }

    public void setShowTooltip(boolean showTooltip) {
        this.showTooltip = showTooltip;
    }

    public void updateChartColor(String unique, Color color) {
//        update chart color
        if (seriesMap.containsKey(unique)) {
            setSeriesDataStyleByDefault(seriesMap.get(unique), color, true);
        }
//        update value maker color
        if (valueMarkerMap.containsKey(unique)) {
            ValueMarker valueMarker = valueMarkerMap.get(unique);
            valueMarker.updateAllLineColor(color);
        }
//        update path marker color
        if (pathMarkerMap.containsKey(unique)) {
            PathMarker pathMarker = pathMarkerMap.get(unique);
            pathMarker.updateAllLineColor(color);
        }
    }
}
