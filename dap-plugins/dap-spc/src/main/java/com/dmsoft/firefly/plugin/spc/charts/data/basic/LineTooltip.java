package com.dmsoft.firefly.plugin.spc.charts.data.basic;

/**
 * Created by cherry on 2018/3/20.
 */
public class LineTooltip {

    private String externalName;
    private String name;
    private Double value;

    /**
     * Constructor for LineTooltip
     */
    public LineTooltip() {
    }

    /**
     * Constructor for LineTooltip
     *
     * @param externalName external name
     * @param name         line name
     * @param value        line value
     */
    public LineTooltip(String externalName, String name, Double value) {
        this.externalName = externalName;
        this.name = name;
        this.value = value;
    }

    public String getExternalName() {
        return externalName;
    }

    public void setExternalName(String externalName) {
        this.externalName = externalName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
