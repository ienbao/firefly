package com.dmsoft.firefly.plugin.yield.controller;

import com.dmsoft.firefly.gui.components.utils.CommonResourceMassages;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.gui.components.window.*;
import com.dmsoft.firefly.plugin.yield.dto.*;
import com.dmsoft.firefly.plugin.yield.handler.ParamKeys;
import com.dmsoft.firefly.plugin.yield.service.YieldSettingService;
import com.dmsoft.firefly.plugin.yield.utils.*;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.job.core.*;
import com.dmsoft.firefly.sdk.message.IMessageManager;
import com.dmsoft.firefly.sdk.utils.FilterUtils;
import com.google.common.collect.Lists;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.scene.control.Button;

import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;


public class YieldMainController implements Initializable {
    private static Logger logger = LoggerFactory.getLogger(YieldMainController.class);
    @FXML
    private Button resetBtn;
    @FXML
    private Button printBtn;
    @FXML
    private Button exportBtn;
    @FXML
    private Button chooseBtn;
    @FXML
    private YieldItemController yieldItemController;
    @FXML
    private OverViewController overViewController;
    @FXML
    private ViewDataController viewDataController;

    @FXML
    private YieldChartResultController yieldResultController;
    private SearchDataFrame dataFrame;
    private YieldAnalysisConfigDto analysisConfigDto;
    private List<SearchConditionDto> initSearchConditionDtoList;
    private YieldSettingDto yieldSettingDto;
    private YieldResultDto yieldResultDto;


    private YieldSettingService yieldSettingService = RuntimeContext.getBean(YieldSettingService.class);
    private EnvService envService = RuntimeContext.getBean(EnvService.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.yieldItemController.init(this);
        this.viewDataController.init(this);
        this.yieldResultController.init(this);
        this.overViewController.init(this);
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
    public void setOverviewResultData(List<YieldOverviewResultAlarmDto> list, List<String> selectRowKey, boolean isTimer) {
        overViewController.setTimerOverviewResultTableData(list, selectRowKey, isTimer);
    }


    public YieldAnalysisConfigDto getAnalysisConfigDto() {
        return analysisConfigDto;
    }

    public void setAnalysisConfigDto(YieldAnalysisConfigDto analysisConfigDto) {
        this.analysisConfigDto = analysisConfigDto;
    }

    /**
     * clear analysis data
     *
     */
    public void clearAnalysisSubShowData() {
        viewDataController.clearViewData();
    }

    /**
     * clear all analysis data
     */
    public void clearAnalysisData() {
        clearAnalysisSubShowData();
        this.setDisable(true);
    }


    private void initComponentEvent() {
        printBtn.setVisible(false);
        exportBtn.setVisible(false);
        resetBtn.setOnAction(event -> getResetBtnEvent());
        printBtn.setOnAction(event -> getExportBtnEvent());
        exportBtn.setOnAction(event -> getExportBtnEvent());
        chooseBtn.setOnAction(event -> getChooseBtnEvent());
    }

    @SuppressWarnings("unchecked")
    private void getResetBtnEvent() {
        WindowProgressTipController windowProgressTipController = WindowMessageFactory.createWindowProgressTip();
        JobContext context = RuntimeContext.getBean(JobFactory.class).createJobContext();
        context.put(ParamKeys.YIELD_SETTING_DTO, yieldSettingDto);
        context.put(ParamKeys.SEARCH_CONDITION_DTO_LIST, initSearchConditionDtoList);
        context.put(ParamKeys.YIELD_ANALYSIS_CONFIG_DTO, analysisConfigDto);
        context.put(ParamKeys.SEARCH_DATA_FRAME, dataFrame);
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
        JobPipeline jobPipeline = RuntimeContext.getBean(JobManager.class).getPipeLine(ParamKeys.YIELD_RESET_JOB_PIPELINE);
        jobPipeline.setCompleteHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                YieldRefreshJudgeUtil.newInstance().setOverViewSelectRowKeyListCache(null);
                List<YieldOverviewResultAlarmDto> YieldOverviewAlarmDtoList = (List<YieldOverviewResultAlarmDto>) context.get(ParamKeys.YIELD_STATISTICAL_RESULT_ALARM_DTO_LIST);
                setOverviewResultData(YieldOverviewAlarmDtoList, null, false);
                windowProgressTipController.closeDialog();
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
            FXMLLoader fxmlLoader = YieldFxmlAndLanguageUtils.getLoaderFXML("view/yield_export.fxml");
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("yieldExport", YieldFxmlAndLanguageUtils.getString("YIELD_EXPORT"), root, getClass().getClassLoader().getResource("css/yield_app.css").toExternalForm());
            YieldLeftConfigDto leftConfigDto = yieldItemController.getCurrentConfigData();
            stage.setResizable(false);
            stage.toFront();
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getChooseBtnEvent() {
        if (overViewController.hasErrorEditCell()) {
            WindowMessageController messageController = WindowMessageFactory.createWindowMessageHasOkAndCancel(YieldFxmlAndLanguageUtils.getString(ResourceMassages.TIP_WARN_HEADER),
                    YieldFxmlAndLanguageUtils.getString(ResourceMassages.SPC_STATISTICAL_ERROR_EDIT_MESSAGE));
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
        List<String> statisticalModifyRowKeyList = overViewController.getEidtStatisticalRowKey();

        YieldRefreshJudgeUtil yieldRefreshJudgeUtil = YieldRefreshJudgeUtil.newInstance();
        YieldRefreshJudgeUtil.RefreshType refreshType = yieldRefreshJudgeUtil.refreshJudge(statisticalModifyRowKeyList);

        switch (refreshType) {
            case NOT_NEED_REFRESH:
                RuntimeContext.getBean(IMessageManager.class).showWarnMsg(
                        YieldFxmlAndLanguageUtils.getString(UIConstant.UI_MESSAGE_TIP_WARNING_TITLE),
                        YieldFxmlAndLanguageUtils.getString("EXCEPTION_SPC_NO_REFRESH_RESULT"));

                break;
            case REFRESH_STATISTICAL_RESULT:
                this.refreshStatisticalResult(yieldRefreshJudgeUtil);
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
        TooltipUtil.installNormalTooltip(resetBtn, YieldFxmlAndLanguageUtils.getString("YIELD_RESET_BTN_TOOLTIP"));
        TooltipUtil.installNormalTooltip(printBtn, YieldFxmlAndLanguageUtils.getString("YIELD_PRINT_BTN_TOOLTIP"));
        TooltipUtil.installNormalTooltip(exportBtn, YieldFxmlAndLanguageUtils.getString("YIELD_EXPORT_BTN_TOOLTIP"));
        TooltipUtil.installNormalTooltip(chooseBtn, YieldFxmlAndLanguageUtils.getString("YIELD_REFRESH_BTN_TOOLTIP"));
    }

    public Map<String, Color> getColorCache() {

        return  null;
    }

    public SearchDataFrame getDataFrame() {
        return dataFrame;
    }

    public void setDataFrame(SearchDataFrame dataFrame) {
        this.dataFrame = dataFrame;
    }


    public List<SearchConditionDto> getInitSearchConditionDtoList() {
        return initSearchConditionDtoList;
    }

    public void setInitSearchConditionDtoList(List<SearchConditionDto> initSearchConditionDtoList) {
        this.initSearchConditionDtoList = initSearchConditionDtoList;
    }

    private List<SearchConditionDto> buildRefreshSearchConditionData(List<YieldOverviewResultAlarmDto> spcStatsDtoList) {
        List<SearchConditionDto> searchConditionDtoList = yieldItemController.buildSearchConditionDataList(yieldItemController.initSelectedItemDto());
        for (int i = 0 ;i <searchConditionDtoList.size() ; i++){
            for (int j = 0 ;j < spcStatsDtoList.size() ;j++) {
                if (searchConditionDtoList.get(i).getItemName().equals(spcStatsDtoList.get(j).getItemName())){
                    searchConditionDtoList.get(i).setLslOrFail(spcStatsDtoList.get(j).getLslOrFail());
                    searchConditionDtoList.get(i).setUslOrPass(spcStatsDtoList.get(j).getUslOrPass());
                }
            }
        }

        return searchConditionDtoList;
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


    @SuppressWarnings("unchecked")
    private void refreshStatisticalResult(YieldRefreshJudgeUtil spcRefreshJudgeUtil) {
        WindowProgressTipController windowProgressTipController = WindowMessageFactory.createWindowProgressTip();
        JobContext context = RuntimeContext.getBean(JobFactory.class).createJobContext();

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
        String primaryKey = yieldItemController.getPrimaryKey();
        YieldOverviewResultAlarmDto yieldOverviewResultAlarmDto = new YieldOverviewResultAlarmDto();
        yieldOverviewResultAlarmDto.setItemName(primaryKey);
        List<YieldOverviewResultAlarmDto> editRowDataList = Lists.newArrayList();
        editRowDataList.add(yieldOverviewResultAlarmDto);
        List<YieldOverviewResultAlarmDto> RowDataList = overViewController.getEditRowStatsData();
        for (YieldOverviewResultAlarmDto yieldOverviewResultAlarmDto1 : RowDataList){
            editRowDataList.add(yieldOverviewResultAlarmDto1);
        }
        List<SearchConditionDto> searchConditionDtoList = buildRefreshSearchConditionData(editRowDataList);
        List<String> projectNameList = envService.findActivatedProjectName();
        List<TestItemWithTypeDto> testItemWithTypeDtoList = yieldItemController.buildSelectTestItemWithTypeData(yieldItemController.initSelectedItemDto());
        context.put(ParamKeys.PROJECT_NAME_LIST, projectNameList);
        context.put(ParamKeys.SEARCH_CONDITION_DTO_LIST, searchConditionDtoList);
        context.put(ParamKeys.YIELD_ANALYSIS_CONFIG_DTO, analysisConfigDto);
        context.put(ParamKeys.TEST_ITEM_WITH_TYPE_DTO_LIST, testItemWithTypeDtoList);
        context.addJobEventListener(event -> windowProgressTipController.getTaskProgress().setProgress(event.getProgress()));
        windowProgressTipController.getCancelBtn().setOnAction(event -> {
            windowProgressTipController.setCancelingText();
            context.interruptBeforeNextJobHandler();
            if (context.isError() || context.getCurrentProgress() == 1.0) {
                windowProgressTipController.closeDialog();
            }
        });

        JobPipeline jobPipeline = RuntimeContext.getBean(JobManager.class).getPipeLine(ParamKeys.SPC_REFRESH_STATISTICAL_JOB_PIPELINE);
        jobPipeline.setCompleteHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                List<YieldOverviewResultAlarmDto> spcStatisticalResultAlarmDtoList = (List<YieldOverviewResultAlarmDto>) context.get(ParamKeys.YIELD_STATISTICAL_RESULT_ALARM_DTO_LIST);
                overViewController.setTimerOverviewResultTableData(spcStatisticalResultAlarmDtoList,null,false);
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


    public YieldSettingDto getYieldSettingDto() {
        return yieldSettingDto;
    }

    public void setYieldSettingDto(YieldSettingDto yieldSettingDto) {
        this.yieldSettingDto = yieldSettingDto;
    }


    public void setYieldResultDto(YieldResultDto yieldResultDto) {
        this.yieldResultDto = yieldResultDto;
    }

    public void YieldAnalyseChart(){
        yieldResultController.analyzeYieldResult(yieldResultDto);
    }

    public YieldChartResultController getYieldResultController() {
        return yieldResultController;
    }

    public void setYieldResultController(YieldChartResultController yieldResultController) {
        this.yieldResultController = yieldResultController;
    }

    public ViewDataController getViewDataController() {
        return viewDataController;
    }

    public void setViewDataController(ViewDataController viewDataController) {
        this.viewDataController = viewDataController;
    }

    public YieldItemController getYieldItemController() {
        return yieldItemController;
    }

    public void setYieldItemController(YieldItemController yieldItemController) {
        this.yieldItemController = yieldItemController;
    }
}
