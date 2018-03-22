package com.dmsoft.firefly.plugin.spc.charts.data;

import com.dmsoft.firefly.plugin.spc.charts.AxisRange;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IBarChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.ILineData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IXYChartData;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * Created by cherry on 2018/3/21.
 */
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
    IXYChartData getXYChartData();

    /**
     * Get line data
     *
     * @return line data
     */
    List<ILineData> getLineData();

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
