package com.dmsoft.firefly.plugin.spc.dto.chart;

import com.dmsoft.firefly.plugin.spc.charts.data.basic.IBoxAndWhiskerData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IPoint;
import javafx.scene.paint.Color;

/**
 * Created by cherry on 2018/3/8.
 */
public interface IBoxChartData {

    IBoxAndWhiskerData getBoxAndWhiskerData();

    IPoint getPoints();

    Color getColor();

    String getUniqueKey();
}
