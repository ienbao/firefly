package com.dmsoft.firefly.plugin.spc.charts.data;

import com.dmsoft.firefly.plugin.spc.charts.data.basic.IXYChartData;
import javafx.scene.paint.Color;

/**
 * Created by cherry on 2018/2/27.
 */
public class XYChartData<X, Y> implements IXYChartData {

    private X[] x = null;
    private Y[] y = null;
    private Object[] ids = null;

    //    Series index
    private int index;

    //    Series color
    private Color color;

    //    Series name
    private String seriesName;

    public XYChartData() {
    }

    public XYChartData(X[] x, Y[] y) {
        this.x = x;
        this.y = y;
    }

    public XYChartData(X[] x, Y[] y, Object[] ids) {
        this.x = x;
        this.y = y;
        this.ids = ids;
    }

    @Override
    public int getLen() {

        return (x == null || y == null) ? 0 : (x.length < y.length) ? x.length : y.length;
    }

    @Override
    public Object getXValueByIndex(int index) {

        return (index >= 0 && index < getLen()) ? x[index] : null;
    }

    @Override
    public Object getYValueByIndex(int index) {

        return (index >= 0 && index < getLen()) ? y[index] : null;
    }

    @Override
    public Object getExtraValueByIndex(int index) {

        return (index >= 0 && index < getLen()) && (ids != null) && (ids.length > index) ? ids[index] : null;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public String getSeriesName() {
        return seriesName;
    }

    public void setX(X[] x) {
        this.x = x;
    }

    public void setY(Y[] y) {
        this.y = y;
    }

    public void setIds(Object[] ids) {
        this.ids = ids;
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

    public int getIndex() {
        return index;
    }
}
