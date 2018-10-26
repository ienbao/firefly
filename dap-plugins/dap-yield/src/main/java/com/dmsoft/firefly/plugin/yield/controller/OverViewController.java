package com.dmsoft.firefly.plugin.yield.controller;

import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.plugin.yield.dto.YieldAnalysisConfigDto;
import com.dmsoft.firefly.plugin.yield.dto.YieldOverviewResultAlarmDto;
import com.dmsoft.firefly.plugin.yield.dto.YieldViewDataResultDto;
import com.dmsoft.firefly.plugin.yield.handler.ParamKeys;
import com.dmsoft.firefly.plugin.yield.service.YieldService;
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

    public void fireClickEvent(String rowKey,String column) {
//        System.out.println(rowKey + column);
        yieldItemController = yieldMainController.getYieldItemController();
        viewDataController = yieldMainController.getViewDataController();
        dataFrame = yieldMainController.getDataFrame();
        List<String> projectNameList = envService.findActivatedProjectName();

        List<SearchConditionDto> searchConditionDtoList = yieldMainController.getInitSearchConditionDtoList();
        YieldAnalysisConfigDto yieldAnalysisConfigDto = yieldMainController.getAnalysisConfigDto();
        List<SearchConditionDto> selectSearchConditionDtoList = Lists.newArrayList();
        selectSearchConditionDtoList.add(searchConditionDtoList.get(0));

        for(int i = 1; i<searchConditionDtoList.size();i++) {
            if (rowKey.equals(searchConditionDtoList.get(i).getItemName())) {
                selectSearchConditionDtoList.add(searchConditionDtoList.get(i));
            }
        }

        JobContext context = RuntimeContext.getBean(JobFactory.class).createJobContext();
        context.put(ParamKeys.PROJECT_NAME_LIST, projectNameList);
        context.put(ParamKeys.SEARCH_DATA_FRAME, dataFrame);
        context.put(ParamKeys.SEARCH_CONDITION_DTO_LIST, selectSearchConditionDtoList);
        context.put(ParamKeys.YIELD_ANALYSIS_CONFIG_DTO, yieldAnalysisConfigDto);


        JobPipeline jobPipeline = RuntimeContext.getBean(JobManager.class).getPipeLine(ParamKeys.YIELD_VIEW_DATA_JOB_PIPELINE);
        jobPipeline.setCompleteHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {

                YieldViewDataResultDto YieldViewDataResultDto = (YieldViewDataResultDto) context.get(ParamKeys.YIELD_VIEW_DATA_RESULT_DTO);
                List<String> rowKeyList = Lists.newArrayList();

                if(column.equals("FPY Samples")) {
                    for (int i = 0; i < YieldViewDataResultDto.getFPYlist().size(); i++) {
                        rowKeyList.add(YieldViewDataResultDto.getFPYlist().get(i).getRowKey());
                    }
                }else if(column.equals("Pass Samples")){
                    for (int i = 0; i < YieldViewDataResultDto.getPASSlist().size(); i++) {
                        rowKeyList.add(YieldViewDataResultDto.getPASSlist().get(i).getRowKey());
                    }
                }else if(column.equals("NTF Samples")){
                    for (int i = 0; i < YieldViewDataResultDto.getNtflist().size(); i++) {
                        rowKeyList.add(YieldViewDataResultDto.getNtflist().get(i).getRowKey());
                    }
                }else if(column.equals("NG Samples")){
                    for (int i = 0; i <YieldViewDataResultDto.getNglist().size(); i++) {
                        rowKeyList.add(YieldViewDataResultDto.getNglist().get(i).getRowKey());
                    }
                }else if(column.equals("Total Samples")){
                    for (int i = 0; i < YieldViewDataResultDto.getTotallist().size(); i++) {
                        rowKeyList.add(YieldViewDataResultDto.getTotallist().get(i).getRowKey());
                    }
                }

                List<String> testItemNameList = Lists.newArrayList();
                testItemNameList.add(selectSearchConditionDtoList.get(0).getItemName());
                testItemNameList.add(selectSearchConditionDtoList.get(1).getItemName());
                SearchDataFrame subDataFrame = dataFrame.subDataFrame(rowKeyList, testItemNameList);
                viewDataController.setViewData(subDataFrame, rowKeyList, selectSearchConditionDtoList, false, rowKey, column);

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

    /**
     * has error edit cell.
     */
    public boolean hasErrorEditCell(){
        return overViewTableModel.hasErrorEditValue();
    }

//    /**
//     * get select row key
//     *
//     * @return row key
//     */
//    public List<String> getSelectStatisticalRowKey() {
//        return overViewTableModel.getSelectRowKey();
//    }

    /**
     * get edit row key
     *
     * @return row key
     */
    public List<String> getEidtStatisticalRowKey() {
        return overViewTableModel.getEditorRowKey();
    }

    /**
     * refresh spc statistical data
     *
     * @param yieldOverviewResultAlarmDtoList the refresh data
     */
    public void refreshStatisticalResult(List<YieldOverviewResultAlarmDto> yieldOverviewResultAlarmDtoList) {
        overViewTableModel.refreshData(yieldOverviewResultAlarmDtoList);
    }


    /**
     * get all stats data
     *
     * @return
     */
    public List<YieldOverviewResultAlarmDto> getAllRowStatsData() {
        return overViewTableModel.getSpcStatsDtoList();
    }

    /**
     * get edit row data
     *
     * @return the row data
     */
    public List<YieldOverviewResultAlarmDto> getEditRowStatsData() {
        return overViewTableModel.getEditRowData();
    }

}
