package com.dmsoft.firefly.plugin.spc.charts.data;

import com.dmsoft.firefly.plugin.spc.charts.data.basic.IXYChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.PointRule;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.PointStyle;
import com.dmsoft.firefly.plugin.spc.dto.RuleResultDto;
import com.dmsoft.firefly.sdk.utils.ColorUtils;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by cherry on 2018/2/27.
 */
public class XYChartData<X, Y> implements IXYChartData {

    private X[] x = null;
    private Y[] y = null;
    private Object[] ids = null;

    //    Series index
    private int index;

    //    Series color
    private Color color;

    //    Series name
    private String seriesName;

    private Map<String, RuleResultDto> ruleResultDtoMap;

    public XYChartData() {
    }

    public XYChartData(X[] x, Y[] y) {
        this.x = x;
        this.y = y;
    }

    public XYChartData(X[] x, Y[] y, Object[] ids) {
        this.x = x;
        this.y = y;
        this.ids = ids;
    }

    @Override
    public int getLen() {

        return (x == null || y == null) ? 0 : (x.length < y.length) ? x.length : y.length;
    }

    @Override
    public Object getXValueByIndex(int index) {

        return (index >= 0 && index < getLen()) ? x[index] : null;
    }

    @Override
    public Object getYValueByIndex(int index) {

        return (index >= 0 && index < getLen()) ? y[index] : null;
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

    public void setX(X[] x) {
        this.x = x;
    }

    public void setY(Y[] y) {
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
