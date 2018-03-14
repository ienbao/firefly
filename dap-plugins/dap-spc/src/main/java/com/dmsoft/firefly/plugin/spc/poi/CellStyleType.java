
/*
 * Copyright (c) 2016. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.poi;

/**
 * Created by Alice on 2016/9/19.
 */
public enum CellStyleType {
    //Summary cell style
    summary_title,
    summary_content,
    summary_content_testItems,
    summary_content_testItems_1,
    summary_content_testItems_weight,
    summary_content_testItems_2,
    summary_content_testItems_3,
    summary_content_testItems_4,
    summary_content_testItems_5,
    summary_content_testItems_6,
    summary_content_testItems_7,
    summary_content_testItems_8,
    summary_content_Excellent,
    summary_content_Adequate,
    summary_content_Marginal,

    summary_params_text,
    summary_params_Excellent,
    summary_params_Adequate,
    summary_params_Marginal,
    summary_params_Bad,

    //TestItem cell style
    testItems_param_l_1,         //细边框黑色粗体白色背景
    testItems_param_l_2,         //细边框黑色粗体灰色背景
    testItems_param_l_3,
    testItems_param_l_4,
    testItems_param_l_5,
    testItems_param_l_6,

    testItems_param_m_1,         //细边框黑色粗体白色背景右对齐
    testItems_param_m_2,         //细边框黑色粗体灰色背景右对齐
    testItems_param_m_3,
    testItems_param_m_4,
    testItems_param_r_1,         //无边框黑色粗体白色背景右对齐
    testItems_param_r_2,
    testItems_param_r_3,
    testItems_param_r_4,
    testItems_param_r_5,
    testItems_param_r_6,

    //table sn cell style
    testItems_tbSn_title,         //双底边框黄色背景黑色粗体
    testItems_tbSn_key,
    testItems_tbSn_data,
    testItems_tbSn_rangeData,
    testItems_tbSn_avgData,
    testItems_tbSn_operator,
    testItems_tbSn_trial,
    testItems_tbSn_avgName,
    testItems_tbSn_rangeName,
    testItems_tbSn_totalName,

    //table anova cell style
    testItems_tbAnova_title_horizonal_l,
    testItems_tbAnova_title_horizonal_m,
    testItems_tbAnova_title_horizonal_r,
    testItems_tbAnova_title_vertical,
    testItems_tbAnova_content,

    //table source cell style
    testItems_tbSource_params_name,
    testItems_tbSource_params_value,
    testItems_tbSource_title_horizonal_l,
    testItems_tbSource_title_horizonal_m,
    testItems_tbSource_title_horizonal_r,
    testItems_tbSource_title_vertical,
    testItems_tbSource_content,
    testItems_tbSource_categoriesName,
    testItems_tbSource_categoriesValue,

    //testData sheet
    items_title,
    items_content,

    //yeild table cell style
    yield_summary_title,
    yield_summary_content_groupkey,
    yield_content_font,
    groupKey_table_up,
    groupKey_table_down,
    groupKey_table_left,
    groupKey_table_right,
    groupKey_table_up_l,
    groupKey_table_up_r,
    groupKey_table_down_l,
    groupKey_table_down_r,

    //spc title cell style
    spc_title,
    spc_value,
    link,

    //
    head_blue,              //blue background, white font and weight,left
    head_slightBlue,         //slight blue background, weight font,left
    head_blue_center,       //blue background, white font and weight,mid
    head_lightBlue,         //light blue background, weight font,left
    head_lightBlue_center       //lightblue background, white font and weight,mid
}
