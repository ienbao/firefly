package com.dmsoft.firefly.plugin.yield.charts.data;

import javafx.geometry.Orientation;
import javafx.scene.paint.Color;

public interface ILineData {

    default String getName() {
        return "";
    }
    default String getTitle() {
        return "";
    }

    /**
     * Get line value
     *
     * @return line value
     */
    double getValue();

    default Color getColor() {
        return null;
    }

    default Orientation getPlotOrientation() {
        return Orientation.VERTICAL;
    }
    default String getLineClass() {
        return "";
    }

    default String getTooltipContent() {
        return getTitle() + "\n" + getName() + "=" + getValue();
    }
}
