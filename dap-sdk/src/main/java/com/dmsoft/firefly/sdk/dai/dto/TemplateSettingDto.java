/*
 * Copyright (c) 2016. For Intelligent Group.
 */
package com.dmsoft.firefly.sdk.dai.dto;

import java.util.LinkedHashMap;

/**
 * Created by Guang.Li on 2018/1/29.
 */
public class TemplateSettingDto {
    private Long id ;
    private String name;
    private int decimalDigit;
    private TimePatternDto timePatternDto;
//    private LinkedHashMap<String, LinkedHashMap<String, AlarmDataDto>> alarmDatas;
    private LinkedHashMap<String, SpecificationDataDto> specificationDatas;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
