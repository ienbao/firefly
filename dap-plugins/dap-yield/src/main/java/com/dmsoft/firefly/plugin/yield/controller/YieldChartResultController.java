package com.dmsoft.firefly.plugin.yield.controller;

import com.dmsoft.firefly.plugin.yield.charts.ChartTooltip;
import com.dmsoft.firefly.plugin.yield.charts.NDChart;
import com.dmsoft.firefly.plugin.yield.charts.data.NDBarChartData;
import com.dmsoft.firefly.plugin.yield.dto.chart.YieldNdChartData;
import com.dmsoft.firefly.plugin.yield.dto.YieldChartDto;
import com.dmsoft.firefly.plugin.yield.dto.YieldChartResultDto;
import com.dmsoft.firefly.plugin.yield.dto.chart.view.ChartPanel;
import com.dmsoft.firefly.plugin.yield.utils.YieldChartToolTip;
import com.dmsoft.firefly.sdk.utils.ColorUtils;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.fxml.Initializable;
import javafx.scene.chart.XYChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class YieldChartResultController implements Initializable {
    private Logger logger = LoggerFactory.getLogger(YieldChartResultController.class);
    private ChartPanel<NDChart> ndChartPane;
    private List<NDBarChartData> ndcChartDataList = Lists.newArrayList();
    private YieldMainController yieldMainController;
    private YieldChartResultDto yieldChartResultDto;
    private Map<String, XYChart> chartMap = Maps.newHashMap();
    private ChartTooltip chartTooltip = new YieldChartToolTip();


    public void init(YieldMainController yieldMainController) {
        this.yieldMainController = yieldMainController;
        Map<String, Color> colorCache = yieldMainController.getColorCache();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    public void initYieldChartData(List<YieldChartDto> yieldChartDtoList){
        ndcChartDataList.clear();
        Map<String, java.awt.Color> colorCache = yieldMainController.getColorCache();
        for (int i = 0; i < yieldChartDtoList.size(); i++) {
            YieldChartDto yieldChartDto = yieldChartDtoList.get(i);
            String key = yieldChartDto.getKey();
            String condition = (DAPStringUtils.isBlank(yieldChartDto.getCondition())) ? "All" : yieldChartDto.getCondition();
            String seriesName = yieldChartDto.getItemName() + "::" + condition;
            javafx.scene.paint.Color color = ColorUtils.toFxColorFromAwtColor(colorCache.get(key));
            YieldChartResultDto yieldChartResultDto = yieldChartDto.getResultDto();
            //List<String> analyzedRowKeys = yieldChartDto.getAnalyzedRowKeys();
            if (yieldChartResultDto == null) {
                continue;
            }
            //nd chart
            YieldNdChartData iNdcChartData = new YieldNdChartData(key, yieldChartResultDto.getNdcResult(), color);
            iNdcChartData.setSeriesName(seriesName);
            ndcChartDataList.add(iNdcChartData);
    }
        //this.setNdChartData(UIConstant.SPC_CHART_NAME[0], ndcChartDataList);
    }

    private void setNdChartData(String chartName, List<NDBarChartData> ndChartData) {
        NDChart chart = ndChartPane.getChart();
        if (chartMap.containsKey(chartName)) {
//            clear chart
            chart.removeAllChilder();
        } else {
            chartMap.put(chartName, chart);
        }
        chart.setData(ndChartData, chartTooltip);
    }
}
