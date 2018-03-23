/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.dto.chart;

import com.dmsoft.firefly.plugin.spc.dto.SpcSettingDto;

import java.util.Map;

/**
 * Created by Ethan.Yang on 2018/3/23.
 */
public class SpcSettingJsonDto {
    private SpcSettingDto spcSettingDto;
    private Map<String, Boolean> exportSetting;

    public SpcSettingDto getSpcSettingDto() {
        return spcSettingDto;
    }

    public void setSpcSettingDto(SpcSettingDto spcSettingDto) {
        this.spcSettingDto = spcSettingDto;
    }

    public Map<String, Boolean> getExportSetting() {
        return exportSetting;
    }

    public void setExportSetting(Map<String, Boolean> exportSetting) {
        this.exportSetting = exportSetting;
    }
}
