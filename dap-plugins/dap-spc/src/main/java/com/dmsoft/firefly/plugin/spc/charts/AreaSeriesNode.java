package com.dmsoft.firefly.plugin.spc.charts;

import com.dmsoft.firefly.plugin.spc.charts.data.basic.IXYChartData;
import com.dmsoft.firefly.plugin.spc.charts.utils.ReflectionUtils;
import com.dmsoft.firefly.sdk.utils.ColorUtils;
import com.google.common.collect.Maps;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.AccessibleRole;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Pair;

import java.util.*;

/**
 * Created by cherry on 2018/3/5.
 */
public class AreaSeriesNode<X, Y> {

    private ObservableList<XYChart.Series<X, Y>> areaSeries = FXCollections.observableArrayList();

    private Map<XYChart.Series, Color> colorMap = Maps.newHashMap();

    public Group buildAreaGroup(IXYChartData<X, Y> xyOneChartData, Color color) {
        XYChart.Series series = this.buildSeries(xyOneChartData);
        Path seriesLine = new Path();
        Path fillPath = new Path();
        seriesLine.setStrokeLineJoin(StrokeLineJoin.MITER);

        fillPath.getStyleClass().add("chart-fill-area-line");
        fillPath.setStyle("-fx-fill: " + ColorUtils.toHexFromFXColor(color));

        seriesLine.getStyleClass().add("chart-series-area-line");
        seriesLine.setStyle("-fx-stroke: " + ColorUtils.toHexFromFXColor(color));
        Group areaGroup = new Group(fillPath, seriesLine);

        this.areaSeries.add(series);
        colorMap.put(series, color);
        series.setNode(areaGroup);
        return areaGroup;
    }

    public void toggleAreaSeries(boolean showed) {

        for (int seriesIndex = 0; seriesIndex < areaSeries.size(); seriesIndex++) {
            XYChart.Series<X, Y> series = areaSeries.get(seriesIndex);
            final ObservableList<Node> children = ((Group) series.getNode()).getChildren();
            Path seriesLine = ((Path) children.get(1));
            Path fillPath = ((Path) children.get(0));
            if (showed) {
                Color color = colorMap.get(series);
                fillPath.setStyle("-fx-fill: " + ColorUtils.toHexFromFXColor(color));
                seriesLine.setStyle("-fx-stroke: " + ColorUtils.toHexFromFXColor(color));
            } else {
                fillPath.setStyle("-fx-stroke-width: 0px;-fx-fill: transparent");
                seriesLine.setStyle("-fx-stroke: transparent");
            }
        }
    }

    public void paintAreaSeries(XYChart chart) {

        List<LineTo> constructedPath = new ArrayList<>(areaSeries.size());
        for (int seriesIndex = 0; seriesIndex < areaSeries.size(); seriesIndex++) {
            double lastX = 0;
            XYChart.Series<X, Y> series = areaSeries.get(seriesIndex);
            final ObservableList<Node> children = ((Group) series.getNode()).getChildren();
            ObservableList<PathElement> seriesLine = ((Path) children.get(1)).getElements();
            ObservableList<PathElement> fillPath = ((Path) children.get(0)).getElements();
            seriesLine.clear();
            fillPath.clear();
            constructedPath.clear();
            for (int i = 0; i < series.getData().size(); i++) {
                XYChart.Data<X, Y> item = series.getData().get(i);
                double x = chart.getXAxis().getDisplayPosition(ReflectionUtils.forceMethodCall(XYChart.Data.class, "getCurrentX", item));
                double y = chart.getYAxis().getDisplayPosition(ReflectionUtils.forceMethodCall(XYChart.Data.class, "getCurrentY", item));
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
                Collections.sort(constructedPath, Comparator.comparingDouble(LineTo::getX));
                LineTo first = constructedPath.get(0);

                final double displayYPos = first.getY();
                final double numericYPos = chart.getYAxis().toNumericValue(chart.getYAxis().getValueForDisplay(displayYPos));

                // RT-34626: We can't always use getZeroPosition(), as it may be the case
                // that the zero position of the y-axis is not visible on the chart. In these
                // cases, we need to use the height between the point and the y-axis line.
                final double yAxisZeroPos = chart.getYAxis().getZeroPosition();
                final boolean isYAxisZeroPosVisible = !Double.isNaN(yAxisZeroPos);
                final double yAxisHeight = chart.getYAxis().getHeight();
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

    private XYChart.Series buildSeries(IXYChartData<X, Y> xyOneChartData) {
        XYChart.Series areaSeries = new XYChart.Series();
        int length = xyOneChartData.getLen();
        for (int i = 0; i < length; i++) {
            X xValue = xyOneChartData.getXValueByIndex(i);
            Y yValue = xyOneChartData.getYValueByIndex(i);
            if (xValue == null || yValue == null) {
                continue;
            }
            XYChart.Data data = new XYChart.Data<>(xValue, yValue);
            areaSeries.getData().add(data);
        }
        return areaSeries;
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

    private Node createAreaSymbol(final XYChart.Data<X, Y> item, int itemIndex) {
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

    public void clear() {
        areaSeries.setAll(FXCollections.observableArrayList());
        colorMap.clear();
    }
}
