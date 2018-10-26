package com.dmsoft.firefly.plugin.yield.service;

import com.dmsoft.firefly.plugin.yield.dto.*;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;

import java.util.List;

/**
 * interface class for yield service
 *
 * @author Tod Dylan
 */
public interface YieldService {

    YieldResultDto getYieldResult(SearchDataFrame searchDataFrame, List<SearchConditionDto> searchConditions,
                                   YieldAnalysisConfigDto configDto);

    YieldViewDataResultDto getViewData(SearchDataFrame searchDataFrame, List<SearchConditionDto> searchConditions, YieldAnalysisConfigDto configDto);

    List<YieldViewDataResultDto> getTotalData(SearchDataFrame searchDataFrame, List<SearchConditionDto> searchConditions, YieldAnalysisConfigDto configDto);

}
