package com.dmsoft.firefly.plugin.spc.charts.data.basic;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;
import com.dmsoft.firefly.plugin.spc.charts.shape.LineType;
import javafx.geometry.Orientation;

/**
 * Created by cherry on 2018/2/10.
 */
public class LineData extends AbstractValueObject {

    private Number value;
    private String name;
    private String title;
    private String color;
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
    public LineData(Number value) {
        this();
        this.value = value;
    }

    /**
     * @param name
     * @param title
     * @param value
     * @param color
     * @param plotOrientation
     */
    public LineData(Number value,
                    String name,
                    String title,
                    String color,
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

    public Number getValue() {
        return value;
    }

    public void setValue(Number value) {
        this.value = value;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
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
