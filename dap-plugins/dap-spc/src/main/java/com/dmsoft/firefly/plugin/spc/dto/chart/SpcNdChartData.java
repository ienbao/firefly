package com.dmsoft.firefly.plugin.spc.dto.chart;

import com.dmsoft.firefly.plugin.spc.charts.data.NDBarChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IBarChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.ILineData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IXYChartData;
import com.dmsoft.firefly.plugin.spc.charts.utils.MathUtils;
import com.dmsoft.firefly.plugin.spc.charts.utils.enums.LineType;
import com.dmsoft.firefly.plugin.spc.dto.analysis.NDCResultDto;
import com.dmsoft.firefly.plugin.spc.dto.chart.pel.LineData;
import com.dmsoft.firefly.plugin.spc.dto.chart.pel.SpcBarChartData;
import com.dmsoft.firefly.plugin.spc.dto.chart.pel.SpcXYChartData;
import com.dmsoft.firefly.plugin.spc.utils.ChartDataUtils;
import com.dmsoft.firefly.plugin.spc.utils.SpcFxmlAndLanguageUtils;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * Created by cherry on 2018/3/22.
 */
public class SpcNdChartData implements NDBarChartData {

    private NDCResultDto ndcResultDto;
    private SpcXYChartData xyChartData;
    private SpcBarChartData barChartData;
    private List<ILineData> lineDataList;

    private String seriesName;
    private String key;
    private Color color;

    private Double minX;
    private Double maxX;
    private Double minY;
    private Double maxY;

    /**
     * Constructor for SpcNdChartData
     *
     * @param key          unique key
     * @param ndcResultDto spc ndc result data
     * @param color        ndc chart color
     */
    public SpcNdChartData(String key, NDCResultDto ndcResultDto, Color color) {
        this.key = key;
        this.color = color;
        this.ndcResultDto = ndcResultDto;
        this.lineDataList = Lists.newArrayList();
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
        Double usl = ndcResultDto.getUsl();
        Double lsl = ndcResultDto.getLsl();
        Double[] uslAndLsl = new Double[]{usl, lsl};
        String[] lineNames = new String[]{
                SpcFxmlAndLanguageUtils.getString(UIConstant.SPC_CHART_LINE_NAME_USL),
                SpcFxmlAndLanguageUtils.getString(UIConstant.SPC_CHART_LINE_NAME_LSL),
                SpcFxmlAndLanguageUtils.getString(UIConstant.SPC_CHART_LINE_NAME_LCL),
                SpcFxmlAndLanguageUtils.getString(UIConstant.SPC_CHART_LINE_NAME_NEGATIVE_2_SIGMA),
                SpcFxmlAndLanguageUtils.getString(UIConstant.SPC_CHART_LINE_NAME_NEGATIVE_SIGMA),
                SpcFxmlAndLanguageUtils.getString(UIConstant.SPC_CHART_LINE_NAME_AVERAGE),
                SpcFxmlAndLanguageUtils.getString(UIConstant.SPC_CHART_LINE_NAME_SIGMA),
                SpcFxmlAndLanguageUtils.getString(UIConstant.SPC_CHART_LINE_NAME_2_SIGMA),
                SpcFxmlAndLanguageUtils.getString(UIConstant.SPC_CHART_LINE_NAME_UCL)};
        Double[] cls = ndcResultDto.getCls();
        for (int i = 0; i < lineNames.length; i++) {
            if (lineNames[i].equalsIgnoreCase(lineNames[0])) {
                if (!DAPStringUtils.isInfinityAndNaN(usl)) {
                    ILineData lineData = new LineData(usl, lineNames[i], LineType.DASHED);
                    lineDataList.add(lineData);
                }
                continue;
            }
            if (lineNames[i].equalsIgnoreCase(lineNames[1])) {
                if (!DAPStringUtils.isInfinityAndNaN(lsl)) {
                    ILineData lineData = new LineData(lsl, lineNames[i], LineType.DASHED);
                    lineDataList.add(lineData);
                }
                continue;
            }
            if (!DAPStringUtils.isInfinityAndNaN(cls[i - 2])) {
//                LineType lineType = lineNames[i].equalsIgnoreCase(UIConstant.SPC_CHART_LINE_NAME[5]) ? LineType.SOLID : LineType.DASHED;
                LineData lineData = new LineData(cls[i - 2], lineNames[i]);
                lineDataList.add(lineData);
            }
        }
        maxY = MathUtils.getMax(histY, curveY);
        minY = MathUtils.getMin(histY, curveY);
        maxX = MathUtils.getMax(histX, curveX, cls, uslAndLsl);
        minX = MathUtils.getMin(histX, curveX, cls, uslAndLsl);
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
