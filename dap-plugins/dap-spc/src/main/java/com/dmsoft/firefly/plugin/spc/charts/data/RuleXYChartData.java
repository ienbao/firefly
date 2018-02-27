package com.dmsoft.firefly.plugin.spc.charts.data;

import com.dmsoft.firefly.plugin.spc.charts.data.basic.AbnormalPointData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.AbstractXYChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.LineData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.BrokenLineData;

import java.util.Map;

/**
 * Created by cherry on 2018/2/12.
 */
public class RuleXYChartData extends AbstractXYChartData {

    //    Line rule data
    private Map<String, LineData> lineDataMap;

    //    Broken line data
    private Map<String, BrokenLineData> brokenLineDataMap;

    //    Abnormal point data
    private Map<String, AbnormalPointData> abnormalPointDataMap;

    public Map<String, LineData> getLineDataMap() {
        return lineDataMap;
    }

    public void setLineDataMap(Map<String, LineData> lineDataMap) {
        this.lineDataMap = lineDataMap;
    }

    public Map<String, BrokenLineData> getBrokenLineDataMap() {
        return brokenLineDataMap;
    }

    public void setBrokenLineDataMap(Map<String, BrokenLineData> brokenLineDataMap) {
        this.brokenLineDataMap = brokenLineDataMap;
    }

    public Map<String, AbnormalPointData> getAbnormalPointDataMap() {
        return abnormalPointDataMap;
    }

    public void setAbnormalPointDataMap(Map<String, AbnormalPointData> abnormalPointDataMap) {
        this.abnormalPointDataMap = abnormalPointDataMap;
    }
}
