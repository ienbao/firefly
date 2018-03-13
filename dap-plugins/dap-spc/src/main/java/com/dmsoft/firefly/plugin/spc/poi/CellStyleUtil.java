
/*
 *
 *  * Copyright (c) 2016. For Intelligent Group.
 *
 */

package com.dmsoft.firefly.plugin.spc.poi;

import com.google.common.collect.Maps;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;

import java.util.Map;

/**
 * Created by Alice on 2016/6/3.
 */
public class CellStyleUtil {

    private static Map<String, CellStyle> resultMap = Maps.newHashMap();
    private static Map<String, Object> styleParam = Maps.newHashMap();

    /**
     * Excel table cell style
     * @param workbook write
     * @return resultMap
     */
    public static Map<String, CellStyle> getStyle(SXSSFWorkbook workbook) {
        if (resultMap.size() == 0) {
            // GRR style
            styleParam.put("foreGroundColor", ExColor.GRAY);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("fontWeight", true);
            styleParam.put("alignment", HorizontalAlignment.CENTER);
            styleParam.put("borderType1", BorderStyle.THIN);
            resultMap.put(CellStyleType.summary_title.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("alignment", HorizontalAlignment.CENTER);
            styleParam.put("borderType1", BorderStyle.THIN);
            resultMap.put(CellStyleType.summary_content.toString(), getStyle(workbook, styleParam));
            resultMap.put(CellStyleType.testItems_tbSn_data.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("borderType1", BorderStyle.THIN);
            styleParam.put("fontColor", IndexedColors.BLUE.index);
            styleParam.put("alignment", HorizontalAlignment.LEFT);
//            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            resultMap.put(CellStyleType.summary_content_testItems.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("borderType1", BorderStyle.THIN);
            styleParam.put("alignment", HorizontalAlignment.LEFT);
            resultMap.put(CellStyleType.summary_content_testItems_1.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("fontWeight", true);
            styleParam.put("borderType1", BorderStyle.THIN);
            styleParam.put("alignment", HorizontalAlignment.LEFT);
            resultMap.put(CellStyleType.summary_content_testItems_weight.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("alignment", HorizontalAlignment.LEFT);
            resultMap.put(CellStyleType.summary_content_testItems_2.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("borderType1", BorderStyle.THIN);
            styleParam.put("alignment", HorizontalAlignment.LEFT);
            styleParam.put("foreGroundColor", ExColor.GREEN);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            resultMap.put(CellStyleType.summary_content_testItems_3.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("borderType1", BorderStyle.THIN);
            styleParam.put("alignment", HorizontalAlignment.LEFT);
            styleParam.put("foreGroundColor", ExColor.BLUE);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            resultMap.put(CellStyleType.summary_content_testItems_4.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("borderType1", BorderStyle.THIN);
            styleParam.put("alignment", HorizontalAlignment.LEFT);
            styleParam.put("foreGroundColor", ExColor.ORANGE);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            resultMap.put(CellStyleType.summary_content_testItems_5.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("borderType1", BorderStyle.THIN);
            styleParam.put("alignment", HorizontalAlignment.LEFT);
            styleParam.put("foreGroundColor", ExColor.RED);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            resultMap.put(CellStyleType.summary_content_testItems_6.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("alignment", HorizontalAlignment.LEFT);
            styleParam.put("foreGroundColor", ExColor.GREEN);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            resultMap.put(CellStyleType.summary_content_testItems_7.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("alignment", HorizontalAlignment.LEFT);
            styleParam.put("foreGroundColor", ExColor.RED);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            resultMap.put(CellStyleType.summary_content_testItems_8.toString(), getStyle(workbook, styleParam));


            styleParam.clear();
            styleParam.put("foreGroundColor", IndexedColors.GREEN.index);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("alignment", HorizontalAlignment.CENTER);
            styleParam.put("borderType1", BorderStyle.THIN);
            resultMap.put(CellStyleType.summary_content_Excellent.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", IndexedColors.SKY_BLUE.index);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("alignment", HorizontalAlignment.CENTER);
            styleParam.put("borderType1", BorderStyle.THIN);
            resultMap.put(CellStyleType.summary_content_Adequate.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.BLUE);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("alignment", HorizontalAlignment.CENTER);
            styleParam.put("borderType1", BorderStyle.THIN);
            resultMap.put(CellStyleType.summary_content_Marginal.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("alignment", HorizontalAlignment.RIGHT);
            resultMap.put(CellStyleType.summary_params_text.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.GREEN);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("alignment", HorizontalAlignment.LEFT);
            resultMap.put(CellStyleType.summary_params_Excellent.toString(), getStyle(workbook, styleParam));


            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.GREEN);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("alignment", HorizontalAlignment.LEFT);
            resultMap.put(CellStyleType.summary_params_Excellent.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.MBLUE);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("alignment", HorizontalAlignment.LEFT);
            resultMap.put(CellStyleType.summary_params_Adequate.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.ORANGE);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("alignment", HorizontalAlignment.LEFT);
            resultMap.put(CellStyleType.summary_params_Marginal.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.RED);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("alignment", HorizontalAlignment.LEFT);
            resultMap.put(CellStyleType.summary_params_Bad.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.BLUE);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("fontWeight", true);
            styleParam.put("fontColor", IndexedColors.WHITE.index);
            styleParam.put("alignment", HorizontalAlignment.LEFT);
            styleParam.put("borderType11", BorderStyle.THIN);
            resultMap.put(CellStyleType.testItems_param_l_1.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.BLUE);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("fontWeight", true);
            styleParam.put("fontColor", IndexedColors.WHITE.index);
            styleParam.put("alignment", HorizontalAlignment.LEFT);
            styleParam.put("borderType1", BorderStyle.THIN);
            resultMap.put(CellStyleType.head_blue.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.BLUE);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("fontWeight", true);
            styleParam.put("fontColor", IndexedColors.WHITE.index);
            styleParam.put("alignment", HorizontalAlignment.CENTER);
            styleParam.put("borderType1", BorderStyle.THIN);
            resultMap.put(CellStyleType.head_blue_center.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.S_LIGHT_BLUE);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("fontWeight", true);
            styleParam.put("alignment", HorizontalAlignment.LEFT);
            styleParam.put("borderType1", BorderStyle.THIN);
            resultMap.put(CellStyleType.head_slightBlue.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.LIGHT_BLUE);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("fontWeight", true);
            styleParam.put("alignment", HorizontalAlignment.LEFT);
            styleParam.put("borderType1", BorderStyle.THIN);
            resultMap.put(CellStyleType.head_lightBlue.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.LIGHT_BLUE);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("fontWeight", true);
            styleParam.put("alignment", HorizontalAlignment.LEFT);
            styleParam.put("borderType1", BorderStyle.THIN);
            resultMap.put(CellStyleType.head_lightBlue_center.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("fontColor", IndexedColors.BLUE.index);
            resultMap.put(CellStyleType.link.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.GRAY);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("fontWeight", true);
            styleParam.put("alignment", HorizontalAlignment.CENTER);
            styleParam.put("borderType7", BorderStyle.THIN);
            styleParam.put("borderType10", BorderStyle.DOUBLE);
            resultMap.put(CellStyleType.testItems_param_l_2.toString(), getStyle(workbook, styleParam));
            resultMap.put(CellStyleType.testItems_param_m_2.toString(), getStyle(workbook, styleParam));
            resultMap.put(CellStyleType.testItems_param_r_5.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.GRAY);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("alignment", HorizontalAlignment.CENTER);
            styleParam.put("borderType11", BorderStyle.THIN);
            resultMap.put(CellStyleType.testItems_param_l_3.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("borderType7", BorderStyle.THIN);
            styleParam.put("borderType10", BorderStyle.DOUBLE);
            resultMap.put(CellStyleType.testItems_param_l_4.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.GRAY);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("borderType6", BorderStyle.THIN);
            resultMap.put(CellStyleType.testItems_param_l_5.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.GRAY);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("borderType8", BorderStyle.THIN);
            styleParam.put("borderType10", BorderStyle.DOUBLE);
            resultMap.put(CellStyleType.testItems_param_l_6.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("fontWeight", true);
            styleParam.put("alignment", HorizontalAlignment.CENTER);
            styleParam.put("borderType4", BorderStyle.THIN);
            resultMap.put(CellStyleType.testItems_param_m_1.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("fontWeight", true);
            styleParam.put("alignment", HorizontalAlignment.CENTER);
            styleParam.put("borderType4", BorderStyle.THIN);
            styleParam.put("borderType8", BorderStyle.THIN);
            resultMap.put(CellStyleType.testItems_param_m_3.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.GRAY);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("fontWeight", true);
            styleParam.put("fontColor", IndexedColors.RED.index);
            styleParam.put("alignment", HorizontalAlignment.CENTER);
            styleParam.put("borderType2", BorderStyle.THIN);
            styleParam.put("borderType10", BorderStyle.DOUBLE);
            resultMap.put(CellStyleType.testItems_param_m_4.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("fontWeight", true);
            styleParam.put("alignment", HorizontalAlignment.RIGHT);
            styleParam.put("borderType1", BorderStyle.THIN);
            resultMap.put(CellStyleType.testItems_param_r_1.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("borderType1", BorderStyle.THIN);
            resultMap.put(CellStyleType.testItems_param_r_2.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("borderType7", BorderStyle.THIN);
            resultMap.put(CellStyleType.testItems_param_r_3.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("borderType4", BorderStyle.THIN);
            resultMap.put(CellStyleType.testItems_param_r_4.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.GRAY);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("fontWeight", true);
            styleParam.put("alignment", HorizontalAlignment.CENTER);
            styleParam.put("borderType2", BorderStyle.THIN);
            styleParam.put("borderType10", BorderStyle.DOUBLE);
            resultMap.put(CellStyleType.testItems_param_r_6.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.BLUE);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("fontWeight", true);
            styleParam.put("fontColor", IndexedColors.WHITE.index);
            styleParam.put("alignment", HorizontalAlignment.CENTER);
            styleParam.put("borderType1", BorderStyle.THIN);
            styleParam.put("borderType8", BorderStyle.DOUBLE);
            resultMap.put(CellStyleType.testItems_tbSn_title.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.S_LIGHT_BLUE);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("alignment", HorizontalAlignment.CENTER);
            styleParam.put("borderType1", BorderStyle.THIN);
            resultMap.put(CellStyleType.testItems_tbSn_rangeData.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.S_LIGHT_BLUE);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("alignment", HorizontalAlignment.CENTER);
            styleParam.put("borderType1", BorderStyle.THIN);
            styleParam.put("borderType8", BorderStyle.DOUBLE);
            resultMap.put(CellStyleType.testItems_tbSn_avgData.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.GRAY);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("alignment", HorizontalAlignment.CENTER);
            styleParam.put("fontWeight", true);
            styleParam.put("borderType3", BorderStyle.THIN);
            resultMap.put(CellStyleType.testItems_tbSn_operator.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.LIGHT_BLUE);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("alignment", HorizontalAlignment.CENTER);
            styleParam.put("fontWeight", true);
            styleParam.put("borderType3", BorderStyle.THIN);
            resultMap.put(CellStyleType.testItems_tbSn_trial.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.LIGHT_BLUE);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("alignment", HorizontalAlignment.CENTER);
            styleParam.put("fontWeight", true);
            styleParam.put("borderType1", BorderStyle.THIN);
            resultMap.put(CellStyleType.testItems_tbSn_rangeName.toString(), getStyle(workbook, styleParam));
            resultMap.put(CellStyleType.testItems_tbSn_totalName.toString(), getStyle(workbook, styleParam));
            resultMap.put(CellStyleType.testItems_tbSource_categoriesName.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.LIGHT_BLUE);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("alignment", HorizontalAlignment.CENTER);
            styleParam.put("fontWeight", true);
            styleParam.put("borderType1", BorderStyle.THIN);
            styleParam.put("borderType8", BorderStyle.DOUBLE);
            resultMap.put(CellStyleType.testItems_tbSn_avgName.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.BLUE);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("alignment", HorizontalAlignment.CENTER);
            styleParam.put("fontWeight", true);
            styleParam.put("fontColor", IndexedColors.WHITE.index);
            styleParam.put("borderType1", BorderStyle.THIN);
            resultMap.put(CellStyleType.testItems_tbAnova_title_horizonal_l.toString(), getStyle(workbook, styleParam));
            resultMap.put(CellStyleType.testItems_tbSource_title_horizonal_l.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.BLUE);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("alignment", HorizontalAlignment.CENTER);
            styleParam.put("fontWeight", true);
            styleParam.put("fontColor", IndexedColors.WHITE.index);
            styleParam.put("borderType1", BorderStyle.THIN);
            resultMap.put(CellStyleType.testItems_tbAnova_title_horizonal_m.toString(), getStyle(workbook, styleParam));
            resultMap.put(CellStyleType.testItems_tbSource_title_horizonal_m.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.BLUE);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("alignment", HorizontalAlignment.CENTER);
            styleParam.put("fontWeight", true);
            styleParam.put("fontColor", IndexedColors.WHITE.index);
            styleParam.put("borderType1", BorderStyle.THIN);
            resultMap.put(CellStyleType.testItems_tbAnova_title_horizonal_r.toString(), getStyle(workbook, styleParam));
            resultMap.put(CellStyleType.testItems_tbSource_title_horizonal_r.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.LIGHT_BLUE);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("alignment", HorizontalAlignment.CENTER);
            styleParam.put("fontWeight", true);
            styleParam.put("borderType1", BorderStyle.THIN);
            resultMap.put(CellStyleType.testItems_tbAnova_title_vertical.toString(), getStyle(workbook, styleParam));
            resultMap.put(CellStyleType.testItems_tbSource_title_vertical.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.S_LIGHT_BLUE);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("alignment", HorizontalAlignment.CENTER);
            styleParam.put("borderType1", BorderStyle.THIN);
            resultMap.put(CellStyleType.testItems_tbAnova_content.toString(), getStyle(workbook, styleParam));
            resultMap.put(CellStyleType.testItems_tbSource_content.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.BLUE);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("fontWeight", true);
            styleParam.put("fontColor", IndexedColors.WHITE.index);
            styleParam.put("alignment", HorizontalAlignment.RIGHT);
            styleParam.put("borderType1", BorderStyle.THIN);
            resultMap.put(CellStyleType.testItems_tbSource_params_name.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.S_LIGHT_BLUE);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("fontWeight", true);
            styleParam.put("fontColor", IndexedColors.BLUE.index);
            styleParam.put("alignment", HorizontalAlignment.CENTER);
            styleParam.put("borderType1", BorderStyle.THIN);
            resultMap.put(CellStyleType.testItems_tbSource_params_value.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", IndexedColors.ROSE.index);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("alignment", HorizontalAlignment.CENTER);
            styleParam.put("borderType1", BorderStyle.THIN);
            resultMap.put(CellStyleType.testItems_tbSource_categoriesValue.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.GRAY);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("borderType1", BorderStyle.THIN);
            resultMap.put(CellStyleType.items_title.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("borderType1", BorderStyle.THIN);
            styleParam.put("alignment", HorizontalAlignment.LEFT);
            resultMap.put(CellStyleType.items_content.toString(), getStyle(workbook, styleParam));


            // spc title style
            XSSFCellStyle spcTitleCellStyle = (XSSFCellStyle) workbook.createCellStyle();
            BorderStyle spcTitleBorderStyle = BorderStyle.THIN;
            spcTitleCellStyle.setFillForegroundColor(ExColor.BLUE);
            spcTitleCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//            spcTitleCellStyle.setAlignment(HorizontalAlignment.CENTER);
            spcTitleCellStyle.setBorderBottom(spcTitleBorderStyle);
            spcTitleCellStyle.setBorderLeft(spcTitleBorderStyle);
            spcTitleCellStyle.setBorderRight(spcTitleBorderStyle);
            spcTitleCellStyle.setBorderTop(spcTitleBorderStyle);
            XSSFFont spcTitileFont = (XSSFFont) workbook.createFont();
            spcTitileFont.setColor(IndexedColors.WHITE.index);
            spcTitleCellStyle.setFont(spcTitileFont);
            resultMap.put(CellStyleType.spc_title.toString(), spcTitleCellStyle);

            // spc value style
            XSSFCellStyle spcValueCellStyle = (XSSFCellStyle) workbook.createCellStyle();
            spcValueCellStyle.setWrapText(true);
            BorderStyle spcValueBorderStyle = BorderStyle.THIN;
            spcValueCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            spcValueCellStyle.setBorderBottom(spcValueBorderStyle);
            spcValueCellStyle.setBorderLeft(spcValueBorderStyle);
            spcValueCellStyle.setBorderRight(spcValueBorderStyle);
            spcValueCellStyle.setBorderTop(spcValueBorderStyle);
            resultMap.put(CellStyleType.spc_value.toString(), spcValueCellStyle);

            // yield style
            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.BLUE);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("fontColor", IndexedColors.WHITE.index);
            resultMap.put(CellStyleType.yield_summary_title.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("fontColor", IndexedColors.BLUE.index);
            resultMap.put(CellStyleType.yield_summary_content_groupkey.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("fontName", "Calibri");
            resultMap.put(CellStyleType.yield_content_font.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.BLUE);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("fontColor", IndexedColors.WHITE.index);
            styleParam.put("borderType7", BorderStyle.THIN);
            resultMap.put(CellStyleType.groupKey_table_up.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("borderType8", BorderStyle.THIN);
            resultMap.put(CellStyleType.groupKey_table_down.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("borderType9", BorderStyle.THICK);
            resultMap.put(CellStyleType.groupKey_table_left.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("borderType10", BorderStyle.THIN);
            resultMap.put(CellStyleType.groupKey_table_right.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.BLUE);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("fontColor", IndexedColors.WHITE.index);
            styleParam.put("borderType7", BorderStyle.THIN);
            styleParam.put("borderType9", BorderStyle.THICK);
            resultMap.put(CellStyleType.groupKey_table_up_l.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("foreGroundColor", ExColor.BLUE);
            styleParam.put("pattern", FillPatternType.SOLID_FOREGROUND);
            styleParam.put("fontColor", IndexedColors.WHITE.index);
            styleParam.put("borderType4", BorderStyle.THIN);
            resultMap.put(CellStyleType.groupKey_table_up_r.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("borderType5", BorderStyle.THIN);
            resultMap.put(CellStyleType.groupKey_table_down_r.toString(), getStyle(workbook, styleParam));

            styleParam.clear();
            styleParam.put("borderType8", BorderStyle.THIN);
            styleParam.put("borderType9", BorderStyle.THICK);
            resultMap.put(CellStyleType.groupKey_table_down_l.toString(), getStyle(workbook, styleParam));

            }
        return resultMap;
    }

    private static CellStyle getStyle(SXSSFWorkbook workbook, Map<String, Object> styleParam) {

        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Arial");
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
        style.setWrapText(true);

        if (styleParam.containsKey("foreGroundColor")) {
            if (styleParam.get("foreGroundColor") instanceof XSSFColor) {
                style.setFillForegroundColor((XSSFColor) styleParam.get("foreGroundColor"));
            } else {
                style.setFillForegroundColor((Short) styleParam.get("foreGroundColor"));
            }
        }

        if (styleParam.containsKey("pattern")) {
            style.setFillPattern((FillPatternType) styleParam.get("pattern"));
        }

        if (styleParam.containsKey("fontWeight")) {
            font.setBold((boolean) styleParam.get("fontWeight"));
        }

        if (styleParam.containsKey("fontColor")) {
            font.setColor((Short) styleParam.get("fontColor"));
        }

        if (styleParam.containsKey("fontName")) {
            font.setFontName((String) styleParam.get("fontName"));
        }

        if (styleParam.containsKey("alignment")) {
            style.setAlignment((HorizontalAlignment) styleParam.get("alignment"));
        }

        if (styleParam.containsKey("borderType1")) {
            style.setBorderTop((BorderStyle) styleParam.get("borderType1")); //上边框
            style.setBorderBottom((BorderStyle) styleParam.get("borderType1")); //下边框
            style.setBorderLeft((BorderStyle) styleParam.get("borderType1")); //左边框
            style.setBorderRight((BorderStyle) styleParam.get("borderType1")); //右边框
        }

        if (styleParam.containsKey("borderType2")) {
            style.setBorderTop((BorderStyle) styleParam.get("borderType2")); //上边框
            style.setBorderBottom((BorderStyle) styleParam.get("borderType2")); //下边框
        }

        if (styleParam.containsKey("borderType3")) {
            style.setBorderLeft((BorderStyle) styleParam.get("borderType3")); //左边框
            style.setBorderRight((BorderStyle) styleParam.get("borderType3")); //右边框
        }

        if (styleParam.containsKey("borderType4")) {
            style.setBorderTop((BorderStyle) styleParam.get("borderType4")); //上边框
            style.setBorderRight((BorderStyle) styleParam.get("borderType4")); //右边框
        }

        if (styleParam.containsKey("borderType11")) {
            style.setBorderTop((BorderStyle) styleParam.get("borderType11")); //下边框
            style.setBorderLeft((BorderStyle) styleParam.get("borderType11")); //左边框
        }

        if (styleParam.containsKey("borderType5")) {
            style.setBorderBottom((BorderStyle) styleParam.get("borderType5")); //下边框
            style.setBorderRight((BorderStyle) styleParam.get("borderType5")); //右边框
        }

        if (styleParam.containsKey("borderType6")) {
            style.setBorderBottom((BorderStyle) styleParam.get("borderType6")); //下边框
            style.setBorderLeft((BorderStyle) styleParam.get("borderType6")); //左边框
        }

        if (styleParam.containsKey("borderType7")) {
            style.setBorderTop((BorderStyle) styleParam.get("borderType7")); //上边框
        }

        if (styleParam.containsKey("borderType8")) {
            style.setBorderBottom((BorderStyle) styleParam.get("borderType8")); //下边框
        }

        if (styleParam.containsKey("borderType9")) {
            style.setBorderLeft((BorderStyle) styleParam.get("borderType9")); //左边框
        }

        if (styleParam.containsKey("borderType10")) {
            style.setBorderRight((BorderStyle) styleParam.get("borderType10")); //右边框
        }

        style.setFont(font);
        return style;
    }

    /**
     * clear result map
     */
    public static void clearResultMap() {
        resultMap.clear();
    }
}
