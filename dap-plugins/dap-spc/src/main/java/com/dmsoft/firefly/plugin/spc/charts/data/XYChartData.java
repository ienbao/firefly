package com.dmsoft.firefly.plugin.spc.charts.data;

/**
 * Created by cherry on 2018/2/7.
 */
public class XYChartData<X, Y, Z> {

    //    Basic chart data
    private X[] x;
    private Y[] y;
    private Z[] ids;

    private String color;

    private String currentGroupKey;

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

    public Z[] getIds() {
        return ids;
    }

    public void setIds(Z[] ids) {
        this.ids = ids;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCurrentGroupKey() {
        return currentGroupKey;
    }

    public void setCurrentGroupKey(String currentGroupKey) {
        this.currentGroupKey = currentGroupKey;
    }
}
