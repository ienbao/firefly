package com.dmsoft.firefly.plugin.spc.charts.data.basic;

import javafx.scene.paint.Color;

/**
 * Created by cherry on 2018/3/8.
 */
public interface IPoint<X, Y> {

    X getXByIndex(int index);

    Y getYByIndex(int index);

    int getLen();

    default Color getColor() {
        return Color.RED;
    }
}
