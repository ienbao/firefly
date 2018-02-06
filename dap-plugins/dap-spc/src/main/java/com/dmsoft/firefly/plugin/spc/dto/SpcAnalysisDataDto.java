/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Created by Ethan.Yang on 2018/2/6.
 */
public class SpcAnalysisDataDto extends AbstractValueObject {
    private SearchConditionDto searchConditionDto;
//    @JsonInclude
//    private List<DataDto> dataDtoList;

    public SearchConditionDto getSearchConditionDto() {
        return searchConditionDto;
    }

    public void setSearchConditionDto(SearchConditionDto searchConditionDto) {
        this.searchConditionDto = searchConditionDto;
    }

//    public List<DataDto> getDataDtoList() {
//        return dataDtoList;
//    }
//
//    public void setDataDtoList(List<DataDto> dataDtoList) {
//        this.dataDtoList = dataDtoList;
//    }
}
