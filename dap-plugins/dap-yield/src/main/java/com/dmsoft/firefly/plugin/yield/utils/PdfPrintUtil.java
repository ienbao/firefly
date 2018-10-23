package com.dmsoft.firefly.plugin.yield.utils;

import com.dmsoft.firefly.gui.components.utils.FileUtil;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.Orientation;
import org.apache.pdfbox.printing.PDFPageable;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.print.PrinterJob;
import java.io.File;
import java.util.List;

public class PdfPrintUtil {

    /**
     * Print pdf.
     *
     * @param folderPath the path of folder.
     */
    public static void printPdf(String folderPath) {
        PrinterJob job = null;
        try {
            List<File> files = FileUtil.getFileList(folderPath);
            if (files != null && !files.isEmpty()) {
                for (File file : files) {
                    String filePath = file.getPath();
                    if ("pdf".equals(filePath.substring(filePath.lastIndexOf(".") + 1))) {
                        PDDocument doc = PDDocument.load(new File(filePath));
                        job = PrinterJob.getPrinterJob();
                        job.setPageable(new PDFPageable(doc, Orientation.AUTO, false, 300));

                        PrintService defaultService = PdfPrintUtil.getPrintService();
                        if (defaultService == null) {
//                            throw new ApplicationException(ExceptionMessages.ERR_90002, ResourceBundleUtils.getString(ExceptionMessages.ERR_90002));
                        }
                        job.setPrintService(defaultService);
                        if (job.printDialog()) {
                            job.print();
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (job != null) {
                job.cancel();
            }
//            throw new ApplicationException(ExceptionMessages.ERR_90002, ResourceBundleUtils.getString(ExceptionMessages.ERR_90002));
        }
    }

    /**
     * Get print service.
     *
     * @return print service
     */
    public static PrintService getPrintService() {
        PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
        if (defaultService == null) {
            PrinterJob job = PrinterJob.getPrinterJob();
            defaultService = job.getPrintService();
        }
        if (defaultService == null) {
//            throw new ApplicationException(ExceptionMessages.ERR_90002, ResourceBundleUtils.getString(ExceptionMessages.ERR_90002));
        }

        return defaultService;
    }
}
