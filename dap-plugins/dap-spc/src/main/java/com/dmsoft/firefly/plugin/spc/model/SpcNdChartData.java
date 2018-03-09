/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.model;

import com.dmsoft.firefly.plugin.spc.charts.data.XYChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IBarChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.ILineData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IXYChartData;
import com.dmsoft.firefly.plugin.spc.dto.analysis.NDCResultDto;
import com.dmsoft.firefly.plugin.spc.dto.chart.INdcChartData;

import java.util.List;

/**
 * Created by Ethan.Yang on 2018/3/9.
 */
public class SpcNdChartData implements INdcChartData {
    private NDCResultDto ndcResultDto;

    /**
     * constructor
     *
     * @param ndcResultDto ndc dto
     */
    public SpcNdChartData(NDCResultDto ndcResultDto) {
        this.ndcResultDto = ndcResultDto;
        this.initData();
    }

    private void initData(){
        XYChartData xyChartData = new XYChartData<>();
    }

    @Override
    public IXYChartData getCurveData() {
        return null;
    }

    @Override
    public IBarChartData getBarData() {
        return null;
    }

    @Override
    public List<ILineData> getLineData() {
        return null;
    }

    @Override
    public Number getXLowerBound() {
        return null;
    }

    @Override
    public Number getXUpperBound() {
        return null;
    }

    @Override
    public Number getYLowerBound() {
        return null;
    }

    @Override
    public Number getYUpperBound() {
        return null;
    }
}
