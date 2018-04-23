package com.dmsoft.firefly.plugin.spc.dto.chart;

import com.dmsoft.firefly.plugin.spc.charts.data.ControlChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.ILineData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IPathData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IPoint;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IXYChartData;
import com.dmsoft.firefly.plugin.spc.charts.utils.MathUtils;
import com.dmsoft.firefly.plugin.spc.charts.utils.enums.LineType;
import com.dmsoft.firefly.plugin.spc.dto.analysis.SpcControlChartDto;
import com.dmsoft.firefly.plugin.spc.dto.chart.pel.LineData;
import com.dmsoft.firefly.plugin.spc.dto.chart.pel.SpcXYChartData;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import javafx.geometry.Orientation;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * Created by cherry on 2018/4/14.
 */
public class SpcMrChartData implements ControlChartData {

    private SpcControlChartDto spcControlChartDto;
    private SpcXYChartData xyChartData;
    private List<ILineData> lineDataList = Lists.newArrayList();
    private List<IPathData> breakLineList = Lists.newArrayList();
    private String seriesName;
    private String key;
    private Color color;
    private Double minX;
    private Double maxX;
    private Double minY;
    private Double maxY;
    private Double[] uclValue;
    private Double[] lclValue;

    /**
     * Constructor for SpcControlChartData
     *
     * @param key                unique key
     * @param spcControlChartDto control chart data
     * @param color              chart color
     */
    public SpcMrChartData(String key, SpcControlChartDto spcControlChartDto, Color color) {
        this.spcControlChartDto = spcControlChartDto;
        this.key = key;
        this.color = color;
        this.initData();
    }

    private void initData() {
        if (spcControlChartDto == null) {
            return;
        }
        Double[] x = spcControlChartDto.getX();
        Double[] y = spcControlChartDto.getY();
        //init lines data
        Double cl = spcControlChartDto.getCl();
        uclValue = spcControlChartDto.getUcl();
        lclValue = spcControlChartDto.getLcl();
        if (!DAPStringUtils.isInfinityAndNaN(cl)) {
            ILineData uslData = new LineData(cl, UIConstant.SPC_CHART_CL, Orientation.HORIZONTAL);
            lineDataList.add(uslData);
        }
        if (uclValue != null && uclValue.length >= 1 && !DAPStringUtils.isInfinityAndNaN(uclValue[0])) {
            ILineData uclData = new LineData(uclValue[0], UIConstant.SPC_UCL_LCL[0], Orientation.HORIZONTAL, LineType.DASHED);
            lineDataList.add(uclData);
        }
        if (lclValue != null && lclValue.length >= 1 && !DAPStringUtils.isInfinityAndNaN(lclValue[0])) {
            ILineData lclData = new LineData(lclValue[0], UIConstant.SPC_UCL_LCL[1], Orientation.HORIZONTAL, LineType.DASHED);
            lineDataList.add(lclData);
        }
        xyChartData = new SpcXYChartData(x, y);
        Double uclRange = ((uclValue != null && uclValue.length >= 1)) ? uclValue[0] : null;
        Double lclRange = ((lclValue != null && lclValue.length >= 1)) ? lclValue[0] : null;
        maxY = MathUtils.getMax(y, new Double[]{cl, uclRange, lclRange});
        minY = MathUtils.getMin(y, new Double[]{cl, uclRange, lclRange});
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
    public List<IPathData> getBrokenLineData() {
        return breakLineList;
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

    @Override
    public Double[] getUclData() {
        return uclValue;
    }

    @Override
    public Double[] getLclData() {
        return lclValue;
    }

    /**
     * Spc point data class
     */
    class SpcPointData implements IPoint {
        private Double[] x;
        private Double[] y;

        /**
         * constructor
         *
         * @param x x
         * @param y y
         */
        SpcPointData(Double[] x, Double[] y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public Object getXByIndex(int index) {
            return index >= x.length && DAPStringUtils.isInfinityAndNaN(x[index]) ? null : x[index];
        }

        @Override
        public Object getYByIndex(int index) {
            return index >= y.length && DAPStringUtils.isInfinityAndNaN(y[index]) ? null : y[index];
        }

        @Override
        public int getLen() {
            return x == null || y == null ? 0 : (x.length > y.length ? y.length : x.length);
        }
    }
}
