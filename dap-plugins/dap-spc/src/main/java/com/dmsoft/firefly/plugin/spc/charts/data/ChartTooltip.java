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

    default Function<PointTooltip, String> getChartPointTooltip() {
        return null;
    }

    default Function<BarToolTip, String> getChartBarToolTip() {
        return null;
    }

    default Function<BoxTooltip, Node> getChartBoxTooltip() {
        return null;
    }

    default Function<LineTooltip, String> getLineTooltip() {
        return null;
    }
}
