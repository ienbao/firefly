/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.utils;

/**
 * Created by Ethan.Yang on 2018/2/6.
 */
public class UIConstant {

    public static final String[] SPC_SR_ALL = new String[]{
            "TestItem", "Condition", "Samples", "AVG", "Max", "Min", "StDev", "LSL", "USL", "Center", "CPK", "Range", "μ-3σ", "μ+3σ", "Kurtosis", "Skewness",
            "CA(%)", "CP", "CPL", "CPU", "Within PPM", "PP", "PPK", "PPL", "PPU", "OverAll PPM"};

    public static final String[] SPC_CHOOSE_RESULT = new String[]{
            "Samples", "AVG", "Max", "Min", "StDev", "LSL", "USL", "Center", "CPK", "Range", "μ-3σ", "μ+3σ", "Kurtosis", "Skewness",
            "CA(%)", "CP", "CPL", "CPU", "Within PPM", "PP", "PPK", "PPL", "PPU", "OverAll PPM"
    };

    public static final String TEST_ITEM = "TestItem";

    public static final String SPC_CHART_NDC = "NdChart";
    public static final String SPC_CHART_RUN = "RunChart";
    public static final String SPC_CHART_XBAR = "XBarChart";

    public static final String[] SPC_CHART_NAME = new String[]{"ND Chart", "Run Chart", "X-bar Chart", "Range Chart", "SD Chart", "Median Chart", "Box Chart", "MR Chart"};

    //    "LCL", "-2σ", "-σ", "μ", "σ", "2σ", "UCL"
    public static final String[] SPC_CHART_XBAR_EXTERN_MENU = new String[]{"LCL", "μ", "UCL", "Point", "Connect Line"};
    public static final String[] SPC_CHART_NDC_EXTERN_MENU = new String[]{"UCL", "USL", "2σ", "σ", "μ", "-σ", "-2σ", "LSL", "LCL", "Histogram", "Normal Curve"};
    public static final String[] SPC_CHART_RUN_EXTERN_MENU = new String[]{"LCL", "-2σ", "-σ", "μ", "σ", "2σ", "UCL", "Point", "Connect Line"};
    public static final String[] SPC_CHART_BOX_EXTERN_MENU = new String[]{"CL", "Grid Line"};

    public static final String[] SPC_CHART_LINE_NAME = new String[]{"LCL", "-2σ", "-σ", "μ", "σ", "2σ", "UCL"};
    public static final String[] SPC_XBARCHART_LINE_NAME = new String[]{"LCL", "μ", "UCL"};
    public static final String[] SPC_NDCCHART_LINE_NAME = new String[]{"UCL", "USL", "2σ", "σ", "μ", "-σ", "-2σ", "LSL", "LCL"};

    public static final String[] SPC_RULE_R = new String[]{"R1", "R2", "R3", "R4", "R5", "R6", "R7", "R8", "R9"};

    public static final String[] SPC_USL_LSL = new String[]{"USL", "LSL"};
    public static final String[] SPC_UCL_LCL = new String[]{"UCL", "LCL"};
    public static final String SPC_CHART_CL = "cl";

}
