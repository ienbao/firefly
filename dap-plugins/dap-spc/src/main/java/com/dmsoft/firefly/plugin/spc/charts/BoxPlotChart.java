package com.dmsoft.firefly.plugin.spc.charts;

import com.dmsoft.firefly.plugin.spc.charts.data.BoxExtraData;
import com.dmsoft.firefly.plugin.spc.charts.data.BoxPlotChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.ChartTooltip;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.BoxTooltip;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IBoxAndWhiskerData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IPoint;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.PointTooltip;
import com.dmsoft.firefly.plugin.spc.charts.view.Candle;
import com.dmsoft.firefly.plugin.spc.utils.BoxChartToolTipContent;
import com.dmsoft.firefly.sdk.utils.ColorUtils;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
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
    private boolean showLined = true;
    private boolean candleWidthByUnit = false;

    private ObservableList<Data<Number, Number>> outliers;
    private Map<String, XYChart.Series> seriesUniqueKeyMap = Maps.newHashMap();

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
        setAnimated(false);
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
     */
    public void setData(List<BoxPlotChartData> boxPlotChartDataList, ChartTooltip chartTooltip) {
        this.removeAllChildren();
        if (boxPlotChartDataList == null) {
            return;
        }
        boxPlotChartDataList.forEach(boxPlotChartData -> createChartSeries(boxPlotChartData, chartTooltip));
    }

    private void createChartSeries(BoxPlotChartData chartData, ChartTooltip chartTooltip) {
//        1.设置箱子数据
//        2.设置线的数据
//        3.设置点的数据
//        4.设置箱子样式
//        5.设置线的样式
//        6.设置点的样式
        String uniqueKey = chartData.getUniqueKey();
        Color color = chartData.getColor();
        XYChart.Series<Number, Number> series = buildSeries(chartData.getBoxAndWhiskerData(), chartData.getSeriesName());
        this.addPoints(chartData.getPoints());
        this.seriesUniqueKeyMap.put(uniqueKey, series);
        this.getData().add(series);
        this.setDataNodeStyleAndTooltip(series, color, chartTooltip == null ? null : chartTooltip.getChartBoxTooltip());
    }

    public void removeAllChildren() {
        ObservableList<Node> nodes = getPlotChildren();
        getPlotChildren().removeAll(nodes);
        getData().setAll(FXCollections.observableArrayList());
        seriesUniqueKeyMap.clear();
        outliers.setAll(FXCollections.observableArrayList());
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
                dataItem.getNode().setStyle("-fx-stroke: " + ColorUtils.toHexFromFXColor(color));
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
                dataItem.getNode().setStyle("-fx-stroke: " + ColorUtils.toHexFromFXColor(color));
            }
        });
    }

    /**
     * Called to update and layout the content for the plot
     */
    @Override
    protected void layoutPlotChildren() {
        // we have nothing to layout if no data is present
        if (getData() == null) {
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

    private void addSymbol(Data<Number, Number> symbol, Color color) {
        Circle circle = new Circle();
        symbol.setNode(circle);
        getPlotChildren().add(circle);
        outliers.add(symbol);
        Tooltip.install(circle, new Tooltip("x: " + symbol.getXValue() + "   y: " + symbol.getYValue()));
        circle.setFill(color);
        circle.setCache(true);
    }

    private void addPoints(IPoint points) {
        if (points == null) return;
        int len = points.getLen();
        for (int i = 0; i < len; i++) {
            Data<Number, Number> data = new Data<>();
            if (points.getXByIndex(i) == null || points.getYByIndex(i) == null) {
                continue;
            }
            data.setXValue((Number) points.getXByIndex(i));
            data.setYValue((Number) points.getYByIndex(i));
            addSymbol(data, points.getColor());
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
        if (seriesUniqueKeyMap.containsKey(unique)) {
            XYChart.Series<Number, Number> series = seriesUniqueKeyMap.get(unique);
//            update path color
            setSeriesDataStyleByDefault(series, color);
//            update box color
            series.getData().forEach(dataItem -> {
                Candle candle = (Candle) dataItem.getNode();
                candle.updateStyleClasses(color);
            });
        }
    }

    public void addStroke() {

        if (showLined) {
            return;
        }
        for (int seriesIndex = 0; seriesIndex < getData().size(); seriesIndex++) {
            XYChart.Series<Number, Number> series = getData().get(seriesIndex);
            Iterator<Data<Number, Number>> iter = getDisplayedDataIterator(series);
            Path seriesPath = null;
            if (series.getNode() instanceof Path) {
                seriesPath = (Path) series.getNode();
                seriesPath.getElements().clear();
            }
            while (iter.hasNext()) {
                XYChart.Data<Number, Number> item = iter.next();
                double x = getXAxis().getDisplayPosition(getCurrentDisplayedXValue(item));
                BoxExtraData extra = (BoxExtraData) item.getExtraValue();
                if (seriesPath != null && extra.getMean() != null) {
                    if (seriesPath.getElements().isEmpty()) {
                        seriesPath.getElements().add(new MoveTo(x, getYAxis().getDisplayPosition(extra.getMean())));
                    } else {
                        seriesPath.getElements().add(new LineTo(x, getYAxis().getDisplayPosition(extra.getMean())));
                    }
                }
            }
        }
        showLined = true;
    }

    public void removeStroke() {
        showLined = false;
        layoutPlotChildren();
    }

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
        }
        // create series path
        Path seriesPath = new Path();
        seriesPath.getStyleClass().setAll("candlestick-average-line", "series" + seriesIndex);
        series.setNode(seriesPath);
        getPlotChildren().add(seriesPath);
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
            double q1 = getYAxis().getDisplayPosition(extra.getQ1());
            double max = getYAxis().getDisplayPosition(extra.getMaxRegularValue());
            double min = getYAxis().getDisplayPosition(extra.getMinRegularValue());

            // calculate candle width; if candleWidthByUnit, use 90% width between ticks
            candleWidth = (getXAxis() instanceof NumberAxis && candleWidthByUnit) ?
                    getXAxis().getDisplayPosition(((NumberAxis) getXAxis()).getTickUnit()) * 0.90 : candleWidth;

            // update candle
            candle.update(q1 - y, max - y, min - y, candleWidth, extra.getColor());
//            candle.updateTooltip(
//                    item.getXValue().doubleValue(),
//                    extra.getMedian().doubleValue(),
//                    extra.getMinRegularValue().doubleValue(),
//                    extra.getMaxRegularValue().doubleValue(),
//                    extra.getQ1().doubleValue(),
//                    extra.getQ3().doubleValue()
//            );

            // position the candle
            candle.setLayoutX(x);
            candle.setLayoutY(y);
        }
    }

    private void drawSeriesPath(Path seriesPath, BoxExtraData extra, double x) {
        if (seriesPath != null && showLined && extra.getMean() != null) {
            if (seriesPath.getElements().isEmpty()) {
                seriesPath.getElements().add(new MoveTo(x, getYAxis().getDisplayPosition(extra.getMean())));
            } else {
                seriesPath.getElements().add(new LineTo(x, getYAxis().getDisplayPosition(extra.getMean())));
            }
        }
    }

    @Override
    protected void seriesRemoved(XYChart.Series<Number, Number> series) {
        // remove all candle nodes
        for (XYChart.Data<Number, Number> d : series.getData()) {
            final Node candle = d.getNode();
            removeCandle(candle);
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

    public void setCandleWidthByUnit(boolean candleWidthByUnit) {
        this.candleWidthByUnit = candleWidthByUnit;
    }
}
