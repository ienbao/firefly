/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.grr.utils;

/**
 * Created by julia on 16/2/4.
 */
public final class AppConstant {
    private AppConstant() {
    }

    public static final String GLOBAL_SEPARATOR = " @#! ";
    public static final String GLOBAL_NAN = "NaN";
    public static final String GLOBAL_SPLIT = " : : ";
    public static final int EXPORT_IMG_WEIGHT = 600;
    public static final int EXPORT_IMG_HEIGHT = 220;


    public static final String GRR_MODEL_SLOT = "Slot";
    public static final String GRR_MODEL_NORMAL = "Normal";

    public static final String GRR_FIRST_CHANGE_OPERATOR = "Appraiser";
    public static final String GRR_FIRST_CHANGE_TRIAL = "Trial";

    public static final String[] GRR_SUMMARY_TB_TITLE = new String[]{"", "TestItem", "LSL", "USL", "Tolerance", "%Repeatability", "%Reproducibility", "%Gauge R & R"};
    public static final String[] GRR_ANOVA_TB_TITLE = new String[]{"Source", "DF", "SS", "MS", "F", "Prob>F"};
    public static final String[] GRR_SOURCE_TB_TITLE = new String[]{"Source of Variation", "SIGMA", "Study Var", "Variation", "%Total Sigma", "%Total Variation", "%Total Tolerance"};

    public static final String GRR_ANOVA_VALUE = "anovaValue";
    public static final String GRR_ANOVA_TITLE = "titleFlag";
    public static final String GRR_SOURCE_VALUE = "sourceValue";

    public static final String GRR_ANOVA_APPRAISERS = "Appraisers";
    public static final String GRR_ANOVA_PARTS = "Parts";
    public static final String GRR_ANOVA_APPRAISERS_AND_PARTS = "Appraisers*Parts";
    public static final String GRR_ANOVA_GAGE = "Gage";
    public static final String GRR_ANOVA_TOTAL = "Total";

    public static final String GRR_SOURCE_REPEATABILITY = "Repeatability";
    public static final String GRR_SOURCE_REPRODUCIBILITY = "Reproducibility";
    public static final String GRR_SOURCE_APPRAISERS = "Appraisers";
    public static final String GRR_SOURCE_APPRAISERS_AND_PARTS = "Appraisers*Parts";
    public static final String GRR_SOURCE_GAGE_RR = "Gage R&R";
    public static final String GRR_SOURCE_PART = "Part";
    public static final String GRR_SOURCE_TOTAL = "Total";
    public static final String GRR_EXPORT_SUMMARY = "Summary";
    public static final String GRR_EXPORT_DATA_SOURCE = "data";
    public static final String GRR_ANOVA = "anova";


}
