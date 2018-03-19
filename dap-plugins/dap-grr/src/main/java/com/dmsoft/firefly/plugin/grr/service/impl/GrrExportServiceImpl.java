/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.grr.service.impl;

import com.dmsoft.firefly.plugin.grr.dto.*;
import com.dmsoft.firefly.plugin.grr.service.GrrExportService;
import com.dmsoft.firefly.plugin.grr.service.impl.export.GrrExcelBuilder;
import com.dmsoft.firefly.plugin.grr.service.impl.export.GrrExportWorker;
import com.dmsoft.firefly.plugin.grr.utils.FileUtils;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.dmsoft.firefly.plugin.grr.utils.ResourceMassages;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * Created by Garen.Pang on 2018/3/15.
 */
public class GrrExportServiceImpl implements GrrExportService {

    private Logger logger = LoggerFactory.getLogger(GrrExportServiceImpl.class);

    @Override
    public String exportGrrSummary(GrrExportConfigDto grrExportConfigDto, List<GrrSummaryDto> grrSummaryExportDtos,  List<Double> level) {
       /*
        1.Verify the validity of the parameters
        2.Create directory exportDir
        3.Build grr excel
         */
        logger.info("Export grr summary start");
        PropertyConfig propertyConfig = new PropertyConfig();
        propertyConfig.setSpcExportNumber(200);
        propertyConfig.setGrrExportNumber(200);
        propertyConfig.setDefaultExportPath("../export/");
        //Verify the validity of the parameters
        if (grrExportConfigDto == null || grrSummaryExportDtos == null || grrSummaryExportDtos.size() <= 0) {
//            throw new ApplicationException(GrrExceptionCode.ERR_12001, GrrFxmlAndLanguageUtils.getString(ResourceMassages.VIEW_DATA));
            throw new ApplicationException(GrrFxmlAndLanguageUtils.getString(ResourceMassages.EXCEPTION_GRR_PARAMETER_INVALID));
        }
        try {
            //Create directory exportDir
            String exportPath = grrExportConfigDto.getExportPath();
            if (StringUtils.isBlank(exportPath)) {
                exportPath = FileUtils.getAbsolutePath(propertyConfig.getDefaultExportPath());
            }
            DateTimeFormatter formatDir = DateTimeFormat.forPattern("yyyyMMddHHmmss");
            DateTime nowDir = new DateTime();
            String fixDir = "Grr_" + nowDir.toString(formatDir);
            File file = new File(exportPath);
            if (!file.exists()) {
                boolean mkDirSucc = file.mkdirs();
                if (!mkDirSucc) {
//                    throw new ApplicationException(GrrExceptionCode.ERR_15009, ResourceBundleUtils.getString(ExceptionMessages.EXCEPTION_GLOBAL_MKDIRS_FAILED));
                    throw new ApplicationException(GrrFxmlAndLanguageUtils.getString(ResourceMassages.EXCEPTION_GRR_PARAMETER_INVALID));
                }
            }
            String exportFilePath = exportPath + "/" + fixDir;
            //Build grr excel
            GrrExportWorker factory = new GrrExportWorker();
            factory.buildGrrSummary(grrExportConfigDto, grrSummaryExportDtos, level);
            GrrExcelBuilder grrExcelBuilder = new GrrExcelBuilder(null, null);
            FileUtils.createDir(exportFilePath);
            logger.info("Export grr only result to filepath:{}", exportFilePath);
            String filePath = exportFilePath + "/" + fixDir + ".xlsx";
            grrExcelBuilder.drawExcel(filePath, factory);
            factory.cleanExportWorker();
            return exportFilePath;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String exportGrrSummaryDetail(GrrExportConfigDto grrExportConfigDto, List<GrrSummaryDto> grrSummaryExportDtos, List<GrrExportResultDto> grrExportResultDtos, List<Double> level) {
        /*
        1.Verify the validity of the parameters
        2.Create directory exportDir
        3.Build grr excel
         */
        logger.info("Export grr summary and detail start");
        PropertyConfig propertyConfig = new PropertyConfig();
        propertyConfig.setSpcExportNumber(200);
        propertyConfig.setGrrExportNumber(200);
        if (grrSummaryExportDtos == null || grrSummaryExportDtos.size() <= 0 || grrExportResultDtos == null || grrExportResultDtos.size() <= 0) {
//            throw new ApplicationException(ExceptionMessages.ERR_15009, ResourceBundleUtils.getString(ExceptionMessages.EXCEPTION_GLOBAL_MKDIRS_FAILED));
            throw new ApplicationException(GrrFxmlAndLanguageUtils.getString(ResourceMassages.EXCEPTION_GRR_PARAMETER_INVALID));
        }
        try {
            //Create directory exportDir
            String exportPath = grrExportConfigDto.getExportPath();
            if (StringUtils.isBlank(exportPath)) {
                exportPath = FileUtils.getAbsolutePath(propertyConfig.getDefaultExportPath());
            }
            DateTimeFormatter formatDir = DateTimeFormat.forPattern("yyyyMMddHHmmss");
            DateTime nowDir = new DateTime();
            String fixDir = "Grr_" + nowDir.toString(formatDir);
            File file = new File(exportPath);
            if (!file.exists()) {
                boolean mkDirSucc = file.mkdirs();
                if (!mkDirSucc) {
//                    throw new ApplicationException(ExceptionMessages.ERR_15009, ResourceBundleUtils.getString(ExceptionMessages.EXCEPTION_GLOBAL_MKDIRS_FAILED));
                    throw new ApplicationException(GrrFxmlAndLanguageUtils.getString(ResourceMassages.EXCEPTION_GRR_PARAMETER_INVALID));
                }
            }
            String exportFilePath = exportPath + "/" + fixDir;
            FileUtils.createDir(exportFilePath);
            //Build grr excel
            int excelIndex = 1;
            int readPieceSize = propertyConfig.getGrrExportNumber();
            for (int startIndex = 0; startIndex < grrExportResultDtos.size(); startIndex += readPieceSize) {
                List<GrrExportResultDto> grrExportResultDtos1 = Lists.newArrayList();
                int endIndex = 0;
                if (startIndex + readPieceSize < grrExportResultDtos.size()) {
                    endIndex = startIndex + readPieceSize;
                } else {
                    endIndex = grrExportResultDtos.size();
                }
                for (int i = startIndex; i < endIndex; i++) {
                    grrExportResultDtos1.add(grrExportResultDtos.get(i));
                }
                String filePath = exportFilePath + "/" + fixDir + "_" + excelIndex + ".xlsx";
                GrrExportWorker factory = new GrrExportWorker();
                factory.buildSummaryAndDetail(grrExportConfigDto, grrSummaryExportDtos, grrExportResultDtos, level);
                GrrExcelBuilder grrExcelBuilder = new GrrExcelBuilder(null, null);
                grrExcelBuilder.drawExcel(filePath, factory);
                logger.info("Export grr result and data to filepath:{}", filePath);
                grrExportResultDtos1 = null;
                factory.cleanExportWorker();
                factory = null;
                grrExcelBuilder = null;
                excelIndex += 1;
            }

            for (GrrExportResultDto grrExportResultDto : grrExportResultDtos) {
                GrrImageDto grrImageDto = grrExportResultDto.getGrrImageDto();
                if (grrImageDto != null) {
                    String componentsImagePath = grrExportResultDto.getGrrImageDto().getGrrComponentsImagePath();
                    String aPlotImagePath = grrExportResultDto.getGrrImageDto().getGrrAPlotImagePath();
                    String rChartImagePath = grrExportResultDto.getGrrImageDto().getGrrRChartImagePath();
                    String xBarImagePath = grrExportResultDto.getGrrImageDto().getGrrXBarImagePath();
                    String rPlotAppImagePath = grrExportResultDto.getGrrImageDto().getGrrRPlotChartAppImagePath();
                    String rPlotPartImagePath = grrExportResultDto.getGrrImageDto().getGrrRPlotChartPartImagePath();
                    if (componentsImagePath != null) {
                        FileUtils.deleteFile(componentsImagePath);
                    }
                    if (aPlotImagePath != null) {
                        FileUtils.deleteFile(aPlotImagePath);
                    }
                    if (rChartImagePath != null) {
                        FileUtils.deleteFile(rChartImagePath);
                    }
                    if (xBarImagePath != null) {
                        FileUtils.deleteFile(xBarImagePath);
                    }
                    if (rPlotAppImagePath != null) {
                        FileUtils.deleteFile(rPlotAppImagePath);
                    }
                    if (rPlotPartImagePath != null) {
                        FileUtils.deleteFile(rPlotPartImagePath);
                    }
                }
            }

            logger.info("Finished excel to filepath" + exportFilePath + "\n");
            return exportFilePath;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
