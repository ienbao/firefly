/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.service.impl;

import com.dmsoft.firefly.plugin.spc.dto.*;

import java.util.List;

/**
 * Created by Ethan.Yang on 2018/2/5.
 */
public interface SpcService {
    /**
     * To get spc statistical result.
     *
     * @param searchConditionDtoList list search condition
     * @param spcSearchConfigDto  search config
     * @return list of spc statistical result
     */
    List<SpcStatisticalResultDto> findStatisticalResult(List<SearchConditionDto> searchConditionDtoList, SpcSearchConfigDto spcSearchConfigDto);

    /**
     * To refresh spc statistical result.
     * @param spcAnalysisDataDto
     * @param spcSearchConfigDto
     * @return list of spc statistical result
     */
    List<SpcStatisticalResultDto> refreshStatisticalResult(List<SpcAnalysisDataDto> spcAnalysisDataDto, SpcSearchConfigDto spcSearchConfigDto);


    /**
     * To find spc chart result.
     * @param spcAnalysisDataDto
     * @param spcSearchConfigDto
     * @return list of the chart result
     */
    List<SpcChartResultDto> findChartResult(List<SpcAnalysisDataDto> spcAnalysisDataDto, SpcSearchConfigDto spcSearchConfigDto);

    /**
     * To find view data result.
     * @param searchConditionDtoList
     * @param spcSearchConfigDto
     * @return the spc view data
     */
    SpcViewDataDto findViewData(List<SearchConditionDto> searchConditionDtoList, SpcSearchConfigDto spcSearchConfigDto);



}
