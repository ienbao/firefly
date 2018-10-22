package com.dmsoft.firefly.plugin.yield.controller;

import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.plugin.yield.dto.YieldAnalysisConfigDto;
import com.dmsoft.firefly.plugin.yield.dto.YieldOverviewResultAlarmDto;
import com.dmsoft.firefly.plugin.yield.dto.YieldViewDataResultDto;
import com.dmsoft.firefly.plugin.yield.handler.ParamKeys;
import com.dmsoft.firefly.plugin.yield.utils.*;
import com.dmsoft.firefly.plugin.yield.dto.SearchConditionDto;
import com.dmsoft.firefly.plugin.yield.model.OverViewTableModel;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.message.IMessageManager;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.job.core.*;
import com.google.common.collect.Lists;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class OverViewController implements Initializable {
    private final Logger logger = LoggerFactory.getLogger(OverViewController.class);

    @FXML
    private TextFieldFilter filterTestItemTf;
    @FXML
    private TableView overViewResultTb;

    private YieldMainController yieldMainController;
    private ViewDataController viewDataController;
    private YieldItemController yieldItemController;
    private List<SearchConditionDto> OverViewConditionDtoList;
    private EnvService envService = RuntimeContext.getBean(EnvService.class);

    private OverViewTableModel overViewTableModel;
    private List<String> selectOverViewResultName = Lists.newArrayList();
    private SearchDataFrame dataFrame;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        filterTestItemTf.getTextField().setPromptText(YieldFxmlAndLanguageUtils.getString(ResourceMassages.FILTER_TEST_ITEM_PROMPT));
        this.initStatisticalResultTable();
        this.initComponentEvent();
    }


    public void init(YieldMainController yieldMainController) {
        this.yieldMainController = yieldMainController;
        this.initComponentEvents();
    }

    private void initStatisticalResultTable() {
        overViewTableModel = new OverViewTableModel();
//        this.initTableMenuEvent();
        TableViewWrapper.decorate(overViewResultTb, overViewTableModel);

//        List<String> preDisplayResult = this.getSpcStatisticalPreference();
        List<String> displayResult = Arrays.asList(UIConstant.YIELD_CHOOSE_RESULT);
        selectOverViewResultName.addAll(displayResult);

        overViewTableModel.initColumn(selectOverViewResultName);
    }

    private void initComponentEvent() {
        filterTestItemTf.getTextField().textProperty().addListener((observable, oldValue, newValue) -> getFilterTestItemTfEvent());

//        statisticalTableModel.getAllCheckBox().setOnAction(event -> getAllCheckBoxEvent());

        overViewResultTb.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            List<String> statisticalSelectRowKeyListCache = YieldRefreshJudgeUtil.newInstance().getOverViewSelectRowKeyListCache();
            if(statisticalSelectRowKeyListCache == null || !statisticalSelectRowKeyListCache.contains(newValue)){
                return;
            }
//            overViewTableModel.stickChartLayer((String)newValue);
        });
    }

    private void getFilterTestItemTfEvent() {
//        overViewTableModel.filterTestItem(filterTestItemTf.getTextField().getText());
    }

    private void initTableMenuEvent() {
//        TableMenuRowEvent selectColor = new ChooseColorMenuEvent();
//        statisticalTableModel.addTableMenuEvent(selectColor);
    }


    private void initComponentEvents() {
        overViewTableModel.setClickListener((rowKey,column) -> fireClickEvent(rowKey,column));
    }

    private void fireClickEvent(String rowKey,String column) {
//        System.out.println(rowKey + column);
        yieldItemController = yieldMainController.getYieldItemController();
        viewDataController = yieldMainController.getViewDataController();
        List<TestItemWithTypeDto> selectedItemDto = yieldItemController.initSelectedItemDto();
        List<String> projectNameList = envService.findActivatedProjectName();
        List<TestItemWithTypeDto> testItemWithTypeDtoList = yieldItemController.buildSelectTestItemWithTypeData(selectedItemDto);
        List<SearchConditionDto> searchConditionDtoList = yieldItemController.buildSearchConditionDataList(selectedItemDto);
        YieldAnalysisConfigDto yieldAnalysisConfigDto = yieldItemController.buildYieldAnalysisConfigData();
        JobContext context = RuntimeContext.getBean(JobFactory.class).createJobContext();
        context.put(ParamKeys.PROJECT_NAME_LIST, projectNameList);
        context.put(ParamKeys.SEARCH_CONDITION_DTO_LIST, searchConditionDtoList);
        context.put(ParamKeys.YIELD_ANALYSIS_CONFIG_DTO, yieldAnalysisConfigDto);
        context.put(ParamKeys.TEST_ITEM_WITH_TYPE_DTO_LIST, testItemWithTypeDtoList);
//        searchConditionDtoList.get(0).setItemName(rowKey);
//        if(column.equals("FPY Samples")) {
//            searchConditionDtoList.get(0).setYieldType(YieldType.FPY);
//        }else if(column.equals("Pass Samples")){
//            searchConditionDtoList.get(0).setYieldType(YieldType.PASS);
//        }else if(column.equals("NTF Samples")){
//            searchConditionDtoList.get(0).setYieldType(YieldType.NTF);
//        }else if(column.equals("NG Samples")){
//            searchConditionDtoList.get(0).setYieldType(YieldType.NG);
//        }else if(column.equals("Total Samples")){
//            searchConditionDtoList.get(0).setYieldType(YieldType.TOTAL);
//        }
        JobPipeline jobPipeline = RuntimeContext.getBean(JobManager.class).getPipeLine(ParamKeys.YIELD_VIEW_DATA_JOB_PIPELINE);
        jobPipeline.setCompleteHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {

                List<YieldViewDataResultDto> YieldViewDataResultDtoList = (List<YieldViewDataResultDto>) context.get(ParamKeys.YIELD_VIEW_DATA_RESULT_DTO_LIST);
                List<String> rowKeyList = Lists.newArrayList();
                for(int i =0; i<YieldViewDataResultDtoList.get(0).getFPYlist().size();i++){
                    rowKeyList.add(YieldViewDataResultDtoList.get(0).getFPYlist().get(i).getRowKey());
                }
                dataFrame = context.getParam(ParamKeys.SEARCH_DATA_FRAME, SearchDataFrame.class);
                List<String> testItemNameList = Lists.newArrayList();
                testItemNameList.add(searchConditionDtoList.get(0).getItemName());
                testItemNameList.add(searchConditionDtoList.get(1).getItemName());
                SearchDataFrame subDataFrame = dataFrame.subDataFrame(rowKeyList, testItemNameList);
                viewDataController.setViewData(subDataFrame, rowKeyList, searchConditionDtoList, false);


            }
        });
        jobPipeline.setErrorHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                logger.error(context.getError().getMessage());
            }
        });
        jobPipeline.setInterruptHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
            }
        });
        logger.info("ViewData Yield.");
        RuntimeContext.getBean(JobManager.class).fireJobASyn(jobPipeline, context);
    }
    /**
     * set statistical result table data
     *
     * @param list the data list
     * @param isTimer isTimer
     * @param selectRowKey selectRowKey
     */
    public void setTimerOverviewResultTableData(List<YieldOverviewResultAlarmDto> list, List<String> selectRowKey, boolean isTimer) {
//        List<String> columnList = statisticalTableModel.getColumnList();
//        statisticalTableModel = new StatisticalTableModel();
//        statisticalTableModel.setTimer(isTimer);
//        statisticalTableModel.initColumn(columnList);
//        this.initTableMenuEvent();
//        TableViewWrapper.decorate(statisticalResultTb, statisticalTableModel);
//
//        statisticalTableModel.initData(list);
//        statisticalTableModel.setSelect(selectRowKey);
//
//        statisticalTableModel.getAllCheckBox().setOnAction(event -> getAllCheckBoxEvent());
        overViewTableModel.initData(list);
//        overViewTableModel.setSelect(selectRowKey);
//        overViewTableModel.setTimer(isTimer);
        overViewTableModel.filterTestItem(filterTestItemTf.getTextField().getText());
    }


}
