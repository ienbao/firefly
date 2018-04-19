/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.utils;

/**
 * Created by Ethan.Yang on 2018/2/6.
 */
public class UIConstant {
    public static final String UI_MESSAGE_TIP_SUCCESS_TITLE = "UI_MESSAGE_TIP_SUCCESS_TITLE";
    public static final String UI_MESSAGE_TIP_WARNING_TITLE = "UI_MESSAGE_TIP_WARNING_TITLE";
    public static final String UI_MESSAGE_TIP_INFO_TITLE = "UI_MESSAGE_TIP_INFO_TITLE";
    public static final String UI_MESSAGE_TIP_LOCATION = "UI_MESSAGE_TIP_LOCATION";

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
    public static final String[] EXPORT_NDC_SECOND_LABELS = {"Item Name", "Search Condition", "Samples", "AVG", "Max", "Min", "USL", "LSL", "Center", "Range", "StDev", "AVG-3SD", "AVG+3SD", "Kurtosis", "Skewness",
            "CA", "CPK", "CP", "CPL", "CPU", "Within PPM", "PPK", "PP", "PPL", "PPU", "Overall PPM"};

    //    "LCL", "-2σ", "-σ", "μ", "σ", "2σ", "UCL"
//    public static final String[] SPC_CHART_XBAR_EXTERN_MENU = new String[]{"LCL", "μ", "UCL", "Point", "Connect Line"};
//    public static final String[] SPC_CHART_NDC_EXTERN_MENU = new String[]{"USL", "LSL", "UCL", "2σ", "σ", "μ", "-σ", "-2σ", "LCL", "Histogram", "Normal Curve"};
//    public static final String[] SPC_CHART_RUN_EXTERN_MENU = new String[]{"USL", "LSL", "LCL", "-2σ", "-σ", "μ", "σ", "2σ", "UCL", "Point", "Connect Line"};
//    public static final String[] SPC_CHART_BOX_EXTERN_MENU = new String[]{"CL", "Grid Line"};

    //    public static final String[] SPC_CHART_LINE_NAME = new String[]{"USL", "LSL", "LCL", "-2σ", "-σ", "μ", "σ", "2σ", "UCL"};
    public static final String[] SPC_XBARCHART_LINE_NAME = new String[]{"LCL", "μ", "UCL"};
    public static final String[] SPC_NDCCHART_LINE_NAME = new String[]{"UCL", "USL", "2σ", "σ", "μ", "-σ", "-2σ", "LSL", "LCL"};

//    public static final String[] SPC_RULE_R = new String[]{"R1", "R2", "R3", "R4", "R5", "R6", "R7", "R8", "R9"};

    public static final String[] EXPORT_SPC_NDC_LABELS = {"CA", "CPK", "CP", "CPL", "CPU", "Within PPM"};
    public static final String[] EXPORT_SPC_DESCRIPTIVE = {"Descriptive Statistics", "Samples", "AVG", "Max", "Min", "Center", "Range", "StDev", "AVG-3SD", "AVG+3SD", "Kurtosis", "Skewness"};
    public static final String[] EXPORT_SPC_PERFORMANCE = {"Performace Cability Index", "PPK", "PP", "PPL", "PPU", "OverAll PPM"};
    public static final String[] SPC_USL_LSL = new String[]{"USL", "LSL"};
    public static final String[] SPC_UCL_LCL = new String[]{"UCL", "LCL"};
    public static final String SPC_CHART_CL = "μ";

    public static final String EXPORT_SUMMARY_EXCELLENT = "EXCELLENT";
    public static final String EXPORT_SUMMARY_ADEQUTE = "GOOD";
    public static final String EXPORT_SUMMARY_MARGIMAL = "ACCEPTABLE";
    public static final String EXPORT_SUMMARY_BAD = "RECTIFICATION";
    public static final String EXPORT_SUMMARY_NULL = "-";

    public static final String[] STATISTICAL_RESULT_RULE_HEADER = new String[]{SpcFxmlAndLanguageUtils.getString("STATISTICS"), SpcFxmlAndLanguageUtils.getString("LOWER_LIMIT"), SpcFxmlAndLanguageUtils.getString("UPPER_LIMIT")};
    public static final String[] CONTROL_ALARM_RULE_HEADER = new String[]{SpcFxmlAndLanguageUtils.getString("IS_USE"), SpcFxmlAndLanguageUtils.getString("RULE_NAME"), "n", "m", "s"};

    public static final double X_FACTOR = 1.0;
    public static final double Y_FACTOR = 0.15;

    public static final int COR_NUMBER = 4;


    //chart user performance
    public static final String CHART_PERFORMANCE_CODE = "SPC_CHARTS";
    public static final String SPC_CHART_PERFORMANCE_KEY_OPERATE = "SPC_CHART_OPERATE";

    public static final String[] SPC_CHART_ND_EXTERN_MENU = new String[]{
            "SPC_CHART_ND_EXTERN_MENU_USL",
            "SPC_CHART_ND_EXTERN_MENU_LSL",
            "SPC_CHART_ND_EXTERN_MENU_UCL",
            "SPC_CHART_ND_EXTERN_MENU_2_SIGMA",
            "SPC_CHART_ND_EXTERN_MENU_SIGMA",
            "SPC_CHART_ND_EXTERN_MENU_AVERAGE",
            "SPC_CHART_ND_EXTERN_MENU_NEGATIVE_SIGMA",
            "SPC_CHART_ND_EXTERN_MENU_NEGATIVE_2_SIGMA",
            "SPC_CHART_ND_EXTERN_MENU_LCL",
            "SPC_CHART_ND_EXTERN_MENU_HISTOGRAM",
            "SPC_CHART_ND_EXTERN_MENU_CURVE"};

    public static final String[] SPC_CHART_RUN_EXTERN_MENU = new String[]{
            "SPC_CHART_RUN_EXTERN_MENU_USL",
            "SPC_CHART_RUN_EXTERN_MENU_LSL",
            "SPC_CHART_RUN_EXTERN_MENU_LCL",
            "SPC_CHART_RUN_EXTERN_MENU_NEGATIVE_2_SIGMA",
            "SPC_CHART_RUN_EXTERN_MENU_NEGATIVE_SIGMA",
            "SPC_CHART_RUN_EXTERN_MENU_AVERAGE",
            "SPC_CHART_RUN_EXTERN_MENU_SIGMA",
            "SPC_CHART_RUN_EXTERN_MENU_2_SIGMA",
            "SPC_CHART_RUN_EXTERN_MENU_UCL",
            "SPC_CHART_RUN_EXTERN_MENU_POINT",
            "SPC_CHART_RUN_EXTERN_MENU_LINE"};

    public static final String[] SPC_CHART_CONTROL_EXTERN_MENU = new String[]{
            "SPC_CHART_CONTROL_EXTERN_MENU_LCL",
            "SPC_CHART_CONTROL_EXTERN_MENU_AVERAGE",
            "SPC_CHART_CONTROL_EXTERN_MENU_UCL",
            "SPC_CHART_CONTROL_EXTERN_MENU_POINT",
            "SPC_CHART_CONTROL_EXTERN_MENU_CONNECT_LINE"};

    public static final String[] SPC_CHART_BOX_EXTERN_MENU = new String[]{
            "SPC_CHART_BOX_EXTERN_MENU_CL",
            "SPC_CHART_BOX_EXTERN_MENU_Line"};

    public static final String[] SPC_RULE_R_EXTERN_MENU = new String[]{
            "SPC_RULE_R_R1",
            "SPC_RULE_R_R2",
            "SPC_RULE_R_R3",
            "SPC_RULE_R_R4",
            "SPC_RULE_R_R5",
            "SPC_RULE_R_R6",
            "SPC_RULE_R_R7",
            "SPC_RULE_R_R8",
            "SPC_RULE_R_R9"};

    public static final String SPC_CHART_LINE_NAME_USL = "SPC_CHART_LINE_NAME_USL";
    public static final String SPC_CHART_LINE_NAME_LSL = "SPC_CHART_LINE_NAME_LSL";
    public static final String SPC_CHART_LINE_NAME_LCL = "SPC_CHART_LINE_NAME_LCL";
    public static final String SPC_CHART_LINE_NAME_NEGATIVE_2_SIGMA = "SPC_CHART_LINE_NAME_NEGATIVE_2_SIGMA";
    public static final String SPC_CHART_LINE_NAME_NEGATIVE_SIGMA = "SPC_CHART_LINE_NAME_NEGATIVE_SIGMA";
    public static final String SPC_CHART_LINE_NAME_AVERAGE = "SPC_CHART_LINE_NAME_AVERAGE";
    public static final String SPC_CHART_LINE_NAME_SIGMA = "SPC_CHART_LINE_NAME_SIGMA";
    public static final String SPC_CHART_LINE_NAME_2_SIGMA = "SPC_CHART_LINE_NAME_2_SIGMA";
    public static final String SPC_CHART_LINE_NAME_UCL = "SPC_CHART_LINE_NAME_UCL";

    public static final String CHART_SAVE_AS = "CHART_SAVE_AS";
    public static final String CHART_SAVE_AS_TITLE = "CHART_SAVE_AS_TITLE";
    public static final String CHART_SAVE_AS_PNG_EXTENSION = "CHART_SAVE_AS_PNG_EXTENSION";
    public static final String BTN_CHART_CHOOSE_LINES = "BTN_CHART_CHOOSE_LINES";
    public static final String BTN_RUN_CHART_CHOOSE_RULES = "BTN_RUN_CHART_CHOOSE_RULES";
    public static final String BTN_RUN_CHART_CHOOSE_ANNOTATION_ITEM = "BTN_RUN_CHART_CHOOSE_ANNOTATION_ITEM";
    public static final String BTN_CHART_ZOOM_IN = "BTN_CHART_ZOOM_IN";
    public static final String BTN_CHART_ZOOM_OUT = "BTN_CHART_ZOOM_OUT";
    public static final String BTN_CHART_EXTENSION_MENU = "BTN_CHART_EXTENSION_MENU";

    public static final String BTN_ANNOTATION_CLEAR = "BTN_ANNOTATION_CLEAR";
    public static final String BTN_ANNOTATION_OPEN = "BTN_ANNOTATION_OPEN";
    public static final String BTN_ANNOTATION_EXIT = "BTN_ANNOTATION_EXIT";
}
