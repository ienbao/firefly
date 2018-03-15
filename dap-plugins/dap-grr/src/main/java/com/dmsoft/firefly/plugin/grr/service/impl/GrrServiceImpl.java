package com.dmsoft.firefly.plugin.grr.service.impl;

import com.dmsoft.firefly.plugin.grr.dto.GrrDetailDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrSummaryDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrTestItemDto;
import com.dmsoft.firefly.plugin.grr.dto.analysis.GrrAnalysisConfigDto;
import com.dmsoft.firefly.plugin.grr.dto.analysis.GrrAnalysisDataDto;
import com.dmsoft.firefly.plugin.grr.dto.analysis.GrrDetailResultDto;
import com.dmsoft.firefly.plugin.grr.dto.analysis.GrrSummaryResultDto;
import com.dmsoft.firefly.plugin.grr.service.GrrAnalysisService;
import com.dmsoft.firefly.plugin.grr.service.GrrService;
import com.dmsoft.firefly.plugin.grr.utils.GrrExceptionCode;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * impl class for grr service
 */
public class GrrServiceImpl implements GrrService {
    private GrrAnalysisService analysisService;

    @Override
    public List<GrrSummaryDto> getSummaryResult(SearchDataFrame dataFrame, List<GrrTestItemDto> testItemDtoList, GrrAnalysisConfigDto configDto) {
        if (dataFrame == null || testItemDtoList == null || configDto == null) {
            throw new ApplicationException(GrrFxmlAndLanguageUtils.getString(GrrExceptionCode.ERR_12001));
        }
        List<GrrSummaryDto> result = Lists.newArrayList();
        List<GrrAnalysisDataDto> grrAnalysisDataDtoList = Lists.newArrayList();
        for (GrrTestItemDto itemDto : testItemDtoList) {
            GrrAnalysisDataDto grrAnalysisDataDto = new GrrAnalysisDataDto();
            List<String> datas = dataFrame.getDataValue(itemDto.getItemName());
            List<Double> doubleList = Lists.newArrayList();
            for (String s : datas) {
                if (DAPStringUtils.isNumeric(s)) {
                    doubleList.add(Double.valueOf(s));
                }
            }
            if (itemDto.getCusLsl() != null) {
                grrAnalysisDataDto.setLsl(itemDto.getCusLsl());
            } else {
                grrAnalysisDataDto.setLsl(dataFrame.getTestItemWithTypeDto(itemDto.getItemName()).getLsl());
            }
            if (itemDto.getCusUsl() != null) {
                grrAnalysisDataDto.setUsl(itemDto.getCusUsl());
            } else {
                grrAnalysisDataDto.setUsl(dataFrame.getTestItemWithTypeDto(itemDto.getItemName()).getUsl());
            }
            grrAnalysisDataDto.setDataList(doubleList);
            grrAnalysisDataDtoList.add(grrAnalysisDataDto);
        }
        for (int i = 0; i < grrAnalysisDataDtoList.size(); i++) {
            GrrSummaryResultDto resultDto = getAnalysisService().analyzeSummaryResult(grrAnalysisDataDtoList.get(i), configDto);
            GrrSummaryDto summaryDto = new GrrSummaryDto();
            summaryDto.setSummaryResultDto(resultDto);
            summaryDto.setItemName(testItemDtoList.get(i).getItemName());
            result.add(summaryDto);
        }
        return result;
    }

    @Override
    public GrrDetailDto getDetailResult(SearchDataFrame dataFrame, GrrTestItemDto testItemDto, GrrAnalysisConfigDto configDto) {
        if (dataFrame == null || testItemDto == null || configDto == null) {
            throw new ApplicationException(GrrFxmlAndLanguageUtils.getString(GrrExceptionCode.ERR_12001));
        }
        GrrDetailDto result = new GrrDetailDto();

        GrrAnalysisDataDto grrAnalysisDataDto = new GrrAnalysisDataDto();
        List<String> datas = dataFrame.getDataValue(testItemDto.getItemName());
        List<Double> doubleList = Lists.newArrayList();
        for (String s : datas) {
            if (DAPStringUtils.isNumeric(s)) {
                doubleList.add(Double.valueOf(s));
            }
        }
        if (testItemDto.getCusLsl() != null) {
            grrAnalysisDataDto.setLsl(testItemDto.getCusLsl());
        } else {
            grrAnalysisDataDto.setLsl(dataFrame.getTestItemWithTypeDto(testItemDto.getItemName()).getLsl());
        }
        if (testItemDto.getCusUsl() != null) {
            grrAnalysisDataDto.setUsl(testItemDto.getCusUsl());
        } else {
            grrAnalysisDataDto.setUsl(dataFrame.getTestItemWithTypeDto(testItemDto.getItemName()).getUsl());
        }
        GrrDetailResultDto resultDto = getAnalysisService().analyzeDetailResult(grrAnalysisDataDto, configDto);
        grrAnalysisDataDto.setDataList(doubleList);
        result.setItemName(testItemDto.getItemName());
        result.setGrrDetailResultDto(resultDto);
        return result;
    }

    public GrrAnalysisService getAnalysisService() {
        return analysisService;
    }

    public void setAnalysisService(GrrAnalysisService analysisService) {
        this.analysisService = analysisService;
    }
}
