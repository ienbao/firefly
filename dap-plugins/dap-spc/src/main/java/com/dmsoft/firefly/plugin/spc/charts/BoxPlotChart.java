package com.dmsoft.firefly.plugin.spc.charts;

import com.dmsoft.firefly.gui.components.chart.ChartOperatorUtils;
import com.dmsoft.firefly.plugin.spc.charts.data.BoxExtraData;
import com.dmsoft.firefly.plugin.spc.charts.data.BoxPlotChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.ChartTooltip;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.BoxTooltip;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IBoxAndWhiskerData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IPoint;
import com.dmsoft.firefly.plugin.spc.charts.utils.MathUtils;
import com.dmsoft.firefly.plugin.spc.charts.view.Candle;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import com.dmsoft.firefly.sdk.plugin.apis.annotation.Chart;
import com.dmsoft.firefly.sdk.utils.ColorUtils;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.animation.FadeTransition;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by cherry on 2018/2/10.
 */
public class BoxPlotChart extends XYChart<Number, Number> {

    private double candleWidth = 10;
    private boolean candleWidthByUnit = false;
    private boolean gridLineChanged = false;

    private ObservableList<Data<Number, Number>> outliers;
    private Map<String, XYChart.Series> uniqueKeySeriesMap = Maps.newHashMap();
    private Map<XYChart.Series, String> seriesUniqueKeyMap = Maps.newHashMap();
    private Map<String, List<Node>> uniqueKeyNodesMap = Maps.newHashMap();

    /**
     * Constructs a BoxPlotChart given the two axes. The initial content for the chart
     * plot background and plot area that includes vertical and horizontal grid
     * lines and fills, are added.
     *
     * @param xAxis X Axis for this XY chart
     * @param yAxis Y Axis for this XY chart
     */
    public BoxPlotChart(Axis<Number> xAxis, Axis<Number> yAxis) {
        super(xAxis, yAxis);
        super.setData(FXCollections.observableArrayList());
        super.setHorizontalZeroLineVisible(false);
        super.setHorizontalGridLinesVisible(false);
        super.setVerticalZeroLineVisible(false);
        super.setAnimated(false);
        xAxis.setAnimated(false);
        yAxis.setAnimated(false);
        outliers = FXCollections.observableArrayList(d -> new Observable[]{d.YValueProperty()});
        outliers.addListener((InvalidationListener) observable -> layoutPlotChildren());
    }

    // -------------- METHODS ------------------------------------------------------------------------------------------

    /**
     * Set box plot chart data
     *
     * @param boxPlotChartDataList box plot chart data
     * @param chartTooltip         chartTooltip
     */
    public void setData(List<BoxPlotChartData> boxPlotChartDataList, ChartTooltip chartTooltip) {
        this.removeAllChildren();
        if (boxPlotChartDataList == null) {
            return;
        }
        setAxisRange(boxPlotChartDataList);
        boxPlotChartDataList.forEach(boxPlotChartData -> createChartSeries(boxPlotChartData, chartTooltip));
    }

    private void setAxisRange(List<BoxPlotChartData> boxPlotChartDataList) {
        Double[] xLower = new Double[boxPlotChartDataList.size()];
        Double[] xUpper = new Double[boxPlotChartDataList.size()];
        Double[] yLower = new Double[boxPlotChartDataList.size()];
        Double[] yUpper = new Double[boxPlotChartDataList.size()];
        for (int i = 0; i < boxPlotChartDataList.size(); i++) {
            xLower[i] = (Double) boxPlotChartDataList.get(i).getXLowerBound();
            xUpper[i] = (Double) boxPlotChartDataList.get(i).getXUpperBound();
            yLower[i] = (Double) boxPlotChartDataList.get(i).getYLowerBound();
            yUpper[i] = (Double) boxPlotChartDataList.get(i).getYUpperBound();
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
        yMax += (yMax - yMin) * UIConstant.Y_FACTOR;
        yMin -= (yMax - yMin) * UIConstant.Y_FACTOR;
        Map<String, Object> yAxisRangeData = ChartOperatorUtils.getAdjustAxisRangeData(yMax, yMin, (int) Math.ceil(yMax - yMin));
        double newYMin = (Double) yAxisRangeData.get(ChartOperatorUtils.KEY_MIN);
        double newYMax = (Double) yAxisRangeData.get(ChartOperatorUtils.KEY_MAX);
        yAxis.setLowerBound(newYMin);
        yAxis.setUpperBound(newYMax);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(xMax + UIConstant.X_FACTOR);
        ChartOperatorUtils.updateAxisTickUnit(xAxis);
        ChartOperatorUtils.updateAxisTickUnit(yAxis);
    }

    private void createChartSeries(BoxPlotChartData chartData, ChartTooltip chartTooltip) {
//        1.设置箱子数据
//        2.设置线的数据
//        3.设置点的数据
//        4.设置箱子样式
//        5.设置线的样式
//        6.设置点的样式
        if (chartData == null) {
            return;
        }
        String uniqueKey = chartData.getUniqueKey();
        Color color = chartData.getColor();
        this.addPoints(chartData.getPoints(), uniqueKey);
        if (chartData.getBoxAndWhiskerData() != null) {
            XYChart.Series<Number, Number> series = buildSeries(chartData.getBoxAndWhiskerData(), chartData.getSeriesName());
            this.uniqueKeySeriesMap.put(uniqueKey, series);
            this.seriesUniqueKeyMap.put(series, uniqueKey);
            this.getData().add(series);
            this.setDataNodeStyleAndTooltip(series, color, chartTooltip == null ? null : chartTooltip.getChartBoxTooltip());
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
     * Remove all chart nodes and all chart data
     */
    public void removeAllChildren() {
        ObservableList<Node> nodes = getPlotChildren();
        getPlotChildren().removeAll(nodes);
        getData().setAll(FXCollections.observableArrayList());
        uniqueKeySeriesMap.clear();
        seriesUniqueKeyMap.clear();
        uniqueKeyNodesMap.clear();
        outliers.setAll(FXCollections.observableArrayList());
        gridLineChanged = false;
    }

    public void toggleVerticalGridLine(boolean showLined) {
        gridLineChanged = true;
        this.setVerticalGridLinesVisible(showLined);
    }

    private Series buildSeries(IBoxAndWhiskerData data, String seriesName) {
        XYChart.Series<Number, Number> series = new XYChart.Series();
        series.setName(seriesName);
        for (int i = 0; i < data.getLen(); i++) {
            boolean valid = data.getXPosByIndex(i) != null;
            valid = valid && data.getQ3ByIndex(i) != null;
            valid = valid && data.getQ1ByIndex(i) != null;
            valid = valid && data.getMaxRegularValueByIndex(i) != null;
            valid = valid && data.getMinRegularValueByIndex(i) != null;
            valid = valid && data.getMedianByIndex(i) != null;
            if (!valid) {
                continue;
            }
            BoxExtraData boxExtraData = new BoxExtraData(
                    data.getXPosByIndex(i),
                    data.getMeanByIndex(i),
                    data.getQ3ByIndex(i),
                    data.getQ1ByIndex(i),
                    data.getMaxRegularValueByIndex(i),
                    data.getMinRegularValueByIndex(i),
                    data.getMedianByIndex(i),
                    data.getColor());

            series.getData().add(
                    new XYChart.Data(
                            data.getXPosByIndex(i),
                            data.getQ3ByIndex(i),
                            boxExtraData)
            );
        }
        return series;
    }

    private void setDataNodeStyleAndTooltip(XYChart.Series series,
                                            Color color,
                                            Function<BoxTooltip, Node> pointTooltipFunction) {

        ObservableList<Data<Number, Number>> data = series.getData();
        if (color != null) {
            series.getNode().setStyle("-fx-stroke: " + ColorUtils.toHexFromFXColor(color));
        }
        series.getNode().getStyleClass().add("candlestick-series");
        data.forEach(dataItem -> {
            if (DAPStringUtils.isNotBlank(ColorUtils.toHexFromFXColor(color))) {
                if (dataItem.getNode() instanceof Candle) {
                    Candle candle = (Candle) dataItem.getNode();
                    candle.updateColor(color);
                }
            }
            if (pointTooltipFunction != null) {
                Node itemNode = dataItem.getNode();
                if (dataItem.getExtraValue() instanceof BoxExtraData) {
                    BoxExtraData extra = (BoxExtraData) dataItem.getExtraValue();
                    BoxTooltip boxTooltip = new BoxTooltip();
                    boxTooltip.setQ1(extra.getQ1().doubleValue());
                    boxTooltip.setQ3(extra.getQ3().doubleValue());
                    boxTooltip.setMaxRegularValue(extra.getMaxRegularValue().doubleValue());
                    boxTooltip.setMinRegularValue(extra.getMinRegularValue().doubleValue());
                    boxTooltip.setMedian(extra.getMedian().doubleValue());
                    Tooltip tooltip = new Tooltip();
                    tooltip.setGraphic(pointTooltipFunction.apply(boxTooltip));
                    tooltip.getStyleClass().setAll("candlestick-tooltip");
                    Tooltip.install(itemNode, tooltip);
                }
            }
        });
    }

    private void setSeriesDataStyleByDefault(XYChart.Series series, Color color) {
        ObservableList<Data<Number, Number>> data = series.getData();
        if (color != null) {
            series.getNode().setStyle("-fx-stroke: " + ColorUtils.toHexFromFXColor(color));
        }
        series.getNode().getStyleClass().add("candlestick-series");
        data.forEach(dataItem -> {
            if (DAPStringUtils.isNotBlank(ColorUtils.toHexFromFXColor(color))) {
                if (dataItem.getNode() instanceof Candle) {
                    Candle candle = (Candle) dataItem.getNode();
                    candle.updateColor(color);
                }
            }
        });
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

    /**
     * Called to update and layout the content for the plot
     */
    @Override
    protected void layoutPlotChildren() {
        // we have nothing to layout if no data is present
        if (getData() == null || gridLineChanged) {
            return;
        }
        // update candle positions
        for (int seriesIndex = 0; seriesIndex < getData().size(); seriesIndex++) {
            XYChart.Series<Number, Number> series = getData().get(seriesIndex);
            Iterator<Data<Number, Number>> iter = getDisplayedDataIterator(series);
            Path seriesPath = null;
            if (series.getNode() instanceof Path) {
                seriesPath = (Path) series.getNode();
                seriesPath.getElements().clear();
            }
//            draw box plot chart
            while (iter.hasNext()) {
                XYChart.Data<Number, Number> item = iter.next();
                Node itemNode = item.getNode();
                BoxExtraData extra = (BoxExtraData) item.getExtraValue();
                double x = getXAxis().getDisplayPosition(getCurrentDisplayedXValue(item));
                double y = getYAxis().getDisplayPosition(getCurrentDisplayedYValue(item));
                this.drawCandle(itemNode, extra, x, y);
                this.drawSeriesPath(seriesPath, extra, x);
            }
        }

//        draw outliers
        for (Data<Number, Number> outlier : outliers) {
            double x = getXAxis().getDisplayPosition(getCurrentDisplayedXValue(outlier));
            double y = getYAxis().getDisplayPosition(getCurrentDisplayedYValue(outlier));
            Circle circle = (Circle) outlier.getNode();
            circle.setCenterX(x);
            circle.setCenterY(y);
            circle.setRadius(2);
        }
    }

    private void addSymbol(Data<Number, Number> symbol, Color color, String uniqueKey) {
        Circle circle = new Circle();
        symbol.setNode(circle);
        getPlotChildren().add(circle);
        addToUniqueKeyNodes(uniqueKey, circle);
        outliers.add(symbol);
        Tooltip.install(circle, new Tooltip("x: " + symbol.getXValue() + "   y: " + symbol.getYValue()));
        circle.setFill(color);
        circle.setCache(true);
    }

    private void addPoints(IPoint points, String uniqueKey) {
        if (points == null) {
            return;
        }
        int len = points.getLen();
        for (int i = 0; i < len; i++) {
            Data<Number, Number> data = new Data<>();
            if (points.getXByIndex(i) == null || points.getYByIndex(i) == null) {
                continue;
            }
            data.setXValue((Number) points.getXByIndex(i));
            data.setYValue((Number) points.getYByIndex(i));
            addSymbol(data, points.getColor(), uniqueKey);
        }
    }

    private void removeSymbol(Data<Number, Number> symbol) {
        if (symbol == null) {
            return;
        }
        if (symbol.getNode() != null) {
            getPlotChildren().remove(symbol.getNode());
            symbol.setNode(null);
        }
        outliers.remove(symbol);
    }

    /**
     * Update chart color
     *
     * @param unique uniqueKey
     * @param color  color
     */
    public void updateChartColor(String unique, Color color) {
        if (uniqueKeySeriesMap.containsKey(unique)) {
            XYChart.Series<Number, Number> series = uniqueKeySeriesMap.get(unique);
//            update path color
            setSeriesDataStyleByDefault(series, color);
        }
    }

    /**
     * Add stroke
     */
    public void addStroke() {
        this.getData().forEach(series -> {
            if (series.getNode().getStyleClass().contains("chart-series-hidden-line")) {
                series.getNode().getStyleClass().remove("chart-series-hidden-line");
            }
        });
    }

    /**
     * Remove stroke
     */
    public void removeStroke() {
        this.getData().forEach(series -> {
            if (!series.getNode().getStyleClass().contains("chart-series-hidden-line")) {
                series.getNode().getStyleClass().add("chart-series-hidden-line");
            }
        });
    }

    /**
     * Toggle stroke show or hide
     *
     * @param showLined if true, show stroke; if false, hidden stroke
     */
    public void toggleStroke(boolean showLined) {

        if (showLined) {
            this.addStroke();
        } else {
            this.removeStroke();
        }
    }

    @Override
    protected void dataItemChanged(XYChart.Data<Number, Number> item) {
    }

    @Override
    protected void dataItemAdded(
            XYChart.Series<Number, Number> series, int itemIndex,
            XYChart.Data<Number, Number> item) {

        Node candle = createCandle(getData().indexOf(series), item, itemIndex);
        addCandle(candle);
        addToSeriesNodes(series, candle);
        // always draw average line on top
        if (series.getNode() != null) {
            series.getNode().toFront();
        }
    }

    @Override
    protected void dataItemRemoved(
            XYChart.Data<Number, Number> item,
            XYChart.Series<Number, Number> series) {

        final Node candle = item.getNode();
        removeCandle(candle);
    }

    @Override
    protected void seriesAdded(XYChart.Series<Number, Number> series, int seriesIndex) {
        // handle any data already in series
        for (int j = 0; j < series.getData().size(); j++) {
            XYChart.Data item = series.getData().get(j);
            Node candle = createCandle(seriesIndex, item, j);
            addCandle(candle);
            addToSeriesNodes(series, candle);
        }
        // create series path
        Path seriesPath = new Path();
        seriesPath.getStyleClass().setAll("candlestick-average-line", "series" + seriesIndex);
        series.setNode(seriesPath);
        getPlotChildren().add(seriesPath);
        addToSeriesNodes(series, seriesPath);
    }

    @Override
    protected void seriesRemoved(XYChart.Series<Number, Number> series) {
        // remove all candle nodes
        for (XYChart.Data<Number, Number> d : series.getData()) {
            final Node candle = d.getNode();
            removeCandle(candle);
        }
    }

    private void addCandle(Node candle) {
        if (shouldAnimate()) {
            candle.setOpacity(0);
            getPlotChildren().add(candle);
            // fade in new candle
            FadeTransition ft = new FadeTransition(Duration.millis(500), candle);
            ft.setToValue(1);
            ft.play();
        } else {
            getPlotChildren().add(candle);
        }
    }

    private void removeCandle(Node candle) {
        if (shouldAnimate()) {
            // fade out old candle
            FadeTransition ft = new FadeTransition(Duration.millis(500), candle);
            ft.setToValue(0);
            ft.setOnFinished((ActionEvent actionEvent) -> {
                getPlotChildren().remove(candle);
            });
            ft.play();
        } else {
            getPlotChildren().remove(candle);
        }
    }

    private void drawCandle(
            Node itemNode,
            BoxExtraData extra,
            double x,
            double y) {

        if (itemNode instanceof Candle && extra != null) {
            Candle candle = (Candle) itemNode;
            final double threshold = 0.90;
            double q1 = getYAxis().getDisplayPosition(extra.getQ1());
            double max = getYAxis().getDisplayPosition(extra.getMaxRegularValue());
            double min = getYAxis().getDisplayPosition(extra.getMinRegularValue());
            // calculate candle width; if candleWidthByUnit, use 90% width between ticks
            candleWidth = (getXAxis() instanceof NumberAxis && candleWidthByUnit)
                    ? getXAxis().getDisplayPosition(((NumberAxis) getXAxis()).getTickUnit()) * threshold : candleWidth;
            // update candle
            candle.update(q1 - y, max - y, min - y, candleWidth, extra.getColor());
            // position the candle
            candle.setLayoutX(x);
            candle.setLayoutY(y);
        }
    }

    private void drawSeriesPath(Path seriesPath, BoxExtraData extra, double x) {
        if (seriesPath != null && extra.getMean() != null) {
            if (seriesPath.getElements().isEmpty()) {
                seriesPath.getElements().add(new MoveTo(x, getYAxis().getDisplayPosition(extra.getMean())));
            } else {
                seriesPath.getElements().add(new LineTo(x, getYAxis().getDisplayPosition(extra.getMean())));
            }
        }
    }

    /**
     * Create a new Candle node to represent a single data item
     */
    private Node createCandle(int seriesIndex, final XYChart.Data item, int itemIndex) {
        Node candle = item.getNode();
        // check if candle has already been created
        if (candle instanceof Candle) {
            ((Candle) candle).setSeriesAndDataStyleClasses("series" + seriesIndex,
                    "data" + itemIndex);
        } else {
            candle = new Candle("series" + seriesIndex, "data" + itemIndex);
            item.setNode(candle);
        }
        return candle;
    }

    /**
     * This is called when the range has been invalidated and we need to update it. If the axis are auto
     * ranging then we compile a list of all data that the given axis has to plot and call invalidateRange() on the
     * axis passing it that data.
     */
    @Override
    protected void updateAxisRange() {
        // For candle stick chart we need to override this method as we need to let the axis know that they need to be able
        // to cover the whole area occupied by the high to low range not just its center data value
        final Axis<Number> xa = getXAxis();
        final Axis<Number> ya = getYAxis();
        List<Number> xData = null;
        List<Number> yData = null;
        if (xa.isAutoRanging()) {
            xData = new ArrayList();
        }
        if (ya.isAutoRanging()) {
            yData = new ArrayList();
        }
        if (xData != null || yData != null) {
            for (XYChart.Series<Number, Number> series : getData()) {
                for (XYChart.Data<Number, Number> data : series.getData()) {
                    if (xData != null) {
                        xData.add(data.getXValue());
                    }
                    if (yData != null) {
                        BoxExtraData extras = (BoxExtraData) data.getExtraValue();
                        if (extras != null) {
                            yData.add(extras.getMaxRegularValue());
                            yData.add(extras.getMinRegularValue());
                        } else {
                            yData.add(data.getYValue());
                        }
                    }
                }
            }
            if (xData != null) {
                xa.invalidateRange(xData);
            }
            if (yData != null) {
                ya.invalidateRange(yData);
            }
        }
    }

    /**
     * Set candle width
     *
     * @param candleWidthByUnit width
     */
    public void setCandleWidthByUnit(boolean candleWidthByUnit) {
        this.candleWidthByUnit = candleWidthByUnit;
    }
}
