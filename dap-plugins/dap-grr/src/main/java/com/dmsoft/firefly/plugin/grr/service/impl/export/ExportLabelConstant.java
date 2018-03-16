/*
 * Copyright (c) 2017. For Intelligent Group.
 */

//package com.intelligent.ispc.utils;
//
///**
// * Created by Alice on 2016/9/8.
// */
//public interface ExportLabelConstant {
//    String [] EXPORT_SUMMARY_LABELS_ONE = {"NO.", "TestItem", "Specs","", "", "R & R Result", "", "", "Result"};
//    String [] EXPORT_SUMMARY_LABELS_TWO = {"", "", "LSL", "USL", "Tolerance", "Part Variation", "Total Process Variation", "Gage R & R", ""};
//
//    String EXPORT_SUMMARY_EXCELLENT = "EXCELLENT";
//    String EXPORT_SUMMARY_ADEQUTE = "ADEQUATE";
//    String EXPORT_SUMMARY_MARGIMAL = "MARGINAL";
//    String EXPORT_SUMMARY_BAD = "BAD";
//
//    String [] EXPORT_SUMMARY_RULES = {"Excellent", "Adequate", "Marginal", "Bad"};
//
//    String [] EXPORT_SOURCE_LABELS = {"SOURCE", "VARIANCE", "SIGMA" ,"SIGMA", "CONTRIBUTION", "TOTAL VARIATION", "TOLERANCE"};
//
//    String EXPORT_GRR_ITEM_CATEGORIES = "Number of Distinct Categories";
//
//    String [] EXPORT_ANOVA_LABELS = {"ANOVA","DF","SS","MS","F","Prob>F"};
//
//    String [] EXPORT_YIELD_SUMMARY_YIELD_QUERY = {"", "", "","", "", "", "", "", "","Yield Query"};
//    String [] EXPORT_YIELD_SUMMARY_SEARCHTIME = {"", "", "", "", "", "", "", "", "SEARCHTIME"};
//    String [] EXPORT_YIELD_SUMMARY_LABELS_ONE = {"Project Name:", "", "Stop Time","", "", "", "", "", "Total:"};
//    String [] EXPORT_YIELD_SUMMARY_LABELS_TWO = {"Station Name", "First Inspect", "First Pass", "First Fail", "FPY", "ReTest Inspect", "ReTest Pass", "ReTest Fail Fail", "ReTest Yield","Total Yield"};
//
//    String[] EXPORT_YIELD_GROUPKEY_SERIALNUMBER = {"", "", "", "", "", "", "SerialNumber List"};
//    String[] EXPORT_YIELD_GROUPKEY_SEARCHTIME = {"", "", "", "", "", "SEARCHTIME"};
//  //  String[] EXPORT_YIELD_GROUPKEY_LABELS_ONE = {"Project Name:", "", "Start Time:", "", "", "Stop Time:", "", "", "Station Name:", "", "Type:", "", "Total:"};
//   // String[] EXPORT_YIELD_GROUPKEY_LABELS_TWO = {"Serial Number", "Project Name", "Station Name", "Slot", "Start Time", "Stop Time", "Is Pass"};
//
//}

package com.dmsoft.firefly.plugin.grr.service.impl.export;

/**
 * Created by Alice on 2016/9/8.
 */
public final class ExportLabelConstant {
    //public static final String[] EXPORT_ITEM_LABELS = {"Serial Number", "Upper Limit", "Lower Limit", "Unit Name", "Tested Value", "Is Pass", "X-Xbar", "MR"};
    public static final String[] EXPORT_ITEM_LABELS = {"Tested Value", "Is Pass", "X-AVG", "MR"};
    public static final String[] EXPORT_SEARCH_LABELS = {"File", "Search Condition"};
    public static final String[] EXPORT_SUMMARY_LABELS_ONE = {"NO.", "TestItem", "Specs", "", "", "R & R Result", "", "", "Result"};
    public static final String[] EXPORT_SUMMARY_LABELS_TWO = {"", "", "LSL", "USL", "Tolerance", "Part Variation", "Total Process Variation", "Gage R & R", ""};
    public static final String[] EXPORT_NDC_FIRST_LABELS = {"Statistical", "Constant", "Capability Index"};
    public static final String[] EXPORT_NDC_SECOND_LABELS = {"Item Name", "Search Condition", "Samples", "AVG", "Max", "Min",  "USL", "LSL", "Center", "Range", "SD", "AVG-3SD", "AVG+3SD", "Kurtosis", "Skewness",
            "CA", "CPK", "CP", "CPL", "CPU", "Within PPM", "PPK", "PP", "PPL", "PPU", "Overall PPM"};
    public static final String[] EXPORT_NDC_LABELS = {"Statistical", "Samples", "AVG", "Max", "Min", "Range", "StDev", "LCL", "UCL", "Kurtosis", "Skewness", "Constant", "Groups",
            "USL", "LSL", "Center", "Capability Index", "CA", "CPK", "CP", "CPL", "CPU"};
    public static final String[] EXPORT_SCT_LABELS = {"Statistical", "Samples", "AVG", "Max", "Min", "Range", "StDev", "LCL", "UCL", "Kurtosis", "Skewness", "Constant", "Groups",
            "USL", "LSL", "Center", "Capability Index", "CA", "CPK", "CP", "CPL", "CPU"};
    public static final String[] EXPORT_SPC_NDC_LABELS = {"CA", "CPK", "CP", "CPL", "CPU", "Within PPM"};
    public static final String[] EXPORT_SPC_DESCRIPTIVE = {"Descriptive Statistics", "Samples", "AVG", "Max", "Min", "Center", "Range", "SD", "AVG-3SD", "AVG+3SD", "Kurtosis", "Skewness"};
    public static final String[] EXPORT_SPC_PERFORMANCE = {"Performace Cability Index", "PPK", "PP", "PPL", "PPU", "Overall PPM"};

    public static final String EXPORT_SUMMARY_EXCELLENT = "EXCELLENT";
    public static final String EXPORT_SUMMARY_ADEQUTE = "GOOD";
    public static final String EXPORT_SUMMARY_MARGIMAL = "ACCEPTABLE";
    public static final String EXPORT_SUMMARY_BAD = "RECTIFICATION";
    public static final String EXPORT_SUMMARY_NULL = "-";

    public static final String[] EXPORT_SUMMARY_RULES = {"A", "B", "C", "D"};

    //    public static final String[] EXPORT_SOURCE_LABELS = {"SOURCE", "VARIANCE", "SIGMA", "SIGMA", "CONTRIBUTION", "TOTAL VARIATION", "TOLERANCE"};
//    public static final String[] EXPORT_SOURCE_LABELS = {"SOURCE", "SIGMA", "SV", "Var.", "%TOTAL VAR", "%CONT.", "%TOL."};
    public static final String[] EXPORT_SOURCE_LABELS = {"SOURCE", "SIGMA", "SV", "Var.", "%CONT.", "%TOTAL VAR", "%TOL."};
    public static final String EXPORT_GRR_ITEM_CATEGORIES = "NDC";

    public static final String[] EXPORT_ANOVA_LABELS = {"ANOVA", "DF", "SS", "MS", "F", "Prob>F"};

    public static final String EXPORT_YIELD_SUMMARY_TESTITEM = "TestItem:";
    public static final String EXPORT_YIELD_SUMMARY_USL = "USL:";
    public static final String EXPORT_YIELD_SUMMARY_LSL = "LSL:";
    public static final String EXPORT_YIELD_DETAIL_USL = "USL";
    public static final String EXPORT_YIELD_DETAIL_LSL = "LSL";
    public static final String[] EXPORT_YIELD_GROUPKEY_TITLE = {"Group", "First Inspect", "First Pass", "First Fail", "FPY", "ReTest Inspect", "ReTest Pass", "ReTest Fail", "ReTest Yield", "Total Yield"};
    public static final String[] EXPORT_YIELD_GROUPKEY_TITLE_ITEM = {"First Inspect", "First Pass", "First Fail", "ReTest Inspect", "ReTest Pass", "ReTest Fail"};

    public static final String[] EXPORT_CORRELATION_SUMMARY = {"", "", "USL", "LSL"};
}
