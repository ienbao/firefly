package com.dmsoft.firefly.plugin.spc.dto.chart;

import com.dmsoft.firefly.plugin.spc.charts.data.BoxExtraData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IBoxAndWhiskerData;
import com.google.common.collect.Lists;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * Created by cherry on 2018/3/4.
 */
public class BoxAndWhiskerData implements IBoxAndWhiskerData {

    private List<BoxExtraData> data = Lists.newArrayList();

    private Color color;

    @Override
    public Number getXPosByIndex(int index) {
        return data == null || data.size() <= index ? null : data.get(index).getxPos();
    }

    @Override
    public Number getMeanByIndex(int index) {
        return data == null || data.size() <= index ? null : data.get(index).getMean();
    }

    @Override
    public Number getMedianByIndex(int index) {
        return data == null || data.size() <= index ? null : data.get(index).getMedian();
    }

    @Override
    public Number getQ1ByIndex(int index) {
        return data == null || data.size() <= index ? null : data.get(index).getQ1();
    }

    @Override
    public Number getQ3ByIndex(int index) {
        return data == null || data.size() <= index ? null : data.get(index).getQ3();
    }

    @Override
    public Number getMinRegularValueByIndex(int index) {
        return data == null || data.size() <= index ? null : data.get(index).getMinRegularValue();
    }

    @Override
    public Number getMaxRegularValueByIndex(int index) {
        return data == null || data.size() <= index ? null : data.get(index).getMaxRegularValue();
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public int getLen() {
        return data == null ? 0 : data.size();
    }

    public void setData(List<BoxExtraData> data) {
        this.data = data;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
