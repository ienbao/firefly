package com.dmsoft.firefly.plugin.yield.controller;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.gui.components.chart.ChartOperatorUtils;
import com.dmsoft.firefly.plugin.yield.dto.*;
import com.dmsoft.firefly.plugin.yield.utils.*;
import com.dmsoft.firefly.plugin.yield.utils.charts.ChartUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.UserPreferenceDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.UserPreferenceService;
import com.dmsoft.firefly.sdk.message.IMessageManager;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import org.apache.commons.lang3.StringUtils;
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
    private EnvService envService = RuntimeContext.getBean(EnvService.class);
    private UserPreferenceService userPreferenceService = RuntimeContext.getBean(UserPreferenceService.class);
    private JsonMapper mapper = JsonMapper.defaultMapper();
    private String[] yieldBarChartCategory;
    private String[] yieldBarChartLabel;
    private Logger logger = LoggerFactory.getLogger(YieldChartResultController.class);
    private List<YieldNTFChartDto> yieldNTFChartDtos;

    public void init(YieldMainController yieldMainController) {
        this.yieldMainController = yieldMainController;
        this.removeBarChartAllResultData();
        this.removeBarChartResultItemAllResultData();
        yieldBarChart.setAnimated(false);
        yieldbarChartItem.setAnimated(false);
        resultNTFNum.getItems().addAll(
//                YieldFxmlAndLanguageUtils.getString(UIConstant.Number_5),
                YieldFxmlAndLanguageUtils.getString(UIConstant.Number_10));
        resultNTFNum.setOnAction(event -> fireResultBasedCmbChangeEvent());
        this.setBarChartStyle();
    }

    private void setBarChartStyle() {
        XYChart.Series series = new XYChart.Series();
        series.getData().add(new XYChart.Data("", 0));
        series.getData().add(new XYChart.Data("  ", 0));
        series.getData().add(new XYChart.Data("   ", 0));
        series.getData().add(new XYChart.Data("    ", 0));
        series.getData().add(new XYChart.Data("     ", 0));
        yieldbarChartItem.getData().addAll(series);
        yieldBarChart.getData().addAll(series);//barChart中添加元素
        //yieldBarChart.getData().addAll(yAxisValue);
        yieldBarChart.getData();
        yieldBarChart.setHorizontalGridLinesVisible(true);
        yieldBarChart.setVerticalGridLinesVisible(true);
        yieldbarChartItem.setHorizontalGridLinesVisible(true);
        yieldbarChartItem.setVerticalGridLinesVisible(true);
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.initI18n();
        this.yieldResultDataController.init(this);
        YieldAnalysisConfigDto yieldAnalysisConfigDto = this.getYieldConfigPreference();
        if (yieldAnalysisConfigDto == null) {
            yieldAnalysisConfigDto = new YieldAnalysisConfigDto();
            yieldAnalysisConfigDto.setPrimaryKey("");
            yieldAnalysisConfigDto.setTopN(10);
            this.updateYieldConfigPreference(yieldAnalysisConfigDto);
        }
        resultNTFNum.setValue(10);
        resultNTFNum.setDisable(true);

    }

    private void initI18n() {
        yieldBarChartLabel = new String[]{
                YieldFxmlAndLanguageUtils.getString(UIConstant.BARCHART_FPY),
                YieldFxmlAndLanguageUtils.getString(UIConstant.BARCHART_NTF),
                YieldFxmlAndLanguageUtils.getString((UIConstant.BARCHART_NG))
        };
    }

    public void analyzeYieldResult(YieldChartResultAlermDto yieldChartResultAlermDto) {
        resultNTFNum.setDisable(false);
        //清除分析之前的数据
        this.removeBarChartAllResultData();
        while (yieldChartResultAlermDto == null) {
            continue;
        }
        this.setAnalysisBarChartResultData(yieldChartResultAlermDto);

    }

    private void removeBarChartAllResultData() {
        yieldBarChart.getData().setAll(FXCollections.observableArrayList());
    }

    public void ananlyzeyieldResultItem(YieldResultDto yieldResultDto) {
        //清除之前的数据
        this.removeBarChartResultItemAllResultData();
        if (yieldResultDto == null) {
            return;
        }

        this.setAnalysisBarChartResultItemData(yieldResultDto);

    }

    private void removeBarChartResultItemAllResultData() {

        yieldbarChartItem.getData().setAll(FXCollections.observableArrayList());
    }

    private void setAnalysisBarChartResultItemData(YieldResultDto yieldResultDto) {
        if (yieldResultDto == null) {
            enableSubResultOperator(false);
            RuntimeContext.getBean(IMessageManager.class).showWarnMsg(
                    YieldFxmlAndLanguageUtils.getString(UIConstant.UI_MESSAGE_TIP_WARNING_TITLE),
                    YieldFxmlAndLanguageUtils.getString("EXCEPTION_GRR_NO_ANALYSIS_RESULT"));
            return;
        }
        resultNTFNum.setDisable(false);
        setBarChartItem(yieldResultDto.getYieldNTFChartDtos());
        yieldNTFChartDtos = yieldResultDto.getYieldNTFChartDtos();
    }


    private void setAnalysisBarChartResultData(YieldChartResultAlermDto yieldChartResultAlermDto) {
        if (yieldChartResultAlermDto == null) {
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
        if (yieldChartResultAlermDto == null) {//判断yiyieldChartResult是否为空
            return;
        }
        yieldBarChart.setHorizontalGridLinesVisible(false);
        yieldBarChart.setVerticalGridLinesVisible(false);
        Double[] yieldChartArray = getYieldChartArrayValue(yieldChartResultAlermDto);
        Double yMax = MathUtils.getNaNToZoreMax(yieldChartArray);
        Double yMin = MathUtils.getNaNToZoreMin(yieldChartArray);
        if (yMax == null || yMin == null) {
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
        series1.getData().add(new XYChart.Data(yieldBarChartLabel[0], (yieldChartResultAlermDto.getFpyPercent() == null ? 0 : DAPStringUtils.isInfinityAndNaN(yieldChartResultAlermDto.getFpyPercent()) ? 0 : yieldChartResultAlermDto.getFpyPercent())));
        series1.getData().add(new XYChart.Data(yieldBarChartLabel[1], (yieldChartResultAlermDto.getNtfPercent() == null ? 0 : DAPStringUtils.isInfinityAndNaN(yieldChartResultAlermDto.getNtfPercent()) ? 0 : yieldChartResultAlermDto.getNtfPercent())));
        series1.getData().add(new XYChart.Data(yieldBarChartLabel[2], (yieldChartResultAlermDto.getNgPercent() == null ? 0 : DAPStringUtils.isInfinityAndNaN(yieldChartResultAlermDto.getNgPercent()) ? 0 : yieldChartResultAlermDto.getNgPercent())));
        yieldBarChart.getData().addAll(series1);//barChart中添加元素
        //yieldBarChart.setBarGap(10);
        yieldBarChart.setCategoryGap(10);
        ChartUtils.setChartTextAndColor(yieldBarChart.getData(), s -> {//设置Chart顶部的数据百分比
            if (DAPStringUtils.isNumeric(s)) {
                Double value = Double.valueOf(s) * 100;
                if (!DAPStringUtils.isInfinityAndNaN(value)) {
                    return DAPStringUtils.formatDouble(value, 2) + "%";
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

    private void fireResultBasedCmbChangeEvent() {
        removeBarChartResultItemAllResultData();
        setBarChartItem(yieldNTFChartDtos);
    }

    private void setBarChartItem(List<YieldNTFChartDto> yieldNTFChartDtos) {
        if (yieldNTFChartDtos.size() == 0) {
            return;
        }


        Double[] yChartArrayData = null;
        for (int i = 0; i < yieldNTFChartDtos.size(); i++) {
            yChartArrayData = new Double[yieldNTFChartDtos.size()];
            yChartArrayData[i] = yieldNTFChartDtos.get(i).getNtfPercent();
        }
        Double yMax = MathUtils.getNaNToZoreMax(yChartArrayData);
        Double yMin = MathUtils.getNaNToZoreMin(yChartArrayData);
        if (yMax == null || yMin == null) {
            return;
        }
        //设置y轴
        NumberAxis yAxis = (NumberAxis) yieldbarChartItem.getYAxis();
        CategoryAxis xAis = (CategoryAxis) yieldbarChartItem.getXAxis();
        Double  xAisLength = xAis.getEndMargin();
        Double avgxAisLength = xAisLength/10;
        System.out.println(avgxAisLength);
        CategoryAxis categoryAxis = new CategoryAxis();
        yieldbarChartItem.setHorizontalGridLinesVisible(false);
        yieldbarChartItem.setVerticalGridLinesVisible(false);
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
        XYChart.Series series2 = new XYChart.Series();
        Integer barChartNTFNum = Integer.parseInt(resultNTFNum.getValue().toString());
        if (yieldNTFChartDtos.size() >= barChartNTFNum) {
            for (int i = 0; i < barChartNTFNum; i++) {
                String key = " ";
                for(int n=0;n<i;n++){
                    key += " ";
                }
                String xValue = yieldNTFChartDtos.get(i).getItemName();
                int index = xValue.length();
                if (index < 10){
                    String xValueIndex =  xValue.substring(0,index);
                    series2.getData().add(new XYChart.Data( yieldNTFChartDtos.get(i).getNtfPercent() == null ? key :( xValueIndex), (yieldNTFChartDtos.get(i).getNtfPercent() == null ? 0 : DAPStringUtils.isInfinityAndNaN(yieldNTFChartDtos.get(i).getNtfPercent()) ? 0 : yieldNTFChartDtos.get(i).getNtfPercent())));
                }else{
                    String xValue1 =  xValue.substring(0,10);
                    String xValue2 = xValue.substring(10,index);
                    xValue = xValue1+ "\n"+ xValue2;
                    series2.getData().add(new XYChart.Data( yieldNTFChartDtos.get(i).getNtfPercent() == null ? key :( xValue), (yieldNTFChartDtos.get(i).getNtfPercent() == null ? 0 : DAPStringUtils.isInfinityAndNaN(yieldNTFChartDtos.get(i).getNtfPercent()) ? 0 : yieldNTFChartDtos.get(i).getNtfPercent())));
                }
            }
        } else if (yieldNTFChartDtos.size() < barChartNTFNum) {
            for (int i = 0; i < yieldNTFChartDtos.size(); i++) {
                String key = " ";
                for(int n=0;n<i;n++){
                    key += " ";
                }
                String xValue = yieldNTFChartDtos.get(i).getItemName();
                int index = xValue.length();
                if (index < 10){
                    String xValueIndex =  xValue.substring(0,index);
                    series2.getData().add(new XYChart.Data( yieldNTFChartDtos.get(i).getNtfPercent() == null ? key :( xValueIndex), (yieldNTFChartDtos.get(i).getNtfPercent() == null ? 0 : DAPStringUtils.isInfinityAndNaN(yieldNTFChartDtos.get(i).getNtfPercent()) ? 0 : yieldNTFChartDtos.get(i).getNtfPercent())));
                }else{
                    String xValue1 =  xValue.substring(0,10);
                    String xValue2 = xValue.substring(10,index);
                    xValue = xValue1+ "\n"+ xValue2;
                    series2.getData().add(new XYChart.Data( yieldNTFChartDtos.get(i).getNtfPercent() == null ? key :( xValue), (yieldNTFChartDtos.get(i).getNtfPercent() == null ? 0 : DAPStringUtils.isInfinityAndNaN(yieldNTFChartDtos.get(i).getNtfPercent()) ? 0 : yieldNTFChartDtos.get(i).getNtfPercent())));
                }

            }
            for (int i = 0 ; i < barChartNTFNum - yieldNTFChartDtos.size() ; i++){
                String key = " ";
                for(int n=0;n<i;n++){
                    key += " ";
                }
                series2.getData().add(new XYChart.Data(   key , 0));
            }
        }
        yieldbarChartItem.getData().addAll(series2);
        yieldbarChartItem.setCategoryGap(30);
        ChartUtils.setChartText(yieldbarChartItem.getData(), s -> {//设置Chart顶部的数据百分比
            if (DAPStringUtils.isNumeric(s)) {
                Double value = Double.valueOf(s) * 100;
                if (!DAPStringUtils.isInfinityAndNaN(value)) {
                    return DAPStringUtils.formatDouble(value, 2) + "%";
                }
            }
            return s + "%";
        });

    }

    private void updateYieldConfigPreference(YieldAnalysisConfigDto configDto) {
        UserPreferenceDto<YieldAnalysisConfigDto> userPreferenceDto = new UserPreferenceDto<>();
        userPreferenceDto.setUserName(envService.getUserName());
        userPreferenceDto.setCode("yield_config_preference");
        userPreferenceDto.setValue(configDto);
        userPreferenceService.updatePreference(userPreferenceDto);
    }

    private YieldAnalysisConfigDto getYieldConfigPreference() {
        String value = userPreferenceService.findPreferenceByUserId("yield_config_preference", envService.getUserName());
        if (StringUtils.isNotBlank(value)) {
            return mapper.fromJson(value, YieldAnalysisConfigDto.class);
        } else {
            return null;
        }
    }


    public Integer getResultNTFNum() {
        return Integer.parseInt(resultNTFNum.getValue().toString());
    }

    public YieldMainController getYieldMainController() {

        return yieldMainController;
    }


    public YieldResultDataController getYieldResultDataController() {
        return yieldResultDataController;
    }

}
