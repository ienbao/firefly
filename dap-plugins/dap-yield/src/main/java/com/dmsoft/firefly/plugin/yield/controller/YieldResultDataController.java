package com.dmsoft.firefly.plugin.yield.controller;

import com.dmsoft.firefly.plugin.yield.dto.*;
import com.dmsoft.firefly.plugin.yield.handler.ParamKeys;
import com.dmsoft.firefly.plugin.yield.service.YieldService;
import com.dmsoft.firefly.plugin.yield.utils.ResourceMassages;
import com.dmsoft.firefly.plugin.yield.utils.YieldFxmlAndLanguageUtils;
import com.dmsoft.firefly.plugin.yield.utils.YieldType;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dataframe.DataFrame;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.job.core.*;
import com.google.common.collect.Lists;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class YieldResultDataController implements Initializable {
    @FXML
    private GridPane gridPane;
    @FXML
    private Label totalSamples0;
    @FXML
    private Label fpySamples0;
    @FXML
    private Label passSamples0;
    @FXML
    private Label ntfSamples0;
    @FXML
    private Label ngSamples0;
    @FXML
    private Label totalSamples1;
    @FXML
    private Label fpySamples1;
    @FXML
    private Label passSamples1;
    @FXML
    private Label ntfSamples1;
    @FXML
    private Label ngSamples1;
    private YieldChartResultController yieldChartResultController;
    private ViewDataController viewDataController;
    private YieldItemController yieldItemController;
    private YieldMainController yieldMainController;
    private EnvService envService = RuntimeContext.getBean(EnvService.class);
    private SearchDataFrame dataFrame;
    private List<TestItemWithTypeDto> testItemWithTypeDto ;
    public static final String Yield_PLUGIN_NAME = "com.dmsoft.firefly.plugin.yield.controller.YieldResultDataController";
    public void init(YieldChartResultController yieldChartResultController) {
        this.yieldChartResultController = yieldChartResultController;
//        this.viewDataController=yieldChartResultController.getYieldMainController().getViewDataController();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setOverviewResultData(YieldTotalProcessesDto list, String rowKey, boolean isTimer) {
        if(list.getTotalSamples()!=null){
            totalSamples1.setText(list.getTotalSamples().toString());
            totalSamples1.setOnMouseClicked(event ->fireClickEvent(rowKey,"Total Samples"));
            fpySamples1.setText(list.getFpySamples().toString());
            fpySamples1.setOnMouseClicked(event ->fireClickEvent(rowKey,"FPY Samples"));
            passSamples1.setText(list.getPassSamples().toString());
            passSamples1.setOnMouseClicked(event ->fireClickEvent(rowKey,"Pass Samples"));
            ntfSamples1.setText(list.getNtfSamples().toString());
            ntfSamples1.setOnMouseClicked(event ->fireClickEvent(rowKey,"NTF Samples"));
            ngSamples1.setText(list.getNgSamples().toString());
            ngSamples1.setOnMouseClicked(event ->fireClickEvent(rowKey,"NG Samples"));
            totalSamples1.setUnderline(true);
            totalSamples1.setUnderline(true);
            fpySamples1.setUnderline(true);
            passSamples1.setUnderline(true);
            ntfSamples1.setUnderline(true);
            ngSamples1.setUnderline(true);
            totalSamples0.setStyle("-fx-text-fill: #222222");
            fpySamples0.setStyle("-fx-text-fill: #222222");
            passSamples0.setStyle("-fx-text-fill: #222222");
            ntfSamples0.setStyle("-fx-text-fill: #222222");
            ngSamples0.setStyle("-fx-text-fill: #222222");
            totalSamples1.setStyle("-fx-text-fill: #222222");
            fpySamples1.setStyle("-fx-text-fill: #222222");
            passSamples1.setStyle("-fx-text-fill: #222222");
            ntfSamples1.setStyle("-fx-text-fill: #222222");
            ngSamples1.setStyle("-fx-text-fill: #222222");
        }else{
            totalSamples1.setText("-");
            fpySamples1.setText("-");
            passSamples1.setText("-");
            ntfSamples1.setText("-");
            ngSamples1.setText("-");
            totalSamples1.setUnderline(false);
            fpySamples1.setUnderline(false);
            passSamples1.setUnderline(false);
            ntfSamples1.setUnderline(false);
            ngSamples1.setUnderline(false);
            totalSamples0.setStyle("-fx-text-fill: #222222");
            fpySamples0.setStyle("-fx-text-fill: #222222");
            passSamples0.setStyle("-fx-text-fill: #222222");
            ntfSamples0.setStyle("-fx-text-fill: #222222");
            ngSamples0.setStyle("-fx-text-fill: #222222");
            totalSamples1.setStyle("-fx-text-fill: #222222");
            fpySamples1.setStyle("-fx-text-fill: #222222");
            passSamples1.setStyle("-fx-text-fill: #222222");
            ntfSamples1.setStyle("-fx-text-fill: #222222");
            ngSamples1.setStyle("-fx-text-fill: #222222");
        }

    }
    private void fireClickEvent(String rowKey,String column) {
//        System.out.println(rowKey + column);
        yieldMainController=yieldChartResultController.getYieldMainController();
        yieldItemController = yieldMainController.getYieldItemController();
        viewDataController = yieldMainController.getViewDataController();
        dataFrame = yieldMainController.getDataFrame();
        List<SearchConditionDto> searchConditionDtoList = yieldMainController.getInitSearchConditionDtoList();
        List<String> projectNameList = envService.findActivatedProjectName();
        YieldAnalysisConfigDto yieldAnalysisConfigDto = yieldMainController.getAnalysisConfigDto();


        JobContext context = RuntimeContext.getBean(JobFactory.class).createJobContext();
        context.put(ParamKeys.PROJECT_NAME_LIST, projectNameList);
        context.put(ParamKeys.SEARCH_DATA_FRAME, dataFrame);
        context.put(ParamKeys.SEARCH_CONDITION_DTO_LIST, searchConditionDtoList);
        context.put(ParamKeys.YIELD_ANALYSIS_CONFIG_DTO, yieldAnalysisConfigDto);



        JobPipeline jobPipeline = RuntimeContext.getBean(JobManager.class).getPipeLine(ParamKeys.YIELD_RESULT_DATA_JOB_PIPELINE);
        jobPipeline.setCompleteHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {

                List<YieldViewDataResultDto> YieldViewDataResultDtoList = (List<YieldViewDataResultDto>) context.get(ParamKeys.YIELD_VIEW_DATA_RESULT_DTO_LIST);
                List<String> rowKeyList = Lists.newArrayList();
//                for (int i = 0; i < YieldViewDataResultDtoList.get(0).getResultlist().size(); i++) {
//                    rowKeyList.add(YieldViewDataResultDtoList.get(0).getResultlist().get(i).getRowKey());
//                }

                if(column.equals("FPY Samples")) {
                    for (int i = 0; i < YieldViewDataResultDtoList.get(0).getFPYlist().size(); i++) {
                        rowKeyList.add(YieldViewDataResultDtoList.get(0).getFPYlist().get(i).getRowKey());
                    }
                }else if(column.equals("Pass Samples")){
                    for (int i = 0; i < YieldViewDataResultDtoList.get(0).getPASSlist().size(); i++) {
                        rowKeyList.add(YieldViewDataResultDtoList.get(0).getPASSlist().get(i).getRowKey());
                    }
                }else if(column.equals("NTF Samples")){
                    for (int i = 0; i < YieldViewDataResultDtoList.get(0).getNtflist().size(); i++) {
                        rowKeyList.add(YieldViewDataResultDtoList.get(0).getNtflist().get(i).getRowKey());
                    }
                }else if(column.equals("NG Samples")){
                    for (int i = 0; i < YieldViewDataResultDtoList.get(0).getNglist().size(); i++) {
                        rowKeyList.add(YieldViewDataResultDtoList.get(0).getNglist().get(i).getRowKey());
                    }
                }else if(column.equals("Total Samples")){
                    for (int i = 0; i < YieldViewDataResultDtoList.get(0).getTotallist().size(); i++) {
                        rowKeyList.add(YieldViewDataResultDtoList.get(0).getTotallist().get(i).getRowKey());
                    }
                }

                List<String> testItemNameList = Lists.newArrayList();
                for(int i =0; i<searchConditionDtoList.size(); i++){
                    testItemNameList.add(searchConditionDtoList.get(0).getItemName());
                }
//                testItemNameList.add(searchConditionDtoList.get(1).getItemName());
                SearchDataFrame subDataFrame = dataFrame.subDataFrame(rowKeyList, testItemNameList);
                viewDataController.setViewData(subDataFrame, rowKeyList, searchConditionDtoList, false, rowKey, column);

            }
        });
        jobPipeline.setErrorHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
//                logger.error(context.getError().getMessage());
            }
        });
        jobPipeline.setInterruptHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
            }
        });
//        logger.info("ViewData Yield.");
        RuntimeContext.getBean(JobManager.class).fireJobASyn(jobPipeline, context);

    }

    private List<SearchConditionDto> buildSearchConditionDataList(List<TestItemWithTypeDto> testItemWithTypeDtoList) {
        if (testItemWithTypeDtoList == null) {
            return null;
        }
        List<String> conditionList = yieldItemController.getSearchTab().getSearch();
        List<SearchConditionDto> searchConditionDtoList = Lists.newArrayList();
        SearchConditionDto searchPrimaryKey = new SearchConditionDto();
        searchPrimaryKey.setItemName(yieldItemController.getConfigComboBox().getValue());
        searchConditionDtoList.add(searchPrimaryKey);
        int i = 0;
        for (TestItemWithTypeDto testItemWithTypeDto : testItemWithTypeDtoList) {
            if (conditionList != null && conditionList.size() != 0) {
                for (String condition : conditionList) {
                    SearchConditionDto searchConditionDto = new SearchConditionDto();
                    searchConditionDto.setKey(ParamKeys.YIELD_ANALYSIS_CONDITION_KEY + i);
                    searchConditionDto.setItemName(testItemWithTypeDto.getTestItemName());
                    searchConditionDto.setUslOrPass(testItemWithTypeDto.getUsl());
                    searchConditionDto.setLslOrFail(testItemWithTypeDto.getLsl());
                    searchConditionDto.setTestItemType(testItemWithTypeDto.getTestItemType());
                    searchConditionDto.setCondition(condition);
                    searchConditionDtoList.add(searchConditionDto);
                    i++;
                }
            } else {
                SearchConditionDto searchConditionDto = new SearchConditionDto();
                searchConditionDto.setItemName(testItemWithTypeDto.getTestItemName());
                searchConditionDto.setUslOrPass(testItemWithTypeDto.getLsl());
                searchConditionDto.setLslOrFail(testItemWithTypeDto.getUsl());
                searchConditionDto.setTestItemType(testItemWithTypeDto.getTestItemType());
                searchConditionDtoList.add(searchConditionDto);
                i++;
            }
        }
        return searchConditionDtoList;
    }

}
