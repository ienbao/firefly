package com.dmsoft.firefly.plugin.grr.controller;

import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.plugin.grr.service.impl.GrrConfigServiceImpl;
import com.dmsoft.firefly.plugin.grr.utils.enums.GrrExportItemKey;
import com.google.common.collect.Maps;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.Map;

/**
 * Created by GuangLi on 2018/3/8.
 */
public class GrrExportSettingController {
    @FXML
    private RadioButton toleranceRBtn;
    @FXML
    private RadioButton contributeRBtn;
    @FXML
    private VBox pane;
    @FXML
    private GridPane chartPane;
    @FXML
    private Button ok;
    @FXML
    private Button cancel;
    @FXML
    private CheckBox chartCbx;
    @FXML
    private CheckBox rrPartCbx;
    @FXML
    private CheckBox rrAppraiserCbx;
    @FXML
    private CheckBox rangeCbx;
    @FXML
    private CheckBox xBarCbx;
    @FXML
    private CheckBox partAppraiserCbx;
    @FXML
    private CheckBox componentCbx;
    @FXML
    private CheckBox sourceCbx;
    @FXML
    private CheckBox exportDetailCbx;

    private ToggleGroup group = new ToggleGroup();
    private Map<String, Boolean> grrExportSettingConfigMap = Maps.newHashMap();
    private Map<String, CheckBox> checkBoxMap = Maps.newHashMap();
    private Map<String, RadioButton> radioButtonMap = Maps.newHashMap();
    private GrrConfigServiceImpl grrConfigService = new GrrConfigServiceImpl();

    @FXML
    private void initialize() {
        toleranceRBtn.setToggleGroup(group);
        contributeRBtn.setToggleGroup(group);
        toleranceRBtn.setSelected(true);
        this.initSelectedMap();
        initData();
        initEvent();
    }

    private void initSelectedMap() {
        GrrExportItemKey[] grrExportItemKeys = GrrExportItemKey.values();
        if (grrExportItemKeys == null) {
            return;
        }
        for (int i = 0; i < grrExportItemKeys.length; i++) {
            String key = grrExportItemKeys[i].getCode();
            if (GrrExportItemKey.EXPORT_DETAIL_SHEET.getCode().equals(key)) {
                checkBoxMap.put(key, exportDetailCbx);
            } else if (GrrExportItemKey.EXPORT_SOURCE_RESULT.getCode().equals(key)) {
                checkBoxMap.put(key, sourceCbx);
            } else if (GrrExportItemKey.EXPORT_CHART.getCode().equals(key)) {
                checkBoxMap.put(key, chartCbx);
            } else if (GrrExportItemKey.EXPORT_R_PART_CHART.getCode().equals(key)) {
                checkBoxMap.put(key, rrPartCbx);
            } else if (GrrExportItemKey.EXPORT_R_APPRAISER_CHART.getCode().equals(key)) {
                checkBoxMap.put(key, rrAppraiserCbx);
            } else if (GrrExportItemKey.EXPORT_RANGE_CHART.getCode().equals(key)) {
                checkBoxMap.put(key, rangeCbx);
            } else if (GrrExportItemKey.EXPORT_X_BAR_CHART.getCode().equals(key)) {
                checkBoxMap.put(key, xBarCbx);
            } else if (GrrExportItemKey.EXPORT_PART_APPRAISER_CHART.getCode().equals(key)) {
                checkBoxMap.put(key, partAppraiserCbx);
            } else if (GrrExportItemKey.EXPORT_COMPONENT_CHART.getCode().equals(key)) {
                checkBoxMap.put(key, componentCbx);
            } else if (GrrExportItemKey.EXPORT_BASE_ON_TOLERANCE.getCode().equals(key)) {
                radioButtonMap.put(key, toleranceRBtn);
            } else if (GrrExportItemKey.EXPORT_BASE_ON_CONTRIBUTE.getCode().equals(key)) {
                radioButtonMap.put(key, contributeRBtn);
            }
        }
    }

    private void initData() {
        grrExportSettingConfigMap = grrConfigService.findGrrExportConfig();
        if (grrExportSettingConfigMap != null) {
            for (Map.Entry<String, Boolean> entry : grrExportSettingConfigMap.entrySet()) {
                String key = entry.getKey();
                Boolean selectable = entry.getValue();
                boolean hasRadioButtonText = GrrExportItemKey.EXPORT_BASE_ON_TOLERANCE.getCode().equals(key);
                hasRadioButtonText = hasRadioButtonText || GrrExportItemKey.EXPORT_BASE_ON_CONTRIBUTE.getCode().equals(key);
                if (hasRadioButtonText) {
                    radioButtonMap.get(entry.getKey()).selectedProperty().setValue(selectable);
                    continue;
                }
                if (!checkBoxMap.containsKey(key)) {
                    return;
                }
                checkBoxMap.get(key).selectedProperty().setValue(selectable);
                if (GrrExportItemKey.EXPORT_DETAIL_SHEET.getCode().equals(key)) {
                    toggleAllCheckBox(!selectable);
                    continue;
                }
                if (GrrExportItemKey.EXPORT_CHART.getCode().equals(key)) {
                    boolean exportDetailSelected = exportDetailCbx.selectedProperty().getValue();
                    toggleAllChartCheckBox(exportDetailSelected ? !selectable : !exportDetailSelected);
                }
            }
        }
    }

    private void toggleAllCheckBox(boolean flag) {
        chartCbx.setDisable(flag);
        sourceCbx.setDisable(flag);
        toggleAllChartCheckBox(flag ? flag : !chartCbx.selectedProperty().getValue());
    }

    private void toggleAllChartCheckBox(boolean flag) {
        xBarCbx.setDisable(flag);
        rangeCbx.setDisable(flag);
        rrPartCbx.setDisable(flag);
        componentCbx.setDisable(flag);
        rrAppraiserCbx.setDisable(flag);
        partAppraiserCbx.setDisable(flag);
    }

    private void initEvent() {
        exportDetailCbx.setOnAction(event -> {
            toggleAllCheckBox(!exportDetailCbx.selectedProperty().getValue());
        });
        chartCbx.setOnAction(event -> {
            toggleAllChartCheckBox(!chartCbx.selectedProperty().getValue());
        });
        ok.setOnAction(event -> {
            saveData();
            StageMap.closeStage("grrExportSetting");
        });
        cancel.setOnAction(event -> StageMap.closeStage("grrExportSetting"));
    }

    private void saveData() {
        if (grrExportSettingConfigMap != null) {
            for (Map.Entry<String, CheckBox> entry : checkBoxMap.entrySet()) {
                grrExportSettingConfigMap.put(entry.getKey(), entry.getValue().selectedProperty().getValue());
            }
            for (Map.Entry<String, RadioButton> entry : radioButtonMap.entrySet()) {
                grrExportSettingConfigMap.put(entry.getKey(), entry.getValue().selectedProperty().getValue());
            }
            grrConfigService.saveGrrExportConfig(grrExportSettingConfigMap);
        }
    }

    public void setData(Map<String, Boolean> data) {
        this.grrExportSettingConfigMap = data;
    }
}
