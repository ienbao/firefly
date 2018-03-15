package com.dmsoft.firefly.plugin.grr.charts;

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
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.List;
import java.util.Map;

/**
 * Created by cherry on 2018/3/14.
 */
public class LinearChart<X, Y> extends LineChart<X, Y> {

    public final static Orientation horizontalType = Orientation.HORIZONTAL;
    public final static Orientation verticalType = Orientation.VERTICAL;
    private ObservableList<Data<X, Y>> horizontalMarkers;
    private ObservableList<XYChart.Data<X, Y>> verticalMarkers;
    private Map<String, Line> lineMap = Maps.newHashMap();

    public LinearChart(Axis<X> xAxis, Axis<Y> yAxis) {
        super(xAxis, yAxis);
        horizontalMarkers = FXCollections.observableArrayList(d -> new Observable[]{d.YValueProperty()});
        verticalMarkers = FXCollections.observableArrayList(d -> new Observable[]{d.YValueProperty()});
    }

    @Override
    protected void layoutPlotChildren() {
        super.layoutPlotChildren();
        paintValueMaker();
    }

    public void paintValueMaker() {
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

    public void buildValueMarker(List<ILineData> lineData, boolean showTooltip) {
        lineData.forEach(oneLineData -> {
            Line line = new Line();
            Orientation orientationType = oneLineData.getPlotOrientation();
            XYChart.Data marker = horizontalType == orientationType ?
                    new XYChart.Data(0, oneLineData.getValue()) :
                    new XYChart.Data(oneLineData.getValue(), 0);
            marker.setNode(line);
            getPlotChildren().add(line);
            lineMap.put(oneLineData.getName(), line);
            if (horizontalType == orientationType) {
                horizontalMarkers.add(marker);
            }
            if (verticalType == orientationType) {
                verticalMarkers.add(marker);
            }

            //Set line style class
            if (DAPStringUtils.isNotBlank(oneLineData.getLineClass())) {
                line.getStyleClass().setAll("line", oneLineData.getLineClass());
            }
            setLineColor(line, oneLineData.getColor());
            if (showTooltip) {
                line.setOnMouseEntered(event -> {
                    //Set tooltip
                    String content = DAPStringUtils.isBlank(oneLineData.getTooltipContent()) ?
                            oneLineData.getTitle() + "\n" + oneLineData.getName() + "="
                                    + oneLineData.getValue() : oneLineData.getTooltipContent();
                    Tooltip.install(line, new Tooltip(content));
                });
            }
        });
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
     *
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
