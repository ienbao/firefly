package com.dmsoft.firefly.plugin.spc.charts.data.basic;

import com.dmsoft.firefly.plugin.spc.charts.utils.enums.LineType;
import javafx.scene.paint.Color;

/**
 * Created by cherry on 2018/3/9.
 */
public interface IPathData {

    /**
     * Get points for path
     *
     * @return points
     */
    IPoint getPoints();

    /**
     * Get path name
     *
     * @return path name
     */
    String getPathName();

    /**
     * Get path title
     *
     * @return path title
     */
    default String getTitle() {
        return "";
    }

    /**
     * Get path tooltip
     *
     * @return path tooltip
     */
    default String getTooltipContent() {
        return "";
    }

    /**
     * Get path color
     *
     * @return path color
     */
    default Color getColor() {
        return null;
    }

    /**
     * Get path line type, egg: solid、dashed
     *
     * @return line type
     */
    default LineType getLineType() {
        return LineType.DASHED;
    }
}
