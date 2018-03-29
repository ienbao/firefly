/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.gui.components.window.WindowMessageFactory;
import com.dmsoft.firefly.plugin.spc.dto.ControlRuleDto;
import com.dmsoft.firefly.plugin.spc.dto.CustomAlarmDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcSettingDto;
import com.dmsoft.firefly.plugin.spc.handler.ParamKeys;
import com.dmsoft.firefly.plugin.spc.model.ControlAlarmRuleTableModel;
import com.dmsoft.firefly.plugin.spc.model.CustomAlarmTestItemRowData;
import com.dmsoft.firefly.plugin.spc.model.StatisticsResultRuleRowData;
import com.dmsoft.firefly.plugin.spc.model.StatisticsRuleModel;
import com.dmsoft.firefly.plugin.spc.utils.*;
import com.dmsoft.firefly.plugin.spc.utils.enums.SpcCustomAlarmKey;
import com.dmsoft.firefly.plugin.spc.utils.enums.SpcProCapAlarmKey;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.job.core.JobContext;
import com.dmsoft.firefly.sdk.job.core.JobFactory;
import com.dmsoft.firefly.sdk.job.core.JobManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Ethan.Yang on 2018/3/13.
 */
public class SpcSettingController implements Initializable {
    private final Logger logger = LoggerFactory.getLogger(SpcSettingController.class);
    //    @FXML
//    private Label defaultSetting;
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
//    @FXML
//    private TextField subgroupSizeTf;
//    @FXML
//    private TextField ndChartNumberTf;

    //alarm setting
    @FXML
    private TextFieldFilter searchTestItemTf;
    @FXML
    private Button addTestItemBtn;
    @FXML
    private TableView<CustomAlarmTestItemRowData> customAlarmTable;
    @FXML
    private TableColumn<CustomAlarmTestItemRowData, String> testItemColumn;
    @FXML
    private Label testItemNameLabel;
    @FXML
    private TableView statisticalResultAlarmSetTable;
//    @FXML
//    private TableColumn<StatisticsResultRuleRowData, String> statisticsColumn;
//    @FXML
//    private TableColumn<StatisticsResultRuleRowData, String> lowerLimitColumn;
//    @FXML
//    private TableColumn<StatisticsResultRuleRowData, String> upperLimitColumn;

    //control Alarm Rule
    @FXML
    private TableView controlAlarmRuleTable;
    private ControlAlarmRuleTableModel controlAlarmRuleTableModel;

    //Export Template Setting
    @FXML
    private ComboBox exportTemplateCb;
    @FXML
    private Button exportTemplateSettingBtn;

    @FXML
    private ScrollPane settingScrollPane;

    private ObservableList<CustomAlarmTestItemRowData> testItemRowDataObservableList;
    private FilteredList<CustomAlarmTestItemRowData> testItemRowDataFilteredList;
    //    private ObservableList<StatisticsResultRuleRowData> statisticsRuleRowDataObservableList;
    private StatisticsRuleModel statisticsRuleModel;

    private SpcExportSettingController spcExportSettingController;
    private AddItemController addItemController;
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

    //    private void setAnalysisSettingData(int customGroupNumber, int chartIntervalNumber) {
//        subgroupSizeTf.setText(String.valueOf(customGroupNumber));
//        ndChartNumberTf.setText(String.valueOf(chartIntervalNumber));
//    }
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
    @FXML
    private VBox defaultSettingVBox, alarmSettingVBox, controlAlarmRuleVBox, exportSettingVBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.searchTestItemTf.getTextField().setPromptText(SpcFxmlAndLanguageUtils.getString(ResourceMassages.FILTER_TEXTFIELD_PROMPT));
        this.initBtnIcon();
        this.initComponent();
        this.initData();
        this.initComponentEvent();
        this.initValidate();
    }

    private void initComponent() {
        testItemColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        testItemRowDataObservableList = FXCollections.observableArrayList();
        testItemRowDataFilteredList = testItemRowDataObservableList.filtered(p -> true);
        customAlarmTable.setItems(testItemRowDataFilteredList);

//        statisticsColumn.setCellValueFactory(cellData -> cellData.getValue().statisticNameProperty());
//        lowerLimitColumn.setCellValueFactory(cellData -> cellData.getValue().lowerLimitProperty());
//        upperLimitColumn.setCellValueFactory(cellData -> cellData.getValue().upperLimitProperty());
//        lowerLimitColumn.setCellFactory(TextFieldTableCell.forTableColumn());
//        upperLimitColumn.setCellFactory(TextFieldTableCell.forTableColumn());
//        statisticsRuleRowDataObservableList = FXCollections.observableArrayList();
//        statisticalResultAlarmSetTable.setItems(statisticsRuleRowDataObservableList);

        statisticsRuleModel = new StatisticsRuleModel();
        TableViewWrapper.decorate(statisticalResultAlarmSetTable, statisticsRuleModel);

        controlAlarmRuleTableModel = new ControlAlarmRuleTableModel();
        TableViewWrapper.decorate(controlAlarmRuleTable, controlAlarmRuleTableModel);
        ((TableColumn) controlAlarmRuleTable.getColumns().get(0)).setPrefWidth(72);
    }

    /**
     * init data
     */
    public void initData() {
        JobContext context = RuntimeContext.getBean(JobFactory.class).createJobContext();
        RuntimeContext.getBean(JobManager.class).fireJobSyn(ParamKeys.FIND_SPC_SETTING_DATA_JOP_PIPELINE, context);
//        Job job = new Job(ParamKeys.FIND_SPC_SETTING_DATA_JOP_PIPELINE);
//        Object returnValue = manager.doJobSyn(job);
//        if (returnValue == null) {
//            logger.debug("Spc setting data is null");
//            return;
//        }
//        if (returnValue instanceof Exception) {
//            ((Exception) returnValue).printStackTrace();
//            return;
//        }
        SpcSettingDto spcSettingDto = context.getParam(ParamKeys.SPC_SETTING_DTO, SpcSettingDto.class);

//        this.setAnalysisSettingData(spcSettingDto.getCustomGroupNumber(), spcSettingDto.getChartIntervalNumber());
        this.setProcessAlarmSettingData(spcSettingDto.getAbilityAlarmRule());
        this.setCustomAlarmSettingData(spcSettingDto.getStatisticalAlarmSetting());
        this.setControlAlarmSettingData(spcSettingDto.getControlChartRule());
        this.setExportSettingData(spcSettingDto.getExportTemplateName());
    }

    /**
     * add custom alarm data
     *
     * @param testItemList testItem list
     * @param testItem     test Item
     */
    public void addCustomAlarmSettingData(List<String> testItemList, String testItem) {
        if (testItem != null) {
            List<CustomAlarmTestItemRowData> list = Lists.newArrayList();
            list.addAll(testItemRowDataObservableList);
            for (CustomAlarmTestItemRowData customAlarmTestItemRowData : list) {
                if (customAlarmTestItemRowData.getName().equals(testItem)) {
                    testItemRowDataObservableList.remove(customAlarmTestItemRowData);
                    break;
                }
            }
            CustomAlarmTestItemRowData newRowData = new CustomAlarmTestItemRowData(testItem, this.initEmptyCustomDto());
            testItemRowDataObservableList.add(0, newRowData);
        }

        if (testItemList != null) {
            for (String item : testItemList) {
                CustomAlarmTestItemRowData newRowData = new CustomAlarmTestItemRowData(item, this.initEmptyCustomDto());
                testItemRowDataObservableList.add(0, newRowData);
            }
        }

    }

    private void initValidate() {
//        SpcSettingValidateUtil.newInstance().validateSpcAnalysisSetting(subgroupSizeTf, ndChartNumberTf);
        SpcSettingValidateUtil.BindNode caBindNode = SpcSettingValidateUtil.newInstance().new BindNode(SpcSettingValidateUtil.BindNode.ASC, caExcellentTf, caAcceptableTf, caRectificationTf);
        SpcSettingValidateUtil.BindNode cpBindNode = SpcSettingValidateUtil.newInstance().new BindNode(cpExcellentTf, cpGoodTf, cpAcceptableTf, cpRectificationTf);
        SpcSettingValidateUtil.BindNode cpkBindNode = SpcSettingValidateUtil.newInstance().new BindNode(cpkExcellentTf, cpkGoodTf, cpkAcceptableTf, cpkRectificationTf);
        SpcSettingValidateUtil.BindNode cplBindNode = SpcSettingValidateUtil.newInstance().new BindNode(cplExcellentTf, cplGoodTf, cplAcceptableTf, cplRectificationTf);
        SpcSettingValidateUtil.BindNode cpuBindNode = SpcSettingValidateUtil.newInstance().new BindNode(cpuExcellentTf, cpuGoodTf, cpuAcceptableTf, cpuRectificationTf);
        SpcSettingValidateUtil.BindNode ppBindNode = SpcSettingValidateUtil.newInstance().new BindNode(ppExcellentTf, ppGoodTf, ppAcceptableTf, ppRectificationTf);
        SpcSettingValidateUtil.BindNode ppkBindNode = SpcSettingValidateUtil.newInstance().new BindNode(ppkExcellentTf, ppkGoodTf, ppkAcceptableTf, ppkRectificationTf);
        SpcSettingValidateUtil.BindNode pplBindNode = SpcSettingValidateUtil.newInstance().new BindNode(pplExcellentTf, pplGoodTf, pplAcceptableTf, pplRectificationTf);
        SpcSettingValidateUtil.BindNode ppuBindNode = SpcSettingValidateUtil.newInstance().new BindNode(ppuExcellentTf, ppuGoodTf, ppuAcceptableTf, ppuRectificationTf);
        SpcSettingValidateUtil.newInstance().validateSpcAlarmSetting(caBindNode, cpBindNode, cpkBindNode, cplBindNode, cpuBindNode, ppBindNode, ppkBindNode, pplBindNode, ppuBindNode);
    }

    private List<CustomAlarmDto> initEmptyCustomDto() {
        List<CustomAlarmDto> customAlarmDtoList = Lists.newArrayList();
        SpcCustomAlarmKey[] customAlarmKeys = SpcCustomAlarmKey.values();
        if (customAlarmKeys == null) {
            return customAlarmDtoList;
        }
        for (int i = 0; i < customAlarmKeys.length; i++) {
            String key = customAlarmKeys[i].getCode();
            CustomAlarmDto customAlarmDto = new CustomAlarmDto();
            customAlarmDto.setStatisticName(key);
            customAlarmDtoList.add(customAlarmDto);
        }
        return customAlarmDtoList;
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
        testItemRowDataObservableList.clear();
        if (statisticalAlarmSetting == null) {
            return;
        }
        for (Map.Entry<String, List<CustomAlarmDto>> entry : statisticalAlarmSetting.entrySet()) {
            CustomAlarmTestItemRowData testItemRowData = new CustomAlarmTestItemRowData(entry.getKey(), entry.getValue());
            testItemRowDataObservableList.add(testItemRowData);
        }
        if (testItemRowDataObservableList != null && testItemRowDataObservableList.size() != 0) {
//            statisticsRuleRowDataObservableList.clear();
            CustomAlarmTestItemRowData customAlarmTestItemRowData = testItemRowDataObservableList.get(0);
            if (customAlarmTestItemRowData == null) {
                return;
            }
            List<CustomAlarmDto> customAlarmDtoList = customAlarmTestItemRowData.getCustomAlarmDtoList();
//            if (customAlarmDtoList == null) {
//                return;
//            }
//            for (CustomAlarmDto customAlarmDto : customAlarmDtoList) {
//                statisticsRuleRowDataObservableList.add(new StatisticsResultRuleRowData(customAlarmDto));
//            }
            statisticsRuleModel.initData(customAlarmDtoList);
            testItemNameLabel.setText(customAlarmTestItemRowData.getName());
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
        apply.setOnAction(event -> getApplyBtnEvent());
        cancel.setOnAction(event -> getCancelBtnEvent());
        ok.setOnAction(event -> getOkBtnEvent());

        addTestItemBtn.setOnAction(event -> getAddTestItemEvent());
        exportTemplateSettingBtn.setOnAction(event -> getExportTemplateSettingEvent());
        searchTestItemTf.getTextField().textProperty().addListener((observable, oldValue, newValue) -> getFilterTestItemTfEvent(newValue));

        customAlarmTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> getCustomAlarmTableChangeEvent(newValue));
        statisticalResultAlarmSetTable.editingCellProperty().addListener(new ChangeListener<TablePosition<StatisticsResultRuleRowData, ?>>() {
            @Override
            public void changed(ObservableValue<? extends TablePosition<StatisticsResultRuleRowData, ?>> observable, TablePosition<StatisticsResultRuleRowData, ?> oldValue, TablePosition<StatisticsResultRuleRowData, ?> newValue) {

            }
        });

//        defaultSetting.setOnMousePressed(defaultSetting -> getDefaultSettingMousePressedEvent());
        alarmSetting.setOnMousePressed(defaultSetting -> getAlarmSettingMousePressedEvent());
        controlAlarmRule.setOnMousePressed(defaultSetting -> getControlAlarmRuleMousePressedEvent());
        exportSetting.setOnMousePressed(defaultSetting -> getExportSettingMousePressedEvent());
    }

    private void getDefaultSettingMousePressedEvent() {
        ScrollPaneValueUtils.setScrollVerticalValue(settingScrollPane, defaultSettingVBox);
//        defaultSetting.setStyle("-fx-background-color: #FFFFFF");
        alarmSetting.setStyle("-fx-background-color: #F0F0F0");
        controlAlarmRule.setStyle("-fx-background-color: #F0F0F0");
        exportSetting.setStyle("-fx-background-color: #F0F0F0");
    }

    private void getAlarmSettingMousePressedEvent() {
        ScrollPaneValueUtils.setScrollVerticalValue(settingScrollPane, alarmSettingVBox);
//        defaultSetting.setStyle("-fx-background-color: #F0F0F0");
        alarmSetting.setStyle("-fx-background-color: #FFFFFF");
        controlAlarmRule.setStyle("-fx-background-color: #F0F0F0");
        exportSetting.setStyle("-fx-background-color: #F0F0F0");
    }

    private void getControlAlarmRuleMousePressedEvent() {
        ScrollPaneValueUtils.setScrollVerticalValue(settingScrollPane, controlAlarmRuleVBox);
//        defaultSetting.setStyle("-fx-background-color: #F0F0F0");
        alarmSetting.setStyle("-fx-background-color: #F0F0F0");
        controlAlarmRule.setStyle("-fx-background-color: #FFFFFF");
        exportSetting.setStyle("-fx-background-color: #F0F0F0");
    }

    private void getExportSettingMousePressedEvent() {
        ScrollPaneValueUtils.setScrollVerticalValue(settingScrollPane, exportSettingVBox);
//        defaultSetting.setStyle("-fx-background-color: #F0F0F0");
        alarmSetting.setStyle("-fx-background-color: #F0F0F0");
        controlAlarmRule.setStyle("-fx-background-color: #F0F0F0");
        exportSetting.setStyle("-fx-background-color: #FFFFFF");
    }

    private void getCustomAlarmTableChangeEvent(CustomAlarmTestItemRowData customAlarmTestItemRowData) {
//        statisticsRuleRowDataObservableList.clear();
        testItemNameLabel.setText("");
        if (customAlarmTestItemRowData != null) {
            List<CustomAlarmDto> customAlarmDtoList = customAlarmTestItemRowData.getCustomAlarmDtoList();
//            if (customAlarmDtoList == null) {
//                return;
//            }
//            for (CustomAlarmDto customAlarmDto : customAlarmDtoList) {
//                statisticsRuleRowDataObservableList.add(new StatisticsResultRuleRowData(customAlarmDto));
//            }
            statisticsRuleModel.initData(customAlarmDtoList);
            testItemNameLabel.setText(customAlarmTestItemRowData.getName());
        }
    }

    private void getFilterTestItemTfEvent(String filterTf) {
        if (testItemRowDataFilteredList == null) {
            return;
        }
        testItemRowDataFilteredList.setPredicate(p -> {
            String testItem = p.getName();
            return testItem.toLowerCase().contains(filterTf.toLowerCase());
        });
    }

    private void getApplyBtnEvent() {
        if (!isSave()) {
            return;
        }
        saveSetting();
//        Job job = new Job(ParamKeys.SAVE_SPC_SETTING_DATA_JOP_PIPELINE);
//        manager.doJobSyn(job, spcSettingDto);
    }

    private void getCancelBtnEvent() {
        StageMap.closeStage(StateKey.SPC_SETTING);
    }

    private void getOkBtnEvent() {
        if (!isSave()) {
            return;
        }
        saveSetting();
//        Job job = new Job(ParamKeys.SAVE_SPC_SETTING_DATA_JOP_PIPELINE);
//        manager.doJobSyn(job, spcSettingDto);
        StageMap.closeStage(StateKey.SPC_SETTING);
    }

    private boolean isSave() {
        boolean result = SpcSettingValidateUtil.newInstance().hasErrorResult();
        boolean controlAlarmHasError = controlAlarmRuleTableModel.hasErrorEditValue();
        boolean statisticalAlarmHasError = statisticsRuleModel.hasErrorEditValue();
        if (result || controlAlarmHasError || statisticalAlarmHasError) {
            WindowMessageFactory.createWindowMessageHasOk(SpcFxmlAndLanguageUtils.getString(ResourceMassages.TIP_WARN_HEADER), SpcFxmlAndLanguageUtils.getString(ResourceMassages.SPC_SETTING_APPLY_WARN_MESSAGE));
            return false;
        }
        return true;
    }

    private void getAddTestItemEvent() {
        if (StageMap.getStage(StateKey.SPC_SETTING_ADD_ITEM) != null) {
            StageMap.showStage(StateKey.SPC_SETTING_ADD_ITEM);
        } else {
            Pane root = null;
            try {
                FXMLLoader fxmlLoader = SpcFxmlAndLanguageUtils.getLoaderFXML("view/add_item_dialog.fxml");
                root = fxmlLoader.load();
                addItemController = fxmlLoader.getController();
                addItemController.setSpcSettingController(this);
                Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel(StateKey.SPC_SETTING_ADD_ITEM, SpcFxmlAndLanguageUtils.getString(ResourceMassages.ADD_TEST_ITEMS), root, getClass().getClassLoader().getResource("css/spc_app.css").toExternalForm());
                stage.toFront();
                stage.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (addItemController != null) {
            addItemController.initData(getAlarmTestItemList());
        }
    }

    private void getExportTemplateSettingEvent() {
        if (StageMap.getStage(StateKey.SPC_EXPORT_TEMPLATE_SETTING) != null) {
            if (spcExportSettingController != null) {
                spcExportSettingController.initData();
            }
            StageMap.showStage(StateKey.SPC_EXPORT_TEMPLATE_SETTING);
        } else {
            this.initSpcExportSettingDialog();
        }
    }

    private void initSpcExportSettingDialog() {
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = SpcFxmlAndLanguageUtils.getLoaderFXML("view/spc_export_setting.fxml");
            root = fxmlLoader.load();
            spcExportSettingController = fxmlLoader.getController();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel(StateKey.SPC_EXPORT_TEMPLATE_SETTING, SpcFxmlAndLanguageUtils.getString(ResourceMassages.EXPORT_SETTING_TITLE), root, getClass().getClassLoader().getResource("css/spc_app.css").toExternalForm());
            stage.toFront();
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private SpcSettingDto buildSaveSettingData() {
        SpcSettingDto spcSettingDto = new SpcSettingDto();
//        spcSettingDto.setCustomGroupNumber(Integer.valueOf(subgroupSizeTf.getText()));
//        spcSettingDto.setChartIntervalNumber(Integer.valueOf(ndChartNumberTf.getText()));

        spcSettingDto.setAbilityAlarmRule(this.buildProcessAlarmData());
        spcSettingDto.setStatisticalAlarmSetting(this.buildStatisticalAlarmData());
        spcSettingDto.setControlChartRule(controlAlarmRuleTableModel.getControlRuleDtoList());

        spcSettingDto.setExportTemplateName(String.valueOf(exportTemplateCb.getValue()));
        return spcSettingDto;
    }

    private Map<String, Double[]> buildProcessAlarmData() {
        SpcProCapAlarmKey[] proCapAlarmKeys = SpcProCapAlarmKey.values();
        if (proCapAlarmKeys == null) {
            return null;
        }
        Map<String, Double[]> abilityAlarmRule = Maps.newHashMap();
        for (int i = 0; i < proCapAlarmKeys.length; i++) {
            String key = proCapAlarmKeys[i].getCode();

            Double[] value = new Double[4];
            if (SpcProCapAlarmKey.CA.getCode().equals(key)) {
                value[0] = Double.valueOf(caExcellentTf.getText());
                value[1] = Double.valueOf(caAcceptableTf.getText());
                value[2] = Double.valueOf(caRectificationTf.getText());
            } else if (SpcProCapAlarmKey.CP.getCode().equals(key)) {
                value[0] = Double.valueOf(cpExcellentTf.getText());
                value[1] = Double.valueOf(cpGoodTf.getText());
                value[2] = Double.valueOf(cpAcceptableTf.getText());
                value[3] = Double.valueOf(cpRectificationTf.getText());
            } else if (SpcProCapAlarmKey.CPK.getCode().equals(key)) {
                value[0] = Double.valueOf(cpkExcellentTf.getText());
                value[1] = Double.valueOf(cpkGoodTf.getText());
                value[2] = Double.valueOf(cpkAcceptableTf.getText());
                value[3] = Double.valueOf(cpkRectificationTf.getText());
            } else if (SpcProCapAlarmKey.CPL.getCode().equals(key)) {
                value[0] = Double.valueOf(cplExcellentTf.getText());
                value[1] = Double.valueOf(cplGoodTf.getText());
                value[2] = Double.valueOf(cplAcceptableTf.getText());
                value[3] = Double.valueOf(cplRectificationTf.getText());
            } else if (SpcProCapAlarmKey.CPU.getCode().equals(key)) {
                value[0] = Double.valueOf(cpuExcellentTf.getText());
                value[1] = Double.valueOf(cpuGoodTf.getText());
                value[2] = Double.valueOf(cpuAcceptableTf.getText());
                value[3] = Double.valueOf(cpuRectificationTf.getText());
            } else if (SpcProCapAlarmKey.PP.getCode().equals(key)) {
                value[0] = Double.valueOf(ppExcellentTf.getText());
                value[1] = Double.valueOf(ppGoodTf.getText());
                value[2] = Double.valueOf(ppAcceptableTf.getText());
                value[3] = Double.valueOf(ppRectificationTf.getText());
            } else if (SpcProCapAlarmKey.PPK.getCode().equals(key)) {
                value[0] = Double.valueOf(ppkExcellentTf.getText());
                value[1] = Double.valueOf(ppkGoodTf.getText());
                value[2] = Double.valueOf(ppkAcceptableTf.getText());
                value[3] = Double.valueOf(ppkRectificationTf.getText());
            } else if (SpcProCapAlarmKey.PPL.getCode().equals(key)) {
                value[0] = Double.valueOf(pplExcellentTf.getText());
                value[1] = Double.valueOf(pplGoodTf.getText());
                value[2] = Double.valueOf(pplAcceptableTf.getText());
                value[3] = Double.valueOf(pplRectificationTf.getText());
            } else if (SpcProCapAlarmKey.PPU.getCode().equals(key)) {
                value[0] = Double.valueOf(ppuExcellentTf.getText());
                value[1] = Double.valueOf(ppuGoodTf.getText());
                value[2] = Double.valueOf(ppuAcceptableTf.getText());
                value[3] = Double.valueOf(ppuRectificationTf.getText());
            }
            abilityAlarmRule.put(key, value);
        }
        return abilityAlarmRule;
    }

    private Map<String, List<CustomAlarmDto>> buildStatisticalAlarmData() {
        Map<String, List<CustomAlarmDto>> statisticalAlarmSetting = Maps.newHashMap();
        if (testItemRowDataObservableList != null) {
            for (CustomAlarmTestItemRowData customAlarmTestItemRowData : testItemRowDataObservableList) {
                statisticalAlarmSetting.put(customAlarmTestItemRowData.getName(), customAlarmTestItemRowData.getCustomAlarmDtoList());
            }
        }
        return statisticalAlarmSetting;
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

    private List<String> getAlarmTestItemList() {
        List<String> testItem = Lists.newArrayList();
        if (testItemRowDataObservableList == null) {
            return testItem;
        }
        for (CustomAlarmTestItemRowData testItemRowData : testItemRowDataObservableList) {
            testItem.add(testItemRowData.getName());
        }
        return testItem;
    }

    private void saveSetting() {
        SpcSettingDto spcSettingDto = this.buildSaveSettingData();
        JobContext context = RuntimeContext.getBean(JobFactory.class).createJobContext();
        context.put(ParamKeys.SPC_SETTING_DTO, spcSettingDto);
        RuntimeContext.getBean(JobManager.class).fireJobSyn(ParamKeys.SAVE_SPC_SETTING_DATA_JOP_PIPELINE, context);
    }

}