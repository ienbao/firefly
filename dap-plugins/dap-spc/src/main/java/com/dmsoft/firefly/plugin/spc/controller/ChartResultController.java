/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.plugin.spc.charts.*;
import com.dmsoft.firefly.plugin.spc.charts.annotation.AnnotationFetch;
import com.dmsoft.firefly.plugin.spc.charts.data.BoxPlotChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.ChartTooltip;
import com.dmsoft.firefly.plugin.spc.charts.data.ControlChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.NDBarChartData;
import com.dmsoft.firefly.plugin.spc.charts.select.SelectCallBack;
import com.dmsoft.firefly.plugin.spc.charts.utils.MathUtils;
import com.dmsoft.firefly.plugin.spc.charts.view.ChartAnnotationButton;
import com.dmsoft.firefly.plugin.spc.charts.view.ChartOperateButton;
import com.dmsoft.firefly.plugin.spc.charts.view.ChartPanel;
import com.dmsoft.firefly.plugin.spc.charts.view.VerticalTabPane;
import com.dmsoft.firefly.plugin.spc.dto.ControlRuleDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcChartDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcSettingDto;
import com.dmsoft.firefly.plugin.spc.dto.analysis.SpcChartResultDto;
import com.dmsoft.firefly.plugin.spc.dto.analysis.SpcControlChartDto;
import com.dmsoft.firefly.plugin.spc.dto.chart.*;
import com.dmsoft.firefly.plugin.spc.utils.ImageUtils;
import com.dmsoft.firefly.plugin.spc.utils.SpcChartToolTip;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.UserPreferenceDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.dai.service.UserPreferenceService;
import com.dmsoft.firefly.sdk.utils.ColorUtils;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.chart.*;
import javafx.scene.control.Tab;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.*;

/**
 * Created by Ethan.Yang on 2018/2/2.
 */
public class ChartResultController implements Initializable {

    private SpcMainController spcMainController;
    private EnvService envService = RuntimeContext.getBean(EnvService.class);
    private SourceDataService sourceDataService = RuntimeContext.getBean(SourceDataService.class);
    private UserPreferenceService userPreferenceService = RuntimeContext.getBean(UserPreferenceService.class);

    @FXML
    private Tab analysisChartTab;
    private VerticalTabPane chartTabPane;
    private ChartAnnotationButton editBtn;
    private Map<String, XYChart> chartMap = Maps.newHashMap();
    private Map<String, String> testItemValue = Maps.newHashMap();
    private ChartPanel<NDChart> ndChartPane;
    private ChartPanel<ControlChart> runChartPane;
    private ChartPanel<ControlChart> xBarChartPane;
    private ChartPanel<ControlChart> rangeChartPane;
    private ChartPanel<ControlChart> sdChartPane;
    private ChartPanel<ControlChart> medianChartPane;
    private ChartPanel<BoxPlotChart> boxChartPane;
    private ChartPanel<ControlChart> mrChartPane;
    private List<XYChart.Data> annotationData = Lists.newArrayList();
    private ChartTooltip chartTooltip = new SpcChartToolTip();
    private String legend = "- - - LSL, USL  —— m Line   —— 6s Line";

    private Map<String, ChartOperatePaneSize> chartOperatePaneSizeMap = Maps.newHashMap();
    private Map<String, List<String>> chartOperateNameMap = Maps.newHashMap();
    private Map<String, SelectCallBack> chartOperateSelectCallBackMap = Maps.newHashMap();
    private Map<String, XYChart> chartNodeMap = Maps.newHashMap();
    private Map<String, ChartOperateButton> chartButtonMap = Maps.newHashMap();
    private Map<String, ChartPanel> chartPanelMap = Maps.newHashMap();
    private JsonMapper mapper = JsonMapper.defaultMapper();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.initChartOperatorMap();
        this.initChartPane();
    }

    private void initChartPane() {

        ndChartPane = new ChartPanel<>((NDChart) chartNodeMap.get(UIConstant.SPC_CHART_NAME[0]));
        runChartPane = new ChartPanel<>((ControlChart) chartNodeMap.get(UIConstant.SPC_CHART_NAME[1]));
        xBarChartPane = new ChartPanel<>((ControlChart) chartNodeMap.get(UIConstant.SPC_CHART_NAME[2]));
        rangeChartPane = new ChartPanel<>((ControlChart) chartNodeMap.get(UIConstant.SPC_CHART_NAME[3]));
        sdChartPane = new ChartPanel<>((ControlChart) chartNodeMap.get(UIConstant.SPC_CHART_NAME[4]));
        medianChartPane = new ChartPanel<>((ControlChart) chartNodeMap.get(UIConstant.SPC_CHART_NAME[5]));
        boxChartPane = new ChartPanel<>((BoxPlotChart) chartNodeMap.get(UIConstant.SPC_CHART_NAME[6]));
        mrChartPane = new ChartPanel<>((ControlChart) chartNodeMap.get(UIConstant.SPC_CHART_NAME[7]));

        this.initChartOperateSelectCallBackMap();
        //nd chart
        ndOperateBtn = this.buildChartOperateButton(UIConstant.SPC_CHART_NAME[0]);
        ndChartPane.getCustomPane().getChildren().add(ndOperateBtn);
        ndChartPane.setLegend(legend);
        //run chart
        this.initRunChartPane((ControlChart) chartNodeMap.get(UIConstant.SPC_CHART_NAME[1]));
        //bar chart
        barOperateBtn = this.buildChartOperateButton(UIConstant.SPC_CHART_NAME[2]);
        xBarChartPane.getCustomPane().getChildren().add(barOperateBtn);
        //range chart
        rangeOperateBtn = this.buildChartOperateButton(UIConstant.SPC_CHART_NAME[3]);
        rangeChartPane.getCustomPane().getChildren().add(rangeOperateBtn);
        //sd chart
        sdOperateBtn = this.buildChartOperateButton(UIConstant.SPC_CHART_NAME[4]);
        sdChartPane.getCustomPane().getChildren().add(sdOperateBtn);
        //median chart
        medianOperateBtn = this.buildChartOperateButton(UIConstant.SPC_CHART_NAME[5]);
        medianChartPane.getCustomPane().getChildren().add(medianOperateBtn);
        //box chart
        boxOperateBtn = this.buildChartOperateButton(UIConstant.SPC_CHART_NAME[6]);
        boxChartPane.getCustomPane().getChildren().add(boxOperateBtn);
        //mr chart
        mrOperateBtn = this.buildChartOperateButton(UIConstant.SPC_CHART_NAME[7]);
        mrChartPane.getCustomPane().getChildren().add(mrOperateBtn);

        //init user performance
        this.initChartButtonMap();
        this.initPerformanceSelected();

        chartTabPane = new VerticalTabPane();
        ndChartPane.setId(UIConstant.SPC_CHART_NAME[0]);
        runChartPane.setId(UIConstant.SPC_CHART_NAME[1]);
        xBarChartPane.setId(UIConstant.SPC_CHART_NAME[2]);
        rangeChartPane.setId(UIConstant.SPC_CHART_NAME[3]);
        sdChartPane.setId(UIConstant.SPC_CHART_NAME[4]);
        medianChartPane.setId(UIConstant.SPC_CHART_NAME[5]);
        boxChartPane.setId(UIConstant.SPC_CHART_NAME[6]);
        mrChartPane.setId(UIConstant.SPC_CHART_NAME[7]);
        chartTabPane.addNode(ndChartPane, 0);
        chartTabPane.addNode(runChartPane, 1);
        chartTabPane.addNode(xBarChartPane, 2);
        chartTabPane.addNode(rangeChartPane, 3);
        chartTabPane.addNode(sdChartPane, 4);
        chartTabPane.addNode(medianChartPane, 5);
        chartTabPane.addNode(boxChartPane, 6);
        chartTabPane.addNode(mrChartPane, 7);
        chartTabPane.activeTabByIndex(0);
        analysisChartTab.setContent(chartTabPane);
    }

    private void initPerformanceSelected() {
        String value = envService.findPreference(UIConstant.CHART_PERFORMANCE_CODE);
        Map data = mapper.fromJson(value, mapper.buildMapType(Map.class, String.class, Map.class));
        if (data == null || data.isEmpty()) {
            this.initChartPerformance();
            return;
        }
        for (Map.Entry<String, ChartOperateButton> chartOperateButtonEntry : chartButtonMap.entrySet()) {
            String chartName = chartOperateButtonEntry.getKey();
            if (data.containsKey(chartName) && data.get(chartName) instanceof Map) {
                Map<String, List<String>> chartPerformance = (Map<String, List<String>>) data.get(chartName);
                if (chartPerformance.containsKey(UIConstant.CHART_PERFORMANCE_KEY_OPERATE)) {
                    chartOperateButtonEntry.getValue().setSelectedSets(Sets.newHashSet(chartPerformance.get(UIConstant.CHART_PERFORMANCE_KEY_OPERATE)));
                }
            }
        }
        //run chart performance
        if (data.containsKey(UIConstant.SPC_CHART_NAME[1]) && data.get(UIConstant.SPC_CHART_NAME[1]) instanceof Map) {
            Map<String, List<String>> chartPerformance = (Map<String, List<String>>) data.get(UIConstant.SPC_CHART_NAME[1]);
            if (chartPerformance.containsKey(UIConstant.CHART_PERFORMANCE_KEY_RULE)) {
                rRuleBtn.setSelectedSets(Sets.newHashSet(chartPerformance.get(UIConstant.CHART_PERFORMANCE_KEY_RULE)));
            }
        }
    }

    private void initChartPerformance() {
        Map<String, Map<String, List<String>>> performanceMap = Maps.newHashMap();
        for (String name : UIConstant.SPC_CHART_NAME) {
            Map<String, List<String>> operatePerformance = Maps.newHashMap();
            if (name.equals(UIConstant.SPC_CHART_NAME[0])) {
                operatePerformance.put(UIConstant.CHART_PERFORMANCE_KEY_OPERATE, Lists.newArrayList(UIConstant.SPC_CHART_NDC_EXTERN_MENU));
            } else if (name.equals(UIConstant.SPC_CHART_NAME[1])) {
                operatePerformance.put(UIConstant.CHART_PERFORMANCE_KEY_OPERATE, Lists.newArrayList(UIConstant.SPC_CHART_RUN_EXTERN_MENU));
                operatePerformance.put(UIConstant.CHART_PERFORMANCE_KEY_RULE, Lists.newArrayList());
            } else if (name.equals(UIConstant.SPC_CHART_NAME[2]) || name.equals(UIConstant.SPC_CHART_NAME[3]) || name.equals(UIConstant.SPC_CHART_NAME[4]) || name.equals(UIConstant.SPC_CHART_NAME[5]) || name.equals(UIConstant.SPC_CHART_NAME[7])) {
                operatePerformance.put(UIConstant.CHART_PERFORMANCE_KEY_OPERATE, Lists.newArrayList(UIConstant.SPC_CHART_CONTROL_EXTERN_MENU));
            } else if (name.equals(UIConstant.SPC_CHART_NAME[6])) {
                operatePerformance.put(UIConstant.CHART_PERFORMANCE_KEY_OPERATE, Lists.newArrayList(UIConstant.SPC_CHART_BOX_EXTERN_MENU));
            }
            performanceMap.put(name, operatePerformance);

            List<String> operateNames = chartOperateNameMap.get(name);
        }
        UserPreferenceDto userPreferenceDto = new UserPreferenceDto();
        userPreferenceDto.setUserName(envService.getUserName());
        userPreferenceDto.setCode(UIConstant.CHART_PERFORMANCE_CODE);
        userPreferenceDto.setValue(performanceMap);
        userPreferenceService.updatePreference(userPreferenceDto);
    }

    private ChartOperateButton buildChartOperateButton(String charName) {
        ChartOperateButton button = new ChartOperateButton(true);
        button.setListViewData(chartOperateNameMap.get(charName));
        button.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_choose_lines_normal.png")));
        button.setListViewSize(chartOperatePaneSizeMap.get(charName).getWidth(), chartOperatePaneSizeMap.get(charName).getHeight());
        button.getStyleClass().add("btn-icon-b");
        button.setSelectCallBack(chartOperateSelectCallBackMap.get(charName));
        return button;
    }

    private SelectCallBack buildControlChartSelectCallBack(ControlChart chart, String chartName) {
        return (name, selected, selectedNames) -> {
            if (UIConstant.SPC_CHART_CONTROL_EXTERN_MENU[4].equalsIgnoreCase(name)) {
                ObservableList<XYChart.Series> series = chart.getData();
                series.forEach(oneSeries -> chart.toggleSeriesLine(oneSeries, selected));
            } else if (UIConstant.SPC_CHART_CONTROL_EXTERN_MENU[3].equalsIgnoreCase(name)) {
                ObservableList<XYChart.Series<Number, Number>> series = chart.getData();
                series.forEach(oneSeries -> oneSeries.getData().forEach(dataItem -> {
                    chart.toggleSeriesPoint(dataItem, selected);
                }));
            } else if (UIConstant.SPC_CHART_CONTROL_EXTERN_MENU[1].equalsIgnoreCase(name)) {
                chart.toggleValueMarker(name, selected);
            } else {
                chart.togglePathMarker(name, selected);
            }
            //update user performance
            updatePerformance(chartName, selectedNames, false);
        };
    }

    private SelectCallBack buildRunChartAnnotationEditSelectCallBack() {
        return (name, selected, selectedNames) -> {
            if (DAPStringUtils.isNotBlank(name)) {
                testItemValue.clear();
                testItemValue = sourceDataService.findTestData(envService.findActivatedProjectName(), name);
            }
        };
    }

    private SelectCallBack buildRunChartRRuleSelectCallBack(ControlChart chart) {
        return ((name, selected, selectedNames) -> {
            ObservableList<XYChart.Series> series = chart.getData();
            series.forEach(oneSeries -> chart.setSeriesDataStyleByRule(Lists.newArrayList(selectedNames)));
            updatePerformance(UIConstant.SPC_CHART_NAME[1], selectedNames, true);
        });
    }

    private void updatePerformance(String chartName, Set<String> selectedNames, boolean ruled) {

        String value = envService.findPreference(UIConstant.CHART_PERFORMANCE_CODE);
        Map data = mapper.fromJson(value, mapper.buildMapType(Map.class, String.class, Map.class));
        data = data == null ? Maps.newLinkedHashMap() : data;
        Map<String, List> operateMap = data.containsKey(chartName) && data.get(chartName) instanceof Map ?
                (Map<String, List>) data.get(chartName) : Maps.newHashMap();
        if (ruled) {
            operateMap.put(UIConstant.CHART_PERFORMANCE_KEY_RULE, Lists.newArrayList(selectedNames));
        } else {
            operateMap.put(UIConstant.CHART_PERFORMANCE_KEY_OPERATE, Lists.newArrayList(selectedNames));
        }
        data.put(chartName, operateMap);
        String performValue = mapper.toJson(data);
        UserPreferenceDto userPreferenceDto = new UserPreferenceDto();
        userPreferenceDto.setUserName(envService.getUserName());
        userPreferenceDto.setCode(UIConstant.CHART_PERFORMANCE_CODE);
        userPreferenceDto.setValue(performValue);
        userPreferenceService.updatePreference(userPreferenceDto);
    }

    private AnnotationFetch buildAnnotationFetch() {
        return new AnnotationFetch() {
            @Override
            public String getValue(Object id) {
                String rowKey = (String) id;
                return testItemValue.get(rowKey);
            }

            @Override
            public String getTextColor() {
                return ColorUtils.toHexFromFXColor(Color.RED);
            }

            @Override
            public boolean showedAnnotation() {
                return editBtn.isShowAnnotation();
            }

            @Override
            public void addData(XYChart.Data data) {
                annotationData.add(data);
            }
        };
    }

    private NDChart buildNDChart() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickMarkVisible(false);
        yAxis.setTickMarkVisible(false);
        yAxis.setMinorTickVisible(false);
        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);
        NDChart<Double, Double> ndChart = new NDChart(xAxis, yAxis);
        return ndChart;
    }

    private ControlChart buildControlChart() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickMarkVisible(false);
        xAxis.setMinorTickVisible(false);
        xAxis.setForceZeroInRange(false);
        yAxis.setTickMarkVisible(false);
        yAxis.setMinorTickVisible(false);
        yAxis.setAutoRanging(false);
        yAxis.setForceZeroInRange(false);
        ControlChart controlChart = new ControlChart(xAxis, yAxis);
        return controlChart;
    }

    private BoxPlotChart buildBoxPlotChart() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickMarkVisible(false);
        yAxis.setTickMarkVisible(false);
        xAxis.setMinorTickVisible(false);
        yAxis.setMinorTickVisible(false);
        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);
        BoxPlotChart boxPlotChart = new BoxPlotChart(xAxis, yAxis);
        return boxPlotChart;
    }

    private void initRunChartPane(ControlChart chart) {

        runOperateBtn = this.buildChartOperateButton(UIConstant.SPC_CHART_NAME[1]);

        rRuleBtn = new ChartOperateButton(false, com.dmsoft.firefly.plugin.spc.charts.utils.enums.Orientation.BOTTOMLEFT);
        rRuleBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_rule_normal.png")));
        rRuleBtn.setListViewData(Arrays.asList(UIConstant.SPC_RULE_R));
        rRuleBtn.setListViewSize(140, 211);
        rRuleBtn.setSelectCallBack(this.buildRunChartRRuleSelectCallBack(chart));

        List<String> itemNames = Lists.newArrayList("");
        itemNames.addAll(envService.findTestItemNames());
        editBtn = new ChartAnnotationButton();
        editBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_tracing_point_normal.png")));
        editBtn.setData(itemNames.size() < 2 ? Lists.newArrayList("") : itemNames);
        editBtn.setCallBack(() -> chart.clearAnnotation(annotationData));
        editBtn.setSelectCallBack(this.buildRunChartAnnotationEditSelectCallBack());
        runChartPane.getCustomPane().getChildren().add(rRuleBtn);
        runChartPane.getCustomPane().getChildren().add(runOperateBtn);
        runChartPane.getCustomPane().getChildren().add(editBtn);
        runChartPane.getCustomPane().setMargin(editBtn, new Insets(0, 0, 0, 5));
        runChartPane.getCustomPane().setMargin(runOperateBtn, new Insets(0, 0, 0, 5));
        runChartPane.getCustomPane().setMargin(rRuleBtn, new Insets(0, 0, 0, 5));
        runChartPane.setLegend(legend);
        chart.activePointClickEvent(true);
        chart.setPointClickCallBack(id -> {
            String key = (String) id;
            spcMainController.setViewDataFocusRowData(key);
        });
        chart.setSeriesAnnotationEvent(buildAnnotationFetch());
    }

    /**
     * init main controller
     *
     * @param spcMainController main controller
     */
    public void init(SpcMainController spcMainController) {
        this.spcMainController = spcMainController;
    }

    /**
     * init spc chart data
     *
     * @param spcChartDtoList the list of chart data
     */
    public void initSpcChartData(List<SpcChartDto> spcChartDtoList) {
        Map<String, java.awt.Color> colorCache = spcMainController.getColorCache();
        List<NDBarChartData> ndcChartDataList = Lists.newArrayList();
        List<ControlChartData> xBarChartDataList = Lists.newArrayList();
        List<ControlChartData> rangeChartDataList = Lists.newArrayList();
        List<ControlChartData> runChartDataList = Lists.newArrayList();
        List<ControlChartData> sdChartDataList = Lists.newArrayList();
        List<ControlChartData> medianChartDataList = Lists.newArrayList();
        List<BoxPlotChartData> boxChartDataList = Lists.newArrayList();
        List<ControlChartData> mrChartDataList = Lists.newArrayList();
        for (SpcChartDto spcChartDto : spcChartDtoList) {
            String key = spcChartDto.getKey();
            String condition = (DAPStringUtils.isBlank(spcChartDto.getCondition())) ? "All" : spcChartDto.getCondition();
            String seriesName = spcChartDto.getItemName() + "::" + condition;
            Color color = ColorUtils.toFxColorFromAwtColor(colorCache.get(key));
            SpcChartResultDto spcChartResultDto = spcChartDto.getResultDto();
            List<String> analyzedRowKeys = spcChartDto.getAnalyzedRowKeys();
            if (spcChartResultDto == null) {
                continue;
            }
            //nd chart
            SpcNdChartData iNdcChartData = new SpcNdChartData(key, spcChartResultDto.getNdcResult(), color);
            iNdcChartData.setSeriesName(seriesName);
            ndcChartDataList.add(iNdcChartData);
            //run chart
            SpcRunChartData runChartData = new SpcRunChartData(key, spcChartResultDto.getRunCResult(), analyzedRowKeys, color);
            runChartData.setSeriesName(seriesName);
            runChartDataList.add(runChartData);
            SpcControlChartData xBarChartData = new SpcControlChartData(key, spcChartResultDto.getXbarCResult(), color);
            xBarChartData.setSeriesName(seriesName);
            xBarChartDataList.add(xBarChartData);
            //range chart
            SpcControlChartData rangeChartData = new SpcControlChartData(key, spcChartResultDto.getRangeCResult(), color);
            rangeChartData.setSeriesName(seriesName);
            rangeChartDataList.add(rangeChartData);
            //sd chart
            SpcControlChartData sdChartData = new SpcControlChartData(key, spcChartResultDto.getSdCResult(), color);
            sdChartData.setSeriesName(seriesName);
            sdChartDataList.add(sdChartData);
            //median chart
            SpcControlChartData medianChartData = new SpcControlChartData(key, spcChartResultDto.getMedianCResult(), color);
            medianChartData.setSeriesName(seriesName);
            medianChartDataList.add(medianChartData);
            //box chart
            SpcBoxChartData boxChartData = new SpcBoxChartData(key, spcChartResultDto.getBoxCResult(), color);
            boxChartData.setSeriesName(seriesName);
            boxChartDataList.add(boxChartData);
            //mr chart
            SpcControlChartData mrChartData = new SpcControlChartData(key, spcChartResultDto.getMrCResult(), color);
            mrChartData.setSeriesName(seriesName);
            mrChartDataList.add(mrChartData);

        }

        this.setNdChartData(UIConstant.SPC_CHART_NAME[0], ndcChartDataList);
        this.setRunChartData(UIConstant.SPC_CHART_NAME[1], runChartDataList);
        this.setControlChartData(UIConstant.SPC_CHART_NAME[2], xBarChartDataList);
        this.setControlChartData(UIConstant.SPC_CHART_NAME[3], rangeChartDataList);
        this.setControlChartData(UIConstant.SPC_CHART_NAME[4], sdChartDataList);
        this.setControlChartData(UIConstant.SPC_CHART_NAME[5], medianChartDataList);
        this.setBoxChartData(UIConstant.SPC_CHART_NAME[6], boxChartDataList);
        this.setControlChartData(UIConstant.SPC_CHART_NAME[7], mrChartDataList);
    }

    private void setNdChartData(String chartName, List<NDBarChartData> ndChartData) {
        NDChart chart = ndChartPane.getChart();
        if (chartMap.containsKey(chartName)) {
//            clear chart
            chart.removeAllChildren();
        } else {
            chartMap.put(chartName, chart);
        }
        Double[] xLower = new Double[ndChartData.size()];
        Double[] xUpper = new Double[ndChartData.size()];
        Double[] yLower = new Double[ndChartData.size()];
        Double[] yUpper = new Double[ndChartData.size()];
        for (int i = 0; i < ndChartData.size(); i++) {
            xLower[i] = (Double) ndChartData.get(i).getXLowerBound();
            xUpper[i] = (Double) ndChartData.get(i).getXUpperBound();
            yLower[i] = (Double) ndChartData.get(i).getYLowerBound();
            yUpper[i] = (Double) ndChartData.get(i).getYUpperBound();
        }
        Double xMax = MathUtils.getMax(xUpper);
        Double xMin = MathUtils.getMin(xLower);
        Double yMax = MathUtils.getMax(yUpper);
        Double yMin = MathUtils.getMin(yLower);
        if (xMax == null || xMin == null || yMax == null || yMin == null) {
            return;
        }
        NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        double yReserve = (yMax - yMin) * UIConstant.FACTOR;
        double xReserve = (xMax - xMin) * UIConstant.FACTOR;
        xAxis.setLowerBound(xMin - xReserve);
        xAxis.setUpperBound(xMax + xReserve);
        yAxis.setLowerBound(yMin);
        yAxis.setUpperBound(yMax + yReserve);
        chart.setData(ndChartData, chartTooltip);
        this.setNdChartPerformance();
        ndChartPane.activeChartDragging();
    }

    private void setRunChartData(String chartName, List<ControlChartData> runChartData) {
        ControlChart chart = runChartPane.getChart();
        if (chartMap.containsKey(chartName)) {
//            clear chart
            chart.removeAllChildren();
        } else {
            chartMap.put(chartName, chart);
        }
        if (runChartData == null) {
            return;
        }
        Double[] xLower = new Double[runChartData.size()];
        Double[] xUpper = new Double[runChartData.size()];
        Double[] yLower = new Double[runChartData.size()];
        Double[] yUpper = new Double[runChartData.size()];
        for (int i = 0; i < runChartData.size(); i++) {
            xLower[i] = (Double) runChartData.get(i).getXLowerBound();
            xUpper[i] = (Double) runChartData.get(i).getXUpperBound();
            yLower[i] = (Double) runChartData.get(i).getYLowerBound();
            yUpper[i] = (Double) runChartData.get(i).getYUpperBound();
        }
        Double xMax = MathUtils.getMax(xUpper);
        Double xMin = MathUtils.getMin(xLower);
        Double yMax = MathUtils.getMax(yUpper);
        Double yMin = MathUtils.getMin(yLower);
        if (xMax == null || xMin == null || yMax == null || yMin == null) {
            return;
        }
        NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        double yReserve = (yMax - yMin) * UIConstant.FACTOR;
        double xReserve = (xMax - xMin) * UIConstant.FACTOR;
        xAxis.setLowerBound(xMin - xReserve);
        xAxis.setUpperBound(xMax + xReserve);
        yAxis.setLowerBound(yMin - yReserve);
        yAxis.setUpperBound(yMax + yReserve);
        chart.setData(runChartData, chartTooltip);
        this.setRunChartPerformance();
        runChartPane.activeChartDragging();
    }

    public void setDisableRulesByConfig() {
        Set ruleNames = Sets.newLinkedHashSet();
        SpcSettingDto spcSettingDto = spcMainController.getSpcSettingDto();
        if (spcSettingDto == null) {
            return;
        }
        List<ControlRuleDto> controlChartRules = spcSettingDto.getControlChartRule();
        if (controlChartRules == null) {
            return;
        }
        controlChartRules.forEach(controlRuleDto -> {
            if (!controlRuleDto.isUsed()) {
                ruleNames.add(controlRuleDto.getRuleName());
            }
        });
        rRuleBtn.setDisableRules(ruleNames);
    }

    private void setControlChartData(String chartName, List<ControlChartData> controlChartData) {

        ControlChart controlChart = (ControlChart) chartNodeMap.get(chartName);
        if (chartMap.containsKey(chartName)) {
//            clear chart
            controlChart.removeAllChildren();
        } else {
            chartMap.put(chartName, controlChart);
        }
        Double[] xLower = new Double[controlChartData.size()];
        Double[] xUpper = new Double[controlChartData.size()];
        Double[] yLower = new Double[controlChartData.size()];
        Double[] yUpper = new Double[controlChartData.size()];
        for (int i = 0; i < controlChartData.size(); i++) {
            xLower[i] = (Double) controlChartData.get(i).getXLowerBound();
            xUpper[i] = (Double) controlChartData.get(i).getXUpperBound();
            yLower[i] = (Double) controlChartData.get(i).getYLowerBound();
            yUpper[i] = (Double) controlChartData.get(i).getYUpperBound();
        }
        Double xMax = MathUtils.getMax(xUpper);
        Double xMin = MathUtils.getMin(xLower);
        Double yMax = MathUtils.getMax(yUpper);
        Double yMin = MathUtils.getMin(yLower);
        if (xMax == null || xMin == null || yMax == null || yMin == null) {
            return;
        }
        NumberAxis xAxis = (NumberAxis) controlChart.getXAxis();
        NumberAxis yAxis = (NumberAxis) controlChart.getYAxis();
        double yReserve = (yMax - yMin) * UIConstant.FACTOR;
        double xReserve = (xMax - xMin) * UIConstant.FACTOR;
        xAxis.setLowerBound(xMin - xReserve);
        xAxis.setUpperBound(xMax + xReserve);
        yAxis.setLowerBound(yMin - yReserve);
        yAxis.setUpperBound(yMax + yReserve);
        controlChart.setData(controlChartData, chartTooltip);
        controlChartData.forEach(controlChartData1 -> {
            Double[] ucl = controlChartData1.getUclData();
            Double[] lcl = controlChartData1.getLclData();
            controlChart.setSeriesDataStyleByRule(controlChartData1.getUniqueKey(), ucl, lcl);
        });
        this.setControlChartPerformance(controlChart);
        chartPanelMap.get(chartName).activeChartDragging();
    }

    private void setBoxChartData(String chartName, List<BoxPlotChartData> boxChartData) {
        BoxPlotChart chart = boxChartPane.getChart();
        if (chartMap.containsKey(chartName)) {
//            clear chart
            chart.removeAllChildren();
        } else {
            chartMap.put(chartName, chart);
        }
        Double[] xLower = new Double[boxChartData.size()];
        Double[] xUpper = new Double[boxChartData.size()];
        Double[] yLower = new Double[boxChartData.size()];
        Double[] yUpper = new Double[boxChartData.size()];
        for (int i = 0; i < boxChartData.size(); i++) {
            xLower[i] = (Double) boxChartData.get(i).getXLowerBound();
            xUpper[i] = (Double) boxChartData.get(i).getXUpperBound();
            yLower[i] = (Double) boxChartData.get(i).getYLowerBound();
            yUpper[i] = (Double) boxChartData.get(i).getYUpperBound();
        }
        Double xMax = MathUtils.getMax(xUpper);
        Double xMin = MathUtils.getMin(xLower);
        Double yMax = MathUtils.getMax(yUpper);
        Double yMin = MathUtils.getMin(yLower);
        if (xMax == null || xMin == null || yMax == null || yMin == null) {
            return;
        }
        NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        double yReserve = (yMax - yMin) * UIConstant.FACTOR;
        double xReserve = (xMax - xMin) * UIConstant.FACTOR;
        xAxis.setLowerBound(xMin - xReserve);
        xAxis.setUpperBound(xMax + xReserve);
        yAxis.setLowerBound(yMin - yReserve);
        yAxis.setUpperBound(yMax + yReserve);
        chart.setData(boxChartData, chartTooltip);
        this.setBoxChartPerformance();
        boxChartPane.activeChartDragging();
    }

    public void clearChartData() {
        for (Map.Entry<String, XYChart> chart : chartMap.entrySet()) {
            if (chart.getValue() instanceof NDChart) {
                ((NDChart) chart.getValue()).removeAllChildren();
            } else if (chart.getValue() instanceof ControlChart) {
                ((ControlChart) chart.getValue()).removeAllChildren();
            } else if (chart.getValue() instanceof BoxPlotChart) {
                ((BoxPlotChart) chart.getValue()).removeAllChildren();
            }
        }
    }

    public void updateChartColor(String unique, Color color) {
        ndChartPane.getChart().updateChartColor(unique, color);
        runChartPane.getChart().updateChartColor(unique, color);
        xBarChartPane.getChart().updateChartColor(unique, color);
        rangeChartPane.getChart().updateChartColor(unique, color);
        sdChartPane.getChart().updateChartColor(unique, color);
        medianChartPane.getChart().updateChartColor(unique, color);
        boxChartPane.getChart().updateChartColor(unique, color);
        mrChartPane.getChart().updateChartColor(unique, color);
    }

    private void setNdChartPerformance() {
        List<String> hiddenLines = Lists.newArrayList();
        for (String operateName : UIConstant.SPC_CHART_NDC_EXTERN_MENU) {
            if (!ndOperateBtn.getSelectedSets().contains(operateName)) {
                if (operateName.equals(UIConstant.SPC_CHART_NDC_EXTERN_MENU[9])) {
                    ndChartPane.getChart().hiddenAllBarSeries();
                    continue;
                }
                if (operateName.equals(UIConstant.SPC_CHART_NDC_EXTERN_MENU[10])) {
                    ndChartPane.getChart().toggleAreaSeries(false);
                    continue;
                }
                hiddenLines.add(operateName);
            }
        }
        ndChartPane.getChart().hiddenValueMarkers(hiddenLines);
    }

    private void setRunChartPerformance() {
        List<String> hiddenLines = Lists.newArrayList();
        for (String operateName : UIConstant.SPC_CHART_RUN_EXTERN_MENU) {
            if (!runOperateBtn.getSelectedSets().contains(operateName)) {
                if (operateName.equals(UIConstant.SPC_CHART_RUN_EXTERN_MENU[9])) {
                    runChartPane.getChart().hiddenAllSeriesPoint();
                    continue;
                }
                if (operateName.equals(UIConstant.SPC_CHART_RUN_EXTERN_MENU[10])) {
                    runChartPane.getChart().hiddenAllSeriesLine();
                    continue;
                }
                hiddenLines.add(operateName);
            }
        }
        runChartPane.getChart().hiddenValueMarkers(hiddenLines);
        runChartPane.getChart().setSeriesDataStyleByRule(Lists.newArrayList(rRuleBtn.getSelectedSets()));
    }

    private void setControlChartPerformance(ControlChart controlChart) {
        List<String> hiddenLines = Lists.newArrayList();
        for (String operateName : UIConstant.SPC_CHART_CONTROL_EXTERN_MENU) {
            if (!runOperateBtn.getSelectedSets().contains(operateName)) {
                if (operateName.equals(UIConstant.SPC_CHART_CONTROL_EXTERN_MENU[3])) {
                    controlChart.hiddenAllSeriesPoint();
                    continue;
                }
                if (operateName.equals(UIConstant.SPC_CHART_CONTROL_EXTERN_MENU[4])) {
                    controlChart.hiddenAllSeriesLine();
                    continue;
                }
                hiddenLines.add(operateName);
            }
        }
        controlChart.hiddenValueMarkers(hiddenLines);
    }

    private void setBoxChartPerformance() {
        for (String operateName : UIConstant.SPC_CHART_BOX_EXTERN_MENU) {
            if (!runOperateBtn.getSelectedSets().contains(operateName)) {
                if (operateName.equals(UIConstant.SPC_CHART_BOX_EXTERN_MENU[0])) {
                    boxChartPane.getChart().removeStroke();
                    continue;
                }
                if (operateName.equals(UIConstant.SPC_CHART_BOX_EXTERN_MENU[1])) {

                }
            }
        }
    }

    private void initChartOperatorMap() {
        chartOperateNameMap.put(UIConstant.SPC_CHART_NAME[0], Lists.newArrayList(UIConstant.SPC_CHART_NDC_EXTERN_MENU));
        chartOperateNameMap.put(UIConstant.SPC_CHART_NAME[1], Lists.newArrayList(UIConstant.SPC_CHART_RUN_EXTERN_MENU));
        chartOperateNameMap.put(UIConstant.SPC_CHART_NAME[2], Lists.newArrayList(UIConstant.SPC_CHART_CONTROL_EXTERN_MENU));
        chartOperateNameMap.put(UIConstant.SPC_CHART_NAME[3], Lists.newArrayList(UIConstant.SPC_CHART_CONTROL_EXTERN_MENU));
        chartOperateNameMap.put(UIConstant.SPC_CHART_NAME[4], Lists.newArrayList(UIConstant.SPC_CHART_CONTROL_EXTERN_MENU));
        chartOperateNameMap.put(UIConstant.SPC_CHART_NAME[5], Lists.newArrayList(UIConstant.SPC_CHART_CONTROL_EXTERN_MENU));
        chartOperateNameMap.put(UIConstant.SPC_CHART_NAME[6], Lists.newArrayList(UIConstant.SPC_CHART_BOX_EXTERN_MENU));
        chartOperateNameMap.put(UIConstant.SPC_CHART_NAME[7], Lists.newArrayList(UIConstant.SPC_CHART_CONTROL_EXTERN_MENU));
        chartOperatePaneSizeMap.put(UIConstant.SPC_CHART_NAME[0], new ChartOperatePaneSize(140, 257));
        chartOperatePaneSizeMap.put(UIConstant.SPC_CHART_NAME[1], new ChartOperatePaneSize(140, 260));
        chartOperatePaneSizeMap.put(UIConstant.SPC_CHART_NAME[2], new ChartOperatePaneSize(140, 120));
        chartOperatePaneSizeMap.put(UIConstant.SPC_CHART_NAME[3], new ChartOperatePaneSize(140, 120));
        chartOperatePaneSizeMap.put(UIConstant.SPC_CHART_NAME[4], new ChartOperatePaneSize(140, 120));
        chartOperatePaneSizeMap.put(UIConstant.SPC_CHART_NAME[5], new ChartOperatePaneSize(140, 120));
        chartOperatePaneSizeMap.put(UIConstant.SPC_CHART_NAME[6], new ChartOperatePaneSize(140, 50));
        chartOperatePaneSizeMap.put(UIConstant.SPC_CHART_NAME[7], new ChartOperatePaneSize(140, 120));
        chartNodeMap.put(UIConstant.SPC_CHART_NAME[0], buildNDChart());
        chartNodeMap.put(UIConstant.SPC_CHART_NAME[1], buildControlChart());
        chartNodeMap.put(UIConstant.SPC_CHART_NAME[2], buildControlChart());
        chartNodeMap.put(UIConstant.SPC_CHART_NAME[3], buildControlChart());
        chartNodeMap.put(UIConstant.SPC_CHART_NAME[4], buildControlChart());
        chartNodeMap.put(UIConstant.SPC_CHART_NAME[5], buildControlChart());
        chartNodeMap.put(UIConstant.SPC_CHART_NAME[6], buildBoxPlotChart());
        chartNodeMap.put(UIConstant.SPC_CHART_NAME[7], buildControlChart());
    }

    private void initChartButtonMap() {
        chartButtonMap.put(UIConstant.SPC_CHART_NAME[0], ndOperateBtn);
        chartButtonMap.put(UIConstant.SPC_CHART_NAME[1], runOperateBtn);
        chartButtonMap.put(UIConstant.SPC_CHART_NAME[2], barOperateBtn);
        chartButtonMap.put(UIConstant.SPC_CHART_NAME[3], rangeOperateBtn);
        chartButtonMap.put(UIConstant.SPC_CHART_NAME[4], sdOperateBtn);
        chartButtonMap.put(UIConstant.SPC_CHART_NAME[5], medianOperateBtn);
        chartButtonMap.put(UIConstant.SPC_CHART_NAME[6], boxOperateBtn);
        chartButtonMap.put(UIConstant.SPC_CHART_NAME[7], mrOperateBtn);
        chartPanelMap.put(UIConstant.SPC_CHART_NAME[0], ndChartPane);
        chartPanelMap.put(UIConstant.SPC_CHART_NAME[1], runChartPane);
        chartPanelMap.put(UIConstant.SPC_CHART_NAME[2], boxChartPane);
        chartPanelMap.put(UIConstant.SPC_CHART_NAME[3], rangeChartPane);
        chartPanelMap.put(UIConstant.SPC_CHART_NAME[4], sdChartPane);
        chartPanelMap.put(UIConstant.SPC_CHART_NAME[5], medianChartPane);
        chartPanelMap.put(UIConstant.SPC_CHART_NAME[6], boxChartPane);
        chartPanelMap.put(UIConstant.SPC_CHART_NAME[7], mrChartPane);
    }

    private void initChartOperateSelectCallBackMap() {
        chartOperateSelectCallBackMap.put(UIConstant.SPC_CHART_NAME[0], (name, selected, selectedNames) -> {
            if (UIConstant.SPC_CHART_NDC_EXTERN_MENU[9].equalsIgnoreCase(name)) {
                ObservableList<XYChart.Series> series = ndChartPane.getChart().getData();
                series.forEach(oneSeries -> ndChartPane.getChart().toggleBarSeries(oneSeries, selected));
            } else if (UIConstant.SPC_CHART_NDC_EXTERN_MENU[10].equalsIgnoreCase(name)) {
                ndChartPane.getChart().toggleAreaSeries(selected);
            } else {
                ndChartPane.getChart().toggleValueMarker(name, selected);
            }
            updatePerformance(UIConstant.SPC_CHART_NAME[0], selectedNames, false);
        });
        chartOperateSelectCallBackMap.put(UIConstant.SPC_CHART_NAME[1], (name, selected, selectedNames) -> {
            ControlChart runChart = runChartPane.getChart();
            if (UIConstant.SPC_CHART_RUN_EXTERN_MENU[10].equalsIgnoreCase(name)) {
                ObservableList<XYChart.Series> series = runChart.getData();
                series.forEach(oneSeries -> runChart.toggleSeriesLine(oneSeries, selected));
            } else if (UIConstant.SPC_CHART_RUN_EXTERN_MENU[9].equalsIgnoreCase(name)) {
                ObservableList<XYChart.Series<Number, Number>> series = runChart.getData();
                series.forEach(oneSeries -> oneSeries.getData().forEach(dataItem -> {
                    runChart.toggleSeriesPoint(dataItem, selected);
                }));
            } else {
                runChart.toggleValueMarker(name, selected);
            }
            updatePerformance(UIConstant.SPC_CHART_NAME[1], selectedNames, false);
        });
        chartOperateSelectCallBackMap.put(UIConstant.SPC_CHART_NAME[2], buildControlChartSelectCallBack(xBarChartPane.getChart(), UIConstant.SPC_CHART_NAME[2]));
        chartOperateSelectCallBackMap.put(UIConstant.SPC_CHART_NAME[3], buildControlChartSelectCallBack(rangeChartPane.getChart(), UIConstant.SPC_CHART_NAME[3]));
        chartOperateSelectCallBackMap.put(UIConstant.SPC_CHART_NAME[4], buildControlChartSelectCallBack(sdChartPane.getChart(), UIConstant.SPC_CHART_NAME[4]));
        chartOperateSelectCallBackMap.put(UIConstant.SPC_CHART_NAME[5], buildControlChartSelectCallBack(medianChartPane.getChart(), UIConstant.SPC_CHART_NAME[5]));
        chartOperateSelectCallBackMap.put(UIConstant.SPC_CHART_NAME[6], (name, selected, selectedNames) -> {
            BoxPlotChart boxPlotChart = boxChartPane.getChart();
            if (name.equalsIgnoreCase(UIConstant.SPC_CHART_BOX_EXTERN_MENU[0])) {
                boxPlotChart.toggleStroke(selected);
            }
        });
        chartOperateSelectCallBackMap.put(UIConstant.SPC_CHART_NAME[7], buildControlChartSelectCallBack(mrChartPane.getChart(), UIConstant.SPC_CHART_NAME[7]));
    }

    private ChartOperateButton ndOperateBtn;
    private ChartOperateButton runOperateBtn;
    private ChartOperateButton barOperateBtn;
    private ChartOperateButton rangeOperateBtn;
    private ChartOperateButton sdOperateBtn;
    private ChartOperateButton medianOperateBtn;
    private ChartOperateButton boxOperateBtn;
    private ChartOperateButton mrOperateBtn;
    private ChartOperateButton rRuleBtn;
}
