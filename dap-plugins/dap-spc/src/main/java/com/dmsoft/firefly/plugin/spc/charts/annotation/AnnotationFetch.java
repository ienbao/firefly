package com.dmsoft.firefly.plugin.spc.charts.annotation;

import javafx.scene.chart.XYChart;

/**
 * Created by cherry on 2018/2/25.
 */
public interface AnnotationFetch {

    String getValue(Object id);

    String getTextColor();

    boolean showedAnnotation();

    void addData(XYChart.Data data);
}
