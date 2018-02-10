/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.service.impl;

import com.dmsoft.firefly.plugin.spc.dto.*;

import java.util.List;
import java.util.Map;

/**
 * Created by Ethan.Yang on 2018/2/5.
 */
public interface SpcService {

     /**
     *To get spc statistical result.
     * @param testItemNames
     * @param conditions
     * @param spcSearchConfigDto
     * @return list of spc statistical result
     */
    List<SpcStatisticalResultDto> findStatisticalResult(List<String> testItemNames,List<String> conditions,SpcSearchConfigDto spcSearchConfigDto);

    /**
     * To find chart and view data.
     * @param searchConditionDtoList
     * @param spcSearchConfigDto search data
     * @return the detail result
     */
    SpcDetailResultDto findChartDataAndViewData(List<SearchConditionDto> searchConditionDtoList,SpcSearchConfigDto spcSearchConfigDto);

    /**
     * To refresh spc statistical result.
     * @param searchConditionDtoList
     * @param spcSearchConfigDto
     * @return list of spc statistical result
     */
    List<SpcStatisticalResultDto> refreshStatisticalResult(List<SearchConditionDto> searchConditionDtoList,SpcSearchConfigDto spcSearchConfigDto);

    /**
     * To refresh all analysis result.
     * @param searchConditionDtoList
     * @param spcSearchConfigDto
     * @param includeLineNo the lineNo of file
     * @return
     */
    SpcAnalysisResultDto refreshAllAnalysisResult(List<SearchConditionDto> searchConditionDtoList,SpcSearchConfigDto spcSearchConfigDto,Map<String,List<Long>> includeLineNo);

    /**
     * To find view data result.
     * @param searchConditionDtoList
     * @param spcSearchConfigDto
     * @param testItems
     * @return the spc view data
     */
    SpcViewDataDto updateViewData(List<SearchConditionDto> searchConditionDtoList, SpcSearchConfigDto spcSearchConfigDto, List<String> testItems);





}
