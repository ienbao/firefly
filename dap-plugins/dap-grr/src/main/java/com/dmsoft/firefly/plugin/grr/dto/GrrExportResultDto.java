/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.grr.dto;

/**
 * Created by Administrator on 2017/7/27.
 */
public class GrrExportResultDto {
    private String itemName;
    private GrrAnovaAndSourceResultDto grrAnovaAndSourceResultDto;
    private GrrImageDto grrImageDto;

    public GrrAnovaAndSourceResultDto getGrrAnovaAndSourceResultDto() {
        return grrAnovaAndSourceResultDto;
    }

    public void setGrrAnovaAndSourceResultDto(GrrAnovaAndSourceResultDto grrAnovaAndSourceResultDto) {
        this.grrAnovaAndSourceResultDto = grrAnovaAndSourceResultDto;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public GrrImageDto getGrrImageDto() {
        return grrImageDto;
    }

    public void setGrrImageDto(GrrImageDto grrImageDto) {
        this.grrImageDto = grrImageDto;
    }
}
