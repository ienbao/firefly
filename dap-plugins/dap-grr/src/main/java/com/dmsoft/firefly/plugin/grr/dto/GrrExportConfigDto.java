/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.grr.dto;

import java.util.List;

/**
 * Created by GuangLi on 2017/7/31.
 */
public class GrrExportConfigDto extends GrrConfigDto {
    private String exportPath;
    private String userName;

    private GrrConfigDto grrConfigDto;

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

    public GrrConfigDto getGrrConfigDto() {
        return grrConfigDto;
    }

    public void setGrrConfigDto(GrrConfigDto grrConfigDto) {
        this.grrConfigDto = grrConfigDto;
    }

    public Boolean isTolerance() {
        if (grrConfigDto != null) {
            return grrConfigDto.getExport().get("Export GRR Result based on process tolerance");
        }
        return true;
    }

    public String getSigma() {
        if (grrConfigDto != null) {
            return grrConfigDto.getSignLevel();
        }
        return "5.15";
    }
    public List<Double> getLevel(){
        if (grrConfigDto != null) {
            return grrConfigDto.getAlarmSetting();
        }
        return null;
    }
}
