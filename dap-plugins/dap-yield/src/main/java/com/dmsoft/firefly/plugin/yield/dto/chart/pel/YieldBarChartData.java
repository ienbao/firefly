package com.dmsoft.firefly.plugin.yield.dto.chart.pel;


import com.dmsoft.firefly.plugin.yield.charts.data.BarCategoryData;
import com.dmsoft.firefly.plugin.yield.charts.data.basic.IBarChartData;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import javafx.scene.paint.Color;

import java.util.List;

public class YieldBarChartData implements IBarChartData<Double,Double> {
    private List<BarCategoryData<Double, Double>> barData = Lists.newArrayList();//创建barData数组
    //    Series index
    private int index;
    //    Series color
    private Color color;
    //    Series name
    private String seriesName;

    public YieldBarChartData(Double[] histX, Double[] histY) {

        for (int i = 0; i < histX.length - 1; i++) {
            BarCategoryData data = new BarCategoryData();
            data.setStartValue(histX[i]);
            data.setEndValue(histX[i + 1]);
            data.setBarWidth(histX[i + 1] - histX[i]);
            data.setValue(histY[i]);
            barData.add(data);//添加barData
        }
    }


    @Override
    public Double getValueByIndex(int index) {
        return (barData != null && index < getLen()) ? ((DAPStringUtils.isInfinityAndNaN(barData.get(index).getValue()))? null : barData.get(index).getValue()) :null;
    }

    @Override
    public Double getStartValueByIndex(int index) {
        return (barData != null && index < getLen()) ? ((DAPStringUtils.isInfinityAndNaN(barData.get(index).getStartValue()) ? null :barData.get(index).getStartValue())) :null;
    }

    @Override
    public Double getBarWidthByIndex(int index) {
        return (barData != null && index < getLen()) ? ((DAPStringUtils.isInfinityAndNaN(barData.get(index).getBarWidth()) ? null : barData.get(index).getBarWidth())) : null;
    }

    @Override
    public Double getEndValueByIndex(int index) {

        return (barData != null && index < getLen()) ? ((DAPStringUtils.isInfinityAndNaN(barData.get(index).getEndValue()) ? null :barData.get(index).getEndValue())) : null;
    }

    @Override
    public int getLen() {
        return barData == null ? 0 : barData.size();
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public String getSeriesName() {
        return null;
    }

    @Override
    public String getTooltipContent(int index) {
        return null;
    }
}