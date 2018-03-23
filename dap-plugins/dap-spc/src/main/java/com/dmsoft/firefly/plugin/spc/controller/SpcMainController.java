/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.gui.components.window.*;
import com.dmsoft.firefly.plugin.spc.dto.*;
import com.dmsoft.firefly.plugin.spc.handler.ParamKeys;
import com.dmsoft.firefly.plugin.spc.service.SpcSettingService;
import com.dmsoft.firefly.plugin.spc.utils.*;
import com.dmsoft.firefly.plugin.spc.utils.ImageUtils;
import com.dmsoft.firefly.plugin.spc.utils.ResourceMassages;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TimePatternDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.job.Job;
import com.dmsoft.firefly.sdk.job.core.JobManager;
import com.dmsoft.firefly.sdk.utils.FilterUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.Color;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * Created by Ethan.Yang on 2018/2/2.
 */
public class SpcMainController implements Initializable {

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

    private JobManager manager = RuntimeContext.getBean(JobManager.class);
    private SpcSettingService spcSettingService = RuntimeContext.getBean(SpcSettingService.class);
    private EnvService envService = RuntimeContext.getBean(EnvService.class);
    private static Logger logger = LoggerFactory.getLogger(SpcMainController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.spcItemController.init(this);
        this.statisticalResultController.init(this);
        this.viewDataController.init(this);
        this.chartResultController.init(this);
        this.initBtnIcon();
        this.initComponentEvent();
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
     */
    public void clearAnalysisSubShowData() {
        //todo clear chart data
        chartResultController.clearChartData();
        viewDataController.setViewData(null, null);
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

    private void getResetBtnEvent() {
        WindowProgressTipController windowProgressTipController = WindowMessageFactory.createWindowProgressTip();
        windowProgressTipController.addProcessMonitorListener(new WindowCustomListener() {
            @Override
            public boolean onShowCustomEvent() {
                System.out.println("show");

                return false;
            }

            @Override
            public boolean onCloseAndCancelCustomEvent() {
                //to do
                System.out.println("close");
                return false;
            }

            @Override
            public boolean onOkCustomEvent() {
                System.out.println("ok");

                return false;
            }
        });
        Service<Integer> service = new Service<Integer>() {
            @Override
            protected Task<Integer> createTask() {
                return new Task<Integer>() {
                    @Override
                    protected Integer call() throws Exception {
                        Thread.sleep(100);
                        Job job = new Job(ParamKeys.SPC_RESET_JOB_PIPELINE);
                        job.addProcessMonitorListener(event -> {
                            System.out.println("event*****" + event.getPoint());
                            updateProgress(event.getPoint(), 100);
                        });
                        Map paramMap = Maps.newHashMap();
                        paramMap.put(ParamKeys.SEARCH_CONDITION_DTO_LIST, initSearchConditionDtoList);
                        paramMap.put(ParamKeys.SPC_ANALYSIS_CONFIG_DTO, analysisConfigDto);
                        paramMap.put(ParamKeys.SEARCH_DATA_FRAME, dataFrame);

                        Object returnValue = manager.doJobSyn(job, paramMap);
                        if (returnValue == null) {
                            //todo message tip
                            ((Exception) returnValue).printStackTrace();
                            logger.error(((Exception) returnValue).getMessage());
                        } else {
                            List<SpcStatisticalResultAlarmDto> spcStatisticalResultAlarmDtoList = (List<SpcStatisticalResultAlarmDto>) returnValue;
                            setStatisticalResultData(spcStatisticalResultAlarmDtoList);
                            clearAnalysisSubShowData();
                        }
                        return null;
                    }
                };
            }
        };
        windowProgressTipController.getTaskProgress().progressProperty().bind(service.progressProperty());
        service.start();
    }

    private void getExportBtnEvent() {
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = SpcFxmlAndLanguageUtils.getLoaderFXML("view/spc_export.fxml");
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("spcExport", "Spc Export", root, getClass().getClassLoader().getResource("css/spc_app.css").toExternalForm());
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

    private void refreshEvent(){
        List<String> currentStatisticalSelectRowKeyList = statisticalResultController.getSelectStatisticalRowKey();
        List<String> currentViewDataSelectRowKeyList = viewDataController.getSelectedRowKeys();
        List<String> statisticalModifyRowKeyList = statisticalResultController.getEidtStatisticalRowKey();

        SpcRefreshJudgeUtil spcRefreshJudgeUtil = SpcRefreshJudgeUtil.newInstance();
        SpcRefreshJudgeUtil.RefreshType refreshType = spcRefreshJudgeUtil.refreshJudge(currentStatisticalSelectRowKeyList, currentViewDataSelectRowKeyList, statisticalModifyRowKeyList);

        switch (refreshType) {
            case NOT_NEED_REFRESH:
                if (currentStatisticalSelectRowKeyList.size() == 0) {
                    this.clearAnalysisSubShowData();
                    spcRefreshJudgeUtil.setViewDataIsBlank(true);
                    spcRefreshJudgeUtil.setStatisticalSelectRowKeyListCache(currentStatisticalSelectRowKeyList);
                }
                System.out.println("not need refresh");
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
        List<String> timeKeys = Lists.newArrayList();
        String timePattern = null;
        try {
            TimePatternDto timePatternDto = envService.findActivatedTemplate().getTimePatternDto();
            if (timePatternDto != null) {
                timeKeys = timePatternDto.getTimeKeys();
                timePattern = timePatternDto.getPattern();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        SearchDataFrame subDataFrame = dataFrame.subDataFrame(rowKeyList, testItemNameList);
        return subDataFrame;
    }

    private boolean resultSelectIsChange(List<String> newList, List<String> oldList) {
        if (oldList == null) {
            if (newList.size() == 0) {
                return false;
            } else {
                return true;
            }
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

    private void refreshStatisticalResult(SpcRefreshJudgeUtil spcRefreshJudgeUtil) {
        //refresh statistical result
        List<String> currentStatisticalSelectRowKeyList = spcRefreshJudgeUtil.getCurrentStatisticalSelectRowKeyList();
        List<String> statisticalSelectRowKeyListCache = spcRefreshJudgeUtil.getStatisticalSelectRowKeyListCache();
        if (currentStatisticalSelectRowKeyList.size() == 0) {
            this.clearAnalysisSubShowData();
        }
        List<String> rowKeyList = statisticalSelectRowKeyListCache == null ? dataFrame.getAllRowKeys() : statisticalSelectRowKeyListCache;

        List<SpcStatisticalResultAlarmDto> editRowDataList = statisticalResultController.getEditRowStatsData();
        List<SearchConditionDto> searchConditionDtoList = this.buildRefreshSearchConditionData(editRowDataList);
        SearchDataFrame subDataFrame = this.buildSubSearchDataFrame(rowKeyList, searchConditionDtoList);
        Job job = new Job(ParamKeys.SPC_REFRESH_STATISTICAL_JOB_PIPELINE);
        Map paramMap = Maps.newHashMap();
        paramMap.put(ParamKeys.SEARCH_CONDITION_DTO_LIST, searchConditionDtoList);
        paramMap.put(ParamKeys.SPC_ANALYSIS_CONFIG_DTO, analysisConfigDto);
        paramMap.put(ParamKeys.SEARCH_DATA_FRAME, subDataFrame);

        Object returnValue = manager.doJobSyn(job, paramMap);
        if (returnValue instanceof Exception) {
            ((Exception) returnValue).printStackTrace();
            logger.error(((Exception) returnValue).getMessage());
            return;
        } else {
//                clearAnalysisSubShowData();
            List<SpcStatisticalResultAlarmDto> spcStatisticalResultAlarmDtoList = (List<SpcStatisticalResultAlarmDto>) returnValue;
            statisticalResultController.refreshStatisticalResult(spcStatisticalResultAlarmDtoList);
//            this.setStatisticalResultData(spcStatisticalResultAlarmDtoList);
        }
    }

    private void refreshChartResult(SpcRefreshJudgeUtil spcRefreshJudgeUtil) {
        //only refresh chart
        List<String> currentStatisticalSelectRowKeyList = spcRefreshJudgeUtil.getCurrentStatisticalSelectRowKeyList();

        List<String> viewDataSelectRowKeyListCache = spcRefreshJudgeUtil.getViewDataSelectRowKeyListCache();
        List<String> rowKeyList = viewDataSelectRowKeyListCache == null ? dataFrame.getAllRowKeys() : viewDataSelectRowKeyListCache;

        spcRefreshJudgeUtil.setStatisticalSelectRowKeyListCache(currentStatisticalSelectRowKeyList);
        spcRefreshJudgeUtil.setViewDataSelectRowKeyListCache(rowKeyList);

        List<SearchConditionDto> searchConditionDtoList = this.buildRefreshSearchConditionData(statisticalResultController.getSelectStatsData());
        if (searchConditionDtoList.size() == 0) {
            return;
        }
        Job job = new Job(ParamKeys.SPC_REFRESH_CHART_JOB_PIPELINE);
        Map paramMap = Maps.newHashMap();
        paramMap.put(ParamKeys.SEARCH_CONDITION_DTO_LIST, searchConditionDtoList);
        paramMap.put(ParamKeys.SPC_ANALYSIS_CONFIG_DTO, analysisConfigDto);

        SearchDataFrame subDataFrame = this.buildSubSearchDataFrame(rowKeyList, searchConditionDtoList);
        paramMap.put(ParamKeys.SEARCH_DATA_FRAME, subDataFrame);

        Object returnValue = manager.doJobSyn(job, paramMap);
        if (returnValue instanceof Exception) {
            ((Exception) returnValue).printStackTrace();
            logger.error(((Exception) returnValue).getMessage());
            return;
        }
        List<SpcChartDto> spcChartDtoList = (List<SpcChartDto>) returnValue;
        chartResultController.initSpcChartData(spcChartDtoList);

        SearchDataFrame viewDataFrame = this.buildSubSearchDataFrame(dataFrame.getAllRowKeys(), searchConditionDtoList);
        viewDataController.setViewData(viewDataFrame, rowKeyList);
    }

    private void refreshAllAnalysisResult(SpcRefreshJudgeUtil spcRefreshJudgeUtil) {
        // refresh all data
        List<String> currentStatisticalSelectRowKeyList = spcRefreshJudgeUtil.getCurrentStatisticalSelectRowKeyList();
        List<String> currentViewDataSelectRowKeyList = spcRefreshJudgeUtil.getCurrentViewDataSelectRowKeyList();
        spcRefreshJudgeUtil.setStatisticalSelectRowKeyListCache(currentStatisticalSelectRowKeyList);
        spcRefreshJudgeUtil.setViewDataSelectRowKeyListCache(currentViewDataSelectRowKeyList);

        //statistical data
        List<SpcStatisticalResultAlarmDto> editRowDataList = statisticalResultController.getAllRowStatsData();
        List<SearchConditionDto> statisticalSearchConditionDtoList = this.buildRefreshSearchConditionData(editRowDataList);
        if (statisticalSearchConditionDtoList.size() == 0) {
            return;
        }
        SearchDataFrame statisticalDataFrame = this.buildSubSearchDataFrame(currentViewDataSelectRowKeyList, statisticalSearchConditionDtoList);

        //chart data
        List<SpcStatisticalResultAlarmDto> chooseRowDataList = statisticalResultController.getSelectStatsData();
        List<SearchConditionDto> chartSearchConditionDtoList = this.buildRefreshSearchConditionData(chooseRowDataList);
        SearchDataFrame chartDataFrame = this.buildSubSearchDataFrame(currentViewDataSelectRowKeyList, chartSearchConditionDtoList);

        Job job = new Job(ParamKeys.SPC_REFRESH_ANALYSIS_JOB_PIPELINE);
        Map paramMap = Maps.newHashMap();
        paramMap.put(ParamKeys.STATISTICAL_SEARCH_DATA_FRAME, statisticalDataFrame);
        paramMap.put(ParamKeys.STATISTICAL_SEARCH_CONDITION_DTO_LIST, statisticalSearchConditionDtoList);
        paramMap.put(ParamKeys.CHART_SEARCH_DATA_FRAME, chartDataFrame);
        paramMap.put(ParamKeys.CHART_SEARCH_CONDITION_DTO_LIST, chartSearchConditionDtoList);
        paramMap.put(ParamKeys.SPC_ANALYSIS_CONFIG_DTO, analysisConfigDto);

        Object returnValue = manager.doJobSyn(job, paramMap);
        if (returnValue instanceof Exception) {
            ((Exception) returnValue).printStackTrace();
            logger.error(((Exception) returnValue).getMessage());
            return;
        }
        Map<String, Object> analysisResultMap = (Map) returnValue;
        List<SpcStatisticalResultAlarmDto> statisticalAnaysisResult = (List<SpcStatisticalResultAlarmDto>) analysisResultMap.get(ParamKeys.STATISTICAL_ANALYSIS_RESULT);
        List<SpcChartDto> spcChartDtoList = (List<SpcChartDto>) analysisResultMap.get(ParamKeys.CHART_ANALYSIS_RESULT);

        //set statistical data
        statisticalResultController.refreshStatisticalResult(statisticalAnaysisResult);

        //set chart data
        chartResultController.initSpcChartData(spcChartDtoList);

        //set view data
        SearchDataFrame viewDataFrame = this.buildSubSearchDataFrame(dataFrame.getAllRowKeys(), chartSearchConditionDtoList);
        viewDataController.setViewData(viewDataFrame, currentViewDataSelectRowKeyList);
    }


}
