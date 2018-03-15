package com.dmsoft.firefly.plugin.grr.charts.data;

import javafx.scene.paint.Color;

/**
 * Created by cherry on 2018/3/15.
 */
public class VerticalCutLine implements ILineData {

    private double value;
    private Color color = Color.rgb(237, 237, 237);

    public VerticalCutLine() {

    }

    public VerticalCutLine(double value) {
        this.value = value;
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public String getLineClass() {
        return null;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
