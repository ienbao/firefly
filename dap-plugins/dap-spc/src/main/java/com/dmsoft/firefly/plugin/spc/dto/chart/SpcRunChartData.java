/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.dto.chart;

import com.dmsoft.firefly.plugin.spc.charts.data.XYChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.ILineData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IXYChartData;
import com.dmsoft.firefly.plugin.spc.charts.utils.MathUtils;
import com.dmsoft.firefly.plugin.spc.dto.analysis.RunCResultDto;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import com.google.common.collect.Lists;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Map;

/**
 * Created by Ethan.Yang on 2018/3/10.
 */
public class SpcRunChartData implements IRunChartData {
    private RunCResultDto runCResultDto;
    private XYChartData xyChartData;
    private String key;
    private Color color;
    private List<ILineData> lineDataList = Lists.newArrayList();

    private Double minX;
    private Double maxX;
    private Double minY;
    private Double maxY;
    /**
     * constructor
     *
     * @param key           key
     * @param runCResultDto run chart dto
     * @param color         color
     */
    public SpcRunChartData(String key, RunCResultDto runCResultDto, Color color) {
        this.runCResultDto = runCResultDto;
        this.key = key;
        this.color = color;
        this.initData();
    }

    private void initData() {
        if (runCResultDto == null) {
            return;
        }
        Double[] x = runCResultDto.getX();
        Double[] y = runCResultDto.getY();
        xyChartData = new XYChartData<>(x, y);

        //init lines data
        String[] uslLslName = UIConstant.SPC_USL_LSL;
        Double usl = runCResultDto.getUsl();
        Double lsl = runCResultDto.getLsl();
        Double[] uslAndlsl = new Double[]{usl, lsl};

        if (usl != null) {
            ILineData uslData = new LineData(usl, uslLslName[1]);
            lineDataList.add(uslData);
        }
        if (lsl != null) {
            ILineData lslData = new LineData(lsl, uslLslName[1]);
            lineDataList.add(lslData);
        }
        String[] lineNames = UIConstant.SPC_CHART_LINE_NAME;
        Double[] cls = runCResultDto.getCls();
        if (cls != null) {
            for (int i = 0; i < cls.length; i++) {
                ILineData lineData = new LineData(cls[i], lineNames[i]);
                lineDataList.add(lineData);
            }
        }

        maxY = MathUtils.getMax(y, cls, uslAndlsl);
        minY = MathUtils.getMin(y, cls, uslAndlsl);
        maxX = MathUtils.getMax(x);
        minX = MathUtils.getMax(x);
    }

    @Override
    public IXYChartData getXYChartData() {
        return xyChartData;
    }

    @Override
    public List<ILineData> getLineData() {
        return lineDataList;
    }

    @Override
    public Map<String, double[]> getAbnormalPointData() {
        return null;
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