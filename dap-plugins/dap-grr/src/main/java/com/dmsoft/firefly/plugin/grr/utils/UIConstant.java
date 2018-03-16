package com.dmsoft.firefly.plugin.grr.utils;

/**
 * Created by cherry on 2018/3/12.
 */
public class UIConstant {

    public static final String UI_MESSAGE_TIP_SUCCESS_TITLE = "UI_MESSAGE_TIP_SUCCESS_TITLE";
    public static final String UI_MESSAGE_TIP_WARNING_TITLE = "UI_MESSAGE_TIP_WARNING_TITLE";
    public static final String UI_MESSAGE_TIP_INFO_TITLE = "UI_MESSAGE_TIP_INFO_TITLE";

    public static final String[] GRR_RESULT_TYPE = new String[] {"process tolerance", "system contribution"};

    public static final String[] GRR_SUMMARY_TITLE = new String[] {"TestItem", "LSL", "USL", "Tolerance", "%Repeatability", "%Reproducibility", "%Gauge R & R"};

    public static final String[] GRR_ANOVA_TITLE = new String[] {"Source", "DF", "SS", "MS", "F", "Prob > F"};

    public static final String[] GRR_ANOVA_SOURCE = new String[] {"Appraisers", "Parts", "Appraisers*Parts", "Repeatability", "Total"};

    public static final String[] GRR_SOURCE_TITLE = new String[] {"Source of Variation", "SIGMA", "Study Var 6", "Variation", "%Total Sigma", "%Total Variation", "%Total Tolerance"};

    public static final String[] CHART_COMPONENT_LABEL = new String[]{ "Gage R&R","Repeatability", "Reproducibility", "Part to Part"};

    public static final String[] CHART_COMPONENT_CATEGORY = new String[]{"Contribution", "Variation", "Tolerance"};

    public static final String[] CHART_OPERATE_NAME = new String[]{"UCL", "μ", "LCL"};
}
