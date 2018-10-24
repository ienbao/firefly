package com.dmsoft.firefly.plugin.yield.controller;

import com.dmsoft.firefly.gui.components.chart.ChartOperatorUtils;
import com.dmsoft.firefly.plugin.yield.charts.ChartTooltip;
import com.dmsoft.firefly.plugin.yield.charts.NDChart;
import com.dmsoft.firefly.plugin.yield.charts.data.NDBarChartData;
import com.dmsoft.firefly.plugin.yield.dto.chart.YieldNdChartData;
import com.dmsoft.firefly.plugin.yield.dto.YieldDetailChartDto;
import com.dmsoft.firefly.plugin.yield.dto.YieldChartResultDto;
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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class YieldChartResultController implements Initializable {

    @FXML
    private BarChart yieldBarChart;

    private String[] yieldBarChartCategory;
    private String[] yieldBarChartLabel;

    private Logger logger = LoggerFactory.getLogger(YieldChartResultController.class);



    public void init(YieldMainController yieldMainController) {



    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.initI18n();

    }

    private void initI18n() {
        yieldBarChartLabel = new String[]{
                YieldFxmlAndLanguageUtils.getString(UIConstant.BARCHART_FPY),
                YieldFxmlAndLanguageUtils.getString(UIConstant.BARCHART_NTF),
                YieldFxmlAndLanguageUtils.getString((UIConstant.BARCHART_NG))
        };

    }

    public void analyzeYieldResult(YieldDetailChartDto yieldDetailChartDto) {
        //清除分析之前的数据

        if (yieldDetailChartDto == null){
            return ;
        }

        this.setAsetAnalysisBarChartResultData(yieldDetailChartDto);

    }

    private void setAsetAnalysisBarChartResultData(YieldDetailChartDto yieldDetailChartDto){
        if (yieldDetailChartDto == null){
            enableSubResultOperator(false);
            RuntimeContext.getBean(IMessageManager.class).showWarnMsg(
                    YieldFxmlAndLanguageUtils.getString(UIConstant.UI_MESSAGE_TIP_WARNING_TITLE),
                    YieldFxmlAndLanguageUtils.getString("EXCEPTION_GRR_NO_ANALYSIS_RESULT"));

            return;
        }

        setBarChart(yieldDetailChartDto.getYieldChartResultDto());
    }

    private void enableSubResultOperator(boolean b) {

            //grrChartBtn.setDisable(!flag);

    }

    private void setBarChart(YieldChartResultDto yieldChartResult) {
        if(yieldChartResult == null){//判断yiyieldChartResult是否为空
            return;
        }
        Double[] yieldChartArray = getYieldChartArrayValue(yieldChartResult);
        Double yMax = MathUtils.getNaNToZoreMax(yieldChartArray);
        Double yMin = MathUtils.getNaNToZoreMin(yieldChartArray);
        if(yMax == null || yMin == null){
            return;

        }
        NumberAxis yAxis = (NumberAxis) yieldBarChart.getYAxis();//设置y轴的值
        final double factor = 0.2;
        double reserve = (yMax - yMin) * factor;
        yAxis.setAutoRanging(false);
        yMax += reserve;
        Map<String, Object> yAxisRangeData = ChartOperatorUtils.getAdjustAxisRangeData(yMax, yMin, 5);
        double newYMin = (Double) yAxisRangeData.get(ChartOperatorUtils.KEY_MIN);
        double newYMax = (Double) yAxisRangeData.get(ChartOperatorUtils.KEY_MAX);
        yAxis.setLowerBound((newYMin < 0 && yMin >= 0) ? 0 : newYMin);
        yAxis.setUpperBound(newYMax);
        ChartOperatorUtils.updateAxisTickUnit(yAxis);
        XYChart.Series series1 = new XYChart.Series();
        series1.getData().add(new XYChart.Data(yieldBarChartLabel[0], DAPStringUtils.isInfinityAndNaN(yieldChartResult.getFPY()) ? 0 : yieldChartResult.getFPY()));
        series1.getData().add(new XYChart.Data(yieldBarChartLabel[1],DAPStringUtils.isInfinityAndNaN(yieldChartResult.getNTF()) ?0 : yieldChartResult.getNTF()));
        series1.getData().add(new XYChart.Data(yieldBarChartLabel[2],DAPStringUtils.isInfinityAndNaN(yieldChartResult.getNG()) ? 0 : yieldChartResult.getNG()));
        yieldBarChart.getData().addAll(series1);//barChart中添加元素
        for (int i = 0 ; i < yieldBarChartCategory.length ;i++){
            XYChart.Series series = (XYChart.Series) yieldBarChart.getData().get(i);
            series.setName("%"+yieldBarChartCategory[i]);
        }
        int digNum = DigNumInstance.newInstance().getDigNum() - 2 >= 0 ? DigNumInstance.newInstance().getDigNum() - 2 : 0;
        ChartUtils.setChartText(yieldBarChart.getData(), s -> {//设置Chart顶部的数据百分比
            if (DAPStringUtils.isNumeric(s)) {
                Double value = Double.valueOf(s);
                if (!DAPStringUtils.isInfinityAndNaN(value)) {
                    return DAPStringUtils.formatDouble(value, 2) + "%";
                }
            }
            return s + "%";
        });


    }

    private Double[] getYieldChartArrayValue(YieldChartResultDto yieldChartResult) {

        Double[] value = new Double[3];
        value[0] = yieldChartResult.getFPY();
        value[1] = yieldChartResult.getNTF();
        value[3] = yieldChartResult.getNG();

        return value;


    }



}
