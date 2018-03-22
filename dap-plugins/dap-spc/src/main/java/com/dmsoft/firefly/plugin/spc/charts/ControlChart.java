package com.dmsoft.firefly.plugin.spc.charts;

import com.dmsoft.firefly.plugin.spc.charts.annotation.AnnotationFetch;
import com.dmsoft.firefly.plugin.spc.charts.data.ChartTooltip;
import com.dmsoft.firefly.plugin.spc.charts.data.ControlChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.*;
import com.dmsoft.firefly.plugin.spc.charts.utils.PointClickCallBack;
import com.dmsoft.firefly.sdk.utils.ColorUtils;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
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
 * Created by cherry on 2018/3/20.
 */
public class ControlChart<X, Y> extends LineChart {
    //    折线
    private Map<String, PathMarker> pathMarkerMap = Maps.newHashMap();
    //    unique key---直线
    private Map<String, ValueMarker> valueMarkerMap = Maps.newHashMap();
    //    图层---颜色
    private Map<XYChart.Series, Color> seriesColorMap = Maps.newHashMap();
    //    unique key----图层
    private Map<String, XYChart.Series> seriesUniqueKeyMap = Maps.newHashMap();
    //    series name----预警规则
    private Map<String, Function<PointRule, PointStyle>> seriesPointRuleMap = Maps.newHashMap();

    //    point click callback
    private PointClickCallBack pointClickCallBack;
    //    whether active point click event
    private boolean pointClick = false;

    /**
     * @param xAxis xAxis
     * @param yAxis yAxis
     */
    public ControlChart(Axis<Number> xAxis, Axis<Number> yAxis) {
        super(xAxis, yAxis, FXCollections.observableArrayList());
        super.setLegendVisible(false);
        super.setAnimated(false);
    }

    /**
     * Set chart data
     *
     * @param controlChartDataList control chart data list
     * @param chartTooltip         chart data tooltip
     */
    public void setData(List<ControlChartData> controlChartDataList, ChartTooltip chartTooltip) {
        this.removeAllChildren();
        if (controlChartDataList == null) {
            return;
        }
        controlChartDataList.forEach(controlChartData -> createChartSeriesData(controlChartData, chartTooltip));
    }

    /**
     * Update chart node color
     *
     * @param unique unique key
     * @param color  color
     */
    public void updateChartColor(String unique, Color color) {
//        update chart color
        if (seriesUniqueKeyMap.containsKey(unique)) {
            this.updateNodeColor(seriesUniqueKeyMap.get(unique), color);
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

    /**
     * Clear annotation
     *
     * @param data data
     */
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

    /**
     * Toggle series path show or hide
     *
     * @param series series name
     * @param showed whether it show or not
     */
    public void toggleSeriesLine(XYChart.Series<X, Y> series, boolean showed) {
        if (!showed) {
            series.getNode().getStyleClass().add("chart-series-hidden-line");
        } else {
            series.getNode().getStyleClass().remove("chart-series-hidden-line");
        }
    }

    /**
     * Toggle point show or hide
     *
     * @param data   data
     * @param showed whether it show or not
     */
    public void toggleSeriesPoint(XYChart.Data<X, Y> data, boolean showed) {
        if (!showed) {
            data.getNode().getStyleClass().add("chart-line-hidden-symbol");
        } else {
            data.getNode().getStyleClass().remove("chart-line-hidden-symbol");
        }
    }

    /**
     * Toggle line show or hide
     *
     * @param lineName line name
     * @param showed   whether it show or not
     */
    public void toggleValueMarker(String lineName, boolean showed) {
        for (Map.Entry<String, ValueMarker> valueMarkerEntry : valueMarkerMap.entrySet()) {
            valueMarkerEntry.getValue().toggleValueMarker(lineName, showed);
        }
    }

    /**
     * Toggle shoe path value or not
     *
     * @param pathName path name
     * @param showed   whether it show or not
     */
    public void togglePathMarker(String pathName, boolean showed) {
        for (Map.Entry<String, PathMarker> valueMarkerEntry : pathMarkerMap.entrySet()) {
            valueMarkerEntry.getValue().togglePathMarker(pathName, showed);
        }
    }

    /**
     * Set run chart R1-R9 rule style
     *
     * @param ruleNames rule names
     */
    public void setSeriesDataStyleByRule(List<String> ruleNames) {
        for (Map.Entry<String, XYChart.Series> seriesUniqueKeyEntry : seriesUniqueKeyMap.entrySet()) {
            XYChart.Series<X, Y> series = seriesUniqueKeyEntry.getValue();
            String uniqueKey = seriesUniqueKeyEntry.getKey();
            series.getData().forEach(dataItem -> {
                if (seriesColorMap.containsKey(seriesUniqueKeyEntry.getValue())) {
                    Color color = seriesColorMap.get(seriesUniqueKeyEntry.getValue());
                    PointRule pointRule = new PointRule(dataItem);
                    pointRule.setNormalColor(color);
                    pointRule.setActiveRule(ruleNames);
                    Function<PointRule, PointStyle> pointRuleSetFunction = seriesPointRuleMap.get(uniqueKey);
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
                }
            });
        }
    }

    /**
     * Set control chart more than ucl or less than lcl rule style
     *
     * @param uniqueKey series unique key
     * @param moreData  ucl data
     * @param lessData  lcl data
     */
    public void setSeriesDataStyleByRule(String uniqueKey, Double[] moreData, Double[] lessData) {
        if (!seriesUniqueKeyMap.containsKey(uniqueKey) || (moreData == null && lessData == null)) return;
        XYChart.Series<X, Y> series = seriesUniqueKeyMap.get(uniqueKey);
        for (int i = 0; i < series.getData().size(); i++) {
            Y data = series.getData().get(i).getYValue();
            if (data instanceof Double) {
                Double value = (Double) data;
                boolean flagMoreValid = moreData != null && !DAPStringUtils.isInfinityAndNaN(moreData[i]) && value > moreData[i];
                boolean flagLessValid = lessData != null && !DAPStringUtils.isInfinityAndNaN(lessData[i]) && value < lessData[i];
                if (flagLessValid || flagMoreValid) {
                    series.getData().get(i).getNode().setStyle("-fx-background-color: " + ColorUtils.toHexFromFXColor(Color.RED));
                }
            }
        }
    }

    /**
     * Set chart all series annotation
     *
     * @param fetch
     */
    public void setSeriesAnnotationEvent(AnnotationFetch fetch) {
        ObservableList<Series<X, Y>> seriesObservableList = this.getData();
        seriesObservableList.forEach(series -> {
            ObservableList<Data<X, Y>> data = series.getData();
            data.forEach(dataItem -> dataItem.getNode().setOnMouseClicked(event -> {
                if (fetch != null && fetch.showedAnnotation()) {
                    String value = fetch.getValue(dataItem.getExtraValue());
                    setNodeAnnotation(dataItem, value, fetch.getTextColor());
                    fetch.addData(dataItem);
                }
            }));
        });
    }

    /**
     * Remove all chart node and clear chart data
     */
    public void removeAllChildren() {
        this.seriesUniqueKeyMap.clear();
        this.seriesPointRuleMap.clear();
        this.seriesColorMap.clear();
        for (Map.Entry<String, ValueMarker> valueMarkerEntry : valueMarkerMap.entrySet()) {
            valueMarkerEntry.getValue().clear();
        }
        for (Map.Entry<String, PathMarker> pathMarkerEntry : pathMarkerMap.entrySet()) {
            pathMarkerEntry.getValue().clear();
        }
        this.valueMarkerMap.clear();
        this.pathMarkerMap.clear();
        this.getData().setAll(FXCollections.observableArrayList());
        this.getPlotChildren().removeAll(this.getPlotChildren());
    }

    /**
     * Active point click fire event
     *
     * @param flag if true
     */
    public void activePointClickEvent(boolean flag) {
        this.pointClick = flag;
    }

    private void createChartSeriesData(ControlChartData controlChartData, ChartTooltip chartTooltip) {
//        1. 设置画图数据, 图的颜色、样式、悬浮提示
//        2. 设置画直线数据， 线的颜色，样式
//        3. 设置画折线数据， 折线的颜色，样式
        if (controlChartData == null) return;
        Color color = controlChartData.getColor();
        String seriesName = controlChartData.getSeriesName();
        String uniqueKey = controlChartData.getUniqueKey();
        List<ILineData> lineDataList = controlChartData.getLineData();
        List<IPathData> pathDataList = controlChartData.getBrokenLineData();
        Function<PointRule, PointStyle> rulePointStyleFunction = controlChartData.getPointFunction();
//        Build series data
        XYChart.Series series = this.buildSeries(controlChartData.getXyOneChartData(), seriesName);
//        Set series for chart
        this.getData().add(series);

        if (pointClick) {
            this.dataClickEvent(series);
        }
//        Set chart series and data color, data tooltip
        this.setDataNodeStyleAndTooltip(series, color, chartTooltip == null ? null : chartTooltip.getChartPointTooltip());
//        Set chart line
        if (lineDataList != null) {
            ValueMarker valueMarker = new ValueMarker();
            lineDataList.forEach(oneLineData -> {
                Line line = valueMarker.buildValueMarker(oneLineData, color, seriesName, chartTooltip == null ? null : chartTooltip.getLineTooltip());
                getPlotChildren().add(line);
            });
            valueMarkerMap.put(uniqueKey, valueMarker);
        }
//       Set chart path
        if (pathDataList != null) {
            PathMarker pathMarker = new PathMarker();
            pathDataList.forEach(onePathData -> {
                Path path = pathMarker.buildPathMarker(onePathData);
                getPlotChildren().add(path);
            });
            pathMarkerMap.put(uniqueKey, pathMarker);
        }

        this.seriesUniqueKeyMap.put(uniqueKey, series);
        this.seriesColorMap.put(series, color);
        this.seriesPointRuleMap.put(uniqueKey, rulePointStyleFunction);
    }

    private XYChart.Series buildSeries(IXYChartData<X, Y> xyOneChartData, String seriesName) {
        XYChart.Series oneSeries = new XYChart.Series();
        oneSeries.setName(seriesName);
        int length = xyOneChartData.getLen();
        for (int i = 0; i < length; i++) {
            X xValue = xyOneChartData.getXValueByIndex(i);
            Y yValue = xyOneChartData.getYValueByIndex(i);
            if (xValue == null || yValue == null) {
                continue;
            }
            XYChart.Data data = new XYChart.Data<>(xValue, yValue);
            Object extraValue = xyOneChartData.getExtraValueByIndex(i) == null ? "" :
                    xyOneChartData.getExtraValueByIndex(i);
            data.setExtraValue(extraValue);
            oneSeries.getData().add(data);
        }
        return oneSeries;
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

    private void updateNodeColor(XYChart.Series series, Color color) {

        ObservableList<Data<X, Y>> data = series.getData();
//        set series node color
        if (DAPStringUtils.isNotBlank(ColorUtils.toHexFromFXColor(color))) {
            series.getNode().setStyle("-fx-stroke: " + ColorUtils.toHexFromFXColor(color));
        }
        data.forEach(dataItem -> {
//            set data node color
            if (DAPStringUtils.isNotBlank(ColorUtils.toHexFromFXColor(color))) {
                dataItem.getNode().setStyle("-fx-background-color: " + ColorUtils.toHexFromFXColor(color));
            }
        });
    }

    private void setDataNodeStyleAndTooltip(XYChart.Series series,
                                            Color color,
                                            Function<PointTooltip, String> pointTooltipFunction) {

        ObservableList<Data<X, Y>> data = series.getData();
//        set series node color
        if (DAPStringUtils.isNotBlank(ColorUtils.toHexFromFXColor(color))) {
            series.getNode().setStyle("-fx-stroke: " + ColorUtils.toHexFromFXColor(color));
        }
        data.forEach(dataItem -> {
//            set data node color
            if (DAPStringUtils.isNotBlank(ColorUtils.toHexFromFXColor(color))) {
                dataItem.getNode().setStyle("-fx-background-color: " + ColorUtils.toHexFromFXColor(color));
            }
//            set data node tooltip
            if (pointTooltipFunction != null) {
                String content = pointTooltipFunction.apply(new PointTooltip(series.getName(), dataItem));
                Tooltip.install(dataItem.getNode(), new Tooltip(content));
            }
        });
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

    private void dataClickEvent(XYChart.Series<X, Y> series) {
        series.getData().forEach(oneData -> oneData.getNode().setOnMouseClicked(event -> {
            if (pointClickCallBack != null) {
                pointClickCallBack.execute(oneData.getExtraValue());
            }
        }));
    }

    public void setPointClickCallBack(PointClickCallBack pointClickCallBack) {
        this.pointClickCallBack = pointClickCallBack;
    }
}
