package com.dmsoft.firefly.plugin.spc.charts.data.basic;

import com.dmsoft.firefly.plugin.spc.charts.utils.enums.LineType;
import javafx.geometry.Orientation;
import javafx.scene.paint.Color;

/**
 * Created by cherry on 2018/3/8.
 */
public interface ILineData {

    /**
     * Get line name
     *
     * @return line name
     */
    String getName();

    /**
     * Get line title
     *
     * @return line title
     */
    default String getTitle() {
        return "";
    }

    /**
     * Get line value
     *
     * @return line value
     */
    Double getValue();

    /**
     * Get line color
     *
     * @return line color
     */
    default Color getColor() {
        return null;
    }

    /**
     * Get line orientation
     *
     * @return line orientation
     */
    default Orientation getPlotOrientation() {
        return Orientation.VERTICAL;
    }

    /**
     * Get line type. egg: solid、dashed
     *
     * @return line type
     */
    default LineType getLineType() {
        return LineType.SOLID;
    }

    /**
     * Get class style for line
     *
     * @return line class style
     */
    default String getLineClass() {
        return "";
    }

    /**
     * Get line tooltip content
     *
     * @return tooltip content
     */
    default String getTooltipContent() {
        return getTitle() + "\n" + getName() + "=" + getValue();
    }
}
