package com.dmsoft.firefly.plugin.spc.charts.data;

import com.dmsoft.firefly.plugin.spc.charts.AxisRange;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IBoxAndWhiskerData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IPoint;
import javafx.scene.paint.Color;

/**
 * Created by cherry on 2018/3/21.
 */
public interface BoxPlotChartData extends AxisRange {

    /**
     * Get box plot chart series data
     *
     * @return box plot chart series data
     */
    IBoxAndWhiskerData getBoxAndWhiskerData();

    /**
     * Get points
     *
     * @return points
     */
    IPoint getPoints();

    /**
     * Get chart color
     *
     * @return color
     */
    Color getColor();

    /**
     * Get chart series unique key
     *
     * @return this series unique key
     */
    String getUniqueKey();

    /**
     * Get series name
     *
     * @return series name
     */
    default String getSeriesName() {
        return "";
    }
}
