package com.dmsoft.firefly.plugin.spc.charts;

import com.dmsoft.firefly.gui.components.chart.ChartOperatorUtils;
import com.dmsoft.firefly.plugin.spc.charts.annotation.AnnotationDataDto;
import com.dmsoft.firefly.plugin.spc.charts.annotation.AnnotationFetch;
import com.dmsoft.firefly.plugin.spc.charts.data.ChartTooltip;
import com.dmsoft.firefly.plugin.spc.charts.data.ControlChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.*;
import com.dmsoft.firefly.plugin.spc.charts.utils.MathUtils;
import com.dmsoft.firefly.plugin.spc.charts.utils.PointClickCallBack;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import com.dmsoft.firefly.sdk.utils.ColorUtils;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by cherry on 2018/3/20.
 */

/**
 * Control chart
 *
 * @param <X> x data class
 * @param <Y> y data class
 */
public class ControlChart<X, Y> extends LineChart {
    private Logger logger = LoggerFactory.getLogger(ControlChart.class);
    //    折线
    private Map<String, List<XYChart.Series<X, Y>>> pathMarkerMap = Maps.newHashMap();
    //    unique key---直线
    private Map<String, ValueMarker> valueMarkerMap = Maps.newHashMap();
    //    图层---颜色
    private Map<XYChart.Series, Color> seriesColorMap = Maps.newHashMap();
    //    unique key----图层
    private Map<String, XYChart.Series> seriesUniqueKeyMap = Maps.newHashMap();
    //    unique key----nodes
    private Map<String, List<Node>> uniqueKeyNodesMap = Maps.newHashMap();
    //    unique key----符合R规则的点
    private Map<String, List<XYChart.Data>> rRuleDataUniqueKeyMap = Maps.newHashMap();
    //    unique key----符合ucl,lcl的点
    private Map<String, List<XYChart.Data>> uclAndLclDataUniqueKeyMap = Maps.newHashMap();
    //    series name----预警规则
    private Map<String, Function<PointRule, PointStyle>> seriesPointRuleMap = Maps.newHashMap();

    //    point click callback
    private PointClickCallBack pointClickCallBack;
    //    whether active point click event
    private boolean pointClick = false;

    private Map<String, Boolean> lineTooltipShowMap = Maps.newHashMap();

    private final double ANCHOR_X = 10.0;
    private final double ANCHOR_Y = 15.0;

    /**
     * @param xAxis xAxis
     * @param yAxis yAxis
     */
    public ControlChart(Axis<Number> xAxis, Axis<Number> yAxis) {
        super(xAxis, yAxis, FXCollections.observableArrayList());
        super.setLegendVisible(false);
        super.setAnimated(false);
        super.setHorizontalZeroLineVisible(false);
        this.setHorizontalGridLinesVisible(false);
        super.setVerticalZeroLineVisible(false);
        this.setVerticalGridLinesVisible(false);
    }

    /**
     * Set chart data
     *
     * @param controlChartDataList control chart data list
     * @param chartTooltip         chart data tooltip
     */
    public void setData(List<ControlChartData> controlChartDataList, ChartTooltip chartTooltip) {
        logger.debug("Chart0");
        this.removeAllChildren();
        logger.debug("Chart1");
        if (controlChartDataList == null) {
            return;
        }
        setAxisRange(controlChartDataList);
        List<XYChart.Series> seriesResult = Lists.newArrayList();
        List<Line> lineResult = Lists.newArrayList();
        List<String> uniqueKey = Lists.newArrayList();
        List<Color> uniqueColor = Lists.newArrayList();
        List<String> seriesNames = Lists.newArrayList();
        controlChartDataList.forEach(controlChartData -> {
            ControlChartSeries series = createChartSeriesData(controlChartData, chartTooltip);
            if (series != null) {
                if (series.getPointSeries() != null) {
                    seriesResult.add(series.getPointSeries());
                }
                seriesResult.addAll(series.getPathSeries());
                lineResult.addAll(series.getConnectLine());
                uniqueKey.add(controlChartData.getUniqueKey());
                uniqueColor.add(controlChartData.getColor());
                seriesNames.add(controlChartData.getSeriesName());
            }
        });
        logger.debug("Chart2");
        this.getPlotChildren().addAll(lineResult);
        logger.debug("Chart3");
        this.getData().addAll(seriesResult);
        logger.debug("Chart4");
        for (int i = 0; i < uniqueKey.size(); i++) {
            Color color = uniqueColor.get(i);
            String seriesName = seriesNames.get(i);
            String key = uniqueKey.get(i);

            //init all nodes style and tooltip
            setDataNodeStyleAndTooltip(seriesUniqueKeyMap.get(key), color, chartTooltip == null ? null : chartTooltip.getChartPointTooltip());
            if (pathMarkerMap != null && pathMarkerMap.containsKey(uniqueKey.get(i))) {
                pathMarkerMap.get(key).forEach(series -> setPathNodeStyleAndTooltip(series, color, seriesName,
                        chartTooltip == null ? null : chartTooltip.getChartPointTooltip()));
            }
            //init unique key all nodes map
            List<Node> nodes = Lists.newArrayList();
            nodes.addAll(getSeriesNodes(Lists.newArrayList(seriesUniqueKeyMap.get(key))));
            if (pathMarkerMap != null && pathMarkerMap.containsKey(key)) {
                nodes.addAll(getSeriesNodes(pathMarkerMap.get(key)));
            }
            if (valueMarkerMap != null && valueMarkerMap.containsKey(key)) {
                nodes.addAll(valueMarkerMap.get(key).getAllLines());
            }
            addToUniqueKeyNodes(key, nodes);
        }

        logger.debug("Chart5");
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
            this.updateNodeColor(unique, color);
        }
//        update value maker color
        if (valueMarkerMap.containsKey(unique)) {
            ValueMarker valueMarker = valueMarkerMap.get(unique);
            valueMarker.updateAllLineColor(color);
        }
//        update path marker color
        if (pathMarkerMap.containsKey(unique)) {
            List<XYChart.Series<X, Y>> seriesList = pathMarkerMap.get(unique);
            seriesList.forEach(series -> this.updatePathColor(series, color));
        }
    }

    /**
     * Stick unique key series layer
     *
     * @param uniqueKey unique key
     */
    public void stickLayerToUniqueKey(String uniqueKey) {
        if (uniqueKeyNodesMap.isEmpty() || !uniqueKeyNodesMap.containsKey(uniqueKey)) {
            return;
        }
        ObservableList<Node> nodes = getPlotChildren();
        List<Node> newNodes = uniqueKeyNodesMap.get(uniqueKey);
        nodes.removeAll(newNodes);
        nodes.addAll(newNodes);
    }

    /**
     * Clear annotation
     *
     * @param data data
     */
    public void clearAnnotation(List<Data<X, Y>> data) {
        data.forEach(dataItem -> {
            StackPane pane = (StackPane) dataItem.getNode();
            for (int i = 0; i < pane.getChildren().size(); i++) {
                Node node = pane.getChildren().get(i);
                if (node instanceof Text) {
                    pane.getChildren().removeAll(node);
                }
            }
            if (dataItem.getNode().getStyleClass().contains("chart-symbol-triangle")) {
                dataItem.getNode().getStyleClass().remove("chart-symbol-triangle");
            }
        });
    }

    /**
     * Set chart all series annotation
     *
     * @param fetch annotation fetch function
     */
    public void setSeriesAnnotationEvent(AnnotationFetch fetch) {
        ObservableList<Series<X, Y>> seriesObservableList = this.getData();
        seriesObservableList.forEach(series -> {
            ObservableList<Data<X, Y>> data = series.getData();
            data.forEach(dataItem -> dataItem.getNode().setOnMouseClicked(event -> {
                if (pointClick && pointClickCallBack != null) {
                    pointClickCallBack.execute(dataItem.getExtraValue());
                }
                if (fetch != null && fetch.showedAnnotation()) {
                    AnnotationDataDto annotationDataDto = fetch.getValue(dataItem.getExtraValue());
                    if (annotationDataDto == null
                            || annotationDataDto.getSelectName() == null
                            || DAPStringUtils.isBlank(annotationDataDto.getSelectName().toString())) {
                        return;
                    }
                    setNodeAnnotation(dataItem, annotationDataDto.getValue(), fetch.getTextColor());
                    fetch.addData(dataItem);
                }
            }));
        });
    }

    /**
     * Hidden all data series lines
     */
    public void hiddenDataSeriesLine() {
        this.toggleDataAllSeriesLine(false);
    }

    /**
     * Hidden all path series lines and nodes
     *
     * @param pathName path name
     */
    public void hiddenPathSeriesLine(String pathName) {
        this.togglePathAllSeriesLine(pathName, false);
    }

    /**
     * Hidden all series point
     */
    public void hiddenDataSeriesPoint() {
        this.toggleDataAllSeriesPoint(false);
    }

    /**
     * Toggle all data series lines
     *
     * @param showed whether it show or hide
     */
    public void toggleDataAllSeriesLine(boolean showed) {
        if (seriesUniqueKeyMap == null) {
            return;
        }
        seriesUniqueKeyMap.forEach((key, value) -> this.toggleShowNode(value.getNode(), showed));
    }

    /**
     * Toggle series path show or hide
     *
     * @param pathName path name
     * @param showed   whether it show or not
     */
    public void togglePathAllSeriesLine(String pathName, boolean showed) {
        if (pathMarkerMap == null || DAPStringUtils.isBlank(pathName)) {
            return;
        }
        pathMarkerMap.forEach((key, value) -> value.forEach(series -> {
            if (pathName.equals(series.getName())) {
                this.toggleShowNode(series.getNode(), showed);
                series.getData().forEach(dataItem -> this.toggleShowNode(dataItem.getNode(), showed));
            }
        }));
    }

    /**
     * Toggle point show or hide
     *
     * @param showed whether it show or not
     */
    public void toggleDataAllSeriesPoint(boolean showed) {
        if (seriesUniqueKeyMap == null) {
            return;
        }
        seriesUniqueKeyMap.forEach((key, value) -> {
            XYChart.Series<X, Y> series = value;
            series.getData().forEach(dataItem -> this.toggleShowNode(dataItem.getNode(), showed));
        });
    }

    /**
     * Set point click call back
     *
     * @param pointClickCallBack point click call back
     */
    public void setPointClickCallBack(PointClickCallBack pointClickCallBack) {
        this.pointClickCallBack = pointClickCallBack;
    }

    private void toggleShowNode(Node node, boolean showed) {
        if (showed) {
            if (!getPlotChildren().contains(node)) {
                getPlotChildren().add(node);
            }
        } else {
            if (getPlotChildren().contains(node)) {
                getPlotChildren().remove(node);
            }
        }
    }

    /**
     * Hidden lines for line names
     *
     * @param lineNames line names
     */
    public void hiddenValueMarkers(List<String> lineNames) {
        for (Map.Entry<String, ValueMarker> valueMarkerEntry : valueMarkerMap.entrySet()) {

            lineNames.forEach(lineName -> {
                lineTooltipShowMap.put(lineName, false);
                valueMarkerEntry.getValue().hiddenValueMarker(lineName);
            });
        }
    }

    /**
     * Toggle line show or hide
     *
     * @param lineName line name
     * @param showed   whether it show or not
     */
    public void toggleValueMarker(String lineName, boolean showed) {
        if (DAPStringUtils.isBlank(lineName)) {
            return;
        }
        for (Map.Entry<String, ValueMarker> valueMarkerEntry : valueMarkerMap.entrySet()) {
            valueMarkerEntry.getValue().toggleValueMarker(lineName, showed);
            lineTooltipShowMap.put(lineName, showed);
        }
    }

    /**
     * Set run chart R1-R9 rule style
     *
     * @param ruleNames rule names
     */
    public void setSeriesDataStyleByRule(List<String> ruleNames) {
        this.rRuleDataUniqueKeyMap.clear();
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
                    //remember to demand r rules point
                    if (pointStyle.getAbnormal()) {
                        if (rRuleDataUniqueKeyMap.containsKey(uniqueKey)) {
                            List<XYChart.Data> dataList = rRuleDataUniqueKeyMap.get(uniqueKey);
                            dataList.add(dataItem);
                        } else {
                            rRuleDataUniqueKeyMap.put(uniqueKey, Lists.newArrayList(dataItem));
                        }
                    }
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
        if (!seriesUniqueKeyMap.containsKey(uniqueKey) || (moreData == null && lessData == null)) {
            return;
        }
        XYChart.Series<X, Y> series = seriesUniqueKeyMap.get(uniqueKey);
        for (int i = 0; i < series.getData().size(); i++) {
            Y data = series.getData().get(i).getYValue();
            if (data instanceof Double) {
                Double value = (Double) data;
                boolean flagMoreValid = moreData != null && i < moreData.length && !DAPStringUtils.isInfinityAndNaN(moreData[i]) && value > moreData[i];
                boolean flagLessValid = lessData != null && i < lessData.length && !DAPStringUtils.isInfinityAndNaN(lessData[i]) && value < lessData[i];
                if (flagLessValid || flagMoreValid) {
                    series.getData().get(i).getNode().setStyle("-fx-background-color: " + ColorUtils.toHexFromFXColor(Color.RED));
                    //remember to demand ucl and lcl rule point
                    if (uclAndLclDataUniqueKeyMap.containsKey(uniqueKey)) {
                        List<XYChart.Data> dataList = uclAndLclDataUniqueKeyMap.get(uniqueKey);
                        dataList.add(series.getData().get(i));
                    } else {
                        uclAndLclDataUniqueKeyMap.put(uniqueKey, Lists.newArrayList(series.getData().get(i)));
                    }
                }
            }
        }
    }

    /**
     * Remove all chart node and clear chart data
     */
    public void removeAllChildren() {
        this.uclAndLclDataUniqueKeyMap.clear();
        this.rRuleDataUniqueKeyMap.clear();
        this.seriesUniqueKeyMap.clear();
        this.seriesPointRuleMap.clear();
        this.uniqueKeyNodesMap.clear();
        this.seriesColorMap.clear();
        this.valueMarkerMap.clear();
        this.pathMarkerMap.clear();
        this.getPlotChildren().clear();
        this.getData().clear();
//        this.getData().setAll(FXCollections.observableArrayList());
//        this.getPlotChildren().removeAll(this.getPlotChildren());
        valueMarkerMap.forEach((key, value) -> value.clear());
    }

    /**
     * Active point click fire event
     *
     * @param flag if true
     */
    public void activePointClickEvent(boolean flag) {
        this.pointClick = flag;
    }

    private void setAxisRange(List<ControlChartData> controlChartDataList) {
        Double[] xLower = new Double[controlChartDataList.size()];
        Double[] xUpper = new Double[controlChartDataList.size()];
        Double[] yLower = new Double[controlChartDataList.size()];
        Double[] yUpper = new Double[controlChartDataList.size()];
        for (int i = 0; i < controlChartDataList.size(); i++) {
            xLower[i] = (Double) controlChartDataList.get(i).getXLowerBound();
            xUpper[i] = (Double) controlChartDataList.get(i).getXUpperBound();
            yLower[i] = (Double) controlChartDataList.get(i).getYLowerBound();
            yUpper[i] = (Double) controlChartDataList.get(i).getYUpperBound();
        }
        Double xMax = MathUtils.getMax(xUpper);
        Double xMin = MathUtils.getMin(xLower);
        Double yMax = MathUtils.getMax(yUpper);
        Double yMin = MathUtils.getMin(yLower);
        if (xMax == null || xMin == null || yMax == null || yMin == null) {
            return;
        }
        NumberAxis xAxis = (NumberAxis) this.getXAxis();
        NumberAxis yAxis = (NumberAxis) this.getYAxis();
        if (yMax - yMin > UIConstant.MARGINAL_VALUE) {
            yMax += (yMax - yMin) * UIConstant.Y_FACTOR;
            yMin -= (yMax - yMin) * UIConstant.Y_FACTOR;
        } else {
            yMax += UIConstant.Y_FACTOR;
            yMin -= UIConstant.Y_FACTOR;
        }
        Map<String, Object> yAxisRangeData = ChartOperatorUtils.getAdjustAxisRangeData(yMax, yMin, (yMax - yMin) > UIConstant.MARGINAL_VALUE
                ? (int) Math.ceil(yMax - yMin) : UIConstant.COR_NUMBER);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(xMax + UIConstant.X_FACTOR);
        double newYMin = (Double) yAxisRangeData.get(ChartOperatorUtils.KEY_MIN);
        double newYMax = (Double) yAxisRangeData.get(ChartOperatorUtils.KEY_MAX);
        yAxis.setLowerBound(newYMin > yMin ? yMin : newYMin);
        yAxis.setUpperBound(newYMax < yMax ? yMax : newYMax);
        ChartOperatorUtils.updateAxisTickUnit(xAxis);
        ChartOperatorUtils.updateAxisTickUnit(yAxis);
    }

    private ControlChartSeries createChartSeriesData(ControlChartData controlChartData, ChartTooltip chartTooltip) {
//        1. 设置画图数据, 图的颜色、样式、悬浮提示
//        2. 设置画直线数据， 线的颜色，样式
//        3. 设置画折线数据， 折线的颜色，样式
        if (controlChartData == null) {
            return null;
        }
        ControlChartSeries result = new ControlChartSeries();
        Color color = controlChartData.getColor();
        String seriesName = controlChartData.getSeriesName();
        String uniqueKey = controlChartData.getUniqueKey();
        List<ILineData> lineDataList = controlChartData.getLineData();
        List<IPathData> pathDataList = controlChartData.getBrokenLineData();
        Function<PointRule, PointStyle> rulePointStyleFunction = controlChartData.getPointFunction();

        if (controlChartData.getXyOneChartData() != null) {
            XYChart.Series series = this.buildDataSeries(controlChartData.getXyOneChartData(), seriesName);
            result.setPointSeries(series);
            result.setColor(color);
            result.setName(seriesName);
            this.seriesUniqueKeyMap.put(uniqueKey, series);
            this.seriesColorMap.put(series, color);
            this.seriesPointRuleMap.put(uniqueKey, rulePointStyleFunction);
        }
//        Set chart line
        result.setConnectLine(Lists.newArrayList());
        if (lineDataList != null) {
            ValueMarker valueMarker = new ValueMarker();
            lineDataList.forEach(oneLineData -> {
                if (oneLineData.getValue() != null) {
                    Line line = valueMarker.buildValueMarker(oneLineData, color, seriesName, chartTooltip == null ? null : chartTooltip.getLineTooltip());
                    result.getConnectLine().add(line);
                    if (chartTooltip != null && chartTooltip.getLineTooltip() != null) {
                        lineTooltipShowMap.put(oneLineData.getName(), true);
                        String content = chartTooltip.getLineTooltip().apply(new LineTooltip(seriesName, oneLineData.getName(), oneLineData.getValue()));
                        Tooltip.install(line, new Tooltip(content));

                    }
                }
            });
            valueMarkerMap.put(uniqueKey, valueMarker);
        }
//       Set chart path
        result.setPathSeries(Lists.newArrayList());
        if (pathDataList != null) {
            List<XYChart.Series<X, Y>> seriesList = Lists.newArrayList();
            pathDataList.forEach(onePathData -> {
                if (onePathData != null) {
                    XYChart.Series<X, Y> series = buildPathSeries(onePathData.getPoints(), onePathData.getPathName());
                    if (series != null) {
                        result.getPathSeries().add(series);
                        seriesList.add(series);
                    }
                }
            });
            pathMarkerMap.put(uniqueKey, seriesList);
        }
        return result;
    }

    private XYChart.Series buildDataSeries(IXYChartData<X, Y> xyOneChartData, String seriesName) {
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
            Object extraValue = xyOneChartData.getExtraValueByIndex(i) == null ? "" : xyOneChartData.getExtraValueByIndex(i);
            data.setExtraValue(extraValue);
            oneSeries.getData().add(data);
        }
        return oneSeries;
    }

    private XYChart.Series buildPathSeries(IPoint<X, Y> xyiPoint, String seriesName) {
        if (xyiPoint == null) {
            return null;
        }
        XYChart.Series oneSeries = new XYChart.Series();
        oneSeries.setName(seriesName);
        int length = xyiPoint.getLen();
        for (int i = 0; i < length; i++) {
            X xValue = xyiPoint.getXByIndex(i);
            Y yValue = xyiPoint.getYByIndex(i);
            if (xValue == null || yValue == null) {
                continue;
            }
            XYChart.Data data = new XYChart.Data<>(xValue, yValue);
            oneSeries.getData().add(data);
        }
        return oneSeries;
    }

    private void setNodeAnnotation(Data<X, Y> data, String value, String textColor) {
//        data.getNode().getStyleClass().clear();
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

    private List<Node> getSeriesNodes(List<XYChart.Series<X, Y>> series) {
        List<Node> nodes = Lists.newArrayList();
        if (series == null) {
            return nodes;
        }
        series.forEach(oneSeries -> {
            nodes.add(oneSeries.getNode());
            oneSeries.getData().forEach(dataItem -> nodes.add(dataItem.getNode()));
        });
        return nodes;
    }

    private void addToUniqueKeyNodes(String uniqueKey, List<Node> node) {
        if (uniqueKeyNodesMap.containsKey(uniqueKey)) {
            uniqueKeyNodesMap.get(uniqueKey).addAll(node);
        } else {
            uniqueKeyNodesMap.put(uniqueKey, Lists.newArrayList(node));
        }
    }

    private void updateNodeColor(String uniqueKey, Color color) {

        XYChart.Series series = seriesUniqueKeyMap.get(uniqueKey);
        seriesColorMap.put(series, color);
        ObservableList<Data<X, Y>> data = series.getData();
//        set series node color
        if (DAPStringUtils.isNotBlank(ColorUtils.toHexFromFXColor(color))) {
            series.getNode().setStyle("-fx-stroke: " + ColorUtils.toHexFromFXColor(color));
        }
        data.forEach(dataItem -> {
//            set data node color
            List<XYChart.Data> rRuleDataList = rRuleDataUniqueKeyMap.containsKey(uniqueKey) ? rRuleDataUniqueKeyMap.get(uniqueKey) : null;
            List<XYChart.Data> uclAndLclDataList = uclAndLclDataUniqueKeyMap.containsKey(uniqueKey) ? uclAndLclDataUniqueKeyMap.get(uniqueKey) : null;
            boolean inValid = rRuleDataList != null && rRuleDataList.contains(dataItem);
            inValid = inValid || uclAndLclDataList != null && uclAndLclDataList.contains(dataItem);
            if (!inValid && DAPStringUtils.isNotBlank(ColorUtils.toHexFromFXColor(color))) {
                dataItem.getNode().setStyle("-fx-background-color: " + ColorUtils.toHexFromFXColor(color));
            }
        });
    }

    private void updatePathColor(XYChart.Series series, Color color) {

        ObservableList<Data<X, Y>> data = series.getData();
//        set series node color
        if (DAPStringUtils.isNotBlank(ColorUtils.toHexFromFXColor(color))) {
            series.getNode().setStyle("-fx-stroke: " + ColorUtils.toHexFromFXColor(color) + ";-fx-stroke-dash-array: 5px;");
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
        Tooltip tooltip = new Tooltip("ASDF");
        data.forEach(dataItem -> {
//            set data node color
            if (DAPStringUtils.isNotBlank(ColorUtils.toHexFromFXColor(color))) {
                dataItem.getNode().setStyle("-fx-background-color: " + ColorUtils.toHexFromFXColor(color));
                dataItem.getNode().getStyleClass().setAll("chart-line-symbol", "chart-line-symbol-hover");
            }
//            set data node tooltip
            if (pointTooltipFunction != null) {
//                Tooltip.install(dataItem.getNode(), new Tooltip(content));
                final Node dataNode = dataItem.getNode();
                dataNode.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                            tooltip.setText(pointTooltipFunction.apply(new PointTooltip(series.getName(), dataItem)));
                            tooltip.show(dataNode, event.getScreenX() + ANCHOR_X, event.getScreenY() + ANCHOR_Y);
                        }
                );
                dataNode.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
                            if (tooltip.isShowing()) {
                                tooltip.setAnchorX(event.getScreenX() + ANCHOR_X);
                                tooltip.setAnchorY(event.getScreenY() + ANCHOR_Y);
                            }
                        }
                );
                dataNode.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
                    if (tooltip.isShowing()) {
                        tooltip.hide();
                    }
                });
            }
        });
    }

    private void setPathNodeStyleAndTooltip(XYChart.Series series,
                                            Color color,
                                            String key,
                                            Function<PointTooltip, String> pointTooltipFunction) {

        ObservableList<Data<X, Y>> data = series.getData();
//        set series node color
        if (DAPStringUtils.isNotBlank(ColorUtils.toHexFromFXColor(color))) {
            series.getNode().setStyle("-fx-stroke: " + ColorUtils.toHexFromFXColor(color) + ";-fx-stroke-dash-array: 5px;");
        }
        Tooltip tooltip = new Tooltip("ASDF");
        data.forEach(dataItem -> {
//            set data node color
            if (DAPStringUtils.isNotBlank(ColorUtils.toHexFromFXColor(color))) {
                dataItem.getNode().setStyle("-fx-background-color: " + ColorUtils.toHexFromFXColor(color));
                dataItem.getNode().getStyleClass().setAll("chart-path-symbol", "chart-path-symbol-hover");
            }
//            set data node tooltip
            if (pointTooltipFunction != null) {
                final Node dataNode = dataItem.getNode();
                dataNode.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                            tooltip.setText(pointTooltipFunction.apply(new PointTooltip(key + " " + series.getName(), dataItem)));
                            tooltip.show(dataNode, event.getScreenX() + ANCHOR_X, event.getScreenY() + ANCHOR_Y);
                        }
                );
                dataNode.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
                            if (tooltip.isShowing()) {
                                tooltip.setAnchorX(event.getScreenX() + ANCHOR_X);
                                tooltip.setAnchorY(event.getScreenY() + ANCHOR_Y);
                            }
                        }
                );
                dataNode.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
                    if (tooltip.isShowing()) {
                        tooltip.hide();
                    }
                });
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
    }

    private class ControlChartSeries {
        private XYChart.Series pointSeries;
        private List<Line> connectLine;
        private List<XYChart.Series> pathSeries;
        private Color color;
        private String name;

        public Series getPointSeries() {
            return pointSeries;
        }

        public void setPointSeries(Series pointSeries) {
            this.pointSeries = pointSeries;
        }

        public List<Line> getConnectLine() {
            return connectLine;
        }

        public void setConnectLine(List<Line> connectLine) {
            this.connectLine = connectLine;
        }

        public List<Series> getPathSeries() {
            return pathSeries;
        }

        public void setPathSeries(List<Series> pathSeries) {
            this.pathSeries = pathSeries;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
