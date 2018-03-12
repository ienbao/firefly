/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.dto.chart;

import com.dmsoft.firefly.plugin.spc.charts.data.BoxExtraData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IBoxAndWhiskerData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IPoint;
import com.dmsoft.firefly.plugin.spc.charts.utils.MathUtils;
import com.dmsoft.firefly.plugin.spc.dto.analysis.BoxCResultDto;
import com.dmsoft.firefly.plugin.spc.dto.analysis.SingleBoxDataDto;
import com.google.common.collect.Lists;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * Created by Ethan.Yang on 2018/3/10.
 */
public class SpcBoxChartData implements IBoxChartData {
    private BoxCResultDto boxCResultDto;
    private BoxAndWhiskerData boxAndWhiskerData;
    private String key;
    private Color color;
    private IPoint iPoint;
    private Double minX;
    private Double maxX;
    private Double minY;
    private Double maxY;

    /**
     * constructor
     *
     * @param key           key
     * @param boxCResultDto box chart dto
     * @param color         color
     */
    public SpcBoxChartData(String key, BoxCResultDto boxCResultDto, Color color) {
        this.boxCResultDto = boxCResultDto;
        this.key = key;
        this.color = color;
        this.initData();
    }

    private void initData() {
        if (boxCResultDto == null || boxCResultDto.getBoxData() == null) {
            return;
        }
        boxAndWhiskerData = new BoxAndWhiskerData();
        List<BoxExtraData> data = Lists.newArrayList();
        List<Double> xPoint = Lists.newArrayList();
        List<Double> yPoint = Lists.newArrayList();
        List<Double> boxY = Lists.newArrayList();
        for (SingleBoxDataDto singleBoxDataDto : boxCResultDto.getBoxData()) {
            BoxExtraData boxExtraData = new BoxExtraData(singleBoxDataDto.getX(), singleBoxDataDto.getCl(),
                    singleBoxDataDto.getMedian(), singleBoxDataDto.getQ1(), singleBoxDataDto.getQ3(), singleBoxDataDto.getLowerWhisker(),
                    singleBoxDataDto.getUpperWhisker());
            data.add(boxExtraData);
            boxY.add(singleBoxDataDto.getCl());
            boxY.add(singleBoxDataDto.getMedian());
            boxY.add(singleBoxDataDto.getQ1());
            boxY.add(singleBoxDataDto.getQ3());
            boxY.add(singleBoxDataDto.getLowerWhisker());
            boxY.add(singleBoxDataDto.getUpperWhisker());
            if (singleBoxDataDto.getAbnormalPoints() == null) {
                continue;
            }
            for (int j = 0; j < singleBoxDataDto.getAbnormalPoints().length; j++) {
                xPoint.add(singleBoxDataDto.getX());
                yPoint.add(singleBoxDataDto.getAbnormalPoints()[j]);
            }
        }
        boxAndWhiskerData.setData(data);
        boxAndWhiskerData.setColor(color);

        iPoint = new IPoint() {
            @Override
            public Object getXByIndex(int index) {
                return xPoint.get(index);
            }

            @Override
            public Object getYByIndex(int index) {
                return yPoint.get(index);
            }

            @Override
            public int getLen() {
                return xPoint == null ? 0 : xPoint.size();
            }
        };

        maxY = MathUtils.getMax((Double[]) yPoint.toArray(), (Double[]) boxY.toArray());
        minY = MathUtils.getMin((Double[]) yPoint.toArray(), (Double[]) boxY.toArray());
        maxX = MathUtils.getMax((Double[]) xPoint.toArray());
        minX = MathUtils.getMax((Double[]) xPoint.toArray());
    }

    @Override
    public IBoxAndWhiskerData getBoxAndWhiskerData() {
        return boxAndWhiskerData;
    }

    @Override
    public IPoint getPoints() {
        return iPoint;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public String getUniqueKey() {
        return key;
    }

    @Override
    public Number getXLowerBound() {
        return minX;
    }

    @Override
    public Number getXUpperBound() {
        return maxX;
    }

    @Override
    public Number getYLowerBound() {
        return minY;
    }

    @Override
    public Number getYUpperBound() {
        return maxY;
    }
}
