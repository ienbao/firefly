/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.gui.components.utils.CommonResourceMassages;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.gui.components.window.*;
import com.dmsoft.firefly.plugin.spc.dto.*;
import com.dmsoft.firefly.plugin.spc.handler.ParamKeys;
import com.dmsoft.firefly.plugin.spc.service.SpcSettingService;
import com.dmsoft.firefly.plugin.spc.utils.*;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.job.core.*;
import com.dmsoft.firefly.sdk.message.IMessageManager;
import com.dmsoft.firefly.sdk.utils.FilterUtils;
import com.google.common.collect.Lists;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Created by Ethan.Yang on 2018/2/2.
 */
public class SpcMainController implements Initializable {

    private static Logger logger = LoggerFactory.getLogger(SpcMainController.class);
    @FXML
    private Button resetBtn;
    @FXML
    private Button printBtn;
    @FXML
    private Button exportBtn;
    @FXML
    private Button chooseBtn;
    @FXML
    private SpcItemController spcItemController;
    @FXML
    private StatisticalResultController statisticalResultController;
    @FXML
    private ViewDataController viewDataController;
    @FXML
    private ChartResultController chartResultController;
    private SearchDataFrame dataFrame;
    private SpcAnalysisConfigDto analysisConfigDto;
    private List<SearchConditionDto> initSearchConditionDtoList;
    private SpcSettingDto spcSettingDto;
    private SpcSettingService spcSettingService = RuntimeContext.getBean(SpcSettingService.class);
    private EnvService envService = RuntimeContext.getBean(EnvService.class);
    private List<String> lastViewDataRowKeyList;
    private List<String> unSelectRowKeyList = Lists.newArrayList();

    private List<SearchConditionDto> timerSearchConditionDtoList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.spcItemController.init(this);
        this.statisticalResultController.init(this);
        this.viewDataController.init(this);
        this.chartResultController.init(this);
        this.initBtnIcon();
        this.initComponentEvent();
        this.setDisable(true);
    }

    /**
     * set disable
     *
     * @param disable disable
     */
    public void setDisable(boolean disable) {
        resetBtn.setDisable(disable);
        chooseBtn.setDisable(disable);
        if (disable) {
            chooseBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/icon_choose_one_gray.png")));
        } else {
            chooseBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/icon_choose_one_white.png")));
        }
    }

    /**
     * set statistical result data
     *
     * @param list         the data list
     * @param isTimer      isTimer
     * @param selectRowKey selectRowKey
     */
    public void setStatisticalResultData(List<SpcStatisticalResultAlarmDto> list, List<String> selectRowKey, boolean isTimer) {
        statisticalResultController.setTimerStatisticalResultTableData(list, selectRowKey, isTimer);
    }

    /**
     * timer refresh statistical result data
     *
     * @param spcStatsDtoList the data list
     */
    public void timerRefreshStatisticalResultData(List<SpcStatisticalResultAlarmDto> spcStatsDtoList) {
        statisticalResultController.refreshStatisticalResult(spcStatsDtoList);
    }

    /**
     * get select search condition
     *
     * @return the list of searchConditionDto
     */
    public List<SearchConditionDto> getSelectSearchCondition() {
        List<SearchConditionDto> searchConditionDtoList = buildRefreshSearchConditionData(statisticalResultController.getSelectStatsData());
        return searchConditionDtoList;
    }

    /**
     * set statistical result data
     *
     * @param list the data list
     */
    public void setStatisticalResultData(List<SpcStatisticalResultAlarmDto> list) {
        statisticalResultController.setStatisticalResultTableData(list);
    }

    /**
     * init spc chart data
     *
     * @param spcChartDtoList the list of chart data
     */
    public void setSpcChartData(List<SpcChartDto> spcChartDtoList) {
        chartResultController.initSpcChartData(spcChartDtoList);
    }

    /**
     * method to set view data in timer model
     *
     * @param chartSearchConditionDtoList       list of chart search condition
     * @param statisticalSearchConditionDtoList list of stats search condition dto
     */
    public void setTimerViewData(List<SearchConditionDto> chartSearchConditionDtoList, List<SearchConditionDto> statisticalSearchConditionDtoList) {
        //set view data
        SearchDataFrame viewDataFrame = buildSubSearchDataFrame(chartSearchConditionDtoList);
        viewDataController.setViewData(viewDataFrame, viewDataFrame.getSearchedRowKey(), statisticalSearchConditionDtoList, true);

    }

    /**
     * get Cache color
     *
     * @return color Cache
     */
    public Map<String, Color> getColorCache() {
        return statisticalResultController.getColorCache();
    }

    /**
     * remove row from dataFrame
     *
     * @param rowKey row key
     */
    public void removeDataFrameRow(String rowKey) {
        if (dataFrame == null) {
            return;
        }
        dataFrame.removeRows(Lists.newArrayList(rowKey));
    }


    /**
     * clear analysis data
     *
     */
    public void clearAnalysisSubShowData() {
        chartResultController.clearChartData();
        viewDataController.clearViewData();
        unSelectRowKeyList.clear();
        lastViewDataRowKeyList = null;
    }

    /**
     * clear all analysis data
     */
    public void clearAnalysisData() {
        statisticalResultController.clearStatisticalResultData();
        clearAnalysisSubShowData();
        this.setDisable(true);
    }

    /**
     * clear chart data
     */
    public void clearChartResultData() {
        chartResultController.clearChartData();
    }

    /**
     * change chart color
     *
     * @param key   key
     * @param color color
     */
    public void updateChartColor(String key, javafx.scene.paint.Color color) {
        chartResultController.updateChartColor(key, color);
    }

    /**
     * stick chart layer
     *
     * @param key key
     */
    public void stickChartLayer(String key) {
        chartResultController.stickLayerToUniqueKey(key);
    }

    /**
     * set view data focus row data
     *
     * @param rowKey row key
     */
    public void setViewDataFocusRowData(String rowKey) {
        viewDataController.setFocusRowData(rowKey);
    }

    private void initComponentEvent() {
        resetBtn.setOnAction(event -> getResetBtnEvent());
        printBtn.setOnAction(event -> getExportBtnEvent());
        exportBtn.setOnAction(event -> getExportBtnEvent());
        chooseBtn.setOnAction(event -> getChooseBtnEvent());
    }

    @SuppressWarnings("unchecked")
    private void getResetBtnEvent() {
        WindowProgressTipController windowProgressTipController = WindowMessageFactory.createWindowProgressTip();
        JobContext context = RuntimeContext.getBean(JobFactory.class).createJobContext();
        context.put(ParamKeys.SPC_SETTING_DTO, spcSettingDto);
        context.put(ParamKeys.SEARCH_CONDITION_DTO_LIST, initSearchConditionDtoList);
        context.put(ParamKeys.SPC_ANALYSIS_CONFIG_DTO, analysisConfigDto);
        context.put(ParamKeys.SEARCH_DATA_FRAME, dataFrame);
        context.addJobEventListener(event -> windowProgressTipController.getTaskProgress().setProgress(event.getProgress()));
        windowProgressTipController.getCancelBtn().setOnAction(event -> {
            windowProgressTipController.setCancelingText();
            context.interruptBeforeNextJobHandler();
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
        JobPipeline jobPipeline = RuntimeContext.getBean(JobManager.class).getPipeLine(ParamKeys.SPC_RESET_JOB_PIPELINE);
        jobPipeline.setCompleteHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                SpcRefreshJudgeUtil.newInstance().setViewDataSelectRowKeyListCache(null);
                SpcRefreshJudgeUtil.newInstance().setStatisticalSelectRowKeyListCache(null);
                List<SpcStatisticalResultAlarmDto> spcStatisticalResultAlarmDtoList = (List<SpcStatisticalResultAlarmDto>) context.get(ParamKeys.SPC_STATISTICAL_RESULT_ALARM_DTO_LIST);
                setStatisticalResultData(spcStatisticalResultAlarmDtoList, null, false);
                clearAnalysisSubShowData();
            }
        });
        jobPipeline.setErrorHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                logger.error(context.getError().toString());
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

    private void getExportBtnEvent() {
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = SpcFxmlAndLanguageUtils.getLoaderFXML("view/spc_export.fxml");
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("spcExport", SpcFxmlAndLanguageUtils.getString("SPC_EXPORT"), root, getClass().getClassLoader().getResource("css/spc_app.css").toExternalForm());
            SpcLeftConfigDto leftConfigDto = spcItemController.getCurrentConfigData();
            ((SpcExportController) fxmlLoader.getController()).initSpcExportLeftConfig(leftConfigDto);
            stage.setResizable(false);
            stage.toFront();
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getChooseBtnEvent() {
        if (statisticalResultController.hasErrorEditCell()) {
            WindowMessageController messageController = WindowMessageFactory.createWindowMessageHasOkAndCancel(SpcFxmlAndLanguageUtils.getString(ResourceMassages.TIP_WARN_HEADER),
                    SpcFxmlAndLanguageUtils.getString(ResourceMassages.SPC_STATISTICAL_ERROR_EDIT_MESSAGE));
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
                    refreshEvent();
                    return false;
                }
            });
        } else {
            refreshEvent();
        }
    }

    private void refreshEvent() {
        List<String> currentStatisticalSelectRowKeyList = statisticalResultController.getSelectStatisticalRowKey();
        List<String> currentViewDataSelectRowKeyList = viewDataController.getSelectedRowKeys();
        List<String> statisticalModifyRowKeyList = statisticalResultController.getEidtStatisticalRowKey();

        SpcRefreshJudgeUtil spcRefreshJudgeUtil = SpcRefreshJudgeUtil.newInstance();
        SpcRefreshJudgeUtil.RefreshType refreshType = spcRefreshJudgeUtil.refreshJudge(currentStatisticalSelectRowKeyList, currentViewDataSelectRowKeyList, statisticalModifyRowKeyList);

        switch (refreshType) {
            case NOT_NEED_REFRESH:
                RuntimeContext.getBean(IMessageManager.class).showWarnMsg(
                        SpcFxmlAndLanguageUtils.getString(UIConstant.UI_MESSAGE_TIP_WARNING_TITLE),
                        SpcFxmlAndLanguageUtils.getString("EXCEPTION_SPC_NO_REFRESH_RESULT"));
                if (currentStatisticalSelectRowKeyList.size() == 0) {
                    chartResultController.clearChartData();
                    viewDataController.clearViewData();
                    lastViewDataRowKeyList = null;
                    spcRefreshJudgeUtil.setViewDataIsBlank(true);
                    spcRefreshJudgeUtil.setStatisticalSelectRowKeyListCache(currentStatisticalSelectRowKeyList);
                }
                break;
            case REFRESH_STATISTICAL_RESULT:
                //refresh statistical result
                this.refreshStatisticalResult(spcRefreshJudgeUtil);
                break;
            case REFRESH_CHART_RESULT:
                //only refresh chart
                this.refreshChartResult(spcRefreshJudgeUtil);
                spcRefreshJudgeUtil.setViewDataIsBlank(false);
                break;
            case REFRESH_ALL_ANALYSIS_RESULT:
                this.refreshAllAnalysisResult(spcRefreshJudgeUtil);
                spcRefreshJudgeUtil.setViewDataIsBlank(false);
                break;
            default:
                break;
        }
    }

    private void initBtnIcon() {
        resetBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_reset_normal.png")));
        printBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_print_normal.png")));
        exportBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_export_normal.png")));
        chooseBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/icon_choose_one_white.png")));
        TooltipUtil.installNormalTooltip(resetBtn, SpcFxmlAndLanguageUtils.getString("SPC_RESET_BTN_TOOLTIP"));
        TooltipUtil.installNormalTooltip(printBtn, SpcFxmlAndLanguageUtils.getString("SPC_PRINT_BTN_TOOLTIP"));
        TooltipUtil.installNormalTooltip(exportBtn, SpcFxmlAndLanguageUtils.getString("SPC_EXPORT_BTN_TOOLTIP"));
        TooltipUtil.installNormalTooltip(chooseBtn, SpcFxmlAndLanguageUtils.getString("SPC_REFRESH_BTN_TOOLTIP"));
    }

    public SearchDataFrame getDataFrame() {
        return dataFrame;
    }

    public void setDataFrame(SearchDataFrame dataFrame) {
        this.dataFrame = dataFrame;
    }

    public SpcAnalysisConfigDto getAnalysisConfigDto() {
        return analysisConfigDto;
    }

    public void setAnalysisConfigDto(SpcAnalysisConfigDto analysisConfigDto) {
        this.analysisConfigDto = analysisConfigDto;
    }

    public List<SearchConditionDto> getInitSearchConditionDtoList() {
        return initSearchConditionDtoList;
    }

    public void setInitSearchConditionDtoList(List<SearchConditionDto> initSearchConditionDtoList) {
        this.initSearchConditionDtoList = initSearchConditionDtoList;
    }

    private List<SearchConditionDto> buildRefreshSearchConditionData(List<SpcStatisticalResultAlarmDto> spcStatsDtoList) {
        List<SearchConditionDto> searchConditionDtoList = Lists.newArrayList();
        if (spcStatsDtoList == null) {
            return searchConditionDtoList;
        }
        for (SpcStatisticalResultAlarmDto spcStatsDto : spcStatsDtoList) {
            SearchConditionDto searchConditionDto = new SearchConditionDto();
            searchConditionDto.setKey(spcStatsDto.getKey());
            searchConditionDto.setItemName(spcStatsDto.getItemName());
            searchConditionDto.setCondition(spcStatsDto.getCondition());
            searchConditionDto.setCusUsl(String.valueOf(spcStatsDto.getStatisticalAlarmDtoMap().get(UIConstant.SPC_SR_ALL[8]).getValue()));
            searchConditionDto.setCusLsl(String.valueOf(spcStatsDto.getStatisticalAlarmDtoMap().get(UIConstant.SPC_SR_ALL[7]).getValue()));
            searchConditionDtoList.add(searchConditionDto);
        }
        return searchConditionDtoList;
    }

    private SearchDataFrame buildSubSearchDataFrame(List<String> rowKeyList, List<SearchConditionDto> searchConditionDtoList) {
        if (dataFrame == null || searchConditionDtoList == null) {
            return null;
        }
        List<String> testItemNameList = Lists.newArrayList();
        List<String> timeKeys = envService.findActivatedTemplate().getTimePatternDto().getTimeKeys();
        String timePattern = envService.findActivatedTemplate().getTimePatternDto().getPattern();
        FilterUtils filterUtils = new FilterUtils(timeKeys, timePattern);
        for (SearchConditionDto searchConditionDto : searchConditionDtoList) {
            if (!testItemNameList.contains(searchConditionDto.getItemName())) {
                testItemNameList.add(searchConditionDto.getItemName());
            }
            String condition = searchConditionDto.getCondition();
            Set<String> conditionTestItemSet = filterUtils.parseItemNameFromConditions(condition);
            for (String conditionTestItem : conditionTestItemSet) {
                if (!testItemNameList.contains(conditionTestItem)) {
                    testItemNameList.add(conditionTestItem);
                }
            }
        }
        return dataFrame.subDataFrame(rowKeyList, testItemNameList);
    }

    private SearchDataFrame buildSubSearchDataFrame(List<SearchConditionDto> searchConditionDtoList) {
        if (dataFrame == null || searchConditionDtoList == null) {
            return null;
        }
        List<String> testItemNameList = Lists.newArrayList();
        List<String> searchCondition = Lists.newArrayList();
        List<String> timeKeys = envService.findActivatedTemplate().getTimePatternDto().getTimeKeys();
        String timePattern = envService.findActivatedTemplate().getTimePatternDto().getPattern();
        FilterUtils filterUtils = new FilterUtils(timeKeys, timePattern);
        for (SearchConditionDto searchConditionDto : searchConditionDtoList) {
            if (!testItemNameList.contains(searchConditionDto.getItemName())) {
                testItemNameList.add(searchConditionDto.getItemName());
            }
            String condition = searchConditionDto.getCondition();
            Set<String> conditionTestItemSet = filterUtils.parseItemNameFromConditions(condition);
            for (String conditionTestItem : conditionTestItemSet) {
                if (!testItemNameList.contains(conditionTestItem)) {
                    testItemNameList.add(conditionTestItem);
                }
            }

            if (!searchCondition.contains(condition)) {
                searchCondition.add(condition);
            }
        }
        return dataFrame.subDataFrame(dataFrame.getSearchRowKey(searchCondition), testItemNameList);
    }

    private boolean resultSelectIsChange(List<String> newList, List<String> oldList) {
        if (oldList == null) {
            return newList.size() != 0;
        }
        if (newList.size() != oldList.size()) {
            return true;
        }
        for (String oldRowKey : oldList) {
            if (!newList.contains(oldRowKey)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private void refreshStatisticalResult(SpcRefreshJudgeUtil spcRefreshJudgeUtil) {
        WindowProgressTipController windowProgressTipController = WindowMessageFactory.createWindowProgressTip();
        JobContext context = RuntimeContext.getBean(JobFactory.class).createJobContext();
        List<String> currentStatisticalSelectRowKeyList = spcRefreshJudgeUtil.getCurrentStatisticalSelectRowKeyList();
        List<String> viewDataSelectRowKeyListCache = spcRefreshJudgeUtil.getViewDataSelectRowKeyListCache();
        if (currentStatisticalSelectRowKeyList.size() == 0) {
            chartResultController.clearChartData();
            viewDataController.clearViewData();
            lastViewDataRowKeyList = null;
            spcRefreshJudgeUtil.setViewDataIsBlank(true);
        }
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
        List<String> rowKeyList = viewDataSelectRowKeyListCache == null ? dataFrame.getSearchedRowKey() : viewDataSelectRowKeyListCache;
        List<SpcStatisticalResultAlarmDto> editRowDataList = statisticalResultController.getEditRowStatsData();
        List<SearchConditionDto> searchConditionDtoList = buildRefreshSearchConditionData(editRowDataList);
        SearchDataFrame subDataFrame = buildSubSearchDataFrame(rowKeyList, searchConditionDtoList);

        context.put(ParamKeys.SPC_SETTING_DTO, spcSettingDto);
        context.put(ParamKeys.SEARCH_CONDITION_DTO_LIST, searchConditionDtoList);
        context.put(ParamKeys.SPC_ANALYSIS_CONFIG_DTO, analysisConfigDto);
        context.put(ParamKeys.SEARCH_DATA_FRAME, subDataFrame);
        context.addJobEventListener(event -> windowProgressTipController.getTaskProgress().setProgress(event.getProgress()));
        windowProgressTipController.getCancelBtn().setOnAction(event -> {
            windowProgressTipController.setCancelingText();
            context.interruptBeforeNextJobHandler();
        });

        JobPipeline jobPipeline = RuntimeContext.getBean(JobManager.class).getPipeLine(ParamKeys.SPC_REFRESH_STATISTICAL_JOB_PIPELINE);
        jobPipeline.setCompleteHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                List<SpcStatisticalResultAlarmDto> spcStatisticalResultAlarmDtoList = (List<SpcStatisticalResultAlarmDto>) context.get(ParamKeys.SPC_STATISTICAL_RESULT_ALARM_DTO_LIST);
                statisticalResultController.refreshStatisticalResult(spcStatisticalResultAlarmDtoList);

                if (editRowDataList != null && editRowDataList.size() != 0) {
                    List<SpcStatisticalResultAlarmDto> allRowDataList = statisticalResultController.getAllRowStatsData();
                    List<SearchConditionDto> statisticalSearchConditionDtoList = buildRefreshSearchConditionData(allRowDataList);
                    viewDataController.updateStatisticalSearchCondition(statisticalSearchConditionDtoList);
                }
                windowProgressTipController.closeDialog();
                logger.info("Refresh Spc statistical data finish.");
            }
        });
        jobPipeline.setErrorHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                logger.error(context.getError().toString());
                windowProgressTipController.updateFailProgress(context.getError().toString());
            }
        });
        jobPipeline.setInterruptHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                windowProgressTipController.closeDialog();
            }
        });
        logger.info("Start refresh Spc statistical data.");
        RuntimeContext.getBean(JobManager.class).fireJobASyn(jobPipeline, context);
    }

    @SuppressWarnings("unchecked")
    private void refreshChartResult(SpcRefreshJudgeUtil spcRefreshJudgeUtil) {
        WindowProgressTipController windowProgressTipController = WindowMessageFactory.createWindowProgressTip();
        List<String> currentStatisticalSelectRowKeyList = spcRefreshJudgeUtil.getCurrentStatisticalSelectRowKeyList();

        List<String> viewDataSelectRowKeyListCache = spcRefreshJudgeUtil.getViewDataSelectRowKeyListCache();

        List<SearchConditionDto> searchConditionDtoList = buildRefreshSearchConditionData(statisticalResultController.getSelectStatsData());
        if (searchConditionDtoList.size() == 0) {
            return;
        }
        if (spcItemController.isTimer()) {
            timerSearchConditionDtoList = searchConditionDtoList;
        }

        //get last select row key
        SearchDataFrame viewDataFrame = buildSubSearchDataFrame(searchConditionDtoList);
        List<String> rowKeyList = Lists.newArrayList();
        if (viewDataSelectRowKeyListCache == null) {
            rowKeyList = viewDataFrame.getSearchedRowKey();
        } else {
            if ((lastViewDataRowKeyList == null || lastViewDataRowKeyList.size() == 0) && unSelectRowKeyList.size() == 0) {
                rowKeyList = viewDataFrame.getSearchedRowKey();
            } else {
                for (String key : viewDataFrame.getSearchedRowKey()) {
                    if ((!lastViewDataRowKeyList.contains(key) || viewDataSelectRowKeyListCache.contains(key)) && !rowKeyList.contains(key) && !unSelectRowKeyList.contains(key)) {
                        rowKeyList.add(key);
                    }
                }
            }
        }
        lastViewDataRowKeyList = viewDataFrame.getAllRowKeys();

//        List<String> rowKeyList = viewDataSelectRowKeyListCache == null ? dataFrame.getSearchedRowKey() : viewDataSelectRowKeyListCache;

        JobContext context = RuntimeContext.getBean(JobFactory.class).createJobContext();
        context.put(ParamKeys.SPC_SETTING_DTO, spcSettingDto);
        context.put(ParamKeys.SEARCH_CONDITION_DTO_LIST, searchConditionDtoList);
        context.put(ParamKeys.SPC_ANALYSIS_CONFIG_DTO, analysisConfigDto);

        SearchDataFrame subDataFrame = buildSubSearchDataFrame(rowKeyList, searchConditionDtoList);
        context.put(ParamKeys.SEARCH_DATA_FRAME, subDataFrame);
        context.addJobEventListener(event -> windowProgressTipController.getTaskProgress().setProgress(event.getProgress()));
        windowProgressTipController.getCancelBtn().setOnAction(event -> {
            windowProgressTipController.setCancelingText();
            context.interruptBeforeNextJobHandler();
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

        JobPipeline jobPipeline = RuntimeContext.getBean(JobManager.class).getPipeLine(ParamKeys.SPC_REFRESH_CHART_JOB_PIPELINE);
        jobPipeline.setCompleteHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                Platform.runLater(() -> {
                    logger.info("Start loading chart data.");
                    chartResultController.initSpcChartData((List<SpcChartDto>) context.get(ParamKeys.SPC_CHART_DTO_LIST));
                    logger.info("Loading chart data finish.");

                    List<SpcStatisticalResultAlarmDto> allRowDataList = statisticalResultController.getAllRowStatsData();
                    List<SearchConditionDto> statisticalSearchConditionDtoList = buildRefreshSearchConditionData(allRowDataList);

                    viewDataController.setViewData(viewDataFrame, subDataFrame.getAllRowKeys(), statisticalSearchConditionDtoList, spcItemController.isTimer());
                    spcRefreshJudgeUtil.setStatisticalSelectRowKeyListCache(currentStatisticalSelectRowKeyList);
                    spcRefreshJudgeUtil.setViewDataSelectRowKeyListCache(subDataFrame.getAllRowKeys());

                    windowProgressTipController.closeDialog();
                    logger.info("Analysis Spc chart finish.");
                });
            }
        });
        jobPipeline.setErrorHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                logger.error(context.getError().getMessage());
                windowProgressTipController.updateFailProgress(context.getError().toString());
            }
        });
        jobPipeline.setInterruptHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                windowProgressTipController.closeDialog();
            }
        });
        logger.info("Start analysis Spc char.");
        RuntimeContext.getBean(JobManager.class).fireJobASyn(jobPipeline, context);
    }

    @SuppressWarnings("unchecked")
    private void refreshAllAnalysisResult(SpcRefreshJudgeUtil spcRefreshJudgeUtil) {
        List<String> currentStatisticalSelectRowKeyList = spcRefreshJudgeUtil.getCurrentStatisticalSelectRowKeyList();
        List<String> currentViewDataSelectRowKeyList = spcRefreshJudgeUtil.getCurrentViewDataSelectRowKeyList();

        List<String> allUnSelectList = Lists.newArrayList();
        allUnSelectList.addAll(unSelectRowKeyList);
        for (String rowKey : allUnSelectList) {
            if (currentViewDataSelectRowKeyList.contains(rowKey)) {
                unSelectRowKeyList.remove(rowKey);
            }
        }
        List<String> unSelectedRowKeyList = viewDataController.getUnSelectedRowKeys();
        if (unSelectedRowKeyList != null) {
            for (String rowKey : unSelectedRowKeyList) {
                if (!unSelectRowKeyList.contains(rowKey)) {
                    unSelectRowKeyList.add(rowKey);
                }
            }
        }

        //statistical data
        List<SpcStatisticalResultAlarmDto> editRowDataList = statisticalResultController.getAllRowStatsData();
        List<SearchConditionDto> statisticalSearchConditionDtoList = buildRefreshSearchConditionData(editRowDataList);
        if (statisticalSearchConditionDtoList.size() == 0) {
            return;
        }
        //chart data
        List<SpcStatisticalResultAlarmDto> chooseRowDataList = statisticalResultController.getSelectStatsData();
        List<SearchConditionDto> chartSearchConditionDtoList = buildRefreshSearchConditionData(chooseRowDataList);
        if (spcItemController.isTimer()) {
            timerSearchConditionDtoList = chartSearchConditionDtoList;
        }
        //view data
        SearchDataFrame viewDataFrame = buildSubSearchDataFrame(chartSearchConditionDtoList);
        List<String> selectRowKeyList = Lists.newArrayList();
        if (currentViewDataSelectRowKeyList == null) {
            selectRowKeyList = viewDataFrame.getSearchedRowKey();
        } else {
            if ((lastViewDataRowKeyList == null || lastViewDataRowKeyList.size() == 0) && unSelectRowKeyList.size() == 0) {
                selectRowKeyList = viewDataFrame.getSearchedRowKey();
            } else {
                for (String key : viewDataFrame.getSearchedRowKey()) {
                    if ((!lastViewDataRowKeyList.contains(key) || currentViewDataSelectRowKeyList.contains(key)) && !selectRowKeyList.contains(key) && !unSelectRowKeyList.contains(key)) {
                        selectRowKeyList.add(key);
                    }
                }
            }
        }


        //get statisticalRowKey
        List<String> statisticalRowKeyList = Lists.newArrayList();
        if (currentViewDataSelectRowKeyList == null) {
            statisticalRowKeyList = dataFrame.getAllRowKeys();
        } else {
            if ((lastViewDataRowKeyList == null || lastViewDataRowKeyList.size() == 0) && unSelectRowKeyList.size() == 0) {
                statisticalRowKeyList = dataFrame.getAllRowKeys();
            } else {
                for (String key : dataFrame.getAllRowKeys()) {
                    if ((!lastViewDataRowKeyList.contains(key) || currentViewDataSelectRowKeyList.contains(key)) && !statisticalRowKeyList.contains(key) && !unSelectRowKeyList.contains(key)) {
                        statisticalRowKeyList.add(key);
                    }
                }
            }
        }
        lastViewDataRowKeyList = viewDataFrame.getAllRowKeys();

        SearchDataFrame statisticalDataFrame = buildSubSearchDataFrame(statisticalRowKeyList, statisticalSearchConditionDtoList);
        SearchDataFrame chartDataFrame = buildSubSearchDataFrame(selectRowKeyList, chartSearchConditionDtoList);

        WindowProgressTipController windowProgressTipController = WindowMessageFactory.createWindowProgressTip();
        JobContext context = RuntimeContext.getBean(JobFactory.class).createJobContext();

        context.put(ParamKeys.SPC_SETTING_DTO, spcSettingDto);
        context.put(ParamKeys.STATISTICAL_SEARCH_DATA_FRAME, statisticalDataFrame);
        context.put(ParamKeys.STATISTICAL_SEARCH_CONDITION_DTO_LIST, statisticalSearchConditionDtoList);
        context.put(ParamKeys.CHART_SEARCH_DATA_FRAME, chartDataFrame);
        context.put(ParamKeys.CHART_SEARCH_CONDITION_DTO_LIST, chartSearchConditionDtoList);
        context.put(ParamKeys.SPC_ANALYSIS_CONFIG_DTO, analysisConfigDto);

        context.addJobEventListener(event -> windowProgressTipController.getTaskProgress().setProgress(event.getProgress()));
        windowProgressTipController.getCancelBtn().setOnAction(event -> {
            windowProgressTipController.setCancelingText();
            context.interruptBeforeNextJobHandler();
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
        JobPipeline jobPipeline = RuntimeContext.getBean(JobManager.class).getPipeLine(ParamKeys.SPC_REFRESH_ANALYSIS_JOB_PIPELINE);
        jobPipeline.setCompleteHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                List<SpcStatisticalResultAlarmDto> statisticalAnaysisResult = (List<SpcStatisticalResultAlarmDto>) context.get(ParamKeys.STATISTICAL_ANALYSIS_RESULT);
                List<SpcChartDto> spcChartDtoList = (List<SpcChartDto>) context.get(ParamKeys.CHART_ANALYSIS_RESULT);
                //set statistical data
                statisticalResultController.refreshStatisticalResult(statisticalAnaysisResult);

                //set chart data
                chartResultController.initSpcChartData(spcChartDtoList);

                //set view data
                SearchDataFrame viewDataFrame = buildSubSearchDataFrame(chartSearchConditionDtoList);

                //get last select row key
//                List<String> selectRowKeyList = Lists.newArrayList();
//                if (currentViewDataSelectRowKeyList == null) {
//                    selectRowKeyList = viewDataFrame.getAllRowKeys();
//                } else {
//                    if (lastViewDataRowKeyList == null || lastViewDataRowKeyList.size() == 0) {
//                        selectRowKeyList = viewDataFrame.getAllRowKeys();
//                    } else {
//                        selectRowKeyList.addAll(currentViewDataSelectRowKeyList);
//                        for (String key : viewDataFrame.getAllRowKeys()) {
//                            if (!lastViewDataRowKeyList.contains(key) && !selectRowKeyList.contains(key)) {
//                                selectRowKeyList.add(key);
//                            }
//                        }
//                    }
//                }
//                lastViewDataRowKeyList = viewDataFrame.getAllRowKeys();
////                List<String> countViewDataRowKeyList = currentViewDataSelectRowKeyList == null ? viewDataFrame.getAllRowKeys() : currentViewDataSelectRowKeyList;
//                spcRefreshJudgeUtil.setViewDataSelectRowKeyListCache(selectRowKeyList);
                viewDataController.setViewData(viewDataFrame, chartDataFrame.getAllRowKeys(), statisticalSearchConditionDtoList, spcItemController.isTimer());
                spcRefreshJudgeUtil.setViewDataSelectRowKeyListCache(chartDataFrame.getAllRowKeys());
                spcRefreshJudgeUtil.setStatisticalSelectRowKeyListCache(currentStatisticalSelectRowKeyList);
                logger.info("refresh Spc data finish.");
            }
        });
        jobPipeline.setErrorHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                logger.error(context.getError().getMessage());
                windowProgressTipController.updateFailProgress(context.getError().getMessage());
            }
        });
        jobPipeline.setInterruptHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                windowProgressTipController.closeDialog();
            }
        });
        logger.info("Start refresh Spc data.");
        RuntimeContext.getBean(JobManager.class).fireJobASyn(jobPipeline, context);
    }


    public SpcSettingDto getSpcSettingDto() {
        return spcSettingDto;
    }

    public void setSpcSettingDto(SpcSettingDto spcSettingDto) {
        this.spcSettingDto = spcSettingDto;
    }

    /**
     * method to set is timer or not
     *
     * @param isTimer is timer
     */
    public void setMainAnalysisTimerState(boolean isTimer) {
        resetBtn.setDisable(isTimer);
        printBtn.setDisable(isTimer);
        exportBtn.setDisable(isTimer);
        if (!isTimer) {
            this.setTimerSearchConditionDtoList(null);
        }

    }

    public List<SearchConditionDto> getTimerSearchConditionDtoList() {
        return timerSearchConditionDtoList;
    }

    public void setTimerSearchConditionDtoList(List<SearchConditionDto> timerSearchConditionDtoList) {
        this.timerSearchConditionDtoList = timerSearchConditionDtoList;
    }

    /**
     * method to get search key in timer
     *
     * @return list of search key
     */
    public List<String> getTimerSearchKeyList() {
        if (timerSearchConditionDtoList == null) {
            return null;
        }
        List<String> timerSearchKeyList = Lists.newArrayList();
        for (SearchConditionDto searchConditionDto : timerSearchConditionDtoList) {
            timerSearchKeyList.add(searchConditionDto.getKey());
        }
        return timerSearchKeyList;
    }
}
