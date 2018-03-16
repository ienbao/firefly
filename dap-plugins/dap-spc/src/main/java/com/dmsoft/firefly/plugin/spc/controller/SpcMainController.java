/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.plugin.spc.dto.*;
import com.dmsoft.firefly.plugin.spc.handler.ParamKeys;
import com.dmsoft.firefly.plugin.spc.service.SpcSettingService;
import com.dmsoft.firefly.plugin.spc.utils.ImageUtils;
import com.dmsoft.firefly.plugin.spc.utils.SpcFxmlAndLanguageUtils;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dataframe.DataFrameFactory;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.job.Job;
import com.dmsoft.firefly.sdk.job.core.JobManager;
import com.dmsoft.firefly.sdk.utils.FilterUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.awt.*;
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
    private JobManager manager = RuntimeContext.getBean(JobManager.class);
    private SpcSettingService spcSettingService = RuntimeContext.getBean(SpcSettingService.class);
    private EnvService envService = RuntimeContext.getBean(EnvService.class);

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
    public void clearAnalysisShowData(){
        statisticalResultController.clearStatisticalResultData();
    }

    private void initComponentEvent() {
        resetBtn.setOnAction(event -> getResetBtnEvent());
        printBtn.setOnAction(event -> getPrintBtnEvent());
        exportBtn.setOnAction(event -> getExportBtnEvent());
        chooseBtn.setOnAction(event -> getChooseBtnEvent());
    }

    private void getResetBtnEvent() {

    }

    private void getPrintBtnEvent() {
        SpcSettingDto spcSettingDto = this.initSpcSettingDto();
        spcSettingService.saveSpcSetting(spcSettingDto);
    }

    @Deprecated
    private SpcSettingDto initSpcSettingDto() {
        SpcSettingDto spcSettingDto = new SpcSettingDto();

        spcSettingDto.setCustomGroupNumber(10);
        spcSettingDto.setChartIntervalNumber(8);

        Map<String, Double[]> abilityAlarmRule = Maps.newHashMap();
        abilityAlarmRule.put("CA", new Double[]{12.5, 25d, 50d});
        abilityAlarmRule.put("CP", new Double[]{1.67, 1.33, 1.0, 0.67});
        abilityAlarmRule.put("CPK", new Double[]{1.67, 1.33, 1.0, 0.67});
        abilityAlarmRule.put("CPL", new Double[]{1.67, 1.33, 1.0, 0.67});
        abilityAlarmRule.put("CPU", new Double[]{1.67, 1.33, 1.0, 0.67});
        abilityAlarmRule.put("PP", new Double[]{1.67, 1.33, 1.0, 0.67});
        abilityAlarmRule.put("PPK", new Double[]{1.67, 1.33, 1.0, 0.67});
        abilityAlarmRule.put("PPL", new Double[]{1.67, 1.33, 1.0, 0.67});
        abilityAlarmRule.put("PPU", new Double[]{1.67, 1.33, 1.0, 0.67});
        spcSettingDto.setAbilityAlarmRule(abilityAlarmRule);

        Map<String, List<CustomAlarmDto>> statistiacalAlarmMap = Maps.newHashMap();
        List<String> testItem = Lists.newArrayList("A1", "A2", "A3");
        testItem.forEach(name -> {
            List<CustomAlarmDto> customAlarmDtoList = Lists.newArrayList();
            List<String> statistics = Lists.newArrayList("AVG", "Max", "Min", "StDev", "Center", "Range", "LCL",
                    "UCL", "Kurtosis", "Skewness");
            statistics.forEach(s -> {
                CustomAlarmDto customAlarmDto = new CustomAlarmDto();
                customAlarmDto.setStatisticName(s);
                customAlarmDto.setUpperLimit(10d);
                customAlarmDto.setLowerLimit(3d);
                customAlarmDtoList.add(customAlarmDto);
            });

            statistiacalAlarmMap.put(name, customAlarmDtoList);
        });
        spcSettingDto.setStatisticalAlarmSetting(statistiacalAlarmMap);

        List<ControlRuleDto> controlChartRule = Lists.newArrayList();
        List<String> alarmNameList = Lists.newArrayList("R1", "R2", "R3", "R4", "R5", "R6", "R7",
                "R8", "R9");
        alarmNameList.forEach(alarmName -> {
            ControlRuleDto controlRuleDto = new ControlRuleDto();
            controlRuleDto.setUsed(true);
            controlRuleDto.setRuleName(alarmName);
            controlRuleDto.setmValue(7d);
            controlRuleDto.setnValue(4d);
            controlRuleDto.setsValue(1d);
            controlChartRule.add(controlRuleDto);
        });
        spcSettingDto.setControlChartRule(controlChartRule);

        spcSettingDto.setExportTemplateName("Default Template");
        return spcSettingDto;
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
        List<SearchConditionDto> searchConditionDtoList = this.buildRefreshSearchConditionData();
        if (searchConditionDtoList.size() == 0) {
            return;
        }
        Job job = new Job(ParamKeys.SPC_REFRESH_JOB_PIPELINE);
        Map paramMap = Maps.newHashMap();
        paramMap.put(ParamKeys.SEARCH_CONDITION_DTO_LIST, searchConditionDtoList);
        paramMap.put(ParamKeys.SPC_ANALYSIS_CONFIG_DTO, analysisConfigDto);

        SearchDataFrame subDataFrame = this.buildSubSearchDataFrame(searchConditionDtoList);
        viewDataController.setViewData(subDataFrame);
        paramMap.put(ParamKeys.SEARCH_DATA_FRAME, subDataFrame);

        Object returnValue = manager.doJobSyn(job, paramMap);
        if (returnValue == null) {
            return;
        }
        List<SpcChartDto> spcChartDtoList = (List<SpcChartDto>) returnValue;
        chartResultController.initSpcChartData(spcChartDtoList);
    }

    private void initBtnIcon() {
        resetBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_reset_normal.png")));
        printBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_print_normal.png")));
        exportBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_export_normal.png")));
        chooseBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/icon_choose_one_white.png")));
    }

    @Deprecated
    private SearchDataFrame initData() {
        List<TestItemWithTypeDto> typeDtoList = Lists.newArrayList();
        List<RowDataDto> rowDataDtoList = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            TestItemWithTypeDto typeDto = new TestItemWithTypeDto();
            typeDto.setTestItemName("itemName" + i);
            typeDto.setLsl("10");
            typeDto.setUsl("30");
            typeDtoList.add(typeDto);
        }
        Random random = new Random();
        int k = random.nextInt(100);
        for (int i = 0; i < k; i++) {
            RowDataDto rowDataDto = new RowDataDto();
            Map<String, String> map = Maps.newHashMap();
            rowDataDto.setRowKey(i + "");
            for (int j = 0; j < 10; j++) {
                map.put(typeDtoList.get(j).getTestItemName(), i + j + "");
            }
            rowDataDto.setData(map);
            rowDataDtoList.add(rowDataDto);
        }
        return RuntimeContext.getBean(DataFrameFactory.class).createSearchDataFrame(typeDtoList, rowDataDtoList);
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

    private List<SearchConditionDto> buildRefreshSearchConditionData() {
        List<SearchConditionDto> searchConditionDtoList = Lists.newArrayList();
        List<SpcStatisticalResultAlarmDto> spcStatsDtoList = statisticalResultController.getSelectStatsData();
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

    private SearchDataFrame buildSubSearchDataFrame(List<SearchConditionDto> searchConditionDtoList) {
        if (dataFrame == null || searchConditionDtoList == null) {
            return null;
        }
        List<String> testItemNameList = Lists.newArrayList();
        List<String> timeKeys = Lists.newArrayList();
        String timePattern = null;
        try {
            timeKeys = envService.findActivatedTemplate().getTimePatternDto().getTimeKeys();
            timePattern = envService.findActivatedTemplate().getTimePatternDto().getPattern();
        } catch (Exception e) {

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
        List<String> rowKeyList = dataFrame.getAllRowKeys();
        SearchDataFrame subDataFrame = dataFrame.subDataFrame(rowKeyList, testItemNameList);
        return subDataFrame;
    }
}
