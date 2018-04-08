package com.dmsoft.firefly.plugin.grr.charts.data;

import javafx.geometry.Orientation;
import javafx.scene.paint.Color;

/**
 * Created by cherry on 2018/3/15.
 */
public class RuleLineData implements ILineData {

    private String name;
    private double value;
    private Orientation orientation = Orientation.HORIZONTAL;
    private String lineClass;
    private Color color;

    /**
     * Construct a new RuleLineData
     */
    public RuleLineData() {

    }

    /**
     * Construct a new RuleLineData with given value
     *
     * @param value line value
     */
    public RuleLineData(double value) {
        this.value = value;
    }

    /**
     * Construct a new RuleLineData with given line name and value
     *
     * @param name  line name
     * @param value line value
     */
    public RuleLineData(String name, double value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
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
    public Orientation getPlotOrientation() {
        return orientation;
    }

    @Override
    public String getTooltipContent() {
        return "(" + getName() + " = " + getValue() + ")";
    }

    @Override
    public String getLineClass() {
        return lineClass;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public void setLineClass(String lineClass) {
        this.lineClass = lineClass;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
