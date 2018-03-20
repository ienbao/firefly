/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.utils;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Created by Ethan.Yang on 2018/3/20.
 */
public class BoxChartToolTipContent extends HBox {

    private VBox leftVBox = new VBox();
    private VBox rightVBox = new VBox();
    private Label q3VLbl = new Label();
    private Label q1VLbl = new Label();
    private Label maxVLbl = new Label();
    private Label minVLbl = new Label();
    private Label medianVLbl = new Label();

    private static final double WIDTH = 160;
    private static final double HEIGHT = 118;

    /**
     * constructor
     *
     * @param median median
     * @param min    min
     * @param max    max
     * @param q1     q1
     * @param q3     q3
     */
    public BoxChartToolTipContent(double median, double min, double max, double q1, double q3) {
        this.init();
        medianVLbl.setText(Double.toString(median));
        q3VLbl.setText(Double.toString(q3));
        q1VLbl.setText(Double.toString(q1));
        maxVLbl.setText(Double.toString(max));
        minVLbl.setText(Double.toString(min));
    }

    private void init() {
        Label q3Lbl = new Label("Q3:");
        Label q1Lbl = new Label("Q1:");
        Label maxLbl = new Label("Max:");
        Label minLbl = new Label("Min:");
        Label medianLbl = new Label("Median:");
        leftVBox.getChildren().addAll(medianLbl, minLbl, maxLbl, q1Lbl, q3Lbl);
        rightVBox.getChildren().addAll(medianVLbl, minVLbl, maxVLbl, q1VLbl, q3VLbl);
        leftVBox.getStyleClass().removeAll("candlestick-tooltip-box");
        leftVBox.getStyleClass().add("candlestick-tooltip-box");
        rightVBox.getStyleClass().removeAll("candlestick-tooltip-box");
        rightVBox.getStyleClass().add("candlestick-tooltip-box");
        this.setPrefSize(WIDTH, HEIGHT);
        this.getChildren().addAll(leftVBox, rightVBox);
    }
}
