package com.dmsoft.firefly.plugin.yield.utils.charts;

import com.dmsoft.firefly.plugin.yield.dto.YieldChartAlermDto;
import com.dmsoft.firefly.plugin.yield.utils.YieldOverviewKey;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
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
            AtomicInteger colorIndex = new AtomicInteger(3);
            data.forEach(dataItem -> {
                if (dataItem.getNode() instanceof StackPane) {
                    StackPane stackPane = (StackPane) dataItem.getNode();
                    stackPane.setAlignment(Pos.TOP_CENTER);
                    stackPane.getStyleClass().add("default-color" + colorIndex.get());
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

    public static void setChartTextAndColor(ObservableList<XYChart.Series> series, Function<String, String> formatTextFunc, Map<String, YieldChartAlermDto> yieldChartResultAlermDtoMap) {
        series.forEach(oneSeries -> {
            ObservableList<XYChart.Data> data = oneSeries.getData();
            //yieldChartResultAlermDtoMap.
            AtomicInteger colorIndex = new AtomicInteger(-1);
            data.forEach(dataItem -> {
                if (dataItem.getNode() instanceof StackPane) {
                    StackPane stackPane = (StackPane) dataItem.getNode();
                    stackPane.setAlignment(Pos.TOP_CENTER);
                    String xName = dataItem.getXValue().toString();
                    if ("%FPY".equals(xName)) {
                        xName = YieldOverviewKey.FPYPER.getCode();
                    } else if ("%NTF".equals(xName)) {
                        xName = YieldOverviewKey.NTFPER.getCode();
                    } else if ("%NG".equals(xName)) {
                        xName = YieldOverviewKey.NGPER.getCode();
                    }
                    if (YieldOverviewKey.EXCELLENT.getCode().equals(yieldChartResultAlermDtoMap.get(xName).getLevel())) {
                        colorIndex.set(0);
                    } else if (YieldOverviewKey.ADEQUATE.getCode().equals(yieldChartResultAlermDtoMap.get(xName).getLevel())) {
                        colorIndex.set(1);
                    } else if (YieldOverviewKey.MARGINAL.getCode().equals(yieldChartResultAlermDtoMap.get(xName).getLevel())) {
                        colorIndex.set(2);
                    } else if (YieldOverviewKey.BAD.getCode().equals(yieldChartResultAlermDtoMap.get(xName).getLevel())) {
                        colorIndex.set(3);
                    }
                    stackPane.getStyleClass().add("default-color" + colorIndex.get());
                    stackPane.setMaxWidth(15);
                    stackPane.setMinWidth(15);
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
