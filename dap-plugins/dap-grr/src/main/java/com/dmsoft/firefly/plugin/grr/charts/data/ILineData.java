package com.dmsoft.firefly.plugin.grr.charts.data;

import com.dmsoft.firefly.plugin.grr.utils.enums.LineType;
import javafx.geometry.Orientation;
import javafx.scene.paint.Color;

/**
 * Created by cherry on 2018/3/8.
 */
public interface ILineData {

    default String getName() {
        return "";
    }

    default String getTitle() {
        return "";
    }

    double getValue();

    default Color getColor() {
        return null;
    }

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
