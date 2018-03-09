package com.dmsoft.firefly.plugin.spc.charts.data.basic;

import javafx.scene.paint.Color;

/**
 * Created by cherry on 2018/2/10.
 */
public interface IBoxAndWhiskerData {

    Number getXPosByIndex(int index);

    Number getMeanByIndex(int index);   //cl

    Number getMedianByIndex(int index);

    Number getQ1ByIndex(int index);

    Number getQ3ByIndex(int index);

    Number getMinRegularValueByIndex(int index);

    Number getMaxRegularValueByIndex(int index);

    default Color getColor() {
        return null;
    }

    int getLen();
}
