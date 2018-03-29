package com.dmsoft.firefly.plugin.spc.dto.chart;

import com.dmsoft.firefly.plugin.spc.charts.data.BoxExtraData;
import com.dmsoft.firefly.plugin.spc.charts.data.BoxPlotChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IBoxAndWhiskerData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IPoint;
import com.dmsoft.firefly.plugin.spc.charts.utils.MathUtils;
import com.dmsoft.firefly.plugin.spc.dto.analysis.BoxCResultDto;
import com.dmsoft.firefly.plugin.spc.dto.analysis.SingleBoxDataDto;
import com.dmsoft.firefly.plugin.spc.dto.chart.pel.BoxAndWhiskerData;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * Created by cherry on 2018/3/21.
 */
public class SpcBoxChartData implements BoxPlotChartData {

    private BoxCResultDto boxCResultDto;
    private BoxAndWhiskerData boxAndWhiskerData;
    private String key;
    private String seriesName;
    private Color color;
    private IPoint iPoint;
    private Double minX;
    private Double maxX;
    private Double minY;
    private Double maxY;

    /**
     * Constructor for SpcBoxChartData
     *
     * @param key           unique key
     * @param boxCResultDto box result data
     * @param color         box color
     */
    public SpcBoxChartData(String key, BoxCResultDto boxCResultDto, Color color) {
        this.key = key;
        this.boxCResultDto = boxCResultDto;
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
        List<Double> boxX = Lists.newArrayList();
        for (SingleBoxDataDto singleBoxDataDto : boxCResultDto.getBoxData()) {
            BoxExtraData boxExtraData = new BoxExtraData(singleBoxDataDto.getX(), singleBoxDataDto.getCl(),
                    singleBoxDataDto.getQ3(), singleBoxDataDto.getQ1(), singleBoxDataDto.getUpperWhisker(),
                    singleBoxDataDto.getLowerWhisker(), singleBoxDataDto.getMedian());
            data.add(boxExtraData);
            boxX.add(singleBoxDataDto.getX());
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
                return DAPStringUtils.isInfinityAndNaN(xPoint.get(index)) ? null : xPoint.get(index);
            }

            @Override
            public Object getYByIndex(int index) {
                return DAPStringUtils.isInfinityAndNaN(yPoint.get(index)) ? null : yPoint.get(index);
            }

            @Override
            public int getLen() {
                return yPoint == null ? 0 : yPoint.size();
            }
        };

        maxY = MathUtils.getMax(yPoint.toArray(new Double[0]), boxY.toArray(new Double[0]));
        minY = MathUtils.getMin(yPoint.toArray(new Double[0]), boxY.toArray(new Double[0]));
        maxX = MathUtils.getMax(xPoint.toArray(new Double[0]), boxX.toArray(new Double[0]));
        minX = MathUtils.getMin(xPoint.toArray(new Double[0]), boxX.toArray(new Double[0]));
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
    public Double getXLowerBound() {
        return minX;
    }

    @Override
    public Double getXUpperBound() {
        return maxX;
    }

    @Override
    public Double getYLowerBound() {
        return minY;
    }

    @Override
    public Double getYUpperBound() {
        return maxY;
    }

    @Override
    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }
}
