package com.dmsoft.firefly.plugin.spc.charts.data.basic;

import javafx.scene.paint.Color;

/**
 * Created by cherry on 2018/2/10.
 */
public interface IBoxAndWhiskerData {

    Double getXPosByIndex(int index);

    Double getMeanByIndex(int index);   //cl

    Double getMedianByIndex(int index);

    Double getQ1ByIndex(int index);

    Double getQ3ByIndex(int index);

    Double getMinRegularValueByIndex(int index);

    Double getMaxRegularValueByIndex(int index);

    default Color getColor() {
        return null;
    }

    int getLen();
}
