package com.dmsoft.firefly.plugin.yield.controller;

import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.plugin.yield.dto.*;
import com.dmsoft.firefly.plugin.yield.handler.ParamKeys;
import com.dmsoft.firefly.plugin.yield.service.YieldService;
import com.dmsoft.firefly.plugin.yield.utils.*;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class OverViewController implements Initializable {
    private final Logger logger = LoggerFactory.getLogger(OverViewController.class);

    @FXML
    private TextFieldFilter filterTestItemTf;
    @FXML
    private TableView overViewResultTb;

    @Autowired
    private EnvService envService;
    @Autowired
    private JobFactory jobFactory;
    @Autowired
    private JobManager jobManager;

    private YieldMainController yieldMainController;
    private ViewDataController viewDataController;
    private YieldItemController yieldItemController;
    private List<SearchConditionDto> OverViewConditionDtoList;

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
        TableViewWrapper.decorate(overViewResultTb, overViewTableModel);
        List<String> displayResult = Arrays.asList(UIConstant.YIELD_CHOOSE_RESULT);
        selectOverViewResultName.addAll(displayResult);

        overViewTableModel.initColumn(selectOverViewResultName);
    }

    private void initComponentEvent() {
        filterTestItemTf.getTextField().textProperty().addListener((observable, oldValue, newValue) -> getFilterTestItemTfEvent());
        overViewResultTb.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            List<String> statisticalSelectRowKeyListCache = YieldRefreshJudgeUtil.newInstance().getOverViewSelectRowKeyListCache();
            if (statisticalSelectRowKeyListCache == null || !statisticalSelectRowKeyListCache.contains(newValue)) {
                return;
            }
        });
    }

    private void getFilterTestItemTfEvent() {
        overViewTableModel.filterTestItem(filterTestItemTf.getTextField().getText());
    }



    private void initComponentEvents() {
        overViewTableModel.setClickListener((ViewDataParamDto) -> fireClickEvent(ViewDataParamDto));
    }

    public void fireClickEvent(ViewDataParamDto viewDataParamDto) {

        String rowKey = viewDataParamDto.getItemName();
        String column = viewDataParamDto.getColumn();
        yieldItemController = yieldMainController.getYieldItemController();
        viewDataController = yieldMainController.getViewDataController();
        dataFrame = yieldMainController.getDataFrame();
        List<String> projectNameList = envService.findActivatedProjectName();

        List<SearchConditionDto> searchConditionDtoList = yieldItemController.buildSearchConditionDataList(yieldItemController.initSelectedItemDto());
        YieldAnalysisConfigDto yieldAnalysisConfigDto = yieldMainController.getAnalysisConfigDto();
        List<SearchConditionDto> selectSearchConditionDtoList = Lists.newArrayList();
        selectSearchConditionDtoList.add(searchConditionDtoList.get(0));

        for (int i = 1; i < searchConditionDtoList.size(); i++) {
            if (rowKey.equals(searchConditionDtoList.get(i).getItemName())) {
                selectSearchConditionDtoList.add(searchConditionDtoList.get(i));
            }
        }

        selectSearchConditionDtoList.get(1).setUslOrPass(viewDataParamDto.getUsl());
        selectSearchConditionDtoList.get(1).setLslOrFail(viewDataParamDto.getLsl());

        if(column.equals("FPY Samples")) {
            selectSearchConditionDtoList.get(0).setYieldType(YieldType.FPY);
        }else if(column.equals("Pass Samples")){
            selectSearchConditionDtoList.get(0).setYieldType(YieldType.PASS);
        }else if(column.equals("NTF Samples")){
            selectSearchConditionDtoList.get(0).setYieldType(YieldType.NTF);
        }else if(column.equals("NG Samples")){
            selectSearchConditionDtoList.get(0).setYieldType(YieldType.NG);
        }else if(column.equals("Total Samples")){
            selectSearchConditionDtoList.get(0).setYieldType(YieldType.TOTAL);
        }

        JobContext context = this.jobFactory.createJobContext();
        context.put(ParamKeys.PROJECT_NAME_LIST, projectNameList);
        context.put(ParamKeys.SEARCH_DATA_FRAME, dataFrame);
        context.put(ParamKeys.VIEW_SEARCH_CONDITION_DTO_LIST, selectSearchConditionDtoList);
        context.put(ParamKeys.SEARCH_CONDITION_DTO_LIST, searchConditionDtoList);
        context.put(ParamKeys.YIELD_ANALYSIS_CONFIG_DTO, yieldAnalysisConfigDto);


        JobPipeline jobPipeline = this.jobManager.getPipeLine(ParamKeys.YIELD_VIEW_DATA_JOB_PIPELINE);
        jobPipeline.setCompleteHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {

                YieldViewDataResultDto yieldViewDataResultDto = (YieldViewDataResultDto) context.get(ParamKeys.YIELD_VIEW_DATA_RESULT_DTO);
                List<String> rowKeyList = Lists.newArrayList();
                if((yieldViewDataResultDto.getResultlist() != null)) {

                    for (int i = 0; i < yieldViewDataResultDto.getResultlist().size(); i++) {
                        rowKeyList.add(yieldViewDataResultDto.getResultlist().get(i));
                    }

                    List<String> testItemNameList = Lists.newArrayList();
                    testItemNameList.add(selectSearchConditionDtoList.get(0).getItemName());
                    testItemNameList.add(selectSearchConditionDtoList.get(1).getItemName());
                    SearchDataFrame subDataFrame = dataFrame.subDataFrame(rowKeyList, testItemNameList);

                    viewDataController.setViewData(subDataFrame, rowKeyList, selectSearchConditionDtoList, false, rowKey, column,null);
                }else{
                    viewDataController.setViewData(null, null, null, false, rowKey, column,null);
                }
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
        this.jobManager.fireJobASyn(jobPipeline, context);

    }

    /**
     * set statistical result table data
     *
     * @param list         the data list
     * @param isTimer      isTimer
     * @param selectRowKey selectRowKey
     */
    public void setTimerOverviewResultTableData(List<YieldOverviewResultAlarmDto> list, List<String> selectRowKey, boolean isTimer) {
        overViewTableModel.initData(list);
        overViewTableModel.filterTestItem(filterTestItemTf.getTextField().getText());
    }

    /**
     * has error edit cell.
     */
    public boolean hasErrorEditCell() {
        return overViewTableModel.hasErrorEditValue();
    }

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
