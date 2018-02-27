package com.dmsoft.firefly.plugin.spc.charts.view;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 * Created by cherry on 2018/2/10.
 */
public class TooltipContent extends GridPane {

    private Label xVLbl = new Label();
    private Label q3VLbl = new Label();
    private Label q1VLbl = new Label();
    private Label maxVLbl = new Label();
    private Label minVLbl = new Label();
    private Label medianVLbl = new Label();

    TooltipContent() {

        Label xLbl = new Label("X:");
        Label q3Lbl = new Label("Q3:");
        Label q1Lbl = new Label("Q1:");
        Label maxLbl = new Label("Max:");
        Label minLbl = new Label("Min:");
        Label medianLbl = new Label("Median");
        xLbl.getStyleClass().add("candlestick-tooltip-label");
        q3Lbl.getStyleClass().add("candlestick-tooltip-label");
        q1Lbl.getStyleClass().add("candlestick-tooltip-label");
        maxLbl.getStyleClass().add("candlestick-tooltip-label");
        minLbl.getStyleClass().add("candlestick-tooltip-label");
        medianLbl.getStyleClass().add("candlestick-tooltip-label");
        setConstraints(xLbl, 0, 0);
        setConstraints(xVLbl, 1, 0);
        setConstraints(medianLbl, 0, 1);
        setConstraints(medianVLbl, 1, 1);
        setConstraints(minLbl, 0, 2);
        setConstraints(minVLbl, 1, 2);
        setConstraints(maxLbl, 0, 3);
        setConstraints(maxVLbl, 1, 3);
        setConstraints(q1Lbl, 0, 4);
        setConstraints(q1VLbl, 1, 4);
        setConstraints(q3Lbl, 0, 5);
        setConstraints(q3VLbl, 1, 5);

        getChildren().addAll(
                xLbl, xVLbl,
                medianLbl, medianVLbl,
                minLbl, minVLbl,
                maxLbl, maxVLbl,
                q1Lbl, q1VLbl,
                q3Lbl, q3VLbl
        );
    }

    public void update(double xPos, double median, double min, double max, double q1, double q3) {
        xVLbl.setText(Double.toString(xPos));
        medianVLbl.setText(Double.toString(median));
        q3VLbl.setText(Double.toString(q3));
        q1VLbl.setText(Double.toString(q1));
        maxVLbl.setText(Double.toString(max));
        minVLbl.setText(Double.toString(min));
    }
}
