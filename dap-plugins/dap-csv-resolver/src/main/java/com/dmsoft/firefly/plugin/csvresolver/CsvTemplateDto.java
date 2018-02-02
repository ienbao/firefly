/*
 * Copyright (c) 2017. For intelligent group.
 */

package com.dmsoft.firefly.plugin.csvresolver;

/**
 * Created by Eligi.Ran on 2017/3/1.
 */
public class CsvTemplateDto {
    private String filePath = null;
    private Integer header = null;
    private Integer item = null;
    private Integer usl = null;
    private Integer lsl = null;
    private Integer unit = null;
    private Integer data = null;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Integer getHeader() {
        return header;
    }

    public void setHeader(Integer header) {
        this.header = header;
    }

    public Integer getItem() {
        return item;
    }

    public void setItem(Integer item) {
        this.item = item;
    }

    public Integer getUsl() {
        return usl;
    }

    public void setUsl(Integer usl) {
        this.usl = usl;
    }

    public Integer getLsl() {
        return lsl;
    }

    public void setLsl(Integer lsl) {
        this.lsl = lsl;
    }

    public Integer getData() {
        return data;
    }

    public void setData(Integer data) {
        this.data = data;
    }

    public Integer getUnit() {
        return unit;
    }

    public void setUnit(Integer unit) {
        this.unit = unit;
    }

    public boolean equalTo(CsvTemplateDto toCompare){
        if(!header.equals(toCompare.header))
            return false;
        if(!item.equals(toCompare.item))
            return false;
        if(!usl.equals(toCompare.usl))
            return false;
        if(!lsl.equals(toCompare.lsl))
            return false;
        if(!unit.equals(toCompare.unit))
            return false;
        if(!data.equals(toCompare.data))
            return false;

        return true;
    }
}
