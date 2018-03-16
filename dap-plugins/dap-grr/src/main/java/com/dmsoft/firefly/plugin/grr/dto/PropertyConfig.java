/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.grr.dto;


/**
 * Created by yangli on 2015-07-24.
 */
public class PropertyConfig {

    private Integer spcExportNumber;

    private Integer grrExportNumber;

    private String defaultExportPath;

    public Integer getSpcExportNumber() {
        return spcExportNumber;
    }

    public void setSpcExportNumber(Integer spcExportNumber) {
        this.spcExportNumber = spcExportNumber;
    }

    public Integer getGrrExportNumber() {
        return grrExportNumber;
    }

    public void setGrrExportNumber(Integer grrExportNumber) {
        this.grrExportNumber = grrExportNumber;
    }

    public String getDefaultExportPath() {
        return defaultExportPath;
    }

    public void setDefaultExportPath(String defaultExportPath) {
        this.defaultExportPath = defaultExportPath;
    }
}
