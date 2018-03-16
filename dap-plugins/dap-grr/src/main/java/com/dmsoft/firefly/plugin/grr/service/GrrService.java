package com.dmsoft.firefly.plugin.grr.service;

import com.dmsoft.firefly.plugin.grr.dto.GrrDetailDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrSummaryDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrTestItemDto;
import com.dmsoft.firefly.plugin.grr.dto.analysis.GrrAnalysisConfigDto;
import com.dmsoft.firefly.sdk.dataframe.DataColumn;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;

import java.util.List;

/**
 * interface for grr service
 *
 * @author Can Guan
 */
public interface GrrService {
    /**
     * method to get grr summary result
     *
     * @param dataFrame       data frame
     * @param testItemDtoList test item dto list, the test item to be analyzed. ps: when the usl and lsl doesn't exist in test item dto, the api will search in data frame
     * @param configDto       config dto
     * @return list of grr summary dto
     */
    List<GrrSummaryDto> getSummaryResult(SearchDataFrame dataFrame, List<GrrTestItemDto> testItemDtoList, GrrAnalysisConfigDto configDto);

    /**
     * method to get detail result
     *
     * @param dataColumn   data frame
     * @param testItemDto test item dto, ps: when the usl and lsl doesn't exist in test item dto, the api will search in data frame
     * @param configDto   config dto
     * @return grr detail dto
     */
    GrrDetailDto getDetailResult(DataColumn dataColumn, GrrTestItemDto testItemDto, GrrAnalysisConfigDto configDto);
}
