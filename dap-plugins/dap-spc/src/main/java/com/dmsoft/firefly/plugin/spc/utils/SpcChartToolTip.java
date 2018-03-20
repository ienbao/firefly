/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.utils;

import com.dmsoft.firefly.plugin.spc.charts.data.ChartTooltip;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.BarToolTip;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.BoxTooltip;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.LineTooltip;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.PointTooltip;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import javafx.scene.Node;

import java.util.function.Function;

/**
 * Created by Ethan.Yang on 2018/3/20.
 */
public class SpcChartToolTip implements ChartTooltip {
    private DigNumInstance digNumInstance = DigNumInstance.newInstance();

    public Function<PointTooltip, String> getChartPointTooltip() {
        return new Function<PointTooltip, String>() {
            @Override
            public String apply(PointTooltip pointTooltip) {
                String seriesName = pointTooltip.getSeriesName();
                String x = DAPStringUtils.formatDouble((Double) pointTooltip.getData().getXValue(), digNumInstance.getDigNum());
                String y = DAPStringUtils.formatDouble((Double) pointTooltip.getData().getYValue(), digNumInstance.getDigNum());
                String tip = seriesName + "\n(" + x + ", " + y + ")";
                return tip;
            }
        };
    }

    public Function<BarToolTip, String> getChartBarToolTip() {
        return new Function<BarToolTip, String>() {
            @Override
            public String apply(BarToolTip barToolTip) {
                String seriesName = barToolTip.getSeriesName();
                String startV = DAPStringUtils.formatDouble(barToolTip.getStartValue(), digNumInstance.getDigNum());
                String endV = DAPStringUtils.formatDouble(barToolTip.getStartValue(), digNumInstance.getDigNum());
                String value = DAPStringUtils.formatDouble(barToolTip.getValue(), digNumInstance.getDigNum());
                String tip = seriesName + "\nX[" + startV + ", " + endV + ")\n" + "Y=" + value;
                return tip;
            }
        };
    }

    public Function<BoxTooltip, Node> getChartBoxTooltip() {
        return new Function<BoxTooltip, Node>() {
            @Override
            public Node apply(BoxTooltip boxTooltip) {
                String max = DAPStringUtils.formatDouble(boxTooltip.getMaxRegularValue(), digNumInstance.getDigNum());
                String min = DAPStringUtils.formatDouble(boxTooltip.getMinRegularValue(), digNumInstance.getDigNum());
                String q1 = DAPStringUtils.formatDouble(boxTooltip.getQ1(), digNumInstance.getDigNum());
                String q3 = DAPStringUtils.formatDouble(boxTooltip.getQ3(), digNumInstance.getDigNum());
                String median = DAPStringUtils.formatDouble(boxTooltip.getMedian(), digNumInstance.getDigNum());
                BoxChartToolTipContent boxChartToolTipContent = new BoxChartToolTipContent(median, min, max, q1, q3);
                return boxChartToolTipContent;
            }
        };
    }

    public Function<LineTooltip, String> getLineTooltip() {
        return new Function<LineTooltip, String>() {
            @Override
            public String apply(LineTooltip lineTooltip) {
                String seriesName = lineTooltip.getExternalName();
                String name = lineTooltip.getName();
                String value = DAPStringUtils.formatDouble(lineTooltip.getValue(), digNumInstance.getDigNum());
                String tip = seriesName + "\n" + name + "= " + value;
                return null;
            }
        };
    }
}
