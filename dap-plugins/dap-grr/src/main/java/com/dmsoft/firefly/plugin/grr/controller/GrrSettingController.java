package com.dmsoft.firefly.plugin.grr.controller;

import com.dmsoft.firefly.gui.components.utils.*;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.gui.components.window.WindowMessageFactory;
import com.dmsoft.firefly.plugin.grr.dto.GrrConfigDto;
import com.dmsoft.firefly.plugin.grr.service.impl.GrrConfigServiceImpl;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.dmsoft.firefly.plugin.grr.utils.ResourceMassages;
import com.dmsoft.firefly.plugin.grr.utils.UIConstant;
import com.dmsoft.firefly.plugin.grr.utils.enums.GrrAnalysisMethod;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by GuangLi on 2018/3/6.
 */
public class GrrSettingController {
    @FXML
    private Label defaultSetting;
    @FXML
    private Label alarmSetting;
    @FXML
    private Label exportSetting;
    @FXML
    private RadioButton anova;
    @FXML
    private RadioButton xbar;
    @FXML
    private ComboBox<Double> coverage;
    @FXML
    private TextField sign;
    @FXML
    private ComboBox<String> sort;
    @FXML
    private ComboBox<String> exportTemplate;
    @FXML
    private TextField levelGood;
    @FXML
    private TextField levelBad;
    @FXML
    private Button exportBtn;
    @FXML
    private Button ok;
    @FXML
    private Button cancel;
    @FXML
    private Button apply;
    private ToggleGroup group = new ToggleGroup();

    private GrrConfigServiceImpl grrConfigService = new GrrConfigServiceImpl();
    private Map<String, Boolean> grrExportSetting = Maps.newHashMap();

    private Map<String, String> i18nMap = Maps.newHashMap();

    @FXML
    private void initialize() {
        initLabelStyle();
        ValidateRule rule = new ValidateRule();
        rule.setMaxLength(255);
        rule.setPattern("^[+]?\\d*[.]?\\d*$");
        rule.setMaxValue(1.0);
        rule.setMinValue(0.0);
        rule.setErrorStyle("text-field-error");
        rule.setEmptyErrorMsg(GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SETTING_RULE_NO_EMPTY));
        rule.setRangErrorMsg(GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SETTING_RULE_INVALID_RANGE));
        TextFieldWrapper.decorate(sign, rule);
        exportBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/setting.svg")));
        exportTemplate.setItems(FXCollections.observableArrayList(GrrFxmlAndLanguageUtils.getString(ResourceMassages.GEE_EXPORT_TEMPLATE)));
        exportTemplate.setValue(GrrFxmlAndLanguageUtils.getString(ResourceMassages.GEE_EXPORT_TEMPLATE));
        anova.setToggleGroup(group);
        anova.setSelected(true);
        xbar.setToggleGroup(group);
        coverage.setItems(FXCollections.observableArrayList(5.15, 6.0));
        coverage.setValue(5.15);
        sort.setItems(FXCollections.observableArrayList(
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SETTING_SORT_DATA_BY_APPRAISERS),
                GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SETTING_SORT_DATA_BY_DEFAULT)));
        sort.getSelectionModel().select(0);
        levelGood.setText("5");
        levelBad.setText("10");
        initI18nMap();
        initConfigData();
        initEvent();
    }

    private void initI18nMap() {
        i18nMap.put(GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SETTING_SORT_DATA_BY_APPRAISERS), UIConstant.GRR_SETTING_SORT_BY_APPRAISERS);
        i18nMap.put(GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SETTING_SORT_DATA_BY_DEFAULT), UIConstant.GRR_SETTING_SORT_BY_DEFAULT);
    }

    private void initConfigData() {
        GrrConfigDto grrConfigDto = grrConfigService.findGrrConfig();
        if (grrConfigDto != null) {
            if (grrConfigDto.getExport() != null) {
                grrExportSetting = grrConfigDto.getExport();
            }
            if (grrConfigDto.getAnalysisMethod().equals(GrrAnalysisMethod.ANOVA)) {
                anova.setSelected(true);
                anova.requestFocus();
            } else {
                xbar.setSelected(true);
                xbar.requestFocus();
            }
            coverage.setValue(grrConfigDto.getCoverage());
            sign.setText(grrConfigDto.getSignLevel());
            i18nMap.forEach((key, value) -> {
                if (value.equals(grrConfigDto.getSortMethod())) {
                    sort.setValue(key);
                }
            });

            levelGood.setText(grrConfigDto.getAlarmSetting().get(0).toString());
            levelBad.setText(grrConfigDto.getAlarmSetting().get(1).toString());
        }
    }

    private void initEvent() {
        levelGood.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                checkInValid(levelGood, levelBad, oldValue, true);
            }
        });
        levelBad.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                checkInValid(levelGood, levelBad, oldValue, false);
            }
        });

        exportBtn.setOnAction(event -> buildExportDia());
        ok.setOnAction(event -> {
            if (DAPStringUtils.isEmpty(levelBad.getText()) || DAPStringUtils.isEmpty(levelGood.getText())) {
                WindowMessageFactory.createWindowMessageHasCancel("Message", GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SETTING_LEVEL_NO_EMPTY));
                return;
            }
            if (levelBad.getStyleClass().contains("text-field-error") || levelGood.getStyleClass().contains("text-field-error")) {
                WindowMessageFactory.createWindowMessageHasCancel("Message", GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SETTING_LEVEL_LEVEL_MUST_BIGGER));
                return;
            }
            if (sign.getStyleClass().contains("text-field-error")) {
                WindowMessageFactory.createWindowMessageHasCancel("Message", GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SETTING_LEVEL_INPUT_ERROR));
                return;
            }
            saveGrrSetting();
            StageMap.closeStage("grrSetting");
        });
        cancel.setOnAction(event -> {
            StageMap.closeStage("grrSetting");
        });
        apply.setOnAction(event -> {
            if (DAPStringUtils.isEmpty(levelBad.getText()) || DAPStringUtils.isEmpty(levelGood.getText())) {
                WindowMessageFactory.createWindowMessageHasCancel("Message", GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SETTING_LEVEL_NO_EMPTY));
                return;
            }
            if (levelBad.getStyleClass().contains("text-field-error") || levelGood.getStyleClass().contains("text-field-error")) {
                WindowMessageFactory.createWindowMessageHasCancel("Message", GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SETTING_LEVEL_LEVEL_MUST_BIGGER));
                return;
            }
            if (sign.getStyleClass().contains("text-field-error")) {
                WindowMessageFactory.createWindowMessageHasCancel("Message", GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SETTING_LEVEL_INPUT_ERROR));
                return;
            }
            saveGrrSetting();
        });
    }

    private void checkInValid(TextField greaterData, TextField lessData, String oldValue, boolean currentGreaterData) {
        Pattern pattern = Pattern.compile("^[+]?\\d*[.]?\\d*$");
        String currentText = currentGreaterData ? greaterData.getText() : lessData.getText();
        String anotherText = currentGreaterData ? lessData.getText() : greaterData.getText();
        TextField currentTextField = currentGreaterData ? greaterData : lessData;
        TextField anotherTextField = currentGreaterData ? lessData : greaterData;
        boolean invalid = !pattern.matcher(currentText).matches();
        invalid = invalid || (!DAPStringUtils.isEmpty(currentText) && !DAPStringUtils.isNumeric(currentText));
        //special char check
        if (invalid) {
            TooltipUtil.installWarnTooltip(currentTextField, GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SETTING_RULE_MUST_NUMBER));
            if (!currentTextField.getStyleClass().contains("text-field-error")) {
                currentTextField.getStyleClass().add("text-field-error");
            }
            currentTextField.setText(oldValue);
        } else {

            // empty check
            if (DAPStringUtils.isEmpty(currentText)) {
                TooltipUtil.installWarnTooltip(currentTextField, GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SETTING_RULE_MUST_NUMBER));
                if (!currentTextField.getStyleClass().contains("text-field-error")) {
                    currentTextField.getStyleClass().add("text-field-error");
                }
                return;
            }

            //another check
            boolean anotherInvalid = DAPStringUtils.isEmpty(currentText) || !DAPStringUtils.isNumeric(anotherText);
            if (anotherInvalid) {
                TooltipUtil.uninstallWarnTooltip(currentTextField);
                if (currentTextField.getStyleClass().contains("text-field-error")) {
                    currentTextField.getStyleClass().remove("text-field-error");
                }
                return;
            }

            // compare check
            boolean compareInvalid = Double.valueOf(greaterData.getText()) >= Double.valueOf(lessData.getText());
            if (compareInvalid) {
                TooltipUtil.installWarnTooltip(currentTextField, GrrFxmlAndLanguageUtils.getString(UIConstant.GRR_SETTING_RULE_MUST_NUMBER));
                if (!currentTextField.getStyleClass().contains("text-field-error")) {
                    currentTextField.getStyleClass().add("text-field-error");
                }
                return;
            }

            TooltipUtil.uninstallWarnTooltip(currentTextField);
            TooltipUtil.uninstallWarnTooltip(anotherTextField);
            if (currentTextField.getStyleClass().contains("text-field-error")) {
                currentTextField.getStyleClass().remove("text-field-error");
            }

            if (anotherTextField.getStyleClass().contains("text-field-error")) {
                anotherTextField.getStyleClass().remove("text-field-error");
            }
        }
    }

    private void initLabelStyle() {
        defaultSetting.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                defaultSetting.getStyleClass().setAll("grr-setting-label-white");
                alarmSetting.getStyleClass().setAll("grr-setting-label");
                exportSetting.getStyleClass().setAll("grr-setting-label");
            }
        });
        alarmSetting.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                defaultSetting.getStyleClass().setAll("grr-setting-label");
                alarmSetting.getStyleClass().setAll("grr-setting-label-white");
                exportSetting.getStyleClass().setAll("grr-setting-label");
            }
        });
        exportSetting.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                defaultSetting.getStyleClass().setAll("grr-setting-label");
                alarmSetting.getStyleClass().setAll("grr-setting-label");
                exportSetting.getStyleClass().setAll("grr-setting-label-white");
            }
        });
    }

    private void saveGrrSetting() {
        GrrConfigDto grrConfigDto = new GrrConfigDto();
        if (anova.isSelected()) {
            grrConfigDto.setAnalysisMethod(GrrAnalysisMethod.ANOVA);
        } else {
            grrConfigDto.setAnalysisMethod(GrrAnalysisMethod.XbarAndRange);
        }
        grrConfigDto.setCoverage(coverage.getValue());
        grrConfigDto.setSignLevel(sign.getText());
        grrConfigDto.setSortMethod(i18nMap.get(sort.getValue()));
        List<Double> alarm = Lists.newArrayList();
        alarm.add(Double.valueOf(levelGood.getText()));
        alarm.add(Double.valueOf(levelBad.getText()));
        grrConfigDto.setAlarmSetting(alarm);
        grrConfigDto.setExport(grrExportSetting);
        grrConfigService.saveGrrConfig(grrConfigDto);
    }

    private void buildExportDia() {
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = GrrFxmlAndLanguageUtils.getLoaderFXML("view/grr_export_setting.fxml");
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("grrExportSetting", GrrFxmlAndLanguageUtils.getString(ResourceMassages.GRR_EXPORT_SETTING_TITLE), root, getClass().getClassLoader().getResource("css/grr_app.css").toExternalForm());
            stage.toFront();
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
