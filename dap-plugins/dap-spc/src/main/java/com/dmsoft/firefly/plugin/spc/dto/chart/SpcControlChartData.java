/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.dto.chart;

import com.dmsoft.firefly.plugin.spc.charts.data.XYChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.ILineData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IPathData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IPoint;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IXYChartData;
import com.dmsoft.firefly.plugin.spc.charts.utils.MathUtils;
import com.dmsoft.firefly.plugin.spc.dto.analysis.SpcControlChartDto;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import com.google.common.collect.Lists;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * Created by Ethan.Yang on 2018/3/10.
 */
public class SpcControlChartData implements IControlChartData {
    private SpcControlChartDto spcControlChartDto;
    private XYChartData xyChartData;
    private String key;
    private Color color;
    private List<ILineData> lineDataList = Lists.newArrayList();
    private List<IPathData> breakLineList = Lists.newArrayList();
    private Double minX;
    private Double maxX;
    private Double minY;
    private Double maxY;

    /**
     * constructor
     *
     * @param key                key
     * @param spcControlChartDto control chart dto
     * @param color              color
     */
    public SpcControlChartData(String key, SpcControlChartDto spcControlChartDto, Color color) {
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
        xyChartData = new XYChartData<>(x, y);

        //init lines data
        Double cl = spcControlChartDto.getCl();
        if (cl != null) {
            ILineData uslData = new LineData(cl, UIConstant.SPC_CHART_CL);
            lineDataList.add(uslData);
        }
        String[] uclLclName = UIConstant.SPC_UCL_LCL;
        Double[] ucl = spcControlChartDto.getUcl();
        Double[] lcl = spcControlChartDto.getLcl();
        IPathData uclData = new IPathData() {
            @Override
            public IPoint getPoints() {
                return new SpcPointData(x, ucl);
            }

            @Override
            public String getPathName() {
                return uclLclName[0];
            }

            @Override
            public Color getColor() {
                return color;
            }
        };
        IPathData lclData = new IPathData() {
            @Override
            public IPoint getPoints() {
                return new SpcPointData(x, lcl);
            }

            @Override
            public String getPathName() {
                return uclLclName[1];
            }

            @Override
            public Color getColor() {
                return color;
            }
        };
        breakLineList.add(uclData);
        breakLineList.add(lclData);

        maxY = MathUtils.getMax(y, ucl, lcl, new Double[]{cl});
        minY = MathUtils.getMin(y, ucl, lcl, new Double[]{cl});
        maxX = MathUtils.getMax(x);
        minX = MathUtils.getMax(x);
    }

    @Override
    public IXYChartData getChartData() {
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

    class SpcPointData implements IPoint {
        private Double[] x;
        private Double[] y;

        /**
         * constructor
         *
         * @param x x
         * @param y y
         */
        public SpcPointData(Double[] x, Double[] y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public Object getXByIndex(int index) {
            return x[index];
        }

        @Override
        public Object getYByIndex(int index) {
            return y[index];
        }

        @Override
        public int getLen() {
            return x == null ? 0 : x.length;
        }
    }
}