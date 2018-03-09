package com.dmsoft.firefly.plugin.spc.dto.chart;

import com.dmsoft.firefly.plugin.spc.charts.data.basic.ILineData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IPathData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IXYChartData;

import java.util.List;

/**
 * Created by cherry on 2018/3/9.
 */
public interface IControlChartData {

//    chart data
    IXYChartData getChartData();

//    cl
    List<ILineData> getLineData();

//    usl lsl
    List<IPathData> getBrokenLineData();
}
