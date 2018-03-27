package com.dmsoft.firefly.plugin.grr.controller;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.gui.components.skin.ExpandableTableViewSkin;
import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.ImageUtils;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.gui.components.window.WindowCustomListener;
import com.dmsoft.firefly.gui.components.window.WindowMessageController;
import com.dmsoft.firefly.gui.components.window.WindowMessageFactory;
import com.dmsoft.firefly.plugin.grr.charts.ChartOperateButton;
import com.dmsoft.firefly.plugin.grr.charts.LinearChart;
import com.dmsoft.firefly.plugin.grr.charts.SelectCallBack;
import com.dmsoft.firefly.plugin.grr.charts.data.RuleLineData;
import com.dmsoft.firefly.plugin.grr.charts.data.VerticalCutLine;
import com.dmsoft.firefly.plugin.grr.charts.data.ILineData;
import com.dmsoft.firefly.plugin.grr.dto.*;
import com.dmsoft.firefly.plugin.grr.dto.analysis.*;
import com.dmsoft.firefly.plugin.grr.dto.analysis.GrrAnovaAndSourceResultDto;
import com.dmsoft.firefly.plugin.grr.handler.ParamKeys;
import com.dmsoft.firefly.plugin.grr.model.*;
import com.dmsoft.firefly.plugin.grr.utils.*;
import com.dmsoft.firefly.plugin.grr.utils.charts.ChartUtils;
import com.dmsoft.firefly.plugin.grr.utils.charts.LegendUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.dto.UserPreferenceDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.UserPreferenceService;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.job.Job;
import com.dmsoft.firefly.sdk.job.core.JobManager;
import com.dmsoft.firefly.sdk.message.IMessageManager;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sun.javafx.charts.Legend;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.*;
import java.util.function.Function;

/**
 * Created by cherry on 2018/3/12.
 */
public class GrrResultController implements Initializable {

    private Set<String> parts = Sets.newLinkedHashSet();
    private Set<String> appraisers = Sets.newLinkedHashSet();

    private GrrSummaryModel summaryModel = new GrrSummaryModel();
    private ItemResultModel itemResultModel = new ItemResultModel();
    private GrrAnovaModel anovaModel = new GrrAnovaModel();
    private GrrSourceModel sourceModel = new GrrSourceModel();

    private GrrMainController grrMainController;
    private JobManager manager = RuntimeContext.getBean(JobManager.class);
    private EnvService envService = RuntimeContext.getBean(EnvService.class);
    private UserPreferenceService userPreferenceService = RuntimeContext.getBean(UserPreferenceService.class);
    private JsonMapper mapper = JsonMapper.defaultMapper();

    /**
     * Init grr main controller
     *
     * @param grrMainController grr main controller
     */
    public void init(GrrMainController grrMainController) {
        this.grrMainController = grrMainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.initComponents();
        this.initData();
        this.initComponentsRender();
        this.initComponentEvents();
        this.initPerformanceSelected();
    }

    /**
     * Analyze grr result
     *
     * @param grrSummaryDtos grr summary dto
     * @param grrDetailDto   grr detail
     */
    public void analyzeGrrResult(List<GrrSummaryDto> grrSummaryDtos, GrrDetailDto grrDetailDto) {
//        Set digNum
        DigNumInstance.newInstance().setDigNum(grrMainController.getActiveTemplateSettingDto().getDecimalDigit());
        List<GrrViewDataDto> viewDataDtos = grrMainController.getGrrDataFrame().getIncludeDatas();
        parts.clear();
        appraisers.clear();
        viewDataDtos.forEach(viewDataDto -> {
            parts.add(viewDataDto.getPart());
            appraisers.add(viewDataDto.getOperator());
        });
        this.removeAllResultData();
        this.summaryModel.setRules(grrMainController.getGrrConfigDto().getAlarmSetting());
        String selectedName = grrDetailDto == null ? null : grrDetailDto.getItemName();
        this.setSummaryData(grrSummaryDtos, selectedName);
        if (grrSummaryDtos == null || grrSummaryDtos.isEmpty()) {
            return;
        }
        if (DAPStringUtils.isNotBlank(selectedName)) {
            this.setItemResultData(grrMainController.getGrrDataFrame(), grrMainController.getSearchConditionDto(), selectedName);
            this.setAnalysisItemResultData(grrDetailDto);
            this.setToleranceValue(summaryModel.getToleranceCellValue(selectedName));
        } else {
            RuntimeContext.getBean(IMessageManager.class).showWarnMsg(
                    GrrFxmlAndLanguageUtils.getString(UIConstant.UI_MESSAGE_TIP_WARNING_TITLE),
                    GrrFxmlAndLanguageUtils.getString("EXCEPTION_GRR_NO_ANALYSIS_RESULT"));
        }
    }

    /**
     * Change grr result when view data change submit
     */
    public void changeGrrResult() {
        submitGrrResult(grrMainController.getSearchConditionDto().getSelectedTestItemDtos().get(0).getTestItemName());
    }

    /**
     * Refresh grr result
     */
    public void refreshGrrResult() {
        if (summaryModel.hasErrorEditValue()) {
            WindowMessageController messageController = WindowMessageFactory.createWindowMessageHasOkAndCancel(GrrFxmlAndLanguageUtils.getString("UI_MESSAGE_TIP_WARNING_TITLE"),
                    GrrFxmlAndLanguageUtils.getString(GrrFxmlAndLanguageUtils.getString("GRR_SUMMARY_ERROR_EDIT_MESSAGE")));
            messageController.addProcessMonitorListener(new WindowCustomListener() {
                @Override
                public boolean onShowCustomEvent() {
                    return false;
                }

                @Override
                public boolean onCloseAndCancelCustomEvent() {
                    return false;
                }

                @Override
                public boolean onOkCustomEvent() {
                    refreshGrrResultEvent();
                    return false;
                }
            });
        } else {

            List<TestItemWithTypeDto> selectTestItemWithTypeDtos = grrMainController.getSearchConditionDto().getSelectedTestItemDtos();
            List<TestItemWithTypeDto> changedTestItemWithTypeDtos = summaryModel.getEditTestItem();
            if (changedTestItemWithTypeDtos.isEmpty() || selectTestItemWithTypeDtos.isEmpty()) {
                System.out.println("not need refresh");
                return;
            }
            refreshGrrResultEvent();
        }

    }

    private void refreshGrrResultEvent() {
//        set rules
        String selectedItem = summaryModel.getSelectedItemName();
        List<TestItemWithTypeDto> selectTestItemWithTypeDtos = grrMainController.getSearchConditionDto().getSelectedTestItemDtos();
        List<TestItemWithTypeDto> changedTestItemWithTypeDtos = summaryModel.getEditTestItem();
//        edit select test item with type
        for (int i = 0; i < selectTestItemWithTypeDtos.size(); i++) {
            for (int j = 0; j < changedTestItemWithTypeDtos.size(); j++) {
                if (selectTestItemWithTypeDtos.get(i).getTestItemName().equals(changedTestItemWithTypeDtos.get(j).getTestItemName())) {
                    selectTestItemWithTypeDtos.set(i, changedTestItemWithTypeDtos.get(j));
                }
            }
        }
        boolean hasSelectedItem = false;
        for (int i = 0; i < selectTestItemWithTypeDtos.size(); i++) {
            if (selectedItem.equals(selectTestItemWithTypeDtos.get(i).getTestItemName())) {
                hasSelectedItem = true;
                break;
            }
        }
        String itemName = hasSelectedItem ? selectedItem : "";
        //clear summary edit data
        summaryModel.clearEditData();
        submitGrrResult(itemName);
    }

    private void submitGrrResult(String selectedItem) {
        Job job = new Job(ParamKeys.GRR_REFRESH_JOB_PIPELINE);
        Map paramMap = Maps.newHashMap();
        paramMap.put(ParamKeys.SEARCH_GRR_CONDITION_DTO, grrMainController.getSearchConditionDto());
        paramMap.put(ParamKeys.SEARCH_VIEW_DATA_FRAME, grrMainController.getGrrDataFrame());
        if (DAPStringUtils.isNotBlank(selectedItem)) {
            paramMap.put(ParamKeys.TEST_ITEM_NAME, selectedItem);
        }

        Platform.runLater(() -> manager.doJobASyn(job, returnValue -> {
            try {
                Platform.runLater(() -> {
                    if (returnValue == null) {
                        //todo message tip
                        return;
                    }
                    if (returnValue instanceof Map) {
                        Map<String, Object> value = (Map<String, Object>) returnValue;
                        if (value.containsKey(UIConstant.ANALYSIS_RESULT_SUMMARY)) {
                            List<GrrSummaryDto> summaryDtos = (List<GrrSummaryDto>) value.get(UIConstant.ANALYSIS_RESULT_SUMMARY);
                            summaryModel.setAnalysisType(resultBasedCmb.getSelectionModel().getSelectedIndex());
                            summaryTb.getSortOrder().clear();
                            summaryTb.sort();
                            summaryModel.setData(summaryDtos, selectedItem);
                            summaryTb.refresh();
                        }
                        if (value.containsKey(UIConstant.ANALYSIS_RESULT_DETAIL)) {
                            GrrDetailDto grrDetailDto = (GrrDetailDto) value.get(UIConstant.ANALYSIS_RESULT_DETAIL);
                            this.removeSubResultData();
                            if (grrDetailDto == null) {
                                RuntimeContext.getBean(IMessageManager.class).showWarnMsg(
                                        GrrFxmlAndLanguageUtils.getString(UIConstant.UI_MESSAGE_TIP_WARNING_TITLE),
                                        GrrFxmlAndLanguageUtils.getString("EXCEPTION_GRR_NO_ANALYSIS_RESULT"));
                                return;
                            }
                            this.setItemResultData(grrMainController.getGrrDataFrame(),
                                    grrMainController.getSearchConditionDto(),
                                    selectedItem);
                            this.setAnalysisItemResultData(grrDetailDto);
                            this.setToleranceValue(summaryModel.getToleranceCellValue(selectedItem));
                        }
                    }
                });
            } catch (ApplicationException exception) {
                exception.printStackTrace();
            }
        }, paramMap, grrMainController));
    }

    private void analyzeGrrSubResult(TestItemWithTypeDto testItemDto, String tolerance) {
        Map detailParamMap = Maps.newHashMap();
        Job detailJob = new Job(ParamKeys.GRR_DETAIL_ANALYSIS_JOB_PIPELINE);
        detailParamMap.put(ParamKeys.SEARCH_GRR_CONDITION_DTO, grrMainController.getSearchConditionDto());
        detailParamMap.put(ParamKeys.SEARCH_VIEW_DATA_FRAME, grrMainController.getGrrDataFrame());
        detailParamMap.put(ParamKeys.TEST_ITEM_WITH_TYPE_DTO_LIST, Lists.newArrayList(testItemDto));
        detailParamMap.put(ParamKeys.TEST_ITEM_NAME, testItemDto.getTestItemName());
        this.removeSubResultData();
        this.setItemResultData(grrMainController.getGrrDataFrame(),
                grrMainController.getSearchConditionDto(),
                testItemDto.getTestItemName());
        this.setToleranceValue(tolerance);

        Platform.runLater(() -> manager.doJobASyn(detailJob, returnValue -> {
            if (returnValue == null) {
                //todo message tip
                return;
            }
            Platform.runLater(() -> {
                if (returnValue instanceof GrrDetailDto) {
                    setAnalysisItemResultData((GrrDetailDto) returnValue);
                }
            });
        }, detailParamMap, grrMainController));
    }

    private void setSummaryData(List<GrrSummaryDto> summaryData, String selectedItemName) {
        summaryTb.getSortOrder().clear();
        summaryTb.sort();
        summaryModel.setAnalysisType(resultBasedCmb.getSelectionModel().getSelectedIndex());
        summaryModel.setData(summaryData, selectedItemName);
    }

    private void setItemResultData(GrrDataFrameDto grrDataFrameDto, SearchConditionDto conditionDto, String itemName) {

        GrrItemResultDto itemResultDto = DataConvertUtils.convertToItemResult(grrDataFrameDto, itemName);
        Set<String> headerArray = Sets.newLinkedHashSet();
        headerArray.add(appKey);
        headerArray.add(trailKey);
        headerArray.addAll(parts);
        List<String> rowKeyArray = buildItemTbRowKey(conditionDto.getAppraiserInt(),
                conditionDto.getTrialInt(),
                Lists.newArrayList(appraisers));
        itemResultModel.setRowKeyArray(FXCollections.observableArrayList(rowKeyArray));
        itemResultModel.setHeaderArray(FXCollections.observableArrayList(headerArray));
        itemResultModel.setData(grrDataFrameDto.getDataFrame(),
                itemName,
                grrDataFrameDto.getIncludeDatas(),
                itemResultDto);
        TableViewWrapper.decorate(itemDetailTb, itemResultModel);
    }

    private List<String> buildItemTbRowKey(int appraiser, int trial, List<String> appraisers) {
        int rowCount = appraiser * (trial + 2) + 2;
        List<String> rowKeys = new ArrayList<>(rowCount);
        int trialIndex = 0;
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            int reminder = rowIndex % (trial + 2);
            int value = rowIndex / (trial + 2);
//            total mean
            if (rowIndex == rowCount - 2) {
                rowKeys.add(rowIndex, UIConstant.TOTAL_MEAN);
                trialIndex = 0;
                continue;
            }
//            total range
            if (rowIndex == rowCount - 1) {
                rowKeys.add(rowIndex, UIConstant.TOTAL_RANGE);
                trialIndex = 0;
                continue;
            }
//            mean
            if (reminder == trial) {
                rowKeys.add(rowIndex, appraisers.get(value) + UIConstant.SPLIT_FLAG + UIConstant.MEAN);
                trialIndex = 0;
                continue;
            }
//            range
            if (reminder == trial + 1) {
                rowKeys.add(rowIndex, appraisers.get(value) + UIConstant.SPLIT_FLAG + UIConstant.RANGE);
                trialIndex = 0;
                continue;
            }
            trialIndex++;
            rowKeys.add(rowIndex, appraisers.get(value) + UIConstant.SPLIT_FLAG + trialIndex);
        }
        return rowKeys;
    }

    private void setAnalysisItemResultData(GrrDetailDto grrDetailDto) {
        if (grrDetailDto == null) {
            RuntimeContext.getBean(IMessageManager.class).showWarnMsg(
                    GrrFxmlAndLanguageUtils.getString(UIConstant.UI_MESSAGE_TIP_WARNING_TITLE),
                    GrrFxmlAndLanguageUtils.getString("EXCEPTION_GRR_NO_ANALYSIS_RESULT"));
            return;
        }
        setComponentChart(grrDetailDto.getGrrDetailResultDto().getComponentChartDto());
        setPartAppraiserChart(grrDetailDto.getGrrDetailResultDto().getPartAppraiserChartDto(),
                Lists.newArrayList(parts), Lists.newArrayList(appraisers));
        setControlChartData(grrDetailDto.getGrrDetailResultDto().getXbarAppraiserChartDto(),
                xBarAppraiserBp,
                xBarAppraiserChart,
                xBarAppraiserChartBtn,
                Lists.newArrayList(parts),
                Lists.newArrayList(appraisers));
        this.setBarChartPerformance();
        setControlChartData(grrDetailDto.getGrrDetailResultDto().getRangeAppraiserChartDto(),
                rangeAppraiserBp,
                rangeAppraiserChart,
                rangeAppraiserChartBtn,
                Lists.newArrayList(parts),
                Lists.newArrayList(appraisers));
        this.setRangeChartPerformance();
        setScatterChartData(grrDetailDto.getGrrDetailResultDto().getRrbyAppraiserChartDto(), rrByAppraiserChart, rrbyAppraiserBp);
        setScatterChartData(grrDetailDto.getGrrDetailResultDto().getRrbyPartChartDto(), rrbyPartChart, rrbyPartBp);
        setAnovaAndSourceTb(grrDetailDto.getGrrDetailResultDto().getAnovaAndSourceResultDto());
    }

    private void removeAllResultData() {
        summaryModel.clearTableData();
        this.removeSubResultData();
    }

    private void setComponentChart(GrrComponentCResultDto componentCResult) {
        if (componentCResult == null) {
            return;
        }
        XYChart.Series series1 = new XYChart.Series();
        XYChart.Series series2 = new XYChart.Series();
        XYChart.Series series3 = new XYChart.Series();
        series1.getData().add(new XYChart.Data<>(CHART_COMPONENT_LABEL[0],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getGrrContri()) ? 0 : componentCResult.getGrrContri()));
        series1.getData().add(new XYChart.Data<>(CHART_COMPONENT_LABEL[1],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getRepeatContri()) ? 0 : componentCResult.getRepeatContri()));
        series1.getData().add(new XYChart.Data<>(CHART_COMPONENT_LABEL[2],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getReprodContri()) ? 0 : componentCResult.getReprodContri()));
        series1.getData().add(new XYChart.Data<>(CHART_COMPONENT_LABEL[3],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getPartContri()) ? 0 : componentCResult.getPartContri()));

        series2.getData().add(new XYChart.Data<>(CHART_COMPONENT_LABEL[0],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getGrrVar()) ? 0 : componentCResult.getGrrVar()));
        series2.getData().add(new XYChart.Data<>(CHART_COMPONENT_LABEL[1],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getRepeatVar()) ? 0 : componentCResult.getRepeatVar()));
        series2.getData().add(new XYChart.Data<>(CHART_COMPONENT_LABEL[2],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getReprodVar()) ? 0 : componentCResult.getReprodVar()));
        series2.getData().add(new XYChart.Data<>(CHART_COMPONENT_LABEL[3],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getPartVar()) ? 0 : componentCResult.getPartVar()));

        series3.getData().add(new XYChart.Data<>(CHART_COMPONENT_LABEL[0],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getGrrTol()) ? 0 : componentCResult.getGrrTol()));
        series3.getData().add(new XYChart.Data<>(CHART_COMPONENT_LABEL[1],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getRepeatTol()) ? 0 : componentCResult.getRepeatTol()));
        series3.getData().add(new XYChart.Data<>(CHART_COMPONENT_LABEL[2],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getReprodTol()) ? 0 : componentCResult.getReprodTol()));
        series3.getData().add(new XYChart.Data<>(CHART_COMPONENT_LABEL[3],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getPartTol()) ? 0 : componentCResult.getPartTol()));
        componentChart.getData().addAll(series1, series2, series3);
        for (int i = 0; i < CHART_COMPONENT_CATEGORY.length; i++) {
            XYChart.Series series = (XYChart.Series) componentChart.getData().get(i);
            series.setName(CHART_COMPONENT_CATEGORY[i]);
        }
        Legend legend = LegendUtils.buildLegend(componentChart.getData(),  "bar-legend-symbol", "chart-bar");
        componentBp.setLeft(legend);
        componentBp.setMargin(legend, new Insets(0, 0, 1, 0));

        //Chart text format
        int digNum = DigNumInstance.newInstance().getDigNum() - 2 >= 0 ? DigNumInstance.newInstance().getDigNum() - 2 : 0;
        ChartUtils.setChartText(componentChart.getData(), s -> {
            if (DAPStringUtils.isNumeric(s)) {
                Double value = Double.valueOf(s);
                if (!DAPStringUtils.isInfinityAndNaN(value)) {
                    return DAPStringUtils.formatDouble(value, digNum) + "%";
                }
            }
            return s + "%";
        });
    }

    private void setPartAppraiserChart(GrrPACResultDto partAppraiserChartDto,
                                       List<String> parts,
                                       List<String> appraisers) {
        double[][] data = partAppraiserChartDto.getDatas();
        double max = MathUtils.getMax(data);
        double min = MathUtils.getMin(data);
        NumberAxis yAxis = (NumberAxis) partAppraiserChart.getYAxis();
        final double factor = 0.20;
        double reserve = (max - min) * factor;
        yAxis.setAutoRanging(false);
        yAxis.setUpperBound(max + reserve);
        yAxis.setLowerBound(min - reserve);
        ObservableList<XYChart.Series> seriesData = FXCollections.observableArrayList();
        for (int i = 0; i < data.length; i++) {
            XYChart.Series series = new XYChart.Series();
            series.setName(appraisers.get(i));
            double[] appraiser = data[i];
            for (int j = 0; j < appraiser.length; j++) {
                if (!DAPStringUtils.isInfinityAndNaN(appraiser[j])) {
                    series.getData().add(new XYChart.Data<>(parts.get(j), appraiser[j], appraisers.get(i)));
                }
            }
            seriesData.add(series);
        }
        partAppraiserChart.getData().addAll(seriesData);
        Legend legend = LegendUtils.buildLegend(partAppraiserChart.getData(),
                "chart-line-symbol", "line-legend-symbol");
        partAppraiserBp.setLeft(legend);
        partAppraiserBp.setMargin(legend, new Insets(0, 0, 1, 0));
        ChartUtils.setChartToolTip(partAppraiserChart.getData(), pointTooltip -> {
            Double value = (Double) pointTooltip.getData().getYValue();
            int digNum = DigNumInstance.newInstance().getDigNum();
            return pointTooltip == null ? "" :
                    "(" + pointTooltip.getData().getExtraValue() + "," +
                            pointTooltip.getData().getXValue() + ")" + "=" + DAPStringUtils.formatDouble(value, digNum);
        });
    }

    private void setControlChartData(GrrControlChartDto chartData,
                                     BorderPane borderPane,
                                     LinearChart chart,
                                     ChartOperateButton button,
                                     List<String> parts,
                                     List<String> appraisers) {

        int partCount = parts.size();
        Double[] x = chartData.getX();
        Double[] y = chartData.getY();
        Double[] ruleData = new Double[]{chartData.getUcl(), chartData.getCl(), chartData.getLcl()};
        double max = MathUtils.getMax(y, ruleData);
        double min = MathUtils.getMin(y, ruleData);
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        final double factor = 0.20;
        double reserve = (max - min) * factor;
        yAxis.setAutoRanging(false);
        yAxis.setUpperBound(max + reserve);
        yAxis.setLowerBound(min - reserve);
        List<ILineData> horizonalLineData = Lists.newArrayList();
        List<ILineData> verticalLineData = Lists.newArrayList();
        XYChart.Series series = new XYChart.Series();
//        draw vertical line
        for (int i = 0; i < x.length; i++) {
            if ((i + 1) % partCount == 0 && i != x.length - 1) {
                double value = (x[i] + x[i + 1]) / 2;
                verticalLineData.add(new VerticalCutLine(value));
            }
            series.setName("");
            series.getData().add(new XYChart.Data<>(x[i], y[i], parts.get(i % partCount)));
        }

        RuleLineData uclLineData = new RuleLineData(UIConstant.CHART_OPERATE_NAME[0], chartData.getUcl());
        RuleLineData clLineData = new RuleLineData(UIConstant.CHART_OPERATE_NAME[1], chartData.getCl());
        RuleLineData lclLineData = new RuleLineData(UIConstant.CHART_OPERATE_NAME[2], chartData.getLcl());
        uclLineData.setColor(Color.rgb(102, 102, 102));
        lclLineData.setColor(Color.rgb(178, 178, 178));
        uclLineData.setLineClass("dashed2-line");
        clLineData.setLineClass("solid-line");
        lclLineData.setLineClass("dashed1-line");
        horizonalLineData.add(uclLineData);
        horizonalLineData.add(clLineData);
        horizonalLineData.add(lclLineData);
        int digNum = DigNumInstance.newInstance().getDigNum();
        chart.getData().add(series);
        button.setDisable(false);

        chart.buildValueMarkerWithoutTooltip(verticalLineData);
        chart.buildValueMarkerWithTooltip(horizonalLineData, new Function<ILineData, String>() {
            @Override
            public String apply(ILineData oneLineData) {
                return oneLineData.getTitle() + "\n" + oneLineData.getName() + "="
                        + DAPStringUtils.formatDouble(oneLineData.getValue(), digNum);
            }
        });
        ChartUtils.setChartToolTip(chart.getData(), pointTooltip -> {
            Double value = (Double) pointTooltip.getData().getYValue();
            return pointTooltip == null ? "" :
                    "(" + pointTooltip.getData().getExtraValue() + "," +
                            pointTooltip.getData().getXValue() + ")" + "=" + DAPStringUtils.formatDouble(value, digNum);
        });

        String legendContent = "- - - LCL,UCL   —— μ Line";
        Label legend = new Label(legendContent);
//        Legend legend = LegendUtils.buildLegend(chart.getData(), "chart-line-symbol", "line-legend-symbol");
        borderPane.setLeft(legend);
        borderPane.setMargin(legend, new Insets(5, 0, 1, 10));
    }

    private void setScatterChartData(GrrScatterChartDto scatterChartData, LineChart chart, BorderPane borderPane) {

        Double[] x = scatterChartData.getX();
        Double[] y = scatterChartData.getY();
        Double[] clX = scatterChartData.getClX();
        Double[] clY = scatterChartData.getClY();
        double max = MathUtils.getMax(y, clY);
        double min = MathUtils.getMin(y, clY);
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        final double factor = 0.20;
        double reserve = (max - min) * factor;
        yAxis.setAutoRanging(false);
        yAxis.setUpperBound(max + reserve);
        yAxis.setLowerBound(min - reserve);
        XYChart.Series scatterSeries = new XYChart.Series();
        XYChart.Series lineSeries = new XYChart.Series();
        scatterSeries.setName("Value");
        lineSeries.setName("AVG");
        for (int i = 0; i < x.length; i++) {
            scatterSeries.getData().add(new XYChart.Data<>(x[i], y[i]));
        }
        for (int i = 0; i < clX.length; i++) {
            lineSeries.getData().add(new XYChart.Data<>(clX[i], clY[i]));
        }
        chart.getData().addAll(scatterSeries, lineSeries);
        ChartUtils.setChartToolTip(chart.getData(), pointTooltip -> {
            Double value = (Double) pointTooltip.getData().getYValue();
            int digNum = DigNumInstance.newInstance().getDigNum();
            return pointTooltip == null ? "" :
                    "(" + DAPStringUtils.formatDouble(value, digNum) + ")";
        });
        scatterSeries.getNode().getStyleClass().add("chart-series-hidden-line");
        Legend legend = LegendUtils.buildLegend(chart.getData(),
                "chart-line-symbol", "line-legend-symbol");
        borderPane.setLeft(legend);
        borderPane.setMargin(legend, new Insets(0, 0, 1, 0));
    }

    private void setAnovaAndSourceTb(GrrAnovaAndSourceResultDto anovaAndSourceResultDto) {
        int digNum = DigNumInstance.newInstance().getDigNum();
        String numberOfDc = digNum >= 0 ? DAPStringUtils.formatDouble(anovaAndSourceResultDto.getNumberOfDc(), digNum)
                : String.valueOf(anovaAndSourceResultDto.getNumberOfDc());
        anovaModel.setData(anovaAndSourceResultDto.getGrrAnovaDtos());
        sourceModel.setData(anovaAndSourceResultDto.getGrrSourceDtos());
        categoryBtn.setText(DAPStringUtils.isBlankWithSpecialNumber(numberOfDc) ? "-" : numberOfDc);
    }

    private void removeSubResultData() {
        xBarAppraiserChart.clear();
        componentChart.getData().setAll(FXCollections.observableArrayList());
        partAppraiserChart.getData().setAll(FXCollections.observableArrayList());
        rrByAppraiserChart.getData().setAll(FXCollections.observableArrayList());
        xBarAppraiserChart.removeAllChildren();
        rangeAppraiserChart.removeAllChildren();
        rrbyPartChart.getData().setAll(FXCollections.observableArrayList());
        componentBp.getChildren().remove(componentBp.getLeft());
        partAppraiserBp.getChildren().remove(partAppraiserBp.getLeft());
        xBarAppraiserBp.getChildren().remove(xBarAppraiserBp.getLeft());
        rangeAppraiserBp.getChildren().remove(rangeAppraiserBp.getLeft());
        rrbyAppraiserBp.getChildren().remove(rrbyAppraiserBp.getLeft());
        rrbyPartBp.getChildren().remove(rrbyPartBp.getLeft());
        itemResultModel.setRowKeyArray(FXCollections.observableArrayList());
        itemResultModel.setHeaderArray(FXCollections.observableArrayList());
        itemResultModel.clearData();
        itemDetailTb.refresh();
        anovaModel.setData(null);
        sourceModel.setData(null);
        toleranceLbl.setText("");
        categoryBtn.setText("");
    }

    private void initComponents() {
        String testItemText = GrrFxmlAndLanguageUtils.getString("GRR_SUMMARY_TEST_ITEM");
        summaryItemTf = new TextFieldFilter();
        summaryItemTf.getTextField().setPromptText(testItemText);
        itemFilterHBox.getChildren().setAll(summaryItemTf);
        resultBasedCmb.getItems().addAll(GRR_RESULT_TYPE);
        resultBasedCmb.setValue(GRR_RESULT_TYPE[0]);
        summaryModel.initColumn(Lists.newArrayList(UIConstant.GRR_SUMMARY_TITLE));
        anovaTb.getColumns().addAll(buildAnovaTbColumn());
        sourceTb.getColumns().addAll(buildSourceTbColumn());

        //table自适应列宽
        itemDetailTb.setSkin(new ExpandableTableViewSkin(itemDetailTb));

        componentChart.setAnimated(false);
        partAppraiserChart.setAnimated(false);
        xBarAppraiserChart = buildControlChart();
        xBarAppraiserChart.setAnimated(false);
        xBarAppraiserVBox.getChildren().add(xBarAppraiserChart);
        xBarAppraiserChartBtn = new ChartOperateButton(true,
                com.dmsoft.firefly.plugin.grr.utils.enums.Orientation.BOTTOMLEFT);
        xBarAppraiserBp.setRight(xBarAppraiserChartBtn);
        xBarAppraiserBp.setMargin(xBarAppraiserChartBtn, new Insets(5, 0, 0, 0));

        rangeAppraiserChart = buildControlChart();
        rangeAppraiserChart.setAnimated(false);
        rangeAppraiserVBox.getChildren().add(rangeAppraiserChart);
        rangeAppraiserChartBtn = new ChartOperateButton(true,
                com.dmsoft.firefly.plugin.grr.utils.enums.Orientation.BOTTOMLEFT);
        rangeAppraiserBp.setRight(rangeAppraiserChartBtn);
        rangeAppraiserBp.setMargin(rangeAppraiserChartBtn, new Insets(5, 0, 0, 0));

        rrByAppraiserChart = buildScatterChart();
        rrByAppraiserChart.setAnimated(false);
        rrByAppraiserVBox.getChildren().add(rrByAppraiserChart);

        rrbyPartChart = buildScatterChart();
        rrbyPartChart.setAnimated(false);
        rrbyPartVBox.getChildren().add(rrbyPartChart);
    }

    private LinearChart buildControlChart() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return (Double) object % ((+parts.size()) / 2) == 0 ? String.valueOf(object) : null;
            }

            @Override
            public Number fromString(String string) {
                return null;
            }
        });
        xAxis.setMinorTickVisible(false);
        xAxis.setTickMarkVisible(false);
        yAxis.setMinorTickVisible(false);
        yAxis.setTickMarkVisible(false);
        yAxis.setAutoRanging(false);
        LinearChart chart = new LinearChart(xAxis, yAxis);
        return chart;
    }

    private LineChart buildScatterChart() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setMinorTickVisible(false);
        xAxis.setMinorTickVisible(false);
        xAxis.setTickMarkVisible(false);
        yAxis.setMinorTickVisible(false);
        yAxis.setTickMarkVisible(false);
        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return (Double) object % 1.0 == 0 ? String.valueOf(object) : null;
            }

            @Override
            public Number fromString(String string) {
                return null;
            }
        });
        LineChart chart = new LineChart(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setVerticalGridLinesVisible(false);
        chart.setHorizontalGridLinesVisible(false);
        return chart;
    }

    private void initComponentsRender() {
        final double INPUT_WIDTH = 200;
        itemFilterHBox.setMargin(summaryItemTf, new Insets(4, 0, 4, 0));
        summaryItemTf.getTextField().setPrefWidth(INPUT_WIDTH);
        summaryItemTf.getTextField().setFocusTraversable(false);
        summaryTb.setEditable(true);
//        componentChart.setBarGap(10);
//        componentChart.setCategoryGap(50);
        xBarAppraiserChart.setLegendVisible(false);
        xBarAppraiserChart.setVerticalGridLinesVisible(false);
        xBarAppraiserChart.setHorizontalGridLinesVisible(false);
        rangeAppraiserChart.setVerticalGridLinesVisible(false);
        rangeAppraiserChart.setHorizontalGridLinesVisible(false);
        ObservableList<TableColumn<String, ?>> summaryTbColumns = summaryTb.getColumns();
        ObservableList<TableColumn<GrrSingleAnova, ?>> anovaTbColumns = anovaTb.getColumns();
        ObservableList<TableColumn<GrrSingleSource, ?>> sourceTbColumns = sourceTb.getColumns();
        summaryTbColumns.get(0).setPrefWidth(30);
        summaryTbColumns.get(1).setPrefWidth(180);
        summaryTbColumns.get(2).setPrefWidth(80);
        summaryTbColumns.get(3).setPrefWidth(80);
        summaryTbColumns.get(4).setPrefWidth(100);
        summaryTbColumns.get(5).setPrefWidth(110);
        summaryTbColumns.get(6).setPrefWidth(110);
        summaryTbColumns.get(6).setPrefWidth(120);
        summaryTbColumns.get(7).setPrefWidth(110);
        anovaTbColumns.get(0).setPrefWidth(100);
        anovaTbColumns.get(1).setPrefWidth(140);
        anovaTbColumns.get(2).setPrefWidth(140);
        anovaTbColumns.get(3).setPrefWidth(130);
        anovaTbColumns.get(4).setPrefWidth(130);
        anovaTbColumns.get(5).setPrefWidth(130);
        sourceTbColumns.get(0).setPrefWidth(127);
        sourceTbColumns.get(1).setPrefWidth(80);
        sourceTbColumns.get(2).setPrefWidth(110);
        sourceTbColumns.get(3).setPrefWidth(110);
        sourceTbColumns.get(4).setPrefWidth(120);
        sourceTbColumns.get(5).setPrefWidth(120);
        sourceTbColumns.get(6).setPrefWidth(120);
        xBarAppraiserChartBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_choose_lines_normal.png")));
        rangeAppraiserChartBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_choose_lines_normal.png")));
        xBarAppraiserChartBtn.setListViewSize(80, 80);
        rangeAppraiserChartBtn.setListViewSize(80, 80);
        xBarAppraiserChartBtn.getStyleClass().add("btn-icon-b");
        rangeAppraiserChartBtn.getStyleClass().add("btn-icon-b");
        xBarAppraiserChartBtn.setDisable(true);
        rangeAppraiserChartBtn.setDisable(true);
    }

    private void initComponentEvents() {
        grrDataBtn.setOnAction(event -> fireDataBtnEvent());
        grrChartBtn.setOnAction(event -> fireChartBtnEvent());
        grrResultBtn.setOnAction(event -> fireResultBtnEvent());
        resultBasedCmb.setOnAction(event -> {
            summaryModel.setAnalysisType(resultBasedCmb.getSelectionModel().getSelectedIndex());
            if (summaryModel.checkSelectedRowKeyInValid()) {
                this.removeSubResultData();
                RuntimeContext.getBean(IMessageManager.class).showWarnMsg(
                        GrrFxmlAndLanguageUtils.getString(UIConstant.UI_MESSAGE_TIP_WARNING_TITLE),
                        GrrFxmlAndLanguageUtils.getString("EXCEPTION_GRR_NO_ANALYSIS_RESULT"));
            }
            summaryTb.refresh();
        });
        summaryItemTf.getTextField().textProperty().addListener(observable -> summaryModel.filterTestItem(summaryItemTf.getTextField().getText()));
        xBarAppraiserChartBtn.setSelectCallBack(buildSelectCallBack(UIConstant.GRR_CHART_XBAR_APPRAISER, xBarAppraiserChart));
        rangeAppraiserChartBtn.setSelectCallBack(buildSelectCallBack(UIConstant.GRR_CHART_RANGE_APPRAISER, rangeAppraiserChart));
        summaryModel.setRadioClickListener((grrSummaryDto, tolerance, validGrr) -> {
            if (!validGrr) {
                removeSubResultData();
                RuntimeContext.getBean(IMessageManager.class).showWarnMsg(
                        GrrFxmlAndLanguageUtils.getString(UIConstant.UI_MESSAGE_TIP_WARNING_TITLE),
                        GrrFxmlAndLanguageUtils.getString("EXCEPTION_GRR_NO_ANALYSIS_RESULT"));
                return;
            }
            grrMainController.getSearchConditionDto().getSelectedTestItemDtos().forEach(testItemWithTypeDto -> {
                if (grrSummaryDto.getItemName().equals(testItemWithTypeDto.getTestItemName())) {
                    analyzeGrrSubResult(testItemWithTypeDto, tolerance);
                }
            });
        });
    }

    private SelectCallBack buildSelectCallBack(String chartName, LinearChart chart) {
        return (name, selected, selectedNames) -> {
            chart.toggleValueMarker(name, selected);
            this.updatePerformance(chartName, selectedNames);
        };
    }

    private void updatePerformance(String chartName, Set<String> selectedNames) {
        String value = envService.findPreference(UIConstant.CHART_PERFORMANCE_CODE);
        Map data = mapper.fromJson(value, mapper.buildMapType(Map.class, String.class, Map.class));
        data = data == null ? Maps.newLinkedHashMap() : data;
        Map<String, List> operateMap = data.containsKey(chartName) && data.get(chartName) instanceof Map ?
                (Map<String, List>) data.get(chartName) : Maps.newHashMap();
        operateMap.put(UIConstant.CHART_PERFORMANCE_KEY_OPERATE, Lists.newArrayList(selectedNames));
        data.put(chartName, operateMap);
        String performValue = mapper.toJson(data);
        UserPreferenceDto userPreferenceDto = new UserPreferenceDto();
        userPreferenceDto.setUserName(envService.getUserName());
        userPreferenceDto.setCode(UIConstant.CHART_PERFORMANCE_CODE);
        userPreferenceDto.setValue(performValue);
        userPreferenceService.updatePreference(userPreferenceDto);
    }

    private void setBarChartPerformance() {
        List<String> barHiddenLines = Lists.newArrayList();
        for (String operateName : UIConstant.CHART_OPERATE_NAME) {
            if (!xBarAppraiserChartBtn.getSelectedSets().contains(operateName)) {
                barHiddenLines.add(operateName);
            }
        }
        xBarAppraiserChart.hiddenValueMarkers(barHiddenLines);
    }

    private void setRangeChartPerformance() {
        List<String> rangeHiddenLines = Lists.newArrayList();
        for (String operateName : UIConstant.CHART_OPERATE_NAME) {
            if (!rangeAppraiserChartBtn.getSelectedSets().contains(operateName)) {
                rangeHiddenLines.add(operateName);
            }
        }
        rangeAppraiserChart.hiddenValueMarkers(rangeHiddenLines);
    }

    private void initData() {
        TableViewWrapper.decorate(summaryTb, summaryModel);
        anovaTb.setItems(anovaModel.getAnovas());
        sourceTb.setItems(sourceModel.getSources());
        xBarAppraiserChartBtn.setListViewData(Lists.newArrayList(UIConstant.CHART_OPERATE_NAME));
        rangeAppraiserChartBtn.setListViewData(Lists.newArrayList(UIConstant.CHART_OPERATE_NAME));
    }

    private void initPerformanceSelected() {
        String value = envService.findPreference(UIConstant.CHART_PERFORMANCE_CODE);
        Map data = mapper.fromJson(value, mapper.buildMapType(Map.class, String.class, Map.class));
        if (data == null || data.isEmpty()) {
            this.initChartPerformance();
            return;
        }
        if (data.containsKey(UIConstant.GRR_CHART_XBAR_APPRAISER) && data.get(UIConstant.GRR_CHART_XBAR_APPRAISER) instanceof Map) {
            Map<String, List<String>> operateMap = (Map<String, List<String>>) data.get(UIConstant.GRR_CHART_XBAR_APPRAISER);
            if (operateMap.containsKey(UIConstant.CHART_PERFORMANCE_KEY_OPERATE) && operateMap.get(UIConstant.CHART_PERFORMANCE_KEY_OPERATE) instanceof List) {
                xBarAppraiserChartBtn.setSelectedSets(Sets.newHashSet(operateMap.get(UIConstant.CHART_PERFORMANCE_KEY_OPERATE)));
            }
        }
        if (data.containsKey(UIConstant.GRR_CHART_RANGE_APPRAISER) && data.get(UIConstant.GRR_CHART_RANGE_APPRAISER) instanceof Map) {
            Map<String, List<String>> operateMap = (Map<String, List<String>>) data.get(UIConstant.GRR_CHART_RANGE_APPRAISER);
            if (operateMap.containsKey(UIConstant.CHART_PERFORMANCE_KEY_OPERATE) && operateMap.get(UIConstant.CHART_PERFORMANCE_KEY_OPERATE) instanceof List) {
                rangeAppraiserChartBtn.setSelectedSets(Sets.newHashSet(operateMap.get(UIConstant.CHART_PERFORMANCE_KEY_OPERATE)));
            }
        }
    }

    private void initChartPerformance() {
        Map<String, Map<String, List<String>>> performanceMap = Maps.newHashMap();
        Map<String, List<String>> barOperatePerformance = Maps.newHashMap();
        Map<String, List<String>> rangeOperatePerformance = Maps.newHashMap();
        String barChartName = UIConstant.GRR_CHART_XBAR_APPRAISER;
        String rangeChartName = UIConstant.GRR_CHART_RANGE_APPRAISER;
        barOperatePerformance.put(UIConstant.CHART_PERFORMANCE_KEY_OPERATE, Lists.newArrayList(UIConstant.CHART_OPERATE_NAME));
        rangeOperatePerformance.put(UIConstant.CHART_PERFORMANCE_KEY_OPERATE, Lists.newArrayList(UIConstant.CHART_OPERATE_NAME));
        performanceMap.put(barChartName, barOperatePerformance);
        performanceMap.put(rangeChartName, rangeOperatePerformance);
        UserPreferenceDto userPreferenceDto = new UserPreferenceDto();
        userPreferenceDto.setUserName(envService.getUserName());
        userPreferenceDto.setCode(UIConstant.CHART_PERFORMANCE_CODE);
        userPreferenceDto.setValue(performanceMap);
        userPreferenceService.updatePreference(userPreferenceDto);
        xBarAppraiserChartBtn.setSelectedSets(Sets.newHashSet(UIConstant.CHART_OPERATE_NAME));
        rangeAppraiserChartBtn.setSelectedSets(Sets.newHashSet(UIConstant.CHART_OPERATE_NAME));
    }

    private ObservableList buildAnovaTbColumn() {
        ObservableList<TableColumn> tableColumns = FXCollections.observableArrayList();
        for (String name : GRR_ANOVA_TITLE) {
            TableColumn tableColumn = new TableColumn(name);
            tableColumn.setCellValueFactory(new PropertyValueFactory(GrrSingleAnova.propertyKeys.get(name)));
            tableColumns.add(tableColumn);
        }
        return tableColumns;
    }

    private ObservableList buildSourceTbColumn() {
        ObservableList<TableColumn> tableColumns = FXCollections.observableArrayList();
        for (String name : GRR_SOURCE_TITLE) {
            TableColumn tableColumn = new TableColumn(name);
            tableColumn.setCellValueFactory(new PropertyValueFactory(GrrSingleSource.propertyKeys.get(name)));
            tableColumns.add(tableColumn);
        }
        return tableColumns;
    }

    private void fireDataBtnEvent() {
        grrResultScrollPane.setVvalue(0);
    }

    private void fireChartBtnEvent() {
        ScrollPaneValueUtils.setScrollVerticalValue(grrResultScrollPane, chartVBox);
    }

    private void fireResultBtnEvent() {
        ScrollPaneValueUtils.setScrollVerticalValue(grrResultScrollPane, resultVBox);
    }

    public void setToleranceValue(String toleranceText) {
        this.toleranceLbl.setText(toleranceText);
    }

    public int getResultBasedCmbIndex() {
        return resultBasedCmb == null ? -1 : resultBasedCmb.getSelectionModel().getSelectedIndex();
    }

    /****** Summary *****/
    @FXML
    private HBox itemFilterHBox;
    @FXML
    private ComboBox resultBasedCmb;
    @FXML
    private TableView<String> summaryTb;
    @FXML
    private TableView itemDetailTb;
    @FXML
    private TableView anovaTb;
    @FXML
    private TableView sourceTb;
    @FXML
    private Button categoryBtn;
    @FXML
    private Button grrDataBtn;
    @FXML
    private Button grrChartBtn;
    @FXML
    private Button grrResultBtn;
    @FXML
    private ScrollPane grrResultScrollPane;
    @FXML
    private VBox chartVBox;
    @FXML
    private VBox resultVBox;
    private TextFieldFilter summaryItemTf;
    /****** Chart ******/
    @FXML
    private VBox xBarAppraiserVBox;
    @FXML
    private VBox rangeAppraiserVBox;
    @FXML
    private VBox rrByAppraiserVBox;
    @FXML
    private VBox rrbyPartVBox;
    @FXML
    private BorderPane componentBp;
    @FXML
    private BorderPane partAppraiserBp;
    @FXML
    private BorderPane xBarAppraiserBp;
    @FXML
    private BorderPane rangeAppraiserBp;
    @FXML
    private BorderPane rrbyAppraiserBp;
    @FXML
    private BorderPane rrbyPartBp;
    @FXML
    private BarChart componentChart;
    @FXML
    private LineChart partAppraiserChart;
    private LinearChart xBarAppraiserChart;
    private LinearChart rangeAppraiserChart;
    private LineChart rrByAppraiserChart;
    private LineChart rrbyPartChart;
    private ChartOperateButton xBarAppraiserChartBtn;
    private ChartOperateButton rangeAppraiserChartBtn;
    @FXML
    private Label toleranceLbl;

    private String appKey = GrrFxmlAndLanguageUtils.getString("APPRAISER") + " ";
    private String trailKey = GrrFxmlAndLanguageUtils.getString("TRAIL") + " ";
    private String[] GRR_RESULT_TYPE = new String[]{
            GrrFxmlAndLanguageUtils.getString("GRR_SUMMARY_TYPE_TOLERANCE"),
            GrrFxmlAndLanguageUtils.getString("GRR_SUMMARY_TYPE_CONTRIBUTION")};
    private String[] CHART_COMPONENT_LABEL = new String[]{
            GrrFxmlAndLanguageUtils.getString("COMPONENTS_GAGE_R"),
            GrrFxmlAndLanguageUtils.getString("COMPONENTS_REPEATABILITY"),
            GrrFxmlAndLanguageUtils.getString("COMPONENTS_REPRODUCIBILITY"),
            GrrFxmlAndLanguageUtils.getString("COMPONENTS_PART")};
    private String[] CHART_COMPONENT_CATEGORY = new String[]{
            GrrFxmlAndLanguageUtils.getString("COMPONENTS_CONTRIBUTION"),
            GrrFxmlAndLanguageUtils.getString("COMPONENTS_VARIATION"),
            GrrFxmlAndLanguageUtils.getString("COMPONENTS_TOLERANCE")};
    private String[] CHART_OPERATE_NAME = new String[]{
            GrrFxmlAndLanguageUtils.getString("COMPONENTS_CONTRIBUTION"),
            GrrFxmlAndLanguageUtils.getString("COMPONENTS_CONTRIBUTION"),
            GrrFxmlAndLanguageUtils.getString("COMPONENTS_CONTRIBUTION")};
    private String[] GRR_SUMMARY_TITLE = new String[]{
            GrrFxmlAndLanguageUtils.getString("GRR_SUMMARY_TITLE_TESTITEM"),
            GrrFxmlAndLanguageUtils.getString("GRR_SUMMARY_TITLE_LSL"),
            GrrFxmlAndLanguageUtils.getString("GRR_SUMMARY_TITLE_USL"),
            GrrFxmlAndLanguageUtils.getString("GRR_SUMMARY_TITLE_TOLERANCE"),
            GrrFxmlAndLanguageUtils.getString("GRR_SUMMARY_TITLE_REPEATABILITY"),
            GrrFxmlAndLanguageUtils.getString("GRR_SUMMARY_TITLE_REPRODUCIBILITY"),
            GrrFxmlAndLanguageUtils.getString("GRR_SUMMARY_TITLE_GAUGE")};
    private String[] GRR_ANOVA_TITLE = new String[]{
            GrrFxmlAndLanguageUtils.getString("GRR_ANOVA_TITLE_SOURCE"),
            GrrFxmlAndLanguageUtils.getString("GRR_ANOVA_TITLE_DF"),
            GrrFxmlAndLanguageUtils.getString("GRR_ANOVA_TITLE_SS"),
            GrrFxmlAndLanguageUtils.getString("GRR_ANOVA_TITLE_MS"),
            GrrFxmlAndLanguageUtils.getString("GRR_ANOVA_TITLE_F"),
            GrrFxmlAndLanguageUtils.getString("GRR_ANOVA_TITLE_PROB")};
    private String[] GRR_SOURCE_TITLE = new String[]{
            GrrFxmlAndLanguageUtils.getString("GRR_SOURCE_TITLE_SOURCE_VARIATION"),
            GrrFxmlAndLanguageUtils.getString("GRR_SOURCE_TITLE_SIGMA"),
            GrrFxmlAndLanguageUtils.getString("GRR_SOURCE_TITLE_STUDY_VAR"),
            GrrFxmlAndLanguageUtils.getString("GRR_SOURCE_TITLE_VARIATION"),
            GrrFxmlAndLanguageUtils.getString("GRR_SOURCE_TITLE_TOTAL_SIGMA"),
            GrrFxmlAndLanguageUtils.getString("GRR_SOURCE_TITLE_TOTAL_VARIATION"),
            GrrFxmlAndLanguageUtils.getString("GRR_SOURCE_TITLE_TOTAL_TOLERANCE")};
}
