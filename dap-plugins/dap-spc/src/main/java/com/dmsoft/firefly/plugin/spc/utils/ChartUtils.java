package com.dmsoft.firefly.plugin.spc.utils;

import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;


/**
 * utils class make chart expandable and draggable
 *
 * @author Can Guan
 */
public class ChartUtils {
    private XYChart chart;
    private ValueAxis xAxis;
    private ValueAxis yAxis;
    private boolean dragging = false;
    private boolean wasXAnimated;
    private boolean wasYAnimated;
    private double lastX;
    private double lastY;
    private double maxRate = 4;
    private double currentRate = 1;
    private double originalXUpper;
    private double originalXLower;
    private double originalYUpper;
    private double originalYLower;

    /**
     * constructor
     *
     * @param chart xy chart
     */
    public ChartUtils(XYChart chart) {
        this(chart, 4);
    }

    /**
     * constructor
     *
     * @param chart   xy chart
     * @param maxRate maxRate : maxRate to expand
     */
    public ChartUtils(XYChart chart, double maxRate) {
        this.chart = chart;
        this.maxRate = maxRate;
        xAxis = (ValueAxis) chart.getXAxis();
        yAxis = (ValueAxis) chart.getYAxis();
        originalXUpper = xAxis.getUpperBound();
        originalXLower = xAxis.getLowerBound();
        originalYUpper = yAxis.getUpperBound();
        originalYLower = yAxis.getLowerBound();
    }

    /**
     * method to zoom in chart
     */
    public void zoomInChart() {
        double newRate = currentRate + 1;
        if (newRate <= maxRate) {
            zoom(newRate);
        }
    }

    /**
     * method to zoom out chart
     */
    public void zoomOutChart() {
        double newRate = currentRate - 1;
        if (newRate >= 1) {
            zoom(newRate);
        }
    }

    private void zoom(double newRate) {
        if (newRate == 1) {
            xAxis.setUpperBound(originalXUpper);
            xAxis.setLowerBound(originalXLower);
            yAxis.setUpperBound(originalYUpper);
            yAxis.setLowerBound(originalYLower);
            currentRate = 1;
            return;
        }
        double currentXUpper = xAxis.getUpperBound();
        double currentXLower = xAxis.getLowerBound();
        double currentYUpper = yAxis.getUpperBound();
        double currentYLower = yAxis.getLowerBound();
        double middleXValue = (currentXUpper + currentXLower) / 2;
        double xSpan = (currentXUpper - currentXLower) / 2;
        double newXUpper = middleXValue + xSpan * currentRate / newRate;
        double newXLower = middleXValue - xSpan * currentRate / newRate;
        if (newXUpper > originalXUpper) {
            double delta = newXUpper - originalXUpper;
            newXUpper = originalXUpper;
            newXLower = newXLower - delta;
            if (newXLower < originalXLower) {
                newXLower = originalXLower;
            }
        }
        if (newXLower < originalXLower) {
            double delta = newXLower - originalXLower;
            newXLower = originalXLower;
            newXUpper = newXUpper - delta;
            if (newXUpper > originalXUpper) {
                newXUpper = originalXUpper;
            }
        }
        xAxis.setUpperBound(newXUpper);
        xAxis.setLowerBound(newXLower);
        double middleYValue = (currentYUpper + currentYLower) / 2;
        double ySpan = (currentYUpper - currentYLower) / 2;
        double newYUpper = middleYValue + ySpan * currentRate / newRate;
        double newYLower = middleYValue - ySpan * currentRate / newRate;
        if (newYUpper > originalYUpper) {
            double delta = newYUpper - originalYUpper;
            newYUpper = originalYUpper;
            newYLower = newYLower - delta;
            if (newYLower < originalYLower) {
                newYLower = originalYLower;
            }
        }
        if (newYLower < originalYLower) {
            double delta = newYLower - originalYLower;
            newYLower = originalYLower;
            newYUpper = newYUpper - delta;
            if (newYUpper > originalYUpper) {
                newYUpper = originalYUpper;
            }
        }
        yAxis.setUpperBound(newYUpper);
        yAxis.setLowerBound(newYLower);
        this.currentRate = newRate;
    }

    /**
     * active chart
     */
    public void activeChartDraggable() {
        String s = ".chart-content";
        chart.lookupAll(s).forEach((node) -> node.addEventHandler(MouseEvent.DRAG_DETECTED, this::startDrag));
        chart.lookupAll(s).forEach((node -> node.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::drag)));
        chart.lookupAll(s).forEach((node -> node.addEventHandler(MouseEvent.MOUSE_RELEASED, this::release)));
    }


    private void startDrag(MouseEvent event) {
        lastX = event.getX();
        lastY = event.getY();
        wasXAnimated = xAxis.getAnimated();
        wasYAnimated = yAxis.getAnimated();
        xAxis.setAnimated(false);
        xAxis.setAutoRanging(false);
        yAxis.setAnimated(false);
        yAxis.setAutoRanging(false);
        dragging = true;
    }

    private void drag(MouseEvent event) {
        if (!dragging || currentRate == 1) {
            return;
        }

        double dX = (event.getX() - lastX) / -xAxis.getScale();
        double newUpperX = xAxis.getUpperBound() + dX;
        double newLowerX = xAxis.getLowerBound() + dX;
        if (newUpperX > originalXUpper) {
            double delta = newUpperX - originalXUpper;
            newUpperX = originalXUpper;
            newLowerX = newLowerX - delta;
        }
        if (newLowerX < originalXLower) {
            double delta = newLowerX - originalXLower;
            newLowerX = originalXLower;
            newUpperX = newUpperX - delta;
        }
        lastX = event.getX();
        xAxis.setAutoRanging(false);
        xAxis.setUpperBound(newUpperX);
        xAxis.setLowerBound(newLowerX);
        double dY = (event.getY() - lastY) / -yAxis.getScale();
        double newUpperY = yAxis.getUpperBound() + dY;
        double newLowerY = yAxis.getLowerBound() + dY;

        if (newUpperY > originalYUpper) {
            double delta = newUpperY - originalYUpper;
            newUpperY = originalYUpper;
            newLowerY = newLowerY - delta;
        }
        if (newLowerY < originalYLower) {
            double delta = newLowerY - originalYLower;
            newLowerY = originalYLower;
            newUpperY = newUpperY - delta;
        }
        lastY = event.getY();
        yAxis.setAutoRanging(false);
        yAxis.setUpperBound(newUpperY);
        yAxis.setLowerBound(newLowerY);
    }

    private void release(MouseEvent event) {
        if (!dragging) {
            return;
        }
        dragging = false;
        xAxis.setAnimated(wasXAnimated);
        yAxis.setAnimated(wasYAnimated);
    }
}
