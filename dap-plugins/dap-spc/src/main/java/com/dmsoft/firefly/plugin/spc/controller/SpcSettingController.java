/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.gui.components.table.NewTableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.plugin.spc.dto.ControlRuleDto;
import com.dmsoft.firefly.plugin.spc.dto.CustomAlarmDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcSettingDto;
import com.dmsoft.firefly.plugin.spc.handler.ParamKeys;
import com.dmsoft.firefly.plugin.spc.model.ChooseTableRowData;
import com.dmsoft.firefly.plugin.spc.model.ControlAlarmRuleTableModel;
import com.dmsoft.firefly.plugin.spc.model.CustomAlarmTestItemRowData;
import com.dmsoft.firefly.plugin.spc.model.StatisticsResultRuleRowData;
import com.dmsoft.firefly.plugin.spc.utils.ImageUtils;
import com.dmsoft.firefly.plugin.spc.utils.ResourceMassages;
import com.dmsoft.firefly.plugin.spc.utils.SpcFxmlAndLanguageUtils;
import com.dmsoft.firefly.plugin.spc.utils.enums.SpcProCapAlarmKey;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.job.Job;
import com.dmsoft.firefly.sdk.job.core.JobManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Ethan.Yang on 2018/3/13.
 */
public class SpcSettingController implements Initializable {
    @FXML
    private Label defaultSetting;
    @FXML
    private Label alarmSetting;
    @FXML
    private Label controlAlarmRule;
    @FXML
    private Label exportSetting;
    @FXML
    private Button apply;
    @FXML
    private Button cancel;
    @FXML
    private Button ok;

    //analysis setting
    @FXML
    private TextField subgroupSizeTf;
    @FXML
    private TextField ndChartNumberTf;

    //alarm setting
    @FXML
    private TextFieldFilter searchTestItemTf;
    @FXML
    private Button addTestItemBtn;
    @FXML
    private TableView<CustomAlarmTestItemRowData> testItemTable;
    @FXML
    private TableColumn<CustomAlarmTestItemRowData, String> testItemColumn;
    @FXML
    private Label testItemNameLabel;
    @FXML
    private TableView<StatisticsResultRuleRowData> statisticalResultAlarmSetTable;
    @FXML
    private TableColumn<StatisticsResultRuleRowData, String> statisticsColumn;
    @FXML
    private TableColumn<StatisticsResultRuleRowData, String> lowerLimitColumn;
    @FXML
    private TableColumn<StatisticsResultRuleRowData, String> upperLimitColumn;

    //control Alarm Rule
    @FXML
    private TableView controlAlarmRuleTable;
    private ControlAlarmRuleTableModel controlAlarmRuleTableModel;
    @FXML
    private TextArea ruleInstructionTextArea;

    //Export Template Setting
    @FXML
    private ComboBox exportTemplateCb;
    @FXML
    private Button exportTemplateSettingBtn;

    private JobManager manager = RuntimeContext.getBean(JobManager.class);

    private ObservableList<CustomAlarmTestItemRowData> testItemRowDataObservableList;
    private ObservableList<StatisticsResultRuleRowData> statisticsRuleRowDataObservableList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.searchTestItemTf.getTextField().setPromptText(SpcFxmlAndLanguageUtils.getString(ResourceMassages.FILTER_TEXTFIELD_PROMPT));
        this.initBtnIcon();
        this.initComponent();
        this.initData();
        this.initComponentEvent();
    }

    private void initComponent() {
        testItemColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        testItemRowDataObservableList = FXCollections.observableArrayList();
        testItemTable.setItems(testItemRowDataObservableList);

        statisticsColumn.setCellValueFactory(cellData -> cellData.getValue().statisticNameProperty());
        lowerLimitColumn.setCellValueFactory(cellData -> cellData.getValue().lowerLimitProperty());
        upperLimitColumn.setCellValueFactory(cellData -> cellData.getValue().upperLimitProperty());
        statisticsRuleRowDataObservableList = FXCollections.observableArrayList();
        statisticalResultAlarmSetTable.setItems(statisticsRuleRowDataObservableList);

        controlAlarmRuleTableModel = new ControlAlarmRuleTableModel();
        NewTableViewWrapper.decorate(controlAlarmRuleTable, controlAlarmRuleTableModel);
    }

    /**
     * init data
     */
    public void initData() {
        Job job = new Job(ParamKeys.FIND_SPC_SETTING_DATA_JOP_PIPELINE);
        Object returnValue = manager.doJobSyn(job);
        if (returnValue == null || returnValue instanceof SpcSettingDto) {
            //todo message tip
        }
        SpcSettingDto spcSettingDto = (SpcSettingDto) returnValue;

        this.setAnalysisSettingData(spcSettingDto.getCustomGroupNumber(), spcSettingDto.getChartIntervalNumber());
        this.setProcessAlarmSettingData(spcSettingDto.getAbilityAlarmRule());
        this.setCustomAlarmSettingData(spcSettingDto.getStatisticalAlarmSetting());
        this.setControlAlarmSettingData(spcSettingDto.getControlChartRule());
        this.setExportSettingData(spcSettingDto.getExportTemplateName());
    }

    private void setAnalysisSettingData(int customGroupNumber, int chartIntervalNumber) {
        subgroupSizeTf.setText(String.valueOf(customGroupNumber));
        ndChartNumberTf.setText(String.valueOf(chartIntervalNumber));
    }

    private void setProcessAlarmSettingData(Map<String, Double[]> abilityAlarmRule) {
        SpcProCapAlarmKey[] proCapAlarmKeys = SpcProCapAlarmKey.values();
        if (proCapAlarmKeys == null) {
            return;
        }
        for (int i = 0; i < proCapAlarmKeys.length; i++) {
            String key = proCapAlarmKeys[i].getCode();
            Double[] value = abilityAlarmRule.get(key);
            if (SpcProCapAlarmKey.CA.getCode().equals(key)) {
                caExcellentTf.setText(alarmDataToText(value[0]));
                caAcceptableTf.setText(alarmDataToText(value[1]));
                caRectificationTf.setText(alarmDataToText(value[2]));
            } else if (SpcProCapAlarmKey.CP.getCode().equals(key)) {
                cpExcellentTf.setText(alarmDataToText(value[0]));
                cpGoodTf.setText(alarmDataToText(value[1]));
                cpAcceptableTf.setText(alarmDataToText(value[2]));
                cpRectificationTf.setText(alarmDataToText(value[3]));
            } else if (SpcProCapAlarmKey.CPK.getCode().equals(key)) {
                cpkExcellentTf.setText(alarmDataToText(value[0]));
                cpkGoodTf.setText(alarmDataToText(value[1]));
                cpkAcceptableTf.setText(alarmDataToText(value[2]));
                cpkRectificationTf.setText(alarmDataToText(value[3]));
            } else if (SpcProCapAlarmKey.CPL.getCode().equals(key)) {
                cplExcellentTf.setText(alarmDataToText(value[0]));
                cplGoodTf.setText(alarmDataToText(value[1]));
                cplAcceptableTf.setText(alarmDataToText(value[2]));
                cplRectificationTf.setText(alarmDataToText(value[3]));
            } else if (SpcProCapAlarmKey.CPU.getCode().equals(key)) {
                cpuExcellentTf.setText(alarmDataToText(value[0]));
                cpuGoodTf.setText(alarmDataToText(value[1]));
                cpuAcceptableTf.setText(alarmDataToText(value[2]));
                cpuRectificationTf.setText(alarmDataToText(value[3]));
            } else if (SpcProCapAlarmKey.PP.getCode().equals(key)) {
                ppExcellentTf.setText(alarmDataToText(value[0]));
                ppGoodTf.setText(alarmDataToText(value[1]));
                ppAcceptableTf.setText(alarmDataToText(value[2]));
                ppRectificationTf.setText(alarmDataToText(value[3]));
            } else if (SpcProCapAlarmKey.PPK.getCode().equals(key)) {
                ppkExcellentTf.setText(alarmDataToText(value[0]));
                ppkGoodTf.setText(alarmDataToText(value[1]));
                ppkAcceptableTf.setText(alarmDataToText(value[2]));
                ppkRectificationTf.setText(alarmDataToText(value[3]));
            } else if (SpcProCapAlarmKey.PPL.getCode().equals(key)) {
                pplExcellentTf.setText(alarmDataToText(value[0]));
                pplGoodTf.setText(alarmDataToText(value[1]));
                pplAcceptableTf.setText(alarmDataToText(value[2]));
                pplRectificationTf.setText(alarmDataToText(value[3]));
            } else if (SpcProCapAlarmKey.PPU.getCode().equals(key)) {
                ppuExcellentTf.setText(alarmDataToText(value[0]));
                ppuGoodTf.setText(alarmDataToText(value[1]));
                ppuAcceptableTf.setText(alarmDataToText(value[2]));
                ppuRectificationTf.setText(alarmDataToText(value[3]));
            }
        }
    }

    private void setCustomAlarmSettingData(Map<String, List<CustomAlarmDto>> statisticalAlarmSetting) {
        if (statisticalAlarmSetting == null) {
            testItemRowDataObservableList.clear();
            return;
        }
        for (Map.Entry<String, List<CustomAlarmDto>> entry : statisticalAlarmSetting.entrySet()) {
            CustomAlarmTestItemRowData testItemRowData = new CustomAlarmTestItemRowData(entry.getKey(), entry.getValue());
            testItemRowDataObservableList.add(testItemRowData);
        }
    }

    private void setControlAlarmSettingData(List<ControlRuleDto> controlChartRule) {
        if (controlChartRule == null) {
            return;
        }
        controlAlarmRuleTableModel.initData(controlChartRule);
    }

    private void setExportSettingData(String exportTemplateName) {
        exportTemplateCb.setValue(exportTemplateName);
    }


    private void initComponentEvent() {

    }

    private void initBtnIcon() {
        addTestItemBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_add_normal.png")));
        exportTemplateSettingBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_setting_normal.png")));
        exportTemplateSettingBtn.setPrefSize(22, 22);
    }

    private String alarmDataToText(Double value) {
        if (value == null) {
            return "";
        }
        return String.valueOf(value);
    }

    //Process Capability alarm Setting
    //CA
    @FXML
    private TextField caExcellentTf;
    @FXML
    private TextField caAcceptableTf;
    @FXML
    private TextField caRectificationTf;

    //CP
    @FXML
    private TextField cpExcellentTf;
    @FXML
    private TextField cpGoodTf;
    @FXML
    private TextField cpAcceptableTf;
    @FXML
    private TextField cpRectificationTf;

    //CPK
    @FXML
    private TextField cpkExcellentTf;
    @FXML
    private TextField cpkGoodTf;
    @FXML
    private TextField cpkAcceptableTf;
    @FXML
    private TextField cpkRectificationTf;

    //CPL
    @FXML
    private TextField cplExcellentTf;
    @FXML
    private TextField cplGoodTf;
    @FXML
    private TextField cplAcceptableTf;
    @FXML
    private TextField cplRectificationTf;

    //CPU
    @FXML
    private TextField cpuExcellentTf;
    @FXML
    private TextField cpuGoodTf;
    @FXML
    private TextField cpuAcceptableTf;
    @FXML
    private TextField cpuRectificationTf;

    //PP
    @FXML
    private TextField ppExcellentTf;
    @FXML
    private TextField ppGoodTf;
    @FXML
    private TextField ppAcceptableTf;
    @FXML
    private TextField ppRectificationTf;

    //PPK
    @FXML
    private TextField ppkExcellentTf;
    @FXML
    private TextField ppkGoodTf;
    @FXML
    private TextField ppkAcceptableTf;
    @FXML
    private TextField ppkRectificationTf;

    //PPL
    @FXML
    private TextField pplExcellentTf;
    @FXML
    private TextField pplGoodTf;
    @FXML
    private TextField pplAcceptableTf;
    @FXML
    private TextField pplRectificationTf;

    //PPU
    @FXML
    private TextField ppuExcellentTf;
    @FXML
    private TextField ppuGoodTf;
    @FXML
    private TextField ppuAcceptableTf;
    @FXML
    private TextField ppuRectificationTf;


}