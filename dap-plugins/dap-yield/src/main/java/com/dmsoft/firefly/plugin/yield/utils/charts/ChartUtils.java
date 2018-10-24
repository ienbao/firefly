package com.dmsoft.firefly.plugin.yield.utils.charts;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.util.function.Function;

public class ChartUtils {

    private static final double ANCHOR_X = 10.0;
    private static final double ANCHOR_Y = 15.0;

    /**
     * Set chart text on top
     *
     * @param series chart series
     */
    public static void setChartText(ObservableList<XYChart.Series> series, Function<String, String> formatTextFunc) {
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
                                String textVal = formatTextFunc.apply(dataItem.getYValue().toString());
                                oldText.setText(textVal);
                                return;
                            }
                        }
                    }
                    String textVal = formatTextFunc.apply(dataItem.getYValue().toString());
                    Text text = new Text(textVal);
                    stackPane.getChildren().add(text);
                    stackPane.setMargin(text, new Insets(-15, 0, 0, 0));
                }
            });
        });
    }


}
