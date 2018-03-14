package com.dmsoft.firefly.plugin.spc.service.impl;

import com.dmsoft.firefly.plugin.spc.dto.ExportParamDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcStatisticalResultAlarmDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcStatsDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcUserActionAttributesDto;
import com.dmsoft.firefly.plugin.spc.export.SpcExportBuilder;
import com.dmsoft.firefly.plugin.spc.export.SpcExportWorker;
import com.dmsoft.firefly.plugin.spc.utils.FileUtils;
import com.dmsoft.firefly.plugin.spc.utils.StringUtils;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by GuangLi on 2018/3/7.
 */
public class SpcExportServiceImpl {
    private Logger logger = LoggerFactory.getLogger(SpcExportServiceImpl.class);

    public String spcExport(SpcUserActionAttributesDto exportConfig, List<SpcStatisticalResultAlarmDto> spcStatsDtos, Map<String, Map<String, String>> chartImage) {
        String[] basePath = new String[1];
        String savePath = FileUtils.getAbsolutePath("../export/");
        String exportPath = exportConfig.getExportPath();
//        String exportType = exportConfig.getExportType();
        Map<String, Boolean> exportDataItem = exportConfig.getExportDataItem();
//        int digitNum = globalSettingService.findGlobalSetting("default").getDecimalDigit();
        if (StringUtils.isBlank(exportPath)) {
            basePath[0] = savePath;
        } else {
            basePath[0] = exportPath;
        }

        String dirName = "SPC_Result_" + getTimeString();
        String dirSavePath = basePath[0] + "/SPC_" + getTimeString();
        FileUtils.createDir(dirSavePath);
        int readPieceSize = 200;
        int excelCapacity = readPieceSize + 1;
        ExportParamDto exportParamDto = new ExportParamDto(excelCapacity, dirSavePath, dirName, 6);
        List<SpcStatisticalResultAlarmDto> spcStatisticDtoToExport = Lists.newArrayList();

        int exportCount = 0;
        int exportTimes = 1;
//        if ("global".equals(exportType)) {
        for (SpcStatisticalResultAlarmDto spcStatisticalResultDto : spcStatsDtos) {
            spcStatisticDtoToExport.add(spcStatisticalResultDto);
            if (exportDataItem.get("SubSummary") && exportDataItem.get("DetailSheet")) {
                exportCount++;
            } else if (exportDataItem.get("SubSummary") && !exportDataItem.get("DetailSheet") && spcStatisticalResultDto.getCondition().equals("SubSummary")) {
                exportCount++;
            } else if (!exportDataItem.get("SubSummary") && exportDataItem.get("DetailSheet") && !spcStatisticalResultDto.getCondition().equals("SubSummary")) {
                exportCount++;
            }
            if (exportCount == readPieceSize) {
                //export
                spcExportBuildDetail(exportParamDto, chartImage, spcStatisticDtoToExport, exportConfig, exportTimes);
                spcStatisticDtoToExport.clear();
                exportCount = 0;
                exportTimes++;
            }
        }
//        }
//        if (exportType == "current") {
//            spcStatisticDtoToExport.addAll(spcStatsDtos);
//        }
        if (spcStatisticDtoToExport.size() != 0) {
            spcExportBuildDetail(exportParamDto, chartImage, spcStatisticDtoToExport, exportConfig, exportTimes);
        }
        return dirSavePath;
    }

    private boolean spcExportBuildDetail(ExportParamDto exportParamDto, Map<String, Map<String, String>> chartImage, List<SpcStatisticalResultAlarmDto> spcStatisticalResultDtos,
                                         SpcUserActionAttributesDto spcUserActionAttributesDto, int exportTimes) {
        SpcExportBuilder spcExportBuilder = new SpcExportBuilder();
        SpcExportWorker spcExportWorker = new SpcExportWorker();
        spcExportWorker.setCurWrittenItemNum(0);
        spcExportWorker.setExcelItemCapacity(exportParamDto.getExcelCapacity());
        String excelPath = exportParamDto.getDirSavePath() + "/" + exportParamDto.getDirName() + "_" + (exportTimes) + ".xlsx";
        spcExportWorker.initWorkbook();
        spcExportWorker.setDigNum(exportParamDto.getDigitNum());
        spcExportWorker.buildSPCMultiItem(chartImage, spcStatisticalResultDtos, spcUserActionAttributesDto);
        spcExportBuilder.drawSpcExcel(excelPath, spcExportWorker);
        logger.info("Export complete.");
        String savePicPath = FileUtils.getAbsolutePath("../export/temp");
//        File file = new File(savePicPath);
        FileUtils.deleteDir(savePicPath);
        spcExportBuilder.clear();
        spcExportWorker.cleanExportWorker();
        return true;
    }

    private String getTimeString() {
//        DateTimeFormatter format = DateTimeFormat.forPattern("yyyyMMddHHmmss");
//        DateTime now = new DateTime();
//        return now.toString(format);
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(d);
    }

}
