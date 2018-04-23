package com.dmsoft.firefly.plugin.spc.charts;

import com.dmsoft.firefly.gui.components.chart.ChartOperatorUtils;
import com.dmsoft.firefly.plugin.spc.charts.data.BarCategoryData;
import com.dmsoft.firefly.plugin.spc.charts.data.ChartTooltip;
import com.dmsoft.firefly.plugin.spc.charts.data.NDBarChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.*;
import com.dmsoft.firefly.plugin.spc.charts.utils.MathUtils;
import com.dmsoft.firefly.plugin.spc.charts.utils.ReflectionUtils;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import com.dmsoft.firefly.sdk.utils.ColorUtils;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.javafx.css.converters.SizeConverter;
import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.*;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.util.*;
import java.util.function.Function;

/**
 * Created by cherry on 2018/3/1.
 */

/**
 * Nd chart
 *
 * @param <X> first data class
 * @param <Y> second data class
 */
public class NDChart<X, Y> extends XYChart<X, Y> {
    private Map<Series<X, Y>, Map<Object, Data<X, Y>>> seriesCategoryMap = new HashMap<>();
    private Map<XYChart.Data, BarCategoryData<X, Y>> barCategoryDataMap = Maps.newHashMap();
    private Data<X, Y> dataItemBeingRemoved = null;
    private Series<X, Y> seriesOfDataRemoved = null;
    //    unique key---area group
    private Map<String, Group> groupMap = Maps.newHashMap();
    //    unique key---bar series
    private Map<String, XYChart.Series> uniqueKeySeriesMap = Maps.newLinkedHashMap();

    private Map<XYChart.Series, String> seriesUniqueKeyMap = Maps.newLinkedHashMap();
    //    unique key----value marker
    private Map<String, ValueMarker> valueMarkerMap = Maps.newLinkedHashMap();
    //    unique key----area series
    private Map<String, AreaSeriesNode> areaSeriesNodeMap = Maps.newLinkedHashMap();

    private Map<String, List<Node>> uniqueKeyNodesMap = Maps.newHashMap();

    private ValueAxis valueAxis;
    private Timeline dataRemoveTimeline;
    private TreeSet categories = new TreeSet();
    private boolean showTooltip = true;
    private double bottomPos = 0;
    private static final String NEGATIVE_STYLE = "negative";
    private final Orientation orientation;

    private boolean barShow = true;
    private boolean areaShow = true;
    private Map<String, Boolean> lineShow = Maps.newLinkedHashMap();

    private final double ANCHOR_X = 10.0;
    private final double ANCHOR_Y = 15.0;

    /**
     * Constructs a XYChart given the two axes. The initial content for the chart
     * plot background and plot area that includes vertical and horizontal grid
     * lines and fills, are added.
     *
     * @param xAxis X Axis for this XY chart
     * @param yAxis Y Axis for this XY chart
     */
    public NDChart(Axis<X> xAxis, Axis<Y> yAxis) {
        super(xAxis, yAxis);
        this.getStyleClass().add("bar-chart");
        this.setAnimated(false);
        this.orientation = Orientation.VERTICAL;
        this.valueAxis = (ValueAxis) yAxis;
        this.setHorizontalZeroLineVisible(false);
        this.setHorizontalGridLinesVisible(false);
        this.setVerticalZeroLineVisible(false);
        this.setVerticalGridLinesVisible(false);
        this.pseudoClassStateChanged(HORIZONTAL_PSEUDOCLASS_STATE, orientation == Orientation.HORIZONTAL);
        this.pseudoClassStateChanged(VERTICAL_PSEUDOCLASS_STATE, orientation == Orientation.VERTICAL);
        this.setData(FXCollections.observableArrayList());
        this.setLegendVisible(false);
    }

    /**
     * Set nd chart data
     *
     * @param barChartDataList chart data
     * @param chartTooltip     chart tooltip rule
     */
    public void setData(List<NDBarChartData> barChartDataList, ChartTooltip chartTooltip) {
        this.removeAllChildren();
        if (barChartDataList == null) {
            return;
        }
        setAxisRange(barChartDataList);
        barChartDataList.forEach(ndBarChartData -> createChartSeriesData(ndBarChartData, chartTooltip));
    }

    private void setAxisRange(List<NDBarChartData> barChartDataList) {
        Double[] xLower = new Double[barChartDataList.size()];
        Double[] xUpper = new Double[barChartDataList.size()];
        Double[] yLower = new Double[barChartDataList.size()];
        Double[] yUpper = new Double[barChartDataList.size()];
        for (int i = 0; i < barChartDataList.size(); i++) {
            xLower[i] = (Double) barChartDataList.get(i).getXLowerBound();
            xUpper[i] = (Double) barChartDataList.get(i).getXUpperBound();
            yLower[i] = (Double) barChartDataList.get(i).getYLowerBound();
            yUpper[i] = (Double) barChartDataList.get(i).getYUpperBound();
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
        }
        if ((xMax - xMin) > UIConstant.MARGINAL_VALUE) {
            xMax += (xMax - xMin) * UIConstant.Y_FACTOR;
            xMin -= (xMax - xMin) * UIConstant.Y_FACTOR;
        }
        Map<String, Object> yAxisRangeData = ChartOperatorUtils.getAdjustAxisRangeData(yMax, yMin, (yMax - yMin) > UIConstant.MARGINAL_VALUE
                ? (int) Math.ceil(yMax - yMin) : UIConstant.COR_NUMBER);
        Map<String, Object> xAxisRangeData = ChartOperatorUtils.getAdjustAxisRangeData(xMax, xMin, (xMax - xMin) > UIConstant.MARGINAL_VALUE
                ? (int) Math.ceil(yMax - yMin) : UIConstant.COR_NUMBER);
        double newYMax = (Double) yAxisRangeData.get(ChartOperatorUtils.KEY_MAX);
        double newXMin = (Double) xAxisRangeData.get(ChartOperatorUtils.KEY_MIN);
        double newXMax = (Double) xAxisRangeData.get(ChartOperatorUtils.KEY_MAX);
        yAxis.setLowerBound(yMin);
        yAxis.setUpperBound(newYMax < yMax ? yMax : newYMax);
        xAxis.setLowerBound(newXMin > xMin ? xMin : newXMin);
        xAxis.setUpperBound(newXMax < xMax ? xMax : newXMax);
        ChartOperatorUtils.updateAxisTickUnit(xAxis);
        ChartOperatorUtils.updateAxisTickUnit(yAxis);
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
     * Remove all chart elements and chart data
     */
    public void removeAllChildren() {
        getPlotChildren().clear();
        for (Map.Entry<String, XYChart.Series> stringSeriesEntry : uniqueKeySeriesMap.entrySet()) {
            seriesRemoved(stringSeriesEntry.getValue());
        }
//        ObservableList<Node> nodes = getPlotChildren();
//        getData().clear();
//        getPlotChildren().removeAll(nodes);
        getPlotChildren().clear();
        clearData();
    }

    /**
     * Update chart color
     *
     * @param uniqueKey unique key
     * @param color     color
     */
    public void updateChartColor(String uniqueKey, Color color) {
//        update chart color
        if (groupMap.containsKey(uniqueKey)) {
            groupMap.get(uniqueKey).setStyle("-fx-stroke: " + ColorUtils.toHexFromFXColor(color));
        }
        if (areaSeriesNodeMap.containsKey(uniqueKey)) {
            areaSeriesNodeMap.get(uniqueKey).updateColor(color);
        }
        if (uniqueKeySeriesMap.containsKey(uniqueKey)) {
            setSeriesDataStyle(uniqueKeySeriesMap.get(uniqueKey), color);
        }

        if (!areaShow) {
            toggleAreaSeries(false);
        }

        if (!barShow) {
            toggleBarSeries(false);
        }

//        update value maker color
        if (valueMarkerMap.containsKey(uniqueKey)) {
            ValueMarker valueMarker = valueMarkerMap.get(uniqueKey);
            valueMarker.updateAllLineColor(color);
            lineShow.forEach((key, value) -> valueMarker.toggleValueMarker(key, value));
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
                valueMarkerEntry.getValue().hiddenValueMarker(lineName);
                lineShow.put(lineName, false);
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
        }
        lineShow.put(lineName, showed);
    }

    /**
     * Toggle bar series show or hide
     *
     * @param showed whether it show or not
     */
    public void toggleBarSeries(boolean showed) {
        ObservableList<Series<X, Y>> seriesObservableList = getData();
        seriesObservableList.forEach(series -> series.getData().forEach(dataItem -> {
            if (!showed) {
                dataItem.getNode().getStyleClass().setAll("chart-hidden-bar");
            } else {
                dataItem.getNode().getStyleClass().setAll("chart-bar");
            }
        }));
        barShow = showed;
    }

    /**
     * Toggle area series show or hide
     *
     * @param showed whether it show or not
     */
    public void toggleAreaSeries(boolean showed) {
        for (Map.Entry<String, AreaSeriesNode> areaSeriesNodeEntry : areaSeriesNodeMap.entrySet()) {
            areaSeriesNodeEntry.getValue().toggleAreaSeries(showed);

//            groupMap.get(areaSeriesNodeEntry.getKey())
        }
        areaShow = showed;
    }

    private void createChartSeriesData(NDBarChartData chartData, ChartTooltip chartTooltip) {
//        1.设置柱子数据源
//        2.设置曲线数据源
//        3.设置直线数据源
//        4.设置柱子样式
//        5.设置曲线样式
//        6.设置直线样式
//        7.设置鼠标悬停提示
        if (chartData == null) {
            return;
        }
        String uniqueKey = chartData.getUniqueKey();
        String seriesName = chartData.getSeriesName();
        Color color = chartData.getColor();
        if (chartData.getXYChartData() != null) {
            this.createAreaGroup(chartData.getXYChartData(), uniqueKey, color);
        }
        if (chartData.getBarChartData() != null) {
            XYChart.Series series = this.buildSeries(chartData.getBarChartData(), seriesName);
            this.uniqueKeySeriesMap.put(uniqueKey, series);
            this.seriesUniqueKeyMap.put(series, uniqueKey);
            this.getData().add(series);
            this.setSeriesDataStyle(series, color);
            this.setSeriesDataTooltip(series, chartTooltip == null ? null : chartTooltip.getChartBarToolTip());
        }

        if (chartData.getLineData() != null) {
            ValueMarker valueMarker = new ValueMarker();
            chartData.getLineData().forEach(lineData -> {
                if (lineData.getValue() != null) {
                    Line line = valueMarker.buildValueMarker(lineData, color, seriesName,
                            (chartTooltip == null) ? null : chartTooltip.getLineTooltip());
                    getPlotChildren().add(line);
                    lineShow.put(lineData.getName(), true);
                    addToUniqueKeyNodes(uniqueKey, line);
                }
            });
            valueMarkerMap.put(uniqueKey, valueMarker);
        }
    }

    private XYChart.Series buildSeries(IBarChartData<X, Y> barData, String seriesName) {
        XYChart.Series oneSeries = new XYChart.Series();
        oneSeries.setName(seriesName);
        int length = barData.getLen();
        for (int i = 0; i < length; i++) {
            X xValue = barData.getStartValueByIndex(i);
            Y yValue = barData.getValueByIndex(i);
            if (xValue == null || yValue == null) {
                continue;
            }
            XYChart.Data data = new XYChart.Data<>(xValue, yValue);
            data.setExtraValue(barData.getEndValueByIndex(i));
            oneSeries.getData().add(data);
            barCategoryDataMap.put(data,
                    new BarCategoryData(xValue, barData.getBarWidthByIndex(i), yValue));
        }
        return oneSeries;
    }

    private void setSeriesDataStyle(XYChart.Series series, Color color) {
        ObservableList<Data<X, Y>> data = series.getData();
        data.forEach(dataItem -> {
            dataItem.getNode().getStyleClass().setAll("chart-bar");
            if (color != null && DAPStringUtils.isNotBlank(ColorUtils.toHexFromFXColor(color))) {
                StringBuilder nodeStyle = new StringBuilder();
                nodeStyle.append("-fx-bar-fill: " + ColorUtils.toHexFromFXColor(color));
                dataItem.getNode().setStyle(nodeStyle.toString());
            }
        });
    }

    private void setSeriesDataTooltip(XYChart.Series<X, Y> series,
                                      Function<BarToolTip, String> barToolTipStringFunction) {
        if (!showTooltip) {
            return;
        }
        Tooltip tooltip = new Tooltip();
        int size = series.getData().size();
        for (int i = 0; i < size; i++) {
            XYChart.Data<X, Y> dataItem = series.getData().get(i);
            if (barToolTipStringFunction != null) {
                boolean lastData = i == size - 1 ? true : false;
                String content = barToolTipStringFunction.apply(new BarToolTip(
                        series.getName(),
                        dataItem.getXValue(),
                        dataItem.getExtraValue(),
                        dataItem.getYValue(),
                        lastData));

                final Node dataNode = dataItem.getNode();
                dataNode.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                            tooltip.setText(content);
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
//                Tooltip.install(dataItem.getNode(), new Tooltip(content));
            }
        }
    }

    private void createAreaGroup(IXYChartData<X, Y> xyOneChartData, String unique, Color color) {
        AreaSeriesNode areaSeriesNode = new AreaSeriesNode();
        Group areaGroup = areaSeriesNode.buildAreaGroup(xyOneChartData, color);
        areaGroup.setStyle("-fx-stroke: " + ColorUtils.toHexFromFXColor(color));
        groupMap.put(unique, areaGroup);
        areaSeriesNodeMap.put(unique, areaSeriesNode);
        getPlotChildren().add(areaGroup);
        addToUniqueKeyNodes(unique, areaGroup);
    }

    private void addToUniqueKeyNodes(String uniqueKey, Node node) {
        if (uniqueKeyNodesMap.containsKey(uniqueKey)) {
            uniqueKeyNodesMap.get(uniqueKey).add(node);
        } else {
            uniqueKeyNodesMap.put(uniqueKey, Lists.newArrayList(node));
        }
    }

    private void addToSeriesNodes(Series series, Node node) {
        String uniqueKey = seriesUniqueKeyMap == null || !seriesUniqueKeyMap.containsKey(series) ? null : seriesUniqueKeyMap.get(series);
        if (DAPStringUtils.isBlank(uniqueKey)) {
            return;
        }
        addToUniqueKeyNodes(uniqueKey, node);
    }

    @Override
    protected void dataItemAdded(Series<X, Y> series, int itemIndex, Data<X, Y> item) {
        Object category = (orientation == Orientation.VERTICAL) ? item.getXValue() : item.getYValue();
        categories.add(category);
        Map<Object, Data<X, Y>> categoryMap = seriesCategoryMap.get(series);
        if (categoryMap == null) {
            categoryMap = new HashMap();
            seriesCategoryMap.put(series, categoryMap);
        }
        if (categoryMap.containsKey(category)) {
            Data data = categoryMap.get(category);
            getPlotChildren().remove(data.getNode());
            removeDataItemFromDisplay(series, data);
            requestChartLayout();
            categoryMap.remove(category);
        }
        categoryMap.put(category, item);
        Node bar = createBar(series, getData().indexOf(series), item, itemIndex);
        if (shouldAnimate()) {
            if (dataRemoveTimeline != null && dataRemoveTimeline.getStatus().equals(Animation.Status.RUNNING)) {
                if (dataItemBeingRemoved != null && dataItemBeingRemoved == item) {
                    dataRemoveTimeline.stop();
                    getPlotChildren().remove(bar);
                    removeDataItemFromDisplay(seriesOfDataRemoved, item);
                    dataItemBeingRemoved = null;
                    seriesOfDataRemoved = null;
                }
            }
            animateDataAdd(item, bar);
        } else {
            getPlotChildren().add(bar);
            addToSeriesNodes(series, bar);
        }
        getPlotChildren().add(bar);
        addToSeriesNodes(series, bar);
    }

    @Override
    protected void dataItemRemoved(Data<X, Y> item, Series<X, Y> series) {
        final Node bar = item.getNode();
        if (bar != null) {
            bar.focusTraversableProperty().unbind();
        }
        if (shouldAnimate()) {
            dataRemoveTimeline = createDataRemoveTimeline(item, bar, series);
            dataItemBeingRemoved = item;
            seriesOfDataRemoved = series;
            dataRemoveTimeline.setOnFinished(event -> {
                ReflectionUtils.forceMethodCall(Data.class, "setSeries", item, new Object[]{null});
                getPlotChildren().remove(bar);
                removeDataItemFromDisplay(series, item);
                dataItemBeingRemoved = null;
                updateMap(series, item);
            });
            dataRemoveTimeline.play();
        } else {
            ReflectionUtils.forceMethodCall(Data.class, "setSeries", item, new Object[]{null});
            getPlotChildren().remove(bar);
            removeDataItemFromDisplay(series, item);
            updateMap(series, item);
            processDataRemove(series, item);
            removeDataItemFromDisplay(series, item);
        }
        barCategoryDataMap.remove(item);
    }

    private void processDataRemove(final Series<X, Y> series, final Data<X, Y> item) {
        Node bar = item.getNode();
        getPlotChildren().remove(bar);
        updateMap(series, item);
    }

    @Override
    protected void dataItemChanged(Data<X, Y> item) {
        double barVal;
        double currentVal;
        if (orientation == Orientation.VERTICAL) {
            barVal = ((Number) item.getYValue()).doubleValue();
            //currentVal = ((Number)item.getCurrentY()).doubleValue();
            Number currentY = (Number) ReflectionUtils.forceMethodCall(Data.class, "getCurrentY", item);
            currentVal = currentY.doubleValue();
        } else {
            barVal = ((Number) item.getXValue()).doubleValue();
            //currentVal = ((Number)item.getCurrentX()).doubleValue();
            Number currentX = (Number) ReflectionUtils.forceMethodCall(Data.class, "getCurrentX", item);
            currentVal = currentX.doubleValue();
        }
        if (currentVal > 0 && barVal < 0) { // going from positive to negative
            // add style class negative
            item.getNode().getStyleClass().add(NEGATIVE_STYLE);
        } else if (currentVal < 0 && barVal > 0) { // going from negative to positive
            // remove style class negative
            // RT-21164 upside down bars: was adding NEGATIVE_STYLE styleclass
            // instead of removing it; when going from negative to positive
            item.getNode().getStyleClass().remove(NEGATIVE_STYLE);
        }
    }

    @Override
    protected void seriesAdded(Series<X, Y> series, int seriesIndex) {
        Map<Object, Data<X, Y>> categoryMap = new HashMap();
        for (int j = 0; j < series.getData().size(); j++) {
            Data<X, Y> item = series.getData().get(j);
            Node bar = createBar(series, seriesIndex, item, j);
            Object category;
            if (orientation == Orientation.VERTICAL) {
                category = item.getXValue();
            } else {
                category = item.getYValue();
            }
            categoryMap.put(category, item);

            categories.add(category);

            if (shouldAnimate()) {
                animateDataAdd(item, bar);
            } else {
                // RT-21164 check if bar value is negative to add NEGATIVE_STYLE style class
                double barVal = (orientation == Orientation.VERTICAL)
                        ? ((Number) item.getYValue()).doubleValue() : ((Number) item.getXValue()).doubleValue();
                if (barVal < 0) {
                    bar.getStyleClass().add(NEGATIVE_STYLE);
                }
                getPlotChildren().add(bar);
                addToSeriesNodes(series, bar);
            }
        }
        if (categoryMap.size() > 0) {
            seriesCategoryMap.put(series, categoryMap);
        }
    }

    @Override
    protected void seriesRemoved(Series<X, Y> series) {
        updateDefaultColorIndex(series);
        // remove all symbol nodes
        if (shouldAnimate()) {
            ParallelTransition pt = new ParallelTransition();
            pt.setOnFinished(event -> removeSeriesFromDisplay(series));
            for (final Data<X, Y> d : series.getData()) {
                final Node bar = d.getNode();
                // Animate series deletion
                if (((int) ReflectionUtils.forceMethodCall(XYChart.class,
                        "getSeriesSize", this)) > 1) {
                    for (int j = 0; j < series.getData().size(); j++) {
                        Data<X, Y> item = series.getData().get(j);
                        Timeline t = createDataRemoveTimeline(item, bar, series);
                        pt.getChildren().add(t);
                    }
                } else {
                    // fade out last series
                    FadeTransition ft = new FadeTransition(Duration.millis(700), bar);
                    ft.setFromValue(1);
                    ft.setToValue(0);
                    ft.setOnFinished(actionEvent -> {
                        getPlotChildren().remove(bar);
                        updateMap(series, d);
                    });
                    pt.getChildren().add(ft);
                }
            }
            pt.play();
        } else {
            for (Data<X, Y> d : series.getData()) {
                final Node bar = d.getNode();
                getPlotChildren().remove(bar);
                updateMap(series, d);
            }
            removeSeriesFromDisplay(series);
        }
    }

    @Override
    protected void layoutPlotChildren() {
        this.paintAreaSeries();
        this.paintBarPlot();
        for (Map.Entry<String, ValueMarker> valueMarkerEntry : valueMarkerMap.entrySet()) {
            valueMarkerEntry.getValue().paintValueMaker(this);
        }
    }

    private void paintAreaSeries() {
        for (Map.Entry<String, AreaSeriesNode> areaSeriesNodeEntry : areaSeriesNodeMap.entrySet()) {
            areaSeriesNodeEntry.getValue().paintAreaSeries(this);
        }
    }

    private void paintBarPlot() {
        double barWidth = 0;
        final double defaultWidth = 0.5;
        SortedSet categoriesOnScreen = getCategoriesOnScreen();
        if (categoriesOnScreen == null) {
            return;
        }
        barWidth = calculateBarWidth(categoriesOnScreen);
        barWidth = (barWidth == 0) ? defaultWidth : barWidth;
        final double zeroPos = (valueAxis.getLowerBound() > 0)
                ? valueAxis.getDisplayPosition(valueAxis.getLowerBound()) : valueAxis.getZeroPosition();
        int catIndex = 0;
        for (Object category : categories) {
            for (Iterator<Series<X, Y>> sit = getDisplayedSeriesIterator(); sit.hasNext(); ) {
                int index = 0;
                Series<X, Y> series = sit.next();
                final Data<X, Y> item = getDataItem(series, index, catIndex, category);
                if (item != null) {
                    final Node bar = item.getNode();
                    final double categoryPos;
                    final double endCategoryPos;
                    final double valPos;
                    double barWidthSize = (Double) barCategoryDataMap.get(item).getBarWidth();
                    X currentX = (X) ReflectionUtils.forceMethodCall(Data.class, "getCurrentX", item);
                    Y currentY = (Y) ReflectionUtils.forceMethodCall(Data.class, "getCurrentY", item);
                    if (orientation == Orientation.VERTICAL) {
                        X endX = null;
                        if (item.getExtraValue() != null && item.getExtraValue() instanceof Double) {
                            endX = (X) item.getExtraValue();
                        }
                        categoryPos = getXAxis().getDisplayPosition(currentX);
                        endCategoryPos = getXAxis().getDisplayPosition(endX);
                        valPos = getYAxis().getDisplayPosition(currentY);
                    } else {
                        Y endY = null;
                        if (item.getExtraValue() != null && item.getExtraValue() instanceof Double) {
                            endY = (Y) item.getExtraValue();
                        }
                        categoryPos = getYAxis().getDisplayPosition(currentY);
                        endCategoryPos = getYAxis().getDisplayPosition(endY);
                        valPos = getXAxis().getDisplayPosition(currentX);
                    }
                    if (Double.isNaN(categoryPos) || Double.isNaN(valPos)) {
                        continue;
                    }
                    final double bottom = Math.min(valPos, zeroPos);
                    final double top = Math.max(valPos, zeroPos);
                    bottomPos = bottom;
                    if (orientation == Orientation.VERTICAL) {
                        bar.resizeRelocate(categoryPos + (barWidth * barWidthSize + getBarGap()) * index,
                                bottom, (endCategoryPos - categoryPos), top - bottom);
                    } else {
                        //noinspection SuspiciousNameCombination
//                        bar.resizeRelocate(bottom, categoryPos + (barWidth * barWidthSize + getBarGap()) * index,
//                                top - bottom, barWidth * barWidthSize);
                        bar.resizeRelocate(bottom, categoryPos + (barWidth * barWidthSize + getBarGap()) * index,
                                top - bottom, (endCategoryPos - categoryPos));
                    }
                    index++;
                }
            }
            catIndex++;
        }
    }

    private void clearData() {
        seriesCategoryMap.clear();
        barCategoryDataMap.clear();
        uniqueKeySeriesMap.clear();
        seriesUniqueKeyMap.clear();
        uniqueKeyNodesMap.clear();
        categories.clear();
        for (Map.Entry<String, ValueMarker> valueMarkerEntry : valueMarkerMap.entrySet()) {
            valueMarkerEntry.getValue().clear();
        }
    }

    private SortedSet getCategoriesOnScreen() {
        ValueAxis categoryAxis = (ValueAxis) getXAxis();
        Object lowestCategoryShowing = categories.higher(categoryAxis.getLowerBound());
        if (lowestCategoryShowing == null) {
            return null;
        }
        Object highestCategoryShowing = categories.lower(categoryAxis.getUpperBound());
        if (highestCategoryShowing == null) {
            return null;
        }
        if (Double.valueOf(highestCategoryShowing + "") < Double.valueOf(lowestCategoryShowing + "")) {
            return null;
        }
        return categories.subSet(lowestCategoryShowing, true, highestCategoryShowing, true);
    }

    private Data<X, Y> getDataItem(Series<X, Y> series, Object category) {
        Map<Object, Data<X, Y>> catMap = seriesCategoryMap.get(series);
        return (catMap != null) ? catMap.get(category) : null;
    }

    private Data<X, Y> getDataItem(Series<X, Y> series, int seriesIndex, int itemIndex, Object category) {
        Map<Object, Data<X, Y>> catMap = seriesCategoryMap.get(series);
        return (catMap != null) ? catMap.get(category) : null;
    }

    private double calculateBarWidth(SortedSet categories) {
        ValueAxis xAxis = (ValueAxis) getXAxis();
        double firstValue = (double) categories.first();
        if (firstValue == 0) {
            return firstValue;
        }
        double mark = xAxis.getDisplayPosition(firstValue) / firstValue;
        return mark;
    }

    private Node createBar(Series series, int seriesIndex, final Data item, int itemIndex) {
        Node bar = item.getNode();
        if (bar == null) {
            bar = new StackPane();
            item.setNode(bar);
        }
        String defaultColorStyleClassValue = (String) ReflectionUtils.forceFieldCall(Series.class,
                "defaultColorStyleClass", series);

        bar.getStyleClass().addAll("chart-bar",
                "series" + seriesIndex,
                "data" + itemIndex,
                defaultColorStyleClassValue);
        return bar;
    }

    private void animateDataAdd(Data<X, Y> item, Node bar) {
        double barVal;
        if (orientation == Orientation.VERTICAL) {
            barVal = ((Number) item.getYValue()).doubleValue();
            if (barVal < 0) {
                bar.getStyleClass().add(NEGATIVE_STYLE);
            }
            ReflectionUtils.forceMethodCall(Data.class, "setCurrentY", item,
                    new Class[]{Object.class},
                    new Object[]{getYAxis().toRealValue((barVal < 0) ? -bottomPos : bottomPos)});

            getPlotChildren().add(bar);
            item.setYValue(getYAxis().toRealValue(barVal));
            ReflectionUtils.forceMethodCall(Chart.class, "animate", this,
                    new Object[]{new KeyFrame[]{
                            new KeyFrame(Duration.ZERO, new KeyValue((ObjectProperty<Y>) ReflectionUtils.forceMethodCall(Data.class, "currentYProperty", item),
                                    (Y) ReflectionUtils.forceMethodCall(Data.class, "getCurrentY", item))),
                            new KeyFrame(Duration.millis(700),
                                    new KeyValue((ObjectProperty<Y>) ReflectionUtils.forceMethodCall(Data.class, "currentYProperty", item), item.getYValue(), Interpolator.EASE_BOTH))}}
            );
        } else {
            barVal = ((Number) item.getXValue()).doubleValue();
            if (barVal < 0) {
                bar.getStyleClass().add(NEGATIVE_STYLE);
            }
            ReflectionUtils.forceMethodCall(Data.class, "setCurrentX", item,
                    new Class[]{Object.class},
                    new Object[]{getXAxis().toRealValue((barVal < 0) ? -bottomPos : bottomPos)});

            getPlotChildren().add(bar);
            item.setXValue(getXAxis().toRealValue(barVal));
            ReflectionUtils.forceMethodCall(Chart.class, "animate", this,
                    new KeyFrame(Duration.ZERO,
                            new KeyValue((ObjectProperty<X>) ReflectionUtils.forceMethodCall(Data.class, "currentXProperty", item),
                                    (X) ReflectionUtils.forceMethodCall(Data.class, "getCurrentX", item))),
                    new KeyFrame(Duration.millis(700),
                            new KeyValue((ObjectProperty<X>) ReflectionUtils.forceMethodCall(Data.class, "currentXProperty", item),
                                    item.getXValue(), Interpolator.EASE_BOTH))
            );
        }
    }

    private Timeline createDataRemoveTimeline(final Data<X, Y> item, final Node bar, final Series<X, Y> series) {
        Timeline t = new Timeline();
        if (orientation == Orientation.VERTICAL) {
            item.setYValue(getYAxis().toRealValue(bottomPos));
            t.getKeyFrames().addAll(new KeyFrame(Duration.ZERO,
                            new KeyValue((ObjectProperty<Y>) ReflectionUtils.forceMethodCall(Data.class, "currentYProperty", item),
                                    (Y) ReflectionUtils.forceMethodCall(Data.class, "getCurrentY", item))),

                    new KeyFrame(Duration.millis(700), actionEvent -> {
                        getPlotChildren().remove(bar);
                        updateMap(series, item);
                    },
                            new KeyValue((ObjectProperty<Y>) ReflectionUtils.forceMethodCall(Data.class, "currentYProperty", item),
                                    item.getYValue(), Interpolator.EASE_BOTH)));
        } else {
            item.setXValue(getXAxis().toRealValue(getXAxis().getZeroPosition()));
            t.getKeyFrames().addAll(new KeyFrame(Duration.ZERO, new KeyValue((ObjectProperty<X>) ReflectionUtils.forceMethodCall(Data.class,
                    "currentXProperty", item), (X) ReflectionUtils.forceMethodCall(Data.class, "getCurrentX", item))),
                    new KeyFrame(Duration.millis(700), actionEvent -> {
                        getPlotChildren().remove(bar);
                        updateMap(series, item);
                    },
                            new KeyValue((ObjectProperty<X>) ReflectionUtils.forceMethodCall(Data.class, "currentXProperty", item), item.getXValue(),
                                    Interpolator.EASE_BOTH)));
        }
        return t;
    }

    private void updateDefaultColorIndex(final Series<X, Y> series) {
        //int clearIndex = seriesColorMap.get(series);
        Map<Series, Integer> seriesColorMapValue = (Map<Series, Integer>) ReflectionUtils.forceFieldCall(XYChart.class, "seriesColorMap", this);
        if (seriesColorMapValue.containsKey(series)) {
            int clearIndex = seriesColorMapValue.get(series);
            //colorBits.clear(clearIndex);
            BitSet colorBitsValue = (BitSet) ReflectionUtils.forceFieldCall(XYChart.class, "colorBits", this);
            // DEFAULT_COLOR
            String defaultColor = (String) ReflectionUtils.forceFieldCall(XYChart.class, "DEFAULT_COLOR", null);
            for (Data<X, Y> d : series.getData()) {
                final Node bar = d.getNode();
                if (bar != null) {
                    bar.getStyleClass().remove(defaultColor + clearIndex);
                    colorBitsValue.clear(clearIndex);
                }
            }
            seriesColorMapValue.remove(series);
        }
    }

    private void updateMap(Series series, Data item) {
        final Object category = (orientation == Orientation.VERTICAL) ? item.getXValue() : item.getYValue();
        Map<Object, Data<X, Y>> categoryMap = seriesCategoryMap.get(series);
        if (categoryMap != null) {
            categoryMap.remove(category);
            categories.remove(category);
            if (categoryMap.isEmpty()) {
                seriesCategoryMap.remove(series);
            }
        }
    }

    /**
     * The gap to leave between bars in the same category
     */
    private DoubleProperty barGap = new StyleableDoubleProperty(2) {
        @Override
        protected void invalidated() {
            get();
            requestChartLayout();
        }

        public Object getBean() {
            return NDChart.this;
        }

        public String getName() {
            return "barGap";
        }

        public CssMetaData<NDChart<?, ?>, Number> getCssMetaData() {
            return NDChart.StyleableProperties.BAR_GAP;
        }
    };

    /**
     * Get bar gap
     *
     * @return bar gap
     */
    public final double getBarGap() {
        return barGap.getValue();
    }

    /**
     * Set bar gap
     *
     * @param value bar gap
     */
    public final void setBarGap(double value) {
        barGap.setValue(value);
    }

    /**
     * Get bar gap property
     *
     * @return bar gap property
     */
    public final DoubleProperty barGapProperty() {
        return barGap;
    }

    /**
     * The gap to leave between bars in separate categories
     */
    private DoubleProperty categoryGap = new StyleableDoubleProperty(10) {
        @Override
        protected void invalidated() {
            get();
            requestChartLayout();
        }

        @Override
        public Object getBean() {
            return NDChart.this;
        }

        @Override
        public String getName() {
            return "categoryGap";
        }

        public CssMetaData<NDChart<?, ?>, Number> getCssMetaData() {
            return NDChart.StyleableProperties.CATEGORY_GAP;
        }
    };

    /**
     * Get category bar gap
     *
     * @return category bar gap
     */
    public final double getCategoryGap() {
        return categoryGap.getValue();
    }

    /**
     * Set category bar gap
     *
     * @param value category bar gap
     */
    public final void setCategoryGap(double value) {
        categoryGap.setValue(value);
    }

    /**
     * Get category gap property
     *
     * @return category gap property
     */
    public final DoubleProperty categoryGapProperty() {
        return categoryGap;
    }

    /**
     * Super-lazy instantiation pattern from Bill Pugh.
     */
    private static class StyleableProperties {
        private static final CssMetaData<NDChart<?, ?>, Number> BAR_GAP =
                new CssMetaData<NDChart<?, ?>, Number>("-fx-bar-gap",
                        SizeConverter.getInstance(), 4.0) {

                    @Override
                    public boolean isSettable(NDChart<?, ?> node) {
                        return node.barGap == null || !node.barGap.isBound();
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(NDChart<?, ?> node) {
                        return (StyleableProperty<Number>) node.barGapProperty();
                    }
                };

        private static final CssMetaData<NDChart<?, ?>, Number> CATEGORY_GAP =
                new CssMetaData<NDChart<?, ?>, Number>("-fx-category-gap",
                        SizeConverter.getInstance(), 10.0) {

                    @Override
                    public boolean isSettable(NDChart<?, ?> node) {
                        return node.categoryGap == null || !node.categoryGap.isBound();
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(NDChart<?, ?> node) {
                        return (StyleableProperty<Number>) node.categoryGapProperty();
                    }
                };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        static {

            final List<CssMetaData<? extends Styleable, ?>> styleables =
                    new ArrayList<CssMetaData<? extends Styleable, ?>>(XYChart.getClassCssMetaData());
            styleables.add(BAR_GAP);
            styleables.add(CATEGORY_GAP);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    /**
     * @return The CssMetaData associated with this class, which may include the
     * CssMetaData of its super classes.
     * @since JavaFX 8.0
     */
    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return NDChart.StyleableProperties.STYLEABLES;
    }

    /**
     * {@inheritDoc}
     *
     * @since JavaFX 8.0
     */
    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }

    /**
     * Pseudoclass indicating this is a vertical chart.
     */
    private static final PseudoClass VERTICAL_PSEUDOCLASS_STATE =
            PseudoClass.getPseudoClass("vertical");

    /**
     * Pseudoclass indicating this is a horizontal chart.
     */
    private static final PseudoClass HORIZONTAL_PSEUDOCLASS_STATE =
            PseudoClass.getPseudoClass("horizontal");
}
