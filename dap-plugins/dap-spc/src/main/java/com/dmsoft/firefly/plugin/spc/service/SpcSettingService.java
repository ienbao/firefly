/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.service;

import com.dmsoft.firefly.plugin.spc.dto.SpcSettingDto;

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
}
