/*
 * Copyright (c) 2016. For Intelligent Group.
 */
package com.dmsoft.firefly.sdk.dai.dto;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Guang.Li on 2018/1/29.
 */
public class TemplateSettingDto implements Serializable {
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
        if (timePatternDto == null) {
            return new TimePatternDto();
        }
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

    /**
     * method to get time keys
     *
     * @return list of time key
     */
    public List<String> getTimeKeys() {
        if (getTimePatternDto() == null || getTimePatternDto().getTimeKeys() == null) {
            return Lists.newArrayList();
        }
        return getTimePatternDto().getTimeKeys();
    }

    /**
     * method to get time pattern
     *
     * @return time pattern
     */
    public String getTimePattern() {
        if (getTimePatternDto() == null) {
            return null;
        }
        return getTimePatternDto().getPattern();
    }
}
