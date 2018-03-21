package com.dmsoft.firefly.plugin.spc.dto.chart;

import com.dmsoft.firefly.plugin.spc.charts.data.ControlChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.ILineData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IPathData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IPoint;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IXYChartData;
import com.dmsoft.firefly.plugin.spc.charts.utils.MathUtils;
import com.dmsoft.firefly.plugin.spc.dto.analysis.SpcControlChartDto;
import com.dmsoft.firefly.plugin.spc.dto.chart.pel.LineData;
import com.dmsoft.firefly.plugin.spc.dto.chart.pel.SpcXYChartData;
import com.dmsoft.firefly.plugin.spc.utils.ChartDataUtils;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import com.dmsoft.firefly.plugin.spc.utils.XYData;
import com.google.common.collect.Lists;
import javafx.geometry.Orientation;
import javafx.scene.paint.Color;
import java.util.List;

/**
 * Created by cherry on 2018/3/21.
 */
public class SpcControlChartData1 implements ControlChartData {

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

    public SpcControlChartData1(String key, SpcControlChartDto spcControlChartDto, Color color) {
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
        if (cl != null) {
            ILineData uslData = new LineData(cl, UIConstant.SPC_CHART_CL, Orientation.HORIZONTAL);
            lineDataList.add(uslData);
        }
        String[] uclLclName = UIConstant.SPC_UCL_LCL;
        uclValue = spcControlChartDto.getUcl();
        lclValue = spcControlChartDto.getLcl();
        XYData ucl = ChartDataUtils.foldCLData(uclValue);
        XYData lcl = ChartDataUtils.foldCLData(lclValue);
        xyChartData = new SpcXYChartData(x, y);
        IPathData uclData = new IPathData() {
            @Override
            public IPoint getPoints() {
                return new SpcPointData(x, spcControlChartDto.getUcl());
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
                return new SpcPointData(x, spcControlChartDto.getLcl());
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
        maxY = MathUtils.getMax(y, ucl.getY(), lcl.getY(), new Double[]{cl});
        minY = MathUtils.getMin(y, ucl.getY(), lcl.getY(), new Double[]{cl});
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
