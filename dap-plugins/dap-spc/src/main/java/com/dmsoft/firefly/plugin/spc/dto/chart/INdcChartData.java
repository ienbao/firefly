package com.dmsoft.firefly.plugin.spc.dto.chart;

import com.dmsoft.firefly.plugin.spc.charts.data.basic.IBarChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.ILineData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IXYChartData;

import java.util.List;

/**
 * Created by cherry on 2018/3/8.
 */
public interface INdcChartData {

    IXYChartData getXYChartData();

    IBarChartData getBarChartData();

    List<ILineData> getLineData();
}
