package com.dmsoft.firefly.plugin.yield.controller;

import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.window.WindowMessageFactory;
import com.dmsoft.firefly.plugin.yiela.spc.controller.SpcSettingController;
import com.dmsoft.firefly.plugin.yiela.spc.utils.ImageUtils;
import com.dmsoft.firefly.plugin.yield.dto.YieldSettingDto;
import com.dmsoft.firefly.plugin.yield.handler.ParamKeys;
import com.dmsoft.firefly.plugin.yiela.yield.utils.*;
import com.dmsoft.firefly.plugin.yield.utils.*;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.job.core.JobContext;
import com.dmsoft.firefly.sdk.job.core.JobFactory;
import com.dmsoft.firefly.sdk.job.core.JobManager;
import com.google.common.collect.Maps;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class YieldSettingController implements Initializable {
    private final Logger logger = LoggerFactory.getLogger(SpcSettingController.class);
    @FXML
    private Label alarmSetting;
    @FXML
    private Label defaultSetting;
    @FXML
    private Label exportMode;
    @FXML
    private Button apply;
    @FXML
    private Button cancel;
    @FXML
    private Button ok;

    //default setting
    @FXML
    private ComboBox defaultSettingCb;

    //Export Template Setting
    @FXML
    private ComboBox exportTemplateCb;
    @FXML
    private Button exportTemplateSettingBtn;

    @FXML
    private ScrollPane settingScrollPane;

//    private SpcExportSettingController spcExportSettingController;
    //CP
    @FXML
    private TextField FPYExcellentTf;
    @FXML
    private TextField FPYGoodTf;
    @FXML
    private TextField FPYAcceptableTf;

    //CPK
    @FXML
    private TextField NTFExcellentTf;
    @FXML
    private TextField NTFGoodTf;
    @FXML
    private TextField NTFAcceptableTf;

    //CPL
    @FXML
    private TextField NGExcellentTf;
    @FXML
    private TextField NGGoodTf;
    @FXML
    private TextField NGAcceptableTf;

    @FXML
    private VBox defaultSettingVBox, alarmSettingVBox,  exportSettingVBox;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.initBtnIcon();
//        this.initComponent();
        this.initData();
        this.initComponentEvent();
//        this.initValidate();
    }
    /**
     * init data
     */
    public void initData() {
        JobContext context = RuntimeContext.getBean(JobFactory.class).createJobContext();
        RuntimeContext.getBean(JobManager.class).fireJobSyn(ParamKeys.FIND_YIELD_SETTING_DATA_JOP_PIPELINE, context);

        YieldSettingDto yieldSettingDto = context.getParam(ParamKeys.YIELD_SETTING_DTO, YieldSettingDto.class);
        this.setProcessAlarmSettingData(yieldSettingDto.getAbilityAlarmRule());
        this.setExportSettingData(yieldSettingDto.getExportTemplateName());
        this.setPrimaryKey(yieldSettingDto.getPrimaryKey());
    }


    private void initBtnIcon() {
        exportTemplateSettingBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_setting_normal.png")));
        exportTemplateSettingBtn.setPrefSize(22, 22);
    }

    private void initComponentEvent() {
        apply.setOnAction(event -> getApplyBtnEvent());
        cancel.setOnAction(event -> getCancelBtnEvent());
        ok.setOnAction(event -> getOkBtnEvent());
//        exportTemplateSettingBtn.setOnAction(event -> getExportTemplateSettingEvent());
        defaultSetting.setOnMousePressed(defaultSetting -> getDefaultSettingMousePressedEvent());
        alarmSetting.setOnMousePressed(defaultSetting -> getAlarmSettingMousePressedEvent());
        exportMode.setOnMousePressed(defaultSetting -> getExportSettingMousePressedEvent());
    }
    private String alarmDataToText(Double value) {
        if (value == null) {
            return "";
        }
        return String.valueOf(value);
    }

    private void setProcessAlarmSettingData(Map<String, Double[]> abilityAlarmRule) {
        YieldProCapAlarmKey[] proCapAlarmKeys = YieldProCapAlarmKey.values();
        if (proCapAlarmKeys == null) {
            return;
        }
        for (int i = 0; i < proCapAlarmKeys.length; i++) {
            String key = proCapAlarmKeys[i].getCode();
            Double[] value = abilityAlarmRule.get(key);
            if (YieldProCapAlarmKey.FPY.getCode().equals(key)) {
                FPYExcellentTf.setText(alarmDataToText(value[0]));
                FPYGoodTf.setText(alarmDataToText(value[1]));
                FPYAcceptableTf.setText(alarmDataToText(value[2]));
            } else if (YieldProCapAlarmKey.NTF.getCode().equals(key)) {
                NTFExcellentTf.setText(alarmDataToText(value[0]));
                NTFGoodTf.setText(alarmDataToText(value[1]));
                NTFAcceptableTf.setText(alarmDataToText(value[2]));

            } else if (YieldProCapAlarmKey.NG.getCode().equals(key)) {
                NGExcellentTf.setText(alarmDataToText(value[0]));
                NGGoodTf.setText(alarmDataToText(value[1]));
                NGAcceptableTf.setText(alarmDataToText(value[2]));

            }
        }
    }
    private void setExportSettingData(String exportTemplateName) {
        exportTemplateCb.setValue(exportTemplateName);
    }
    private void setPrimaryKey(String primaryKey) {
        defaultSettingCb.setValue(primaryKey);
    }
    private void getAlarmSettingMousePressedEvent() {
        ScrollPaneValueUtils.setScrollVerticalValue(settingScrollPane, alarmSettingVBox);
        defaultSetting.setStyle("-fx-background-color: #F0F0F0");
        alarmSetting.setStyle("-fx-background-color: #FFFFFF");
        exportMode.setStyle("-fx-background-color: #F0F0F0");
    }
    private void getExportSettingMousePressedEvent() {
        ScrollPaneValueUtils.setScrollVerticalValue(settingScrollPane, exportSettingVBox);
        defaultSetting.setStyle("-fx-background-color: #F0F0F0");
        alarmSetting.setStyle("-fx-background-color: #F0F0F0");
        exportMode.setStyle("-fx-background-color: #FFFFFF");
    }
    private void getDefaultSettingMousePressedEvent() {
        ScrollPaneValueUtils.setScrollVerticalValue(settingScrollPane, defaultSettingVBox);
        defaultSetting.setStyle("-fx-background-color: #FFFFFF");
        alarmSetting.setStyle("-fx-background-color: #F0F0F0");
        exportMode.setStyle("-fx-background-color: #F0F0F0");
    }
    private void getCancelBtnEvent() {
        StageMap.closeStage(StateKey.YIELD_SETTING);
    }
    private void getApplyBtnEvent() {
        if (!isSave()) {
            return;
        }
        saveSetting();

    }
    private void getOkBtnEvent() {
        if (!isSave()) {
            return;
        }
        saveSetting();
        StageMap.closeStage(StateKey.YIELD_SETTING);
    }
    private boolean isSave() {
        boolean result = YieldSettingValidateUtil.newInstance().hasErrorResult();

        if (result ) {
            WindowMessageFactory.createWindowMessageHasOk(YieldFxmlAndLanguageUtils.getString(ResourceMassages.TIP_WARN_HEADER),YieldFxmlAndLanguageUtils.getString(ResourceMassages.YIELD_SETTING_APPLY_WARN_MESSAGE));
            return false;
        }
        return true;
    }
    private void saveSetting() {
        YieldSettingDto yieldSettingDto = this.buildSaveSettingData();
        JobContext context = RuntimeContext.getBean(JobFactory.class).createJobContext();
        context.put(ParamKeys.YIELD_SETTING_DTO, yieldSettingDto);
        RuntimeContext.getBean(JobManager.class).fireJobSyn(ParamKeys.SAVE_YIELD_SETTING_DATA_JOP_PIPELINE, context);
    }
    private YieldSettingDto buildSaveSettingData() {
        YieldSettingDto yieldSettingDto = new YieldSettingDto();
        yieldSettingDto.setAbilityAlarmRule(this.buildProcessAlarmData());
        yieldSettingDto.setPrimaryKey(String.valueOf(defaultSettingCb.getValue()));
        yieldSettingDto.setExportTemplateName(String.valueOf(exportTemplateCb.getValue()));
        return yieldSettingDto;
    }
    private Map<String, Double[]> buildProcessAlarmData() {
        YieldProCapAlarmKey[] proCapAlarmKeys = YieldProCapAlarmKey.values();
        if (proCapAlarmKeys == null) {
            return null;
        }
        Map<String, Double[]> abilityAlarmRule = Maps.newHashMap();
        for (int i = 0; i < proCapAlarmKeys.length; i++) {
            String key = proCapAlarmKeys[i].getCode();

            Double[] value = new Double[3];
            if (YieldProCapAlarmKey.FPY.getCode().equals(key)) {
                value[0] = Double.valueOf(FPYExcellentTf.getText());
                value[1] = Double.valueOf(FPYGoodTf.getText());
                value[2] = Double.valueOf(FPYAcceptableTf.getText());
            } else if (YieldProCapAlarmKey.NTF.getCode().equals(key)) {
                value[0] = Double.valueOf(NTFExcellentTf.getText());
                value[1] = Double.valueOf(NTFGoodTf.getText());
                value[2] = Double.valueOf(NTFAcceptableTf.getText());

            } else if (YieldProCapAlarmKey.NG.getCode().equals(key)) {
                value[0] = Double.valueOf(NGExcellentTf.getText());
                value[1] = Double.valueOf(NGGoodTf.getText());
                value[2] = Double.valueOf(NGAcceptableTf.getText());
            }
            abilityAlarmRule.put(key, value);
        }
        return abilityAlarmRule;
    }
}