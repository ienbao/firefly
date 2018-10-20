package com.dmsoft.firefly.plugin.grr.service.impl;

import com.dmsoft.firefly.plugin.grr.dto.GrrDetailDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrExportDetailDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrSummaryDto;
import com.dmsoft.firefly.plugin.grr.service.GrrAnalysisService;
import com.dmsoft.firefly.plugin.grr.service.GrrService;
import com.dmsoft.firefly.plugin.grr.utils.GrrExceptionCode;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.dmsoft.firefly.plugin.grr.dto.analysis.*;
import com.dmsoft.firefly.plugin.yiela.grr.dto.analysis.*;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dataframe.DataColumn;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.job.core.JobContext;
import com.dmsoft.firefly.sdk.job.core.JobEvent;
import com.dmsoft.firefly.sdk.job.core.JobManager;
import com.dmsoft.firefly.sdk.plugin.apis.annotation.OpenService;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * impl class for grr service
 */
@OpenService
public class GrrServiceImpl implements GrrService {
    private static final String MAP_KEY_DATA = "data";
    private static final String MAP_KEY_COUNT = "count";
    private static Logger logger = LoggerFactory.getLogger(GrrServiceImpl.class);
    private GrrAnalysisService analysisService;

    @Override
    @SuppressWarnings("unchecked")
    public List<GrrSummaryDto> getSummaryResult(SearchDataFrame dataFrame, List<TestItemWithTypeDto> testItemDtoList, List<String> rowKeysToByAnalyzed, GrrAnalysisConfigDto configDto) {
        logger.debug("Getting GRR summary result...");
        if (dataFrame == null || testItemDtoList == null || configDto == null) {
            pushProgress(100);
            throw new ApplicationException(GrrFxmlAndLanguageUtils.getString(GrrExceptionCode.ERR_12001));
        }
        List<GrrSummaryDto> result = Lists.newArrayList();
        List<GrrAnalysisDataDto> grrAnalysisDataDtoList = Lists.newArrayList();
        int n = 0;
        double len = testItemDtoList.size();
        for (TestItemWithTypeDto itemDto : testItemDtoList) {
            GrrAnalysisDataDto grrAnalysisDataDto = new GrrAnalysisDataDto();
            List<String> datas = dataFrame.getDataValue(itemDto.getTestItemName(), rowKeysToByAnalyzed);
            Map<String, Object> dataMap = convertData(datas);
            List<Double> doubleList = (List<Double>) dataMap.get(MAP_KEY_DATA);
            Integer count = (Integer) dataMap.get(MAP_KEY_COUNT);
            grrAnalysisDataDto.setLsl(itemDto.getLsl());
            grrAnalysisDataDto.setUsl(itemDto.getUsl());
            if (datas == null || doubleList == null || count == datas.size() || datas.size() != doubleList.size()) {
                grrAnalysisDataDto.setDataList(null);
            } else {
                grrAnalysisDataDto.setDataList(doubleList);
            }
            grrAnalysisDataDtoList.add(grrAnalysisDataDto);
            n++;
            pushProgress((int) (n / len * 40));
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
            pushProgress((int) (40 + ((i + 1) / (double) (grrAnalysisDataDtoList.size())) * 60));
        }
        logger.info("Get GRR summary result done.");
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GrrDetailDto getDetailResult(DataColumn dataColumn, TestItemWithTypeDto testItemDto, List<String> rowKeysToByAnalyzed, GrrAnalysisConfigDto configDto) {
        logger.debug("Getting GRR detail result...");
        if (dataColumn == null || testItemDto == null || configDto == null) {
            pushProgress(100);
            throw new ApplicationException(GrrFxmlAndLanguageUtils.getString(GrrExceptionCode.ERR_12001));
        }
        GrrDetailDto result = new GrrDetailDto();

        GrrAnalysisDataDto grrAnalysisDataDto = new GrrAnalysisDataDto();
        List<String> datas = dataColumn.getData(rowKeysToByAnalyzed);
        Map<String, Object> dataMap = convertData(datas);
        List<Double> doubleList = (List<Double>) dataMap.get(MAP_KEY_DATA);
        Integer count = (Integer) dataMap.get(MAP_KEY_COUNT);
        pushProgress(20);
        if (datas == null || doubleList == null || count == datas.size() || datas.size() != doubleList.size()) {
            pushProgress(80);
            return null;
        }
        grrAnalysisDataDto.setLsl(testItemDto.getLsl());
        grrAnalysisDataDto.setUsl(testItemDto.getUsl());
        grrAnalysisDataDto.setDataList(doubleList);
        pushProgress(40);
        GrrDetailResultDto resultDto = getAnalysisService().analyzeDetailResult(grrAnalysisDataDto, configDto);
        result.setItemName(testItemDto.getTestItemName());
        result.setGrrDetailResultDto(resultDto);
        logger.info("Get GRR detail result done.");
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
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
        pushProgress(20);
        if (datas == null || doubleList == null || count == datas.size() || datas.size() != doubleList.size()) {
            pushProgress(80);
            GrrExportDetailDto emptyResult = new GrrExportDetailDto();
            emptyResult.setItemName(testItemDto.getTestItemName());
            GrrExportDetailResultDto emptyDetailResult = new GrrExportDetailResultDto();
            if (DAPStringUtils.isNumeric(testItemDto.getLsl())) {
                emptyDetailResult.setLsl(Double.valueOf(testItemDto.getLsl()));
            }
            if (DAPStringUtils.isNumeric(testItemDto.getUsl())) {
                emptyDetailResult.setUsl(Double.valueOf(testItemDto.getUsl()));
            }
            emptyResult.setExportDetailDto(emptyDetailResult);
            return emptyResult;
        }
        grrAnalysisDataDto.setLsl(testItemDto.getLsl());
        grrAnalysisDataDto.setUsl(testItemDto.getUsl());
        grrAnalysisDataDto.setDataList(doubleList);
        pushProgress(40);
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
            } else if (s != null && DAPStringUtils.isBlankWithSpecialNumber(s)) {
                doubleList.add(Double.NaN);
                nanCount++;
            }
        }
        data.put(MAP_KEY_DATA, doubleList);
        data.put(MAP_KEY_COUNT, nanCount);
        return data;
    }

    private void pushProgress(int progress) {
        JobContext context = RuntimeContext.getBean(JobManager.class).findJobContext(Thread.currentThread());
        if (context != null) {
            context.pushEvent(new JobEvent("GrrService", progress + 0.0, null));
        }
    }

    public GrrAnalysisService getAnalysisService() {
        return analysisService;
    }

    public void setAnalysisService(GrrAnalysisService analysisService) {
        this.analysisService = analysisService;
    }
}
