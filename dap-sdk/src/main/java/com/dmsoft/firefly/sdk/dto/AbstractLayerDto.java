package com.dmsoft.firefly.sdk.dto;

import java.awt.*;
import java.util.Map;

/**
 * Created by GuangLi on 2018/1/26.
 */
public class AbstractLayerDto {
    //each chart layer key
    private String layerKey;
    private Color color;
    // key : attribute name
    private Map<String, String[]> attribute;
    private String[] x;
    private String[] y;

    public String getLayerKey() {
        return layerKey;
    }

    public void setLayerKey(String layerKey) {
        this.layerKey = layerKey;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Map<String, String[]> getAttribute() {
        return attribute;
    }

    public void setAttribute(Map<String, String[]> attribute) {
        this.attribute = attribute;
    }

    public String[] getX() {
        return x;
    }

    public void setX(String[] x) {
        this.x = x;
    }

    public String[] getY() {
        return y;
    }

    public void setY(String[] y) {
        this.y = y;
    }
}
