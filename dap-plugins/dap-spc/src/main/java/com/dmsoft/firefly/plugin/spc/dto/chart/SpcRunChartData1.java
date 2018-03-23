package com.dmsoft.firefly.plugin.spc.dto.chart;

import com.dmsoft.firefly.plugin.spc.charts.data.ControlChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.XYChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.*;
import com.dmsoft.firefly.plugin.spc.charts.utils.MathUtils;
import com.dmsoft.firefly.plugin.spc.charts.utils.enums.LineType;
import com.dmsoft.firefly.plugin.spc.dto.RuleResultDto;
import com.dmsoft.firefly.plugin.spc.dto.analysis.RunCResultDto;
import com.dmsoft.firefly.plugin.spc.dto.chart.pel.LineData;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import com.dmsoft.firefly.sdk.utils.ColorUtils;
import com.google.common.collect.Lists;
import javafx.geometry.Orientation;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.function.Function;

/**
 * Created by cherry on 2018/3/20.
 */
public class SpcRunChartData1 implements ControlChartData {

    private XYChartData xyChartData;
    private RunCResultDto runCResultDto;
    private List<ILineData> lineDataList;
    private List<String> analyzedRowKeys;
    private Double minX;
    private Double maxX;
    private Double minY;
    private Double maxY;
    private String key;
    private String seriesName;
    private Color color;

    public SpcRunChartData1(String key, RunCResultDto runCResultDto, List<String> analyzedRowKeys, Color color) {
        this.runCResultDto = runCResultDto;
        this.analyzedRowKeys = analyzedRowKeys;
        this.lineDataList = Lists.newArrayList();
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
        xyChartData.setRuleResultDtoMap(runCResultDto.getRuleResultDtoMap());
        if (analyzedRowKeys != null) {
            xyChartData.setIds(analyzedRowKeys.toArray(new Object[analyzedRowKeys.size()]));
        }
        //init lines data
        Double usl = runCResultDto.getUsl();
        Double lsl = runCResultDto.getLsl();
        Double[] uslAndlsl = new Double[]{usl, lsl};
        String[] lineNames = UIConstant.SPC_CHART_LINE_NAME;
        if (usl != null) {
            ILineData uslData = new LineData(usl, lineNames[1], Orientation.HORIZONTAL);
            lineDataList.add(uslData);
        }
        if (lsl != null) {
            ILineData lslData = new LineData(lsl, lineNames[0], Orientation.HORIZONTAL);
            lineDataList.add(lslData);
        }
        Double[] cls = runCResultDto.getCls();
        if (cls != null) {
            for (int i = 0; i < cls.length; i++) {
                LineType lineType = lineNames[i + 2].equals(lineNames[5]) ? LineType.SOLID : LineType.DASHED;
                ILineData lineData = new LineData(cls[i], lineNames[i + 2], Orientation.HORIZONTAL, lineType);
                lineDataList.add(lineData);
            }
        }
        maxY = MathUtils.getMax(y, cls, uslAndlsl);
        minY = MathUtils.getMin(y, cls, uslAndlsl);
        maxX = MathUtils.getMax(x);
        minX = MathUtils.getMin(x);
    }

    @Override
    public IXYChartData getXyOneChartData() {
        return xyChartData;
    }

    @Override
    public List<ILineData> getLineData() {
        return lineDataList;
    }

    @Override
    public Function<PointRule, PointStyle> getPointFunction() {
        return pointRule -> {
            List<String> activeRuleList = pointRule.getActiveRule();
            Color pointColor = pointRule.getNormalColor();
            Double y = (Double) pointRule.getData().getYValue();
            Double x = (Double) pointRule.getData().getXValue();
            if (activeRuleList != null) {
                for (String rule : activeRuleList) {
                    RuleResultDto ruleResultDto = runCResultDto.getRuleResultDtoMap().get(rule);
                    Double[] abnormalX = ruleResultDto.getX();
                    Double[] abnormalY = ruleResultDto.getY();
                    if (abnormalX == null || abnormalY == null) {
                        continue;
                    }
                    for (int i = 0; i < abnormalY.length; i++) {
                        if (abnormalY[i] == null || abnormalX[i] == null) {
                            continue;
                        }
                        if (abnormalY[i] == y && abnormalX[i] == x) {
                            pointColor = Color.RED;
                        }
                    }
                }
            }
            PointStyle pointStyle = new PointStyle();
            pointStyle.setStyle("-fx-background-color:" + ColorUtils.toHexFromFXColor(pointColor));
            return pointStyle;
        };
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

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

}