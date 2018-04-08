package com.dmsoft.firefly.plugin.spc.export;

import com.dmsoft.firefly.plugin.spc.poi.*;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.google.common.collect.Lists;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Eligi.Ran on 2016/9/23.
 */
public class SpcExportBuilder extends SampleExcelBuilder {
    private static Logger logger = LoggerFactory.getLogger(SpcExportBuilder.class);
    private SXSSFWorkbook currentWb;
    private CreationHelper creationHelper;
    public static final String OS_NAME;
    private static final String OS_WIN = "win";
    private List<Integer> breakRowLists = Lists.newArrayList();
    private int runChartRows;
    private int perRow;

    static {
        OS_NAME = System.getProperty("os.name");
    }

    /**
     * clear all the params
     */
    public void clear() {
        currentWb = null;
        creationHelper = null;
    }

    /**
     * draw spc excel
     *
     * @param path    excel path.
     * @param factory contains data to export.
     */
    public void drawSpcExcel(String path, ExWorker factory) {
        SpcExportWorker spcExportWorker = (SpcExportWorker) factory;
        Iterator<ExSheet> sheets = spcExportWorker.getSheets().iterator();
        int[] chartImgInformation = spcExportWorker.getChartImageInfo();
        init(spcExportWorker);
        OutputStream out = null;
        breakRowLists = spcExportWorker.getBreakRowLists();
        runChartRows = spcExportWorker.getRunChartRow();
        perRow = spcExportWorker.getPerRow();
        try {

            currentWb = spcExportWorker.getCurrentWorkbook();
//            ZipSecureFile.setMinInflateRatio(0.0001);
            currentWb.setCompressTempFiles(true);
            out = new FileOutputStream(path);
            for (int i = 0; sheets.hasNext(); i++) {
                ExSheet currentSheet = sheets.next();
                sheets.remove();
//                if (chartImgInformation != null && i == chartImgInformation[0]) {
//                    drawSpcSheet(currentWb, currentSheet, chartImgInformation);
//                    continue;
//                }
                drawSpcSheet(currentWb, currentSheet, chartImgInformation);
                if (!sheets.hasNext()) {
                    logger.debug("Writing excel.");
                    currentWb.write(out);
                    out.close();
                    currentWb.dispose();
                    currentWb.close();
                    logger.debug("Excel at " + path + " has been written done.");
                }
                currentSheet = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    private void drawSpcSheet(SXSSFWorkbook workbook, ExSheet exSheet, int[] chartImgPos) {
        String name = exSheet.getName();
        Integer firstColWidth = null;
        final int fstColWidth = 1000;
        boolean breakRow = false;
        if (name.equals("_2")) {
            return;
        }
        if (name.equals("Summary_1")) {
            name = "Summary";
            breakRow = false;
        } else if (name.equals("All_Summary_Chart_2")) {
            name = "All_Summary_Chart";
            breakRow = true;
        } else if (exSheet.getIndex() > 0) {
            firstColWidth = fstColWidth;
            breakRow = true;
        }
        logger.debug("sheet name:{}", name);
        List<ExCell> exCells = exSheet.getExCells();
        SXSSFSheet sheet = null;

        try {
            sheet = workbook.createSheet(name);
        } catch (IllegalArgumentException e) {
            sheet = workbook.getSheet(name);
        }

        chartImgPos[4]++;
        if (chartImgPos[4] == 0) {
            int startCol = chartImgPos[1], endCol = chartImgPos[2], width = chartImgPos[3];
            sheet.setColumnWidth(startCol - 1, width / 8);
            for (int i = startCol; i <= endCol  + 1; i++) {
                sheet.setColumnWidth(i, width);
            }
        }
        sheet.setDefaultColumnWidth(13);

//        sheet.setFitToPage(false);
        if (breakRow) {
            for (int row : breakRowLists) {
                sheet.setRowBreak(row);
            }
        }

        drawSheet(exCells, sheet, workbook, firstColWidth);
        exCells.clear();
        try {
            sheet.flushRows();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sheet = null;

    }

    protected void drawSheet(List<ExCell> exCells, SXSSFSheet sheet, SXSSFWorkbook workbook, Integer firstColWidth) {
        if (sheet.getSheetName().equals("Summary")) {
            // indexes of columns that need to adjust column width.
            final int[] adjustCols = new int[]{2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13, 14, 15, 16, 17, 18, 19};
            int tempLength = adjustCols.length;
            final int adjustColWidth = 3800;
            sheet.setColumnWidth(0, 3400);
            sheet.setColumnWidth(1, adjustColWidth);
            for (int i = 0; i < tempLength; i++) {
                sheet.setColumnWidth(adjustCols[i], adjustColWidth);
            }
            //combine cell
            sheet.addMergedRegion(new CellRangeAddress(
                    0, //first row (0-based)
                    0, //last row  (0-based)
                    1, //first column (0-based)
                    2  //last column  (0-based)
            ));
            sheet.addMergedRegion(new CellRangeAddress(
                    1, //first row (0-based)
                    1, //last row  (0-based)
                    1, //first column (0-based)
                    2  //last column  (0-based)
            ));
        } else {
            if (runChartRows != 0) {
                sheet.addMergedRegion(new CellRangeAddress(
                        runChartRows, //first row (0-based)
                        runChartRows, //last row  (0-based)
                        1, //first column (0-based)
                        2  //last column  (0-based)
                ));
                sheet.addMergedRegion(new CellRangeAddress(
                        runChartRows, //first row (0-based)
                        runChartRows, //last row  (0-based)
                        3, //first column (0-based)
                        4  //last column  (0-based)
                ));
            }

            sheet.addMergedRegion(new CellRangeAddress(
                    1, //first row (0-based)
                    1, //last row  (0-based)
                    1, //first column (0-based)
                    4  //last column  (0-based)
            ));

            sheet.addMergedRegion(new CellRangeAddress(
                    2, //first row (0-based)
                    2, //last row  (0-based)
                    1, //first column (0-based)
                    4  //last column  (0-based)
            ));

            sheet.addMergedRegion(new CellRangeAddress(
                    6, //first row (0-based)
                    6, //last row  (0-based)
                    3, //first column (0-based)
                    4  //last column  (0-based)
            ));

            sheet.addMergedRegion(new CellRangeAddress(
                    10, //first row (0-based)
                    10, //last row  (0-based)
                    0, //first column (0-based)
                    1  //last column  (0-based)
            ));

            sheet.addMergedRegion(new CellRangeAddress(
                    perRow, //first row (0-based)
                    perRow, //last row  (0-based)
                    3, //first column (0-based)
                    4  //last column  (0-based)
            ));


            final int[] adjustCols = new int[]{0, 1, 2, 3, 4, 5};
            for (int col = 0; col < adjustCols.length; col++) {
                sheet.setColumnWidth(adjustCols[col], 3300);
            }
        }
        for (int i = 0; i < exCells.size(); i++) {
            ExCell exCell = exCells.get(i);
            if (ExCellType.IMAGE.equals(exCell.getCellType())) {
                fillImageToSheet(workbook, sheet, exCell);
            } else if (ExCellType.TEXT.equals(exCell.getCellType())) {
                fillTextToSheet(sheet, exCell);
            } else if ((ExCellType.HYPERLINK.equals(exCell.getCellType()))) {
                fillHyperLinkToSheet(sheet, exCell);
            }
            exCell = null;
        }
    }

    private void fillImageToSheet(SXSSFWorkbook workbook, SXSSFSheet sheet, ExCell exCell) {
        Integer[] c = exCell.getCoordinate();
        String value = exCell.getValue();
        if (c.length < 8) {
            return;
        }

        InputStream is = null;
        Integer pictureIdx = null;
        try {
            is = new FileInputStream(value);
            byte[] bytes = IOUtils.toByteArray(is);
            pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Drawing drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = new XSSFClientAnchor(c[0], c[1], c[2], c[3], c[4].shortValue(), c[5], c[6].shortValue(), c[7]);
        if (drawing != null) {
            Picture pict = drawing.createPicture(anchor, pictureIdx);
            pict.resize(1, 1);
        }
        sheet.setDefaultRowHeight((short) (16 * 15));
    }

    private void fillTextToSheet(SXSSFSheet sheet, ExCell exCell) {
        Integer[] coordinate = exCell.getCoordinate();
        String value = exCell.getValue();
        CellStyle cellStyle = exCell.getStyle();
        if (coordinate.length < 2) {
            return;
        }

        int x = coordinate[0].intValue();
        int y = coordinate[1].intValue();

        SXSSFRow row = sheet.getRow(x);

        if (row == null) {
            row = sheet.createRow(x);
            row.setHeight((short) (20 * 15));
        }

        SXSSFCell cell = row.getCell(y);
        if (cell == null) {
            cell = row.createCell(y);
        }

        if (cellStyle != null) {
            cellStyle.setWrapText(true);
           /* if (OS_NAME.toLowerCase().startsWith(OS_WIN)) {
                cellStyle.setWrapText(true);
            } else {
                cellStyle.setWrapText(false);
            }*/
//            cellStyle.setWrapText(true);
            cell.setCellStyle(cellStyle);
        }
        if (value == null) {
            value = "";
        }

        cell.setCellValue(new XSSFRichTextString(value));
    }

    private void fillHyperLinkToSheet(SXSSFSheet sheet, ExCell exCell) {
        Integer[] coordinate = exCell.getCoordinate();
        String value = exCell.getValue();
        String hyperlinkCode = exCell.getHyperLinkCode();
        CellStyle cellStyle = exCell.getStyle();

        if (coordinate.length < 2) {
            return;
        }

        int x = coordinate[0].intValue();
        int y = coordinate[1].intValue();

        SXSSFRow row = sheet.getRow(x);

        if (row == null) {
            row = sheet.createRow(x);
            row.setHeight((short) (20 * 15));
        }

        SXSSFCell cell = row.getCell(y);
        if (cell == null) {
            cell = row.createCell(y);
        }

        if (cellStyle != null) {
            cellStyle.setWrapText(true);
            cell.setCellStyle(cellStyle);
        }

        if (hyperlinkCode != null) {
//            CreationHelper createHelper = sheet.getWorkbook().getCreationHelper();
//            Hyperlink link = createHelper.createHyperlink(Hyperlink.LINK_DOCUMENT);
//            int tempRow = Integer.parseInt(hyperlinkCode.substring(hyperlinkCode.length()-1,hyperlinkCode.length()));
            Hyperlink link = creationHelper.createHyperlink(HyperlinkType.DOCUMENT);
            link.setAddress("'" + hyperlinkCode + "'!R1C1");
            cell.setHyperlink(link);
        }

        XSSFRichTextString text = new XSSFRichTextString(value);
        cell.setCellValue(text);
    }

    private void init(SpcExportWorker spcExportWorker) {
        currentWb = spcExportWorker.getCurrentWorkbook();
        creationHelper = currentWb.getCreationHelper();

    }

}