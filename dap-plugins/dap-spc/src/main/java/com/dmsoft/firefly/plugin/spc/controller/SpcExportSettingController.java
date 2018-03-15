/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.plugin.spc.service.SpcSettingService;
import com.dmsoft.firefly.plugin.spc.utils.StateKey;
import com.dmsoft.firefly.plugin.spc.utils.enums.SpcExportItemKey;
import com.dmsoft.firefly.plugin.spc.utils.enums.SpcProCapAlarmKey;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.google.common.collect.Maps;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Ethan.Yang on 2018/3/14.
 */
public class SpcExportSettingController implements Initializable {
    @FXML
    private Button saveBtn;
    @FXML
    private Button cancelBtn;

    private Map<String, CheckBox> checkBoxMap;
    private SpcSettingService spcSettingService = RuntimeContext.getBean(SpcSettingService.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.initComponentEvent();
        this.initCheckBoxMap();
        this.initData();
    }

    /**
     * init data
     */
    public void initData() {
        Map<String, Boolean> exportSetting = spcSettingService.findSpcExportTemplateSetting();
        if (exportSetting != null) {
            for (Map.Entry<String, Boolean> entry : exportSetting.entrySet()) {
                Boolean isSelect = entry.getValue();
                if (checkBoxMap.get(entry.getKey()) != null) {
                    checkBoxMap.get(entry.getKey()).selectedProperty().setValue(isSelect);
                }
            }
        }
    }

    private void initCheckBoxMap() {
        SpcExportItemKey[] spcExportItemKeys = SpcExportItemKey.values();
        if (spcExportItemKeys == null) {
            return;
        }
        checkBoxMap = Maps.newHashMap();
        for (int i = 0; i < spcExportItemKeys.length; i++) {
            String key = spcExportItemKeys[i].getCode();
            if (SpcExportItemKey.EXPORT_SUB_SUMMARY.getCode().equals(key)) {
                checkBoxMap.put(key, exportSummary);
            } else if (SpcExportItemKey.EXPORT_SUB_SUMMARY.getCode().equals(key)) {
                checkBoxMap.put(key, exportDetailSheet);
            } else if (SpcExportItemKey.EXPORT_DETAIL_SHEET.getCode().equals(key)) {
                checkBoxMap.put(key, exportCharts);
            } else if (SpcExportItemKey.ND_CHART.getCode().equals(key)) {
                checkBoxMap.put(key, ndChart);
            } else if (SpcExportItemKey.RUN_CHART.getCode().equals(key)) {
                checkBoxMap.put(key, runChart);
            } else if (SpcExportItemKey.X_BAR_CHART.getCode().equals(key)) {
                checkBoxMap.put(key, xBarChart);
            } else if (SpcExportItemKey.RANGE_CHART.getCode().equals(key)) {
                checkBoxMap.put(key, rangeChart);
            } else if (SpcExportItemKey.SD_CHART.getCode().equals(key)) {
                checkBoxMap.put(key, sdChart);
            } else if (SpcExportItemKey.MEDIAN_CHART.getCode().equals(key)) {
                checkBoxMap.put(key, medianChart);
            } else if (SpcExportItemKey.BOX_CHART.getCode().equals(key)) {
                checkBoxMap.put(key, boxChart);
            } else if (SpcExportItemKey.MR_CHART.getCode().equals(key)) {
                checkBoxMap.put(key, mrChart);
            } else if (SpcExportItemKey.DESCRIPTIVE_STATISTICS.getCode().equals(key)) {
                checkBoxMap.put(key, statistics);
            } else if (SpcExportItemKey.SAMPLES.getCode().equals(key)) {
                checkBoxMap.put(key, samples);
            } else if (SpcExportItemKey.MEAN.getCode().equals(key)) {
                checkBoxMap.put(key, mean);
            } else if (SpcExportItemKey.SD.getCode().equals(key)) {
                checkBoxMap.put(key, sd);
            } else if (SpcExportItemKey.RANGE.getCode().equals(key)) {
                checkBoxMap.put(key, range);
            } else if (SpcExportItemKey.Max.getCode().equals(key)) {
                checkBoxMap.put(key, max);
            } else if (SpcExportItemKey.Min.getCode().equals(key)) {
                checkBoxMap.put(key, min);
            } else if (SpcExportItemKey.LCL.getCode().equals(key)) {
                checkBoxMap.put(key, lcl);
            } else if (SpcExportItemKey.UCL.getCode().equals(key)) {
                checkBoxMap.put(key, ucl);
            } else if (SpcExportItemKey.KURTOSIS.getCode().equals(key)) {
                checkBoxMap.put(key, kurtosis);
            } else if (SpcExportItemKey.SKEWNESS.getCode().equals(key)) {
                checkBoxMap.put(key, skewness);
            } else if (SpcExportItemKey.PROCESS_CAPABILITY_INDEX.getCode().equals(key)) {
                checkBoxMap.put(key, processCapability);
            } else if (SpcExportItemKey.CA.getCode().equals(key)) {
                checkBoxMap.put(key, ca);
            } else if (SpcExportItemKey.CP.getCode().equals(key)) {
                checkBoxMap.put(key, cp);
            } else if (SpcExportItemKey.CPU.getCode().equals(key)) {
                checkBoxMap.put(key, cpu);
            } else if (SpcExportItemKey.CPL.getCode().equals(key)) {
                checkBoxMap.put(key, cpl);
            } else if (SpcExportItemKey.CPK.getCode().equals(key)) {
                checkBoxMap.put(key, cpk);
            } else if (SpcExportItemKey.WITHIN_PPM.getCode().equals(key)) {
                checkBoxMap.put(key, withinPPM);
            } else if (SpcExportItemKey.PROCESS_PERFORMANCE_INDEX.getCode().equals(key)) {
                checkBoxMap.put(key, processPerformance);
            } else if (SpcExportItemKey.PP.getCode().equals(key)) {
                checkBoxMap.put(key, pp);
            } else if (SpcExportItemKey.PPU.getCode().equals(key)) {
                checkBoxMap.put(key, ppu);
            } else if (SpcExportItemKey.PPL.getCode().equals(key)) {
                checkBoxMap.put(key, ppl);
            } else if (SpcExportItemKey.PPK.getCode().equals(key)) {
                checkBoxMap.put(key, ppk);
            } else if (SpcExportItemKey.OVERALL_PPM.getCode().equals(key)) {
                checkBoxMap.put(key, overallPPM);
            }
        }
    }


    private void initComponentEvent() {
        saveBtn.setOnAction(event -> getSaveBtnEvent());
        cancelBtn.setOnAction(event -> getCancelBtnEvent());

        exportCharts.selectedProperty().addListener((ov, v1, v2) -> {
            this.setExportChartsDisable(!v2);
        });
        statistics.selectedProperty().addListener((ov, v1, v2) -> {
            this.setStatisticsDisable(!v2);
        });
        processCapability.selectedProperty().addListener((ov, v1, v2) -> {
            this.setProcessCapabilityDisable(!v2);
        });
        processPerformance.selectedProperty().addListener((ov, v1, v2) -> {
            this.setProcessPerformanceDisable(!v2);
        });
    }

    private void getSaveBtnEvent() {
        if (checkBoxMap == null) {
            return;
        }
        Map<String, Boolean> exportSetting = Maps.newHashMap();
        for (Map.Entry<String, CheckBox> entry : checkBoxMap.entrySet()) {
            Boolean isSelect = entry.getValue().selectedProperty().getValue();
            exportSetting.put(entry.getKey(), isSelect);
        }
        spcSettingService.saveSpcExportTemplateSetting(exportSetting);
        StageMap.closeStage(StateKey.SPC_EXPORT_TEMPLATE_SETTING);
    }

    private void getCancelBtnEvent() {
        StageMap.closeStage(StateKey.SPC_EXPORT_TEMPLATE_SETTING);
    }

    private void setExportChartsDisable(boolean isDisable) {
        ndChart.setDisable(isDisable);
        runChart.setDisable(isDisable);
        xBarChart.setDisable(isDisable);
        rangeChart.setDisable(isDisable);
        sdChart.setDisable(isDisable);
        medianChart.setDisable(isDisable);
        boxChart.setDisable(isDisable);
        mrChart.setDisable(isDisable);
    }

    private void setStatisticsDisable(boolean isDisable) {
        samples.setDisable(isDisable);
        mean.setDisable(isDisable);
        sd.setDisable(isDisable);
        range.setDisable(isDisable);
        max.setDisable(isDisable);
        min.setDisable(isDisable);
        lcl.setDisable(isDisable);
        ucl.setDisable(isDisable);
        kurtosis.setDisable(isDisable);
        skewness.setDisable(isDisable);
    }

    private void setProcessCapabilityDisable(boolean isDisable) {
        ca.setDisable(isDisable);
        cp.setDisable(isDisable);
        cpu.setDisable(isDisable);
        cpl.setDisable(isDisable);
        cpk.setDisable(isDisable);
        withinPPM.setDisable(isDisable);
    }

    private void setProcessPerformanceDisable(boolean isDisable) {
        pp.setDisable(isDisable);
        ppu.setDisable(isDisable);
        ppl.setDisable(isDisable);
        ppk.setDisable(isDisable);
        overallPPM.setDisable(isDisable);
    }

    @FXML
    private CheckBox exportSummary;
    @FXML
    private CheckBox exportDetailSheet;

    @FXML
    private CheckBox exportCharts;
    @FXML
    private CheckBox ndChart;
    @FXML
    private CheckBox runChart;
    @FXML
    private CheckBox xBarChart;
    @FXML
    private CheckBox rangeChart;
    @FXML
    private CheckBox sdChart;
    @FXML
    private CheckBox medianChart;
    @FXML
    private CheckBox boxChart;
    @FXML
    private CheckBox mrChart;


    @FXML
    private CheckBox statistics;
    @FXML
    private CheckBox samples;
    @FXML
    private CheckBox mean;
    @FXML
    private CheckBox sd;
    @FXML
    private CheckBox range;
    @FXML
    private CheckBox max;
    @FXML
    private CheckBox min;
    @FXML
    private CheckBox lcl;
    @FXML
    private CheckBox ucl;
    @FXML
    private CheckBox kurtosis;
    @FXML
    private CheckBox skewness;

    @FXML
    private CheckBox processCapability;
    @FXML
    private CheckBox ca;
    @FXML
    private CheckBox cp;
    @FXML
    private CheckBox cpu;
    @FXML
    private CheckBox cpl;
    @FXML
    private CheckBox cpk;
    @FXML
    private CheckBox withinPPM;

    @FXML
    private CheckBox processPerformance;
    @FXML
    private CheckBox pp;
    @FXML
    private CheckBox ppu;
    @FXML
    private CheckBox ppl;
    @FXML
    private CheckBox ppk;
    @FXML
    private CheckBox overallPPM;


}
