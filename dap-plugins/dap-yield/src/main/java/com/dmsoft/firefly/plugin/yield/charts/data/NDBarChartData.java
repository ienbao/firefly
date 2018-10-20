package com.dmsoft.firefly.plugin.yield.charts.data;

import com.dmsoft.firefly.plugin.yield.charts.data.basic.IBarChartData;
import com.dmsoft.firefly.plugin.yield.charts.data.basic.IXYChartData;
import com.dmsoft.firefly.plugin.yield.charts.AxisRange;
import javafx.scene.paint.Color;

public interface NDBarChartData extends AxisRange {
    /**
     * Get bar data
     *
     * @return bar data
     */
    IBarChartData getBarChartData();

    /**
     * Get area data
     *
     * @return area data
     */

    /**
     * Get line data
     *
     * @return line data
     */
    IXYChartData getXYChartData();

    /**
     * Get chart color
     *
     * @return chart color
     */
    Color getColor();

    /**
     * Get series unique key
     *
     * @return series unique key
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
