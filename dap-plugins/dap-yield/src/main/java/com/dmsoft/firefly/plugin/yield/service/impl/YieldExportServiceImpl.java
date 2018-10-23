package com.dmsoft.firefly.plugin.yield.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by GuangLi on 2018/3/7.
 */
public class YieldExportServiceImpl {
    private Logger logger = LoggerFactory.getLogger(YieldExportServiceImpl.class);

//    /**
//     * method to export spc result
//     *
//     * @param exportConfig export config dto
//     * @param spcStatsDtos spc stats dto
//     * @param chartImage   chart path
//     * @param runChartRule run chart rules
//     * @return export path
//     */
//    public String spcExport(YieldExportConfigDto exportConfig, List<SpcStatisticalResultAlarmDto> spcStatsDtos,
//                            Map<String, Map<String, String>> chartImage, Map<String, String> runChartRule) {
//        //TODO : add context and progress
//        String[] basePath = new String[1];
//        String savePath = FileUtils.getAbsolutePath("../export/");
//        String exportPath = exportConfig.getExportPath();
////        String exportType = exportConfig.getExportType();
//        Map<String, Boolean> exportDataItem = exportConfig.getExportDataItem();
//        if (DAPStringUtils.isBlank(exportPath)) {
//            basePath[0] = savePath;
//        } else {
//            basePath[0] = exportPath;
//        }
//
//        String dirName = "SPC_Result_" + getTimeString();
//        String dirSavePath = basePath[0];
//        FileUtils.createDir(dirSavePath);
//        int readPieceSize = SpcExportProperty.EXPORT_EXCEL_SHEET_SIZE;
//        int excelCapacity = readPieceSize + 1;
//        ExportParamDto exportParamDto = new ExportParamDto(excelCapacity, dirSavePath, dirName, 6);
//        List<SpcStatisticalResultAlarmDto> spcStatisticDtoToExport = Lists.newArrayList();
//
//        int exportCount = 0;
//        int exportTimes = 1;
////        if ("global".equals(exportType)) {
//        for (SpcStatisticalResultAlarmDto spcStatisticalResultDto : spcStatsDtos) {
//            spcStatisticDtoToExport.add(spcStatisticalResultDto);
//            if (exportDataItem.get(SpcExportItemKey.EXPORT_SUB_SUMMARY.getCode()) && exportDataItem.get(SpcExportItemKey.EXPORT_DETAIL_SHEET.getCode())) {
//                exportCount++;
//            } else if (exportDataItem.get(SpcExportItemKey.EXPORT_SUB_SUMMARY.getCode()) && !exportDataItem.get(SpcExportItemKey.EXPORT_DETAIL_SHEET.getCode()) && spcStatisticalResultDto.getCondition().equals(SpcExportItemKey.EXPORT_DETAIL_SHEET.getCode())) {
//                exportCount++;
//            } else if (!exportDataItem.get(SpcExportItemKey.EXPORT_SUB_SUMMARY.getCode()) && exportDataItem.get(SpcExportItemKey.EXPORT_DETAIL_SHEET.getCode()) && !spcStatisticalResultDto.getCondition().equals(SpcExportItemKey.EXPORT_SUB_SUMMARY.getCode())) {
//                exportCount++;
//            }
//            if (exportCount == readPieceSize) {
//                //export
//                spcExportBuildDetail(exportParamDto, chartImage, spcStatisticDtoToExport, exportConfig, exportTimes, runChartRule);
//                spcStatisticDtoToExport.clear();
//                exportCount = 0;
//                exportTimes++;
//            }
//        }
//
//        if (spcStatisticDtoToExport.size() != 0) {
//            spcExportBuildDetail(exportParamDto, chartImage, spcStatisticDtoToExport, exportConfig, exportTimes, runChartRule);
//        }
//        String savePicPath = FileUtils.getAbsolutePath("../export");
//        FileUtils.deleteDir(savePicPath);
//        return basePath[0];
//    }
//
//    private boolean spcExportBuildDetail(ExportParamDto exportParamDto, Map<String, Map<String, String>> chartImage, List<SpcStatisticalResultAlarmDto> spcStatisticalResultDtos,
//                                         SpcExportConfigDto spcExportConfigDto, int exportTimes, Map<String, String> runChartRule) {
//        SpcExportBuilder spcExportBuilder = new SpcExportBuilder();
//        SpcExportWorker spcExportWorker = new SpcExportWorker();
//        spcExportWorker.setCurWrittenItemNum(0);
//        spcExportWorker.setExcelItemCapacity(exportParamDto.getExcelCapacity());
//        String excelPath = exportParamDto.getDirSavePath() + "/" + exportParamDto.getDirName() + "_" + (exportTimes) + ".xlsx";
//        spcExportWorker.initWorkbook();
//        spcExportWorker.buildSPCMultiItem(chartImage, spcStatisticalResultDtos, spcExportConfigDto, runChartRule);
//        spcExportBuilder.drawSpcExcel(excelPath, spcExportWorker);
//        logger.info("Export complete.");
//        spcExportBuilder.clear();
//        spcExportWorker.cleanExportWorker();
//        return true;
//    }
//
//    private String getTimeString() {
//        Date d = new Date();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
//        return sdf.format(d);
//    }
}
