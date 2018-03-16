/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.grr.dto;

import java.util.LinkedHashMap;

/**
 * Created by GuangLi on 2017/7/31.
 */
public class GrrExportConfigDto extends GrrConfigDto {
    private String exportPath;
    private String userName;
    private Boolean isTolerance;
    private String sigma;
    private LinkedHashMap<String, SpecificationDataDto> specificationDataDtoMap;

    public String getExportPath() {
        return exportPath;
    }

    public void setExportPath(String exportPath) {
        this.exportPath = exportPath;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Boolean isTolerance() {
        return isTolerance;
    }

    public void setTolerance(Boolean tolerance) {
        isTolerance = tolerance;
    }

    public Boolean getTolerance() {
        return isTolerance;
    }

    public LinkedHashMap<String, SpecificationDataDto> getSpecificationDataDtoMap() {
        return specificationDataDtoMap;
    }

    public void setSpecificationDataDtoMap(LinkedHashMap<String, SpecificationDataDto> specificationDataDtoMap) {
        this.specificationDataDtoMap = specificationDataDtoMap;
    }

    public String getSigma() {
        return sigma;
    }

    public void setSigma(String sigma) {
        this.sigma = sigma;
    }
}
