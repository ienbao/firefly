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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class YieldResultDataController implements Initializable {
    private final Logger logger = LoggerFactory.getLogger(YieldResultDataController.class);
    @FXML
    private HBox hBox;
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
        hBox.setStyle("-fx-background-color: #F5F5F5");
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

        if(list.getTotalSamples()!=null){
            totalSamples1.setText(list.getTotalSamples().toString());
            fpySamples1.setText(list.getFpySamples().toString());
            passSamples1.setText(list.getPassSamples().toString());
            ntfSamples1.setText(list.getNtfSamples().toString());
            ngSamples1.setText(list.getNgSamples().toString());
            totalSamples1.setUnderline(true);
            totalSamples1.setUnderline(true);
            fpySamples1.setUnderline(true);
            passSamples1.setUnderline(true);
            ntfSamples1.setUnderline(true);
            ngSamples1.setUnderline(true);
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
        }
        totalSamples1.setOnMouseClicked(event ->fireClickEvent(rowKey,"Total Samples",list));
        fpySamples1.setOnMouseClicked(event ->fireClickEvent(rowKey,"FPY Samples",list));
        passSamples1.setOnMouseClicked(event ->fireClickEvent(rowKey,"Pass Samples",list));
        ntfSamples1.setOnMouseClicked(event ->fireClickEvent(rowKey,"NTF Samples",list));
        ngSamples1.setOnMouseClicked(event ->fireClickEvent(rowKey,"NG Samples",list));

    }
    private void fireClickEvent(String rowKey,String column,YieldTotalProcessesDto list) {
        yieldMainController = yieldChartResultController.getYieldMainController();
        List<YieldOverviewResultAlarmDto> RowDataList = yieldMainController.getOverViewController().getAllRowStatsData();
        viewDataController = yieldMainController.getViewDataController();
        if(list.getTotalSamples()!=null){
            yieldItemController = yieldMainController.getYieldItemController();
            dataFrame = yieldMainController.getDataFrame();
            List<SearchConditionDto> searchConditionDtoList =  yieldItemController.buildSearchConditionDataList(yieldItemController.initSelectedItemDto());
            for(int i = 1; i<searchConditionDtoList.size();i++){
                for(int j = 0; j<RowDataList.size();j++){
                    if(searchConditionDtoList.get(i).getItemName().equals(RowDataList.get(j).getItemName())){
                        if(!(searchConditionDtoList.get(i).getUslOrPass().equals(RowDataList.get(j).getUslOrPass()))){
                            searchConditionDtoList.get(i).setUslOrPass(RowDataList.get(j).getUslOrPass());
                        }
                        if(!(searchConditionDtoList.get(i).getLslOrFail().equals(RowDataList.get(j).getLslOrFail()))){
                            searchConditionDtoList.get(i).setLslOrFail(RowDataList.get(j).getLslOrFail());
                        }
                    }
                }
            }

            List<String> projectNameList = envService.findActivatedProjectName();
            YieldAnalysisConfigDto yieldAnalysisConfigDto = yieldMainController.getAnalysisConfigDto();

            if(column.equals("FPY Samples")) {
                searchConditionDtoList.get(0).setYieldType(YieldType.FPY);
            }else if(column.equals("Pass Samples")){
                searchConditionDtoList.get(0).setYieldType(YieldType.PASS);
            }else if(column.equals("NTF Samples")){
                searchConditionDtoList.get(0).setYieldType(YieldType.NTF);
            }else if(column.equals("NG Samples")){
                searchConditionDtoList.get(0).setYieldType(YieldType.NG);
            }else if(column.equals("Total Samples")){
                searchConditionDtoList.get(0).setYieldType(YieldType.TOTAL);
            }

            JobContext context = RuntimeContext.getBean(JobFactory.class).createJobContext();
            context.put(ParamKeys.PROJECT_NAME_LIST, projectNameList);
            context.put(ParamKeys.SEARCH_DATA_FRAME, dataFrame);
            context.put(ParamKeys.SEARCH_CONDITION_DTO_LIST, searchConditionDtoList);
            context.put(ParamKeys.YIELD_ANALYSIS_CONFIG_DTO, yieldAnalysisConfigDto);


            JobPipeline jobPipeline = RuntimeContext.getBean(JobManager.class).getPipeLine(ParamKeys.YIELD_RESULT_DATA_JOB_PIPELINE);
            jobPipeline.setCompleteHandler(new AbstractBasicJobHandler() {
                @Override
                public void doJob(JobContext context) {

                    YieldViewDataResultDto yieldViewDataResultDto = (YieldViewDataResultDto) context.get(ParamKeys.YIELD_VIEW_DATA_RESULT_DTO);
                    List<String> rowKeyList = Lists.newArrayList();

                    if ((yieldViewDataResultDto.getResultlist() != null)){
                        for (int i = 0; i < yieldViewDataResultDto.getResultlist().size(); i++) {
                            rowKeyList.add(yieldViewDataResultDto.getResultlist().get(i));
                        }

                    List<String> testItemNameList = Lists.newArrayList();
//                    for (int i = 0; i < searchConditionDtoList.size(); i++) {
                        testItemNameList.add(searchConditionDtoList.get(0).getItemName());
//                    }

                    SearchDataFrame subDataFrame = dataFrame.subDataFrame(rowKeyList, testItemNameList);
                    viewDataController.setViewData(subDataFrame, rowKeyList, searchConditionDtoList, false, rowKey, column, Yield_PLUGIN_NAME);
                     }else{
                        viewDataController.setViewData(null, null, null, false, rowKey, column,Yield_PLUGIN_NAME);
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
            RuntimeContext.getBean(JobManager.class).fireJobASyn(jobPipeline, context);
        }
        else{

            viewDataController.setViewData(null, null, null, false, "-", "-",Yield_PLUGIN_NAME);
        }

    }

}
