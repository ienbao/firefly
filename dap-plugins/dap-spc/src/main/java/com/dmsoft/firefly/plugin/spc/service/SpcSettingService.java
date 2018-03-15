/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.service;

import com.dmsoft.firefly.plugin.spc.dto.SpcSettingDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcStatisticalResultAlarmDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcStatsDto;

import java.util.List;
import java.util.Map;

/**
 * Created by Ethan.Yang on 2018/3/8.
 */
public interface SpcSettingService {
    /**
     * save spc config setting
     *
     * @param spcSettingDto spc setting
     */
    void saveSpcSetting(SpcSettingDto spcSettingDto);

    /**
     * find grr config setting
     *
     * @return spc setting
     */
    SpcSettingDto findSpcSetting();

    /**
     * set statistical result alarm
     *
     * @param spcStatsDtoList statistical result
     * @return the data of statistical result after setting
     */
    List<SpcStatisticalResultAlarmDto> setStatisticalResultAlarm(List<SpcStatsDto> spcStatsDtoList);

    /**
     * save spc export template setting.
     * @param exportSetting export Setting data
     */
    void saveSpcExportTemplateSetting(Map<String, Boolean> exportSetting);

    /**
     * find spc export template setting.
     * @return
     */
    Map<String, Boolean>  findSpcExportTemplateSetting();


}
