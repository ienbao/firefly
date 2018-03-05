package com.dmsoft.firefly.plugin.spc.charts.data.basic;

/**
 * Created by cherry on 2018/2/10.
 */
public interface IBoxAndWhiskerData {

    Number getxPosByIndex(int index);

    Number getMeanByIndex(int index);

    Number getMedianByIndex(int index);

    Number getQ1ByIndex(int index);

    Number getQ3ByIndex(int index);

    Number getMinRegularValueByIndex(int index);

    Number getMaxRegularValueByIndex(int index);

    String getColor();

    int getLen();
}
