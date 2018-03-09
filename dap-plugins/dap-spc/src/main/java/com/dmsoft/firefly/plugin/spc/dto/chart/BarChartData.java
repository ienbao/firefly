package com.dmsoft.firefly.plugin.spc.dto.chart;

import com.dmsoft.firefly.plugin.spc.charts.data.BarCategoryData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IBarChartData;
import com.google.common.collect.Lists;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * Created by cherry on 2018/2/27.
 */
public class BarChartData<X, Y> implements IBarChartData {

    private List<BarCategoryData<X, Y>> barData = Lists.newArrayList();

    //    Series index
    private int index;
    //    Series color
    private Color color;
    //    Series name
    private String seriesName;

    public BarChartData(double[] histX, double[] histY) {
        for (int i = 0; i < histX.length; i++) {
            BarCategoryData data = new BarCategoryData();
            data.setStartValue(histX[i]);
            data.setEndValue(histX[i + 1]);
            data.setBarWidth(histX[i + 1] - histX[i]);
            data.setValue(histY[i]);
            barData.add(data);
        }
    }

    public BarChartData(String seriesName) {
        this.seriesName = seriesName;
    }

    @Override
    public Y getValueByIndex(int index) {

        return (barData != null && index < getLen()) ? barData.get(index).getValue() : null;
    }

    @Override
    public X getStartValueByIndex(int index) {

        return (barData != null && index < getLen()) ? barData.get(index).getStartValue() : null;
    }

    @Override
    public Object getBarWidthByIndex(int index) {

        return (barData != null && index < getLen()) ? barData.get(index).getBarWidth() : null;
    }

    @Override
    public Object getEndValueByIndex(int index) {

        return (barData != null && index < getLen()) ? barData.get(index).getEndValue() : null;
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
        return seriesName;
    }

    public void setBarData(List<BarCategoryData<X, Y>> barData) {
        this.barData = barData;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    @Override
    public int getSeriesIndex() {
        return 0;
    }
}
