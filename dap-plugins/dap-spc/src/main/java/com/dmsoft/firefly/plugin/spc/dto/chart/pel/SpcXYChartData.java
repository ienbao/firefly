/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.dto.chart.pel;

import com.dmsoft.firefly.plugin.spc.charts.data.basic.IXYChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.PointRule;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.PointStyle;
import com.dmsoft.firefly.plugin.spc.dto.RuleResultDto;
import com.dmsoft.firefly.sdk.utils.ColorUtils;
import com.dmsoft.firefly.sdk.utils.DAPDoubleUtils;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Ethan.Yang on 2018/3/20.
 */
public class SpcXYChartData implements IXYChartData<Double, Double> {
    private Double[] x = null;
    private Double[] y = null;
    private Object[] ids = null;

    //    Series index
    private int index;

    //    Series color
    private Color color;

    //    Series name
    private String seriesName;
    private Map<String, RuleResultDto> ruleResultDtoMap;

    public SpcXYChartData(Double[] x, Double[] y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int getLen() {
        return (x == null || y == null) ? 0 : (x.length < y.length) ? x.length : y.length;
    }

    @Override
    public Double getXValueByIndex(int index) {
        return (index >= 0 && index < getLen()) ? (DAPDoubleUtils.isBlank(x[index]) ? null : x[index]) : null;
    }

    @Override
    public Double getYValueByIndex(int index) {
        return (index >= 0 && index < getLen()) ? (DAPDoubleUtils.isBlank(x[index]) ? null : y[index]) : null;
    }

    @Override
    public Object getExtraValueByIndex(int index) {
        return (index >= 0 && index < getLen()) && (ids != null) && (ids.length > index) ? ids[index] : null;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public String getSeriesName() {
        return seriesName;
    }

    public void setX(Double[] x) {
        this.x = x;
    }

    public void setY(Double[] y) {
        this.y = y;
    }

    public void setIds(Object[] ids) {
        this.ids = ids;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public int getIndex() {
        return index;
    }

    public Function<PointRule, PointStyle> getPointFunction() {
        if (ruleResultDtoMap == null) {
            return null;
        }
        return new Function<PointRule, PointStyle>() {
            @Override
            public PointStyle apply(PointRule pointRule) {
                List<String> activeRuleList = pointRule.getActiveRule();
                Color pointColor = pointRule.getNormalColor();
                Double y = (Double) pointRule.getData().getYValue();
                Double x = (Double) pointRule.getData().getXValue();

                if (activeRuleList != null) {
                    for (String rule : activeRuleList) {
                        RuleResultDto ruleResultDto = ruleResultDtoMap.get(rule);
                        Double[] abnormalX = ruleResultDto.getX();
                        Double[] abnormalY = ruleResultDto.getY();
                        for (int i = 0; i < abnormalY.length; i++) {
                            if (abnormalY[i] == y && abnormalX[i] == x) {
                                pointColor = Color.RED;
                            }
                        }
                    }
                }
                PointStyle pointStyle = new PointStyle();
                pointStyle.setStyle("-fx-background-color: " + ColorUtils.toHexFromFXColor(pointColor));
                return pointStyle;
            }
        };
    }

    public void setRuleResultDtoMap(Map<String, RuleResultDto> ruleResultDtoMap) {
        this.ruleResultDtoMap = ruleResultDtoMap;
    }
}
