package com.dmsoft.firefly.plugin.grr.utils;

import javafx.scene.paint.Color;

/**
 * Created by cherry on 2018/3/12.
 */
public class UIConstant {

    public static final String UI_MESSAGE_TIP_SUCCESS_TITLE = "UI_MESSAGE_TIP_SUCCESS_TITLE";
    public static final String UI_MESSAGE_TIP_WARNING_TITLE = "UI_MESSAGE_TIP_WARNING_TITLE";
    public static final String UI_MESSAGE_TIP_INFO_TITLE = "UI_MESSAGE_TIP_INFO_TITLE";
    public static final String UI_MESSAGE_TIP_LOCATION = "UI_MESSAGE_TIP_LOCATION";
    public static final String UI_MESSAGE_TIP_FILE_NOT_EXIST = "UI_MESSAGE_TIP_FILE_NOT_EXIST";

    public static final String EXCEPTION_GRR_MODEL = "EXCEPTION_GRR_MODEL";
    public static final String SPLIT_FLAG = "!@#";

    public static final String GRR_EXPORT_STAGE = "GRR_EXPORT_STAGE";

    public static final String GRR_EXPORT_DIALOG_TITLE = "GRR_EXPORT_DIALOG_TITLE";

    public static final Color COLOR_EXCELLENT = Color.rgb(125, 174, 20);
    public static final Color COLOR_GOOD = Color.rgb(72, 124, 244);
    public static final Color COLOR_ACCEPTABLE = Color.rgb(243, 132, 0);
    public static final Color COLOR_RECTIFICATION = Color.rgb(233, 68, 41);
    public static final Color COLOR_EDIT_CHANGE = Color.rgb(243, 132, 0);
    public static final Color COLOR_EDIT_ERROR = Color.rgb(234, 32, 40);
    public static final Color COLOR_MEAN_RANGE = Color.grayRgb(242);
    public static final Color COLOR_TOTAL_MEAN_RANGE = Color.grayRgb(229);

    public static final Double X_FACTOR = 0.5;

    public static final String AXIS_LBL_PREFIX_APPRAISER = "AXIS_LBL_PREFIX_APPRAISER";

    public static final String MEAN = "Mean";
    public static final String RANGE = "Range";
    public static final String TOTAL_MEAN = "Total Mean";
    public static final String TOTAL_RANGE = "Total Range";

    public static final String CHART_PERFORMANCE_CODE = "GRR_CHARTS";
    public static final String CHART_PERFORMANCE_KEY_OPERATE = "GRR_CHART_OPERATE";

    public static final String GRR_CHART_XBAR_APPRAISER = "GRR_CHART_XBAR_APPRAISER";
    public static final String GRR_CHART_RANGE_APPRAISER = "GRR_CHART_RANGE_APPRAISER";
    public static final String BTN_CHART_CHOOSE_LINES = "BTN_CHART_CHOOSE_LINES";
    public static final String BTN_CHART_ZOOM_IN = "BTN_CHART_ZOOM_IN";
    public static final String BTN_CHART_ZOOM_OUT = "BTN_CHART_ZOOM_OUT";
    public static final String BTN_CHART_EXTENSION_MENU = "BTN_CHART_EXTENSION_MENU";
    public static final String CHART_EXTENSION_MENU_SAVE = "CHART_SAVE_AS";

    //    grr setting
    public static final String GRR_SETTING_SORT_DATA_BY_APPRAISERS = "GRR_SETTING_SORT_DATA_BY_APPRAISERS";
    public static final String GRR_SETTING_SORT_DATA_BY_DEFAULT = "GRR_SETTING_SORT_DATA_BY_DEFAULT";
    public static final String GRR_SETTING_RULE_NO_EMPTY = "GRR_SETTING_RULE_NO_EMPTY";
    public static final String GRR_SETTING_RULE_INVALID_RANGE = "GRR_SETTING_RULE_INVALID_RANGE";
    public static final String GRR_SETTING_RULE_MUST_NUMBER = "GRR_SETTING_RULE_MUST_NUMBER";
    public static final String GRR_SETTING_LEVEL_NO_EMPTY = "GRR_SETTING_LEVEL_NO_EMPTY";
    public static final String GRR_SETTING_LEVEL_LEVEL_MUST_BIGGER = "GRR_SETTING_LEVEL_LEVEL_MUST_BIGGER";
    public static final String GRR_SETTING_LEVEL_INPUT_ERROR = "GRR_SETTING_LEVEL_INPUT_ERROR";

    public static final String GRR_SETTING_SORT_BY_APPRAISERS = "Appraisers";
    public static final String GRR_SETTING_SORT_BY_DEFAULT = "Default";

    public static final String COMPONENTS_GAGE_R = "COMPONENTS_GAGE_R";
    public static final String COMPONENTS_REPEATABILITY = "COMPONENTS_REPEATABILITY";
    public static final String COMPONENTS_REPRODUCIBILITY = "COMPONENTS_REPRODUCIBILITY";
    public static final String COMPONENTS_PART = "COMPONENTS_PART";
    public static final String COMPONENTS_CONTRIBUTION = "COMPONENTS_CONTRIBUTION";
    public static final String COMPONENTS_VARIATION = "COMPONENTS_VARIATION";
    public static final String COMPONENTS_TOLERANCE = "COMPONENTS_TOLERANCE";

    public static final String CHART_LINE_NAME_UCL = "CHART_LINE_NAME_UCL";
    public static final String CHART_LINE_NAME_AVG = "CHART_LINE_NAME_AVG";
    public static final String CHART_LINE_NAME_LCL = "CHART_LINE_NAME_LCL";

    public static final String GRR_SUMMARY_TYPE_TOLERANCE = "GRR_SUMMARY_TYPE_TOLERANCE";
    public static final String GRR_SUMMARY_TYPE_CONTRIBUTION = "GRR_SUMMARY_TYPE_CONTRIBUTION";

    public static final String GRR_SUMMARY_TITLE_TESTITEM = "GRR_SUMMARY_TITLE_TESTITEM";
    public static final String GRR_SUMMARY_TITLE_LSL = "GRR_SUMMARY_TITLE_LSL";
    public static final String GRR_SUMMARY_TITLE_USL = "GRR_SUMMARY_TITLE_USL";
    public static final String GRR_SUMMARY_TITLE_TOLERANCE = "GRR_SUMMARY_TITLE_TOLERANCE";
    public static final String GRR_SUMMARY_TITLE_REPEATABILITY = "GRR_SUMMARY_TITLE_REPEATABILITY";
    public static final String GRR_SUMMARY_TITLE_REPRODUCIBILITY = "GRR_SUMMARY_TITLE_REPRODUCIBILITY";
    public static final String GRR_SUMMARY_TITLE_GAUGE = "GRR_SUMMARY_TITLE_GAUGE";
    public static final String GRR_ANOVA_TITLE_SOURCE = "GRR_ANOVA_TITLE_SOURCE";
    public static final String GRR_ANOVA_TITLE_DF = "GRR_ANOVA_TITLE_DF";
    public static final String GRR_ANOVA_TITLE_SS = "GRR_ANOVA_TITLE_SS";
    public static final String GRR_ANOVA_TITLE_MS = "GRR_ANOVA_TITLE_MS";
    public static final String GRR_ANOVA_TITLE_F = "GRR_ANOVA_TITLE_F";
    public static final String GRR_ANOVA_TITLE_PROB = "GRR_ANOVA_TITLE_PROB";
    public static final String GRR_SOURCE_TITLE_SOURCE_VARIATION = "GRR_SOURCE_TITLE_SOURCE_VARIATION";
    public static final String GRR_SOURCE_TITLE_SIGMA = "GRR_SOURCE_TITLE_SIGMA";
    public static final String GRR_SOURCE_TITLE_STUDY_VAR = "GRR_SOURCE_TITLE_STUDY_VAR";
    public static final String GRR_SOURCE_TITLE_VARIATION = "GRR_SOURCE_TITLE_VARIATION";
    public static final String GRR_SOURCE_TITLE_TOTAL_SIGMA = "GRR_SOURCE_TITLE_TOTAL_SIGMA";
    public static final String GRR_SOURCE_TITLE_TOTAL_VARIATION = "GRR_SOURCE_TITLE_TOTAL_VARIATION";
    public static final String GRR_SOURCE_TITLE_TOTAL_TOLERANCE = "GRR_SOURCE_TITLE_TOTAL_TOLERANCE";

    public static final String EXPORT_DETIAL = "EXPORT_DETIAL";
    public static final String EXPORT_SOURCE = "EXPORT_SOURCE";
    public static final String EXPORT_CHARTS = "EXPORT_CHARTS";
    public static final String CHART_1 = "CHART_1";
    public static final String CHART_2 = "CHART_2";
    public static final String CHART_3 = "CHART_3";
    public static final String CHART_4 = "CHART_4";
    public static final String CHART_5 = "CHART_5";
    public static final String CHART_6 = "CHART_6";
    public static final String GRR_EXPORT_BTN_OK = "GRR_EXPORT_BTN_OK";

    public static final String CHART_SAVE_AS_TITLE = "CHART_SAVE_AS_TITLE";
    public static final String CHART_SAVE_AS_PNG_EXTENSION = "CHART_SAVE_AS_PNG_EXTENSION";
}
