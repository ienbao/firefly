package com.dmsoft.firefly.plugin.spc.charts.data;

import com.dmsoft.firefly.plugin.spc.charts.AxisRange;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IBoxAndWhiskerData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IPoint;
import javafx.scene.paint.Color;

/**
 * Created by cherry on 2018/3/21.
 */
public interface BoxPlotChartData extends AxisRange {

    IBoxAndWhiskerData getBoxAndWhiskerData();

    IPoint getPoints();

    Color getColor();

    String getUniqueKey();

    default String getSeriesName() {
        return "";
    }
}
