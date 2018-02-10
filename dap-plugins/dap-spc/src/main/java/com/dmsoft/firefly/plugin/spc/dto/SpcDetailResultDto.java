/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

import java.util.List;

/**
 * Created by Ethan.Yang on 2018/2/8.
 */
public class SpcDetailResultDto extends AbstractValueObject {

    private List<SpcChartResultDto> chartResultDtoList;
    private List<SpcViewDataDto> spcViewDataDtoList;

    public List<SpcChartResultDto> getChartResultDtoList() {
        return chartResultDtoList;
    }

    public void setChartResultDtoList(List<SpcChartResultDto> chartResultDtoList) {
        this.chartResultDtoList = chartResultDtoList;
    }

    public List<SpcViewDataDto> getSpcViewDataDtoList() {
        return spcViewDataDtoList;
    }

    public void setSpcViewDataDtoList(List<SpcViewDataDto> spcViewDataDtoList) {
        this.spcViewDataDtoList = spcViewDataDtoList;
    }
}
