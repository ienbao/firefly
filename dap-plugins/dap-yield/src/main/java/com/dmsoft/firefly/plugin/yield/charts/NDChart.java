package com.dmsoft.firefly.plugin.yield.charts;

import com.dmsoft.firefly.plugin.yield.charts.data.BarCategoryData;
import com.dmsoft.firefly.plugin.yield.charts.data.NDBarChartData;
import com.dmsoft.firefly.plugin.yield.charts.data.basic.BarToolTip;
import com.dmsoft.firefly.plugin.yield.charts.data.basic.IBarChartData;
import com.dmsoft.firefly.plugin.yield.dto.YieldChartResultDto;
import com.dmsoft.firefly.plugin.yield.dto.YieldDetailChartDto;
import com.dmsoft.firefly.sdk.utils.ColorUtils;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Maps;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class NDChart<X,Y> extends XYChart<X,Y>{

    private Orientation orientation;
    private ValueAxis valueAxis;
    private static final PseudoClass VERTICAL_PSEUDOCLASS_STATE =
            PseudoClass.getPseudoClass("vertical");
    private Map<String, XYChart.Series> uniqueKeySeriesMap = Maps.newLinkedHashMap();
    private Map<XYChart.Series, String> seriesUniqueKeyMap = Maps.newLinkedHashMap();

    private Map<XYChart.Data, BarCategoryData<X, Y>> barCategoryDataMap = Maps.newHashMap();
    private Boolean showToltip = true;
    private final double ANCHOR_X = 10.0; //固定x
    private final double ANCHOR_Y = 15.0;//固定y
    private YieldChartResultDto yieldChartResultDto;
    private ChartTooltip chartTooltip;


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
        this.setData(yieldChartResultDto, chartTooltip);
    }

    /**
     * Constructs a XYChart given the two axes. The initial content for the chart
     * plot background and plot area that includes vertical and horizontal grid
     * lines and fills, are added.
     *
     */
    public BarChart setData(YieldChartResultDto yieldChartResultDto , ChartTooltip chartTooltip){
        this.removeAllChilder();

        BarChart barChart = new BarChart(new CategoryAxis(),new NumberAxis());
        barChart.setAnimated(false);
        barChart.setLegendVisible(false);
        //setAxisRange(barChartDataList);//设置坐标轴的范围
       XYChart.Series series = setComponentBarChart();
        barChart.getData().addAll(series);
        return barChart;


    }

    private XYChart.Series setComponentBarChart( ) {
        XYChart.Series series1 = new XYChart.Series();
        series1.getData().add(new XYChart.Data("123",12));
        series1.getData().add(new XYChart.Data("456",23));
        series1.getData().add(new XYChart.Data("789",10));
       return  series1;

    }
    private void createChartSeriesData(NDBarChartData chartData, ChartTooltip chartTooltip) {
        //1、设置barchart的数据源
        if(chartData == null){
            return;
        }
        Color color = chartData.getColor();
        String uniqueKey = chartData.getUniqueKey();
        String seriesName = chartData.getSeriesName();
        if(chartData.getBarChartData() != null){
            XYChart.Series series = this.buildSeries(chartData.getBarChartData(), seriesName);
            this.uniqueKeySeriesMap.put(uniqueKey,series);
        //2、设置barChart的样式
            this.getData().add(series);
            this.setSeriesDataStyle(series,color);
            this.setSeriesDataToolTip (series,chartTooltip == null ? null : chartTooltip.getChartBarToolTip());
        }
        //3、设置barchart的鼠标悬停


    }

    private void setSeriesDataToolTip(Series<X, Y> series,
                                      Function<BarToolTip, String> barToolTipStringFunction) {//提示信息
        if (!showToltip) {
            return;
        }
        Tooltip tooltipMsg = new Tooltip();
        int size = series.getData().size();
        for (int i = 0; i < size; i++) {
            Data<X, Y> dataItem = series.getData().get(i);
            if (barToolTipStringFunction != null) {
                boolean lastData = i == size - 1 ? true : false;
                String content = barToolTipStringFunction.apply(new BarToolTip(
                        series.getName(),
                        dataItem.getXValue(),
                        dataItem.getExtraValue(),
                        dataItem.getYValue(),
                        lastData));

                final Node dataNode = dataItem.getNode();
                dataNode.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                    tooltipMsg.setText(content);
                    tooltipMsg.show(dataNode, event.getScreenX() + ANCHOR_X, event.getScreenY() + ANCHOR_Y);
                        }
                );
                dataNode.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
                            if (tooltipMsg.isShowing()) {
                                tooltipMsg.setAnchorX(event.getScreenX() + ANCHOR_X);
                                tooltipMsg.setAnchorY(event.getScreenY() + ANCHOR_Y);
                            }
                        }
                );
                dataNode.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
                    if (tooltipMsg.isShowing()) {
                        tooltipMsg.hide();
                    }
                });
            }
        }
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


    public void removeAllChilder() {
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


