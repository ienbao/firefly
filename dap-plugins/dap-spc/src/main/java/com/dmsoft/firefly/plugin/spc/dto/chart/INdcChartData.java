package com.dmsoft.firefly.plugin.spc.dto.chart;

import com.dmsoft.firefly.plugin.spc.charts.AxisRange;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IBarChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.ILineData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IXYChartData;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * Created by cherry on 2018/3/8.
 */
public interface INdcChartData {

    IXYChartData getCurveData();

    IBarChartData getBarData();

    List<ILineData> getLineData();

    Color getColor();

    String getUniqueKey();
}
