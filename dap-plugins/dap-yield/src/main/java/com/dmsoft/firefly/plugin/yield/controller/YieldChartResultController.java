package com.dmsoft.firefly.plugin.yield.controller;

import com.dmsoft.firefly.gui.components.chart.ChartOperatorUtils;
import com.dmsoft.firefly.plugin.yield.charts.ChartTooltip;
import com.dmsoft.firefly.plugin.yield.charts.NDChart;
import com.dmsoft.firefly.plugin.yield.charts.data.NDBarChartData;
import com.dmsoft.firefly.plugin.yield.dto.*;
import com.dmsoft.firefly.plugin.yield.dto.chart.YieldNdChartData;
import com.dmsoft.firefly.plugin.yield.dto.chart.view.ChartPanel;
import com.dmsoft.firefly.plugin.yield.utils.*;
import com.dmsoft.firefly.plugin.yield.utils.charts.ChartUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.message.IMessageManager;
import com.dmsoft.firefly.sdk.utils.ColorUtils;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.util.internal.MathUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class YieldChartResultController implements Initializable {
    private YieldMainController yieldMainController;
    @FXML
    private YieldResultDataController yieldResultDataController;
    @FXML
    private BarChart yieldBarChart;
    @FXML
    private BarChart yieldbarChartItem;
    @FXML
    private GridPane YieldGridPane;
    @FXML
    private ComboBox resultNTFNum;

    private String[] yieldBarChartCategory;
    private String[] yieldBarChartLabel;
    private Logger logger = LoggerFactory.getLogger(YieldChartResultController.class);

    public void init(YieldMainController yieldMainController) {
    this.yieldMainController = yieldMainController;
        this.initComponentEvents();
        this.removeBarChartAllResultData();
        this.removeBarChartResultItemAllResultData();
        yieldBarChart.setAnimated(false);
        yieldbarChartItem.setAnimated(false);



    }

    private void initComponentEvents() {
       // event -> fireResultBasedCmbChangeEvent();

    }

    private Object fireResultBasedCmbChangeEvent() {
        return null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.initI18n();
        this.yieldResultDataController.init(this);
    }

    private void initI18n() {
        yieldBarChartLabel = new String[]{
                YieldFxmlAndLanguageUtils.getString(UIConstant.BARCHART_FPY),
                YieldFxmlAndLanguageUtils.getString(UIConstant.BARCHART_NTF),
                YieldFxmlAndLanguageUtils.getString((UIConstant.BARCHART_NG))
        };
    }
    public void analyzeYieldResult(YieldChartResultAlermDto yieldChartResultAlermDto) {
        //清除分析之前的数据
        this.removeBarChartAllResultData();
        while (yieldChartResultAlermDto == null){
            continue;
        }
       this.setAnalysisBarChartResultData(yieldChartResultAlermDto);
    }

    private void removeBarChartAllResultData() {
        yieldBarChart.getData().setAll(FXCollections.observableArrayList());
    }


    public void ananlyzeyieldResultItem(YieldResultDto yieldResultDto){
        //清除之前的数据
        this.removeBarChartResultItemAllResultData();
        if(yieldResultDto == null){
            return;
        }

        this.setAnalysisBarChartResultItemData(yieldResultDto);

    }
    private void removeBarChartResultItemAllResultData() {

        yieldbarChartItem.getData().setAll(FXCollections.observableArrayList());
    }
    private void setAnalysisBarChartResultItemData(YieldResultDto yieldResultDto) {
        if (yieldResultDto == null){
            enableSubResultOperator(false);
            RuntimeContext.getBean(IMessageManager.class).showWarnMsg(
                    YieldFxmlAndLanguageUtils.getString(UIConstant.UI_MESSAGE_TIP_WARNING_TITLE),
                    YieldFxmlAndLanguageUtils.getString("EXCEPTION_GRR_NO_ANALYSIS_RESULT"));
            return;
        }
        setBarChartItem(yieldResultDto.getYieldNTFChartDtos());

    }



    private void setAnalysisBarChartResultData(YieldChartResultAlermDto yieldChartResultAlermDto){
        if (yieldChartResultAlermDto == null){
            enableSubResultOperator(false);
            RuntimeContext.getBean(IMessageManager.class).showWarnMsg(
                    YieldFxmlAndLanguageUtils.getString(UIConstant.UI_MESSAGE_TIP_WARNING_TITLE),
                    YieldFxmlAndLanguageUtils.getString("EXCEPTION_GRR_NO_ANALYSIS_RESULT"));
            return;
        }
        setBarChart(yieldChartResultAlermDto);
    }

    private void enableSubResultOperator(boolean b) {
            //grrChartBtn.setDisable(!flag);
    }



    //移除统计过后的数据
    private void setBarChart(YieldChartResultAlermDto yieldChartResultAlermDto) {
        if(yieldChartResultAlermDto == null){//判断yiyieldChartResult是否为空
            return;
        }
        Double[] yieldChartArray = getYieldChartArrayValue(yieldChartResultAlermDto);
        Double yMax = MathUtils.getNaNToZoreMax(yieldChartArray);
        Double yMin = MathUtils.getNaNToZoreMin(yieldChartArray);
        if(yMax == null || yMin == null){
            return;
        }
        NumberAxis yAxis = (NumberAxis) yieldBarChart.getYAxis();//设置y轴的值
        final double factor = 0.2;
        double reserve = (yMax - yMin) * factor;
        yAxis.setAutoRanging(false);
        yAxis.setTickMarkVisible(false);
        yAxis.setTickLabelsVisible(false);
        yMax += reserve;
        Map<String, Object> yAxisRangeData = ChartOperatorUtils.getAdjustAxisRangeData(yMax, yMin, 5);
        double newYMin = (Double) yAxisRangeData.get(ChartOperatorUtils.KEY_MIN);
        double newYMax = (Double) yAxisRangeData.get(ChartOperatorUtils.KEY_MAX);
        yAxis.setLowerBound((newYMin < 0 && yMin >= 0) ? 0 : newYMin);
        yAxis.setUpperBound(newYMax);
        ChartOperatorUtils.updateAxisTickUnit(yAxis);
        XYChart.Series series1 = new XYChart.Series();
            series1.getData().add(new XYChart.Data(yieldBarChartLabel[0],(yieldChartResultAlermDto.getFpyPercent() == null ? 0 : DAPStringUtils.isInfinityAndNaN(yieldChartResultAlermDto.getFpyPercent()) ? 0 : yieldChartResultAlermDto.getFpyPercent())));
            series1.getData().add(new XYChart.Data(yieldBarChartLabel[1], (yieldChartResultAlermDto.getNtfPercent()== null ? 0 : DAPStringUtils.isInfinityAndNaN(yieldChartResultAlermDto.getNtfPercent()) ? 0 : yieldChartResultAlermDto.getNtfPercent())));
            series1.getData().add(new XYChart.Data(yieldBarChartLabel[2], (yieldChartResultAlermDto.getNgPercent() == null ? 0 : DAPStringUtils.isInfinityAndNaN(yieldChartResultAlermDto.getNgPercent() ) ? 0 : yieldChartResultAlermDto.getNgPercent())));
            yieldBarChart.getData().addAll(series1);//barChart中添加元素

//        for (int i = 0 ; i < yieldBarChartCategory.length ;i++){
//            XYChart.Series series = (XYChart.Series) yieldBarChart.getData().get(i);
//            series.setName("%"+yieldBarChartCategory[i]);
//        }
        int digNum = DigNumInstance.newInstance().getDigNum() - 2 >= 0 ? DigNumInstance.newInstance().getDigNum() - 2 : 0;
        //YieldChartResultAlermDto yieldChartResultAlermDto = new YieldChartResultAlermDto();
        ChartUtils.setChartTextAndColor(yieldBarChart.getData(), s -> {//设置Chart顶部的数据百分比
            if (DAPStringUtils.isNumeric(s)) {
                Double value = Double.valueOf(s)*100;
                if (!DAPStringUtils.isInfinityAndNaN(value)) {
                    //Integer oo = Integer.parseInt(DAPStringUtils.formatDouble(value, 2))*100;
                    return DAPStringUtils.formatDouble(value, 2)+ "%";
                }
            }
            return s + "%";
        }, yieldChartResultAlermDto.getYieldChartResultAlermDtoMap());


    }

    private Double[] getYieldChartArrayValue(YieldChartResultAlermDto yieldChartResultAlermDto) {

        Double[] value = new Double[3];
        value[0] = yieldChartResultAlermDto.getFpyPercent();
        value[1] = yieldChartResultAlermDto.getNtfPercent();
        value[2] = yieldChartResultAlermDto.getNgPercent();

        return value;


    }
    private void setBarChartItem(List<YieldNTFChartDto> yieldNTFChartDtos) {
        if (yieldNTFChartDtos.size() == 0 ) {
            return ;
        }
        resultNTFNum.getItems().addAll(
                YieldFxmlAndLanguageUtils.getString(UIConstant.Number_1),
                YieldFxmlAndLanguageUtils.getString(UIConstant.Number_2),
                YieldFxmlAndLanguageUtils.getString(UIConstant.Number_3),
                YieldFxmlAndLanguageUtils.getString(UIConstant.Number_4),
                YieldFxmlAndLanguageUtils.getString(UIConstant.Number_5)
                );
        resultNTFNum.setValue(YieldFxmlAndLanguageUtils.getString(UIConstant.Number_5));
        Double[]  yChartArrayData = null;
        for (int i = 0 ; i < yieldNTFChartDtos.size() ; i++){
              yChartArrayData= new Double[yieldNTFChartDtos.size()];
            yChartArrayData[i] = yieldNTFChartDtos.get(i).getNtfPercent();
        }
        Double yMax = MathUtils.getNaNToZoreMax(yChartArrayData);
        Double yMin = MathUtils.getNaNToZoreMin(yChartArrayData);
        if (yMax == null || yMin == null){
            return;
        }
        //设置y轴
        NumberAxis yAxis = (NumberAxis) yieldbarChartItem.getYAxis();
        final double factor = 0.2;
        double reserve = (yMax - yMin) * factor;
        yAxis.setAutoRanging(false);
        yAxis.setTickMarkVisible(false);
        yAxis.setTickLabelsVisible(false);
        yMax += reserve;
        Map<String, Object> yAxisRangeData = ChartOperatorUtils.getAdjustAxisRangeData(yMax, yMin, 5);
        double newYMin = (Double) yAxisRangeData.get(ChartOperatorUtils.KEY_MIN);
        double newYMax = (Double) yAxisRangeData.get(ChartOperatorUtils.KEY_MAX);
        yAxis.setLowerBound((newYMin < 0 && yMin >= 0) ? 0 : newYMin);
        yAxis.setUpperBound(newYMax);
        ChartOperatorUtils.updateAxisTickUnit(yAxis);
        yAxis.setAutoRanging(false);
        XYChart.Series series2 =new XYChart.Series();
        for (int i = 0 ; i < yieldNTFChartDtos.size() ; i++){
            series2.getData().add(new XYChart.Data(yieldNTFChartDtos.get(i).getItemName(),(yieldNTFChartDtos.get(i).getNtfPercent()==null ? 0 :DAPStringUtils.isInfinityAndNaN(yieldNTFChartDtos.get(i).getNtfPercent()) ? 0 : yieldNTFChartDtos.get(i).getNtfPercent())));
            if(yieldNTFChartDtos.get(i).getNtfPercent() == null) ;
        }
        yieldbarChartItem.getData().addAll(series2);
        //int digNum = DigNumInstance.newInstance().getDigNum() - 2 >= 0 ? DigNumInstance.newInstance().getDigNum() - 2 : 0;
        ChartUtils.setChartText(yieldbarChartItem.getData(), s -> {//设置Chart顶部的数据百分比
            if (DAPStringUtils.isNumeric(s)) {
                Double value = Double.valueOf(s)*100;
                if (!DAPStringUtils.isInfinityAndNaN(value)) {
                    return DAPStringUtils.formatDouble(value, 0) + "%";
                }
            }
            return s + "%";
        });

    }


    public YieldMainController getYieldMainController() {

        return yieldMainController;
    }

    public void setYieldMainController(YieldMainController yieldMainController) {
        this.yieldMainController = yieldMainController;
    }

    public YieldResultDataController getYieldResultDataController() {
        return yieldResultDataController;
    }

    public void setYieldResultDataController(YieldResultDataController yieldResultDataController) {
        this.yieldResultDataController = yieldResultDataController;
    }
}
