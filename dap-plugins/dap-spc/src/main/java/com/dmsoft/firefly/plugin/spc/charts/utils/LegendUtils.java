package com.dmsoft.firefly.plugin.spc.charts.utils;

import com.sun.javafx.charts.Legend;
import javafx.geometry.Pos;
import javafx.scene.shape.Line;

/**
 * Created by cherry on 2018/3/30.
 */
public class LegendUtils {

    public static Legend buildReferenceLineLegend() {
        Legend legend = new Legend();
        Line uLine = new Line(0, 0, 20, 0);
        Line sLine = new Line(0, 0, 20, 0);
        Line uslAndLslLine = new Line(0, 0, 20, 0);
        uslAndLslLine.setStyle("-fx-stroke-dash-array: 4px; -fx-stroke: rgb(102, 102, 102)");
        uLine.setStyle("-fx-stroke: rgb(102, 102, 102)");
        sLine.setStyle("-fx-stroke: rgb(102, 102, 102)");
        Legend.LegendItem uslAndLslItem = new Legend.LegendItem("LSL, USL", uslAndLslLine);
        Legend.LegendItem uItem = new Legend.LegendItem("m Line", uLine);
        Legend.LegendItem sigmaItem = new Legend.LegendItem("6s Line", sLine);
        legend.getItems().add(uslAndLslItem);
        legend.getItems().add(uItem);
        legend.getItems().add(sigmaItem);
        legend.setTileAlignment(Pos.TOP_RIGHT);
        legend.getStyleClass().add("legend");
        return legend;
    }
}
