package com.dmsoft.firefly.plugin.yield.service;

import com.dmsoft.firefly.plugin.yield.dto.*;

import java.util.List;

public interface YieldSettingService {
//    /**
//     * set statistical result alarm
//     *
//     * @param yieldStatsDtoList statistical result
//     * @param yieldSettingDto spcSettingDto
//     * @return the data of statistical result after setting
//     */

    List<YieldOverviewResultAlarmDto> setStatisticalResultAlarm(List<YieldOverviewDto> overViewDtoList, YieldSettingDto yieldSettingDto);
    /**
     * find grr config setting
     *
     * @return spc setting
     */
    YieldSettingDto findYieldSetting();
    /**
     * save yiels config setting
     *
     * @param yieldSettingDto spc setting
     */
    void saveYieldSetting(YieldSettingDto yieldSettingDto);

    YieldChartResultAlermDto setChartResultAlarm(YieldTotalProcessesDto totalProcessesDtos, YieldSettingDto yieldSettingDto);

}

