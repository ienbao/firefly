/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.utils;

/**
 * Created by Ethan.Yang on 2018/2/6.
 */
public class UIConstant {
    public static final String GLOBAL_NAN = "NaN";
    public static final String GLOBAL_SPLIT = " : : ";
    public static final int EXPORT_IMG_WEIGHT = 600;
    public static final int EXPORT_IMG_HEIGHT = 220;

    public static final String CHART_TYPE_SPC_ND = "NDC";
    public static final String CHART_TYPE_SPC_RUN = "RunC";
    public static final String CHART_TYPE_SPC_XBAR = "XbarC";
    public static final String CHART_TYPE_SPC_SD = "SDC";
    public static final String CHART_TYPE_SPC_R = "RangeC";
    public static final String CHART_TYPE_SPC_MR = "MRC";
    public static final String CHART_TYPE_SPC_SCT = "MedianC";
    public static final String CHART_TYPE_SPC_SCT_BOX = "BoxC";

    public static final String[] SPC_SR_ALL = new String[]{
            "Test Item", "Condition", "Samples", "AVG", "Max", "Min", "StDev", "LSL", "USL", "Center", "CPK", "Range", "μ-3σ", "μ+3σ", "Kurtosis", "Skewness",
            "CA(%)", "CP", "CPL", "CPU", "Within PPM", "PP", "PPK", "PPL", "PPU", "OverAll PPM"};

    public static final String[] SPC_STATISTICAL_FIX_COLUMN = new String[]{"", "Test Item", "Condition"};

    public static final String[] SPC_CHOOSE_RESULT = new String[]{
            "Samples", "AVG", "Max", "Min", "StDev", "LSL", "USL", "Center", "CPK", "Range", "μ-3σ", "μ+3σ", "Kurtosis", "Skewness",
            "CA(%)", "CP", "CPL", "CPU", "Within PPM", "PP", "PPK", "PPL", "PPU", "OverAll PPM"
    };

    public static final String[] SPC_STATISTICAL = new String[]{
            "Samples", "AVG", "Max", "Min", "StDev", "Range", "μ-3σ", "μ+3σ", "Kurtosis", "Skewness"
    };
    public static final String[] SPC_CAPABILITY = new String[]{
            "CPK", "CA", "CP", "CPL", "CPU", "Within PPM"
    };
    public static final String[] SPC_PERFORMANCE = new String[]{
            "PP", "PPK", "PPL", "PPU", "OverAll PPM"
    };
    public static final String[] SPC_EXPORT_A = new String[]{
            "CA", "CPK", "CP", "CPL", "CPU", "Within PPM", "PPK", "PP", "PPL", "PPU", "OverAll PPM"
    };
    public static final String[] SPC_EXPORT_B = new String[]{
            "Samples", "AVG", "Max", "Min", "Center", "Range", "StDev", "μ-3σ", "μ+3σ", "Kurtosis", "Skewness"
    };

    public static final String[] SPC_LEVEL_RESULT = new String[]{
            "AVG", "Max", "Min", "StDev", "LSL", "USL", "Center", "CPK", "Range", "LCL", "UCL", "Kurtosis", "Skewness",
            "CA", "CP", "CPL", "CPU", "PP", "PPK", "PPL", "PPU"
    };
    public static final String TEST_ITEM = "TestItem";

    public static final String SPC_CHART_NDC = "ND Chart";
    public static final String SPC_CHART_RUN = "Run Chart";
    public static final String SPC_CHART_XBAR = "X-bar Chart";
    public static final String SPC_CHART_RANGE = "Range Chart";
    public static final String SPC_CHART_SD = "SD Chart";
    public static final String SPC_CHART_MED = "Median Chart";
    public static final String SPC_CHART_BOX = "Box Chart";
    public static final String SPC_CHART_MR = "MR Chart";

    public static final String[] SPC_CHART_NAME = new String[]{"ND Chart", "Run Chart", "X-bar Chart", "Range Chart", "SD Chart", "Median Chart", "Box Chart", "MR Chart"};
    public static final String[] EXPORT_NDC_SECOND_LABELS = {"Item Name", "Search Condition", "Samples", "AVG", "Max", "Min", "USL", "LSL", "Center", "Range", "StDev", "μ-3σ", "μ+3σ", "Kurtosis", "Skewness",
            "CA", "CPK", "CP", "CPL", "CPU", "Within PPM", "PPK", "PP", "PPL", "PPU", "Overall PPM"};

    //    "LCL", "-2σ", "-σ", "μ", "σ", "2σ", "UCL"
    public static final String[] SPC_CHART_XBAR_EXTERN_MENU = new String[]{"LCL", "μ", "UCL", "Point", "Connect Line"};
    public static final String[] SPC_CHART_NDC_EXTERN_MENU = new String[]{"USL", "LSL", "UCL", "2σ", "σ", "μ", "-σ", "-2σ", "LCL", "Histogram", "Normal Curve"};
    public static final String[] SPC_CHART_RUN_EXTERN_MENU = new String[]{"USL", "LSL", "LCL", "-2σ", "-σ", "μ", "σ", "2σ", "UCL", "Point", "Connect Line"};
    public static final String[] SPC_CHART_BOX_EXTERN_MENU = new String[]{"CL", "Grid Line"};

    public static final String[] SPC_CHART_LINE_NAME = new String[]{"USL", "LSL", "LCL", "-2σ", "-σ", "μ", "σ", "2σ", "UCL"};
    public static final String[] SPC_XBARCHART_LINE_NAME = new String[]{"LCL", "μ", "UCL"};
    public static final String[] SPC_NDCCHART_LINE_NAME = new String[]{"UCL", "USL", "2σ", "σ", "μ", "-σ", "-2σ", "LSL", "LCL"};

    public static final String[] SPC_RULE_R = new String[]{"R1", "R2", "R3", "R4", "R5", "R6", "R7", "R8", "R9"};

    public static final String[] EXPORT_SPC_NDC_LABELS = {"CA", "CPK", "CP", "CPL", "CPU", "Within PPM"};
    public static final String[] EXPORT_SPC_DESCRIPTIVE = {"Descriptive Statistics", "Samples", "AVG", "Max", "Min", "Center", "Range", "StDev", "μ-3σ", "μ+3σ", "Kurtosis", "Skewness"};
    public static final String[] EXPORT_SPC_PERFORMANCE = {"Performace Cability Index", "PPK", "PP", "PPL", "PPU", "OverAll PPM"};
    public static final String[] SPC_USL_LSL = new String[]{"USL", "LSL"};
    public static final String[] SPC_UCL_LCL = new String[]{"UCL", "LCL"};
    public static final String SPC_CHART_CL = "μ";

    public static final String EXPORT_SUMMARY_EXCELLENT = "EXCELLENT";
    public static final String EXPORT_SUMMARY_ADEQUTE = "GOOD";
    public static final String EXPORT_SUMMARY_MARGIMAL = "ACCEPTABLE";
    public static final String EXPORT_SUMMARY_BAD = "RECTIFICATION";
    public static final String EXPORT_SUMMARY_NULL = "-";

    public static final String[] CONTROL_ALARM_RULE_HEADER = new String[]{"Is Use", "Rule Name", "n", "m", "s"};

    public static final double FACTOR = 0.20;
}
