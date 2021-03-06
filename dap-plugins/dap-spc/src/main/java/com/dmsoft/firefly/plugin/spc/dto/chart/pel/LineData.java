package com.dmsoft.firefly.plugin.spc.dto.chart.pel;

import com.dmsoft.firefly.plugin.spc.charts.data.basic.ILineData;
import com.dmsoft.firefly.plugin.spc.charts.utils.enums.LineType;
import com.dmsoft.firefly.sdk.utils.DAPDoubleUtils;
import javafx.geometry.Orientation;
import javafx.scene.paint.Color;

/**
 * Created by cherry on 2018/2/10.
 */
public class LineData implements ILineData {

    private Double value;
    private String name;
    private String title;
    private Color color;
    private String lineClass;
    private LineType lineType;
    private Orientation plotOrientation;

    /**
     * Constructor for LineData
     */
    public LineData() {
        this.lineType = LineType.SOLID;
        this.plotOrientation = Orientation.VERTICAL;
    }

    /**
     * Constructor for LineData
     *
     * @param value           line value
     * @param name            line name
     * @param plotOrientation line orientation
     */
    public LineData(Double value, String name, Orientation plotOrientation) {
        this(value, name, plotOrientation, LineType.SOLID);
    }

    /**
     * Constructor for LineData
     *
     * @param value           line value
     * @param name            line name
     * @param plotOrientation line orientation
     * @param lineType        line type
     */
    public LineData(Double value, String name, Orientation plotOrientation, LineType lineType) {
        this();
        this.lineType = lineType;
        this.plotOrientation = plotOrientation;
        this.value = value;
        this.name = name;
    }

    /**
     * Constructor for LineData
     *
     * @param value    line value
     * @param name     line name
     * @param lineType line type
     */
    public LineData(Double value, String name, LineType lineType) {
        this(value, name, Orientation.VERTICAL, lineType);
    }

    /**
     * Constructor for LineData
     *
     * @param value line value
     * @param name  line name
     */
    public LineData(Double value, String name) {
        this();
        this.value = value;
        this.name = name;
    }

    /**
     * Constructor for LineData
     *
     * @param name            line name
     * @param title           line title
     * @param value           line value
     * @param color           line color
     * @param plotOrientation line orientation
     */
    public LineData(Double value,
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

    public Double getValue() {
        return (DAPDoubleUtils.isBlank(value) ? null : value);
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
