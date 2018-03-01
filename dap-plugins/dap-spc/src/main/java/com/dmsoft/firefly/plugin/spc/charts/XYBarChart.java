package com.dmsoft.firefly.plugin.spc.charts;

import com.dmsoft.firefly.plugin.spc.charts.data.basic.IBarChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IXYChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.PointTooltip;
import com.dmsoft.firefly.plugin.spc.charts.utils.ReflectionUtils;
import com.dmsoft.firefly.plugin.spc.dto.chart.BarCategoryData;
import com.google.common.collect.Maps;
import com.sun.javafx.charts.Legend;
import com.sun.javafx.css.converters.SizeConverter;
import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.*;
import java.util.function.Function;

/**
 * Created with IntelliJ IDEA.
 * User: Pedro
 * Date: 29-08-2013
 * Time: 15:27
 * To change this template use File | Settings | File Templates.
 */
public class XYBarChart<X, Y> extends XYChart<X, Y> {
    // -------------- PRIVATE FIELDS -------------------------------------------

    private Map<Series, Map<Object, Data<X, Y>>> seriesCategoryMap = new HashMap<>();
    private Map<XYChart.Data, BarCategoryData<X, Y>> barCategoryDataMap = Maps.newHashMap();
    private AreaSeries<X, Y> areaSeries = new AreaSeries<>();

    private TreeSet categories = new TreeSet();

    private boolean showTooltip = true;

    private Legend legend = new Legend();
    private boolean seriesRemove = false;
    private final Orientation orientation;

    private ValueAxis valueAxis;

    private Timeline dataRemoveTimeline;
    private Data<X, Y> dataItemBeingRemoved = null;
    private Series<X, Y> seriesOfDataRemoved = null;
    private double bottomPos = 0;
    private static String NEGATIVE_STYLE = "negative";
    // -------------- PUBLIC PROPERTIES ----------------------------------------

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
            return XYBarChart.this;
        }

        public String getName() {
            return "barGap";
        }

        public CssMetaData<XYBarChart<?, ?>, Number> getCssMetaData() {
            return StyleableProperties.BAR_GAP;
        }
    };

    public final double getBarGap() {
        return barGap.getValue();
    }

    public final void setBarGap(double value) {
        barGap.setValue(value);
    }

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
            return XYBarChart.this;
        }

        @Override
        public String getName() {
            return "categoryGap";
        }

        public CssMetaData<XYBarChart<?, ?>, Number> getCssMetaData() {
            return StyleableProperties.CATEGORY_GAP;
        }
    };

    public final double getCategoryGap() {
        return categoryGap.getValue();
    }

    public final void setCategoryGap(double value) {
        categoryGap.setValue(value);
    }

    public final DoubleProperty categoryGapProperty() {
        return categoryGap;
    }

    // -------------- CONSTRUCTOR ----------------------------------------------

    /**
     * Construct a new XYBarChart with the given axis.
     *
     * @param xAxis The x axis to use
     * @param yAxis The y axis to use
     */
    public XYBarChart(Axis<X> xAxis, Axis<Y> yAxis) {
        super(xAxis, yAxis);
        getStyleClass().add("bar-chart");
        setLegend(legend);
        orientation = Orientation.VERTICAL;

        // assuming value axis is the second axis
        valueAxis = (ValueAxis) yAxis;
        // update css
        pseudoClassStateChanged(HORIZONTAL_PSEUDOCLASS_STATE, orientation == Orientation.HORIZONTAL);
        pseudoClassStateChanged(VERTICAL_PSEUDOCLASS_STATE, orientation == Orientation.VERTICAL);
        this.setData(FXCollections.observableArrayList());
        this.setLegendVisible(false);
    }

    /**
     * Construct a new XYBarChart with the given axis and data.
     *
     * @param xAxis        The x axis to use
     * @param yAxis        The y axis to use
     * @param barChartData The data to use, this is the actual list used so any changes to it will be reflected in the chart
     */
    public XYBarChart(Axis<X> xAxis, Axis<Y> yAxis, IBarChartData<X, Y> barChartData) {
        this(xAxis, yAxis);
        this.createChartSeries(barChartData);
    }

    /**
     * Construct a new XYBarChart with the given axis and data.
     *
     * @param xAxis        The x axis to use
     * @param yAxis        The y axis to use
     * @param barChartData The data to use, this is the actual list used so any changes to it will be reflected in the chart
     * @param categoryGap  The gap to leave between bars in separate categories
     */
    public XYBarChart(Axis<X> xAxis, Axis<Y> yAxis, IBarChartData<X, Y> barChartData, double categoryGap) {

        this(xAxis, yAxis, barChartData);
        setCategoryGap(categoryGap);
    }

    public void createChartSeries(IBarChartData<X, Y> barChartData) {

        XYChart.Series oneSeries = this.buildSeries(barChartData);
        this.getData().add(oneSeries);
        this.setSeriesDataStyleByDefault(oneSeries, barChartData.getColor());
        this.setSeriesDataTooltip(oneSeries, null);
    }

    public void addAreaSeries(IXYChartData<X, Y> xyOneChartData) {
        Group areaGroup = this.areaSeries.buildAreaGroup(xyOneChartData);
        areaGroup.setStyle("-fx-stroke: " + xyOneChartData.getColor());
        getPlotChildren().add(areaGroup);
    }

    private void paintAreaSeries() {
        this.areaSeries.paintAreaSeries(this);
    }

    private void setSeriesDataStyleByDefault(XYChart.Series series, String color) {

        ObservableList<Data<X, Y>> data = series.getData();
        data.forEach(dataItem -> {
            dataItem.getNode().setStyle("-fx-background-color: " + color);
        });
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

    /**
     * Populates the tooltip with data (chart-type independent).
     *
     * @param series The series
     * @param data   The data
     */
    public void populateTooltip(final Series<X, Y> series, final Data<X, Y> data) {

        String seriesName = series.getName();
        Tooltip tooltip = new Tooltip(
                seriesName + "\n" + "X[" + data.getXValue() + ", " +
                        data.getExtraValue() + "]" + "\n" + "Y = " + data.getYValue());
        Tooltip.install(data.getNode(), tooltip);
    }

    public XYChart.Series buildSeries(IBarChartData<X, Y> barChartData) {
        XYChart.Series oneSeries = new XYChart.Series();
        oneSeries.setName(barChartData.getSeriesName());
        int length = barChartData.getLen();
        for (int i = 0; i < length; i++) {
            X xValue = barChartData.getStartValueByIndex(i);
            Y yValue = barChartData.getValueByIndex(i);
            if (xValue == null || yValue == null) {
                continue;
            }
            XYChart.Data data = new XYChart.Data<>(xValue, yValue);
            data.setExtraValue(barChartData.getEndValueByIndex(i));
            oneSeries.getData().add(data);
            barCategoryDataMap.put(data,
                    new BarCategoryData(xValue, barChartData.getBarWidthByIndex(i), yValue));
        }
        return oneSeries;
    }

    // -------------- PROTECTED METHODS ----------------------------------------

    @Override
    protected void dataItemAdded(Series<X, Y> series, int itemIndex, Data<X, Y> item) {
        Object category;
        if (orientation == Orientation.VERTICAL) {
            category = item.getXValue();
        } else {
            category = item.getYValue();
        }

        categories.add(category);

        Map<Object, Data<X, Y>> categoryMap = seriesCategoryMap.get(series);

        if (categoryMap == null) {
            categoryMap = new HashMap<Object, Data<X, Y>>();
            seriesCategoryMap.put(series, categoryMap);
        }
        if (categoryMap.containsKey(category)) {
            // RT-21162 : replacing the previous data, first remove the node from scenegraph.
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
        }
        getPlotChildren().add(bar);
    }

    @Override
    protected void dataItemRemoved(final Data<X, Y> item, final Series<X, Y> series) {
        final Node bar = item.getNode();
        if (shouldAnimate()) {
            dataRemoveTimeline = createDataRemoveTimeline(item, bar, series);
            dataItemBeingRemoved = item;
            seriesOfDataRemoved = series;
            dataRemoveTimeline.setOnFinished(event -> {
                ReflectionUtils.forceMethodCall(Data.class, "setSeries", item, new Object[]{null}); // TODO: make sure this is working as expected
                getPlotChildren().remove(bar);
                removeDataItemFromDisplay(series, item);
                dataItemBeingRemoved = null;
                updateMap(series, item);
            });
            dataRemoveTimeline.play();
        } else {
            ReflectionUtils.forceMethodCall(Data.class, "setSeries", item, new Object[]{null}); // TODO: make sure this is working as expected
            getPlotChildren().remove(bar);
            removeDataItemFromDisplay(series, item);
            updateMap(series, item);
        }
        barCategoryDataMap.remove(item);

    }

    /**
     * @inheritDoc
     */
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
        // handle any data already in series
        // create entry in the map
        Map<Object, Data<X, Y>> categoryMap = new HashMap<Object, Data<X, Y>>();
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
                double barVal = (orientation == Orientation.VERTICAL) ? ((Number) item.getYValue()).doubleValue() :
                        ((Number) item.getXValue()).doubleValue();
                if (barVal < 0) {
                    bar.getStyleClass().add(NEGATIVE_STYLE);
                }
                getPlotChildren().add(bar);
            }
        }
        if (categoryMap.size() > 0) seriesCategoryMap.put(series, categoryMap);
    }

    @Override
    protected void seriesRemoved(final Series<X, Y> series) {
        updateDefaultColorIndex(series);
        // remove all symbol nodes
        if (shouldAnimate()) {
            ParallelTransition pt = new ParallelTransition();
            pt.setOnFinished(event -> removeSeriesFromDisplay(series));
            for (final Data<X, Y> d : series.getData()) {
                final Node bar = d.getNode();
                seriesRemove = true;
                // Animate series deletion
                if (/*getSeriesSize()*/((int) ReflectionUtils.forceMethodCall(XYChart.class, "getSeriesSize", this)) > 1) {
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

//    static int layoutPlotChildrenCount = 1;

    /**
     * @inheritDoc
     */
    @Override
    protected void layoutPlotChildren() {
        this.paintBarPlot();
        this.paintAreaSeries();
    }

    private void paintBarPlot() {
        double barWidth = 0;
        double categorySize = 0;
        SortedSet categoriesOnScreen = getCategoriesOnScreen();
        if (categoriesOnScreen == null)
            return;

//        categorySize = calculateCategorySize(categoriesOnScreen);
//        barWidth = calculateBarWidth(categorySize);

        barWidth = calculateBarWidth(categoriesOnScreen);

//        if (categorySize == 0) categorySize = getData().size() / 2d;
        if (barWidth == 0) barWidth = 0.5;

        final double barOffset = -(categorySize / 2d);
        final double zeroPos = (valueAxis.getLowerBound() > 0) ?
                valueAxis.getDisplayPosition(valueAxis.getLowerBound()) : valueAxis.getZeroPosition();
        int catIndex = 0;
        for (Object category : categories) {
            int index = 0;
            for (Iterator<Series<X, Y>> sit = getDisplayedSeriesIterator(); sit.hasNext(); ) {
                Series<X, Y> series = sit.next();
                final Data<X, Y> item = getDataItem(series, index, catIndex, category);
                if (item != null) {
                    final Node bar = item.getNode();
                    final double categoryPos;
                    final double valPos;
                    double barWidthSize = (Double) barCategoryDataMap.get(item).getBarWidth();
                    X currentX = (X) ReflectionUtils.forceMethodCall(Data.class, "getCurrentX", item);
                    Y currentY = (Y) ReflectionUtils.forceMethodCall(Data.class, "getCurrentY", item);
                    if (orientation == Orientation.VERTICAL) {
                        categoryPos = getXAxis().getDisplayPosition(currentX);
                        valPos = getYAxis().getDisplayPosition(currentY);
                    } else {
                        categoryPos = getYAxis().getDisplayPosition(currentY);
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
                                bottom, barWidth * barWidthSize, top - bottom);
                    } else {
                        //noinspection SuspiciousNameCombination
                        bar.resizeRelocate(bottom, categoryPos + (barWidth * barWidthSize + getBarGap()) * index,
                                top - bottom, barWidth * barWidthSize);
                    }

                    index++;
                }
            }
            catIndex++;
        }
    }

    private double calculateBarWidth(SortedSet categories) {
        ValueAxis xAxis = (ValueAxis) getXAxis();
        double firstValue = (double) categories.first();
        double mark = xAxis.getDisplayPosition(firstValue) / firstValue;
        return mark;
    }

    private double calculateCategorySize(SortedSet categories) {
        double halfCategorySize = Double.POSITIVE_INFINITY;
        ValueAxis xAxis = (ValueAxis) getXAxis();

        double firstMark = xAxis.getDisplayPosition(xAxis.getLowerBound());
        Iterator iterator = categories.iterator();
        double secondMark = xAxis.getDisplayPosition((Number) iterator.next());
        halfCategorySize = Math.min(secondMark - firstMark, halfCategorySize);
        firstMark = secondMark;
        for (; iterator.hasNext(); ) {
            secondMark = xAxis.getDisplayPosition((Number) iterator.next());
            halfCategorySize = Math.min((secondMark - firstMark) / 2d, halfCategorySize);
            firstMark = secondMark;
        }
        halfCategorySize = Math.min(xAxis.getDisplayPosition(xAxis.getUpperBound()) - xAxis.getDisplayPosition((Number) categories.last()), halfCategorySize);

        return halfCategorySize * 2;
    }

    private double calculateBarWidth(double categorySize) {
        int nSeries = (int) ReflectionUtils.forceMethodCall(XYChart.class, "getSeriesSize", this);
        double barWidth = (categorySize - (nSeries - 1) * getBarGap()) / nSeries;
        return barWidth;
    }

    private SortedSet getCategoriesOnScreen() {
        ValueAxis categoryAxis = (ValueAxis) getXAxis();
        Object lowestCategoryShowing = categories.higher(categoryAxis.getLowerBound());
        if (lowestCategoryShowing == null)
            return null;

        Object highestCategoryShowing = categories.lower(categoryAxis.getUpperBound());
        if (highestCategoryShowing == null)
            return null;

        if (Double.valueOf(highestCategoryShowing + "") < Double.valueOf(lowestCategoryShowing + ""))
            return null;

        return categories.subSet(lowestCategoryShowing, true, highestCategoryShowing, true);
    }

    /**
     * This is called whenever a series is added or removed and the legend needs to be updated
     */
    @Override
    protected void updateLegend() {
        legend.getItems().clear();
        if (getData() != null) {
            for (int seriesIndex = 0; seriesIndex < getData().size(); seriesIndex++) {
                Series series = getData().get(seriesIndex);
                Legend.LegendItem legenditem = new Legend.LegendItem(series.getName());
                legenditem.getSymbol().getStyleClass().addAll("chart-bar", "series" + seriesIndex, "bar-legend-symbol",
                        (String) ReflectionUtils.forceFieldCall(Series.class, "defaultColorStyleClass", series));
                legend.getItems().add(legenditem);
            }
        }
        if (legend.getItems().size() > 0) {
            if (getLegend() == null) {
                setLegend(legend);
            }
        } else {
            setLegend(null);
        }
    }

    // -------------- PRIVATE METHODS ------------------------------------------

    private void updateMap(Series series, Data item) {
        final Object category = (orientation == Orientation.VERTICAL) ? item.getXValue() :
                item.getYValue();
        Map<Object, Data<X, Y>> categoryMap = seriesCategoryMap.get(series);
        if (categoryMap != null) {
            categoryMap.remove(category);
            categories.remove(category);
            if (categoryMap.isEmpty()) seriesCategoryMap.remove(series);
        }
//        if (seriesCategoryMap.isEmpty() && categoryAxis.isAutoRanging()) categoryAxis.getCategories().clear();
    }

    private void animateDataAdd(Data<X, Y> item, Node bar) {
        double barVal;
        if (orientation == Orientation.VERTICAL) {
            barVal = ((Number) item.getYValue()).doubleValue();
            if (barVal < 0) {
                bar.getStyleClass().add(NEGATIVE_STYLE);
            }
            //item.setCurrentY(getYAxis().toRealValue((barVal < 0) ? -bottomPos : bottomPos));
            ReflectionUtils.forceMethodCall(Data.class, "setCurrentY", item, new Class[]{Object.class}, new Object[]{getYAxis().toRealValue((barVal < 0) ? -bottomPos : bottomPos)});
            getPlotChildren().add(bar);
            item.setYValue(getYAxis().toRealValue(barVal));
//            animate(
//                    new KeyFrame(Duration.ZERO, new KeyValue(item.currentYProperty(),
//                            item.getCurrentY())),
//                    new KeyFrame(Duration.millis(700),
//                            new KeyValue(item.currentYProperty(), item.getYValue(), Interpolator.EASE_BOTH))
//            );
            ReflectionUtils.forceMethodCall(Chart.class, "animate", this, new Object[]{new KeyFrame[]{
                    new KeyFrame(Duration.ZERO, new KeyValue(/*item.currentYProperty(),*/ (ObjectProperty<Y>) ReflectionUtils.forceMethodCall(Data.class, "currentYProperty", item),
                                     /*item.getCurrentY()*/ (Y) ReflectionUtils.forceMethodCall(Data.class, "getCurrentY", item))),
                    new KeyFrame(Duration.millis(700),
                            new KeyValue(/*item.currentYProperty()*/(ObjectProperty<Y>) ReflectionUtils.forceMethodCall(Data.class, "currentYProperty", item), item.getYValue(), Interpolator.EASE_BOTH))}}
            );
        } else {
            barVal = ((Number) item.getXValue()).doubleValue();
            if (barVal < 0) {
                bar.getStyleClass().add(NEGATIVE_STYLE);
            }
            //item.setCurrentX(getXAxis().toRealValue((barVal < 0) ? -bottomPos : bottomPos));
            ReflectionUtils.forceMethodCall(Data.class, "setCurrentX", item, new Class[]{Object.class}, new Object[]{getXAxis().toRealValue((barVal < 0) ? -bottomPos : bottomPos)});
            getPlotChildren().add(bar);
            item.setXValue(getXAxis().toRealValue(barVal));
            ReflectionUtils.forceMethodCall(Chart.class, "animate", this,
                    new KeyFrame(Duration.ZERO, new KeyValue(/*item.currentXProperty(),*/(ObjectProperty<X>) ReflectionUtils.forceMethodCall(Data.class, "currentXProperty", item),
                            /*item.getCurrentX()*/ (X) ReflectionUtils.forceMethodCall(Data.class, "getCurrentX", item))),
                    new KeyFrame(Duration.millis(700),
                            new KeyValue(/*item.currentXProperty()*/ (ObjectProperty<X>) ReflectionUtils.forceMethodCall(Data.class, "currentXProperty", item), item.getXValue(), Interpolator.EASE_BOTH))
            );
        }
    }

    private Timeline createDataRemoveTimeline(final Data<X, Y> item, final Node bar, final Series<X, Y> series) {
        Timeline t = new Timeline();
        if (orientation == Orientation.VERTICAL) {
            //            item.setYValue(getYAxis().toRealValue(getYAxis().getZeroPosition()));
            item.setYValue(getYAxis().toRealValue(bottomPos));
            t.getKeyFrames().addAll(new KeyFrame(Duration.ZERO,
                            new KeyValue(/*item.currentYProperty()*/(ObjectProperty<Y>) ReflectionUtils.forceMethodCall(Data.class, "currentYProperty", item), /*item.getCurrentY()*/ (Y) ReflectionUtils.forceMethodCall(Data.class, "getCurrentY", item))),
                    new KeyFrame(Duration.millis(700), new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            getPlotChildren().remove(bar);
                            updateMap(series, item);
                        }
                    },
                            new KeyValue(/*item.currentYProperty()*/ (ObjectProperty<Y>) ReflectionUtils.forceMethodCall(Data.class, "currentYProperty", item), item.getYValue(),
                                    Interpolator.EASE_BOTH)));
        } else {
            item.setXValue(getXAxis().toRealValue(getXAxis().getZeroPosition()));
            t.getKeyFrames().addAll(new KeyFrame(Duration.ZERO, new KeyValue(/*item.currentXProperty()*/(ObjectProperty<X>) ReflectionUtils.forceMethodCall(Data.class, "currentXProperty", item), /*item.getCurrentX()*/ (X) ReflectionUtils.forceMethodCall(Data.class, "getCurrentX", item))),
                    new KeyFrame(Duration.millis(700), new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            getPlotChildren().remove(bar);
                            updateMap(series, item);
                        }
                    },
                            new KeyValue(/*item.currentXProperty()*/ (ObjectProperty<X>) ReflectionUtils.forceMethodCall(Data.class, "currentXProperty", item), item.getXValue(),
                                    Interpolator.EASE_BOTH)));
        }
        return t;
    }

    private void updateDefaultColorIndex(final Series<X, Y> series) {
        //int clearIndex = seriesColorMap.get(series);
        Map<Series, Integer> seriesColorMapValue = (Map<Series, Integer>) ReflectionUtils.forceFieldCall(XYChart.class, "seriesColorMap", this);
        int clearIndex = seriesColorMapValue.get(series);

        //colorBits.clear(clearIndex);
        BitSet colorBitsValue = (BitSet) ReflectionUtils.forceFieldCall(XYChart.class, "colorBits", this);

        // DEFAULT_COLOR
        String DEFAULT_COLOR_VALUE = (String) ReflectionUtils.forceFieldCall(XYChart.class, "DEFAULT_COLOR", null);

        for (Data<X, Y> d : series.getData()) {
            final Node bar = d.getNode();
            if (bar != null) {
                bar.getStyleClass().remove(DEFAULT_COLOR_VALUE + clearIndex);
                colorBitsValue.clear(clearIndex);
            }
        }
        seriesColorMapValue.remove(series);
    }

    private Node createBar(Series series, int seriesIndex, final Data item, int itemIndex) {
        Node bar = item.getNode();
        if (bar == null) {
            bar = new StackPane();
            item.setNode(bar);
        }

        String defaultColorStyleClassValue = (String) ReflectionUtils.forceFieldCall(Series.class, "defaultColorStyleClass", series);
        bar.getStyleClass().addAll("chart-bar", "series" + seriesIndex, "data" + itemIndex, defaultColorStyleClassValue);
        return bar;
    }

    private Data<X, Y> getDataItem(Series<X, Y> series, Object category) {
        Map<Object, Data<X, Y>> catmap = seriesCategoryMap.get(series);
        return (catmap != null) ? catmap.get(category) : null;
    }

    private Data<X, Y> getDataItem(Series<X, Y> series, int seriesIndex, int itemIndex, Object category) {
        Map<Object, Data<X, Y>> catmap = seriesCategoryMap.get(series);
        return (catmap != null) ? catmap.get(category) : null;
    }

    // -------------- STYLESHEET HANDLING ------------------------------------------------------------------------------

    /**
     * Super-lazy instantiation pattern from Bill Pugh.
     *
     * @treatAsPrivate implementation detail
     */
    private static class StyleableProperties {
        private static final CssMetaData<XYBarChart<?, ?>, Number> BAR_GAP =
                new CssMetaData<XYBarChart<?, ?>, Number>("-fx-bar-gap",
                        SizeConverter.getInstance(), 4.0) {

                    @Override
                    public boolean isSettable(XYBarChart<?, ?> node) {
                        return node.barGap == null || !node.barGap.isBound();
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(XYBarChart<?, ?> node) {
                        return (StyleableProperty<Number>) node.barGapProperty();
                    }
                };

        private static final CssMetaData<XYBarChart<?, ?>, Number> CATEGORY_GAP =
                new CssMetaData<XYBarChart<?, ?>, Number>("-fx-category-gap",
                        SizeConverter.getInstance(), 10.0) {

                    @Override
                    public boolean isSettable(XYBarChart<?, ?> node) {
                        return node.categoryGap == null || !node.categoryGap.isBound();
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(XYBarChart<?, ?> node) {
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
        return StyleableProperties.STYLEABLES;
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
