package com.dmsoft.firefly.plugin.grr.utils;

import javafx.scene.paint.Color;

/**
 * Created by cherry on 2018/3/12.
 */
public class UIConstant {

    public static final String UI_MESSAGE_TIP_SUCCESS_TITLE = "UI_MESSAGE_TIP_SUCCESS_TITLE";
    public static final String UI_MESSAGE_TIP_WARNING_TITLE = "UI_MESSAGE_TIP_WARNING_TITLE";
    public static final String UI_MESSAGE_TIP_INFO_TITLE = "UI_MESSAGE_TIP_INFO_TITLE";
    public static final String EXCEPTION_GRR_MODEL = "EXCEPTION_GRR_MODEL";
    public static final String[] GRR_RESULT_TYPE = new String[] {"process tolerance", "system contribution"};
    public static final String[] GRR_SUMMARY_TITLE = new String[] {"TestItem", "LSL", "USL", "Tolerance", "%Repeatability", "%Reproducibility", "%Gauge R & R"};
    public static final String[] GRR_ANOVA_TITLE = new String[] {"Source", "DF", "SS", "MS", "F", "Prob > F"};
    public static final String[] GRR_SOURCE_TITLE = new String[] {"Source of Variation", "SIGMA", "Study Var 6", "Variation", "%Total Sigma", "%Total Variation", "%Total Tolerance"};
    public static final String[] CHART_COMPONENT_LABEL = new String[]{ "Gage R&R","Repeatability", "Reproducibility", "Part to Part"};
    public static final String[] CHART_COMPONENT_CATEGORY = new String[]{"Contribution", "Variation", "Tolerance"};
    public static final String[] CHART_OPERATE_NAME = new String[]{"UCL", "μ", "LCL"};
    public static final String SPLIT_FLAG = "!@#";

    public static final Color COLOR_EXCELLENT = Color.rgb(125, 174, 20);
    public static final Color COLOR_GOOD = Color.rgb(72, 124, 244);
    public static final Color COLOR_ACCEPTABLE = Color.rgb(243, 132, 0);
    public static final Color COLOR_RECTIFICATION = Color.rgb(233, 68, 41);

    public static final String MEAN = "Mean";
    public static final String RANGE = "Range";
    public static final String TOTAL_MEAN = "Total Mean";
    public static final String TOTAL_RANGE = "Total Range";
}
