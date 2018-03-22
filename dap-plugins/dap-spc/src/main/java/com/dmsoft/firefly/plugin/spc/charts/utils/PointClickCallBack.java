package com.dmsoft.firefly.plugin.spc.charts.utils;

/**
 * Created by cherry on 2018/3/17.
 */
public interface PointClickCallBack {

    /**
     * Run chart point click call back
     *
     * @param id click point node for unique key
     */
    void execute(Object id);
}
