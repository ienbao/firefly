/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.utils.enums;

/**
 * Created by Ethan.Yang on 2018/3/15.
 */
public enum SpcExportItemKey {
    EXPORT_SUB_SUMMARY("exportSummary"),
    EXPORT_DETAIL_SHEET("exportDetailSheet"),

    EXPORT_CHARTS("exportCharts"),
    ND_CHART("ndChart"),
    RUN_CHART("runChart"),
    X_BAR_CHART("xBarChart"),
    RANGE_CHART("rangeChart"),
    SD_CHART("sdChart"),
    MEDIAN_CHART("medianChart"),
    BOX_CHART("boxChart"),
    MR_CHART("mrChart"),

    DESCRIPTIVE_STATISTICS("statistics"),
    SAMPLES("samples"),
    MEAN("mean"),
    SD("sd"),
    RANGE("range"),
    Max("max"),
    Min("min"),
    LCL("lcl"),
    UCL("ucl"),
    KURTOSIS("kurtosis"),
    SKEWNESS("skewness"),

    PROCESS_CAPABILITY_INDEX("processCapability"),
    CA("ca"),
    CP("cp"),
    CPU("cpu"),
    CPL("cpl"),
    CPK("cpk"),
    WITHIN_PPM("withinPPM"),

    PROCESS_PERFORMANCE_INDEX("processPerformance"),
    PP("pp"),
    PPU("ppu"),
    PPL("ppl"),
    PPK("ppk"),
    OVERALL_PPM("overallPPM");

    private String code;

    SpcExportItemKey(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
