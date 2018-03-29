package com.dmsoft.firefly.plugin.spc.charts.view;

import com.dmsoft.firefly.sdk.utils.ColorUtils;
import javafx.scene.Group;
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

    /**
     * Constructor
     *
     * @param seriesStyleClass series style class
     * @param dataStyleClass   data style class
     */
    public Candle(String seriesStyleClass, String dataStyleClass) {
        setAutoSizeChildren(false);
        getChildren().addAll(mediaVerticalLine, bar, topHorizontalLine, bottomHorizontalLine);
        this.seriesStyleClass = seriesStyleClass;
        this.dataStyleClass = dataStyleClass;
        this.updateColor(null);
    }

    /**
     * Set series style class, data style class
     *
     * @param seriesStyleClass series style class
     * @param dataStyleClass   data style class
     */
    public void setSeriesAndDataStyleClasses(String seriesStyleClass, String dataStyleClass) {
        this.seriesStyleClass = seriesStyleClass;
        this.dataStyleClass = dataStyleClass;
        this.updateColor(null);
    }

    /**
     * Update bar size
     *
     * @param closeOffset height
     * @param highOffset  max value
     * @param lowOffset   min value
     * @param candleWidth width
     * @param color       color
     */
    public void update(double closeOffset, double highOffset, double lowOffset, double candleWidth, Color color) {
        openAboveClose = closeOffset > 0;
        updateColor(color);
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

    /**
     * Update box color
     *
     * @param color color
     */
    public void updateColor(Color color) {
        String styleColor = (color == null) ? "#6fc1be" : ColorUtils.toHexFromFXColor(color);
        mediaVerticalLine.setStyle("-fx-stroke: " + styleColor);
        topHorizontalLine.setStyle("-fx-stroke: " + styleColor);
        bottomHorizontalLine.setStyle("-fx-stroke: " + styleColor);
        StringBuilder barStyle = new StringBuilder("-fx-padding: 5;-demo-bar-fill: ");
        barStyle.append(styleColor);
        barStyle.append(";-fx-background-color: ");
        barStyle.append("linear-gradient(derive(-demo-bar-fill, -30%), derive(-demo-bar-fill, -40%)),");
        barStyle.append("linear-gradient(derive(-demo-bar-fill, 100%), derive(-demo-bar-fill, 10%)),");
        barStyle.append("linear-gradient(derive(-demo-bar-fill, 10%), derive(-demo-bar-fill, -10%));");
        barStyle.append("-fx-background-insets: 0, 1, 1;");
        bar.setStyle(barStyle.toString());
        getStyleClass().setAll("candlestick-candle", seriesStyleClass, dataStyleClass);
        bar.getStyleClass().setAll("candlestick-bar", seriesStyleClass, dataStyleClass,
                openAboveClose ? "open-above-close" : "close-above-open");
    }

}
