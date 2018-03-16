/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.grr.service.impl.export;

import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.google.common.collect.Lists;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * draw GRR excel
 * Created by Ailce on 2016/9/23.
 */
public class GrrExcelBuilder extends SampleExcelBuilder {
    private static Logger logger = LoggerFactory.getLogger(GrrExcelBuilder.class);

    private Integer operator;
    private Integer trial;
    private List<Integer> breakRowLists = Lists.newArrayList();
    private Integer disCategories = 2;
    private Map<String, Integer> disCategoriesMap = new HashMap<>();

    /**
     * Constructor
     *
     * @param operator operator
     * @param trial    trail
     */
    public GrrExcelBuilder(Integer operator, Integer trial) {
        this.operator = operator;
        this.trial = trial;
    }

    /**
     * draw excel of summary and detail
     *
     * @param path              file path of exporting excel
     * @param factory           class of filling data to cell
     * @param length            length
     * @param itemNameAddLength itemNameAddLength
     */
    public void drawExcel(String path, GrrExportWorker factory, int length, int itemNameAddLength) {
        Iterator<ExSheet> sheet = factory.getSheets().iterator();
        SXSSFWorkbook currentWb = factory.getCurrentWorkbook();
         disCategoriesMap = factory.getDisCategoriesPlaceMap();
        OutputStream out = null;
        try {
            int i = 0;
            out = new FileOutputStream(path);
            for (; sheet.hasNext(); i++) {
                ExSheet exSheet = sheet.next();
                breakRowLists = factory.getBreakRowLists().get(exSheet.getName());
                if (disCategoriesMap != null && disCategoriesMap.size() != 0) {
                    disCategories = factory.getDisCategoriesPlaceMap().get(exSheet.getName());
                }
                drawGrrSheet(currentWb, exSheet, i, length, itemNameAddLength);
            }
            currentWb.write(out);
        } catch (Exception e) {
            logger.error("can not find directory:" + e);
            throw new ApplicationException(e.getMessage());
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void drawGrrSheet(SXSSFWorkbook workbook, ExSheet exSheet, int i, int length, int itemNameAddLength) {
        //draw excel of summary table
        if (i == 0) {
            String name = exSheet.getName();
            logger.debug("sheet name:{}", name);
            List<ExCell> exCells = exSheet.getExCells();
            SXSSFSheet sheet = workbook.createSheet(name);

            CellRangeAddress cellRangeAddressPer = new CellRangeAddress(0, 0, 3, 4);
            sheet.addMergedRegion(cellRangeAddressPer);

            for (int k = 0; k < itemNameAddLength; k++) {
                CellRangeAddress cellRangeAddressResult = new CellRangeAddress(length + 4 + k, length + 4 + k, 0, 4);
                sheet.addMergedRegion(cellRangeAddressResult);
            }

            final int columnWidth13 = 10;
            final int columnWidth23 = 23;
            sheet.setDefaultColumnWidth(columnWidth13);
            sheet.setColumnWidth(0, columnWidth23 * 256);
            drawSheet(exCells, sheet, workbook);
            try {
                sheet.flushRows();
            } catch (IOException e) {
                e.printStackTrace();
            }
            sheet = null;
        } else {
            //draw excel of detail table
            drawGRRItemSheet(workbook, exSheet);
//            drawGRRItemsSheetOld(workbook, exSheet);
        }
    }

    private void drawGRRItemSheet(SXSSFWorkbook workbook, ExSheet exSheet) {
        String name = exSheet.getName();
        logger.debug("sheet name:{}", name);
        List<ExCell> exCells = exSheet.getExCells();
        SXSSFSheet sheet = workbook.createSheet(name);

        CellRangeAddress cellRangeAddressTestHead = new CellRangeAddress(1, 1, 0, 5);
        sheet.addMergedRegion(cellRangeAddressTestHead);

        for (int i = 2; i < 10; i++) {
            CellRangeAddress cellRangeAddressTest = new CellRangeAddress(i, i, 1, 5);
            sheet.addMergedRegion(cellRangeAddressTest);
        }

//        if (disCategoriesMap != null && disCategoriesMap.size() != 0) {
//            CellRangeAddress cellRangeAddressTestNum = new CellRangeAddress(disCategories, disCategories, 0, 1);
//            sheet.addMergedRegion(cellRangeAddressTestNum);
//        }

        if (breakRowLists != null && !breakRowLists.isEmpty()) {
            for (int row : breakRowLists) {
                sheet.setRowBreak(row);
            }
        }

        final int columnWidth10 = 8;
        final int columnWidth13 = 13;
        sheet.setDefaultColumnWidth(columnWidth10);
        sheet.setColumnWidth(0, columnWidth13 * 256);

        // Merge cells of table head
        int currentRow = 1;
        final int columnNum1 = 1;
        final int columnNum7 = 7;

        CellRangeAddress testItem = new CellRangeAddress(currentRow, currentRow, columnNum1, columnNum1 + 1);
        CellRangeAddress blank = new CellRangeAddress(currentRow, currentRow, columnNum7, columnNum7 + 1);
        sheet.addMergedRegion(blank);
        sheet.addMergedRegion(testItem);
        currentRow++;
        CellRangeAddress testItemValue = new CellRangeAddress(currentRow, currentRow + 1, columnNum1, columnNum1 + 1);
        sheet.addMergedRegion(testItemValue);

        //Merge cells of normal
        currentRow = operator * trial + operator * 2 + 6;
        CellRangeAddress partRange = new CellRangeAddress(currentRow, currentRow, columnNum1, columnNum1 + 1);
        sheet.addMergedRegion(partRange);
        currentRow++;
        CellRangeAddress partAVG = new CellRangeAddress(currentRow, currentRow, columnNum1, columnNum1 + 1);
        sheet.addMergedRegion(partAVG);

        // Merge cells of anova
        currentRow = currentRow + 2;
        for (int i = 0; i < 6; i++) {
            int n = 1;
            CellRangeAddress anova = new CellRangeAddress(currentRow, currentRow, n++, n++);
            sheet.addMergedRegion(anova);
            currentRow++;
        }

        // Merge cells of source
        currentRow++;
        CellRangeAddress sigmaTitle = new CellRangeAddress(currentRow, currentRow, columnNum1, columnNum7);
        sheet.addMergedRegion(sigmaTitle);

        currentRow++;
        for (int i = 0; i < 8; i++) {
            int n = 1;
            CellRangeAddress source = new CellRangeAddress(currentRow, currentRow, n++, n++);
            sheet.addMergedRegion(source);
            currentRow++;
        }

        //Merge cells of last table
        currentRow++;
        final int columnNum4 = 4;
        CellRangeAddress categoryName = new CellRangeAddress(currentRow, currentRow, columnNum1, columnNum4);
        sheet.addMergedRegion(categoryName);

        final int columnNum5 = 5;
        CellRangeAddress categoryValue = new CellRangeAddress(currentRow, currentRow, columnNum5, columnNum5 + 1);
        sheet.addMergedRegion(categoryValue);

        final int columnWidth = 20;
        final int columnWidth5 = 5;
        final int columnWidth15 = 15;
        sheet.setDefaultColumnWidth(columnWidth);
        sheet.setColumnWidth(0, columnWidth5 * 256);
        sheet.setColumnWidth(1, columnWidth15 * 256);
        sheet.setColumnWidth(2, columnWidth5 * 256);
        sheet.createFreezePane(0, 1, 0, 1);


        drawSheet(exCells, sheet, workbook);
        try {
            sheet.flushRows();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sheet = null;
    }
}

