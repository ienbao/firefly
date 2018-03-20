/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.utils;

import com.dmsoft.firefly.plugin.spc.charts.data.ChartTooltip;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.BarToolTip;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.BoxTooltip;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.LineTooltip;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.PointTooltip;
import javafx.scene.Node;

import java.util.function.Function;

/**
 * Created by Ethan.Yang on 2018/3/20.
 */
public class SpcChartToolTip implements ChartTooltip {

    public Function<PointTooltip, String> getChartPointTooltip() {
        return new Function<PointTooltip, String>() {
            @Override
            public String apply(PointTooltip pointTooltip) {
                String seriesName = pointTooltip.getSeriesName();
//                Double pointTooltip.getData().getXValue();
//                String tip =  seriesName + "\n("
//                        + pointTooltip.getData().getXValue() +
//                        ", " + pointTooltip.getData().getYValue() + ")";
                return null;
            }
        };
    }

    public Function<BarToolTip, String> getChartBarToolTip() {
        return null;
    }

    public Function<BoxTooltip, Node> getChartBoxTooltip() {
        return null;
    }

    public Function<LineTooltip, String> getLineTooltip() {
        return null;
    }
}
