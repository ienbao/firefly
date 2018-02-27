package com.dmsoft.firefly.plugin.spc.charts;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.AccessibleRole;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.*;

/**
 * Created by cherry on 2018/2/11.
 */
public class NDChart<X, Y> extends BarChart {

    private ObservableList<Series<X, Y>> areaSeries = FXCollections.observableArrayList();

    /**
     * A multiplier for teh Y values that we store for each series, it is used to animate in a new series
     */
    private Map<Series<X, Y>, DoubleProperty> seriesYMultiplierMap = new HashMap<>();

    public NDChart(CategoryAxis xAxis, ValueAxis yAxis) {
        this(xAxis, yAxis, FXCollections.observableArrayList());
    }

    /**
     * Constructs a NDChart given the two axes. The initial content for the chart
     * plot background and plot area that includes vertical and horizontal grid
     * lines and fills, are added.
     *
     * @param xAxis      X Axis for this bar chart
     * @param yAxis      Y Axis for this bar chart
     * @param data       This bar chart series data
     * @param areaSeries Area chart series data
     */
    public NDChart(
            CategoryAxis xAxis,
            ValueAxis yAxis,
            ObservableList data,
            ObservableList<Series<X, Y>> areaSeries) {

        super(xAxis, yAxis, data);
        this.setLegendVisible(false);
        this.areaSeries = areaSeries == null ? FXCollections.observableArrayList() : areaSeries;

//        Add area series data and node
        for (int i = 0; i < areaSeries.size(); i++) {
            addAreaSeries(areaSeries.get(i));
        }
    }

    /**
     * Constructs a NDChart given the two axes. The initial content for the chart
     * plot background and plot area that includes vertical and horizontal grid
     * lines and fills, are added.
     *
     * @param xAxis X Axis for this bar chart
     * @param yAxis Y Axis for this bar chart
     * @param data  This bar chart series data
     */
    public NDChart(CategoryAxis xAxis, ValueAxis yAxis, ObservableList data) {
        this(xAxis, yAxis, data, FXCollections.observableArrayList());
    }

    @Override
    protected void layoutPlotChildren() {

        super.layoutPlotChildren();

        List<LineTo> constructedPath = new ArrayList<>(areaSeries.size());
        for (int seriesIndex = 0; seriesIndex < areaSeries.size(); seriesIndex++) {
            double lastX = 0;
            Series<X, Y> series = areaSeries.get(seriesIndex);
            final ObservableList<Node> children = ((Group) series.getNode()).getChildren();
            ObservableList<PathElement> seriesLine = ((Path) children.get(1)).getElements();
            ObservableList<PathElement> fillPath = ((Path) children.get(0)).getElements();
            seriesLine.clear();
            fillPath.clear();
            constructedPath.clear();
            for (int i = 0; i < series.getData().size(); i++) {
                Data<X, Y> item = series.getData().get(i);
                double x = getXAxis().getDisplayPosition(getCurrentDisplayedXValue(item));
                double y = getYAxis().getDisplayPosition(getCurrentDisplayedYValue(item));
                constructedPath.add(new LineTo(x, y));
                if (Double.isNaN(x) || Double.isNaN(y)) {
                    continue;
                }
                lastX = x;
                Node symbol = item.getNode();
                if (symbol != null) {
                    final double w = symbol.prefWidth(-1);
                    final double h = symbol.prefHeight(-1);
                    symbol.resizeRelocate(x - (w / 2), y - (h / 2), w, h);
                }
            }

            if (!constructedPath.isEmpty()) {
                Collections.sort(constructedPath, (e1, e2) -> Double.compare(e1.getX(), e2.getX()));
                LineTo first = constructedPath.get(0);

                final double displayYPos = first.getY();
                final double numericYPos = getYAxis().toNumericValue(getYAxis().getValueForDisplay(displayYPos));

                // RT-34626: We can't always use getZeroPosition(), as it may be the case
                // that the zero position of the y-axis is not visible on the chart. In these
                // cases, we need to use the height between the point and the y-axis line.
                final double yAxisZeroPos = getYAxis().getZeroPosition();
                final boolean isYAxisZeroPosVisible = !Double.isNaN(yAxisZeroPos);
                final double yAxisHeight = getYAxis().getHeight();
                final double yFillPos = isYAxisZeroPosVisible ? yAxisZeroPos :
                        numericYPos < 0 ? numericYPos - yAxisHeight : yAxisHeight;

                seriesLine.add(new MoveTo(first.getX(), displayYPos));
                fillPath.add(new MoveTo(first.getX(), yFillPos));
                seriesLine.addAll(constructedPath);
                fillPath.addAll(constructedPath);
                fillPath.add(new LineTo(lastX, yFillPos));
                fillPath.add(new ClosePath());
            }
            smooth(seriesLine, fillPath);
        }
    }

    //    Draw smooth line
    private static void smooth(ObservableList<PathElement> strokeElements, ObservableList<PathElement> fillElements) {
        // as we do not have direct access to the data, first recreate the list of all the data points we have
        final Point2D[] dataPoints = new Point2D[strokeElements.size()];
        for (int i = 0; i < strokeElements.size(); i++) {
            final PathElement element = strokeElements.get(i);
            if (element instanceof MoveTo) {
                final MoveTo move = (MoveTo) element;
                dataPoints[i] = new Point2D(move.getX(), move.getY());
            } else if (element instanceof LineTo) {
                final LineTo line = (LineTo) element;
                final double x = line.getX(), y = line.getY();
                dataPoints[i] = new Point2D(x, y);
            }
        }
        // next we need to know the zero Y value
        final double zeroY = ((MoveTo) fillElements.get(0)).getY();
        // now clear and rebuild elements
        strokeElements.clear();
        fillElements.clear();
        Pair<Point2D[], Point2D[]> result = calcCurveControlPoints(dataPoints);
        Point2D[] firstControlPoints = result.getKey();
        Point2D[] secondControlPoints = result.getValue();
        // start both paths
        strokeElements.add(new MoveTo(dataPoints[0].getX(), dataPoints[0].getY()));
        fillElements.add(new MoveTo(dataPoints[0].getX(), zeroY));
        fillElements.add(new LineTo(dataPoints[0].getX(), dataPoints[0].getY()));
        // add curves
        for (int i = 1; i < dataPoints.length; i++) {
            final int ci = i - 1;
            strokeElements.add(new CubicCurveTo(
                    firstControlPoints[ci].getX(), firstControlPoints[ci].getY(),
                    secondControlPoints[ci].getX(), secondControlPoints[ci].getY(),
                    dataPoints[i].getX(), dataPoints[i].getY()));
            fillElements.add(new CubicCurveTo(
                    firstControlPoints[ci].getX(), firstControlPoints[ci].getY(),
                    secondControlPoints[ci].getX(), secondControlPoints[ci].getY(),
                    dataPoints[i].getX(), dataPoints[i].getY()));
        }
        // end the paths
        fillElements.add(new LineTo(dataPoints[dataPoints.length - 1].getX(), zeroY));
        fillElements.add(new ClosePath());
    }

    /**
     * Calculate open-ended Bezier Spline Control Points.
     */
    public static Pair<Point2D[], Point2D[]> calcCurveControlPoints(Point2D[] dataPoints) {
        Point2D[] firstControlPoints;
        Point2D[] secondControlPoints;
        int n = dataPoints.length - 1;
        if (n == 1) { // Special case: Bezier curve should be a straight line.
            firstControlPoints = new Point2D[1];
            // 3P1 = 2P0 + P3
            firstControlPoints[0] = new Point2D(
                    (2 * dataPoints[0].getX() + dataPoints[1].getX()) / 3,
                    (2 * dataPoints[0].getY() + dataPoints[1].getY()) / 3);

            secondControlPoints = new Point2D[1];
            // P2 = 2P1 – P0
            secondControlPoints[0] = new Point2D(
                    2 * firstControlPoints[0].getX() - dataPoints[0].getX(),
                    2 * firstControlPoints[0].getY() - dataPoints[0].getY());
            return new Pair(firstControlPoints, secondControlPoints);
        }

        // Calculate first Bezier control points
        // Right hand side vector
        double[] rhs = new double[n];

        // Set right hand side X values
        for (int i = 1; i < n - 1; ++i) {
            rhs[i] = 4 * dataPoints[i].getX() + 2 * dataPoints[i + 1].getX();
        }
        rhs[0] = dataPoints[0].getX() + 2 * dataPoints[1].getX();
        rhs[n - 1] = (8 * dataPoints[n - 1].getX() + dataPoints[n].getX()) / 2.0;
        // Get first control points X-values
        double[] x = getFirstControlPoints(rhs);

        // Set right hand side Y values
        for (int i = 1; i < n - 1; ++i) {
            rhs[i] = 4 * dataPoints[i].getY() + 2 * dataPoints[i + 1].getY();
        }
        rhs[0] = dataPoints[0].getY() + 2 * dataPoints[1].getY();
        rhs[n - 1] = (8 * dataPoints[n - 1].getY() + dataPoints[n].getY()) / 2.0;
        // Get first control points Y-values
        double[] y = getFirstControlPoints(rhs);

        // Fill output arrays.
        firstControlPoints = new Point2D[n];
        secondControlPoints = new Point2D[n];
        for (int i = 0; i < n; ++i) {
            // First control point
            firstControlPoints[i] = new Point2D(x[i], y[i]);
            // Second control point
            if (i < n - 1) {
                secondControlPoints[i] = new Point2D(2 * dataPoints[i + 1].getX() - x[i + 1], 2
                        * dataPoints[i + 1].getY() - y[i + 1]);
            } else {
                secondControlPoints[i] = new Point2D((dataPoints[n].getX() + x[n - 1]) / 2,
                        (dataPoints[n].getY() + y[n - 1]) / 2);
            }
        }
        return new Pair(firstControlPoints, secondControlPoints);
    }

    /**
     * Solves a tridiagonal system for one of coordinates (x or y) of first
     * Bezier control points.
     */
    private static double[] getFirstControlPoints(double[] rhs) {
        int n = rhs.length;
        // Solution vector.
        double[] x = new double[n];
        // Temp workspace.
        double[] tmp = new double[n];
        double b = 2.0;
        x[0] = rhs[0] / b;
        // Decomposition and forward substitution.
        for (int i = 1; i < n; i++) {
            tmp[i] = 1 / b;
            b = (i < n - 1 ? 4.0 : 3.5) - tmp[i];
            x[i] = (rhs[i] - x[i - 1]) / b;
        }
        for (int i = 1; i < n; i++) {
            // Back substitution.
            x[n - i - 1] -= tmp[n - i] * x[n - i];
        }
        return x;
    }

    public void addAreaSeries(Series<X, Y> areaSeries) {
        // create new paths for series
        Path seriesLine = new Path();
        Path fillPath = new Path();
        fillPath.setFill(new Color(0.56078434f, 0.7372549f, 0.56078434f, 0.5));
        fillPath.setStyle("-fx-stroke: transparent; -fx-fill: rgb(143, 188, 143, 0.5)");
        seriesLine.getStyleClass().add("chart-series-area-line");
        Group areaGroup = new Group(fillPath, seriesLine);
        areaSeries.setNode(areaGroup);
        // create series Y multiplier
        DoubleProperty seriesYAnimMultiplier = new SimpleDoubleProperty(this, "seriesYMultiplier");
        seriesYMultiplierMap.put(areaSeries, seriesYAnimMultiplier);
        // handle any data already in series
        if (shouldAnimate()) {
            seriesYAnimMultiplier.setValue(0d);
        } else {
            seriesYAnimMultiplier.setValue(1d);
        }
        getPlotChildren().add(areaGroup);

        List<KeyFrame> keyFrames = new ArrayList<KeyFrame>();
        if (shouldAnimate()) {
            // animate in new series
            keyFrames.add(new KeyFrame(Duration.ZERO,
                    new KeyValue(areaGroup.opacityProperty(), 0),
                    new KeyValue(seriesYAnimMultiplier, 0)
            ));
            keyFrames.add(new KeyFrame(Duration.millis(200),
                    new KeyValue(areaGroup.opacityProperty(), 1)
            ));
            keyFrames.add(new KeyFrame(Duration.millis(500),
                    new KeyValue(seriesYAnimMultiplier, 1)
            ));
        }
        for (int j = 0; j < areaSeries.getData().size(); j++) {
            Data<X, Y> item = areaSeries.getData().get(j);
            final Node symbol = createAreaSymbol(item, j);
            if (symbol != null) {
                if (shouldAnimate()) {
                    symbol.setOpacity(0);
                    getPlotChildren().add(symbol);
                    // fade in new symbol
                    keyFrames.add(new KeyFrame(Duration.ZERO, new KeyValue(symbol.opacityProperty(), 0)));
                    keyFrames.add(new KeyFrame(Duration.millis(200), new KeyValue(symbol.opacityProperty(), 1)));
                } else {
                    getPlotChildren().add(symbol);
                }
            }
        }

        this.areaSeries.add(areaSeries);
    }

    private Node createAreaSymbol(final Data<X, Y> item, int itemIndex) {
        Node symbol = item.getNode();
        // check if symbol has already been created
        if (symbol == null) {
            symbol = new StackPane();
            symbol.setAccessibleRole(AccessibleRole.TEXT);
            symbol.setAccessibleRoleDescription("Point");
            symbol.focusTraversableProperty().bind(Platform.accessibilityActiveProperty());
            item.setNode(symbol);
        }
        // set symbol styles
        // Note: not sure if we want to add or check, ie be more careful and efficient here
        if (symbol != null) {
            symbol.getStyleClass().add("chart-area-symbol");
        }
        return symbol;
    }
}
