/*
 * Copyright (c) 2015. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.spc.export;

import com.dmsoft.firefly.plugin.spc.dto.*;
import com.dmsoft.firefly.plugin.spc.poi.*;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import com.dmsoft.firefly.plugin.spc.utils.enums.SpcKey;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by cherry on 2016/4/19.
 */
public class SpcExportWorker implements ExWorker {
    //cell's start coordinate in sheet
    private final Integer[] headIndex = new Integer[]{0, 0};
    private final Integer[] dataIndex = new Integer[]{9, 0};
    //position and size of chart image
    private final int imageStartCol = dataIndex[1] + 3, imageEndCol = dataIndex[1] + 8;
    private SXSSFWorkbook workbook = null;
    private static final int IMAGE_COL_WIDTH = 5500, CACHE_ROW = 1000;
    private static final int DX2 = 1023, // the x coordinate within the first cell.
            DY2 = 255; // the y coordinate within the first cell.
    private Map<String, CellStyle> cpColorMap = Maps.newHashMap();
    //index of current row to write
    private Integer currentRow = 0;
    //sheets to export
    private List<ExSheet> sheets = Lists.newArrayList();
    //default digit number
    private int digNum = 6;
    private int imgSheetIndex = 0, curWrittenItemNum = 0;
    //number of sheets in excel
    private int excelItemCapacity;

    private int count = 0;
    private boolean sameItem = true;
    private List<Integer> breakRowLists = Lists.newArrayList();
    private int runChartRow = 0;
    private int perRow = 0;

    /**
     * build SPC export excel data.
     *
     * @param spcStatisticalResultDtos   spc static result data
     * @param chartPicPaths              chartPicPaths
     * @param spcUserActionAttributesDto spc user action and config dto
     */
    public void buildSPCMultiItem(Map<String, Map<String, String>> chartPicPaths, List<SpcStatisticalResultAlarmDto> spcStatisticalResultDtos, SpcUserActionAttributesDto spcUserActionAttributesDto) {
        Map<String, String> rRules = Maps.newHashMap();
        rRules.put("analysisKey0", "R1,R2");
        String perfomer = spcUserActionAttributesDto.getPerformer();
        Map<String, Boolean> exportDataItem = spcUserActionAttributesDto.getExportDataItem();

        //summary sheet
        ExSheet summarySheet = new ExSheet();
        ExSheet allSummarySheet = new ExSheet();
        summarySheet.setName("Summary");
        allSummarySheet.setName("All_Summary_Chart");
        summarySheet.setIndex(1);
        allSummarySheet.setIndex(2);
        List<ExCell> summaryCellList = Lists.newArrayList();
        List<ExCell> ndcCellList = Lists.newArrayList();
        if ((curWrittenItemNum) % excelItemCapacity == 0) {
            currentRow = 0;
        }
        //build summary sheet
        int itemCounter = 0;

        itemCounter++;
        for (SpcStatisticalResultAlarmDto spcStatisticalResultDto : spcStatisticalResultDtos) {
            String itemName = spcStatisticalResultDto.getItemName();
            String condition = spcStatisticalResultDto.getCondition();
            if ("SubSummary".equals(condition) && !exportDataItem.get("SubSummary")) {
                continue;
            }
            if (!exportDataItem.get("DetailSheet")) {
                ndcCellList.addAll(buildMultiNDC(spcStatisticalResultDto, itemCounter, exportDataItem, perfomer, false));
                currentRow += 1;
            } else {
                ndcCellList.addAll(buildMultiNDC(spcStatisticalResultDto, itemCounter, exportDataItem, perfomer, true));
                currentRow += 1;
                itemCounter++;
            }
        }

        ExSheet exChartSheet1 = new ExSheet();
        exChartSheet1.setName("");
        exChartSheet1.setIndex(2);
        summaryCellList.addAll(ndcCellList);
        summarySheet.setExCells(summaryCellList);
        exChartSheet1.setExCells(summaryCellList);
        sheets.add(summarySheet);
        if (!exportDataItem.get("DetailSheet") && !exportDataItem.get("SubSummary")) {
            sheets.add(exChartSheet1);
            return;
        }
        summaryCellList = Lists.newArrayList();
        ndcCellList = null;

        //build sub sheets
        currentRow = 3;
        count = 0;

        int sheetIndex = curWrittenItemNum;
        for (SpcStatisticalResultAlarmDto spcStatisticalResultDto : spcStatisticalResultDtos) {
            String key = spcStatisticalResultDto.getKey();
            String testItemName = spcStatisticalResultDto.getItemName();
            String condition = spcStatisticalResultDto.getCondition();
            if (DAPStringUtils.isBlank(condition)) {
                condition = "All";
            }
            if ("SubSummary".equals(condition) && !exportDataItem.get("SubSummary")
                    || (!"SubSummary".equals(condition) && !exportDataItem.get("DetailSheet"))) {
                continue;
            }

            boolean firstChart = false;
            ExSheet exSheet = new ExSheet();
            List<ExCell> cellList = Lists.newArrayList();

            Map<String, String> headerMap = new HashMap<>();
            testItemName = DAPStringUtils.filterSpeChars(testItemName);
            headerMap.put("name", testItemName);
            headerMap.put("searchCondition", condition);

            String sheetName = "";
            if ("SubSummary".equals(condition)) {
                sheetName = "SubSummary" + "_" + testItemName;
                sheetName = DAPStringUtils.filterSpeChars(sheetName);
            } else {
                sheetName = "Detail" + "_" + testItemName + "_" + condition;
                sheetName = DAPStringUtils.filterSpeChars(sheetName);
            }
            exSheet.setName(sheetName);

            List<ExCell> header = buildItemHeader(headerMap);
            List<ExCell> rcCellList;
            List<ExCell> rcCellListAll = Lists.newArrayList();
            cellList.addAll(header);
            boolean chartFlag = false;
            Boolean rowAdd = false;
            count = 0;
            currentRow = 3;

            if (chartPicPaths.get(key) != null) {
                Map<String, String> chartPicPath = chartPicPaths.get(key);
                if (!"SubSummary".equals(condition)) {
                    ndcCellList = buildTestData(spcStatisticalResultDto.getStatisticalAlarmDtoMap(), exportDataItem);
                    cellList.addAll(ndcCellList);
                }
                ndcCellList = null;

                if (chartPicPath.containsKey(UIConstant.SPC_CHART_NDC)) {
                    ndcCellList = buildNDChart(chartPicPath.get(UIConstant.SPC_CHART_NDC));
                    cellList.addAll(ndcCellList);
                    count++;
                    currentRow -= 20;
                    firstChart = true;
                }
                ndcCellList = null;

                if (chartPicPath.containsKey(UIConstant.SPC_CHART_RUN)) {
                    if (!firstChart) {
                        currentRow += 2;
                        firstChart = true;
                    }
                    if (!"SubSummary".equals(condition)) {
                        rcCellList = buildRChart(rRules.get(key), chartPicPath.get(UIConstant.SPC_CHART_RUN), "Run Chart");
                        chartFlag = true;
                        rcCellListAll.addAll(rcCellList);
                        count++;
                    } else {
                        rcCellList = buildRChart(null, chartPicPath.get(UIConstant.SPC_CHART_RUN), "Run Chart");
                        chartFlag = true;
                        rcCellListAll.addAll(rcCellList);
                        count++;
                    }
                    rowAdd = true;
                }

                if (chartPicPath.containsKey(UIConstant.SPC_CHART_XBAR)) {
                    if (!firstChart) {
                        currentRow += 2;
                        firstChart = true;
                    }
                    rcCellList = buildRChart(null, chartPicPath.get(UIConstant.SPC_CHART_XBAR), "X-Bar Chart");
                    chartFlag = true;
                    rcCellListAll.addAll(rcCellList);
                    count++;
                    rowAdd = true;
                }
                if (chartPicPath.containsKey(UIConstant.SPC_CHART_RANGE)) {
                    if (!firstChart) {
                        currentRow += 2;
                        firstChart = true;
                    }
                    rcCellList = buildRChart(null, chartPicPath.get(UIConstant.SPC_CHART_RANGE), "Range Chart");
                    chartFlag = true;
                    rcCellListAll.addAll(rcCellList);
                    count++;
                    rowAdd = true;
                }

                if (chartPicPath.containsKey(UIConstant.SPC_CHART_SD)) {
                    if (!firstChart) {
                        currentRow += 2;
                        firstChart = true;
                    }
                    rcCellList = buildRChart(null, chartPicPath.get(UIConstant.SPC_CHART_SD), "SD Chart");
                    chartFlag = true;
                    rcCellListAll.addAll(rcCellList);
                    count++;
                    rowAdd = true;
                }

                if (rowAdd) {
                    currentRow += 20;
                }

                if (chartPicPath.containsKey(UIConstant.SPC_CHART_MED)) {
                    if (!firstChart) {
                        currentRow += 2;
                        firstChart = true;
                    }
                    currentRow -= 4;
                    rcCellList = buildSCTChart(chartPicPath.get(UIConstant.SPC_CHART_MED), "Median Chart");
                    chartFlag = true;
                    cellList.addAll(rcCellList);
                }

                if (chartPicPath.containsKey(UIConstant.SPC_CHART_BOX)) {
                    if (!firstChart) {
                        currentRow += 2;
                        firstChart = true;
                    }
                    currentRow -= 4;
                    rcCellList = buildSCTChart(chartPicPath.get(UIConstant.SPC_CHART_BOX), "Box Chart");
                    cellList.addAll(rcCellList);
                } else {
                    currentRow += 1;
                }

                count = 0;
                if (chartPicPath.containsKey(UIConstant.SPC_CHART_MR)) {
                    if (!firstChart) {
                        currentRow += 2;
                        firstChart = true;
                    }
                    rcCellList = buildRChart(null, chartPicPath.get(UIConstant.SPC_CHART_MR), "MR Chart");
                    chartFlag = true;
                    rcCellListAll.addAll(rcCellList);
                    count++;
                }
                rcCellList = null;

                if (chartFlag) {
                    cellList.addAll(rcCellListAll);
                }
                rcCellListAll = null;
                exSheet.setIndex(sheetIndex + 1);
                exSheet.setExCells(cellList);
                if (40 < currentRow) {
                    if (!breakRowLists.contains(43)) {
                        breakRowLists.add(43);
                    }
                }

                if (100 < currentRow) {
                    if (!breakRowLists.contains(103)) {
                        breakRowLists.add(103);
                    }
                }

                if (160 < currentRow) {
                    if (!breakRowLists.contains(163)) {
                        breakRowLists.add(163);
                    }
                }
                sheets.add(exSheet);
                sheetIndex++;
            }
        }
    }


    private List<ExCell> buildItemHeader(Map<String, String> headerMap) {
        Map<String, CellStyle> cellStyleMap = CellStyleUtil.getStyle(getCurrentWorkbook());
        List<ExCell> exCellList = new ArrayList<>();
        exCellList.add(ExUtil.fillToCell(new Integer[]{headIndex[0], headIndex[1]}, "Summary", ExCellType.HYPERLINK.withCode(ExSheet.formatName(0, "Summary")), cellStyleMap.get(CellStyleType.link.toString())));
        if (headerMap != null) {
            String conditionValue = headerMap.get("searchCondition");
            if (conditionValue.startsWith(" & ")) {
                conditionValue = conditionValue.substring(3, conditionValue.length());
            }
            if (headerMap.get("name").equals("SummaryChat")) {
                conditionValue = "Summary";
            }
            exCellList.add(ExUtil.fillToCell(new Integer[]{headIndex[0] + 1, headIndex[1]}, "Test Item", ExCellType.TEXT, cellStyleMap.get(CellStyleType.head_lightBlue.toString())));
            exCellList.add(ExUtil.fillToCell(new Integer[]{headIndex[0] + 1, headIndex[1] + 1}, headerMap.get("name"), ExCellType.TEXT, cellStyleMap.get(CellStyleType.items_content.toString())));
            exCellList.add(ExUtil.fillToCell(new Integer[]{headIndex[0] + 1, headIndex[1] + 2}, "", ExCellType.TEXT, cellStyleMap.get(CellStyleType.items_content.toString())));
            exCellList.add(ExUtil.fillToCell(new Integer[]{headIndex[0] + 1, headIndex[1] + 3}, "", ExCellType.TEXT, cellStyleMap.get(CellStyleType.items_content.toString())));
            exCellList.add(ExUtil.fillToCell(new Integer[]{headIndex[0] + 1, headIndex[1] + 4}, "", ExCellType.TEXT, cellStyleMap.get(CellStyleType.items_content.toString())));
            exCellList.add(ExUtil.fillToCell(new Integer[]{headIndex[0] + 2, headIndex[1]}, "Search Condition", ExCellType.TEXT, cellStyleMap.get(CellStyleType.head_lightBlue.toString())));
            exCellList.add(ExUtil.fillToCell(new Integer[]{headIndex[0] + 2, headIndex[1] + 1}, conditionValue, ExCellType.TEXT, cellStyleMap.get(CellStyleType.items_content.toString())));
            exCellList.add(ExUtil.fillToCell(new Integer[]{headIndex[0] + 2, headIndex[1] + 2}, "", ExCellType.TEXT, cellStyleMap.get(CellStyleType.items_content.toString())));
            exCellList.add(ExUtil.fillToCell(new Integer[]{headIndex[0] + 2, headIndex[1] + 3}, "", ExCellType.TEXT, cellStyleMap.get(CellStyleType.items_content.toString())));
            exCellList.add(ExUtil.fillToCell(new Integer[]{headIndex[0] + 2, headIndex[1] + 4}, "", ExCellType.TEXT, cellStyleMap.get(CellStyleType.items_content.toString())));
        }
        return exCellList;
    }

    private List<ExCell> buildMultiNDC(SpcStatisticalResultAlarmDto statisticalResultAlarmDto, int itemCounter, Map<String, Boolean> exportDataItem, String performer, boolean isHyperlink) {
        String itemName = statisticalResultAlarmDto.getItemName();
        String condition = statisticalResultAlarmDto.getCondition();
        Map<String, StatisticalAlarmDto> dto = statisticalResultAlarmDto.getStatisticalAlarmDtoMap();

        List<ExCell> exCellList = Lists.newArrayList();
        Map<String, CellStyle> cellStyleMap = CellStyleUtil.getStyle(getCurrentWorkbook());
        CellStyle cellStyle = cellStyleMap.get(CellStyleType.head_lightBlue.toString());
        String[] scdLabels = UIConstant.EXPORT_NDC_SECOND_LABELS;
        if (currentRow == 0) {
            exCellList.add(ExUtil.fillToCell(new Integer[]{0, currentRow}, "", ExCellType.TEXT, cellStyle));
            exCellList.add(ExUtil.fillToCell(new Integer[]{0, 0}, "Date:", ExCellType.TEXT, cellStyle));
            exCellList.add(ExUtil.fillToCell(new Integer[]{0, 1}, new SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().getTime()), ExCellType.TEXT, cellStyleMap.get(CellStyleType.items_content.toString())));
            exCellList.add(ExUtil.fillToCell(new Integer[]{0, 2}, "", ExCellType.TEXT, cellStyleMap.get(CellStyleType.items_content.toString())));
            exCellList.add(ExUtil.fillToCell(new Integer[]{1, 0}, "Author:", ExCellType.TEXT, cellStyle));
            exCellList.add(ExUtil.fillToCell(new Integer[]{1, 1}, performer, ExCellType.TEXT, cellStyleMap.get(CellStyleType.items_content.toString())));
            exCellList.add(ExUtil.fillToCell(new Integer[]{1, 2}, "", ExCellType.TEXT, cellStyleMap.get(CellStyleType.items_content.toString())));
        }


        int startRow = 3;
        int columnCountPerRow = 4;
        startRow = startRow + (currentRow / columnCountPerRow) * (24 + 2);
        if (currentRow % columnCountPerRow == 0) {
            int tempLength = scdLabels.length;
            int m = 0;
            for (int i = 0; i < tempLength; i++) {
                if (i == 0 && "Basic".equals(condition)) {
                    exCellList.add(ExUtil.fillToCell(new Integer[]{startRow + m, 0}, "Item Name", ExCellType.TEXT, cellStyle));
                    m++;
                } else {
                    if (exportDataItem != null && exportDataItem.keySet().contains(scdLabels[i])) {
                        if (exportDataItem.get(scdLabels[i])) {
                            exCellList.add(ExUtil.fillToCell(new Integer[]{startRow + m, 0}, scdLabels[i], ExCellType.TEXT, cellStyle));
                            m++;
                        }
                    } else {
                        if (i == 0 || i == 1 || (exportDataItem != null && exportDataItem.keySet().contains(scdLabels[i]))) {
                            exCellList.add(ExUtil.fillToCell(new Integer[]{startRow + m, 0}, scdLabels[i], ExCellType.TEXT, cellStyle));
                            m++;
                        }
                    }
                }
            }
        }

        int n = startRow;
        int column = 1 + currentRow % columnCountPerRow;

        itemName = DAPStringUtils.filterSpeChars(itemName);
        String ref = itemName;
        String link = null;

        if (DAPStringUtils.isBlank(condition)) {
            condition = "All";
        }
        if (!"All Summary".equals(condition)) {
            if ("SubSummary".equals(condition)) {
                link = "SubSummary" + "_" + itemName;
                link = DAPStringUtils.filterSpeChars(link);
            } else {
                link = "Detail" + "_" + itemName + "_" + condition;
                link = DAPStringUtils.filterSpeChars(link);
            }
        }

        CellStyle textCellStyle = cellStyleMap.get(CellStyleType.summary_content_testItems_1.toString());

        ExCell hyperCell;
        if (isHyperlink) {
            if (!ref.equals("All_Summary_Chart")) {
                if (link == null) {
                    hyperCell = ExUtil.fillToCell(new Integer[]{n++, column}, ref, ExCellType.HYPERLINK.withCode(ExSheet.formatName(itemCounter, ref)),
                            cellStyleMap.get(CellStyleType.summary_content_testItems.toString()));
                } else {
                    hyperCell = ExUtil.fillToCell(new Integer[]{n++, column}, ref, ExCellType.HYPERLINK.withCode(ExSheet.formatName(itemCounter, link)),
                            cellStyleMap.get(CellStyleType.summary_content_testItems.toString()));
                }
            } else {
                hyperCell = ExUtil.fillToCell(new Integer[]{n++, column}, ref, ExCellType.TEXT,
                        textCellStyle);
            }
        } else {
            hyperCell = ExUtil.fillToCell(new Integer[]{n++, column}, ref, ExCellType.TEXT,
                    textCellStyle);
        }
        exCellList.add(hyperCell);


        exCellList.add(ExUtil.fillToCell(new Integer[]{n++, column}, condition, ExCellType.TEXT, textCellStyle));
        for (int i = 0; i < UIConstant.SPC_EXPORT_RESULT.length; i++) {
            String name = UIConstant.SPC_EXPORT_RESULT[i];
            if (exportDataItem.containsKey(name) && exportDataItem.get(name)) {
                exCellList.add(ExUtil.fillToCell(new Integer[]{n++, column}, (checkStaticData(dto, name) ? "-" : formatDouble(Double.valueOf(dto.get(name).getValue()), 0) + ""), ExCellType.TEXT,
                        (checkStaticData(dto, name) || cusCpwToLevel(dto, name).equals(SpcKey.NORMAL.getCode())) ? textCellStyle : fillPcColor(cusCpwToLevel(dto, name))));
            }
        }

        return exCellList;
    }

    private List<ExCell> buildTestData(Map<String, StatisticalAlarmDto> dto, Map<String, Boolean> exportDataItem) {
        String[] labels = UIConstant.EXPORT_SPC_NDC_LABELS;
        String[] descriptiveLabels = UIConstant.EXPORT_SPC_DESCRIPTIVE;
        String[] performanceLabels = UIConstant.EXPORT_SPC_PERFORMANCE;
        List<ExCell> exCellList = new ArrayList<>();
        Map<String, CellStyle> cellStyleMap = CellStyleUtil.getStyle(getCurrentWorkbook());
        CellStyle cellStyle = cellStyleMap.get(CellStyleType.head_lightBlue.toString());
        CellStyle textStyle = cellStyleMap.get(CellStyleType.items_content.toString());

        if (dto != null) {
            currentRow = currentRow + 2;
            int m = 1;
            int tempDigit = (digNum - 2) >= 0 ? (digNum - 2) : 0;
            exCellList.add(ExUtil.fillToCell(new Integer[]{currentRow + 1, dataIndex[1]}, "USL", ExCellType.TEXT, cellStyle));
            exCellList.add(ExUtil.fillToCell(new Integer[]{currentRow + 1, dataIndex[1] + 1}, (checkStaticData(dto, "USL") ? "-" : dto.get("USL").getValue().toString()),
                    ExCellType.TEXT, textStyle));
            exCellList.add(ExUtil.fillToCell(new Integer[]{currentRow + 2, dataIndex[1]}, "LSL", ExCellType.TEXT, cellStyle));
            exCellList.add(ExUtil.fillToCell(new Integer[]{currentRow + 2, dataIndex[1] + 1}, (checkStaticData(dto, "LSL") ? "-" : dto.get("LSL").getValue().toString()),
                    ExCellType.TEXT, textStyle));

            currentRow++;
            exCellList.add(ExUtil.fillToCell(new Integer[]{currentRow, dataIndex[1] + 3}, "Process Cability Index", ExCellType.TEXT, cellStyle));
            exCellList.add(ExUtil.fillToCell(new Integer[]{currentRow, dataIndex[1] + 4}, "", ExCellType.TEXT, cellStyle));
            int lablesLength = labels.length;
            int fillCount1 = 0;
            for (int i = 0; i < lablesLength; i++) {
                if (exportDataItem != null && exportDataItem.keySet().contains(labels[i])) {
                    if (exportDataItem.get(labels[i])) {
                        exCellList.add(ExUtil.fillToCell(new Integer[]{currentRow + 1 + fillCount1, dataIndex[1] + 3}, labels[i], ExCellType.TEXT, cellStyle));
                        fillCount1++;
                    }
                }
            }
            int performanceLength = performanceLabels.length;
            int fillCount2 = 0;
            for (int j = 0; j < performanceLength; j++) {
                if (exportDataItem != null && exportDataItem.keySet().contains(performanceLabels[j])) {
                    if (exportDataItem.get(performanceLabels[j])) {
                        exCellList.add(ExUtil.fillToCell(new Integer[]{currentRow + 2 + fillCount1 + fillCount2, dataIndex[1] + 3}, performanceLabels[j], ExCellType.TEXT, cellStyle));
                        if (j == 0) {
                            exCellList.add(ExUtil.fillToCell(new Integer[]{currentRow + 2 + fillCount1 + fillCount2, dataIndex[1] + 4}, "", ExCellType.TEXT, cellStyle));
                            perRow = currentRow + 2 + fillCount1 + fillCount2;
                        }
                        fillCount2++;
                    }
                } else {
                    if (j == 0) {
                        exCellList.add(ExUtil.fillToCell(new Integer[]{currentRow + 2 + fillCount1 + fillCount2, dataIndex[1] + 3}, performanceLabels[j], ExCellType.TEXT, cellStyle));
                        exCellList.add(ExUtil.fillToCell(new Integer[]{currentRow + 2 + fillCount1 + fillCount2, dataIndex[1] + 4}, "", ExCellType.TEXT, cellStyle));
                        perRow = currentRow + 2 + fillCount1 + fillCount2;
                        fillCount2++;
                    }
                }
            }

            int n = 1;
            for (int i = 0; i < UIConstant.SPC_EXPORT_A.length; i++) {
                String name = UIConstant.SPC_EXPORT_A[i];
                String s = "";
                if (name.equals("CA")) {
                    s = "%";
                }
                if (exportDataItem.keySet().contains(name) && exportDataItem.get(name)) {
                    exCellList.add(ExUtil.fillToCell(new Integer[]{currentRow + n++, dataIndex[1] + 4}, (checkStaticData(dto, name) ? "-" : formatDouble(Double.valueOf(dto.get(name).getValue()), tempDigit) + s),
                            ExCellType.TEXT, (checkStaticData(dto, name) || cusCpwToLevel(dto, name).equals(SpcKey.NORMAL.getCode())) ? textStyle : fillPcColor(cusCpwToLevel(dto, name))));
                }
            }

            int descriptiveLablesLength = descriptiveLabels.length;
            int fillCount3 = 0;
            for (int i = 0; i < descriptiveLablesLength; i++) {
                if (exportDataItem != null && exportDataItem.keySet().contains(descriptiveLabels[i])) {
                    if (exportDataItem.get(descriptiveLabels[i])) {
                        exCellList.add(ExUtil.fillToCell(new Integer[]{currentRow + 4 + fillCount3, dataIndex[1]}, descriptiveLabels[i], ExCellType.TEXT, cellStyle));
                        if (i == 0) {
                            exCellList.add(ExUtil.fillToCell(new Integer[]{currentRow + 4 + fillCount3, dataIndex[1] + 1}, "", ExCellType.TEXT, cellStyle));
                        }
                        fillCount3++;
                    }
                } else {
                    if (i == 0) {
                        exCellList.add(ExUtil.fillToCell(new Integer[]{currentRow + 4 + fillCount3, dataIndex[1]}, descriptiveLabels[i], ExCellType.TEXT, cellStyle));
                        exCellList.add(ExUtil.fillToCell(new Integer[]{currentRow + 4 + fillCount3, dataIndex[1] + 1}, "", ExCellType.TEXT, cellStyle));
                        fillCount3++;
                    }
                }
            }

            int p = 1;
            for (int i = 0; i < UIConstant.SPC_EXPORT_B.length; i++) {
                String name = UIConstant.SPC_EXPORT_B[i];
                if (exportDataItem.keySet().contains(name) && exportDataItem.get(name)) {
                    exCellList.add(ExUtil.fillToCell(new Integer[]{currentRow + 4 + p++, dataIndex[1] + 1}, (checkStaticData(dto, name) ? "-" : formatDouble(Double.valueOf(dto.get(name).getValue()), 0) +  ""),
                            ExCellType.TEXT, textStyle));
                }
            }
            currentRow = currentRow + 6 + descriptiveLablesLength;
        }
        return exCellList;
    }

    private List<ExCell> buildNDChart(String filePath) {
        List<ExCell> exCellList = new ArrayList<>();

        exCellList.add(ExUtil.fillToCell(new Integer[]{currentRow + 1, dataIndex[1]}, "ND Chart", ExCellType.TEXT, fillTitleStyle()));
        exCellList.add(ExUtil.fillToCell(new Integer[]{0, 0, DX2, DY2, imageStartCol - 3, currentRow + 2, imageEndCol - 3, currentRow + 20}, filePath, ExCellType.IMAGE));

        currentRow = currentRow + 22;
        return exCellList;
    }

    private List<ExCell> buildSCTChart(String filePath, String chartName) {
        currentRow = currentRow + 2;
        int startCol = 1;
        int endCol = 6;
        if (count == 0) {
            startCol = dataIndex[1] + 1;
            endCol = dataIndex[1] + 6;
        } else if (count == 1) {
            currentRow -= 1;
            startCol = dataIndex[1] + 1;
            endCol = dataIndex[1] + 6;
        }
        List<ExCell> exCellList = new ArrayList<>();
        exCellList.add(ExUtil.fillToCell(new Integer[]{currentRow + 1, endCol - 6}, chartName, ExCellType.TEXT, fillTitleStyle()));
        exCellList.add(ExUtil.fillToCell(new Integer[]{0, 0, DX2, DY2, startCol - 1, currentRow + 2, endCol - 1, currentRow + 20}, filePath, ExCellType.IMAGE));
        currentRow = currentRow + 22;
        return exCellList;
    }

    private List<ExCell> buildRChart(String rRule, String filePath, String rCharName) {
        int startCol = 1;
        int endCol = 6;
        if (count == 0) {
            startCol = dataIndex[1] + 1;
            endCol = dataIndex[1] + 6;
        } else if (count == 1) {
            currentRow += 20;
            startCol = dataIndex[1] + 1;
            endCol = dataIndex[1] + 6;
        } else if (count == 2) {
            currentRow += 20;
            startCol = dataIndex[1] + 1;
            endCol = dataIndex[1] + 6;
        } else if (count == 3) {
            currentRow += 20;
            startCol = dataIndex[1] + 1;
            endCol = dataIndex[1] + 6;
        } else if (count == 4) {
            currentRow += 20;
            startCol = dataIndex[1] + 1;
            endCol = dataIndex[1] + 6;
        }
        List<ExCell> exCellList = new ArrayList<>();
        exCellList.add(ExUtil.fillToCell(new Integer[]{currentRow - 1, startCol - 1}, rCharName, ExCellType.TEXT, fillTitleStyle()));

        if (rCharName.equals("Run Chart")) {
            if (DAPStringUtils.isNotBlank(rRule)) {
                runChartRow = currentRow - 1;
                exCellList.add(ExUtil.fillToCell(new Integer[]{currentRow - 1, startCol}, "Out of Control Conditions:", ExCellType.TEXT, fillTitleStyle()));
                exCellList.add(ExUtil.fillToCell(new Integer[]{currentRow - 1, startCol + 1}, "", ExCellType.TEXT, fillTitleStyle()));
                exCellList.add(ExUtil.fillToCell(new Integer[]{currentRow - 1, startCol + 2}, rRule, ExCellType.TEXT, fillRcColor(true)));
                exCellList.add(ExUtil.fillToCell(new Integer[]{currentRow - 1, startCol + 3}, "", ExCellType.TEXT, fillRcColor(true)));

            }
        }
        exCellList.add(ExUtil.fillToCell(new Integer[]{0, 0, DX2, DY2, startCol - 1, currentRow, endCol - 1, currentRow + 18}, filePath, ExCellType.IMAGE));
        return exCellList;
    }

    private CellStyle fillPcColor(String level) {
        CellStyle cellStyle = getCpColorMap().get(level);
        addBorder(cellStyle);
        return getCpColorMap().get(level);
    }

    private CellStyle fillRcColor(boolean isOverRule) {
        CellStyle style = this.getCurrentWorkbook().createCellStyle();
        Font font = this.getCurrentWorkbook().createFont();
        font.setColor(IndexedColors.BLACK.index);
        if (isOverRule) {
            font.setColor(IndexedColors.RED.index);
        }
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setFont(font);
        return style;
    }

    private CellStyle fillTitleStyle() {
        CellStyle style = this.getCurrentWorkbook().createCellStyle();
        Font font = this.getCurrentWorkbook().createFont();
        font.setBold(true);
        style.setFillForegroundColor(IndexedColors.BLUE.index);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setFont(font);
        return style;
    }

    private String formatDouble(double str, int dig) {
        return String.format("%." + dig + "f", str);
    }

    private Map<String, CellStyle> getCpColorMap() {
        if (cpColorMap.size() == 0) {
            XSSFCellStyle style = (XSSFCellStyle) this.getCurrentWorkbook().createCellStyle();
            style.setFillForegroundColor(ExColor.GREEN);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            addBorder(style);
            XSSFFont font = (XSSFFont) workbook.createFont();
            font.setColor(IndexedColors.BLACK.index);
            style.setFont(font);
            cpColorMap.put(SpcKey.EXCELLENT.getCode(), style);

            style = (XSSFCellStyle) this.getCurrentWorkbook().createCellStyle();
            style.setFillForegroundColor(ExColor.CYAN);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            addBorder(style);
            font = (XSSFFont) workbook.createFont();
            font.setColor(IndexedColors.BLACK.index);
            style.setFont(font);
            cpColorMap.put(SpcKey.GOOD.getCode(), style);

            style = (XSSFCellStyle) this.getCurrentWorkbook().createCellStyle();
            style.setFillForegroundColor(ExColor.MBLUE);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            addBorder(style);
            font = (XSSFFont) workbook.createFont();
            font.setColor(IndexedColors.BLACK.index);
            style.setFont(font);
            cpColorMap.put(SpcKey.ACCEPTABLE.getCode(), style);

            style = (XSSFCellStyle) this.getCurrentWorkbook().createCellStyle();
            style.setFillForegroundColor(ExColor.ORANGE);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            addBorder(style);
            font = (XSSFFont) workbook.createFont();
            font.setColor(IndexedColors.BLACK.index);
            style.setFont(font);
            cpColorMap.put(SpcKey.BAD.getCode(), style);

            style = (XSSFCellStyle) this.getCurrentWorkbook().createCellStyle();
            style.setFillForegroundColor(ExColor.RED);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            addBorder(style);
            font = (XSSFFont) workbook.createFont();
            font.setColor(IndexedColors.BLACK.index);
            style.setFont(font);
            cpColorMap.put(SpcKey.RECTIFICATION.getCode(), style);

            style = (XSSFCellStyle) this.getCurrentWorkbook().createCellStyle();
            style.setFillForegroundColor(ExColor.RED);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            addBorder(style);
            font = (XSSFFont) workbook.createFont();
            font.setColor(IndexedColors.BLACK.index);
            style.setFont(font);
            cpColorMap.put(SpcKey.PASS.getCode(), style);

            style = (XSSFCellStyle) this.getCurrentWorkbook().createCellStyle();
            style.setFillForegroundColor(ExColor.ORANGE);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            addBorder(style);
            font = (XSSFFont) workbook.createFont();
            font.setColor(IndexedColors.BLACK.index);
            style.setFont(font);
            cpColorMap.put(SpcKey.FAIL.getCode(), style);

            style = (XSSFCellStyle) this.getCurrentWorkbook().createCellStyle();
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            addBorder(style);
            font = (XSSFFont) workbook.createFont();
            font.setColor(IndexedColors.BLACK.index);
            style.setFont(font);
            cpColorMap.put(SpcKey.NORMAL.getCode(), style);
        }

        return cpColorMap;
    }

    private boolean checkStaticData(Map<String, StatisticalAlarmDto> dto, String checkType) {
        if (dto == null) {
            return true;
        }
        if (Double.NEGATIVE_INFINITY == dto.get(checkType).getValue() || Double.POSITIVE_INFINITY == dto.get(checkType).getValue() || "-".equals(dto.get(checkType).getValue())) {
            return true;
        }
        return false;
    }

    @Override
    public SXSSFWorkbook getCurrentWorkbook() {
        return workbook;
    }

    @Override
    public List<ExSheet> getSheets() {
        return sheets;
    }

    public int[] getChartImageInfo() {
        return new int[]{imgSheetIndex, imageStartCol, imageEndCol, IMAGE_COL_WIDTH, curWrittenItemNum};
    }

    public int getCurWrittenItemNum() {
        return curWrittenItemNum;
    }

    public void setCurWrittenItemNum(int curWrittenItem) {
        this.curWrittenItemNum = curWrittenItem;
    }

    public int getExcelItemCapacity() {
        return excelItemCapacity;
    }

    public void setExcelItemCapacity(int excelItemCapacity) {
        this.excelItemCapacity = excelItemCapacity;
    }

    /**
     * initial worker
     */
    public void initWorkbook() {
        workbook = new SXSSFWorkbook(CACHE_ROW);
    }

    public void setDigNum(int digNum) {
        this.digNum = digNum;
    }

    /**
     * clear worker up
     */
    public void cleanExportWorker() {
        sheets.clear();
        cpColorMap = null;
        workbook = null;
        CellStyleUtil.clearResultMap();
    }

    private void addBorder(CellStyle cellStyle) {
        try {
            BorderStyle borderStyle = BorderStyle.THIN;
            cellStyle.setBorderBottom(borderStyle);
            cellStyle.setBorderLeft(borderStyle);
            cellStyle.setBorderRight(borderStyle);
            cellStyle.setBorderTop(borderStyle);
        } catch (Exception e) {
//            throw new ApplicationException(ExceptionMessages.ERR_20001, ResourceBundleUtils.getString(ExceptionMessages.ERR_20001));
        }
    }

    private String cusCpwToLevel(Map<String, StatisticalAlarmDto> cusCpwMap, String key) {
        if (cusCpwMap == null || cusCpwMap.get(key) == null || cusCpwMap.get(key).getLevel() == null) {
            return SpcKey.NORMAL.getCode();
        }
        return cusCpwMap.get(key).getLevel();
    }

    public List<Integer> getBreakRowLists() {
        return breakRowLists;
    }

    public int getRunChartRow() {
        return runChartRow;
    }

    public int getPerRow() {
        return perRow;
    }
}