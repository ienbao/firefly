package com.dmsoft.firefly.plugin.spc.charts.data.basic;

import java.util.Set;

/**
 * Created by cherry on 2018/2/26.
 */
public class PointStyle {

    private String style;
    private String tooltipContent;
    private Boolean abnormal;
    private Set<String> classStyle;

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public Set<String> getClassStyle() {
        return classStyle;
    }

    public void setClassStyle(Set<String> classStyle) {
        this.classStyle = classStyle;
    }

    public String getTooltipContent() {
        return tooltipContent;
    }

    public void setTooltipContent(String tooltipContent) {
        this.tooltipContent = tooltipContent;
    }

    public Boolean getAbnormal() {
        return abnormal;
    }

    public void setAbnormal(Boolean abnormal) {
        this.abnormal = abnormal;
    }
}
