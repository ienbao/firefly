/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.grr.service.impl.export;

import com.dmsoft.firefly.plugin.grr.dto.*;
import com.dmsoft.firefly.plugin.grr.dto.analysis.GrrAnovaAndSourceResultDto;
import com.dmsoft.firefly.plugin.grr.dto.analysis.GrrAnovaDto;
import com.dmsoft.firefly.plugin.grr.dto.analysis.GrrSourceDto;
import com.dmsoft.firefly.plugin.grr.dto.analysis.GrrSummaryResultDto;
import com.dmsoft.firefly.plugin.grr.service.impl.export.enums.RuleLevelType;
import com.dmsoft.firefly.plugin.grr.utils.AppConstant;
import com.dmsoft.firefly.plugin.grr.utils.enums.GrrResultName;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * fill data to sheet
 * <p>
 * Created by Alice on 2016/9/15.
 * Update by cherry on 2016/09/26
 */
public class GrrExportWorker implements ExWorker {
    private SXSSFWorkbook workbook = new SXSSFWorkbook(SXSSFWorkbook.DEFAULT_WINDOW_SIZE);
    private List<ExSheet> sheets = new ArrayList<>();
    private ExSheet exSheet = new ExSheet();
    private List<ExCell> cellList = Lists.newLinkedList();

    private Map<String, CellStyle> mapRuleStyle = Maps.newHashMap();
    private Map<String, CellStyle> mapCellStyle = Maps.newHashMap();

    private Integer[] headIndex = new Integer[]{0, 0};
    private Integer currentRow = 0;

    private Integer[] summaryDataIndex = new Integer[]{0, 3};
    private Integer[] itemParamIndex = new Integer[]{0, 1};
    private Map<String, List<Integer>> breakRowLists = new HashMap<>();
    private Map<String, Integer> disCategoriesPlaceMap = new HashMap<>();
    private int digNum = 6;

    /**
     * buildGrrSummary
     *
     * @param grrExportConfigDto   grrExportConfigDto
     * @param grrSummaryResultDtos grrSummaryResultDtos
     */
    public void buildGrrSummary(GrrExportConfigDto grrExportConfigDto, List<GrrSummaryDto> grrSummaryResultDtos) {
        digNum = grrExportConfigDto.getDigNum();
        mapCellStyle = CellStyleUtil.getStyle(this.getCurrentWorkbook());
        String userName = grrExportConfigDto.getUserName();
        if (sheets != null && sheets.size() < 1) {
            cellList.addAll(buildSummaryHead(userName));
        }
        cellList.addAll(buildSummaryContent(grrSummaryResultDtos, grrExportConfigDto));
        exSheet.setExCells(cellList);
        exSheet.setName(AppConstant.GRR_EXPORT_SUMMARY);
        if (sheets != null && sheets.size() < 1) {
            sheets.add(exSheet);
        }
    }

    /**
     * buildSummaryAndDetail
     *
     * @param grrExportConfigDto   grrExportConfigDto
     * @param grrSummaryResultDtos grrSummaryResultDtos
     * @param grrExportResultDtos  grrExportResultDtos
     */
    public void buildSummaryAndDetail(GrrExportConfigDto grrExportConfigDto, List<GrrSummaryDto> grrSummaryResultDtos, List<GrrExportResultDto> grrExportResultDtos) {
        digNum = grrExportConfigDto.getDigNum();
        mapCellStyle = CellStyleUtil.getStyle(this.getCurrentWorkbook());
        String username = grrExportConfigDto.getUserName();
        List<ExCell> summaryHead = buildSummaryHead(username);
        cellList.addAll(summaryHead);
        List<ExCell> summaryContent = buildSummaryAndDetailContent(grrSummaryResultDtos, grrExportConfigDto);
        cellList.addAll(summaryContent);
        exSheet.setExCells(cellList);
        sheets.add(exSheet);
        exSheet.setName(AppConstant.GRR_EXPORT_SUMMARY);
        cellList = null;
        exSheet = null;

        for (int i = 0; i < grrSummaryResultDtos.size(); i++) {
            for (int j = 0; j < grrExportResultDtos.size(); j++) {
                String summaryItemName = grrSummaryResultDtos.get(i).getItemName();
                String subItemName = grrExportResultDtos.get(j).getItemName();

                if (summaryItemName.equals(subItemName)) {
                    ExSheet exSheetItem = new ExSheet();
                    String itemName = grrExportResultDtos.get(j).getItemName();
                    itemName = "Detail_" + itemName;
                    itemName = DAPStringUtils.filterSpeChars(itemName);
                    exSheetItem.setName(itemName);
                    exSheetItem.setIndex(i + 1);
                    List<ExCell> itemsCellList = buildGRRTestItem(exSheetItem.getName(), grrExportResultDtos.get(j), grrSummaryResultDtos.get(i), grrExportConfigDto);
                    exSheetItem.setExCells(itemsCellList);
                    sheets.add(exSheetItem);
                    j++;
                    exSheetItem = null;
                    itemsCellList = null;
                }
            }
        }
    }

    private List<ExCell> buildSummaryHead(String performer) {
        List<ExCell> exCellList = Lists.newArrayList();

        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String nowDate = sdf.format(d);
        //Person Data
        exCellList.add(ExUtil.fillToCell(new Integer[]{headIndex[0], headIndex[0]}, "Date",
                ExCellType.TEXT, mapCellStyle.get(CellStyleType.head_lightBlue.toString())));
        exCellList.add(ExUtil.fillToCell(new Integer[]{headIndex[0], headIndex[0] + 1}, nowDate,
                ExCellType.TEXT, mapCellStyle.get(CellStyleType.summary_content_testItems_1.toString())));
        exCellList.add(ExUtil.fillToCell(new Integer[]{headIndex[0], headIndex[0] + 2}, "Author:",
                ExCellType.TEXT, mapCellStyle.get(CellStyleType.head_lightBlue.toString())));
        exCellList.add(ExUtil.fillToCell(new Integer[]{headIndex[0], headIndex[0] + 3}, performer,
                ExCellType.TEXT, mapCellStyle.get(CellStyleType.summary_content_testItems_1.toString())));
        exCellList.add(ExUtil.fillToCell(new Integer[]{headIndex[0], headIndex[0] + 4}, "",
                ExCellType.TEXT, mapCellStyle.get(CellStyleType.summary_content_testItems_1.toString())));

        //Head
        exCellList.add(ExUtil.fillToCell(new Integer[]{headIndex[0] + 2, headIndex[0]}, "TestItem",
                ExCellType.TEXT, mapCellStyle.get(CellStyleType.head_lightBlue.toString())));
        exCellList.add(ExUtil.fillToCell(new Integer[]{headIndex[0] + 2, headIndex[0] + 1}, "%Repeat.",
                ExCellType.TEXT, mapCellStyle.get(CellStyleType.head_lightBlue.toString())));
        exCellList.add(ExUtil.fillToCell(new Integer[]{headIndex[0] + 2, headIndex[0] + 2}, "%Reprod.",
                ExCellType.TEXT, mapCellStyle.get(CellStyleType.head_lightBlue.toString())));
        exCellList.add(ExUtil.fillToCell(new Integer[]{headIndex[0] + 2, headIndex[0] + 3}, "%Gage R & R",
                ExCellType.TEXT, mapCellStyle.get(CellStyleType.head_lightBlue.toString())));
        exCellList.add(ExUtil.fillToCell(new Integer[]{headIndex[0] + 2, headIndex[0] + 4}, "Result",
                ExCellType.TEXT, mapCellStyle.get(CellStyleType.head_lightBlue.toString())));

        return exCellList;
    }

    private List<ExCell> buildSummaryContent(List<GrrSummaryDto> grrSummaryResultDtos, GrrExportConfigDto grrExportConfigDto) {
        List<ExCell> exCellList = Lists.newArrayList();
        mapRuleStyle = SummaryRuleStyle.getBackStyle(this.getCurrentWorkbook());

        for (int i = 0; i < grrSummaryResultDtos.size(); i++) {
            String name = grrSummaryResultDtos.get(i).getItemName();
            GrrSummaryDto grrSummaryDto = grrSummaryResultDtos.get(i);
            GrrSummaryResultDto grrSummary = grrSummaryDto.getSummaryResultDto();
            String tolerance = grrSummaryDto.getSummaryResultDto().getTolerance().toString();
            String repeat = "";
            String reprod = "";
            String grr = "";
            String result = "";
            if (!grrExportConfigDto.isTolerance()) {
                repeat = grrSummary.getRepeatabilityOnContribution().toString();
                reprod = grrSummary.getReproducibilityOnContribution().toString();
                grr = grrSummary.getGrrOnContribution().toString();
                result = buildGrrLevel(grrExportConfigDto.getLevel(), grrSummary.getGrrOnContribution());
            } else {
                repeat = grrSummary.getRepeatabilityOnTolerance().toString();
                reprod = grrSummary.getReproducibilityOnTolerance().toString();
                grr = grrSummary.getGrrOnTolerance().toString();
                result = buildGrrLevel(grrExportConfigDto.getLevel(), grrSummary.getGrrOnTolerance());
            }

            String[] arr = {name, repeat, reprod, grr};

            if (DAPStringUtils.isEmpty(result)) {
                result = "-";
            }

            exCellList.add(ExUtil.fillToCell(new Integer[]{summaryDataIndex[1] + i, summaryDataIndex[0]}, arr[0],
                    ExCellType.TEXT, mapCellStyle.get(CellStyleType.summary_content_testItems_1.toString())));
            exCellList.add(ExUtil.fillToCell(new Integer[]{summaryDataIndex[1] + i, summaryDataIndex[0] + 1}, !DAPStringUtils.isNumeric(arr[1]) ? "-" : arr[1] + "%",
                    ExCellType.TEXT, mapCellStyle.get(CellStyleType.summary_content_testItems_1.toString())));
            exCellList.add(ExUtil.fillToCell(new Integer[]{summaryDataIndex[1] + i, summaryDataIndex[0] + 2}, !DAPStringUtils.isNumeric(arr[2]) ? "-" : arr[2] + "%",
                    ExCellType.TEXT, mapCellStyle.get(CellStyleType.summary_content_testItems_1.toString())));
            exCellList.add(ExUtil.fillToCell(new Integer[]{summaryDataIndex[1] + i, summaryDataIndex[0] + 3}, !DAPStringUtils.isNumeric(arr[3]) ? "-" : arr[3] + "%",
                    ExCellType.TEXT, mapCellStyle.get(CellStyleType.summary_content_testItems_1.toString())));
            exCellList.add(ExUtil.fillToCell(new Integer[]{summaryDataIndex[1] + i, summaryDataIndex[0] + 4}, result,
                    ExCellType.TEXT, mapRuleStyle.get(result)));

            arr = null;
        }
        return exCellList;
    }

    private List<ExCell> buildSummaryAndDetailContent(List<GrrSummaryDto> grrSummaryResultDtos, GrrExportConfigDto grrExportConfigDto) {
        List<ExCell> exCellList = Lists.newArrayList();
        List<String> itemNameAdd = Lists.newArrayList();
        mapRuleStyle = SummaryRuleStyle.getBackStyle(this.getCurrentWorkbook());

        for (int i = 0; i < grrSummaryResultDtos.size(); i++) {

            String name = grrSummaryResultDtos.get(i).getItemName();
            GrrSummaryResultDto grrSummary = grrSummaryResultDtos.get(i).getSummaryResultDto();
            String repeat = "";
            String reprod = "";
            String grr = "";
            String result = "";
            int digNumber = digNum <= 2 ? 0 : digNum - 2;
            if (!grrExportConfigDto.isTolerance()) {
                repeat = DAPStringUtils.formatDouble(grrSummary.getRepeatabilityOnContribution(), digNumber);
                reprod = DAPStringUtils.formatDouble(grrSummary.getReproducibilityOnContribution(), digNumber);
                grr = DAPStringUtils.formatDouble(grrSummary.getGrrOnContribution(), digNumber);
                result = buildGrrLevel(grrExportConfigDto.getLevel(), grrSummary.getGrrOnContribution());
            } else {
                repeat = DAPStringUtils.formatDouble(grrSummary.getRepeatabilityOnTolerance(), digNumber);
                reprod = DAPStringUtils.formatDouble(grrSummary.getReproducibilityOnTolerance(), digNumber);
                grr = DAPStringUtils.formatDouble(grrSummary.getGrrOnTolerance(), digNumber);
                result = buildGrrLevel(grrExportConfigDto.getLevel(), grrSummary.getGrrOnTolerance());
            }

            String[] arr = {name, repeat, reprod, grr};

            if (DAPStringUtils.isEmpty(result)) {
                result = "-";
            }

            if (arr[3].equals("-")) {
                exCellList.add(ExUtil.fillToCell(new Integer[]{summaryDataIndex[1] + i, summaryDataIndex[0]}, arr[0],
                        ExCellType.TEXT, mapCellStyle.get(CellStyleType.summary_content_testItems_1.toString())));
            } else {
                exCellList.add(ExUtil.fillToCell(new Integer[]{summaryDataIndex[1] + i, summaryDataIndex[0]}, arr[0],
                        ExCellType.HYPERLINK.withCode(ExSheet.formatName(i + 1, "Detail_" + name)), mapCellStyle.get(CellStyleType.summary_content_testItems.toString())));
            }

            exCellList.add(ExUtil.fillToCell(new Integer[]{summaryDataIndex[1] + i, summaryDataIndex[0] + 1}, !DAPStringUtils.isNumeric(arr[1]) ? "-" : arr[1] + "%",
                    ExCellType.TEXT, mapCellStyle.get(CellStyleType.summary_content_testItems_1.toString())));
            exCellList.add(ExUtil.fillToCell(new Integer[]{summaryDataIndex[1] + i, summaryDataIndex[0] + 2}, !DAPStringUtils.isNumeric(arr[2]) ? "-" : arr[2] + "%",
                    ExCellType.TEXT, mapCellStyle.get(CellStyleType.summary_content_testItems_1.toString())));
            exCellList.add(ExUtil.fillToCell(new Integer[]{summaryDataIndex[1] + i, summaryDataIndex[0] + 3}, !DAPStringUtils.isNumeric(arr[3]) ? "-" : arr[3] + "%",
                    ExCellType.TEXT, mapCellStyle.get(CellStyleType.summary_content_testItems_1.toString())));

            exCellList.add(ExUtil.fillToCell(new Integer[]{summaryDataIndex[1] + i, summaryDataIndex[0] + 4}, result,
                    ExCellType.TEXT, mapRuleStyle.get(result)));

            arr = null;
        }
        Font font = workbook.createFont();
        font.setFontName("Arial");
        if (itemNameAdd != null && !itemNameAdd.isEmpty()) {
            XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
            style.setFont(font);
            for (int i = 0; i < itemNameAdd.size(); i++) {
                exCellList.add(ExUtil.fillToCell(new Integer[]{summaryDataIndex[1] + 1 + grrSummaryResultDtos.size() + i, summaryDataIndex[0]}, itemNameAdd.get(i) + "  is not consistent with GRR analysis require!",
                        ExCellType.TEXT, style));
            }
        }

        return exCellList;
    }

    private List<ExCell> buildGRRTestItem(String sheetName, GrrExportResultDto grrExportResultDto, GrrSummaryDto grrSummaryResultDto, GrrExportConfigDto grrExportConfigDto) {
        List<ExCell> exCellList = Lists.newArrayList();

        if (grrExportResultDto.getGrrAnovaAndSourceResultDto() != null || grrSummaryResultDto != null || grrExportResultDto.getGrrImageDto() != null) {
            exCellList.add(ExUtil.fillToCell(new Integer[]{0, 0}, "Summary", ExCellType.HYPERLINK.withCode(ExSheet.formatName(0, "Summary")), mapCellStyle.get(CellStyleType.link.toString())));
        }
        currentRow = 0;
        if (grrSummaryResultDto != null) {
            List<ExCell> exCellParamList = buildTestItemParam(grrSummaryResultDto, grrExportConfigDto);
            exCellList.addAll(exCellParamList);
        }
        if (grrExportResultDto.getGrrAnovaAndSourceResultDto() != null) {
            List<ExCell> exCellGRRAnovaList = buildAnova(grrExportResultDto.getGrrAnovaAndSourceResultDto());
            List<ExCell> exCellGRRSourceList = buildSource(grrExportResultDto.getGrrAnovaAndSourceResultDto(), sheetName, grrExportConfigDto.getSigma());
            exCellList.addAll(exCellGRRAnovaList);
            exCellList.addAll(exCellGRRSourceList);
        }
        if (grrExportResultDto.getGrrImageDto() != null) {
            List<ExCell> exCellGRRImagesList = buildImages(sheetName, grrExportResultDto.getGrrImageDto());
            exCellList.addAll(exCellGRRImagesList);
        }

        return exCellList;
    }

    private List<ExCell> buildTestItemParam(GrrSummaryDto grrSummaryResultDto, GrrExportConfigDto grrExportConfigDto) {
        List<ExCell> exCellList = Lists.newArrayList();
        mapRuleStyle = SummaryRuleStyle.getBackStyle(this.getCurrentWorkbook());
        String name = grrSummaryResultDto.getItemName();
        GrrSummaryResultDto grrSummary = grrSummaryResultDto.getSummaryResultDto();
        //getProcess
        String usl = grrSummary.getUsl().toString();
        String lsl = grrSummary.getLsl().toString();
        String tolerance = grrSummary.getTolerance() == null ? "-" : DAPStringUtils.formatDouble(grrSummary.getTolerance(), digNum);
        String grr = "";
        String result = "";
        int digNumber = digNum <= 2 ? 0 : digNum - 2;

        if (grrExportConfigDto.isTolerance()) {
            grr = grrSummary.getGrrOnTolerance() == null ? "-" : DAPStringUtils.formatDouble(grrSummary.getGrrOnTolerance(), digNumber) + "%";
            result = buildGrrLevel(grrExportConfigDto.getLevel(), grrSummary.getGrrOnTolerance()) == null ? "-" : buildGrrLevel(grrExportConfigDto.getLevel(), grrSummary.getGrrOnTolerance());
        } else {
            grr = grrSummary.getGrrOnContribution() == null ? "-" : DAPStringUtils.formatDouble(grrSummary.getGrrOnContribution(), digNumber) + "%";
            result = buildGrrLevel(grrExportConfigDto.getLevel(), grrSummary.getGrrOnContribution()) == null ? "-" : buildGrrLevel(grrExportConfigDto.getLevel(), grrSummary.getGrrOnContribution());
        }


        String partNum = String.valueOf(grrExportConfigDto.getParts());
        String appraiserNum = String.valueOf(grrExportConfigDto.getAppraisers());
        String trailNum = String.valueOf(grrExportConfigDto.getTrials());
        String[] key = {"USL", "LSL", "Tolerance", "Parts", "Appraisers", "Trials", "Gage R&R"};
        String[] value = {usl, lsl, tolerance, partNum, appraiserNum, trailNum, grr};

        for (int j = 0; j < value.length; j++) {
            if (j == 0 || j == 1) {
                if (value[j] != null && DAPStringUtils.isBlankWithSpecialNumber(value[j])) {
                    value[j] = "-";
                }
                if (value[j] == null) {
                    value[j] = " ";
                }
            } else {
                if (DAPStringUtils.isBlankWithSpecialNumber(value[j]) || DAPStringUtils.isCheckInfinityAndNaN(value[j])) {
                    value[j] = "-";
                }
            }
        }

        //back ground and bounder
        for (int i = 0; i < 6; i++) {
            exCellList.add(ExUtil.fillToCell(new Integer[]{itemParamIndex[1], itemParamIndex[0] + i}, "",
                    ExCellType.TEXT, mapCellStyle.get(CellStyleType.head_lightBlue_center.toString())));
        }
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 6; j++) {
                exCellList.add(ExUtil.fillToCell(new Integer[]{itemParamIndex[1] + i, itemParamIndex[0] + j}, "",
                        ExCellType.TEXT, mapCellStyle.get(CellStyleType.summary_content_testItems_1.toString())));
            }
        }
        //fill data
        CellStyle cellStyle = mapCellStyle.get(CellStyleType.head_lightBlue_center.toString());
        cellStyle.setWrapText(false);
        exCellList.add(ExUtil.fillToCell(new Integer[]{itemParamIndex[1], itemParamIndex[0]}, "Test Item:" + name,
                ExCellType.TEXT, cellStyle));
        for (int i = 0; i < key.length; i++) {
            exCellList.add(ExUtil.fillToCell(new Integer[]{itemParamIndex[1] + 1 + i, itemParamIndex[0]}, key[i],
                    ExCellType.TEXT, mapCellStyle.get(CellStyleType.summary_content_testItems_weight.toString())));
            exCellList.add(ExUtil.fillToCell(new Integer[]{itemParamIndex[1] + 1 + i, itemParamIndex[0] + 1}, value[i],
                    ExCellType.TEXT, mapCellStyle.get(CellStyleType.summary_content_testItems_1.toString())));

        }

        if (DAPStringUtils.isEmpty(result)) {
            result = "-";
        }

        exCellList.add(ExUtil.fillToCell(new Integer[]{itemParamIndex[1] + 8, itemParamIndex[0]}, "Result",
                ExCellType.TEXT, mapCellStyle.get(CellStyleType.summary_content_testItems_weight.toString())));
        exCellList.add(ExUtil.fillToCell(new Integer[]{itemParamIndex[1] + 8, itemParamIndex[0] + 1}, result,
                ExCellType.TEXT, mapRuleStyle.get(result)));
        currentRow = currentRow + 9;

        return exCellList;
    }

    private List<ExCell> buildAnova(GrrAnovaAndSourceResultDto grrAnovaAndSourceResultDto) {
        List<ExCell> exCellList = Lists.newArrayList();
        String[] anovaTitle = ExportLabelConstant.EXPORT_ANOVA_LABELS;
        List<GrrAnovaDto> grrAnovaDtos = grrAnovaAndSourceResultDto.getGrrAnovaDtos();
        if (grrAnovaDtos != null && !grrAnovaDtos.isEmpty()) {
            currentRow = currentRow + 2;
            for (int i = 0; i < anovaTitle.length; i++) {
                exCellList.add(ExUtil.fillToCell(new Integer[]{itemParamIndex[1] + currentRow, itemParamIndex[0] + i}, anovaTitle[i], ExCellType.TEXT, mapCellStyle.get(CellStyleType.head_lightBlue.toString())));
            }

            currentRow = currentRow + 1;
            for (int i = 0; i < grrAnovaDtos.size(); i++) {
                String keyValue = grrAnovaDtos.get(i).getName() == null ? "-" : grrAnovaDtos.get(i).getName().toString();
                String dfValue = grrAnovaDtos.get(i).getDf() == null ? "-" : DAPStringUtils.formatDouble(grrAnovaDtos.get(i).getDf(), digNum);
                String ssValue = grrAnovaDtos.get(i).getSs() == null ? "-" : DAPStringUtils.formatDouble(grrAnovaDtos.get(i).getSs(), digNum);
                String msValue = grrAnovaDtos.get(i).getMs() == null ? "-" : DAPStringUtils.formatDouble(grrAnovaDtos.get(i).getMs(), digNum);
                String fValue = grrAnovaDtos.get(i).getF() == null ? "-" : DAPStringUtils.formatDouble(grrAnovaDtos.get(i).getF(), digNum);
                String probFValue = grrAnovaDtos.get(i).getProbF() == null ? "-" : DAPStringUtils.formatDouble(grrAnovaDtos.get(i).getProbF(), digNum);
                String[] arr = {dfValue, ssValue, msValue, fValue, probFValue};

                if (keyValue.equals("Repeatability")) {
                    keyValue = "Repeat.";
                }

                if (keyValue.equals("Appraisers*Parts")) {
                    keyValue = "A*p";
                }

                exCellList.add(ExUtil.fillToCell(new Integer[]{itemParamIndex[1] + currentRow + i, itemParamIndex[0]},
                        keyValue, ExCellType.TEXT, mapCellStyle.get(CellStyleType.summary_content_testItems_weight.toString())));
                for (int j = 0; j < arr.length; j++) {
                    if (DAPStringUtils.isBlankWithSpecialNumber(arr[j]) || DAPStringUtils.isCheckInfinityAndNaN(arr[j])) {
                        arr[j] = "-";
                    }

                    exCellList.add(ExUtil.fillToCell(new Integer[]{itemParamIndex[1] + currentRow + i, itemParamIndex[0] + 1 + j},
                            arr[j], ExCellType.TEXT, mapCellStyle.get(CellStyleType.summary_content_testItems_1.toString())));
                }
                arr = null;
            }
            currentRow = currentRow + grrAnovaDtos.size();
        }
        anovaTitle = null;
        return exCellList;
    }

    private List<ExCell> buildSource(GrrAnovaAndSourceResultDto grrAnovaAndSourceResultDto, String sheetName, String coverage) {
        List<ExCell> exCellList = Lists.newArrayList();

        String[] sourceTitle1 = ExportLabelConstant.EXPORT_SOURCE_LABELS;
        String[] sourceTitle = sourceTitle1.clone();
        String categories = ExportLabelConstant.EXPORT_GRR_ITEM_CATEGORIES;
        sourceTitle[2] = sourceTitle[2] + " " + coverage;
        List<GrrSourceDto> grrSourceDtoList = grrAnovaAndSourceResultDto.getGrrSourceDtos();

        if (grrSourceDtoList != null && !grrSourceDtoList.isEmpty()) {
            currentRow = currentRow + 2;
            for (int i = 0; i < sourceTitle.length; i++) {
                exCellList.add(ExUtil.fillToCell(new Integer[]{itemParamIndex[1] + currentRow, itemParamIndex[0] + i}, sourceTitle[i], ExCellType.TEXT,
                        mapCellStyle.get(CellStyleType.head_lightBlue.toString())));
            }

            currentRow = currentRow + 1;
            for (int i = 0; i < grrSourceDtoList.size(); i++) {
                GrrResultName grrKey = grrSourceDtoList.get(i).getName();
                String keyValue = "";
                String variation = grrSourceDtoList.get(i).getVariation() == null ? "-" : DAPStringUtils.formatDouble(grrSourceDtoList.get(i).getVariation(), digNum);
                String sigma = grrSourceDtoList.get(i).getSigma() == null ? "-" : DAPStringUtils.formatDouble(grrSourceDtoList.get(i).getSigma(), digNum);
                String sv = grrSourceDtoList.get(i).getStudyVar() == null ? "-" : DAPStringUtils.formatDouble(grrSourceDtoList.get(i).getStudyVar(), digNum);
                String contribution = grrSourceDtoList.get(i).getContribution() == null ? "-" : DAPStringUtils.formatDouble(grrSourceDtoList.get(i).getContribution(), digNum);
                String totalVariation = grrSourceDtoList.get(i).getTotalVariation() == null ? "-" : DAPStringUtils.formatDouble(grrSourceDtoList.get(i).getTotalVariation(), digNum);
                String tolerance = grrSourceDtoList.get(i).getTotalTolerance() == null ? "-" : DAPStringUtils.formatDouble(grrSourceDtoList.get(i).getTotalTolerance(), digNum);
                String[] arr = {sigma, sv, variation, contribution, totalVariation, tolerance};
                for (int j = 0; j < arr.length; j++) {
                    if (DAPStringUtils.isBlankWithSpecialNumber(arr[j]) || DAPStringUtils.isCheckInfinityAndNaN(arr[j])) {
                        arr[j] = "-";
                    }
                }
                if (grrKey.equals(GrrResultName.Repeatability)) {
                    keyValue = "Repeat.";
                }
                if (grrKey.equals(GrrResultName.Reproducibility)) {
                    keyValue = "Reprod.";
                }

                if (grrKey.equals(GrrResultName.AppraisersAndParts)) {
                    keyValue = "A*p";
                }
                exCellList.add(ExUtil.fillToCell(new Integer[]{itemParamIndex[1] + currentRow + i, itemParamIndex[0]}, keyValue, ExCellType.TEXT,
                        mapCellStyle.get(CellStyleType.summary_content_testItems_weight.toString())));
                for (int j = 0; j < arr.length; j++) {
                    if (j < 3) {
//                        arr[j] = arr[j].equals("-") ? "-" : formatDouble(Double.valueOf(arr[j]), digGrrNum);
                        arr[j] = arr[j];
                    } else {
//                        arr[j] = arr[j].equals("-") ? "-" : formatDouble(Double.valueOf(arr[j]), digGrrNum) + "%";
                        arr[j] = arr[j].equals("-") ? "-" : arr[j] + "%";
                    }
                    exCellList.add(ExUtil.fillToCell(new Integer[]{itemParamIndex[1] + currentRow + i, itemParamIndex[0] + 1 + j}, arr[j], ExCellType.TEXT,
                            mapCellStyle.get(CellStyleType.summary_content_testItems_1.toString())));
                }
            }

            currentRow = currentRow + grrSourceDtoList.size();
        }

        currentRow = currentRow + 2;
        String distinctCategories = grrAnovaAndSourceResultDto.getNumberOfDc().toString();

        CellStyle cellStyle = mapCellStyle.get(CellStyleType.head_lightBlue_center.toString());
        cellStyle.setWrapText(false);
        exCellList.add(ExUtil.fillToCell(new Integer[]{itemParamIndex[1] + currentRow, itemParamIndex[0]}, categories, ExCellType.TEXT, cellStyle));

        if (!DAPStringUtils.isNumeric(distinctCategories)) {
            distinctCategories = "-";
            exCellList.add(ExUtil.fillToCell(new Integer[]{itemParamIndex[1] + currentRow, itemParamIndex[0] + 1}, distinctCategories, ExCellType.TEXT, cellStyle));
//        }else if (Integer.parseInt(distinctCategories) < 5) {
        } else if (Double.valueOf(distinctCategories).intValue() < 5) {
            exCellList.add(ExUtil.fillToCell(new Integer[]{itemParamIndex[1] + currentRow, itemParamIndex[0] + 1}, distinctCategories, ExCellType.TEXT,
                    mapCellStyle.get(CellStyleType.summary_content_testItems_8.toString())));
        } else {
            exCellList.add(ExUtil.fillToCell(new Integer[]{itemParamIndex[1] + currentRow, itemParamIndex[0] + 1}, distinctCategories, ExCellType.TEXT,
                    mapCellStyle.get(CellStyleType.summary_content_testItems_7.toString())));
        }

        exCellList.add(ExUtil.fillToCell(new Integer[]{itemParamIndex[1] + currentRow, itemParamIndex[0] + 2}, "", ExCellType.TEXT,
                mapCellStyle.get(CellStyleType.summary_content_testItems_2.toString())));
        disCategoriesPlaceMap.put(sheetName, itemParamIndex[1] + currentRow);
        currentRow = currentRow + 1;

        sourceTitle = null;
        sourceTitle1 = null;

        return exCellList;
    }

    private List<ExCell> buildImages(String sheetName, GrrImageDto grrImageDto) {
        List<ExCell> exCellList = Lists.newArrayList();
        List<Integer> pageRows = Lists.newArrayList();

        final int widthIndex = 7;
        final int heightIndex = 14;
        int interval = 0;
        int rowBreak = 1;
        int imageCount = 0;


        if (DAPStringUtils.isNotBlank(grrImageDto.getGrrComponentsImagePath())) {
            currentRow = currentRow + 2;
            exCellList.add(ExUtil.fillToCell(new Integer[]{0, 0, AppConstant.EXPORT_IMG_WEIGHT, AppConstant.EXPORT_IMG_HEIGHT,
                    itemParamIndex[0], itemParamIndex[1] + currentRow + interval, itemParamIndex[0] + widthIndex, itemParamIndex[1] + heightIndex + currentRow + interval}, grrImageDto.getGrrComponentsImagePath(), ExCellType.IMAGE));
            currentRow = currentRow + heightIndex;
            imageCount++;
            if (imageCount == 1 || imageCount == 4) {
                pageRows.add(currentRow + rowBreak);
            }
            if (imageCount == 3 || imageCount == 6) {
                pageRows.add(currentRow + interval + rowBreak);
            }
        }

        if (DAPStringUtils.isNotBlank(grrImageDto.getGrrAPlotImagePath())) {
            currentRow = currentRow + 2;
            exCellList.add(ExUtil.fillToCell(new Integer[]{0, 0, AppConstant.EXPORT_IMG_WEIGHT, AppConstant.EXPORT_IMG_HEIGHT,
                    itemParamIndex[0], itemParamIndex[1] + currentRow + interval, itemParamIndex[0] + widthIndex, itemParamIndex[1] + heightIndex + currentRow + interval}, grrImageDto.getGrrAPlotImagePath(), ExCellType.IMAGE));
            currentRow = currentRow + heightIndex;
            imageCount++;
            if ((imageCount == 1 || imageCount == 4)) {
                pageRows.add(currentRow + rowBreak);
            }
            if (imageCount == 3 || imageCount == 6) {
                pageRows.add(currentRow + interval + rowBreak);
            }
        }

        if (DAPStringUtils.isNotBlank(grrImageDto.getGrrXBarImagePath())) {
            currentRow = currentRow + 2;
            exCellList.add(ExUtil.fillToCell(new Integer[]{0, 0, AppConstant.EXPORT_IMG_WEIGHT, AppConstant.EXPORT_IMG_HEIGHT,
                    itemParamIndex[0], itemParamIndex[1] + currentRow + interval, itemParamIndex[0] + widthIndex, itemParamIndex[1] + heightIndex + currentRow + interval}, grrImageDto.getGrrXBarImagePath(), ExCellType.IMAGE));
            currentRow = currentRow + heightIndex;
            imageCount++;
            if ((imageCount == 1 || imageCount == 4)) {
                pageRows.add(currentRow + rowBreak);
            }
            if (imageCount == 3 || imageCount == 6) {
                pageRows.add(currentRow + interval + rowBreak);
            }
        }

        if (DAPStringUtils.isNotBlank(grrImageDto.getGrrRChartImagePath())) {
            currentRow = currentRow + 2;
            exCellList.add(ExUtil.fillToCell(new Integer[]{0, 0, AppConstant.EXPORT_IMG_WEIGHT, AppConstant.EXPORT_IMG_HEIGHT,
                    itemParamIndex[0], itemParamIndex[1] + currentRow + interval, itemParamIndex[0] + widthIndex, itemParamIndex[1] + heightIndex + currentRow + interval}, grrImageDto.getGrrRChartImagePath(), ExCellType.IMAGE));
            currentRow = currentRow + heightIndex;
            imageCount++;
            if ((imageCount == 1 || imageCount == 4)) {
                pageRows.add(currentRow + rowBreak);
            }
            if (imageCount == 3 || imageCount == 6) {
                pageRows.add(currentRow + interval + rowBreak);
            }
        }

        if (DAPStringUtils.isNotBlank(grrImageDto.getGrrRPlotChartAppImagePath())) {
            currentRow = currentRow + 2;
            exCellList.add(ExUtil.fillToCell(new Integer[]{0, 0, AppConstant.EXPORT_IMG_WEIGHT, AppConstant.EXPORT_IMG_HEIGHT,
                    itemParamIndex[0], itemParamIndex[1] + currentRow + interval, itemParamIndex[0] + widthIndex, itemParamIndex[1] + heightIndex + currentRow + interval}, grrImageDto.getGrrRPlotChartAppImagePath(), ExCellType.IMAGE));
            currentRow = currentRow + heightIndex;
            imageCount++;
            if ((imageCount == 1 || imageCount == 4)) {
                pageRows.add(currentRow + rowBreak);
            }
            if (imageCount == 3 || imageCount == 6) {
                pageRows.add(currentRow + interval + rowBreak);
            }
        }

        if (DAPStringUtils.isNotBlank(grrImageDto.getGrrRPlotChartPartImagePath())) {
            currentRow = currentRow + 2;
            exCellList.add(ExUtil.fillToCell(new Integer[]{0, 0, AppConstant.EXPORT_IMG_WEIGHT, AppConstant.EXPORT_IMG_HEIGHT,
                    itemParamIndex[0], itemParamIndex[1] + currentRow + interval, itemParamIndex[0] + widthIndex, itemParamIndex[1] + heightIndex + currentRow + interval}, grrImageDto.getGrrRPlotChartPartImagePath(), ExCellType.IMAGE));
            currentRow = currentRow + heightIndex;
            imageCount++;
            if ((imageCount == 1 || imageCount == 4)) {
                pageRows.add(currentRow + rowBreak);
            }
            if (imageCount == 3 || imageCount == 6) {
                pageRows.add(currentRow + interval + rowBreak);
            }
        }
        breakRowLists.put(sheetName, pageRows);
        return exCellList;
    }

    @Override
    public SXSSFWorkbook getCurrentWorkbook() {
        return workbook;
    }

    @Override
    public List<ExSheet> getSheets() {
        return sheets;
    }

    public Map<String, List<Integer>> getBreakRowLists() {
        return breakRowLists;
    }

    public void setBreakRowLists(Map<String, List<Integer>> breakRowLists) {
        this.breakRowLists = breakRowLists;
    }

    public Map<String, Integer> getDisCategoriesPlaceMap() {
        return disCategoriesPlaceMap;
    }

    public void setDisCategoriesPlaceMap(Map<String, Integer> disCategoriesPlaceMap) {
        this.disCategoriesPlaceMap = disCategoriesPlaceMap;
    }

    private boolean isChinese(String strName) {
        char[] ch = strName.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }

    private boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    /**
     * clear worker up
     */
    public void cleanExportWorker() {
        sheets.clear();
        workbook = null;
        CellStyleUtil.clearResultMap();
    }

    private String buildGrrLevel(List<Double> level, Double value) {
        if (value != null) {
            if (value < level.get(0)) {
                return RuleLevelType.EXCELLENT.getValue();
            } else if (value < level.get(1)) {
                return RuleLevelType.ADEQUATE.getValue();
            } else {
                return RuleLevelType.BAD.getValue();
            }
        }
        return null;
    }
}