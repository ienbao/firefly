package com.dmsoft.firefly.plugin.grr.controller;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.gui.components.chart.ChartOperatorUtils;
import com.dmsoft.firefly.gui.components.skin.ExpandableTableViewSkin;
import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.ImageUtils;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.gui.components.window.WindowCustomListener;
import com.dmsoft.firefly.gui.components.window.WindowMessageController;
import com.dmsoft.firefly.gui.components.window.WindowMessageFactory;
import com.dmsoft.firefly.gui.components.window.WindowProgressTipController;
import com.dmsoft.firefly.plugin.grr.charts.ChartOperateButton;
import com.dmsoft.firefly.plugin.grr.charts.ChartRightPane;
import com.dmsoft.firefly.plugin.grr.charts.LinearChart;
import com.dmsoft.firefly.plugin.grr.charts.SelectCallBack;
import com.dmsoft.firefly.plugin.grr.charts.data.ILineData;
import com.dmsoft.firefly.plugin.grr.charts.data.RuleLineData;
import com.dmsoft.firefly.plugin.grr.charts.data.VerticalCutLine;
import com.dmsoft.firefly.plugin.grr.dto.*;
import com.dmsoft.firefly.plugin.grr.dto.analysis.*;
import com.dmsoft.firefly.plugin.grr.handler.ParamKeys;
import com.dmsoft.firefly.plugin.grr.model.*;
import com.dmsoft.firefly.plugin.grr.service.GrrConfigService;
import com.dmsoft.firefly.plugin.grr.utils.*;
import com.dmsoft.firefly.plugin.grr.utils.charts.ChartUtils;
import com.dmsoft.firefly.plugin.grr.utils.charts.LegendUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.dto.UserPreferenceDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.UserPreferenceService;
import com.dmsoft.firefly.sdk.job.core.*;
import com.dmsoft.firefly.sdk.message.IMessageManager;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sun.javafx.charts.Legend;
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
import javafx.scene.layout.BorderPane;
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
    private GrrAnovaModel grrAnovaModel = new GrrAnovaModel();
    private GrrSourceModel grrSourceModel = new GrrSourceModel();
    private GrrMainController grrMainController;
    private EnvService envService = RuntimeContext.getBean(EnvService.class);
    private UserPreferenceService userPreferenceService = RuntimeContext.getBean(UserPreferenceService.class);
    private GrrConfigService grrConfigService = RuntimeContext.getBean(GrrConfigService.class);
    private JsonMapper mapper = JsonMapper.defaultMapper();

    /**
     * Init grr main controller
     *
     * @param grrMainController grr main controller
     */
    public void init(GrrMainController grrMainController) {
        this.grrMainController = grrMainController;
        this.initData();
        this.initComponentsRender();
        this.initComponentEvents();
        this.initPerformanceSelected();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.initComponents();
        this.initI18n();
    }

    /**
     * Analyze grr result
     *
     * @param grrSummaryDtos grr summary dto
     * @param grrDetailDto   grr detail
     */
    public void analyzeGrrResult(List<GrrSummaryDto> grrSummaryDtos, GrrDetailDto grrDetailDto) {
//        Set digNum
        this.removeAllResultData();
        DigNumInstance.newInstance().setDigNum(grrMainController.getActiveTemplateSettingDto().getDecimalDigit());
        List<GrrViewDataDto> viewDataDtos = grrMainController.getGrrDataFrame().getIncludeDatas();
        parts.clear();
        appraisers.clear();
        viewDataDtos.forEach(viewDataDto -> {
            parts.add(viewDataDto.getPart());
            appraisers.add(viewDataDto.getOperator());
        });
        if (grrSummaryDtos == null || grrSummaryDtos.isEmpty()) {
            return;
        }
        String selectedName = grrSummaryDtos.get(0).getItemName();
        this.summaryModel.setRules(grrMainController.getGrrConfigDto().getAlarmSetting());
        this.setSummaryData(grrSummaryDtos, selectedName);
        this.setToleranceValue(summaryModel.getToleranceCellValue(selectedName));
        if (grrDetailDto != null) {
            this.setItemResultData(grrMainController.getGrrDataFrame(), grrMainController.getSearchConditionDto(), selectedName);
            this.setAnalysisItemResultData(grrDetailDto);
        } else {
            this.enableSubResultOperator(false);
            RuntimeContext.getBean(IMessageManager.class).showWarnMsg(
                    GrrFxmlAndLanguageUtils.getString(UIConstant.UI_MESSAGE_TIP_WARNING_TITLE),
                    GrrFxmlAndLanguageUtils.getString("EXCEPTION_GRR_NO_ANALYSIS_RESULT"));
        }
    }

    /**
     * Change grr result when view data change submit
     */
    public void changeGrrResult() {
        submitGrrResult(grrMainController.getSearchConditionDto().getSelectedTestItemDtos().get(0), true);
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
                RuntimeContext.getBean(IMessageManager.class).showWarnMsg(
                        GrrFxmlAndLanguageUtils.getString(UIConstant.UI_MESSAGE_TIP_WARNING_TITLE),
                        GrrFxmlAndLanguageUtils.getString("EXCEPTION_GRR_NO_REFRESH_RESULT"));
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
        TestItemWithTypeDto testItemWithTypeDto = null;
        for (int i = 0; i < changedTestItemWithTypeDtos.size(); i++) {
            if (selectedItem.equals(changedTestItemWithTypeDtos.get(i).getTestItemName())) {
                testItemWithTypeDto = changedTestItemWithTypeDtos.get(i);
                hasSelectedItem = true;
                break;
            }
        }
        //clear summary edit data
        summaryModel.clearEditData();
        summaryTb.refresh();
        submitGrrResult(testItemWithTypeDto, hasSelectedItem);
    }

    @SuppressWarnings("unchecked")
    private void submitGrrResult(TestItemWithTypeDto testItemWithTypeDto, Boolean analyseSubResult) {
        JobContext context = RuntimeContext.getBean(JobFactory.class).createJobContext();
        WindowProgressTipController windowProgressTipController = WindowMessageFactory.createWindowProgressTip();
        context.put(ParamKeys.SEARCH_GRR_CONDITION_DTO, grrMainController.getSearchConditionDto());
        context.put(ParamKeys.SEARCH_VIEW_DATA_FRAME, grrMainController.getGrrDataFrame());
        if (testItemWithTypeDto != null && analyseSubResult) {
            context.put(ParamKeys.TEST_ITEM_WITH_TYPE_DTO, testItemWithTypeDto);
        }
        context.addJobEventListener(event -> windowProgressTipController.getTaskProgress().setProgress(event.getProgress()));
        windowProgressTipController.getCancelBtn().setOnAction(event -> context.interruptBeforeNextJobHandler());
        JobPipeline jobPipeline = RuntimeContext.getBean(JobManager.class).getPipeLine(ParamKeys.GRR_REFRESH_JOB_PIPELINE);
        jobPipeline.setCompleteHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                List<GrrSummaryDto> grrSummaryDtoList = (List<GrrSummaryDto>) context.get(ParamKeys.GRR_SUMMARY_DTO_LIST);
                if (grrSummaryDtoList == null || grrSummaryDtoList.isEmpty()) {
                    return;
                }
                String itemName = context.containsKey(ParamKeys.GRR_DETAIL_DTO)
                        ? ((TestItemWithTypeDto) context.get(ParamKeys.TEST_ITEM_WITH_TYPE_DTO)).getTestItemName()
                        : summaryModel.getSelectedItemName();
                summaryModel.setAnalysisType(resultBasedCmb.getSelectionModel().getSelectedIndex());
                summaryModel.setData(grrSummaryDtoList, itemName);
                summaryTb.refresh();
                if (context.containsKey(ParamKeys.GRR_DETAIL_DTO)) {
                    removeSubResultData();
                    setToleranceValue(summaryModel.getToleranceCellValue(itemName));
                    GrrDetailDto grrDetailDto = context.getParam(ParamKeys.GRR_DETAIL_DTO, GrrDetailDto.class);
                    if (grrDetailDto != null) {
                        setItemResultData(grrMainController.getGrrDataFrame(), grrMainController.getSearchConditionDto(), itemName);
                        setAnalysisItemResultData(grrDetailDto);
                    } else {
                        enableSubResultOperator(false);
                        RuntimeContext.getBean(IMessageManager.class).showWarnMsg(
                                GrrFxmlAndLanguageUtils.getString(UIConstant.UI_MESSAGE_TIP_WARNING_TITLE),
                                GrrFxmlAndLanguageUtils.getString("EXCEPTION_GRR_NO_ANALYSIS_RESULT"));
                    }
                }

                windowProgressTipController.closeDialog();
            }
        });
        jobPipeline.setErrorHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                windowProgressTipController.updateFailProgress(context.getError().toString());
            }
        });
        jobPipeline.setInterruptHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                windowProgressTipController.closeDialog();
            }
        });
        RuntimeContext.getBean(JobManager.class).fireJobASyn(jobPipeline, context);

    }

    private void analyzeGrrSubResult(TestItemWithTypeDto testItemDto, String tolerance) {
        this.removeSubResultData();
        this.setToleranceValue(tolerance);
        JobContext context = RuntimeContext.getBean(JobFactory.class).createJobContext();
        WindowProgressTipController windowProgressTipController = WindowMessageFactory.createWindowProgressTip();
        context.put(ParamKeys.SEARCH_GRR_CONDITION_DTO, buildSearchConditionDto(testItemDto));
        context.put(ParamKeys.SEARCH_VIEW_DATA_FRAME, grrMainController.getGrrDataFrame());
        context.addJobEventListener(event -> windowProgressTipController.getTaskProgress().setProgress(event.getProgress()));
        windowProgressTipController.getCancelBtn().setOnAction(event -> context.interruptBeforeNextJobHandler());
        JobPipeline jobPipeline = RuntimeContext.getBean(JobManager.class).getPipeLine(ParamKeys.GRR_DETAIL_ANALYSIS_JOB_PIPELINE);
        jobPipeline.setCompleteHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                GrrDetailDto grrDetailDto = context.getParam(ParamKeys.GRR_DETAIL_DTO, GrrDetailDto.class);
                if (grrDetailDto == null) {
                    enableSubResultOperator(false);
                    RuntimeContext.getBean(IMessageManager.class).showWarnMsg(
                            GrrFxmlAndLanguageUtils.getString(UIConstant.UI_MESSAGE_TIP_WARNING_TITLE),
                            GrrFxmlAndLanguageUtils.getString("EXCEPTION_GRR_NO_ANALYSIS_RESULT"));
                } else {
                    setAnalysisItemResultData(grrDetailDto);
                    setItemResultData(grrMainController.getGrrDataFrame(), grrMainController.getSearchConditionDto(), testItemDto.getTestItemName());
                }
                windowProgressTipController.closeDialog();
            }
        });
        jobPipeline.setInterruptHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                context.interruptBeforeNextJobHandler();
                windowProgressTipController.closeDialog();
            }
        });
        jobPipeline.setErrorHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                windowProgressTipController.updateFailProgress(context.getError().toString());
            }
        });
        RuntimeContext.getBean(JobManager.class).fireJobASyn(jobPipeline, context);
    }

    private SearchConditionDto buildSearchConditionDto(TestItemWithTypeDto testItemDto) {
        SearchConditionDto searchConditionDto = new SearchConditionDto();
        searchConditionDto.setSelectedTestItemDtos(Lists.newArrayList(testItemDto));
        searchConditionDto.setAppraiser(grrMainController.getSearchConditionDto().getAppraiser());
        searchConditionDto.setAppraiserInt(grrMainController.getSearchConditionDto().getAppraiserInt());
        searchConditionDto.setAppraisers(grrMainController.getSearchConditionDto().getAppraisers());
        searchConditionDto.setTrialInt(grrMainController.getSearchConditionDto().getTrialInt());
        searchConditionDto.setPart(grrMainController.getSearchConditionDto().getPart());
        searchConditionDto.setPartInt(grrMainController.getSearchConditionDto().getPartInt());
        searchConditionDto.setParts(grrMainController.getSearchConditionDto().getParts());
        searchConditionDto.setSearchCondition(grrMainController.getSearchConditionDto().getSearchCondition());
        return searchConditionDto;
    }

    private void setSummaryData(List<GrrSummaryDto> summaryData, String selectedItemName) {
        summaryTb.getSortOrder().clear();
        summaryTb.sort();
        summaryModel.setAnalysisType(resultBasedCmb.getSelectionModel().getSelectedIndex());
        summaryModel.setData(summaryData, selectedItemName);
        summaryTb.refresh();
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

        this.enableSubResultOperator(true);
    }

    private void enableSubResultOperator(boolean flag) {
        grrDataBtn.setDisable(!flag);
        grrChartBtn.setDisable(!flag);
        grrResultBtn.setDisable(!flag);
        xBarAppraiserChartBtn.setDisable(!flag);
        rangeAppraiserChartBtn.setDisable(!flag);
        componentChartRightPane.toggleExtensionMenu(flag);
        partAppraiserChartRightPane.toggleExtensionMenu(flag);
        xBarAppraiserChartRightPane.toggleExtensionMenu(flag);
        rangeAppraiserChartRightPane.toggleExtensionMenu(flag);
        rrByAppraiserChartRightPane.toggleExtensionMenu(flag);
        rrbyPartChartRightPane.toggleExtensionMenu(flag);
        grrResultScrollPane.setVvalue(0.0);
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
            enableSubResultOperator(false);
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
        summaryItemTf.setDisable(false);
        resultBasedCmb.setDisable(false);
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
        Double[] array = getArrayValue(componentCResult);
        Double yMax = MathUtils.getMax(array);
        Double yMin = MathUtils.getMin(array);
        if (yMax == null || yMin == null) {
            return;
        }
        NumberAxis yAxis = (NumberAxis) componentChart.getYAxis();
        yAxis.setUpperBound(yMax + 20);
        yAxis.setLowerBound(yMin);
        ChartOperatorUtils.updateAxisTickUnit(yAxis);

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
            series.setName("% " + CHART_COMPONENT_CATEGORY[i]);
        }
        Legend legend = LegendUtils.buildLegend(componentChart.getData(), "bar-legend-symbol", "chart-bar");
        componentBp.setLeft(legend);

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
        Double max = MathUtils.getMax(data);
        Double min = MathUtils.getMin(data);
        if (DAPStringUtils.isInfinityAndNaN(max) || DAPStringUtils.isInfinityAndNaN(min)) {
            return;
        }
        NumberAxis yAxis = (NumberAxis) partAppraiserChart.getYAxis();
        final double factor = 0.20;
        double reserve = (max - min) * factor;
        yAxis.setAutoRanging(false);
        yAxis.setUpperBound(max + reserve);
        yAxis.setLowerBound(min - reserve);
        ChartOperatorUtils.updateAxisTickUnit(yAxis);
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
        ChartUtils.setChartToolTip(partAppraiserChart.getData(), pointTooltip -> {
            Double value = (Double) pointTooltip.getData().getYValue();
            int digNum = DigNumInstance.newInstance().getDigNum();
            return pointTooltip == null ? "" : "(" + pointTooltip.getData().getExtraValue() + ","
                    + pointTooltip.getData().getXValue() + ")" + "=" + DAPStringUtils.formatDouble(value, digNum);
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
        Double max = MathUtils.getMax(y, ruleData);
        Double min = MathUtils.getMin(y, ruleData);
        if (DAPStringUtils.isInfinityAndNaN(max) || DAPStringUtils.isInfinityAndNaN(min)) {
            return;
        }
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        final double factor = 0.20;
        double reserve = (max - min) * factor;
        yAxis.setAutoRanging(false);
        yAxis.setUpperBound(max + reserve);
        yAxis.setLowerBound(min - reserve);
        ChartOperatorUtils.updateAxisTickUnit(yAxis);
        List<ILineData> horizontalLineData = Lists.newArrayList();
        List<ILineData> verticalLineData = Lists.newArrayList();
        XYChart.Series series = new XYChart.Series();
        series.setName("");
//        draw vertical line
        for (int i = 0; i < x.length; i++) {
            if ((i + 1) % partCount == 0 && i != x.length - 1) {
                double value = (x[i] + x[i + 1]) / 2;
                verticalLineData.add(new VerticalCutLine(value));
            }
            if (DAPStringUtils.isInfinityAndNaN(x[i]) || DAPStringUtils.isInfinityAndNaN(y[i])) {
                continue;
            }
            series.getData().add(new XYChart.Data<>(x[i], y[i], parts.get(i % partCount)));
        }
        if (!DAPStringUtils.isInfinityAndNaN(chartData.getUcl())) {
            RuleLineData uclLineData = new RuleLineData(CHART_OPERATE_NAME[0], chartData.getUcl());
            uclLineData.setColor(Color.rgb(102, 102, 102));
            uclLineData.setLineClass("dashed2-line");
            horizontalLineData.add(uclLineData);
        }
        if (!DAPStringUtils.isInfinityAndNaN(chartData.getCl())) {
            RuleLineData clLineData = new RuleLineData(CHART_OPERATE_NAME[1], chartData.getCl());
            clLineData.setLineClass("solid-line");
            horizontalLineData.add(clLineData);
        }
        if (!DAPStringUtils.isInfinityAndNaN(chartData.getLcl())) {
            RuleLineData lclLineData = new RuleLineData(CHART_OPERATE_NAME[2], chartData.getLcl());
            lclLineData.setColor(Color.rgb(178, 178, 178));
            lclLineData.setLineClass("dashed1-line");
            horizontalLineData.add(lclLineData);
        }
        int digNum = DigNumInstance.newInstance().getDigNum();
        chart.getData().add(series);
        button.setDisable(false);

        chart.buildValueMarkerWithoutTooltip(verticalLineData);
        chart.buildValueMarkerWithTooltip(horizontalLineData, new Function<ILineData, String>() {
            @Override
            public String apply(ILineData oneLineData) {
                return oneLineData.getTitle() + "\n" + oneLineData.getName() + "="
                        + DAPStringUtils.formatDouble(oneLineData.getValue(), digNum);
            }
        });
        ChartUtils.setChartToolTip(chart.getData(), pointTooltip -> {
            Double value = (Double) pointTooltip.getData().getYValue();
            return pointTooltip == null ? "" : "(" + pointTooltip.getData().getExtraValue() + ","
                    + pointTooltip.getData().getXValue() + ")" + "=" + DAPStringUtils.formatDouble(value, digNum);
        });

        String legendContent = "- - - LCL,UCL   —— μ Line";
        Label legend = new Label(legendContent);
        borderPane.setLeft(legend);
        borderPane.setMargin(legend, new Insets(1, 0, 0, 0));
    }

    private void setScatterChartData(GrrScatterChartDto scatterChartData, LineChart chart, BorderPane borderPane) {

        Double[] x = scatterChartData.getX();
        Double[] y = scatterChartData.getY();
        Double[] clX = scatterChartData.getClX();
        Double[] clY = scatterChartData.getClY();
        Double max = MathUtils.getMax(y, clY);
        Double min = MathUtils.getMin(y, clY);
        if (DAPStringUtils.isInfinityAndNaN(max) || DAPStringUtils.isInfinityAndNaN(min)) {
            return;
        }
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        final double factor = 0.20;
        Double reserve = (max - min) * factor;
        yAxis.setAutoRanging(false);
        yAxis.setUpperBound(max + reserve);
        yAxis.setLowerBound(min - reserve);
        ChartOperatorUtils.updateAxisTickUnit(yAxis);
        XYChart.Series scatterSeries = new XYChart.Series();
        XYChart.Series lineSeries = new XYChart.Series();
        scatterSeries.setName("Value");
        lineSeries.setName("AVG");
        for (int i = 0; i < x.length; i++) {
            if (DAPStringUtils.isInfinityAndNaN(x[i]) || DAPStringUtils.isInfinityAndNaN(y[i])) {
                continue;
            }
            scatterSeries.getData().add(new XYChart.Data<>(x[i], y[i]));
        }
        for (int i = 0; i < clX.length; i++) {
            if (DAPStringUtils.isInfinityAndNaN(x[i]) || DAPStringUtils.isInfinityAndNaN(y[i])) {
                continue;
            }
            lineSeries.getData().add(new XYChart.Data<>(clX[i], clY[i]));
        }
        chart.getData().addAll(scatterSeries, lineSeries);
        ChartUtils.setChartToolTip(chart.getData(), pointTooltip -> {
            Double value = (Double) pointTooltip.getData().getYValue();
            int digNum = DigNumInstance.newInstance().getDigNum();
            return pointTooltip == null ? "" : "(" + DAPStringUtils.formatDouble(value, digNum) + ")";
        });
        scatterSeries.getNode().getStyleClass().add("chart-series-hidden-line");
        Legend legend = LegendUtils.buildLegend(chart.getData(),
                "chart-line-symbol", "line-legend-symbol");
        borderPane.setLeft(legend);
    }

    private void setAnovaAndSourceTb(GrrAnovaAndSourceResultDto anovaAndSourceResultDto) {

        anovaTb.getSortOrder().clear();
        sourceTb.getSortOrder().clear();
        anovaTb.sort();
        sourceTb.sort();
        grrAnovaModel.setData(anovaAndSourceResultDto.getGrrAnovaDtos());
        grrSourceModel.initColumn(buildSourceTbColumn());
        grrSourceModel.setData(anovaAndSourceResultDto.getGrrSourceDtos());
        int digNum = DigNumInstance.newInstance().getDigNum();
        String numberOfDc = digNum >= 0 ? DAPStringUtils.formatDouble(anovaAndSourceResultDto.getNumberOfDc(), digNum)
                : String.valueOf(anovaAndSourceResultDto.getNumberOfDc());
        categoryBtn.setText(DAPStringUtils.isBlankWithSpecialNumber(numberOfDc) ? "-" : numberOfDc);
    }

    private void removeSubResultData() {
        xBarAppraiserChart.clear();
        xBarAppraiserChart.removeAllChildren();
        rangeAppraiserChart.removeAllChildren();
        componentBp.getChildren().remove(componentBp.getLeft());
        partAppraiserBp.getChildren().remove(partAppraiserBp.getLeft());
        xBarAppraiserBp.getChildren().remove(xBarAppraiserBp.getLeft());
        rangeAppraiserBp.getChildren().remove(rangeAppraiserBp.getLeft());
        rrbyAppraiserBp.getChildren().remove(rrbyAppraiserBp.getLeft());
        rrbyPartBp.getChildren().remove(rrbyPartBp.getLeft());
        componentChart.getData().setAll(FXCollections.observableArrayList());
        partAppraiserChart.getData().setAll(FXCollections.observableArrayList());
        rrByAppraiserChart.getData().setAll(FXCollections.observableArrayList());
        rrbyPartChart.getData().setAll(FXCollections.observableArrayList());
        itemResultModel.clearTableData();
        grrAnovaModel.clearTableData();
        grrSourceModel.clearTableData();
        toleranceLbl.setText("");
        categoryBtn.setText("");
        itemDetailTb.refresh();
    }

    private void initComponents() {
        summaryItemTf = new TextFieldFilter();
        xBarAppraiserChart = buildControlChart();
        rangeAppraiserChart = buildControlChart();
        rrByAppraiserChart = buildScatterChart();
        rrbyPartChart = buildScatterChart();
        xBarAppraiserVBox.getChildren().add(xBarAppraiserChart);
        rangeAppraiserVBox.getChildren().add(rangeAppraiserChart);
        rrByAppraiserVBox.getChildren().add(rrByAppraiserChart);
        rrbyPartVBox.getChildren().add(rrbyPartChart);

        componentChartRightPane = new ChartRightPane(componentChart);
        partAppraiserChartRightPane = new ChartRightPane(partAppraiserChart);
        xBarAppraiserChartRightPane = new ChartRightPane(xBarAppraiserChart);
        rangeAppraiserChartRightPane = new ChartRightPane(rangeAppraiserChart);
        rrByAppraiserChartRightPane = new ChartRightPane(rrByAppraiserChart);
        rrbyPartChartRightPane = new ChartRightPane(rrbyPartChart);

        xBarAppraiserChartBtn = new ChartOperateButton(true,
                com.dmsoft.firefly.plugin.grr.utils.enums.Orientation.BOTTOMLEFT);
        rangeAppraiserChartBtn = new ChartOperateButton(true,
                com.dmsoft.firefly.plugin.grr.utils.enums.Orientation.BOTTOMLEFT);
        Tooltip.install(xBarAppraiserChartBtn, new Tooltip(GrrFxmlAndLanguageUtils.getString(UIConstant.BTN_CHART_CHOOSE_LINES)));
        Tooltip.install(rangeAppraiserChartBtn, new Tooltip(GrrFxmlAndLanguageUtils.getString(UIConstant.BTN_CHART_CHOOSE_LINES)));
        xBarAppraiserChartRightPane.addCustomPaneChildren(xBarAppraiserChartBtn);
        rangeAppraiserChartRightPane.addCustomPaneChildren(rangeAppraiserChartBtn);

        componentBp.setRight(componentChartRightPane);
        partAppraiserBp.setRight(partAppraiserChartRightPane);
        xBarAppraiserBp.setRight(xBarAppraiserChartRightPane);
        rangeAppraiserBp.setRight(rangeAppraiserChartRightPane);
        rrbyAppraiserBp.setRight(rrByAppraiserChartRightPane);
        rrbyPartBp.setRight(rrbyPartChartRightPane);
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
        final double inputWidth = 200;

        summaryItemTf.getTextField().setPrefWidth(inputWidth);
        summaryItemTf.getTextField().setFocusTraversable(false);
        String testItemText = GrrFxmlAndLanguageUtils.getString("GRR_SUMMARY_TEST_ITEM");
        summaryItemTf.getTextField().setPromptText(testItemText);
        summaryTb.setEditable(true);

        //table自适应列宽
        itemDetailTb.setSkin(new ExpandableTableViewSkin(itemDetailTb));
        componentChart.setAnimated(false);
        partAppraiserChart.setAnimated(false);
        xBarAppraiserChart.setAnimated(false);
        rangeAppraiserChart.setAnimated(false);
        rrByAppraiserChart.setAnimated(false);
        rrbyPartChart.setAnimated(false);

        xBarAppraiserChart.setHorizontalZeroLineVisible(false);
        xBarAppraiserChart.setVerticalZeroLineVisible(false);
        rangeAppraiserChart.setHorizontalZeroLineVisible(false);
        rangeAppraiserChart.setVerticalZeroLineVisible(false);
        rrByAppraiserChart.setHorizontalZeroLineVisible(false);
        rrByAppraiserChart.setVerticalZeroLineVisible(false);
        rrbyPartChart.setHorizontalZeroLineVisible(false);
        rrbyPartChart.setVerticalZeroLineVisible(false);

        componentChart.setVerticalGridLinesVisible(false);
        componentChart.setHorizontalGridLinesVisible(false);
        partAppraiserChart.setHorizontalGridLinesVisible(false);
        partAppraiserChart.setVerticalGridLinesVisible(false);
        xBarAppraiserChart.setVerticalGridLinesVisible(false);
        xBarAppraiserChart.setHorizontalGridLinesVisible(false);
        rangeAppraiserChart.setVerticalGridLinesVisible(false);
        rangeAppraiserChart.setHorizontalGridLinesVisible(false);
        xBarAppraiserChart.setLegendVisible(false);
        ObservableList<TableColumn<String, ?>> summaryTbColumns = summaryTb.getColumns();
        summaryTbColumns.get(0).prefWidthProperty().bind(summaryTb.widthProperty().divide(25));
        summaryTbColumns.get(1).prefWidthProperty().bind(summaryTb.widthProperty().divide(4));
        summaryTbColumns.get(2).prefWidthProperty().bind(summaryTb.widthProperty().divide(11));
        summaryTbColumns.get(3).prefWidthProperty().bind(summaryTb.widthProperty().divide(11));
        summaryTbColumns.get(4).prefWidthProperty().bind(summaryTb.widthProperty().divide(10));
        summaryTbColumns.get(5).prefWidthProperty().bind(summaryTb.widthProperty().divide(7));
        summaryTbColumns.get(6).prefWidthProperty().bind(summaryTb.widthProperty().divide(7));
        summaryTbColumns.get(7).prefWidthProperty().bind(summaryTb.widthProperty().divide(7));
//        anovaTbColumns.get(0).setPrefWidth(100);
//        anovaTbColumns.get(1).setPrefWidth(140);
//        anovaTbColumns.get(2).setPrefWidth(140);
//        anovaTbColumns.get(3).setPrefWidth(130);
//        anovaTbColumns.get(4).setPrefWidth(130);
//        anovaTbColumns.get(5).setPrefWidth(130);
//        sourceTbColumns.get(0).setPrefWidth(127);
//        sourceTbColumns.get(1).setPrefWidth(80);
//        sourceTbColumns.get(2).setPrefWidth(110);
//        sourceTbColumns.get(3).setPrefWidth(110);
//        sourceTbColumns.get(4).setPrefWidth(120);
//        sourceTbColumns.get(5).setPrefWidth(120);
//        sourceTbColumns.get(6).setPrefWidth(120);
        xBarAppraiserChartBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_choose_lines_normal.png")));
        rangeAppraiserChartBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_choose_lines_normal.png")));
        xBarAppraiserChartBtn.setListViewSize(80, 80);
        rangeAppraiserChartBtn.setListViewSize(80, 80);
        xBarAppraiserChartBtn.getStyleClass().add("btn-icon-b");
        rangeAppraiserChartBtn.getStyleClass().add("btn-icon-b");

        grrDataBtn.getStyleClass().add("btn-group");
        grrChartBtn.getStyleClass().add("btn-group");
        grrResultBtn.getStyleClass().add("btn-group-last");

        summaryItemTf.setDisable(true);
        resultBasedCmb.setDisable(true);
        this.enableSubResultOperator(false);
    }

    private void initComponentEvents() {
        grrDataBtn.setOnAction(event -> fireDataBtnEvent());
        grrChartBtn.setOnAction(event -> fireChartBtnEvent());
        grrResultBtn.setOnAction(event -> fireResultBtnEvent());
        resultBasedCmb.setOnAction(event -> fireResultBasedCmbChangeEvent());
        summaryModel.setRadioClickListener((grrSummaryDto, tolerance, validGrr) -> fireRadioBtnClickEvent(validGrr, tolerance, grrSummaryDto));
        summaryItemTf.getTextField().textProperty().addListener(observable -> summaryModel.filterTestItem(summaryItemTf.getTextField().getText()));
        xBarAppraiserChartBtn.setSelectCallBack(buildSelectCallBack(GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_CHART_XBAR_APPRAISER), xBarAppraiserChart));
        rangeAppraiserChartBtn.setSelectCallBack(buildSelectCallBack(GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_CHART_RANGE_APPRAISER), rangeAppraiserChart));
    }

    private SelectCallBack buildSelectCallBack(String chartName, LinearChart chart) {
        return (name, selected, selectedNames) -> {
            chart.toggleValueMarker(name, selected);
            this.updatePerformance(chartName, selectedNames);
        };
    }

    @SuppressWarnings("unchecked")
    private void updatePerformance(String chartName, Set<String> selectedNames) {
        String value = envService.findPreference(UIConstant.CHART_PERFORMANCE_CODE);
        Map data = mapper.fromJson(value, mapper.buildMapType(Map.class, String.class, Map.class));
        data = data == null ? Maps.newLinkedHashMap() : data;
        Map<String, List> operateMap = data.containsKey(chartName) && data.get(chartName) instanceof Map
                ? (Map<String, List>) data.get(chartName) : Maps.newHashMap();
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
        for (String operateName : CHART_OPERATE_NAME) {
            if (!xBarAppraiserChartBtn.getSelectedSets().contains(operateName)) {
                barHiddenLines.add(operateName);
            }
        }
        xBarAppraiserChart.hiddenValueMarkers(barHiddenLines);
    }

    private void setRangeChartPerformance() {
        List<String> rangeHiddenLines = Lists.newArrayList();
        for (String operateName : CHART_OPERATE_NAME) {
            if (!rangeAppraiserChartBtn.getSelectedSets().contains(operateName)) {
                rangeHiddenLines.add(operateName);
            }
        }
        rangeAppraiserChart.hiddenValueMarkers(rangeHiddenLines);
    }

    private void initData() {
        resultBasedCmb.getItems().addAll(
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SUMMARY_TYPE_TOLERANCE),
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SUMMARY_TYPE_CONTRIBUTION));
        resultBasedCmb.setValue(GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SUMMARY_TYPE_TOLERANCE));
        summaryModel.initColumn(Lists.newArrayList(GRR_SUMMARY_TITLE));
        grrAnovaModel.initColumn(Lists.newArrayList(GRR_ANOVA_TITLE));
        grrSourceModel.initColumn(buildSourceTbColumn());
        TableViewWrapper.decorate(summaryTb, summaryModel);
        TableViewWrapper.decorate(anovaTb, grrAnovaModel);
        TableViewWrapper.decorate(sourceTb, grrSourceModel);
        xBarAppraiserChartBtn.setListViewData(Lists.newArrayList(CHART_OPERATE_NAME));
        rangeAppraiserChartBtn.setListViewData(Lists.newArrayList(CHART_OPERATE_NAME));
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
        barOperatePerformance.put(UIConstant.CHART_PERFORMANCE_KEY_OPERATE, Lists.newArrayList(CHART_OPERATE_NAME));
        rangeOperatePerformance.put(UIConstant.CHART_PERFORMANCE_KEY_OPERATE, Lists.newArrayList(CHART_OPERATE_NAME));
        performanceMap.put(barChartName, barOperatePerformance);
        performanceMap.put(rangeChartName, rangeOperatePerformance);
        UserPreferenceDto userPreferenceDto = new UserPreferenceDto();
        userPreferenceDto.setUserName(envService.getUserName());
        userPreferenceDto.setCode(UIConstant.CHART_PERFORMANCE_CODE);
        userPreferenceDto.setValue(performanceMap);
        userPreferenceService.updatePreference(userPreferenceDto);
        xBarAppraiserChartBtn.setSelectedSets(Sets.newHashSet(CHART_OPERATE_NAME));
        rangeAppraiserChartBtn.setSelectedSets(Sets.newHashSet(CHART_OPERATE_NAME));
    }

    private List<String> buildSourceTbColumn() {
        final double defaultCoverage = 6.0;
        List<String> tableColumns = Lists.newArrayList();
        GrrConfigDto grrConfigDto = grrConfigService.findGrrConfig();
        double coverage = grrConfigDto == null || grrConfigDto.getCoverage() == null ? defaultCoverage : grrConfigDto.getCoverage();
        for (String name : GRR_SOURCE_TITLE) {
            name = name.equals(GRR_SOURCE_TITLE[2]) ? name + " " + coverage : name;
            tableColumns.add(name);
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

    private void fireResultBasedCmbChangeEvent() {
        summaryModel.setAnalysisType(resultBasedCmb.getSelectionModel().getSelectedIndex());
        if (grrMainController.getSearchConditionDto() == null || grrMainController.getSearchConditionDto().getSelectedTestItemDtos() == null) {
            return;
        }
        grrMainController.getSearchConditionDto().getSelectedTestItemDtos().forEach(testItemWithTypeDto -> {
            if (summaryModel.getSelectedItemName().equals(testItemWithTypeDto.getTestItemName())) {
                analyzeGrrSubResult(testItemWithTypeDto, summaryModel.getToleranceCellValue(summaryModel.getSelectedItemName()));
            }
        });
        summaryTb.refresh();
    }

    private void fireRadioBtnClickEvent(boolean validGrr, String tolerance, GrrSummaryDto grrSummaryDto) {
        if (!validGrr) {
            removeSubResultData();
            enableSubResultOperator(false);
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
    }

    private void setToleranceValue(String toleranceText) {
        this.toleranceLbl.setText(toleranceText);
    }

    private Double[] getArrayValue(GrrComponentCResultDto resultDto) {
        Double[] value = new Double[12];
        value[0] = resultDto.getGrrContri();
        value[1] = resultDto.getGrrTol();
        value[2] = resultDto.getGrrVar();
        value[3] = resultDto.getPartContri();
        value[4] = resultDto.getPartTol();
        value[5] = resultDto.getPartVar();
        value[6] = resultDto.getRepeatContri();
        value[7] = resultDto.getRepeatTol();
        value[8] = resultDto.getRepeatVar();
        value[9] = resultDto.getReprodContri();
        value[10] = resultDto.getReprodTol();
        value[11] = resultDto.getReprodVar();
        return value;
    }

    private void initI18n() {
        appKey = GrrFxmlAndLanguageUtils.getString("APPRAISER") + " ";
        trailKey = GrrFxmlAndLanguageUtils.getString("TRAIL") + " ";
        CHART_COMPONENT_LABEL = new String[]{
                GrrFxmlAndLanguageUtils.getString(UIConstant.COMPONENTS_GAGE_R),
                GrrFxmlAndLanguageUtils.getString(UIConstant.COMPONENTS_REPEATABILITY),
                GrrFxmlAndLanguageUtils.getString(UIConstant.COMPONENTS_REPRODUCIBILITY),
                GrrFxmlAndLanguageUtils.getString(UIConstant.COMPONENTS_PART)};
        CHART_COMPONENT_CATEGORY = new String[]{
                GrrFxmlAndLanguageUtils.getString(UIConstant.COMPONENTS_CONTRIBUTION),
                GrrFxmlAndLanguageUtils.getString(UIConstant.COMPONENTS_VARIATION),
                GrrFxmlAndLanguageUtils.getString(UIConstant.COMPONENTS_TOLERANCE)};
        CHART_OPERATE_NAME = new String[]{
                GrrFxmlAndLanguageUtils.getString(UIConstant.CHART_LINE_NAME_UCL),
                GrrFxmlAndLanguageUtils.getString(UIConstant.CHART_LINE_NAME_AVG),
                GrrFxmlAndLanguageUtils.getString(UIConstant.CHART_LINE_NAME_LCL)};
        GRR_SUMMARY_TITLE = new String[]{
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SUMMARY_TITLE_TESTITEM),
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SUMMARY_TITLE_LSL),
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SUMMARY_TITLE_USL),
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SUMMARY_TITLE_TOLERANCE),
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SUMMARY_TITLE_REPEATABILITY),
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SUMMARY_TITLE_REPRODUCIBILITY),
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SUMMARY_TITLE_GAUGE)};
        GRR_ANOVA_TITLE = new String[]{
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_ANOVA_TITLE_SOURCE),
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_ANOVA_TITLE_DF),
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_ANOVA_TITLE_SS),
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_ANOVA_TITLE_MS),
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_ANOVA_TITLE_F),
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_ANOVA_TITLE_PROB)};
        GRR_SOURCE_TITLE = new String[]{
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SOURCE_TITLE_SOURCE_VARIATION),
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SOURCE_TITLE_SIGMA),
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SOURCE_TITLE_STUDY_VAR),
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SOURCE_TITLE_VARIATION),
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SOURCE_TITLE_TOTAL_SIGMA),
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SOURCE_TITLE_TOTAL_VARIATION),
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SOURCE_TITLE_TOTAL_TOLERANCE)};
    }

    /****** Summary *****/
//    @FXML
//    private HBox itemFilterHBox;
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
    @FXML
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
    private ChartRightPane componentChartRightPane;
    private ChartRightPane partAppraiserChartRightPane;
    private ChartRightPane xBarAppraiserChartRightPane;
    private ChartRightPane rangeAppraiserChartRightPane;
    private ChartRightPane rrByAppraiserChartRightPane;
    private ChartRightPane rrbyPartChartRightPane;
    private ChartOperateButton xBarAppraiserChartBtn;
    private ChartOperateButton rangeAppraiserChartBtn;
    @FXML
    private Label toleranceLbl;

    private String appKey;
    private String trailKey;
    private String[] CHART_COMPONENT_LABEL;
    private String[] CHART_COMPONENT_CATEGORY;
    private String[] CHART_OPERATE_NAME;
    private String[] GRR_SUMMARY_TITLE;
    private String[] GRR_ANOVA_TITLE;
    private String[] GRR_SOURCE_TITLE;
}
