package com.dmsoft.firefly.plugin.grr.utils.charts;

import com.sun.javafx.charts.Legend;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

/**
 * Created by cherry on 2018/3/14.
 */
public class LegendUtils {

    public static Legend buildLegend(ObservableList<XYChart.Series> data, String... classStyles) {

        Legend legend = new Legend();
        for (int seriesIndex = 0; seriesIndex < data.size(); seriesIndex++) {
            XYChart.Series series = data.get(seriesIndex);
            Legend.LegendItem legendItem = new Legend.LegendItem(series.getName());
            legendItem.getSymbol().getStyleClass().removeAll(classStyles);
            legendItem.getSymbol().getStyleClass().add("default-color" + seriesIndex);
            legendItem.getSymbol().getStyleClass().addAll(classStyles);
            legend.getItems().add(legendItem);
        }
        legend.getStyleClass().removeAll("legend");
        legend.getStyleClass().add("legend");
        return legend;
    }
}
