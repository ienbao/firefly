package com.dmsoft.firefly.plugin.yield.charts;



import com.dmsoft.firefly.plugin.yield.charts.data.basic.BarToolTip;

import java.util.function.Function;

public interface ChartTooltip {

    /**
     * Get bar chart bar tooltip
     *
     * @return bar tooltip function
     */
    default Function<BarToolTip, String> getChartBarToolTip() {
        return null;
    }


}
