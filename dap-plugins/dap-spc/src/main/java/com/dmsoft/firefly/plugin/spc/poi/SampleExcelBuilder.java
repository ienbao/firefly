/*
 *
 *  * Copyright (c) 2016. For Intelligent Group.
 *
 */

package com.dmsoft.firefly.plugin.spc.poi;

import com.dmsoft.firefly.sdk.exception.ApplicationException;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

import static com.dmsoft.firefly.sdk.utils.DAPStringUtils.filterSpeChars;

/**
 * Created by Peter on 2016/4/18.
 */
public class SampleExcelBuilder implements ExcelBuilder {
    private static Logger logger = LoggerFactory.getLogger(SampleExcelBuilder.class);

    public static final String OS_NAME;
    private static final String OS_WIN = "win";

    static {
        OS_NAME = System.getProperty("os.name");
    }

    @Override
    public void drawExcel(String path, ExWorker factory) {

        SXSSFWorkbook workbook = factory.getCurrentWorkbook();
        List<ExSheet> sheets = factory.getSheets();

        try {
            OutputStream out = new FileOutputStream(path);
            for (int i = 0; i < sheets.size(); i++) {
                drawSheet(workbook, sheets.get(i));
            }
            workbook.write(out);
            out.close();

        } catch (Exception e) {

            logger.error("can not find directory:" + e);
            throw new ApplicationException(e.getMessage());
        }
        sheets = null;
    }

    protected void drawSheet(SXSSFWorkbook workbook, ExSheet exSheet) {

        String name = exSheet.getName();
        logger.debug("sheet name:{}", name);
        List<ExCell> exCells = exSheet.getExCells();
        SXSSFSheet sheet = workbook.createSheet(name);
        sheet.setDefaultColumnWidth(8);
        sheet.setColumnWidth(0, 10 * 256);//30 * 256
        //sheet.setColumnWidth(1, 8 * 256);//30 * 256
        drawSheet(exCells, sheet, workbook);
        exCells = null;
        sheet = null;
        workbook = null;
    }

    protected void drawSheet(List<ExCell> exCells, SXSSFSheet sheet, SXSSFWorkbook workbook) {
        for (int i = 0; i < exCells.size(); i++) {
            ExCell exCell = exCells.get(i);
            if (ExCellType.IMAGE.equals(exCell.getCellType())) {
                fillImageToSheet(workbook, sheet, exCell);
            } else if (ExCellType.TEXT.equals(exCell.getCellType())) {
                fillTextToSheet(sheet, exCell);
            } else if (ExCellType.HYPERLINK.equals(exCell.getCellType())) {
                fillHyperLinkToSheet(sheet, exCell);
            } else if (ExCellType.IMAGE_ANCHOR.equals(exCell.getCellType())) {
                fillImageToSheetAnchor(workbook, sheet, exCell);
            }
        }
        exCells = null;
    }

    private void fillImageToSheetAnchor(SXSSFWorkbook workbook, SXSSFSheet sheet, ExCell exCell) {
        Integer[] c = exCell.getCoordinate();
        String value = exCell.getValue();
        if (c.length != 2) {
            return;
        }
        try {
            InputStream is = new FileInputStream(value);
            byte[] bytes = IOUtils.toByteArray(is);
            int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);

            CreationHelper helper = workbook.getCreationHelper();
            Drawing patriarch = sheet.createDrawingPatriarch();
            ClientAnchor anchor = helper.createClientAnchor();

            anchor.setCol1(c[0]);
            anchor.setRow1(c[1]);

            Picture pict = patriarch.createPicture(anchor, pictureIdx);
            pict.getClientAnchor().setAnchorType(ClientAnchor.AnchorType.DONT_MOVE_AND_RESIZE);
            pict.resize();
            //resize ???
//            double d = 1.7976931348623157E308D;
//            XSSFPicture picture = (XSSFPicture) pict;
//            XSSFClientAnchor pref = picture.getPreferredSize(d, d);
//            int row2 = anchor.getRow1() + (pref.getRow2() - pref.getRow1());
//            int col2 = anchor.getCol1() + (pref.getCol2() - pref.getCol1());
//            anchor.setCol2(col2);
//            anchor.setDx2(pref.getDx2() *100 /113);
//            anchor.setRow2(row2);
//            anchor.setDy2(pref.getDy2() *100 /90);
            bytes = null;
            is = null;

        } catch (Exception e) {

            logger.error("can not find file:" + e);
            throw new ApplicationException(e.getMessage());
        }
    }

    private void fillImageToSheet(SXSSFWorkbook workbook, SXSSFSheet sheet, ExCell exCell) {
        Integer[] c = exCell.getCoordinate();
        String value = exCell.getValue();
        if (c.length < 8) {
            return;
        }

        ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();

        BufferedImage bufferImg = null;
        try {
            bufferImg = ImageIO.read(new File(value));
            ImageIO.write(bufferImg, "png", byteArrayOut);
            Drawing patriarch = sheet.createDrawingPatriarch();
            XSSFClientAnchor anchor = new XSSFClientAnchor(c[0], c[1], c[2], c[3], c[4].shortValue(), c[5], c[6].shortValue(), c[7]);
            if (c[0] != -1) {
                anchor.setAnchorType(ClientAnchor.AnchorType.DONT_MOVE_AND_RESIZE);
                patriarch.createPicture(anchor, workbook.addPicture(byteArrayOut.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG)).resize(1);
            } else {
                anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
                patriarch.createPicture(anchor, workbook.addPicture(byteArrayOut.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG)).resize();
            }
            bufferImg.flush();
            anchor = null;
        } catch (Exception e) {

            logger.error("can not find file:" + e);
            throw new ApplicationException(e.getMessage());
        }
        c = null;
        byteArrayOut = null;

    }

    private void fillTextToSheet(SXSSFSheet sheet, ExCell exCell) {
        Integer[] coordinate = exCell.getCoordinate();
        String value = exCell.getValue();
        CellStyle cellStyle = exCell.getStyle();
//        cellStyle.setWrapText(true);

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
//            if(OS_NAME.toLowerCase().startsWith(OS_WIN)) {
//                cellStyle.setWrapText(true);
//            }else{
//                cellStyle.setWrapText(false);
//            }
            cell.setCellStyle(cellStyle);
        }

        XSSFRichTextString text = new XSSFRichTextString(value);
        cell.setCellValue(text);
        coordinate = null;
        cellStyle = null;
        text = null;
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
            cell.setCellStyle(cellStyle);
        }

        if (hyperlinkCode != null) {
            CreationHelper createHelper = sheet.getWorkbook().getCreationHelper();
            Hyperlink link = createHelper.createHyperlink(HyperlinkType.DOCUMENT);
            hyperlinkCode = filterSpeChars(hyperlinkCode);
            link.setAddress("#'" + hyperlinkCode + "'!R1C1");
            cell.setHyperlink(link);
        }

        XSSFRichTextString text = new XSSFRichTextString(value);
        cell.setCellValue(text);
        coordinate = null;
        cellStyle = null;
        text = null;
    }
}
