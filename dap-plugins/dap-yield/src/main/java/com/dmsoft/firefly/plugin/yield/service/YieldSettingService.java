package com.dmsoft.firefly.plugin.yield.service;

import com.dmsoft.firefly.plugin.yield.dto.YieldOverviewDto;
import com.dmsoft.firefly.plugin.yield.dto.YieldOverviewResultAlarmDto;
import com.dmsoft.firefly.plugin.yield.dto.YieldSettingDto;

import java.util.List;

public interface YieldSettingService {
    /**
     * set statistical result alarm
     *
     * @param spcStatsDtoList statistical result
     * @param spcSettingDto spcSettingDto
     * @return the data of statistical result after setting
     */
    List<YieldOverviewResultAlarmDto> setStatisticalResultAlarm(List<YieldOverviewDto> overViewDtoList, YieldSettingDto yieldSettingDto);
}
