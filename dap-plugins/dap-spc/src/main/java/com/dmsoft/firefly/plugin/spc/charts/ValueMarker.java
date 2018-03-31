package com.dmsoft.firefly.plugin.spc.charts;

import com.dmsoft.firefly.plugin.spc.charts.data.basic.ILineData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.LineTooltip;
import com.dmsoft.firefly.plugin.spc.charts.utils.enums.LineType;
import com.dmsoft.firefly.sdk.utils.ColorUtils;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Maps;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.Map;
import java.util.function.Function;

/**
 * Created by cherry on 2018/2/27.
 */

/**
 * Value marker
 *
 * @param <X> x data class
 * @param <Y> y data class
 */
public class ValueMarker<X, Y> {

    public static final Orientation HORIZONTALTYPE = Orientation.HORIZONTAL;
    public static final Orientation VERTICALTYPE = Orientation.VERTICAL;
    private ObservableList<XYChart.Data<X, Y>> horizontalMarkers;
    private ObservableList<XYChart.Data<X, Y>> verticalMarkers;

    private Map<String, Line> lineMap = Maps.newHashMap();

    /**
     * The no parameters construction of ValueMarker
     */
    public ValueMarker() {

        horizontalMarkers = FXCollections.observableArrayList(d -> new Observable[]{d.YValueProperty()});
        verticalMarkers = FXCollections.observableArrayList(d -> new Observable[]{d.YValueProperty()});
    }

    /**
     * Paint value maker
     *
     * @param chart chart for paint value marker
     */
    public void paintValueMaker(XYChart<X, Y> chart) {

        //        Draw horizontal markers
        for (XYChart.Data<X, Y> horizontalMarker : horizontalMarkers) {
            double lower = ((ValueAxis) chart.getXAxis()).getLowerBound();
            double upper = ((ValueAxis) chart.getXAxis()).getUpperBound();
            X lowerX = chart.getXAxis().toRealValue(lower);
            X upperX = chart.getXAxis().toRealValue(upper);
            Line line = (Line) horizontalMarker.getNode();
            line.setStartX(chart.getXAxis().getDisplayPosition(lowerX));
            line.setEndX(chart.getXAxis().getDisplayPosition(upperX));
            line.setStartY(chart.getYAxis().getDisplayPosition(horizontalMarker.getYValue()));
            line.setEndY(line.getStartY());
        }

//        Draw vertical markers
        for (XYChart.Data<X, Y> verticalMarker : verticalMarkers) {
            double lower = ((ValueAxis) chart.getYAxis()).getLowerBound();
            double upper = ((ValueAxis) chart.getYAxis()).getUpperBound();
            Y lowerY = chart.getYAxis().toRealValue(lower);
            Y upperY = chart.getYAxis().toRealValue(upper);
            Line line = (Line) verticalMarker.getNode();
            line.setStartX(chart.getXAxis().getDisplayPosition(verticalMarker.getXValue()));
            line.setEndX(line.getStartX());
            line.setStartY((chart.getYAxis()).getDisplayPosition(lowerY));
            line.setEndY(chart.getYAxis().getDisplayPosition(upperY));
        }
    }

    /**
     * Build value marker by parameters
     *
     * @param lineData            line data
     * @param color               color
     * @param seriesName          series name
     * @param lineTooltipFunction line tooltip function
     * @return line object
     */
    public Line buildValueMarker(ILineData lineData,
                                 Color color,
                                 String seriesName,
                                 Function<LineTooltip, String> lineTooltipFunction) {

        Line line = new Line();
        Orientation orientationType = lineData.getPlotOrientation();
        XYChart.Data marker = HORIZONTALTYPE == orientationType
                ? new XYChart.Data(0, lineData.getValue()) : new XYChart.Data(lineData.getValue(), 0);
        marker.setNode(line);
        lineMap.put(lineData.getName(), line);
        if (HORIZONTALTYPE == orientationType) {
            horizontalMarkers.add(marker);
        }
        if (VERTICALTYPE == orientationType) {
            verticalMarkers.add(marker);
        }

        //Set line style class
        line.getStyleClass().setAll("line");
        if (lineData.getLineType() == LineType.SOLID) {
            line.getStyleClass().add("solid-line");
        } else {     //(lineData.getLineType() == LineType.DASHED)
            line.getStyleClass().add("dashed-line");
        }
        if (lineTooltipFunction != null) {
            String content = lineTooltipFunction.apply(new LineTooltip(seriesName, lineData.getName(), lineData.getValue()));
            Tooltip.install(line, new Tooltip(content));
        }
        setLineColor(line, color);
        return line;
    }

    private void setLineColor(Line line, Color color) {
        //Set line color
        String colorStr = color == null || DAPStringUtils.isBlank(ColorUtils.toHexFromFXColor(color))
                ? "black" : ColorUtils.toHexFromFXColor(color);
        line.setStyle("-fx-stroke:" + colorStr);
    }

    /**
     * ]Toggle show or hide value marker by name
     *
     * @param lineName line name
     * @param showed   whether it show or not
     */
    public void toggleValueMarker(String lineName, boolean showed) {
        if (showed) {
            showValueMarker(lineName);
        } else {
            hiddenValueMarker(lineName);
        }
    }

    /**
     * Hidden value marker
     *
     * @param lineName line name
     */
    public void hiddenValueMarker(String lineName) {
        if (lineMap.containsKey(lineName)) {
            lineMap.get(lineName).getStyleClass().add("hidden-line");
        }
    }

    /**
     * Show value marker
     *
     * @param lineName line name
     */
    public void showValueMarker(String lineName) {
        if (lineMap.containsKey(lineName)) {
            lineMap.get(lineName).getStyleClass().remove("hidden-line");
        }
    }

    /**
     * Update all line color
     *
     * @param color line color
     */
    public void updateAllLineColor(Color color) {
        for (Map.Entry<String, Line> stringLineMap : lineMap.entrySet()) {
            setLineColor(stringLineMap.getValue(), color);
        }
    }

    /**
     * Clear value marker data
     */
    public void clear() {
        lineMap.clear();
        horizontalMarkers.setAll(FXCollections.observableArrayList());
        verticalMarkers.setAll(FXCollections.observableArrayList());
    }
}
