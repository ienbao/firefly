/*
 * Copyright (c) 2016. For Intelligent Group.
 */
package com.dmsoft.firefly.sdk.dai.dto;

import java.util.LinkedHashMap;

/**
 * Created by Guang.Li on 2018/1/29.
 */
public class TemplateSettingDto {
    private String name;
    private int decimalDigit = 6;
    private TimePatternDto timePatternDto;
    private LinkedHashMap<String, SpecificationDataDto> specificationDatas;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDecimalDigit() {
        return decimalDigit;
    }

    public void setDecimalDigit(int decimalDigit) {
        this.decimalDigit = decimalDigit;
    }

    public TimePatternDto getTimePatternDto() {
        return timePatternDto;
    }

    public void setTimePatternDto(TimePatternDto timePatternDto) {
        this.timePatternDto = timePatternDto;
    }

    public LinkedHashMap<String, SpecificationDataDto> getSpecificationDatas() {
        return specificationDatas;
    }

    public void setSpecificationDatas(LinkedHashMap<String, SpecificationDataDto> specificationDatas) {
        this.specificationDatas = specificationDatas;
    }
}
