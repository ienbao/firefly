package com.dmsoft.firefly.plugin.spc.charts;

import com.dmsoft.firefly.plugin.spc.charts.data.basic.IPathData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IPoint;
import com.dmsoft.firefly.plugin.spc.charts.utils.enums.LineType;
import com.dmsoft.firefly.sdk.utils.ColorUtils;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Maps;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

import java.util.Map;

/**
 * Created by cherry on 2018/3/9.
 */
public class PathMarker {

    private Map<Node, IPoint> pathDataMap = Maps.newHashMap();
    private Map<String, Path> pathMap = Maps.newHashMap();

    /**
     * Paint path in chart
     *
     * @param chart chart
     */
    public void paintPathMarker(XYChart chart) {
        for (Map.Entry<Node, IPoint> pathData : pathDataMap.entrySet()) {
            Path path = (Path) pathData.getKey();
            IPoint points = pathData.getValue();
            path.getElements().clear();
            int len = points.getLen();
            for (int i = 0; i < len; i++) {
                double x = (double) points.getXByIndex(i);
                double y = (double) points.getYByIndex(i);
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
        setPathColor(path, pathData.getColor());

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
        IPoint points = pathData.getPoints();
        pathDataMap.put(path, points);
        pathMap.put(pathData.getPathName(), path);
        return path;
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
     * Hidden path by path name
     *
     * @param pathName
     */
    public void hiddenPathMarker(String pathName) {

        if (pathMap.containsKey(pathName)) {
            pathMap.get(pathName).getStyleClass().add("hidden-line");
        }
    }

    /**
     * Show path by path name
     *
     * @param pathName path name
     */
    public void showPathMarker(String pathName) {

        if (pathMap.containsKey(pathName)) {
            pathMap.get(pathName).getStyleClass().remove("hidden-line");
        }
    }

    /**
     * Update all path color
     *
     * @param color
     */
    public void updateAllLineColor(Color color) {
        for (Map.Entry<String, Path> stringPathEntry : pathMap.entrySet()) {
            setPathColor(stringPathEntry.getValue(), color);
        }
    }

    /**
     * Clear path value data
     */
    public void clear() {
        pathMap.clear();
        pathDataMap.clear();
    }

    private void setPathColor(Path path, Color color) {
        //Set line color
        String colorStr = color == null || DAPStringUtils.isBlank(ColorUtils.toHexFromFXColor(color))
                ? "black" : ColorUtils.toHexFromFXColor(color);
        path.setStyle("-fx-stroke:" + colorStr);
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
}
