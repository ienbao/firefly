package com.dmsoft.firefly.plugin.spc.charts.data.basic;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;
import javafx.geometry.Orientation;

/**
 * Created by cherry on 2018/2/12.
 */
public class BrokenLineData<X> extends AbstractValueObject {

    private X[] value;
    private String name;
    private String color;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public X[] getValue() {
        return value;
    }

    public void setValue(X[] value) {
        this.value = value;
    }
}
