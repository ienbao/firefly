package com.dmsoft.firefly.plugin.spc.charts.annotation;

import javafx.scene.chart.XYChart;

/**
 * Created by cherry on 2018/2/25.
 */
public interface AnnotationFetch {

    /**
     * Get annotation value by current node data
     *
     * @param id someone data for chart node
     * @return value annotation value
     */
    String getValue(Object id);

    /**
     * Get annotation text color
     *
     * @return text color
     */
    String getTextColor();

    /**
     * Get annotation show status
     *
     * @return true or false
     */
    boolean showedAnnotation();

    /**
     * Add data to annotation data
     *
     * @param data chart node data
     */
    void addData(XYChart.Data data);
}
