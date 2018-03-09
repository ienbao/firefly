package com.dmsoft.firefly.plugin.spc.charts;

import com.dmsoft.firefly.plugin.spc.charts.data.BoxExtraData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IBoxAndWhiskerData;
import com.dmsoft.firefly.plugin.spc.charts.view.Candle;
import com.dmsoft.firefly.sdk.utils.ColorUtils;
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

/**
 * Created by cherry on 2018/2/10.
 */
public class BoxPlotChart extends XYChart<Number, Number> {

    private ObservableList<Data<Number, Number>> outliers;
    private double candleWidth = 10;
    private boolean showLined = true;
    private boolean candleWidthByUnit = false;

    /**
     * Constructs a BoxPlotChart given the two axes. The initial content for the chart
     * plot background and plot area that includes vertical and horizontal grid
     * lines and fills, are added.
     *
     * @param xAxis X Axis for this XY chart
     * @param yAxis Y Axis for this XY chart
     */
    public BoxPlotChart(Axis<Number> xAxis, Axis<Number> yAxis) {
        this(xAxis, yAxis, null);
    }

    /**
     * Construct a new CandleStickChart with the given axis and data.
     */
    public BoxPlotChart(Axis<Number> xAxis, Axis<Number> yAxis, IBoxAndWhiskerData data) {
        super(xAxis, yAxis);
        if (data == null) {
            setData(FXCollections.observableArrayList());
        } else {
            createChartSeries(data);
        }
        setAnimated(false);
        xAxis.setAnimated(false);
        yAxis.setAnimated(false);
        outliers = FXCollections.observableArrayList(d -> new Observable[]{d.YValueProperty()});
        outliers.addListener((InvalidationListener) observable -> layoutPlotChildren());
    }

    // -------------- METHODS ------------------------------------------------------------------------------------------

    public void createChartSeries(IBoxAndWhiskerData data) {
        if (data == null) {
            return;
        }
        XYChart.Series<Number, Number> series = buildSeries(data);
        this.getData().add(series);
        this.setSeriesDataStyleByDefault(series, data.getColor());
    }

    private Series buildSeries(IBoxAndWhiskerData data) {
        XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
        for (int i = 0; i < data.getLen(); i++) {

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

    private void setSeriesDataStyleByDefault(XYChart.Series series, Color color) {

        ObservableList<Data<Number, Number>> data = series.getData();
        if (color != null) {
            series.getNode().setStyle("-fx-stroke: " + ColorUtils.toHexFromFXColor(color));
        }
        series.getNode().getStyleClass().add("candlestick-series");
        data.forEach(dataItem -> {
            if (color != null) {
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
                this.drawCandle(itemNode, item, extra, x, y);
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

    public void removeAllChildren() {
        ObservableList<Node> nodes = getPlotChildren();
        getPlotChildren().removeAll(nodes);
        getData().setAll(FXCollections.observableArrayList());
        outliers.setAll(FXCollections.observableArrayList());
    }

    public void addSymbol(Data<Number, Number> symbol) {
        Circle circle = new Circle();
        symbol.setNode(circle);
        getPlotChildren().add(circle);
        outliers.add(symbol);
        Tooltip.install(circle, new Tooltip("x: " + symbol.getXValue() + "   y: " + symbol.getYValue()));
        circle.setFill(Color.RED);
        circle.setCache(true);
    }

    public void removeSymbol(Data<Number, Number> symbol) {
        if (symbol == null) {
            return;
        }
        if (symbol.getNode() != null) {
            getPlotChildren().remove(symbol.getNode());
            symbol.setNode(null);
        }
        outliers.remove(symbol);
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
                if (seriesPath != null) {
                    if (seriesPath.getElements().isEmpty()) {
                        seriesPath.getElements().add(new MoveTo(x, getYAxis().getDisplayPosition(extra.getMedian())));
                    } else {
                        seriesPath.getElements().add(new LineTo(x, getYAxis().getDisplayPosition(extra.getMedian())));
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
            XYChart.Data<Number, Number> item,
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
            candle.updateTooltip(
                    item.getXValue().doubleValue(),
                    extra.getMedian().doubleValue(),
                    extra.getMinRegularValue().doubleValue(),
                    extra.getMaxRegularValue().doubleValue(),
                    extra.getQ1().doubleValue(),
                    extra.getQ3().doubleValue()
            );

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
//            LineTo line = new LineTo(x + candleWidth, getYAxis().getDisplayPosition(extra.getMean()));
//            seriesPath.getElements().add(line);
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
