package com.dmsoft.firefly.plugin.spc.charts.data;

import com.dmsoft.firefly.plugin.spc.charts.AxisRange;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.*;
import com.dmsoft.firefly.plugin.spc.utils.XYData;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.function.Function;

/**
 * Created by cherry on 2018/3/20.
 */
public interface ControlChartData extends AxisRange {

    IXYChartData getXyOneChartData();

    default List<ILineData> getLineData() {
        return null;
    }

    default List<IPathData> getBrokenLineData() {
        return null;
    }

    default Function<PointRule, PointStyle> getPointFunction() {
        return null;
    }

    Color getColor();

    String getUniqueKey();

    String getSeriesName();

    default Double[] getUclData() {
        return null;
    }

    default Double[] getLclData() {
        return null;
    }

}
