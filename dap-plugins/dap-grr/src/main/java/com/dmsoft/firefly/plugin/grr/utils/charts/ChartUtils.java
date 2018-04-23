package com.dmsoft.firefly.plugin.grr.utils.charts;

import com.dmsoft.firefly.plugin.grr.charts.data.PointTooltip;
import com.dmsoft.firefly.plugin.grr.utils.DigNumInstance;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.util.function.Function;

/**
 * Created by cherry on 2018/3/14.
 */
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

    /**
     * Set chart tooltip
     *
     * @param series               chart series
     * @param pointTooltipFunction point tooltip rule
     */
    public static void setChartToolTip(ObservableList<XYChart.Series> series,
                                       Function<PointTooltip, String> pointTooltipFunction) {
        Tooltip tooltip = new Tooltip();
        series.forEach(oneSeries -> {
            ObservableList<XYChart.Data> data = oneSeries.getData();
            data.forEach(dataItem -> {
                if (dataItem.getNode() instanceof StackPane) {
                    String content = pointTooltipFunction.apply(new PointTooltip(oneSeries.getName(), dataItem));
                    final Node dataNode = dataItem.getNode();
                    dataNode.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                                tooltip.setText(content);
                                tooltip.show(dataNode, event.getScreenX() + ANCHOR_X, event.getScreenY() + ANCHOR_Y);
                            }
                    );
                    dataNode.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
                                if (tooltip.isShowing()) {
                                    tooltip.setAnchorX(event.getScreenX() + ANCHOR_X);
                                    tooltip.setAnchorY(event.getScreenY() + ANCHOR_Y);
                                }
                            }
                    );
                    dataNode.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
                        if (tooltip.isShowing()) {
                            tooltip.hide();
                        }
                    });
//                    Tooltip.install(dataItem.getNode(), new Tooltip(content));
                }
            });
        });
    }
}
