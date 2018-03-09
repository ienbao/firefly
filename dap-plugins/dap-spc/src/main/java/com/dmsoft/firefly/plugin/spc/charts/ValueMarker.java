package com.dmsoft.firefly.plugin.spc.charts;

import com.dmsoft.firefly.plugin.spc.charts.data.basic.ILineData;
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

/**
 * Created by cherry on 2018/2/27.
 */
public class ValueMarker<X, Y> {

    public final static Orientation horizontalType = Orientation.HORIZONTAL;
    public final static Orientation verticalType = Orientation.VERTICAL;
    private ObservableList<XYChart.Data<X, Y>> horizontalMarkers;
    private ObservableList<XYChart.Data<X, Y>> verticalMarkers;

    private Map<String, Line> lineMap = Maps.newHashMap();

    public ValueMarker() {

        horizontalMarkers = FXCollections.observableArrayList(d -> new Observable[]{d.YValueProperty()});
        verticalMarkers = FXCollections.observableArrayList(d -> new Observable[]{d.YValueProperty()});
    }

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

    public Line buildValueMarker(ILineData lineData) {

        Line line = new Line();
        Orientation orientationType = lineData.getPlotOrientation();
        XYChart.Data marker = horizontalType == orientationType ?
                new XYChart.Data(0, lineData.getValue()) :
                new XYChart.Data(lineData.getValue(), 0);
        marker.setNode(line);
        lineMap.put(lineData.getName(), line);
        if (horizontalType == orientationType) {
            horizontalMarkers.add(marker);
        }
        if (verticalType == orientationType) {
            verticalMarkers.add(marker);
        }

        //Set line style class
        if (DAPStringUtils.isNotBlank(lineData.getLineClass())) {
            line.getStyleClass().setAll("line", lineData.getLineClass());
        }
        setLineColor(line, lineData.getColor());
        line.setOnMouseEntered(event -> {
            //Set tooltip
            String content = DAPStringUtils.isBlank(lineData.getTooltipContent()) ?
                    lineData.getTitle() + "\n" + lineData.getName() + "="
                            + lineData.getValue() : lineData.getTooltipContent();
            Tooltip.install(line, new Tooltip(content));
        });
        return line;
    }

    private void setLineColor(Line line, Color color) {
        //Set line color
        String colorStr = color == null || DAPStringUtils.isBlank(ColorUtils.toHexFromFXColor(color))
                ? "black" : ColorUtils.toHexFromFXColor(color);
        line.setStyle("-fx-stroke:" + colorStr);
    }

    public void toggleValueMarker(String lineName, boolean showed) {

        if (showed) {
            showValueMarker(lineName);
        } else {
            hiddenValueMarker(lineName);
        }
    }

    public void hiddenValueMarker(String lineName) {

        if (lineMap.containsKey(lineName)) {
            lineMap.get(lineName).getStyleClass().add("hidden-line");
        }
    }

    public void showValueMarker(String lineName) {

        if (lineMap.containsKey(lineName)) {
            lineMap.get(lineName).getStyleClass().remove("hidden-line");
        }
    }

    /**
     * Update all line color
     * @param color
     */
    public void updateAllLineColor(Color color) {
        for (Map.Entry<String, Line> stringLineMap : lineMap.entrySet()) {
            setLineColor(stringLineMap.getValue(), color);
        }
    }

    public void clear() {
        lineMap.clear();
        horizontalMarkers.setAll(FXCollections.observableArrayList());
        verticalMarkers.setAll(FXCollections.observableArrayList());
    }
}
