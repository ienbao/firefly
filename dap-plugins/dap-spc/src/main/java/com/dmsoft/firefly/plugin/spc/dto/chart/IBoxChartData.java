package com.dmsoft.firefly.plugin.spc.dto.chart;

import com.dmsoft.firefly.plugin.spc.charts.data.basic.IBoxAndWhiskerData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IPoint;

import java.util.List;

/**
 * Created by cherry on 2018/3/8.
 */
public interface IBoxChartData {

    IBoxAndWhiskerData getBoxAndWhiskerData();

    List<IPoint> getPoints();
}
