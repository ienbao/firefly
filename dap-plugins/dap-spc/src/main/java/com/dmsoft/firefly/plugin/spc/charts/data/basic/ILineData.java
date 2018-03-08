package com.dmsoft.firefly.plugin.spc.charts.data.basic;

import com.dmsoft.firefly.plugin.spc.charts.utils.enums.LineType;
import javafx.geometry.Orientation;
import javafx.scene.paint.Color;

/**
 * Created by cherry on 2018/3/8.
 */
public interface ILineData {

    String getName();

    String getTitle();

    Number getValue();

    Color getColor();

    default Orientation getPlotOrientation() {
        return Orientation.VERTICAL;
    }

    default LineType getLineType() {
        return LineType.SOLID;
    }

    default String getLineClass() {
        return "";
    }

    default String getTooltipContent() {
        return getTitle() + "\n" + getName() + "=" + getValue();
    }
}