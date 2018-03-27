package com.dmsoft.firefly.plugin.grr.service.impl;

import com.dmsoft.firefly.plugin.grr.dto.GrrDetailDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrExportDetailDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrSummaryDto;
import com.dmsoft.firefly.plugin.grr.dto.analysis.*;
import com.dmsoft.firefly.plugin.grr.service.GrrAnalysisService;
import com.dmsoft.firefly.plugin.grr.service.GrrService;
import com.dmsoft.firefly.plugin.grr.utils.GrrExceptionCode;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dataframe.DataColumn;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.plugin.apis.annotation.OpenService;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * impl class for grr service
 */
@OpenService
public class GrrServiceImpl implements GrrService {
    private GrrAnalysisService analysisService;

    private static final String MAP_KEY_DATA = "data";
    private static final String MAP_KEY_COUNT = "count";

    @Override
    public List<GrrSummaryDto> getSummaryResult(SearchDataFrame dataFrame, List<TestItemWithTypeDto> testItemDtoList, List<String> rowKeysToByAnalyzed, GrrAnalysisConfigDto configDto) {
        if (dataFrame == null || testItemDtoList == null || configDto == null) {
            throw new ApplicationException(GrrFxmlAndLanguageUtils.getString(GrrExceptionCode.ERR_12001));
        }
        List<GrrSummaryDto> result = Lists.newArrayList();
        List<GrrAnalysisDataDto> grrAnalysisDataDtoList = Lists.newArrayList();
        for (TestItemWithTypeDto itemDto : testItemDtoList) {
            GrrAnalysisDataDto grrAnalysisDataDto = new GrrAnalysisDataDto();
            List<String> datas = dataFrame.getDataValue(itemDto.getTestItemName(), rowKeysToByAnalyzed);
            Map<String, Object> dataMap = convertData(datas);
            List<Double> doubleList = (List<Double>) dataMap.get(MAP_KEY_DATA);
            Integer count = (Integer) dataMap.get(MAP_KEY_COUNT);
            if (itemDto.getLsl() != null) {
                grrAnalysisDataDto.setLsl(itemDto.getLsl());
            } else {
                grrAnalysisDataDto.setLsl(dataFrame.getTestItemWithTypeDto(itemDto.getTestItemName()).getLsl());
            }
            if (itemDto.getUsl() != null) {
                grrAnalysisDataDto.setUsl(itemDto.getUsl());
            } else {
                grrAnalysisDataDto.setUsl(dataFrame.getTestItemWithTypeDto(itemDto.getTestItemName()).getUsl());
            }
            if (datas == null || doubleList == null || count == datas.size() || datas.size() != doubleList.size()) {
                grrAnalysisDataDto.setDataList(null);
            } else {
                grrAnalysisDataDto.setDataList(doubleList);
            }
            grrAnalysisDataDtoList.add(grrAnalysisDataDto);
        }
        for (int i = 0; i < grrAnalysisDataDtoList.size(); i++) {
            GrrSummaryDto summaryDto = new GrrSummaryDto();
            summaryDto.setItemName(testItemDtoList.get(i).getTestItemName());
            if (grrAnalysisDataDtoList.get(i) == null) {
                continue;
            }
            if (grrAnalysisDataDtoList.get(i).getDataList() == null) {
                GrrSummaryResultDto grrSummaryResultDto = new GrrSummaryResultDto();
                if (DAPStringUtils.isNumeric(grrAnalysisDataDtoList.get(i).getUsl())) {
                    grrSummaryResultDto.setUsl(Double.valueOf(grrAnalysisDataDtoList.get(i).getUsl()));
                } else {
                    grrSummaryResultDto.setUsl(Double.NaN);
                }
                if (DAPStringUtils.isNumeric(grrAnalysisDataDtoList.get(i).getLsl())) {
                    grrSummaryResultDto.setLsl(Double.valueOf(grrAnalysisDataDtoList.get(i).getLsl()));
                } else {
                    grrSummaryResultDto.setLsl(Double.NaN);
                }
                grrSummaryResultDto.setTolerance(grrSummaryResultDto.getUsl() - grrSummaryResultDto.getLsl());
                summaryDto.setSummaryResultDto(grrSummaryResultDto);
            } else {
                GrrSummaryResultDto resultDto = getAnalysisService().analyzeSummaryResult(grrAnalysisDataDtoList.get(i), configDto);
                summaryDto.setSummaryResultDto(resultDto);
            }
            result.add(summaryDto);
        }
        return result;
    }

    @Override
    public GrrDetailDto getDetailResult(DataColumn dataColumn, TestItemWithTypeDto testItemDto, List<String> rowKeysToByAnalyzed, GrrAnalysisConfigDto configDto) {
        if (dataColumn == null || testItemDto == null || configDto == null) {
            throw new ApplicationException(GrrFxmlAndLanguageUtils.getString(GrrExceptionCode.ERR_12001));
        }
        GrrDetailDto result = new GrrDetailDto();

        GrrAnalysisDataDto grrAnalysisDataDto = new GrrAnalysisDataDto();
        List<String> datas = dataColumn.getData(rowKeysToByAnalyzed);
        Map<String, Object> dataMap = convertData(datas);
        List<Double> doubleList = (List<Double>) dataMap.get(MAP_KEY_DATA);
        Integer count = (Integer) dataMap.get(MAP_KEY_COUNT);
        if (datas == null || doubleList == null || count == datas.size() || datas.size() != doubleList.size()) {
            return null;
        }
        if (testItemDto.getLsl() != null) {
            grrAnalysisDataDto.setLsl(testItemDto.getLsl());
        } else {
            grrAnalysisDataDto.setLsl(dataColumn.getTestItemWithTypeDto().getLsl());
        }
        if (testItemDto.getUsl() != null) {
            grrAnalysisDataDto.setUsl(testItemDto.getUsl());
        } else {
            grrAnalysisDataDto.setUsl(dataColumn.getTestItemWithTypeDto().getUsl());
        }
        grrAnalysisDataDto.setDataList(doubleList);
        GrrDetailResultDto resultDto = getAnalysisService().analyzeDetailResult(grrAnalysisDataDto, configDto);
        result.setItemName(testItemDto.getTestItemName());
        result.setGrrDetailResultDto(resultDto);
        return result;
    }

    @Override
    public GrrExportDetailDto getExportDetailResult(DataColumn dataColumn, TestItemWithTypeDto testItemDto, List<String> rowKeysToByAnalyzed, GrrAnalysisConfigDto configDto) {
        if (dataColumn == null || testItemDto == null || configDto == null) {
            throw new ApplicationException(GrrFxmlAndLanguageUtils.getString(GrrExceptionCode.ERR_12001));
        }
        GrrExportDetailDto result = new GrrExportDetailDto();

        GrrAnalysisDataDto grrAnalysisDataDto = new GrrAnalysisDataDto();
        List<String> datas = dataColumn.getData(rowKeysToByAnalyzed);
        Map<String, Object> dataMap = convertData(datas);
        List<Double> doubleList = (List<Double>) dataMap.get(MAP_KEY_DATA);
        Integer count = (Integer) dataMap.get(MAP_KEY_COUNT);
        if (datas == null || doubleList == null || count == datas.size() || datas.size() != doubleList.size()) {
            return null;
        }
        if (datas == null || doubleList == null || datas.size() != doubleList.size()) {
            return null;
        }
        if (testItemDto.getLsl() != null) {
            grrAnalysisDataDto.setLsl(testItemDto.getLsl());
        } else {
            grrAnalysisDataDto.setLsl(dataColumn.getTestItemWithTypeDto().getLsl());
        }
        if (testItemDto.getUsl() != null) {
            grrAnalysisDataDto.setUsl(testItemDto.getUsl());
        } else {
            grrAnalysisDataDto.setUsl(dataColumn.getTestItemWithTypeDto().getUsl());
        }
        grrAnalysisDataDto.setDataList(doubleList);
        GrrExportDetailResultDto resultDto = getAnalysisService().analyzeExportDetailResult(grrAnalysisDataDto, configDto);
        result.setItemName(testItemDto.getTestItemName());
        result.setExportDetailDto(resultDto);
        return result;
    }

    private Map<String, Object> convertData(List<String> datas) {
        List<Double> doubleList = Lists.newArrayList();
        Map<String, Object> data = Maps.newHashMap();
        Integer nanCount = 0;
        for (String s : datas) {
            if (DAPStringUtils.isNumeric(s)) {
                doubleList.add(Double.valueOf(s));
            } else if (s != null && DAPStringUtils.isSpecialBlank(s)) {
                doubleList.add(Double.NaN);
                nanCount++;
            }
        }
        data.put(MAP_KEY_DATA, doubleList);
        data.put(MAP_KEY_COUNT, nanCount);
        return data;
    }

    public GrrAnalysisService getAnalysisService() {
        return analysisService;
    }

    public void setAnalysisService(GrrAnalysisService analysisService) {
        this.analysisService = analysisService;
    }
}
