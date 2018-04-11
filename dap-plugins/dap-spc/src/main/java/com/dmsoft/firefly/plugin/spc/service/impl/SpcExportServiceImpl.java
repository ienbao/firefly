package com.dmsoft.firefly.plugin.spc.service.impl;

import com.dmsoft.firefly.plugin.spc.dto.ExportParamDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcExportConfigDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcStatisticalResultAlarmDto;
import com.dmsoft.firefly.plugin.spc.export.SpcExportBuilder;
import com.dmsoft.firefly.plugin.spc.export.SpcExportWorker;
import com.dmsoft.firefly.plugin.spc.utils.FileUtils;
import com.dmsoft.firefly.plugin.spc.utils.enums.SpcExportItemKey;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by GuangLi on 2018/3/7.
 */
public class SpcExportServiceImpl {
    private Logger logger = LoggerFactory.getLogger(SpcExportServiceImpl.class);

    /**
     * method to export spc result
     *
     * @param exportConfig export config dto
     * @param spcStatsDtos spc stats dto
     * @param chartImage   chart path
     * @param runChartRule run chart rules
     * @return export path
     */
    public String spcExport(SpcExportConfigDto exportConfig, List<SpcStatisticalResultAlarmDto> spcStatsDtos,
                            Map<String, Map<String, String>> chartImage, Map<String, String> runChartRule) {
        //TODO : add context and progress
        String[] basePath = new String[1];
        String savePath = FileUtils.getAbsolutePath("../export/");
        String exportPath = exportConfig.getExportPath();
//        String exportType = exportConfig.getExportType();
        Map<String, Boolean> exportDataItem = exportConfig.getExportDataItem();
        if (DAPStringUtils.isBlank(exportPath)) {
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
            if (exportDataItem.get(SpcExportItemKey.EXPORT_SUB_SUMMARY.getCode()) && exportDataItem.get(SpcExportItemKey.EXPORT_DETAIL_SHEET.getCode())) {
                exportCount++;
            } else if (exportDataItem.get(SpcExportItemKey.EXPORT_SUB_SUMMARY.getCode()) && !exportDataItem.get(SpcExportItemKey.EXPORT_DETAIL_SHEET.getCode()) && spcStatisticalResultDto.getCondition().equals(SpcExportItemKey.EXPORT_DETAIL_SHEET.getCode())) {
                exportCount++;
            } else if (!exportDataItem.get(SpcExportItemKey.EXPORT_SUB_SUMMARY.getCode()) && exportDataItem.get(SpcExportItemKey.EXPORT_DETAIL_SHEET.getCode()) && !spcStatisticalResultDto.getCondition().equals(SpcExportItemKey.EXPORT_SUB_SUMMARY.getCode())) {
                exportCount++;
            }
            if (exportCount == readPieceSize) {
                //export
                spcExportBuildDetail(exportParamDto, chartImage, spcStatisticDtoToExport, exportConfig, exportTimes, runChartRule);
                spcStatisticDtoToExport.clear();
                exportCount = 0;
                exportTimes++;
            }
        }

        if (spcStatisticDtoToExport.size() != 0) {
            spcExportBuildDetail(exportParamDto, chartImage, spcStatisticDtoToExport, exportConfig, exportTimes, runChartRule);
        }
        String savePicPath = FileUtils.getAbsolutePath("../export");
        FileUtils.deleteDir(savePicPath);
        return basePath[0];
    }

    private boolean spcExportBuildDetail(ExportParamDto exportParamDto, Map<String, Map<String, String>> chartImage, List<SpcStatisticalResultAlarmDto> spcStatisticalResultDtos,
                                         SpcExportConfigDto spcExportConfigDto, int exportTimes, Map<String, String> runChartRule) {
        SpcExportBuilder spcExportBuilder = new SpcExportBuilder();
        SpcExportWorker spcExportWorker = new SpcExportWorker();
        spcExportWorker.setCurWrittenItemNum(0);
        spcExportWorker.setExcelItemCapacity(exportParamDto.getExcelCapacity());
        String excelPath = exportParamDto.getDirSavePath() + "/" + exportParamDto.getDirName() + "_" + (exportTimes) + ".xlsx";
        spcExportWorker.initWorkbook();
        spcExportWorker.buildSPCMultiItem(chartImage, spcStatisticalResultDtos, spcExportConfigDto, runChartRule);
        spcExportBuilder.drawSpcExcel(excelPath, spcExportWorker);
        logger.info("Export complete.");
        spcExportBuilder.clear();
        spcExportWorker.cleanExportWorker();
        return true;
    }

    private String getTimeString() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(d);
    }
}
