package com.dmsoft.firefly.plugin.spc.utils;

import com.aspose.cells.License;
import com.aspose.cells.Workbook;
import com.dmsoft.firefly.gui.components.utils.FileUtil;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.plugin.PluginContext;
import com.google.common.collect.Lists;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

/**
 * Created by julia on 17/6/23.
 */
public class ExcelToPdfUtil {
    private final Logger logger = LoggerFactory.getLogger(ExcelToPdfUtil.class);

    private static InputStream license;
    private static InputStream cells;

    public ExcelToPdfUtil() {

    }

    public boolean getLicense() throws Exception {
        boolean result = false;
        try {
            String path = "license.xml";
//            String path2 = ApplicationPathUtil.getCanonicalPath() + "license.xml";
//            String path1 = FileUtils.getAbsolutePath("../license.xml");
            FileInputStream fileInputStream = null;
            try {
                logger.debug("path1: " + path);
                logger.debug("path2: " + path);
                fileInputStream= new FileInputStream(new File(path));
            } catch (FileNotFoundException e) {
                logger.debug("path: " + path);
//                fileInputStream= new FileInputStream(new File(path1));
            }
            License aposeLic = new License();
            aposeLic.setLicense(fileInputStream);
            logger.debug("result: " + result);
            result = true;
        } catch (Exception var3) {
            logger.error("result: " + result);
            var3.printStackTrace();
            throw var3;
        }
        logger.debug("result: " + result);
        return result;
    }

    public boolean excelToPdf(String folderPath) throws Exception {
        boolean isSuccess = false;
        if (getLicense()) {
            try {
                logger.debug("folderPath: " + folderPath);
                List<File> files = FileUtil.getFileList(folderPath);
                List<String> pdfs = Lists.newArrayList();
                PDFMergerUtility mergePdf = new PDFMergerUtility();
                String destinationFileName = folderPath + File.separator + "merged.pdf";
                logger.debug("files: " + files.size());

                if (files != null && !files.isEmpty()) {
                    for (File file : files) {
                        String filePath = file.getPath();
                        if ("xlsx".equals(filePath.substring(filePath.lastIndexOf(".") + 1))) {
                            logger.debug("filePath: " + filePath);
                            cells = new FileInputStream(file);
                            String targetPath = filePath.substring(0, filePath.lastIndexOf(".")) + ".pdf";
                            Workbook wb = new Workbook(cells);
                            File targetFile = new File(targetPath);
                            FileOutputStream fileOS = new FileOutputStream(targetFile);
                            wb.save(fileOS, 13);
                            pdfs.add(targetPath);
                            file.delete();
                            file.exists();
                            fileOS.close();
                        }
                    }
                    for (int i = 0; i < pdfs.size(); i++) {
                        mergePdf.addSource(pdfs.get(i));
                    }
                    mergePdf.setDestinationFileName(destinationFileName);
                    mergePdf.mergeDocuments();
                }
                for (int i = 0; i < pdfs.size(); i++) {
                    FileUtils.deleteFile(pdfs.get(i));
                }
                isSuccess = true;
            } catch (Exception var8) {
                var8.printStackTrace();
            }
        }
        return isSuccess;
    }
}

