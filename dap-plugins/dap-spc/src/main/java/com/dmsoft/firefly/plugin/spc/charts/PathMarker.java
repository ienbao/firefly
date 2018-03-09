package com.dmsoft.firefly.plugin.spc.charts;

import com.dmsoft.firefly.plugin.spc.charts.data.basic.IPathData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IPoint;
import com.dmsoft.firefly.plugin.spc.charts.utils.enums.LineType;
import com.dmsoft.firefly.sdk.utils.ColorUtils;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Maps;
import javafx.scene.Node;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

import java.util.List;
import java.util.Map;

/**
 * Created by cherry on 2018/3/9.
 */
public class PathMarker {

    private Map<Node, List<IPoint>> pathDataMap = Maps.newHashMap();
    private Map<String, Node> pathMap = Maps.newHashMap();

    /**
     * Paint path in chart
     *
     * @param chart chart
     */
    public void paintPathMarker(XYChart chart) {
        for (Map.Entry<Node, List<IPoint>> pathData : pathDataMap.entrySet()) {
            Path path = (Path) pathData.getKey();
            List<IPoint> points = pathData.getValue();
            path.getElements().clear();
            for (int i = 0; i < points.size(); i++) {
                double x = (double) points.get(i).getXByIndex(i);
                double y = (double) points.get(i).getYByIndex(i);
                this.layoutPath(path, x, y, chart);
            }
        }
    }

    /**
     * Build path node
     *
     * @param pathData path data
     * @return path
     */
    public Path buildPathMarker(IPathData pathData) {

        Path path = new Path();
//        set path style
        String color = pathData.getColor() == null || DAPStringUtils.isBlank(ColorUtils.toHexFromFXColor(pathData.getColor()))
                ? "black" : ColorUtils.toHexFromFXColor(pathData.getColor());
        path.setStyle("-fx-stroke:" + color);

        if (LineType.SOLID == pathData.getLineType()) {
            path.getStyleClass().add("solid-line");
        } else if (LineType.DASHED == pathData.getLineType()) {
            path.getStyleClass().add("dashed-line");
        } else {
            path.getStyleClass().add("solid-line");
        }

        path.setOnMouseEntered(event -> {
            //Set tooltip
            if (DAPStringUtils.isNotBlank(pathData.getTooltipContent())) {
                Tooltip.install(path, new Tooltip(pathData.getTooltipContent()));
            }
        });
        List<IPoint> points = pathData.getPoints();
        pathDataMap.put(path, points);
        pathMap.put(pathData.getPathName(), path);
        return path;
    }

    private void layoutPath(Path path, double x, double y, XYChart chart) {
        if (path.getElements().isEmpty()) {
            path.getElements().add(new MoveTo(chart.getXAxis().getDisplayPosition(x),
                    chart.getYAxis().getDisplayPosition(y)));
        } else {
            path.getElements().add(new LineTo(chart.getXAxis().getDisplayPosition(x),
                    chart.getYAxis().getDisplayPosition(y)));
        }
    }

    /**
     * Toggle path show or hidden
     *
     * @param pathName pathName
     * @param showed   whether it is show or not show
     */
    public void togglePathMarker(String pathName, boolean showed) {
        if (showed) {
            showPathMarker(pathName);
        } else {
            hiddenPathMarker(pathName);
        }
    }

    /**
     * Hidden path
     *
     * @param pathName
     */
    public void hiddenPathMarker(String pathName) {

        if (pathMap.containsKey(pathName)) {
            pathMap.get(pathName).getStyleClass().add("hidden-line");
        }
    }

    public void showPathMarker(String pathName) {

        if (pathMap.containsKey(pathName)) {
            pathMap.get(pathName).getStyleClass().remove("hidden-line");
        }
    }
}
