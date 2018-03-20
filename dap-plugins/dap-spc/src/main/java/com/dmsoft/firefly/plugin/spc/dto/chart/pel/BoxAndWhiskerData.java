package com.dmsoft.firefly.plugin.spc.dto.chart.pel;

import com.dmsoft.firefly.plugin.spc.charts.data.BoxExtraData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IBoxAndWhiskerData;
import com.dmsoft.firefly.sdk.utils.DAPDoubleUtils;
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
    public Double getXPosByIndex(int index) {
        return data == null || data.size() <= index ? null : (DAPDoubleUtils.isBlank(data.get(index).getxPos()) ? null : data.get(index).getxPos());
    }

    @Override
    public Double getMeanByIndex(int index) {
        return data == null || data.size() <= index ? null : (DAPDoubleUtils.isBlank(data.get(index).getMean()) ? null : data.get(index).getMean());
    }

    @Override
    public Double getMedianByIndex(int index) {
        return data == null || data.size() <= index ? null : (DAPDoubleUtils.isBlank(data.get(index).getMedian()) ? null : data.get(index).getMedian());
    }

    @Override
    public Double getQ1ByIndex(int index) {
        return data == null || data.size() <= index ? null : (DAPDoubleUtils.isBlank(data.get(index).getQ1()) ? null : data.get(index).getQ1());
    }

    @Override
    public Double getQ3ByIndex(int index) {
        return data == null || data.size() <= index ? null : (DAPDoubleUtils.isBlank(data.get(index).getQ3()) ? null : data.get(index).getQ3());
    }

    @Override
    public Double getMinRegularValueByIndex(int index) {
        return data == null || data.size() <= index ? null : (DAPDoubleUtils.isBlank(data.get(index).getMinRegularValue()) ? null : data.get(index).getMinRegularValue());
    }

    @Override
    public Double getMaxRegularValueByIndex(int index) {
        return data == null || data.size() <= index ? null : (DAPDoubleUtils.isBlank(data.get(index).getMaxRegularValue()) ? null : data.get(index).getMaxRegularValue());
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
