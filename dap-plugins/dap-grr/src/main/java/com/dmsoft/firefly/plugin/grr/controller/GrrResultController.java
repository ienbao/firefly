package com.dmsoft.firefly.plugin.grr.controller;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.gui.components.chart.ChartOperatorUtils;
import com.dmsoft.firefly.gui.components.skin.ExpandableTableViewSkin;
import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.CommonResourceMassages;
import com.dmsoft.firefly.gui.components.utils.ImageUtils;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.gui.components.window.*;
import com.dmsoft.firefly.plugin.grr.charts.ChartOperateButton;
import com.dmsoft.firefly.plugin.grr.charts.ChartRightPane;
import com.dmsoft.firefly.plugin.grr.charts.LinearChart;
import com.dmsoft.firefly.plugin.grr.charts.SelectCallBack;
import com.dmsoft.firefly.plugin.grr.charts.data.ILineData;
import com.dmsoft.firefly.plugin.grr.charts.data.RuleLineData;
import com.dmsoft.firefly.plugin.grr.charts.data.VerticalCutLine;
import com.dmsoft.firefly.plugin.grr.dto.*;
import com.dmsoft.firefly.plugin.grr.handler.ParamKeys;
import com.dmsoft.firefly.plugin.grr.service.GrrConfigService;
import com.dmsoft.firefly.plugin.grr.utils.*;
import com.dmsoft.firefly.plugin.grr.utils.charts.ChartUtils;
import com.dmsoft.firefly.plugin.grr.utils.charts.LegendUtils;
import com.dmsoft.firefly.plugin.grr.utils.enums.Orientation;
import com.dmsoft.firefly.plugin.grr.dto.analysis.*;
import com.dmsoft.firefly.plugin.grr.model.GrrAnovaModel;
import com.dmsoft.firefly.plugin.grr.model.GrrSourceModel;
import com.dmsoft.firefly.plugin.grr.model.GrrSummaryModel;
import com.dmsoft.firefly.plugin.grr.model.ItemResultModel;
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
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.*;
import java.util.function.Function;
import org.springframework.stereotype.Component;

/**
 * Created by cherry on 2018/3/12.
 */
@Component
public class GrrResultController implements Initializable {
    private Set<String> parts = Sets.newLinkedHashSet();
    private Set<String> appraisers = Sets.newLinkedHashSet();
    private GrrSummaryModel summaryModel = new GrrSummaryModel();
    private ItemResultModel itemResultModel = new ItemResultModel();
    private GrrAnovaModel grrAnovaModel = new GrrAnovaModel();
    private GrrSourceModel grrSourceModel = new GrrSourceModel();
    private GrrMainController grrMainController;
//
//    @Autowired
//    private  GrrSummaryModel summaryModel;
//
//    @Autowired
//    private ItemResultModel itemResultModel;
//
//    @Autowired
//    private GrrAnovaModel grrAnovaModel;
//
//    @Autowired
//    private GrrSourceModel grrSourceModel;

    @Autowired
    private EnvService envService;

    @Autowired
    private UserPreferenceService userPreferenceService;

    @Autowired
    private GrrConfigService grrConfigService;

    @Autowired
    private IMessageManager iMessageManager;

    @Autowired
    private JobFactory jobFactory;

    @Autowired
    private JobManager jobManager;
//    private EnvService envService = RuntimeContext.getBean(EnvService.class);
//    private UserPreferenceService userPreferenceService = RuntimeContext.getBean(UserPreferenceService.class);
//    private GrrConfigService grrConfigService = RuntimeContext.getBean(GrrConfigService.class);
    private JsonMapper mapper = JsonMapper.defaultMapper();

    /**
     * Init grr main controller
     *
     * @param grrMainController grr main controller
     */
    public void init(GrrMainController grrMainController) {
        this.grrMainController = grrMainController;
//        this.initData();
//        this.initComponentsRender();
//        this.initComponentEvents();
//        this.initPerformanceSelected();
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
            this.iMessageManager.showWarnMsg(
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
                this.iMessageManager.showWarnMsg(
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
        JobContext context = this.jobFactory.createJobContext();
        WindowProgressTipController windowProgressTipController = WindowMessageFactory.createWindowProgressTip();
        context.put(ParamKeys.SEARCH_GRR_CONDITION_DTO, grrMainController.getSearchConditionDto());
        context.put(ParamKeys.SEARCH_VIEW_DATA_FRAME, grrMainController.getGrrDataFrame());
        if (testItemWithTypeDto != null && analyseSubResult) {
            context.put(ParamKeys.TEST_ITEM_WITH_TYPE_DTO, testItemWithTypeDto);
        }
        context.addJobEventListener(event -> windowProgressTipController.getTaskProgress().setProgress(event.getProgress()));
        windowProgressTipController.getCancelBtn().setOnAction(event -> {
            windowProgressTipController.setCancelingText();
            context.interruptBeforeNextJobHandler();
            if (context.isError() || context.getCurrentProgress() == 1.0) {
                windowProgressTipController.closeDialog();
            }
        });
        JobPipeline jobPipeline = this.jobManager.getPipeLine(ParamKeys.GRR_REFRESH_JOB_PIPELINE);
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
                        iMessageManager.showWarnMsg(
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
        this.jobManager.fireJobASyn(jobPipeline, context);

    }

    private void analyzeGrrSubResult(TestItemWithTypeDto testItemDto, String tolerance) {
        this.removeSubResultData();
        this.setToleranceValue(tolerance);
        JobContext context = this.jobFactory.createJobContext();
        WindowProgressTipController windowProgressTipController = WindowMessageFactory.createWindowProgressTip();
        context.put(ParamKeys.SEARCH_GRR_CONDITION_DTO, buildSearchConditionDto(testItemDto));
        context.put(ParamKeys.SEARCH_VIEW_DATA_FRAME, grrMainController.getGrrDataFrame());
        context.addJobEventListener(event -> windowProgressTipController.getTaskProgress().setProgress(event.getProgress()));
        windowProgressTipController.getCancelBtn().setOnAction(event -> {
            windowProgressTipController.setCancelingText();
            context.interruptBeforeNextJobHandler();
            if (context.isError() || context.getCurrentProgress() == 1.0) {
                windowProgressTipController.closeDialog();
            }
        });
        Stage stage1 = StageMap.getStage(CommonResourceMassages.COMPONENT_STAGE_WINDOW_PROGRESS_TIP);
        WindowPane windowPane = null;
        if (stage1.getScene().getRoot() instanceof WindowPane) {
            windowPane = (WindowPane) stage1.getScene().getRoot();
        }
        if (windowPane != null) {
            windowPane.getCloseBtn().setOnAction(event -> {
                windowProgressTipController.setCancelingText();
                context.interruptBeforeNextJobHandler();
            });
        }
        JobPipeline jobPipeline = this.jobManager.getPipeLine(ParamKeys.GRR_DETAIL_ANALYSIS_JOB_PIPELINE);
        jobPipeline.setCompleteHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                GrrDetailDto grrDetailDto = context.getParam(ParamKeys.GRR_DETAIL_DTO, GrrDetailDto.class);
                if (grrDetailDto == null) {
                    enableSubResultOperator(false);
                    iMessageManager.showWarnMsg(
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
        this.jobManager.fireJobASyn(jobPipeline, context);
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

    /**
     * Disable grr result operator
     */
    public void disableResultOperator() {
        summaryItemTf.setDisable(true);
        resultBasedCmb.setDisable(true);
        enableSubResultOperator(false);
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
            this.iMessageManager.showWarnMsg(
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
                Lists.newArrayList(parts));
        this.setBarChartPerformance();
        setControlChartData(grrDetailDto.getGrrDetailResultDto().getRangeAppraiserChartDto(),
                rangeAppraiserBp,
                rangeAppraiserChart,
                rangeAppraiserChartBtn,
                Lists.newArrayList(parts));
        this.setRangeChartPerformance();
        setScatterChartData(grrDetailDto.getGrrDetailResultDto().getRrbyAppraiserChartDto(), rrByAppraiserChart, rrbyAppraiserBp);
        setScatterChartData(grrDetailDto.getGrrDetailResultDto().getRrbyPartChartDto(), rrbyPartChart, rrbyPartBp);
        setControlChartXAxisLabel(Lists.newArrayList(appraisers), (NumberAxis) xBarAppraiserChart.getXAxis());
        setControlChartXAxisLabel(Lists.newArrayList(appraisers), (NumberAxis) rangeAppraiserChart.getXAxis());
        setScatterChartXAxisLabel(Lists.newArrayList(appraisers), (NumberAxis) rrByAppraiserChart.getXAxis());
        setScatterChartXAxisLabel(Lists.newArrayList(parts), (NumberAxis) rrbyPartChart.getXAxis());
        setAnovaAndSourceTb(grrDetailDto.getGrrDetailResultDto().getAnovaAndSourceResultDto());
    }

    private void setControlChartXAxisLabel(List<String> appraisers, NumberAxis xAxis) {
        xAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(xAxis) {
            @Override
            public String toString(final Number object) {
                int partSize = parts.size();
                double standardValue = (partSize + 1) / 2.0;

                if (object != null && object instanceof Double) {
                    int partQuotient = (int) (object.doubleValue() / partSize);
                    double standardQuotient = ((Double) object - standardValue) % partSize;
                    double partRemainder = object.doubleValue() % partSize;
                    if (standardQuotient % 1.0 == 0 && partRemainder == standardValue) {
                        boolean appraiserValid = DAPStringUtils.isNotEmpty(grrMainController.getSearchConditionDto().getAppraiser());
                        appraiserValid = appraiserValid && appraisers != null && appraisers.size() > partQuotient;
                        return appraiserValid ? appraisers.get(partQuotient)
                                : GrrFxmlAndLanguageUtils.getString(UIConstant.AXIS_LBL_PREFIX_APPRAISER) + (partQuotient + 1);
                    }
                }
                return "";
            }
        });
    }

    private void setScatterChartXAxisLabel(List<String> xAxisLabels, NumberAxis xAxis) {
        xAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(xAxis) {
            @Override
            public String toString(final Number object) {
                if (object != null && object instanceof Double && (Double) object % 1 == 0) {
                    int value = object.intValue();
                    boolean xAxisLabelValid = xAxisLabels != null;
                    xAxisLabelValid = xAxisLabelValid && !xAxisLabels.isEmpty();
                    xAxisLabelValid = xAxisLabelValid && xAxisLabels.size() >= value;
                    if (xAxisLabelValid && object.intValue() != 0) {
                        return xAxisLabels.get(object.intValue() - 1);
                    }
                }
                return "";
            }
        });
    }

    public void removeAllResultData() {
        summaryItemTf.setDisable(false);
        resultBasedCmb.setDisable(false);
        summaryModel.clearTableData();
        this.removeSubResultData();
    }

    /**
     * Toggle tick label visible
     *
     * @param flag it show or hide
     */
    public void toggleTickLabelsVisible(boolean flag) {
        componentChart.getXAxis().setTickLabelsVisible(flag);
        partAppraiserChart.getXAxis().setTickLabelsVisible(flag);
        xBarAppraiserChart.getXAxis().setTickLabelsVisible(flag);
        rangeAppraiserChart.getXAxis().setTickLabelsVisible(flag);
        rrbyPartChart.getXAxis().setTickLabelsVisible(flag);
        rrByAppraiserChart.getXAxis().setTickLabelsVisible(flag);
    }

    private void setComponentChart(GrrComponentCResultDto componentCResult) {
        if (componentCResult == null) {
            return;
        }
        XYChart.Series series1 = new XYChart.Series();
        XYChart.Series series2 = new XYChart.Series();
        XYChart.Series series3 = new XYChart.Series();
        Double[] array = getArrayValue(componentCResult);
        Double yMax = MathUtils.getNaNToZoreMax(array);
        Double yMin = MathUtils.getNaNToZoreMin(array);
        if (yMax == null || yMin == null) {
            return;
        }
        NumberAxis yAxis = (NumberAxis) componentChart.getYAxis();
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

        series1.getData().add(new XYChart.Data<>(chartComponentLabel[0],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getGrrContri()) ? 0 : componentCResult.getGrrContri()));
        series1.getData().add(new XYChart.Data<>(chartComponentLabel[1],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getRepeatContri()) ? 0 : componentCResult.getRepeatContri()));
        series1.getData().add(new XYChart.Data<>(chartComponentLabel[2],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getReprodContri()) ? 0 : componentCResult.getReprodContri()));
        series1.getData().add(new XYChart.Data<>(chartComponentLabel[3],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getPartContri()) ? 0 : componentCResult.getPartContri()));

        series2.getData().add(new XYChart.Data<>(chartComponentLabel[0],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getGrrVar()) ? 0 : componentCResult.getGrrVar()));
        series2.getData().add(new XYChart.Data<>(chartComponentLabel[1],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getRepeatVar()) ? 0 : componentCResult.getRepeatVar()));
        series2.getData().add(new XYChart.Data<>(chartComponentLabel[2],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getReprodVar()) ? 0 : componentCResult.getReprodVar()));
        series2.getData().add(new XYChart.Data<>(chartComponentLabel[3],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getPartVar()) ? 0 : componentCResult.getPartVar()));

        series3.getData().add(new XYChart.Data<>(chartComponentLabel[0],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getGrrTol()) ? 0 : componentCResult.getGrrTol()));
        series3.getData().add(new XYChart.Data<>(chartComponentLabel[1],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getRepeatTol()) ? 0 : componentCResult.getRepeatTol()));
        series3.getData().add(new XYChart.Data<>(chartComponentLabel[2],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getReprodTol()) ? 0 : componentCResult.getReprodTol()));
        series3.getData().add(new XYChart.Data<>(chartComponentLabel[3],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getPartTol()) ? 0 : componentCResult.getPartTol()));
        componentChart.getData().addAll(series1, series2, series3);
        for (int i = 0; i < chartComponentCategory.length; i++) {
            XYChart.Series series = (XYChart.Series) componentChart.getData().get(i);
            series.setName("% " + chartComponentCategory[i]);
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
        Double yMax = MathUtils.getMax(data);
        Double yMin = MathUtils.getMin(data);
        if (DAPStringUtils.isInfinityAndNaN(yMax) || DAPStringUtils.isInfinityAndNaN(yMin)) {
            return;
        }
        NumberAxis yAxis = (NumberAxis) partAppraiserChart.getYAxis();
        final double factor = 0.01;
        double reserve = (yMax - yMin) * factor;
        yAxis.setAutoRanging(false);
        yMax += reserve;
        yMin -= reserve;
        Map<String, Object> yAxisRangeData = ChartOperatorUtils.getAdjustAxisRangeData(yMax, yMin, (int) Math.ceil(yMax - yMin));
        double newYMin = (Double) yAxisRangeData.get(ChartOperatorUtils.KEY_MIN);
        double newYMax = (Double) yAxisRangeData.get(ChartOperatorUtils.KEY_MAX);
        yAxis.setLowerBound(newYMin);
        yAxis.setUpperBound(newYMax);
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
                                     List<String> parts) {

        int partCount = parts.size();
        Double[] x = chartData.getX();
        Double[] y = chartData.getY();
        Double[] ruleData = new Double[]{chartData.getUcl(), chartData.getCl(), chartData.getLcl()};
        Double yMax = MathUtils.getMax(y, ruleData);
        Double yMin = MathUtils.getMin(y, ruleData);
        Double xMax = MathUtils.getMax(x);
        Double xMin = MathUtils.getMin(x);
        if (DAPStringUtils.isInfinityAndNaN(yMax) || DAPStringUtils.isInfinityAndNaN(yMin)) {
            return;
        }
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        final double factor = 0.01;
        double reserve = (yMax - yMin) * factor;
        yAxis.setAutoRanging(false);
        yMax += reserve;
        yMin -= reserve;
        Map<String, Object> yAxisRangeData = ChartOperatorUtils.getAdjustAxisRangeData(yMax, yMin, (int) Math.ceil(yMax - yMin));
        double newYMin = (Double) yAxisRangeData.get(ChartOperatorUtils.KEY_MIN);
        double newYMax = (Double) yAxisRangeData.get(ChartOperatorUtils.KEY_MAX);
        yAxis.setLowerBound(newYMin);
        yAxis.setUpperBound(newYMax);
        xAxis.setLowerBound(xMin - UIConstant.X_FACTOR);
        xAxis.setUpperBound(xMax + UIConstant.X_FACTOR);
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
            RuleLineData uclLineData = new RuleLineData(chartOperateName[0], chartData.getUcl());
            uclLineData.setColor(Color.rgb(102, 102, 102));
            uclLineData.setLineClass("dashed2-line");
            horizontalLineData.add(uclLineData);
        }
        if (!DAPStringUtils.isInfinityAndNaN(chartData.getCl())) {
            RuleLineData clLineData = new RuleLineData(chartOperateName[1], chartData.getCl());
            clLineData.setLineClass("solid-line");
            horizontalLineData.add(clLineData);
        }
        if (!DAPStringUtils.isInfinityAndNaN(chartData.getLcl())) {
            RuleLineData lclLineData = new RuleLineData(chartOperateName[2], chartData.getLcl());
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
                return oneLineData.getName() + "=" + DAPStringUtils.formatDouble(oneLineData.getValue(), digNum);
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
        Double yMax = MathUtils.getMax(y, clY);
        Double yMin = MathUtils.getMin(y, clY);
        Double xMin = MathUtils.getMin(x, clX);
        Double xMax = MathUtils.getMax(x, clX);
        if (DAPStringUtils.isInfinityAndNaN(yMax)
                || DAPStringUtils.isInfinityAndNaN(yMin)
                || DAPStringUtils.isInfinityAndNaN(xMax)
                || DAPStringUtils.isInfinityAndNaN(xMin)) {
            return;
        }
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        final double factor = 0.01;
        Double reserve = (yMax - yMin) * factor;
        yAxis.setAutoRanging(false);
        yMax += reserve;
        yMin -= reserve;
        Map<String, Object> yAxisRangeData = ChartOperatorUtils.getAdjustAxisRangeData(yMax, yMin, (int) Math.ceil(yMax - yMin));
        double newYMin = (Double) yAxisRangeData.get(ChartOperatorUtils.KEY_MIN);
        double newYMax = (Double) yAxisRangeData.get(ChartOperatorUtils.KEY_MAX);
        xAxis.setLowerBound(xMin - UIConstant.X_FACTOR);
        xAxis.setUpperBound(xMax + UIConstant.X_FACTOR);
        yAxis.setLowerBound(newYMin);
        yAxis.setUpperBound(newYMax);
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
        Legend legend = LegendUtils.buildLegend(chart.getData(), "chart-line-symbol", "line-legend-symbol");
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
        this.toggleTickLabelsVisible(true);
    }

    private void initComponents() {
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
                Orientation.BOTTOMLEFT);
        rangeAppraiserChartBtn = new ChartOperateButton(true,
                Orientation.BOTTOMLEFT);
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
        final double tickUnit = 0.5;
        xAxis.setTickUnit(tickUnit);
        xAxis.setMinorTickVisible(false);
        xAxis.setTickMarkVisible(false);
        yAxis.setMinorTickVisible(false);
        yAxis.setTickMarkVisible(false);
        yAxis.setAutoRanging(false);
        xAxis.setAutoRanging(false);
        LinearChart chart = new LinearChart(xAxis, yAxis);
        chart.setVerticalGridLinesVisible(false);
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
        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);
        xAxis.setTickUnit(1);
        LineChart chart = new LineChart(xAxis, yAxis);
        chart.setLegendVisible(false);
        return chart;
    }

    private void initComponentsRender() {
        summaryItemTf.getTextField().getStyleClass().add("grr-summary-item-tf");

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
        xBarAppraiserChart.setVerticalGridLinesVisible(false);

        rangeAppraiserChart.setHorizontalZeroLineVisible(false);
        rangeAppraiserChart.setVerticalZeroLineVisible(false);
        rangeAppraiserChart.setVerticalGridLinesVisible(false);

        rrByAppraiserChart.setHorizontalZeroLineVisible(false);
        rrByAppraiserChart.setVerticalZeroLineVisible(false);
        rrbyPartChart.setHorizontalZeroLineVisible(false);
        rrbyPartChart.setVerticalZeroLineVisible(false);
//        componentChart.setVerticalGridLinesVisible(false);
//        componentChart.setHorizontalGridLinesVisible(false);
//        partAppraiserChart.setHorizontalGridLinesVisible(false);
//        partAppraiserChart.setVerticalGridLinesVisible(false);
//        rangeAppraiserChart.setHorizontalGridLinesVisible(false);
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
        xBarAppraiserChartBtn.getStyleClass().add("grr-xBarAppraiserChartBtn");
        rangeAppraiserChartBtn.getStyleClass().add("grr-xBarAppraiserChartBtn");
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
        for (String operateName : chartOperateName) {
            if (!xBarAppraiserChartBtn.getSelectedSets().contains(operateName)) {
                barHiddenLines.add(operateName);
            }
        }
        xBarAppraiserChart.hiddenValueMarkers(barHiddenLines);
    }

    private void setRangeChartPerformance() {
        List<String> rangeHiddenLines = Lists.newArrayList();
        for (String operateName : chartOperateName) {
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
        summaryModel.initColumn(Lists.newArrayList(grrSummaryTitle));
        grrAnovaModel.initColumn(Lists.newArrayList(grrAnovaTitle));
        grrSourceModel.initColumn(buildSourceTbColumn());
        TableViewWrapper.decorate(summaryTb, summaryModel);
        TableViewWrapper.decorate(anovaTb, grrAnovaModel);
        TableViewWrapper.decorate(sourceTb, grrSourceModel);
        xBarAppraiserChartBtn.setListViewData(Lists.newArrayList(chartOperateName));
        rangeAppraiserChartBtn.setListViewData(Lists.newArrayList(chartOperateName));
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
        barOperatePerformance.put(UIConstant.CHART_PERFORMANCE_KEY_OPERATE, Lists.newArrayList(chartOperateName));
        rangeOperatePerformance.put(UIConstant.CHART_PERFORMANCE_KEY_OPERATE, Lists.newArrayList(chartOperateName));
        performanceMap.put(barChartName, barOperatePerformance);
        performanceMap.put(rangeChartName, rangeOperatePerformance);
        UserPreferenceDto userPreferenceDto = new UserPreferenceDto();
        userPreferenceDto.setUserName(envService.getUserName());
        userPreferenceDto.setCode(UIConstant.CHART_PERFORMANCE_CODE);
        userPreferenceDto.setValue(performanceMap);
        userPreferenceService.updatePreference(userPreferenceDto);
        xBarAppraiserChartBtn.setSelectedSets(Sets.newHashSet(chartOperateName));
        rangeAppraiserChartBtn.setSelectedSets(Sets.newHashSet(chartOperateName));
    }

    private List<String> buildSourceTbColumn() {
        final double defaultCoverage = 6.0;
        List<String> tableColumns = Lists.newArrayList();
        GrrConfigDto grrConfigDto = grrConfigService.findGrrConfig();
        double coverage = grrConfigDto == null || grrConfigDto.getCoverage() == null ? defaultCoverage : grrConfigDto.getCoverage();
        for (String name : grrSourceTitle) {
            name = name.equals(grrSourceTitle[2]) ? name + " " + coverage : name;
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
            this.iMessageManager.showWarnMsg(
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
        chartComponentLabel = new String[]{
                GrrFxmlAndLanguageUtils.getString(UIConstant.COMPONENTS_GAGE_R),
                GrrFxmlAndLanguageUtils.getString(UIConstant.COMPONENTS_REPEATABILITY),
                GrrFxmlAndLanguageUtils.getString(UIConstant.COMPONENTS_REPRODUCIBILITY),
                GrrFxmlAndLanguageUtils.getString(UIConstant.COMPONENTS_PART)};
        chartComponentCategory = new String[]{
                GrrFxmlAndLanguageUtils.getString(UIConstant.COMPONENTS_CONTRIBUTION),
                GrrFxmlAndLanguageUtils.getString(UIConstant.COMPONENTS_VARIATION),
                GrrFxmlAndLanguageUtils.getString(UIConstant.COMPONENTS_TOLERANCE)};
        chartOperateName = new String[]{
                GrrFxmlAndLanguageUtils.getString(UIConstant.CHART_LINE_NAME_UCL),
                GrrFxmlAndLanguageUtils.getString(UIConstant.CHART_LINE_NAME_AVG),
                GrrFxmlAndLanguageUtils.getString(UIConstant.CHART_LINE_NAME_LCL)};
        grrSummaryTitle = new String[]{
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SUMMARY_TITLE_TESTITEM),
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SUMMARY_TITLE_LSL),
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SUMMARY_TITLE_USL),
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SUMMARY_TITLE_TOLERANCE),
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SUMMARY_TITLE_REPEATABILITY),
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SUMMARY_TITLE_REPRODUCIBILITY),
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SUMMARY_TITLE_GAUGE)};
        grrAnovaTitle = new String[]{
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_ANOVA_TITLE_SOURCE),
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_ANOVA_TITLE_DF),
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_ANOVA_TITLE_SS),
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_ANOVA_TITLE_MS),
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_ANOVA_TITLE_F),
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_ANOVA_TITLE_PROB)};
        grrSourceTitle = new String[]{
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
    private String[] chartComponentLabel;
    private String[] chartComponentCategory;
    private String[] chartOperateName;
    private String[] grrSummaryTitle;
    private String[] grrAnovaTitle;
    private String[] grrSourceTitle;
}
