/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.service;

import com.dmsoft.firefly.plugin.spc.dto.*;
import com.dmsoft.firefly.plugin.spc.service.impl.SpcService;

import java.util.List;

/**
 * Created by Ethan.Yang on 2018/2/5.
 */
public class SpcServiceImpl implements SpcService{

    @Override
    public List<SpcStatisticalResultDto> findStatisticalResult(List<SearchConditionDto> searchConditionDtoList, SpcSearchConfigDto spcSearchConfigDto) {
        return null;
    }

    @Override
    public List<SpcStatisticalResultDto> refreshStatisticalResult(List<SpcAnalysisDataDto> spcAnalysisDataDto, SpcSearchConfigDto spcSearchConfigDto) {
        return null;
    }

    @Override
    public List<SpcChartResultDto> findChartResult(List<SpcAnalysisDataDto> spcAnalysisDataDto, SpcSearchConfigDto spcSearchConfigDto) {
        return null;
    }

    @Override
    public SpcViewDataDto findViewData(List<SearchConditionDto> searchConditionDtoList, SpcSearchConfigDto spcSearchConfigDto) {
        return null;
    }

}
