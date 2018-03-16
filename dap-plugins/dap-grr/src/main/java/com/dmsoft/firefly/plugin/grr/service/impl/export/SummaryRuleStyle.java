/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.grr.service.impl.export;

import com.google.common.collect.Maps;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import java.util.Map;

/**
 * Created by Alice on 2016/9/8.
 */
public class SummaryRuleStyle {
    /**
     * Return Back style.
     *
     * @param workbook workbook
     * @return resultMap
     */
    public static Map<String, CellStyle> getBackStyle(SXSSFWorkbook workbook) {
        Map<String, CellStyle> resultMap = Maps.newHashMap();
        if (resultMap.size() == 0) {
            Font font = workbook.createFont();
            font.setFontName("Arial");
            XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
            style.setFillForegroundColor(ExColor.GREEN);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setFont(font);
            style.setAlignment(HorizontalAlignment.LEFT);
            resultMap.put(ExportLabelConstant.EXPORT_SUMMARY_EXCELLENT, style);

            style = (XSSFCellStyle) workbook.createCellStyle();
            style.setFillForegroundColor(ExColor.MBLUE);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setFont(font);
            style.setAlignment(HorizontalAlignment.LEFT);
            resultMap.put(ExportLabelConstant.EXPORT_SUMMARY_ADEQUTE, style);

            style = (XSSFCellStyle) workbook.createCellStyle();
            style.setFillForegroundColor(ExColor.MBLUE);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setFont(font);
            style.setAlignment(HorizontalAlignment.LEFT);
            resultMap.put("ADEQUATE", style);

            style = (XSSFCellStyle) workbook.createCellStyle();
            style.setFillForegroundColor(ExColor.ORANGE);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setFont(font);
            style.setAlignment(HorizontalAlignment.LEFT);
            resultMap.put(ExportLabelConstant.EXPORT_SUMMARY_MARGIMAL, style);

            style = (XSSFCellStyle) workbook.createCellStyle();
            style.setFillForegroundColor(ExColor.RED);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setFont(font);
            style.setAlignment(HorizontalAlignment.LEFT);
            resultMap.put(ExportLabelConstant.EXPORT_SUMMARY_BAD, style);

            style = (XSSFCellStyle) workbook.createCellStyle();
            style.setFillForegroundColor(ExColor.WHITE);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setFont(font);
            style.setAlignment(HorizontalAlignment.LEFT);
            resultMap.put(ExportLabelConstant.EXPORT_SUMMARY_NULL, style);
        }
        return resultMap;
    }
}
