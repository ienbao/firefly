package com.dmsoft.firefly.plugin.grr.service.impl;

import com.dmsoft.firefly.plugin.grr.dto.GrrDetailDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrExportDetailDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrSummaryDto;
import com.dmsoft.firefly.plugin.grr.dto.analysis.*;
import com.dmsoft.firefly.plugin.grr.service.GrrAnalysisService;
import com.dmsoft.firefly.plugin.grr.service.GrrService;
import com.dmsoft.firefly.plugin.grr.utils.GrrExceptionCode;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
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

import java.util.List;

/**
 * impl class for grr service
 */
@OpenService
public class GrrServiceImpl implements GrrService {
    private GrrAnalysisService analysisService;

    @Override
    public List<GrrSummaryDto> getSummaryResult(SearchDataFrame dataFrame, List<TestItemWithTypeDto> testItemDtoList, List<String> rowKeysToByAnalyzed, GrrAnalysisConfigDto configDto) {
        if (dataFrame == null || testItemDtoList == null || configDto == null) {
            throw new ApplicationException(GrrFxmlAndLanguageUtils.getString(GrrExceptionCode.ERR_12001));
        }
        List<GrrSummaryDto> result = Lists.newArrayList();
        List<GrrAnalysisDataDto> grrAnalysisDataDtoList = Lists.newArrayList();
        int n = 0;
        double len = testItemDtoList.size();
        for (TestItemWithTypeDto itemDto : testItemDtoList) {
            GrrAnalysisDataDto grrAnalysisDataDto = new GrrAnalysisDataDto();
            List<String> datas = dataFrame.getDataValue(itemDto.getTestItemName(), rowKeysToByAnalyzed);
            List<Double> doubleList = convertData(datas);
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
            grrAnalysisDataDto.setDataList(doubleList);
            grrAnalysisDataDtoList.add(grrAnalysisDataDto);
            n++;
            pushProgress((int) (n / len * 40));
        }
        for (int i = 0; i < grrAnalysisDataDtoList.size(); i++) {
            GrrSummaryResultDto resultDto = getAnalysisService().analyzeSummaryResult(grrAnalysisDataDtoList.get(i), configDto);
            GrrSummaryDto summaryDto = new GrrSummaryDto();
            summaryDto.setSummaryResultDto(resultDto);
            summaryDto.setItemName(testItemDtoList.get(i).getTestItemName());
            result.add(summaryDto);
            pushProgress((int) (40 + ((i + 1) / (double) (grrAnalysisDataDtoList.size())) * 60));
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
        List<Double> doubleList = convertData(datas);
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
        pushProgress(40);
        GrrDetailResultDto resultDto = getAnalysisService().analyzeDetailResult(grrAnalysisDataDto, configDto);
        result.setItemName(testItemDto.getTestItemName());
        result.setGrrDetailResultDto(resultDto);
        pushProgress(100);
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
        List<Double> doubleList = convertData(datas);
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
        pushProgress(40);
        GrrExportDetailResultDto resultDto = getAnalysisService().analyzeExportDetailResult(grrAnalysisDataDto, configDto);
        result.setItemName(testItemDto.getTestItemName());
        result.setExportDetailDto(resultDto);
        pushProgress(100);
        return result;
    }

    private List<Double> convertData(List<String> datas) {
        List<Double> doubleList = Lists.newArrayList();
        for (String s : datas) {
            if (DAPStringUtils.isNumeric(s)) {
                doubleList.add(Double.valueOf(s));
            } else if (s != null && DAPStringUtils.isSpecialBlank(s)) {
                doubleList.add(Double.NaN);
            }
        }
        return doubleList;
    }

    private void pushProgress(int progress) {
        JobContext context = RuntimeContext.getBean(JobManager.class).findJobContext(Thread.currentThread());
        context.pushEvent(new JobEvent("GrrService", progress + 0.0, null));
    }

    public GrrAnalysisService getAnalysisService() {
        return analysisService;
    }

    public void setAnalysisService(GrrAnalysisService analysisService) {
        this.analysisService = analysisService;
    }
}
