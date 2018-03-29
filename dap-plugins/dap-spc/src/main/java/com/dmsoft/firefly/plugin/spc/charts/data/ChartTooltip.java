package com.dmsoft.firefly.plugin.spc.charts.data;

import com.dmsoft.firefly.plugin.spc.charts.data.basic.BarToolTip;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.BoxTooltip;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.LineTooltip;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.PointTooltip;
import javafx.scene.Node;

import java.util.function.Function;

/**
 * Created by cherry on 2018/3/20.
 */
public interface ChartTooltip {

    /**
     * Get chart point tooltip
     *
     * @return point tooltip function
     */
    default Function<PointTooltip, String> getChartPointTooltip() {
        return null;
    }

    /**
     * Get bar chart bar tooltip
     *
     * @return bar tooltip function
     */
    default Function<BarToolTip, String> getChartBarToolTip() {
        return null;
    }

    /**
     * Get box chart box tooltip
     *
     * @return box tooltip function
     */
    default Function<BoxTooltip, Node> getChartBoxTooltip() {
        return null;
    }

    /**
     * Get line tooltip
     *
     * @return line tooltip function
     */
    default Function<LineTooltip, String> getLineTooltip() {
        return null;
    }
}
