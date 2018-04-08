/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.utils.enums;

/**
 * Created by Ethan.Yang on 2018/3/15.
 */
public enum SpcExportItemKey {
    EXPORT_SUB_SUMMARY("SubSummary"),
    EXPORT_DETAIL_SHEET("DetailSheet"),

    EXPORT_CHARTS("ExportCharts"),
    ND_CHART("ND Chart"),
    RUN_CHART("Run Chart"),
    X_BAR_CHART("X-bar Chart"),
    RANGE_CHART("Range Chart"),
    SD_CHART("SD Chart"),
    MEDIAN_CHART("Median Chart"),
    BOX_CHART("Box Chart"),
    MR_CHART("MR Chart"),

    DESCRIPTIVE_STATISTICS("statistics"),
    SAMPLES("Samples"),
    MEAN("AVG"),
    SD("StDev"),
    RANGE("Range"),
    Max("Max"),
    Min("Min"),
    LCL("μ-3σ"),
    UCL("μ+3σ"),
    KURTOSIS("Kurtosis"),
    SKEWNESS("Skewness"),

    PROCESS_CAPABILITY_INDEX("processCapability"),
    CA("CA"),
    CP("CPK"),
    CPU("CP"),
    CPL("CPL"),
    CPK("CPU"),
    WITHIN_PPM("Within PPM"),

    PROCESS_PERFORMANCE_INDEX("processPerformance"),
    PP("PP"),
    PPU("PPU"),
    PPL("PPL"),
    PPK("PPK"),
    OVERALL_PPM("OverAll PPM");

    private String code;

    SpcExportItemKey(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
