package com.dmsoft.firefly.plugin.yield.controller;

import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.gui.components.window.WindowMessageFactory;

import com.dmsoft.firefly.plugin.yield.dto.YieldSettingDto;
import com.dmsoft.firefly.plugin.yield.handler.ParamKeys;
import com.dmsoft.firefly.plugin.yield.utils.*;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.job.core.JobContext;
import com.dmsoft.firefly.sdk.job.core.JobFactory;
import com.dmsoft.firefly.sdk.job.core.JobManager;
import com.google.common.collect.Maps;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

@Component
public class YieldSettingController implements Initializable {
    private final Logger logger = LoggerFactory.getLogger(YieldSettingController.class);
    @FXML
    private Label alarmSetting;
    @FXML
    private Button apply;
    @FXML
    private Button cancel;
    @FXML
    private Button ok;
    @FXML
    private ScrollPane settingScrollPane;

    //FPY
    @FXML
    private TextField FPYExcellentTf;
    @FXML
    private TextField FPYGoodTf;
    @FXML
    private TextField FPYAcceptableTf;

    //NTF
    @FXML
    private TextField NTFExcellentTf;
    @FXML
    private TextField NTFGoodTf;
    @FXML
    private TextField NTFAcceptableTf;

    //NG
    @FXML
    private TextField NGExcellentTf;
    @FXML
    private TextField NGGoodTf;
    @FXML
    private TextField NGAcceptableTf;

    @FXML
    private VBox  alarmSettingVBox;

    @Autowired
    private JobFactory jobFactory;
    @Autowired
    private JobManager jobManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.initData();
        this.initComponentEvent();
        this.initValidate();
    }
    /**
     * init data
     */
    public void initData() {
        JobContext context = this.jobFactory.createJobContext();
        this.jobManager.fireJobSyn(ParamKeys.FIND_YIELD_SETTING_DATA_JOP_PIPELINE, context);

        YieldSettingDto yieldSettingDto = context.getParam(ParamKeys.YIELD_SETTING_DTO, YieldSettingDto.class);
        this.setProcessAlarmSettingData(yieldSettingDto.getAbilityAlarmRule());
    }

    private void initValidate() {

        YieldSettingValidateUtil.BindNode NTFBindNode = YieldSettingValidateUtil.newInstance().new BindNode(NTFExcellentTf, NTFGoodTf, NTFAcceptableTf);
        YieldSettingValidateUtil.BindNode FPYBindNode = YieldSettingValidateUtil.newInstance().new BindNode(FPYExcellentTf, FPYGoodTf, FPYAcceptableTf);
        YieldSettingValidateUtil.BindNode NGBindNode = YieldSettingValidateUtil.newInstance().new BindNode(NGExcellentTf, NGGoodTf, NGAcceptableTf);
        YieldSettingValidateUtil.newInstance().validateYieldAlarmSetting( FPYBindNode,NTFBindNode, NGBindNode);
    }

    private void initComponentEvent() {
        apply.setOnAction(event -> getApplyBtnEvent());
        cancel.setOnAction(event -> getCancelBtnEvent());
        ok.setOnAction(event -> getOkBtnEvent());
        alarmSetting.setOnMousePressed(defaultSetting -> getAlarmSettingMousePressedEvent());
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
            if (YieldProCapAlarmKey.FPYPER.getCode().equals(key)) {
                FPYExcellentTf.setText(alarmDataToText(value[0]));
                FPYGoodTf.setText(alarmDataToText(value[1]));
                FPYAcceptableTf.setText(alarmDataToText(value[2]));
            } else if (YieldProCapAlarmKey.NTFPER.getCode().equals(key)) {
                NTFExcellentTf.setText(alarmDataToText(value[0]));
                NTFGoodTf.setText(alarmDataToText(value[1]));
                NTFAcceptableTf.setText(alarmDataToText(value[2]));

            } else if (YieldProCapAlarmKey.NGPER.getCode().equals(key)) {
                NGExcellentTf.setText(alarmDataToText(value[0]));
                NGGoodTf.setText(alarmDataToText(value[1]));
                NGAcceptableTf.setText(alarmDataToText(value[2]));
            }
        }
    }

    private void getAlarmSettingMousePressedEvent() {
        ScrollPaneValueUtils.setScrollVerticalValue(settingScrollPane, alarmSettingVBox);
        alarmSetting.setStyle("-fx-background-color: #FFFFFF");
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
        JobContext context = this.jobFactory.createJobContext();
        context.put(ParamKeys.YIELD_SETTING_DTO, yieldSettingDto);
        this.jobManager.fireJobSyn(ParamKeys.SAVE_YIELD_SETTING_DATA_JOP_PIPELINE, context);
    }
    private YieldSettingDto buildSaveSettingData() {
        YieldSettingDto yieldSettingDto = new YieldSettingDto();
        yieldSettingDto.setAbilityAlarmRule(this.buildProcessAlarmData());
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
            if (YieldProCapAlarmKey.FPYPER.getCode().equals(key)) {
                value[0] = Double.valueOf(FPYExcellentTf.getText());
                value[1] = Double.valueOf(FPYGoodTf.getText());
                value[2] = Double.valueOf(FPYAcceptableTf.getText());
            } else if (YieldProCapAlarmKey.NTFPER.getCode().equals(key)) {
                value[0] = Double.valueOf(NTFExcellentTf.getText());
                value[1] = Double.valueOf(NTFGoodTf.getText());
                value[2] = Double.valueOf(NTFAcceptableTf.getText());

            } else if (YieldProCapAlarmKey.NGPER.getCode().equals(key)) {
                value[0] = Double.valueOf(NGExcellentTf.getText());
                value[1] = Double.valueOf(NGGoodTf.getText());
                value[2] = Double.valueOf(NGAcceptableTf.getText());
            }
            abilityAlarmRule.put(key, value);
        }
        return abilityAlarmRule;
    }
}