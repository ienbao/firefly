package com.dmsoft.firefly.plugin.grr.charts;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.dmsoft.firefly.plugin.grr.charts.data.ILineData;
import com.dmsoft.firefly.sdk.utils.ColorUtils;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Maps;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * Created by cherry on 2018/3/14.
 */

/**
 * Linear Chart
 *
 * @param <X> first data class
 * @param <Y> second data class
 */
public class LinearChart<X, Y> extends LineChart<X, Y> {

    public static final Orientation HORIZONTALTYPE = Orientation.HORIZONTAL;
    public static final Orientation VERTICALTYPE = Orientation.VERTICAL;
    private ObservableList<Data<X, Y>> horizontalMarkers;
    private ObservableList<XYChart.Data<X, Y>> verticalMarkers;
    private Map<String, Line> lineMap = Maps.newHashMap();

    private final double ANCHOR_X = 10.0;
    private final double ANCHOR_Y = 15.0;

    /**
     * Construct a new LinearChart with the given axis.
     *
     * @param xAxis x axis
     * @param yAxis y axis
     */
    public LinearChart(Axis<X> xAxis, Axis<Y> yAxis) {
        super(xAxis, yAxis);
        super.setHorizontalZeroLineVisible(false);
        super.setVerticalZeroLineVisible(false);
        horizontalMarkers = FXCollections.observableArrayList(d -> new Observable[]{d.YValueProperty()});
        verticalMarkers = FXCollections.observableArrayList(d -> new Observable[]{d.YValueProperty()});
    }

    @Override
    protected void layoutPlotChildren() {
        super.layoutPlotChildren();
        paintValueMaker();
    }

    private void paintValueMaker() {
        //        Draw horizontal markers
        for (XYChart.Data<X, Y> horizontalMarker : horizontalMarkers) {
            double lower = ((ValueAxis) this.getXAxis()).getLowerBound();
            double upper = ((ValueAxis) this.getXAxis()).getUpperBound();
            X lowerX = this.getXAxis().toRealValue(lower);
            X upperX = this.getXAxis().toRealValue(upper);
            Line line = (Line) horizontalMarker.getNode();
            line.setStartX(this.getXAxis().getDisplayPosition(lowerX));
            line.setEndX(this.getXAxis().getDisplayPosition(upperX));
            line.setStartY(this.getYAxis().getDisplayPosition(horizontalMarker.getYValue()));
            line.setEndY(line.getStartY());
        }

//        Draw vertical markers
        for (XYChart.Data<X, Y> verticalMarker : verticalMarkers) {
            String str = null;
            double lower = ((ValueAxis) this.getYAxis()).getLowerBound();
            double upper = ((ValueAxis) this.getYAxis()).getUpperBound();
            Y lowerY = this.getYAxis().toRealValue(lower);
            Y upperY = this.getYAxis().toRealValue(upper);
            Line line = (Line) verticalMarker.getNode();
            line.setStartX(this.getXAxis().getDisplayPosition(verticalMarker.getXValue()));
            line.setEndX(line.getStartX());
            line.setStartY((this.getYAxis()).getDisplayPosition(lowerY));
            line.setEndY(this.getYAxis().getDisplayPosition(upperY));
        }
    }

    /**
     * Build value marker with given line data and tooltip
     *
     * @param lineData             line data
     * @param pointTooltipFunction point tooltip function
     */
    public void buildValueMarkerWithTooltip(List<ILineData> lineData, Function<ILineData, String> pointTooltipFunction) {
        buildValueMarkerWithoutTooltip(lineData);
        Tooltip tooltip = new Tooltip();
        lineData.forEach(oneLineData -> {
            if (lineMap.containsKey(oneLineData.getName())) {
                Line line = lineMap.get(oneLineData.getName());
                line.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                            tooltip.setText(pointTooltipFunction.apply(oneLineData));
                            tooltip.show(line, event.getScreenX() + ANCHOR_X, event.getScreenY() + ANCHOR_Y);
                        }
                );
                line.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
                            if (tooltip.isShowing()) {
                                tooltip.setAnchorX(event.getScreenX() + ANCHOR_X);
                                tooltip.setAnchorY(event.getScreenY() + ANCHOR_Y);
                            }
                        }
                );
                line.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
                    if (tooltip.isShowing()) {
                        tooltip.hide();
                    }
                });
            }
        });

    }

    /**
     * Build value marker with tooltip with line data
     *
     * @param lineData line data
     */
    public void buildValueMarkerWithoutTooltip(List<ILineData> lineData) {
        lineData.forEach(oneLineData -> {
            Line line = new Line();
            Orientation orientationType = oneLineData.getPlotOrientation();
            XYChart.Data marker = HORIZONTALTYPE == orientationType
                    ? new XYChart.Data(0, oneLineData.getValue()) : new XYChart.Data(oneLineData.getValue(), 0);
            marker.setNode(line);
            getPlotChildren().add(line);
            lineMap.put(oneLineData.getName(), line);
            if (HORIZONTALTYPE == orientationType) {
                horizontalMarkers.add(marker);
            }
            if (VERTICALTYPE == orientationType) {
                verticalMarkers.add(marker);
            }

            //Set line style class
            if (DAPStringUtils.isNotBlank(oneLineData.getLineClass())) {
                line.getStyleClass().setAll("line", oneLineData.getLineClass());
            }
            setLineColor(line, oneLineData.getColor());
        });
    }

    private void setLineColor(Line line, Color color) {
        //Set line color
        String colorStr = color == null || DAPStringUtils.isBlank(ColorUtils.toHexFromFXColor(color))
                ? "black" : ColorUtils.toHexFromFXColor(color);
        line.setStyle("-fx-stroke:" + colorStr);
    }

    /**
     * Hidden lines for line names
     *
     * @param lineNames line names
     */
    public void hiddenValueMarkers(List<String> lineNames) {
        if (lineNames == null) {
            return;
        }
        lineNames.forEach(lineName -> toggleValueMarker(lineName, false));
    }

    /**
     * Toggle value marker show or hidden
     *
     * @param lineName line name
     * @param showed   whether it show or hide
     */
    public void toggleValueMarker(String lineName, boolean showed) {

        if (showed) {
            showValueMarker(lineName);
        } else {
            hiddenValueMarker(lineName);
        }
    }

    /**
     * Hidden value marker with given line name
     *
     * @param lineName line name
     */
    public void hiddenValueMarker(String lineName) {

        if (lineMap.containsKey(lineName)) {
            lineMap.get(lineName).getStyleClass().add("hidden-line");
        }
    }

    /**
     * Show value marker with given line name
     *
     * @param lineName line name
     */
    public void showValueMarker(String lineName) {

        if (lineMap.containsKey(lineName)) {
            lineMap.get(lineName).getStyleClass().remove("hidden-line");
        }
    }

    /**
     * Remove all chart node and clear chart data
     */
    public void removeAllChildren() {
        this.horizontalMarkers.setAll(FXCollections.observableArrayList());
        this.verticalMarkers.setAll(FXCollections.observableArrayList());
        this.getData().clear();
        this.getPlotChildren().clear();
    }

    /**
     * Update all line color
     *
     * @param color color
     */
    public void updateAllLineColor(Color color) {
        for (Map.Entry<String, Line> stringLineMap : lineMap.entrySet()) {
            setLineColor(stringLineMap.getValue(), color);
        }
    }

    /**
     * Clear chart nodes and data
     */
    public void clear() {
        lineMap.clear();
        horizontalMarkers.setAll(FXCollections.observableArrayList());
        verticalMarkers.setAll(FXCollections.observableArrayList());
    }
}
