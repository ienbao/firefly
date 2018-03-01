package com.dmsoft.firefly.plugin.spc.dto.chart;

import com.dmsoft.firefly.plugin.spc.charts.data.XYChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.LineData;

import java.util.Map;

/**
 * Created by cherry on 2018/2/27.
 */
public class RuleXYChartData {

    private XYChartData xyChartData;
    private Map<String, LineData> lineDataMap;

    public XYChartData getXyChartData() {
        return xyChartData;
    }

    public void setXyChartData(XYChartData xyChartData) {
        this.xyChartData = xyChartData;
    }

    public Map<String, LineData> getLineDataMap() {
        return lineDataMap;
    }

    public void setLineDataMap(Map<String, LineData> lineDataMap) {
        this.lineDataMap = lineDataMap;
    }
}
