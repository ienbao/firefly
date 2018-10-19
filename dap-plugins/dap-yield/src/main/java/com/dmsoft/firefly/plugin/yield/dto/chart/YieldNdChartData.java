package com.dmsoft.firefly.plugin.yield.dto.chart;

import com.dmsoft.firefly.plugin.yield.charts.data.NDBarChartData;
import com.dmsoft.firefly.plugin.yield.charts.data.basic.IBarChartData;
import com.dmsoft.firefly.plugin.yield.charts.data.basic.IXYChartData;
import com.dmsoft.firefly.plugin.yield.dto.NDCResultDto;
import com.dmsoft.firefly.plugin.yield.dto.chart.pel.YieldBarChartData;
import com.dmsoft.firefly.plugin.yield.dto.chart.pel.YieldXYChartData;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * Created by Tommy on 2018/10/18.
 */
public  class YieldNdChartData implements NDBarChartData {
    private NDCResultDto ndcResultDto;
    private YieldXYChartData xyChartData;
    private YieldBarChartData barChartData ;

    private String seriesName;
    private String key;
    private Color color;



    /**
     * Constructor for SpcNdChartData
     *
     * @param key          unique key
     * @param ndcResultDto spc ndc result data
     * @param color        ndc chart color
     */
    public YieldNdChartData(String key, NDCResultDto ndcResultDto, Color color) {
        this.ndcResultDto = ndcResultDto;//包含x，y轴的值
        this.key = key;
        this.color = color;
        this.initData();
    }

    private void initData() {

        //init bar data
        Double[] histX = ndcResultDto.getHistX();
        Double[] histY = ndcResultDto.getHistY();
        barChartData = new YieldBarChartData(histX, histY);

    }



    @Override
    public IBarChartData getBarChartData() {
        return barChartData;
    }

    @Override
    public IXYChartData getXYChartData() {
        return xyChartData;
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
    public String getSeriesName() {
        return seriesName;
    }



    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }
}
