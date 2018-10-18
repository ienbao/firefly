package com.dmsoft.firefly.plugin.yield.charts;

import com.dmsoft.firefly.plugin.spc.charts.data.basic.IBarChartData;
import com.dmsoft.firefly.plugin.yield.charts.data.BarCategoryData;
import com.dmsoft.firefly.plugin.yield.charts.data.NDBarChartData;
import com.dmsoft.firefly.sdk.utils.ColorUtils;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Maps;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class NDChart<X,Y> extends XYChart<X,Y>{

    private Orientation orientation;
    private ValueAxis valueAxis;
    private static final PseudoClass VERTICAL_PSEUDOCLASS_STATE =
            PseudoClass.getPseudoClass("vertical");
    private Map<String, XYChart.Series> uniqueKeySeriesMap = Maps.newLinkedHashMap();
    private Map<XYChart.Data, BarCategoryData<X, Y>> barCategoryDataMap = Maps.newHashMap();

    /**
     * Constructs a XYChart given the two axes. The initial content for the chart
     * plot background and plot area that includes vertical and horizontal grid
     * lines and fills, are added.
     *
     * @param xAxis X Axis for this XY chart
     * @param yAxis Y Axis for this XY chart
     */
    public NDChart(Axis<X> xAxis, Axis<Y> yAxis) {
        super(xAxis, yAxis);
        this.getStyleClass().add("bar-chart");//添加NDchart的样式
        this.setAnimated(false);//设置是否动画
        this.orientation = Orientation.VERTICAL;//设置方向为垂直
        this.valueAxis = (ValueAxis) yAxis;
        this.setHorizontalGridLinesVisible(false);
        this.setHorizontalZeroLineVisible(false);
        this.setVerticalZeroLineVisible(false);
        this.setVerticalGridLinesVisible(false);
        this.pseudoClassStateChanged(VERTICAL_PSEUDOCLASS_STATE,orientation == Orientation.HORIZONTAL);
        this.pseudoClassStateChanged(VERTICAL_PSEUDOCLASS_STATE,orientation == Orientation.VERTICAL);
        this.setData(FXCollections.observableArrayList());//创建一个列表
        this.setLegendVisible(false);//图示可见
    }

    /**
     * Constructs a XYChart given the two axes. The initial content for the chart
     * plot background and plot area that includes vertical and horizontal grid
     * lines and fills, are added.
     *
     */
    public void setData(List<NDBarChartData> barChartDataList, ChartTooltip chartTooltip){
        this.removeAllChilder();
        if(barChartDataList == null){
            return;
        }
        setAxisRange(barChartDataList);//设置坐标轴的范围
        barChartDataList.forEach(ndBarChartData ->createChartSeriesData(ndBarChartData,chartTooltip) );

    }

    private void createChartSeriesData(NDBarChartData chartData, ChartTooltip chartTooltip) {
        //1、设置barchart的数据源
        if(chartData == null){
            return;
        }
        Color color = chartData.getColor();
        String seriesName = chartData.getSeriesName();
        if(chartData.getBarChartData() != null){
            XYChart.Series series = this.buildSeries(chartData.getBarChartData(), seriesName);
        //2、设置barChart的样式
            this.setSeriesDataStyle(series,color);

        }

        //2、设置barchart的样式
        //3、设置barchart的鼠标悬停
    }

    private void setSeriesDataStyle(XYChart.Series series,Color color) {
        ObservableList<Data<X, Y>> data = series.getData();
            data.forEach(dataItem -> {
                dataItem.getNode().getStyleClass().setAll("chart-bar");
                if (color != null && DAPStringUtils.isNotBlank(ColorUtils.toHexFromFXColor(color))) {
                    StringBuilder nodeStyle = new StringBuilder();
                    nodeStyle.append("-fx-bar-fill: " + ColorUtils.toHexFromFXColor(color));
                    dataItem.getNode().setStyle(nodeStyle.toString());
                }
            });



    }

    private XYChart.Series buildSeries(IBarChartData<X,Y> barChart, String seriesName) {
        ObservableList<XYChart.Data<X,Y>> dataList = FXCollections.observableArrayList();
        int len = barChart.getLen();
        for (int i = 0 ; i < len ; i++){
            X xValue = barChart.getStartValueByIndex(i);
            Y yValue = barChart.getValueByIndex(i);

//            if (xValue == null || yValue == null){
//                continue;
//            }
            XYChart.Data<X,Y> data = new XYChart.Data<>(xValue,yValue);
            data.setExtraValue(barChart.getEndValueByIndex(i));
            dataList.add(data);
            barCategoryDataMap.put(data,
                    new BarCategoryData(xValue, barChart.getBarWidthByIndex(i), yValue));


        }

        return new XYChart.Series<X,Y>(seriesName,dataList);
    }

    private void setAxisRange(List<NDBarChartData> barChartDataList) {

    }


    private void removeAllChilder() {
        ObservableList<Node> nodes = getPlotChildren();//获取节点
        getPlotChildren().remove(nodes);//删除节点
        for (Map.Entry<String, XYChart.Series> stringSeriesEntry : uniqueKeySeriesMap.entrySet()) {
            seriesRemoved(stringSeriesEntry.getValue());
        }

    }

    @Override
    protected void dataItemAdded(Series series, int itemIndex, Data item) {

    }

    @Override
    protected void dataItemRemoved(Data item, Series series) {

    }

    @Override
    protected void dataItemChanged(Data item) {

    }

    @Override
    protected void seriesAdded(Series series, int seriesIndex) {

    }

    @Override
    protected void seriesRemoved(Series series) {

    }

    @Override
    protected void layoutPlotChildren() {

    }
}


