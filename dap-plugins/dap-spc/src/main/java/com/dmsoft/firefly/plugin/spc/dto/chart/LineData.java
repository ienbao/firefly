package com.dmsoft.firefly.plugin.spc.dto.chart;

import com.dmsoft.firefly.plugin.spc.charts.data.basic.ILineData;
import com.dmsoft.firefly.plugin.spc.charts.utils.enums.LineType;
import javafx.geometry.Orientation;
import javafx.scene.paint.Color;

/**
 * Created by cherry on 2018/2/10.
 */
public class LineData implements ILineData {

    private double value;
    private String name;
    private String title;
    private Color color;
    private String lineClass;
    private LineType lineType;
    private Orientation plotOrientation;

    public LineData() {
        this.lineType = LineType.SOLID;
        this.plotOrientation = Orientation.VERTICAL;
    }

    /**
     * @param value
     */
    public LineData(double value, String name) {
        this();
        this.name = name;
        this.value = value;
    }

    /**
     * @param name
     * @param title
     * @param value
     * @param color
     * @param plotOrientation
     */
    public LineData(double value,
                    String name,
                    String title,
                    Color color,
                    Orientation plotOrientation) {
        this.name = name;
        this.title = title;
        this.value = value;
        this.color = color;
        this.plotOrientation = plotOrientation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Orientation getPlotOrientation() {
        return plotOrientation;
    }

    public void setPlotOrientation(Orientation plotOrientation) {
        this.plotOrientation = plotOrientation;
    }

    public LineType getLineType() {
        return lineType;
    }

    public void setLineType(LineType lineType) {
        this.lineType = lineType;
    }

    public String getLineClass() {
        return lineClass;
    }

    public void setLineClass(String lineClass) {
        this.lineClass = lineClass;
    }
}
