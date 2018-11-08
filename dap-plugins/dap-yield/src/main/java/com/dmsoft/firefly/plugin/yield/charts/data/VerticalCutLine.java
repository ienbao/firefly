package com.dmsoft.firefly.plugin.yield.charts.data;

import javafx.scene.paint.Color;

public class VerticalCutLine implements ILineData {
    private double value;
    private Color color = Color.rgb(237, 237, 237);

    public VerticalCutLine(Double value)  {
        this.value = value;
    }
    @Override
    public double getValue() {
        return value;
    }
    public void setValue(double value) {
        this.value = value;
    }
    @Override
    public Color getColor() {
        return color;
    }
    public void setColor(Color color) {
        this.color = color;
    }
}
