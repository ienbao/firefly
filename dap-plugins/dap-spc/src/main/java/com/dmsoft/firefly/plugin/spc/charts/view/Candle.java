package com.dmsoft.firefly.plugin.spc.charts.view;

import com.dmsoft.firefly.sdk.utils.ColorUtils;
import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * Created by cherry on 2018/2/10.
 */
public class Candle extends Group {

    private Line mediaVerticalLine = new Line();
    private Line topHorizontalLine = new Line();
    private Line bottomHorizontalLine = new Line();
    private Region bar = new Region();
    private String seriesStyleClass;
    private String dataStyleClass;
    private boolean openAboveClose = true;
    private Tooltip tooltip = new Tooltip();

    public Candle(String seriesStyleClass, String dataStyleClass) {
        setAutoSizeChildren(false);
        getChildren().addAll(mediaVerticalLine, bar, topHorizontalLine, bottomHorizontalLine);
        this.seriesStyleClass = seriesStyleClass;
        this.dataStyleClass = dataStyleClass;
        updateStyleClasses(null);
        tooltip.setGraphic(new TooltipContent());
        tooltip.getStyleClass().setAll("candlestick-tooltip");
        Tooltip.install(bar, tooltip);
    }

    public void setSeriesAndDataStyleClasses(String seriesStyleClass, String dataStyleClass) {
        this.seriesStyleClass = seriesStyleClass;
        this.dataStyleClass = dataStyleClass;
        updateStyleClasses(null);
    }

    public void update(double closeOffset, double highOffset, double lowOffset, double candleWidth, Color color) {
        openAboveClose = closeOffset > 0;
        updateStyleClasses(color);
        mediaVerticalLine.setStartY(highOffset);
        mediaVerticalLine.setEndY(lowOffset);
        topHorizontalLine.setStartX(mediaVerticalLine.getStartX() - candleWidth / 4);
        topHorizontalLine.setEndX(mediaVerticalLine.getEndX() + candleWidth / 4);
        topHorizontalLine.setStartY(highOffset);
        topHorizontalLine.setEndY(highOffset);
        bottomHorizontalLine.setStartX(mediaVerticalLine.getStartX() - candleWidth / 4);
        bottomHorizontalLine.setEndX(mediaVerticalLine.getEndX() + candleWidth / 4);
        bottomHorizontalLine.setStartY(lowOffset);
        bottomHorizontalLine.setEndY(lowOffset);
        if (candleWidth == -1) {
            candleWidth = bar.prefWidth(-1);
        }
        if (openAboveClose) {
            bar.resizeRelocate(-candleWidth / 2, 0, candleWidth, closeOffset);
        } else {
            bar.resizeRelocate(-candleWidth / 2, closeOffset, candleWidth, closeOffset * -1);
        }
    }

    public void updateTooltip(double xPos, double median, double min, double max, double q1, double q3) {
        TooltipContent tooltipContent = (TooltipContent) tooltip.getGraphic();
        tooltipContent.update(xPos, median, min, max, q1, q3);
//                    tooltip.setText("Open: "+open+"\nClose: "+close+"\nHigh: "+high+"\nLow: "+low);
    }

    private void updateStyleClasses(Color color) {
        String styleColor = (color == null) ? "#6fc1be" : ColorUtils.toHexFromFXColor(color);
        mediaVerticalLine.setStyle("-fx-stroke: " + styleColor);
        topHorizontalLine.setStyle("-fx-stroke: " + styleColor);
        bottomHorizontalLine.setStyle("-fx-stroke: " + styleColor);

        bar.setStyle("-fx-padding: 5;-demo-bar-fill: " + styleColor + ";-fx-background-color: " +
                "linear-gradient(derive(-demo-bar-fill, -30%), derive(-demo-bar-fill, -40%))," +
                "linear-gradient(derive(-demo-bar-fill, 100%), derive(-demo-bar-fill, 10%))," +
                "linear-gradient(derive(-demo-bar-fill, 10%), derive(-demo-bar-fill, -10%));" +
                "-fx-background-insets: 0, 1, 1;");

        getStyleClass().setAll("candlestick-candle", seriesStyleClass, dataStyleClass);
//        mediaVerticalLine.getStyleClass().setAll("candlestick-line", seriesStyleClass, dataStyleClass,
//                openAboveClose ? "open-above-close" : "close-above-open");
//
//        topHorizontalLine.getStyleClass().setAll("candlestick-line", seriesStyleClass, dataStyleClass,
//                openAboveClose ? "open-above-close" : "close-above-open");
//
//        bottomHorizontalLine.getStyleClass().setAll("candlestick-line", seriesStyleClass, dataStyleClass,
//                openAboveClose ? "open-above-close" : "close-above-open");

        bar.getStyleClass().setAll("candlestick-bar", seriesStyleClass, dataStyleClass,
                openAboveClose ? "open-above-close" : "close-above-open");
    }

}
