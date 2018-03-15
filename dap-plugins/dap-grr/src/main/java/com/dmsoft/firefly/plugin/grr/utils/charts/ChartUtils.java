package com.dmsoft.firefly.plugin.grr.utils.charts;

import com.dmsoft.firefly.plugin.grr.charts.data.PointTooltip;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.util.function.Function;

/**
 * Created by cherry on 2018/3/14.
 */
public class ChartUtils {

    /**
     * Set chart text on top
     *
     * @param series chart series
     * @param suffix suffix
     */
    public static void setChartText(ObservableList<XYChart.Series> series, String suffix) {
        series.forEach(oneSeries -> {
            ObservableList<XYChart.Data> data = oneSeries.getData();
            data.forEach(dataItem -> {
                if (dataItem.getNode() instanceof StackPane) {
                    StackPane stackPane = (StackPane) dataItem.getNode();
                    stackPane.setAlignment(Pos.TOP_CENTER);
                    if (!stackPane.getChildren().isEmpty()) {
                        for (int i = 0; i < stackPane.getChildren().size(); i++) {
                            Node node = stackPane.getChildren().get(i);
                            if (node instanceof Text) {
                                Text oldText = (Text) node;
                                oldText.setText(dataItem.getYValue().toString() + suffix);
                                return;
                            }
                        }
                    }
                    Text text = new Text(dataItem.getYValue().toString() + suffix);
                    stackPane.getChildren().add(text);
                    stackPane.setMargin(text, new Insets(-15, 0, 0, 0));
                }
            });
        });
    }

    /**
     * Set chart tooltip
     *
     * @param series               chart series
     * @param pointTooltipFunction point tooltip rule
     */
    public static void setChartToolTip(ObservableList<XYChart.Series> series,
                                       Function<PointTooltip, String> pointTooltipFunction) {
        series.forEach(oneSeries -> {
            ObservableList<XYChart.Data> data = oneSeries.getData();
            data.forEach(dataItem -> {
                if (dataItem.getNode() instanceof StackPane) {
                    String content = pointTooltipFunction.apply(new PointTooltip(oneSeries.getName(), dataItem));
                    Tooltip.install(dataItem.getNode(), new Tooltip(content));
                }
            });
        });
    }
}
