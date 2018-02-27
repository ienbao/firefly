package com.dmsoft.firefly.plugin.spc.charts.data.basic;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

/**
 * Created by cherry on 2018/2/12.
 */
public abstract class AbstractXYChartData<X, Y, Z> extends AbstractValueObject {

    //    Basic chart x, y coordinate
    private Z[] ids = null;
    private X[] x = null;
    private Y[] y = null;

    //    Series index
    private int index;

    //    Series color
    private String color;

    //    Series name
    private String seriesName;

    /**
     * Construct a new AbstractXYChartData.
     */
    public AbstractXYChartData() {
    }

    /**
     * Construct a new AbstractXYChartData with the given x. y.
     *
     * @param x x coordinates
     * @param y y coordinates
     */
    public AbstractXYChartData(X[] x, Y[] y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Construct a new AbstractXYChartData with the given x. y.id.
     *
     * @param ids unique identifiers of this value
     * @param x   x coordinates
     * @param y   y coordinates
     */
    public AbstractXYChartData(Z[] ids, X[] x, Y[] y) {
        this.ids = ids;
        this.x = x;
        this.y = y;
    }

    public Z[] getIds() {
        return ids;
    }

    public void setIds(Z[] ids) {
        this.ids = ids;
    }

    public X[] getX() {
        return x;
    }

    public void setX(X[] x) {
        this.x = x;
    }

    public Y[] getY() {
        return y;
    }

    public void setY(Y[] y) {
        this.y = y;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }
}
