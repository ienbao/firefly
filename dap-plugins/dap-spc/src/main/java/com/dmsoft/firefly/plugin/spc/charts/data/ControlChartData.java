package com.dmsoft.firefly.plugin.spc.charts.data;

import com.dmsoft.firefly.plugin.spc.charts.AxisRange;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.*;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.function.Function;

/**
 * Created by cherry on 2018/3/20.
 */
public interface ControlChartData extends AxisRange {

    /**
     * Get xy chart data for one series
     *
     * @return one series chart data
     */
    IXYChartData getXyOneChartData();

    /**
     * Get line data for one series
     *
     * @return all line data
     */
    default List<ILineData> getLineData() {
        return null;
    }

    /**
     * Get broken line data for one series
     *
     * @return broken line data
     */
    default List<IPathData> getBrokenLineData() {
        return null;
    }

    /**
     * Get point rule function
     *
     * @return point rule function
     */
    default Function<PointRule, PointStyle> getPointFunction() {
        return null;
    }

    /**
     * Get color for one series
     *
     * @return color
     */
    Color getColor();

    /**
     * Get unique key for one series
     *
     * @return unique key
     */
    String getUniqueKey();

    /**
     * Get series name for one series
     *
     * @return series name
     */
    String getSeriesName();

    /**
     * Get ucl array data for one series
     *
     * @return ucl array data
     */
    default Double[] getUclData() {
        return null;
    }

    /**
     * Get lcl array data for one series
     *
     * @return lcl array data
     */
    default Double[] getLclData() {
        return null;
    }

}
