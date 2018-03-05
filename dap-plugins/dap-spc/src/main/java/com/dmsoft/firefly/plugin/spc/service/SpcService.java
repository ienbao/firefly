package com.dmsoft.firefly.plugin.spc.service;

import com.dmsoft.firefly.plugin.spc.dto.SearchConditionDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcAnalysisConfigDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcChartDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcStatsDto;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;

import java.util.List;

/**
 * interface class for spc service
 *
 * @author Can Guan, Ethan Yang
 */
public interface SpcService {
    /**
     * method to get spc statistical result from data frame
     *
     * @param searchDataFrame  data frame
     * @param searchConditions list of search conditions. ps: when the usl and lsl doesn't exist in search condition dto, the api will search in data frame
     * @param configDto        spc analysis config dto
     * @return list of spc statistical result dto
     */
    List<SpcStatsDto> getStatisticalResult(SearchDataFrame searchDataFrame, List<SearchConditionDto> searchConditions,
                                           SpcAnalysisConfigDto configDto);

    /**
     * method to get chart result from data frame
     *
     * @param searchDataFrame  data frame
     * @param searchConditions list of search conditions. ps: when the usl and lsl doesn't exist in search condition dto, the api will search in data frame
     * @param configDto        spc analysis config dto
     * @return list of spc chart result dto
     */
    List<SpcChartDto> getChartResult(SearchDataFrame searchDataFrame, List<SearchConditionDto> searchConditions,
                                     SpcAnalysisConfigDto configDto);
}
