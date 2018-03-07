package com.dmsoft.firefly.plugin.spc.charts.data.basic;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;
import javafx.scene.paint.Color;

/**
 * Created by cherry on 2018/2/12.
 */
public class AbnormalPointData<X, Y> extends AbstractValueObject {

    private X[] x;
    private Y[] y;
    private String name;
    private Color color;
    private boolean isVisible;

    public X[] getX() {
        return x;
    }

    public void setX(X[] x) {
        this.x = x;
    }

    public Y[] getY() {
        return y;
    }

    public void setY(Y[] y) {
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
