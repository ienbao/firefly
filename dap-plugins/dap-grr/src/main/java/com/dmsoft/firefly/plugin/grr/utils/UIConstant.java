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

    public static final String EXCEPTION_GRR_MODEL = "EXCEPTION_GRR_MODEL";
//    public static final String[] GRR_RESULT_TYPE = new String[] {"process tolerance", "system contribution"};
//    public static final String[] GRR_SUMMARY_TITLE = new String[] {"TestItem", "LSL", "USL", "Tolerance", "%Repeatability", "%Reproducibility", "%Gauge R & R"};
//    public static final String[] GRR_ANOVA_TITLE = new String[] {"Source", "DF", "SS", "MS", "F", "Prob > F"};
//    public static final String[] GRR_SOURCE_TITLE = new String[] {"Source of Variation", "SIGMA", "Study Var 6", "Variation", "%Total Sigma", "%Total Variation", "%Total Tolerance"};
//    public static final String[] CHART_COMPONENT_LABEL = new String[]{ "Gage R&R","Repeatability", "Reproducibility", "Part to Part"};
//    public static final String[] CHART_COMPONENT_CATEGORY = new String[]{"Contribution", "Variation", "Tolerance"};
//    public static final String[] CHART_OPERATE_NAME = new String[]{"UCL", "μ", "LCL"};
    public static final String SPLIT_FLAG = "!@#";

    public static final Color COLOR_EXCELLENT = Color.rgb(125, 174, 20);
    public static final Color COLOR_GOOD = Color.rgb(72, 124, 244);
    public static final Color COLOR_ACCEPTABLE = Color.rgb(243, 132, 0);
    public static final Color COLOR_RECTIFICATION = Color.rgb(233, 68, 41);
    public static final Color COLOR_EDIT_CHANGE = Color.rgb(243, 132, 0);
    public static final Color COLOR_EDIT_ERROR = Color.rgb(234, 32, 40);
    public static final Color COLOR_MEAN_RANGE = Color.grayRgb(242);
    public static final Color COLOR_TOTAL_MEAN_RANGE = Color.grayRgb(229);

    public static final String MEAN = "Mean";
    public static final String RANGE = "Range";
    public static final String TOTAL_MEAN = "Total Mean";
    public static final String TOTAL_RANGE = "Total Range";

    public static final String ANALYSIS_RESULT_SUMMARY = "SummaryResult";
    public static final String ANALYSIS_RESULT_DETAIL = "DetailResult";

    public static final String CHART_PERFORMANCE_CODE="GRR_CHARTS";
    public static final String CHART_PERFORMANCE_KEY_OPERATE="GRR_CHART_OPERATE";

    public static final String GRR_CHART_XBAR_APPRAISER = "X-bar by Appraiser";
    public static final String GRR_CHART_RANGE_APPRAISER = "Range by Appraiser";

    public static final String BTN_CHART_CHOOSE_LINES = GrrFxmlAndLanguageUtils.getString("BTN_CHART_CHOOSE_LINES");
    public static final String BTN_CHART_ZOOM_IN = GrrFxmlAndLanguageUtils.getString("BTN_CHART_ZOOM_IN");
    public static final String BTN_CHART_ZOOM_OUT = GrrFxmlAndLanguageUtils.getString("BTN_CHART_ZOOM_OUT");
    public static final String BTN_CHART_EXTENSION_MENU = GrrFxmlAndLanguageUtils.getString("BTN_CHART_EXTENSION_MENU");

    public static final String[] GRR_RESULT_TYPE = new String[]{
            GrrFxmlAndLanguageUtils.getString("GRR_SUMMARY_TYPE_TOLERANCE"),
            GrrFxmlAndLanguageUtils.getString("GRR_SUMMARY_TYPE_CONTRIBUTION")};
    public static final String[] CHART_COMPONENT_LABEL = new String[]{
            GrrFxmlAndLanguageUtils.getString("COMPONENTS_GAGE_R"),
            GrrFxmlAndLanguageUtils.getString("COMPONENTS_REPEATABILITY"),
            GrrFxmlAndLanguageUtils.getString("COMPONENTS_REPRODUCIBILITY"),
            GrrFxmlAndLanguageUtils.getString("COMPONENTS_PART")};
    public static final String[] CHART_COMPONENT_CATEGORY = new String[]{
            GrrFxmlAndLanguageUtils.getString("COMPONENTS_CONTRIBUTION"),
            GrrFxmlAndLanguageUtils.getString("COMPONENTS_VARIATION"),
            GrrFxmlAndLanguageUtils.getString("COMPONENTS_TOLERANCE")};
    public static final String[] CHART_OPERATE_NAME = new String[]{
            GrrFxmlAndLanguageUtils.getString("COMPONENTS_CONTRIBUTION"),
            GrrFxmlAndLanguageUtils.getString("COMPONENTS_CONTRIBUTION"),
            GrrFxmlAndLanguageUtils.getString("COMPONENTS_CONTRIBUTION")};
    public static final String[] GRR_SUMMARY_TITLE = new String[]{
            GrrFxmlAndLanguageUtils.getString("GRR_SUMMARY_TITLE_TESTITEM"),
            GrrFxmlAndLanguageUtils.getString("GRR_SUMMARY_TITLE_LSL"),
            GrrFxmlAndLanguageUtils.getString("GRR_SUMMARY_TITLE_USL"),
            GrrFxmlAndLanguageUtils.getString("GRR_SUMMARY_TITLE_TOLERANCE"),
            GrrFxmlAndLanguageUtils.getString("GRR_SUMMARY_TITLE_REPEATABILITY"),
            GrrFxmlAndLanguageUtils.getString("GRR_SUMMARY_TITLE_REPRODUCIBILITY"),
            GrrFxmlAndLanguageUtils.getString("GRR_SUMMARY_TITLE_GAUGE")};
    public static final String[] GRR_ANOVA_TITLE = new String[]{
            GrrFxmlAndLanguageUtils.getString("GRR_ANOVA_TITLE_SOURCE"),
            GrrFxmlAndLanguageUtils.getString("GRR_ANOVA_TITLE_DF"),
            GrrFxmlAndLanguageUtils.getString("GRR_ANOVA_TITLE_SS"),
            GrrFxmlAndLanguageUtils.getString("GRR_ANOVA_TITLE_MS"),
            GrrFxmlAndLanguageUtils.getString("GRR_ANOVA_TITLE_F"),
            GrrFxmlAndLanguageUtils.getString("GRR_ANOVA_TITLE_PROB")};
    public static final String[] GRR_SOURCE_TITLE = new String[]{
            GrrFxmlAndLanguageUtils.getString("GRR_SOURCE_TITLE_SOURCE_VARIATION"),
            GrrFxmlAndLanguageUtils.getString("GRR_SOURCE_TITLE_SIGMA"),
            GrrFxmlAndLanguageUtils.getString("GRR_SOURCE_TITLE_STUDY_VAR"),
            GrrFxmlAndLanguageUtils.getString("GRR_SOURCE_TITLE_VARIATION"),
            GrrFxmlAndLanguageUtils.getString("GRR_SOURCE_TITLE_TOTAL_SIGMA"),
            GrrFxmlAndLanguageUtils.getString("GRR_SOURCE_TITLE_TOTAL_VARIATION"),
            GrrFxmlAndLanguageUtils.getString("GRR_SOURCE_TITLE_TOTAL_TOLERANCE")};
}
