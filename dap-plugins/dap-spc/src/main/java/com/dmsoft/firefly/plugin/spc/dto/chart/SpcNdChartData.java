/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.dto.chart;

import com.dmsoft.firefly.plugin.spc.charts.data.basic.IBarChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.ILineData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IXYChartData;
import com.dmsoft.firefly.plugin.spc.charts.utils.MathUtils;
import com.dmsoft.firefly.plugin.spc.dto.analysis.NDCResultDto;
import com.dmsoft.firefly.plugin.spc.dto.chart.pel.LineData;
import com.dmsoft.firefly.plugin.spc.dto.chart.pel.SpcBarChartData;
import com.dmsoft.firefly.plugin.spc.dto.chart.pel.SpcXYChartData;
import com.dmsoft.firefly.plugin.spc.utils.ChartDataUtils;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import com.google.common.collect.Lists;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * Created by Ethan.Yang on 2018/3/9.
 */
public class SpcNdChartData implements INdcChartData {
    private NDCResultDto ndcResultDto;
    private SpcXYChartData xyChartData;
    private SpcBarChartData barChartData;
    private List<ILineData> lineDataList = Lists.newArrayList();

    private String key;
    private Color color;

    private Double minX;
    private Double maxX;
    private Double minY;
    private Double maxY;

    /**
     * constructor
     *
     * @param key          key
     * @param ndcResultDto ndc dto
     * @param color        color
     */
    public SpcNdChartData(String key, NDCResultDto ndcResultDto, Color color) {
        this.ndcResultDto = ndcResultDto;
        this.key = key;
        this.color = color;
        this.initData();
    }

    private void initData() {
        if (ndcResultDto == null) {
            return;
        }
        //init bar data
        Double[] histX = ndcResultDto.getHistX();
        Double[] histY = ndcResultDto.getHistY();
        barChartData = new SpcBarChartData(histX, histY);

        //init curve data
        Double[] curveX = ndcResultDto.getCurveX();
        Double[] curveY = ChartDataUtils.rebaseNormalCurveData(histY, ndcResultDto.getCurveY());
        xyChartData = new SpcXYChartData(curveX, curveY);

        //init lines data
        String[] uslLslName = UIConstant.SPC_USL_LSL;
        Double usl = ndcResultDto.getUsl();
        Double lsl = ndcResultDto.getLsl();
        Double[] uslAndLsl = new Double[]{usl, lsl};
        if (usl != null) {
            ILineData uslData = new LineData(usl, uslLslName[0]);
            lineDataList.add(uslData);
        }
        if (lsl != null) {
            ILineData lslData = new LineData(lsl, uslLslName[1]);
            lineDataList.add(lslData);
        }
        String[] lineNames = UIConstant.SPC_CHART_LINE_NAME;
        Double[] cls = ndcResultDto.getCls();
        if (cls != null) {
            for (int i = 0; i < cls.length; i++) {
                ILineData lineData = new LineData(cls[i], lineNames[i]);
                lineDataList.add(lineData);
            }
        }

        maxY = MathUtils.getMax(histY, curveY);
        minY = MathUtils.getMin(histY, curveY);
        maxX = MathUtils.getMax(histX, curveX, cls, uslAndLsl);
        minX = MathUtils.getMin(histX, curveX, cls, uslAndLsl);
    }

    @Override
    public IXYChartData getCurveData() {
        return xyChartData;
    }

    @Override
    public IBarChartData getBarData() {
        return barChartData;
    }

    @Override
    public List<ILineData> getLineData() {
        return lineDataList;
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
