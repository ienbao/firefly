package com.dmsoft.firefly.plugin.spc.charts.data.basic;

import com.dmsoft.firefly.plugin.spc.charts.utils.enums.LineType;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * Created by cherry on 2018/3/9.
 */
public interface IPathData {

    IPoint getPoints();

    String getPathName();

    default String getTitle() {
        return "";
    }

    default String getTooltipContent() {
        return "";
    }

    default Color getColor() {
        return null;
    }

    default LineType getLineType() {
        return LineType.SOLID;
    }
}