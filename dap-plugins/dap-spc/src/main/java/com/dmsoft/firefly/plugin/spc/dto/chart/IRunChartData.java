package com.dmsoft.firefly.plugin.spc.dto.chart;

import com.dmsoft.firefly.plugin.spc.charts.AxisRange;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.ILineData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IXYChartData;

import java.util.List;
import java.util.Map;

/**
 * Created by cherry on 2018/3/9.
 */
public interface IRunChartData extends AxisRange {

    IXYChartData getXYChartData();

    List<ILineData> getLineData();

    Map<String, double[]> getAbnormalPointData();
}
